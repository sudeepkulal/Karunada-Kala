# Karunada Kala 🎭

Karunada Kala is a modern Android application dedicated to preserving and celebrating the rich cultural heritage of Karnataka, India. The app connects users with traditional artisans, offers AI-powered educational content, and keeps the community updated on cultural events.

## Features ✨

- **Art Form Catalog**: Explore a rich grid of Karnataka's traditional art forms (Yakshagana, Ilkal weaves, Pottery, etc.).
- **Gemini AI Integration**: Descriptions for art forms are automatically generated using Google Gemini AI when data is missing.
- **Artisan Map**: Interactive OpenStreetMap to locate artisans and workshops across the state.
- **Workshop Sign-Up**: Seamless registration flow to participate in hands-on cultural workshops.
- **Admin Dashboard**: Comprehensive management tools for users, art forms, and artisan profiles.
- **Personalized Experience**: Dark mode support, profile management, and activity history.

## Tech Stack 🛠️

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Backend**: Firebase (Authentication & Firestore)
- **AI**: Google Gemini API
- **Maps**: OpenStreetMap (OSMDroid)
- **Data Persistence**: Jetpack DataStore

## Getting Started 🚀

1. **Clone the repository**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/KarunadaKala.git
   ```
2. **Add API Keys**:
   - Create a `local.properties` file in the root directory.
   - Add your Gemini API key: `GEMINI_API_KEY=your_key_here`.
3. **Firebase Setup**:
   - Add your `google-services.json` to the `app/` folder.
4. **Build and Run**: Open the project in Android Studio and run it on your device.

## License 📜
This project is licensed under the MIT License.
