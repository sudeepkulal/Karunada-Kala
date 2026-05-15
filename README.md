# Karunada Kala 🎭 (ಕರ್ನಾಟಕದ ಕಲಾ ಸಂಪ್ರದಾಯ)

**Karunada Kala** is a modern, community-driven Android application dedicated to the preservation, discovery, and celebration of Karnataka's rich cultural heritage. Built with Jetpack Compose and powered by Google Gemini AI, the app bridges the gap between traditional artisans and modern enthusiasts.

## 📲 Download & Install

You can download the latest stable version of the app directly from this repository:

**[Download Karunada Kala APK](./Karunada-Kala.apk)**

*Note: To install the APK, you may need to enable "Install from Unknown Sources" in your Android security settings.*

---

## ✨ Features Overview

### 1. 🛡️ Role-Based Access Control
The app provides a tailored experience based on three distinct user roles:
- **Explorer:** Casual users who want to discover art forms, find artisans on the map, and sign up for workshops.
- **Artisan:** Cultural practitioners who can manage their professional profile and view student registrations.
- **Admin:** Power users who moderate the platform, manage the art catalog, and oversee user roles.

### 2. 🎨 AI-Powered Art Form Catalog
- **Heritage Grid:** A beautiful, searchable catalog of Karnataka's traditional art forms like Yakshagana, Dollu Kunitha, Ilkal weaves, and more.
- **Gemini AI Integration:** If a description is missing, the app uses **Google Gemini AI** to auto-generate a vivid, 200-word educational history of the art form.
- **Interactive Details:** Each art form includes a summary, a detailed heritage background, and an integrated **YouTube player** to watch live performances.
- **Social Sharing:** Easily share your favorite art forms with friends and family via a single tap.

### 3. 📍 Interactive Artisan Map
- **Live Map:** Using OpenStreetMap (OSMDroid), users can locate artisans across the state in real-time.
- **Smart Filters:** Filter map pins between **Workshops** (learning centers) and **Performances** (viewing venues).
- **Location Picker:** Artisans can register their exact location using an interactive "Pick on Map" tool with a **GPS "Locate Me"** button for precision.

### 4. 📝 Workshop Sign-Up & Management
- **Seamless Registration:** Explorers can sign up for specific workshops directly through an artisan's profile.
- **Artisan Dashboard:** Artisans have a dedicated "Workshop Registrations" view where they can see student details and contact them via **WhatsApp or Phone** with one click.
- **My Activity:** Users can track their own participation history in the "My Activity" section of their profile.

### 5. 📅 Cultural Event Feed
- **Live Updates:** Stay informed about upcoming cultural festivals, exhibitions, and performances across various districts of Karnataka.
- **Easy Posting:** Artisans and Admins can post new events with images, dates, and locations to keep the community engaged.

### 6. ⚙️ Personalized Experience
- **Persistent Dark Mode:** Toggle between Light and Dark themes. The app remembers your preference using **Jetpack DataStore**.
- **Profile Management:** Update your name and phone number anytime to keep your registration details current.
- **Privacy & Security:** Dedicated screens explaining data encryption and location privacy.

### 7. 🔑 Admin Command Center
- **User Management:** View all registered users and promote/demote roles via an interactive dialog.
- **Catalog Control:** Add, edit, or delete art forms and artisan profiles to ensure the data remains accurate and high-quality.

---

## 🛠️ Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% Declarative UI)
- **Backend:** [Firebase](https://firebase.google.com/) (Authentication & Firestore NoSQL)
- **Intelligence:** [Google Gemini AI](https://ai.google.dev/) (Generative AI for descriptions)
- **Maps:** [OSMDroid](https://github.com/osmdroid/osmdroid) (Open-source alternative to Google Maps)
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
- **Architecture:** MVVM (Model-View-ViewModel) with StateFlow
- **Data Persistence:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)

---

## 🚀 Getting Started for Developers

### Prerequisites
- Android Studio Ladybug or newer.
- A Firebase Project (with Auth and Firestore enabled).
- A Google AI Studio API Key for Gemini.

### Setup Instructions
1. **Clone the repo:** `git clone https://github.com/YOUR_USERNAME/KarunadaKala.git`
2. **API Keys:** Add `GEMINI_API_KEY=your_key` to your `local.properties` file.
3. **Firebase:** Download your `google-services.json` and place it in the `app/` directory.
4. **Build:** Sync Gradle and run the app on an emulator or physical device (API 24+).

---

## 📜 License
This project is licensed under the **MIT License**. Created with ❤️ for the heritage of Karnataka.
