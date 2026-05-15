package com.example.karunada_kala.data

import com.example.karunada_kala.BuildConfig
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Calls the Gemini API (free tier — gemini-1.5-flash) to auto-generate
 * Karnataka art form descriptions when Firestore doesn't have one.
 *
 * Securely fetches GEMINI_API_KEY from local.properties via BuildConfig.
 * (No billing account required for the free tier — 1,500 requests/day.)
 */
object GeminiRepository {

    private val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY

    private const val MODEL = "gemini-flash-latest"
    private val ENDPOINT =
        "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$GEMINI_API_KEY"

    /**
     * Returns a 100–200 word description of the given Karnataka art form.
     * Returns null on any network or parsing error so callers can fall back gracefully.
     */
    suspend fun generateArtFormDescription(artFormName: String, region: String): String? =
        withContext(Dispatchers.IO) {
            try {
                if (GEMINI_API_KEY.isBlank()) {
                    Log.e("GeminiAI", "API Key is missing!")
                    return@withContext null
                }

                Log.d("GeminiAI", "Generating description for $artFormName using $MODEL...")
                
                val prompt = """
                    Write a vivid, engaging description of the traditional Karnataka art form called 
                    "$artFormName" from the $region region of India. 
                    The description should be 100 to 200 words. 
                    Cover its origin, what makes it unique, and why it matters culturally.
                    Use simple English suitable for a general audience.
                    Do NOT include any markdown formatting.
                """.trimIndent()

                val requestBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("maxOutputTokens", 500)
                        put("temperature", 0.7)
                    })
                }.toString()

                val connection = (URL(ENDPOINT).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json")
                    doOutput = true
                    connectTimeout = 20_000
                    readTimeout = 20_000
                }

                connection.outputStream.use { it.write(requestBody.toByteArray()) }

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    val errorMsg = connection.errorStream?.bufferedReader()?.readText()
                    Log.e("GeminiAI", "API Error: $responseCode - $errorMsg")
                    return@withContext null
                }

                val responseText = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)

                // Navigate: candidates[0].content.parts[0].text
                val result = json
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
                    .trim()

                Log.d("GeminiAI", "Successfully generated description.")
                result

            } catch (e: Exception) {
                Log.e("GeminiAI", "Exception: ${e.message}")
                null // Caller will use pre-stored Firestore description as fallback
            }
        }
}