# Bank Management App

A native Android mobile-money / digital wallet demo application, similar in concept to services like bKash, Nagad, or Google Pay. The app presents a dashboard of common financial actions (Cash In, Cash Out, Send Money, Savings, Transactions, and Pay Bill) and reads transaction history from a Firebase Realtime Database.

<p>
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/Language-Java-007396?logo=openjdk&logoColor=white">
  <img alt="Backend" src="https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase&logoColor=black">
  <img alt="Min SDK" src="https://img.shields.io/badge/minSdk-24-blue">
  <img alt="Target SDK" src="https://img.shields.io/badge/targetSdk-34-blue">
</p>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Screens](#screens)
- [Architecture and Design Patterns](#architecture-and-design-patterns)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Building the APK Without Android Studio](#building-the-apk-without-android-studio)
- [Running on Appetize.io](#running-on-appetizeio)
- [Firebase Configuration](#firebase-configuration)
- [Data Model](#data-model)
- [Known Limitations](#known-limitations)
- [License](#license)

---

## Overview

This project is a single-module Android application written in Java. It uses classic Android Views with XML layouts (no Jetpack Compose). The home screen shows six tappable cards. Each card opens a dedicated feature screen. The Transactions screen is the only screen wired to live data, pulling records from Firebase Realtime Database and rendering them in a `RecyclerView`.

The application id is `com.example.myapplication` and the launcher activity is `MainActivity`.

## Features

| Feature | Screen | Status | Description |
| --- | --- | --- | --- |
| Cash In | `CashIn` | Functional | Accepts an amount in an input field and confirms via a Toast message. |
| Cash Out | `CashOut` | Placeholder UI | Layout with edge-to-edge insets handling. Business logic not yet implemented. |
| Send Money | `SendMoney` | Placeholder UI | Layout only. Business logic not yet implemented. |
| Savings | `Savings` | Placeholder UI | Layout only. Business logic not yet implemented. |
| Transactions | `Transactions` | Functional | Live list of transactions read from Firebase Realtime Database. |
| Pay Bill | `PayBill` | Placeholder UI | Layout only. Business logic not yet implemented. |

Note: this is a demo / academic project. Several feature screens render their UI but do not yet perform real financial operations. The `FinancialServicesFacade` methods are stubs intended as extension points.

## Screens

1. **Main dashboard** (`MainActivity` + `activity_main.xml`): six `CardView` items acting as the navigation hub.
2. **Cash In** (`activity_cash_in.xml`): amount entry with validation and confirmation Toast.
3. **Transactions** (`activity_transactions.xml`): `RecyclerView` bound to Firebase data through `Adapter` and `Model`.
4. **Cash Out, Send Money, Savings, Pay Bill**: standalone activities with prepared layouts.

## Architecture and Design Patterns

The codebase demonstrates a few common design patterns:

- **Singleton**: `NavigationManager` exposes a single shared instance through `getInstance()` and centralizes all screen-to-screen `Intent` navigation.
- **Facade**: `FinancialServicesFacade` provides a simplified surface (`processPayment`, `checkBalance`, `transferMoney`, and per-feature stubs) that hides the intended payment and balance subsystems.
- **Adapter (RecyclerView)**: `Adapter` binds a list of `Model` objects to the transaction list rows defined in `item.xml`.
- **POJO / Model**: `Model` is a plain data class with a no-argument constructor so Firebase can deserialize snapshots via `DataSnapshot.getValue(Model.class)`.

High-level flow:

```
MainActivity (dashboard)
   |
   |-- NavigationManager.getInstance()  ->  startActivity(...)
   |-- FinancialServicesFacade          ->  (stubbed business logic)
   |
   +--> CashIn / CashOut / SendMoney / Savings / PayBill / Transactions
                                                              |
                                                              +--> FirebaseDatabase "transaction" node
                                                                        |
                                                                        +--> Adapter + Model -> RecyclerView
```

## Tech Stack

- **Language**: Java (source/target compatibility 1.8)
- **UI**: Android Views, XML layouts, Material Components, ConstraintLayout, CardView, RecyclerView
- **Backend**: Firebase Realtime Database (`com.google.firebase:firebase-database`)
- **Build system**: Gradle (Kotlin DSL) with the Gradle Wrapper, version catalog in `gradle/libs.versions.toml`
- **Android Gradle Plugin**: 8.4.0
- **Gradle**: 8.6
- **compileSdk / targetSdk**: 34
- **minSdk**: 24 (Android 7.0)

Key dependency versions (from `gradle/libs.versions.toml`):

| Library | Version |
| --- | --- |
| Android Gradle Plugin | 8.4.0 |
| Google Services plugin | 4.4.1 |
| AppCompat | 1.6.1 |
| Material Components | 1.12.0 |
| Activity | 1.9.0 |
| ConstraintLayout | 2.1.4 |
| Firebase Database | 21.0.0 |

## Project Structure

```
BankManagementAndroid/
├── app/
│   ├── build.gradle.kts          App module build config
│   ├── google-services.json      Firebase project config
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/myapplication/
│       │   ├── MainActivity.java             Dashboard, wires up the six cards
│       │   ├── NavigationManager.java        Singleton navigation
│       │   ├── FinancialServicesFacade.java  Facade over financial operations (stubs)
│       │   ├── CashIn.java                   Functional cash-in screen
│       │   ├── CashOut.java                  Placeholder screen
│       │   ├── SendMoney.java                Placeholder screen
│       │   ├── Savings.java                  Placeholder screen
│       │   ├── PayBill.java                  Placeholder screen
│       │   ├── Transactions.java             Firebase-backed transaction list
│       │   ├── Adapter.java                  RecyclerView adapter
│       │   └── Model.java                    Transaction data model (POJO)
│       └── res/
│           ├── layout/           Activity and item layouts
│           ├── drawable/         Icons and backgrounds
│           ├── mipmap-*/         Launcher icons
│           └── values/           Colors, strings, themes
├── build.gradle.kts              Root build config
├── settings.gradle.kts           Module and repository setup
├── gradle/libs.versions.toml     Version catalog
├── gradlew / gradlew.bat         Gradle Wrapper
└── local.properties              Local SDK path (not committed)
```

## Prerequisites

You do **not** need Android Studio to build this project. You only need:

1. **A JDK** (version 17 or newer; JDK 17 to 21 recommended for AGP 8.4).
2. **The Android SDK command-line tools** (a small download, no IDE required), with:
   - `platform-tools`
   - `platforms;android-34`
   - `build-tools;34.0.0`
3. **Gradle** is not required as a separate install. The bundled Gradle Wrapper (`gradlew`) downloads Gradle 8.6 automatically on first run.

## Building the APK Without Android Studio

The steps below produce a debug APK on Windows using PowerShell. Adjust paths for macOS or Linux as needed.

### 1. Point the build at your JDK

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-20"
```

### 2. Install the Android command-line SDK tools

Download the command-line tools zip from the official Android developer site, then arrange them into the required layout so `sdkmanager` can find itself:

```
C:\Android\sdk\cmdline-tools\latest\bin\sdkmanager.bat
```

### 3. Accept licenses and install the required packages

```powershell
$sdkm = "C:\Android\sdk\cmdline-tools\latest\bin\sdkmanager.bat"
& $sdkm --licenses --sdk_root="C:\Android\sdk"
& $sdkm --sdk_root="C:\Android\sdk" "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### 4. Tell Gradle where the SDK is

Create a `local.properties` file in the project root (`BankManagementAndroid/local.properties`):

```properties
sdk.dir=C\:\\Android\\sdk
```

### 5. Build the debug APK

```powershell
$env:ANDROID_SDK_ROOT = "C:\Android\sdk"
.\gradlew.bat assembleDebug
```

The first build downloads Gradle 8.6 and all dependencies, so it can take a few minutes. When it finishes you will see `BUILD SUCCESSFUL`.

### 6. Locate the APK

```
app\build\outputs\apk\debug\app-debug.apk
```

This APK is automatically signed with the Android debug keystore, so it is installable and runnable immediately. No manual signing step is needed for a demo.

### Other useful commands

| Command | Result |
| --- | --- |
| `.\gradlew.bat assembleDebug` | Build the debug APK (recommended for demos). |
| `.\gradlew.bat assembleRelease` | Build an unsigned release APK (needs a keystore to be installable). |
| `.\gradlew.bat clean` | Delete build outputs. |
| `.\gradlew.bat tasks` | List all available Gradle tasks. |

## Running on Appetize.io

1. Go to [appetize.io](https://appetize.io) and choose the upload option.
2. Upload `app-debug.apk`.
3. Select an Android device profile and launch the session.

The debug APK works on Appetize.io without any extra signing. Keep in mind that the Transactions screen needs network access to reach Firebase, which the Appetize.io emulator provides. If the Firebase database has no data under the `transaction` node, the list simply renders empty.

## Firebase Configuration

The app ships with a `google-services.json` under `app/`. This file binds the app to a specific Firebase project. The Transactions screen reads from the `transaction` node of the Realtime Database:

```java
FirebaseDatabase.getInstance().getReference("transaction");
```

If you want to point the app at your own Firebase project:

1. Create a Firebase project and register an Android app with the application id `com.example.myapplication`.
2. Enable Realtime Database.
3. Download your own `google-services.json` and replace the file in `app/`.
4. Add records under a `transaction` node that match the data model below.

## Data Model

Each transaction (`Model.java`) has the following fields:

| Field | Type | Description |
| --- | --- | --- |
| `id` | int | Transaction identifier |
| `amount` | int | Transaction amount |
| `type` | String | Transaction type (for example cash in, send money) |
| `reciever` | String | Recipient label (spelling matches the source field name) |
| `savings` | int | Savings amount associated with the record |
| `date` | String | Transaction date |

Example Realtime Database entry:

```json
{
  "transaction": {
    "-Nxxxxxxxxxxxxxxx": {
      "id": 1,
      "amount": 500,
      "type": "Send Money",
      "reciever": "John Doe",
      "savings": 0,
      "date": "2024-05-17"
    }
  }
}
```

## Known Limitations

- Cash Out, Send Money, Savings, and Pay Bill screens render their layouts but do not perform real operations yet.
- `FinancialServicesFacade` methods are stubs and print to the console rather than moving real money.
- The default application id and namespace remain `com.example.myapplication` from the project template.
- The app label in `strings.xml` is `My Application`. Update `app_name` if you want a branded title.
- There is no authentication layer. This is a demo, not a production banking client.

## License

No license file is currently included. Add one (for example MIT) if you intend to distribute this project.
