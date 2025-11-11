# Setup Instructions for Developers

This guide will help you set up and run the **SimpleConvert Forex Calculator** app on your local
machine.

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (Hedgehog or later recommended)
- **JDK 11** or higher
- **Git** (for cloning the repository)
- **Android SDK** with API level 25+ (Android 7.1) and API level 36

---

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/Itzz-Aadi/forex-calculator-app.git
cd forex-calculator-app
```

### 2. Open in Android Studio

1. Open Android Studio
2. Click **File → Open**
3. Navigate to the cloned repository folder
4. Click **OK**

### 3. Configure Firebase (REQUIRED for Gemini AI)

The app uses Firebase Vertex AI for the Gemini chatbot feature. You need to set up your own Firebase
project:

#### Step-by-step Firebase Setup:

1. **Go to Firebase Console**: https://console.firebase.google.com/
2. **Create a new project** or use an existing one
3. **Add an Android app** to your project:
    - Package name: `com.example.forexcalculatorapp`
    - Download the `google-services.json` file
4. **Place `google-services.json`** in the `app/` folder:
   ```
   forex-calculator-app/
   └── app/
       ├── src/
       ├── build.gradle.kts
       └── google-services.json  ← Place here
   ```
5. **Enable Vertex AI in Firebase**:
    - In Firebase Console, go to **Build → Vertex AI in Firebase**
    - Click **Get started** or **Enable**
    - Upgrade to Blaze plan (free tier: 1,500 requests/day)

📖 **Detailed guide**: See [FIREBASE_SETUP_COMPLETE_GUIDE.md](FIREBASE_SETUP_COMPLETE_GUIDE.md)

### 4. Sync and Build

1. In Android Studio, click **File → Sync Project with Gradle Files**
2. Wait for the sync to complete
3. Click **Build → Rebuild Project**

### 5. Run the App

1. Connect an Android device or start an emulator
2. Click the **Run** button (green play icon) or press `Shift + F10`
3. Select your device/emulator
4. Wait for the app to install and launch

---

## 🔧 Configuration

### API Keys

The app uses the following APIs:

#### 1. Frankfurter API (Currency Exchange Rates)

- **No API key required** ✅
- Free and open-source
- Automatically works out of the box

#### 2. Twelve Data API (Stock Prices & Historical Data)

- **API Key**: Currently uses a free demo key
- **Optional**: Replace with your own key in `FinancialApiService.kt` (line ~50)
- Get a free key at: https://twelvedata.com/

#### 3. Firebase Vertex AI (Gemini)

- **Configured via `google-services.json`** (see step 3 above)
- Required for the Gemini Chat feature

---

## 🏗️ Project Structure

```
app/src/main/java/com/example/forexcalculatorapp/
├── data/
│   ├── CurrencyApiService.kt          # Frankfurter API
│   ├── FinancialApiService.kt         # Twelve Data API
│   ├── GeminiService.kt               # Firebase Vertex AI
│   ├── CurrencyRepository.kt          # Currency data
│   └── StockRepository.kt             # Stock data
├── ui/
│   ├── components/
│   │   ├── CurrencyCard.kt
│   │   ├── CurrencyPickerModal.kt
│   │   ├── CurrencyChart.kt
│   │   ├── StockChart.kt
│   │   └── StockSearchModal.kt
│   ├── screens/
│   │   ├── MainScreen.kt              # Currency conversion
│   │   ├── StockListScreen.kt         # Stock browser
│   │   ├── ForexMarketScreen.kt       # Market dashboard
│   │   └── GeminiChatScreen.kt        # AI chat
│   └── theme/
│       ├── Theme.kt
│       └── Type.kt
├── viewmodel/
│   ├── MainViewModel.kt
│   ├── StockListViewModel.kt
│   ├── ForexMarketViewModel.kt
│   └── GeminiViewModel.kt
└── MainActivity.kt
```

---

## 🎨 Features

- ✅ **Currency Conversion** with real-time exchange rates
- ✅ **Stock Price Browser** with 300+ stocks
- ✅ **Real-Time Charts** for currencies and stocks
- ✅ **Forex Market Dashboard** with 10 popular pairs
- ✅ **Gemini AI Assistant** for forex questions and analysis
- ✅ **Material 3 Design** with light/dark themes
- ✅ **Smooth Animations** and premium UI/UX

---

## 🐛 Troubleshooting

### Build Fails: "google-services.json is missing"

**Solution**: Follow step 3 above to add the Firebase configuration file.

### Gemini Chat Shows Errors

**Solution**: Make sure you've:

1. Added `google-services.json` to the `app/` folder
2. Enabled Vertex AI in Firebase Console
3. Upgraded to the Blaze plan (free tier available)

### Stock Prices Not Loading

**Solution**: The free Twelve Data API has rate limits (8 requests/minute). If you hit the limit,
wait a minute and try again, or get your own API key.

### App Crashes on Launch

**Solution**:

1. Clean and rebuild: **Build → Clean Project**, then **Build → Rebuild Project**
2. Invalidate caches: **File → Invalidate Caches → Restart**
3. Check minimum SDK: Your device must be Android 7.1 (API 25) or higher

---

## 📚 Documentation

- **[README.md](README.md)** - Project overview and features
- **[FIREBASE_SETUP_COMPLETE_GUIDE.md](FIREBASE_SETUP_COMPLETE_GUIDE.md)** - Detailed Firebase setup
- **[MIGRATION_COMPLETE_SUMMARY.md](MIGRATION_COMPLETE_SUMMARY.md)** - Gemini integration details
- **[HOW_TO_FIX_404_ERROR.md](HOW_TO_FIX_404_ERROR.md)** - Troubleshooting Gemini errors

---

## 🛠️ Tech Stack

- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM with StateFlow
- **Networking**: Retrofit 2 + OkHttp
- **Charts**: Vico (Compose-based charting)
- **AI**: Firebase Vertex AI (Gemini 1.5 Flash)
- **Async**: Kotlin Coroutines + Flow

---

## 📱 Minimum Requirements

- **Minimum SDK**: API 25 (Android 7.1)
- **Target SDK**: API 36
- **Compile SDK**: API 36

---

## 🤝 Contributing

Feel free to fork this repository and submit pull requests. For major changes, please open an issue
first to discuss what you would like to change.

---

## 📄 License

This project is created as a demonstration of modern Android development practices.

---

## 🆘 Need Help?

If you encounter any issues:

1. Check the [FIREBASE_SETUP_COMPLETE_GUIDE.md](FIREBASE_SETUP_COMPLETE_GUIDE.md)
2. Review the [Troubleshooting section](#-troubleshooting) above
3. Open an issue on GitHub

---

**Happy Coding!** 🚀
