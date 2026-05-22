
# Afterma — Maternal Wellness Mobile App

> Premium maternal healing and emotional wellness platform for postpartum recovery, pediatric support, therapy journeys, and safe motherhood.

---

## Table of Contents

- [About](#about)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Setup](#environment-setup)
- [Building](#building)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Contributing](#contributing)

---

## About

**Afterma** is an Android application built with Kotlin and Jetpack Compose. It is powered by Google's Gemini AI (via AI Studio server-side) to provide personalised maternal health support, postpartum recovery guidance, therapy journeys, and safe motherhood resources.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| AI | Gemini API (server-side via AI Studio) |
| Build | Gradle (Kotlin DSL) |
| DI | KSP (Kotlin Symbol Processing) |
| Screenshot Tests | Roborazzi |
| Secrets Management | Secrets Gradle Plugin |

---

## Prerequisites

- **Android Studio** Hedgehog (2023.1) or newer
- **JDK 17** (Gradle JVM set to JDK 17 in Android Studio settings)
- **Android SDK 34** (targetSdk)
- **Gemini API Key** — get one free at [Google AI Studio](https://aistudio.google.com/app/apikey)

---

## Getting Started

```bash
# 1. Clone the repository
git clone https://github.com/ankeetray2/afterma-mobile.git
cd afterma-mobile

# 2. Set up your environment file
cp .env.example .env
# Open .env and fill in your GEMINI_API_KEY and any other values

# 3. Open in Android Studio
# File → Open → select the afterma-mobile directory

# 4. Let Gradle sync complete, then Run the app
```

---

## Environment Setup

This project uses the [Secrets Gradle Plugin](https://github.com/google/secrets-gradle-plugin) to inject environment variables at build time.

1. Copy `.env.example` → `.env`
2. Fill in your actual keys (see the file for descriptions of each variable)
3. **Never commit `.env`** — it is already listed in `.gitignore`

When running inside **Google AI Studio**, the `GEMINI_API_KEY` is injected automatically via the AI Studio Secrets panel — you don't need to set it manually there.

### Required Variables

| Variable | Description |
|---|---|
| `GEMINI_API_KEY` | Your Gemini AI API key (required for all AI features) |
| `KEYSTORE_FILE` | Path to your signing keystore (release builds only) |
| `KEYSTORE_PASSWORD` | Keystore password (release builds only) |
| `KEY_ALIAS` | Key alias inside the keystore (release builds only) |
| `KEY_PASSWORD` | Key password (release builds only) |

---

## Building

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing keystore configured in .env)
./gradlew assembleRelease

# Build and install on connected device
./gradlew installDebug
```

---

## Running Tests

```bash
# Unit tests
./gradlew test

# Screenshot tests (Roborazzi)
./gradlew verifyRoborazziDebug

# Record new screenshots (update baselines)
./gradlew recordRoborazziDebug
```

---

## Project Structure

```
afterma-mobile/
├── app/                        # Main Android application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/           # Kotlin source files
│   │   │   ├── res/            # Resources (layouts, drawables, strings)
│   │   │   └── AndroidManifest.xml
│   │   └── test/               # Unit & screenshot tests
│   └── build.gradle.kts        # App-level Gradle config
├── gradle/                     # Gradle wrapper & version catalog
├── .build-outputs/             # AI Studio build artifact staging
├── .env.example                # Environment variable template (commit this)
├── .env                        # Your local secrets (DO NOT commit)
├── .gitignore
├── build.gradle.kts            # Root Gradle config
├── gradle.properties           # JVM args, parallel builds, caching
├── metadata.json               # AI Studio app metadata
├── settings.gradle.kts         # Module/repo setup
└── README.md
```

---

## Contributing

1. Fork the repo and create a feature branch (`git checkout -b feat/your-feature`)
2. Make your changes and write/update tests
3. Run `./gradlew test verifyRoborazziDebug` to ensure nothing is broken
4. Open a Pull Request with a clear description

---

## License

Private / All rights reserved — Afterma © 2026
