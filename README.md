# 🌸 Afterma — Premium Maternal Wellness Mobile App

> **Afterma** is a premium, empathetic maternal healing and emotional wellness platform designed to guide new mothers through postpartum recovery, lactation tracking, pediatric support, therapy journeys, and safe motherhood.

---

## 📖 Table of Contents

- [✨ Core Features](#-core-features)
- [🛠️ Tech Stack & Architecture](#%EF%B8%8F-tech-stack--architecture)
- [📋 Prerequisites](#-prerequisites)
- [🚀 Getting Started](#-getting-started)
- [🔐 Environment & Secrets Setup](#-environment--secrets-setup)
- [📦 Building the App](#-building-the-app)
- [🧪 Testing Strategy](#-testing-strategy)
- [🗂️ Project Structure](#%EF%B8%8F-project-structure)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)

---

## ✨ Core Features

Afterma is built to support mothers dynamically through every stage of their postpartum and wellness journey:

### 1. 🤖 Afterma AI (Empathetic Chat Companion)
* Powered by the **Google Gemini API** (via `firebase-ai` SDK).
* Provides 24/7 personalized, compassionate guidance for sleep deprivation, baby-care queries, post-birth adjustments, and maternal stress management.

### 2. 📅 Postpartum Recovery Timeline (Care Journey)
* Chronological stages of recovery tailored to postpartum phases (Immediate Postpartum, Early Weeks, etc.).
* Dynamic checklists tracking maternal mental check-ins, physical recovery tasks, and baby milestones.

### 3. 🌙 Mental Wellness Sanctuary (Quiet Room)
* Access to guided mindfulness audio journeys and deep breathing exercises.
* Daily mental check-ins, mood tracking, and a secure private journal.
* Local persistent storage of mood history and mindfulness logs.

### 4. 🍵 Restorative Kitchen (Safe Recipes)
* A collection of clean, nutrient-dense recipes customized to specific maternal needs (Lactation Support, Sleep & Calm, Postpartum Recovery).
* Examples: *Ayurvedic Restorative Kitchari*, *Lactation Boosting Fennel Tea*, and *Calming Ashwagandha Golden Milk*.

### 5. 🩸 Menstrual & Fertility Cycle Tracker
* Tailored specifically for the postpartum period to track cycle return, symptoms, flow intensity, and body signals.

### 6. 🍼 Lactation & Feeding Log
* Track breastfeeding duration (left/right breast), pumping sessions (volume & time), and formula feeds.

### 7. 📚 Educational Clinic (Learning Hub)
* Evidence-based articles written by maternal healthcare experts covering physical healing, PMADs (Postpartum Mood & Anxiety Disorders), and newborn care.
* Emphasizes critical red flags and warning signs for safe recovery.

### 8. 🩺 Care Connect (Specialist Directory)
* Seamless virtual booking platform connecting mothers to certified Perinatal Psychotherapists, IBCLC Lactation Consultants, and Pelvic Floor Physiotherapists.

### 9. 💬 Embrace Forum (Community Support)
* Safe, anonymous community space to share experiences, ask questions, and support peers.

---

## 🛠️ Tech Stack & Architecture

| Layer | Component / Tool | Details |
|---|---|---|
| **Language** | [Kotlin](https://kotlinlang.org/) | Modern, expressive, and type-safe language (`v2.2.10`) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/compose) | Fully declarative native UI rendering |
| **Architecture** | MVVM (Model-View-ViewModel) | Clean separation of concerns with unidirectional data flow |
| **Database** | [Room Database](https://developer.android.com/training/data-storage/room) | Local SQLite caching and persistence (via Room `v2.7.0` & KSP) |
| **AI Integration** | [Google Gemini API](https://firebase.google.com/docs/vertex-ai) | Integration using the official Google Firebase AI SDK (`firebase-ai`) |
| **Secrets Management** | [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin) | Securely injects API keys and environment variables at build-time |
| **Networking** | [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) | REST API consumption and interceptor logging |
| **Image Loading** | [Coil](https://coil-kt.github.io/coil/) | Lightweight asynchronous image loading for Compose |
| **Screenshot Tests** | [Roborazzi](https://github.com/takahirom/roborazzi) | Automated visual regression and screenshot testing |

---

## 📋 Prerequisites

Before opening the project, ensure you have the following installed:

* **Android Studio** Ladybug (2024.2.1) or newer recommended (Min SDK version compatibility: Hedgehog)
* **JDK 17** configured as your Gradle JVM in Android Studio settings:
  * `Settings/Preferences` → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → `Gradle JDK` → `JDK 17`
* **Android SDK 36** (targetSdk and compileSdk set to 36)
* **Android Min SDK 24** (Android 7.0 Nougat)

---

## 🚀 Getting Started

Follow these steps to run Afterma on your machine or emulator:

### 1. Clone the repository
```bash
git clone https://github.com/ankeetray2/afterma-mobile.git
cd afterma-mobile
```

### 2. Set up local secrets
Copy the example environment configuration:
```bash
cp .env.example .env
```
Open `.env` in your editor and input your credentials:
```properties
GEMINI_API_KEY=your_actual_gemini_api_key_here
```

### 3. Open in Android Studio
1. Select **File** → **Open...**
2. Browse to the cloned directory and click **OK**.
3. Allow Gradle to sync and download all dependencies.

### 4. Run the Application
1. Connect a physical Android device or launch an Emulator (running API level 24+).
2. Click the **Run** button (green play icon) or press `Shift + F10` (Windows) / `Control + R` (macOS).

---

## 🔐 Environment & Secrets Setup

> [!IMPORTANT]
> The `.env` file contains your local secrets and api keys. It is listed in `.gitignore` and **must never be committed to source control**.

At build-time, the [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin) automatically parses `.env` and generates the corresponding `BuildConfig` fields, exposing them safely to the Kotlin codebase.

| Variable | Scope | Description |
|---|---|---|
| `GEMINI_API_KEY` | Debug & Release | Required for utilizing all generative AI features (Afterma AI chat). |
| `KEYSTORE_FILE` | Release Only | Path to your signing keystore (e.g., `my-upload-key.jks`). |
| `KEYSTORE_PASSWORD` | Release Only | Password of the release keystore. |
| `KEY_ALIAS` | Release Only | Alias name inside the release keystore. |
| `KEY_PASSWORD` | Release Only | Password of the release key. |

---

## 📦 Building the App

Use the Gradle Wrapper (`gradlew`) to build the application from the CLI:

```bash
# Clean the project build directories
./gradlew clean

# Build the Debug APK
./gradlew assembleDebug

# Build the Release APK (requires signing credentials configured in .env)
./gradlew assembleRelease

# Install the Debug build onto a connected device or emulator
./gradlew installDebug
```

---

## 🧪 Testing Strategy

Afterma uses a robust testing setup that includes both unit tests and screenshot visual regression tests.

```bash
# Run all Local JVM Unit Tests
./gradlew test

# Verify visual layout against baseline screenshots (Roborazzi)
./gradlew verifyRoborazziDebug

# Record and update baseline screenshots (run when UI components change intentionally)
./gradlew recordRoborazziDebug
```

---

## 🗂️ Project Structure

The project conforms to clean code conventions and feature-based packaging:

```
afterma-mobile/
├── app/                        # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/       # Repositories, Database (Room), and DAOs
│   │   │   │   ├── features/   # Feature packages (auth, carejourney, chat, community, cycle, dashboard, lactation, learning, profile, recipes, wellness)
│   │   │   │   ├── ui/         # Navigation, Components, Design System, Themes, ViewModels
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/            # Static assets, drawables, layout configs
│   │   └── test/               # Local unit tests and Roborazzi screenshot tests
│   └── build.gradle.kts        # App-level gradle build script
├── gradle/                     # Gradle wrapper and dependency version catalog (libs.versions.toml)
├── .build-outputs/             # Build output and staging artifacts
├── .env.example                # Example environment file (committed)
├── .env                        # Local secrets file (ignored by Git)
├── build.gradle.kts            # Project-level gradle build script
├── settings.gradle.kts         # Multi-project gradle module settings
└── README.md                   # Project documentation
```

---

## 🤝 Contributing

We welcome contributions to help improve Afterma!

1. **Fork** the repository and create a new feature branch:
   ```bash
   git checkout -b feat/amazing-feature
   ```
2. **Commit** your changes with clear, descriptive commit messages.
3. **Test** your changes locally:
   ```bash
   ./gradlew test verifyRoborazziDebug
   ```
4. **Push** your branch and open a **Pull Request** detailing the changes.

---

## 📜 License

Private / All rights reserved — Afterma © 2026
