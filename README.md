# My Bank: Android Mobile Banking App

A native Android mobile banking and digital wallet app written in Java, in the style of bKash, Nagad or a retail bank app. Users sign in with their phone number by SMS code, register once with KYC details, unlock the app with a 4-digit PIN, and then cash in, cash out, send money, pay bills and move money to savings. Balance and transaction history are stored per user in Firebase Realtime Database and update live on screen.

<p>
  <img alt="Platform" src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white">
  <img alt="Language" src="https://img.shields.io/badge/Language-Java-007396?logo=openjdk&logoColor=white">
  <img alt="Backend" src="https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase&logoColor=black">
  <img alt="Min SDK" src="https://img.shields.io/badge/minSdk-24-blue">
  <img alt="Target SDK" src="https://img.shields.io/badge/targetSdk-34-blue">
</p>

> This is a demo and academic project. It moves no real money, and several screens use built-in demo data (see [Known limitations](#known-limitations)).

---

## Contents

- [Features](#features)
- [App flow](#app-flow)
- [Screens](#screens)
- [Architecture](#architecture)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Firebase setup](#firebase-setup)
- [Data model](#data-model)
- [Building the APK](#building-the-apk)
- [Running the app](#running-the-app)
- [Known limitations](#known-limitations)
- [License](#license)

## Features

| Feature | Status | Notes |
| --- | --- | --- |
| Phone sign-in (SMS one-time code) | Live | Firebase Phone Auth. Bangladeshi numbers, `+880` prefix added automatically. |
| One-time registration (KYC) | Live | Name, NID, date of birth, address, bank account and a 4-digit PIN, saved to Firebase. |
| PIN lock on app open | Live | The PIN is stored on the device only as a SHA-256 hash, so unlocking works offline. |
| Wallet balance | Live | Updates on screen in real time. Can be hidden with the eye toggle. New accounts start with ৳ 4,500. |
| Cash In / Cash Out | Live | Custom amount keypad, quick-amount chips, PIN confirmation, success receipt. |
| Send Money | Live | Recipient field plus amount. Checks for insufficient balance. |
| Pay Bill | Live | 8 biller categories (electricity, gas, water, internet, mobile, card, tuition, insurance). |
| Move to Savings | Live | Moves money from the wallet balance to the savings balance. |
| Transaction history | Live | Newest first, shown on Home and on each account's detail screen. |
| Offline tolerance | Live | Realtime Database disk persistence is enabled at app start. |
| Multiple accounts and net worth | Demo data | Savings, current, fixed deposit, e-wallet and loan cards come from `DemoData`. |
| QR scan | UI only | Screen layout only, with no camera scanning yet. |
| Profile settings | UI only | The settings list is static. Logout works. |

All amounts are shown in Bangladeshi Taka (৳) with lakh-style grouping, for example `৳ 5,20,400.00`.

## App flow

```
SplashActivity
   ├─ not signed in ─────────────► LoginActivity (phone → SMS code)
   │                                   ├─ new user ───► RegisterActivity (KYC + PIN) ─┐
   │                                   └─ returning ──────────────────────────────────┤
   ├─ signed in, PIN on device ──► PinActivity ───────────────────────────────────────┤
   └─ signed in, no PIN on device ────────────────────────────────────────────────────┤
                                                                                      ▼
                                                     MainActivity (bottom navigation)
                                         Home · Accounts · Scan · Bills · Profile
                                                      │
                                                      └─► TransactActivity
                                         amount keypad → PIN confirm → receipt
```

"Use password instead" on the PIN screen signs the user out and returns to phone login.

## Screens

| Screen | Class | What it does |
| --- | --- | --- |
| Splash | `SplashActivity` | Routes to login, PIN lock or the dashboard depending on the Firebase sign-in state. |
| Login | `LoginActivity` | Two-step phone verification with resend. Pre-fills the last number used. |
| Register | `RegisterActivity` | KYC form with a date picker and PIN confirmation. |
| PIN lock | `PinActivity` | 4-digit unlock that checks automatically once 4 digits are entered. |
| Home | `HomeFragment` | Greeting, live balance, quick actions, accounts and the 4 latest transactions. |
| Accounts | `AccountsFragment` | Account cards with total net worth. |
| Account detail | `AccountDetailActivity` | Balance card, Cash In / Cash Out shortcuts and the 8 latest transactions. |
| Bills | `BillsFragment` | Biller grid and upcoming bills. |
| Scan | `ScanFragment` | QR scan placeholder. |
| Profile | `ProfileFragment` | Name, phone, settings list and logout. |
| Transaction | `TransactActivity` | Shared flow for Cash In, Cash Out, Send Money and Pay Bill. |

## Architecture

The app is a single module that uses classic Android Views with XML layouts (no Jetpack Compose).

- **Facade:** `FinancialServicesFacade` is the only class that reads or writes wallet data. It wraps Firebase Auth and Realtime Database behind simple asynchronous calls (`cashIn`, `cashOut`, `sendMoney`, `payBill`, `addToSavings`, `observeUser`, `observeTransactions`, `createProfile` and others), each of which reports back through a callback.
- **Single update per operation:** every operation writes the new balance (and savings, if it changed) together with the new transaction record in one `updateChildren` call, so the balance and the history cannot get out of step.
- **Live listeners:** screens subscribe in `onStart` and unsubscribe in `onStop`, so the balance and history refresh as soon as the database changes.
- **Local caches:** `SessionManager` keeps the PIN hash for the signed-in user, and `LocalProfile` keeps the name and phone number for greetings and login pre-fill. Both use `SharedPreferences`. Firebase holds the authoritative copy.
- **Binders and helpers:** `AccountBinder` and `TransactionBinder` fill in list rows. `MoneyUtils` formats currency, and `Ui` provides dp conversion and rounded backgrounds.

## Tech stack

- **Language:** Java (source and target compatibility 1.8)
- **UI:** Android Views, XML layouts, Material Components, ConstraintLayout, fragments
- **Backend:** Firebase Authentication (phone) and Firebase Realtime Database
- **Build:** Gradle Kotlin DSL with the Gradle Wrapper and a version catalog (`gradle/libs.versions.toml`)

| Component | Version |
| --- | --- |
| Android Gradle Plugin | 8.4.0 |
| Gradle | 8.6 |
| compileSdk / targetSdk | 34 |
| minSdk | 24 (Android 7.0) |
| Firebase BoM | 33.1.2 |
| Google Services plugin | 4.5.0 |
| AppCompat | 1.6.1 |
| Material Components | 1.12.0 |
| ConstraintLayout | 2.1.4 |

## Project structure

```
BankManagementAndroid/
├── app/
│   ├── build.gradle.kts              App module config (applicationId com.mobilebank)
│   ├── google-services.json          Firebase project config
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/myapplication/
│       │   ├── App.java                      Application class, enables offline persistence
│       │   ├── SplashActivity.java           Start-up routing
│       │   ├── LoginActivity.java            Phone + SMS code sign-in
│       │   ├── RegisterActivity.java         KYC registration
│       │   ├── PinActivity.java              PIN unlock
│       │   ├── MainActivity.java             Bottom-navigation host
│       │   ├── HomeFragment.java
│       │   ├── AccountsFragment.java
│       │   ├── BillsFragment.java
│       │   ├── ScanFragment.java
│       │   ├── ProfileFragment.java
│       │   ├── AccountDetailActivity.java
│       │   ├── TransactActivity.java         Keypad → PIN → receipt flow
│       │   ├── FinancialServicesFacade.java  All Firebase reads and writes
│       │   ├── SessionManager.java           On-device PIN hash
│       │   ├── LocalProfile.java             On-device name and phone cache
│       │   ├── Profile.java                  KYC data
│       │   ├── Transaction.java              Transaction record
│       │   ├── Account.java                  Account card data
│       │   ├── DemoData.java                 Demo accounts, billers and settings
│       │   ├── AccountBinder.java
│       │   ├── TransactionBinder.java
│       │   ├── MoneyUtils.java               Taka formatting
│       │   └── Ui.java                       Small view helpers
│       └── res/                              Layouts, drawables, colors, strings
├── html/                             Static HTML mock-ups of the app design
├── BankManagement-debug.apk          Prebuilt debug APK
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/libs.versions.toml
└── gradlew / gradlew.bat
```

## Firebase setup

The app ships with a `google-services.json` that connects it to an existing Firebase project. To use your own project instead:

1. Create a Firebase project and add an Android app with the package name **`com.mobilebank`**. This must match `applicationId` in `app/build.gradle.kts`.
2. Under **Authentication → Sign-in method**, enable **Phone**.
3. Add your app's SHA-1 and SHA-256 fingerprints in the Firebase project settings. Phone Auth needs them on real devices. You can print them with `.\gradlew.bat signingReport`.
4. Create a **Realtime Database**, then download the updated `google-services.json` so that it includes the database URL.
5. Replace `app/google-services.json` with your file.
6. Set database rules so that each user can access only their own data:

   ```json
   {
     "rules": {
       "users": {
         "$uid": {
           ".read": "auth != null && auth.uid === $uid",
           ".write": "auth != null && auth.uid === $uid"
         }
       }
     }
   }
   ```

### Test phone numbers

Under **Authentication → Sign-in method → Phone → Phone numbers for testing**, add a number and a fixed code (for example `+8801700000000` / `123456`). Test numbers send no real SMS. Debug builds also turn off app verification (`setAppVerificationDisabledForTesting`), so test numbers work on emulators and Appetize without the reCAPTCHA step. Release builds do not include this setting.

## Data model

Each user's data is stored under `users/$uid`:

```json
{
  "users": {
    "<firebase-uid>": {
      "name": "Rahim Uddin",
      "phone": "+8801700000000",
      "nid": "1234567890",
      "dob": "01 Jan 2000",
      "address": "Dhaka",
      "bankAccount": "0123456789",
      "pinHash": "<sha-256 hex>",
      "balance": 4500.0,
      "savings": 0.0,
      "transactions": {
        "<push-id>": {
          "type": "Send Money",
          "title": "To Karim",
          "amount": 500.0,
          "incoming": false,
          "timestamp": 1752700000000,
          "balanceAfter": 4000.0
        }
      }
    }
  }
}
```

| Transaction field | Type | Meaning |
| --- | --- | --- |
| `type` | String | `Cash In`, `Cash Out`, `Send Money`, `Pay Bill` or `Savings` |
| `title` | String | Counterparty or detail, for example `To Karim` or `Electricity` |
| `amount` | double | Always positive |
| `incoming` | boolean | `true` when money comes into the wallet |
| `timestamp` | long | Milliseconds since epoch |
| `balanceAfter` | double | Wallet balance after the transaction |

## Building the APK

You can build with Android Studio (open the folder and run the `app` configuration) or entirely from the command line.

### Command line, without Android Studio (Windows PowerShell)

Requirements: JDK 17 to 21 and the Android SDK command-line tools. You don't need to install Gradle separately, because the wrapper downloads Gradle 8.6 on first run.

```powershell
# 1. Point the build at your JDK
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# 2. Install the SDK packages (after unzipping the command-line tools into
#    C:\Android\sdk\cmdline-tools\latest\)
$sdkm = "C:\Android\sdk\cmdline-tools\latest\bin\sdkmanager.bat"
& $sdkm --licenses --sdk_root="C:\Android\sdk"
& $sdkm --sdk_root="C:\Android\sdk" "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# 3. Tell Gradle where the SDK is
Set-Content local.properties 'sdk.dir=C\:\\Android\\sdk'

# 4. Build
.\gradlew.bat assembleDebug
```

The APK is written to `app\build\outputs\apk\debug\app-debug.apk` and is signed with the debug key, so you can install it straight away.

| Command | Result |
| --- | --- |
| `.\gradlew.bat assembleDebug` | Build the debug APK |
| `.\gradlew.bat installDebug` | Build and install on a connected device or emulator |
| `.\gradlew.bat assembleRelease` | Build an unsigned release APK (needs a keystore to install) |
| `.\gradlew.bat clean` | Delete build outputs |

## Running the app

- **Prebuilt APK:** install `BankManagement-debug.apk` from the repository root on any Android 7.0+ device or emulator.
- **Device or emulator:** run `.\gradlew.bat installDebug`, or press Run in Android Studio.
- **In the browser:** upload the debug APK to [appetize.io](https://appetize.io) and log in with a Firebase test phone number.

## Known limitations

- **Balances are calculated on the device.** The app reads the balance, works out the new value and writes it back. Two transactions at the same moment could overwrite each other, and a modified client could write any balance. A production app would do this on the server (for example with Cloud Functions or database transactions plus validation rules).
- **Transfers don't reach a real recipient.** Send Money deducts from the sender but credits no other user.
- **Some data is fixed demo data.** The multiple accounts, net worth, billers, upcoming bills and settings come from `DemoData`. The account detail screen shows wallet transactions, not transactions for that account.
- **QR scan and Statement are placeholders.**
- **Only Bangladeshi numbers are supported.** Login assumes the `+880` country code.
- **PIN storage is basic.** The PIN is hashed with plain SHA-256 and no salt, and there is no limit on wrong attempts.
- The Java package is still the template's `com.example.myapplication`, while the application id is `com.mobilebank`.

## License

No license file is included yet. Add one (for example MIT) if you plan to distribute this project.
