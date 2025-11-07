# SimpleConvert - Quick Start Guide

## 🚀 Get Started in 3 Minutes

This guide will help you build and run the SimpleConvert Forex Calculator app on your device.

## Prerequisites

Before you begin, ensure you have:

- ✅ **Android Studio** (Hedgehog 2023.1.1 or later)
- ✅ **JDK 11** or later (usually bundled with Android Studio)
- ✅ **Android Device** or **Emulator** running Android 7.1 (API 25) or higher
- ✅ **Internet Connection** (required for downloading dependencies and API calls)

## Step-by-Step Instructions

### 1. Open the Project

1. Launch **Android Studio**
2. Click **File** → **Open**
3. Navigate to the project folder: `C:/Users/aadit/AndroidStudioProjects/forexcalculatorapp`
4. Click **OK**

### 2. Sync Gradle

Android Studio will automatically start syncing Gradle files. You'll see a progress bar at the
bottom.

**If sync doesn't start automatically:**

- Click **File** → **Sync Project with Gradle Files**
- Or click the elephant icon (🐘) in the toolbar

**Wait for the sync to complete.** This may take 1-3 minutes on first run as it downloads
dependencies.

### 3. Set Up a Device

#### Option A: Use a Physical Device

1. Enable **Developer Options** on your Android device:
    - Go to **Settings** → **About Phone**
    - Tap **Build Number** 7 times
2. Enable **USB Debugging**:
    - Go to **Settings** → **Developer Options**
    - Turn on **USB Debugging**
3. Connect your device via USB
4. Select **Allow** when prompted on your device

#### Option B: Use an Emulator

1. Click **Device Manager** in the toolbar (phone icon)
2. Click **Create Device**
3. Select a device (e.g., "Pixel 6")
4. Select a system image (API 25 or higher recommended: API 33+)
5. Click **Finish**

### 4. Run the App

1. Ensure your device/emulator is selected in the device dropdown
2. Click the **Run** button (▶️ green play icon) or press **Shift + F10**
3. Wait for the app to build and install (30-60 seconds)
4. The app will launch automatically

## 🎉 Success!

If everything worked, you should see the **SimpleConvert** app with:

- A large amount input field at the top
- "From" currency card (USD by default)
- A swap button in the center
- "To" currency card (EUR by default)
- A result card at the bottom

## How to Use the App

### Basic Conversion

1. **Enter an amount** in the input field at the top
2. Wait 300ms - the conversion happens automatically!
3. See the **converted amount** in the result card at the bottom

### Change Currencies

1. **Tap** on the "From" or "To" currency card
2. A modal will slide up with a searchable list
3. **Search** by typing a currency code (e.g., "GBP") or name (e.g., "British")
4. **Tap** on a currency to select it
5. Conversion updates automatically!

### Swap Currencies

1. **Tap** the swap button (⇅) between the currency cards
2. From and To currencies switch instantly
3. Conversion updates automatically!

### View Exchange Rate

- Below the converted amount, you'll see the exchange rate
- Example: "1 USD = 0.9185 EUR"

## Troubleshooting

### ❌ Gradle Sync Failed

**Problem**: "Failed to sync Gradle project"

**Solution**:

1. Click **File** → **Invalidate Caches** → **Invalidate and Restart**
2. Wait for Android Studio to restart
3. Try syncing again

### ❌ Build Failed

**Problem**: "Build failed with an exception"

**Solution**:

1. Click **Build** → **Clean Project**
2. Wait for it to complete
3. Click **Build** → **Rebuild Project**

### ❌ App Crashes on Launch

**Problem**: App crashes immediately after launching

**Solution**:

1. Check if your device/emulator is running API 25 or higher
2. Check logcat for error messages (bottom panel in Android Studio)
3. Ensure you have internet connection

### ❌ "Couldn't update rates" Error

**Problem**: Error message appears in the app

**Solution**:

- Check your internet connection
- Verify the Frankfurter API is accessible: https://api.frankfurter.dev/currencies
- Wait a few seconds and try again

### ❌ Compose Preview Not Working

**Problem**: Can't see Compose previews

**Solution**:

1. Click **Build** → **Rebuild Project**
2. Restart Android Studio
3. Update Android Studio to the latest version

## Testing the App

### Test Features

1. **Amount Input**
    - ✅ Enter various amounts (100, 1000, 0.5, etc.)
    - ✅ Try entering letters (should be blocked)
    - ✅ Observe the 300ms debounce (conversion waits briefly)

2. **Currency Selection**
    - ✅ Open the currency picker
    - ✅ Search for currencies (try "USD", "Euro", "Japanese")
    - ✅ Select different currencies
    - ✅ Observe automatic conversion

3. **Swap Function**
    - ✅ Enter an amount
    - ✅ Tap the swap button
    - ✅ Verify currencies switched
    - ✅ Check that conversion updated

4. **Loading State**
    - ✅ Enter an amount quickly
    - ✅ Observe the shimmer loading effect
    - ✅ Wait for result to appear

5. **Dark Mode**
    - ✅ Enable dark mode on your device
    - ✅ Reopen the app
    - ✅ Verify colors look good in dark theme

## Next Steps

### Explore the Code

Now that the app is running, explore the codebase:

1. **Start with**: `MainActivity.kt` - The entry point
2. **Then check**: `MainScreen.kt` - The main UI
3. **Understand state**: `MainViewModel.kt` - Business logic
4. **See API calls**: `CurrencyRepository.kt` - Data layer

### Customize the App

Try making these changes:

1. **Change default currencies**:
    - Open `MainViewModel.kt`
    - Edit `MainUiState` defaults (line 21-22)
    - Example: Change EUR to GBP

2. **Adjust debounce time**:
    - Open `MainViewModel.kt`
    - Find `delay(300)` (line 70)
    - Change to `delay(500)` for slower response

3. **Modify colors**:
    - Open `ui/theme/Theme.kt`
    - Edit the color values
    - Rebuild and see changes

### Build for Release

When ready to share your app:

1. Click **Build** → **Generate Signed Bundle / APK**
2. Choose **APK**
3. Create a keystore (if first time)
4. Sign your APK
5. Install the APK on any Android device

## Resources

### Documentation

- `README.md` - Project overview and features
- `IMPLEMENTATION_GUIDE.md` - Detailed architecture guide
- `PROJECT_SUMMARY.md` - Complete file listing

### API Documentation

- Frankfurter API: https://www.frankfurter.app/

### Learning Resources

- Jetpack Compose: https://developer.android.com/jetpack/compose
- Material 3: https://m3.material.io/
- Kotlin Coroutines: https://kotlinlang.org/docs/coroutines-overview.html

## Support

### Common Questions

**Q: Can I use this app without internet?**
A: No, the app requires internet to fetch exchange rates. Offline mode is a potential future
feature.

**Q: How often are rates updated?**
A: The Frankfurter API updates rates daily. Each conversion fetches the latest available rate.

**Q: How many currencies are supported?**
A: The app supports 30+ major currencies provided by the Frankfurter API.

**Q: Can I see historical rates?**
A: Not in the current version. This is a potential future enhancement.

**Q: Is my data private?**
A: Yes! The app doesn't collect, store, or transmit any personal data. Only conversion requests are
sent to the API.

## Feedback

Found a bug or have a suggestion?

This is a demonstration project showcasing modern Android development practices.

---

**Happy Converting! 💱**

*Built with ❤️ using Kotlin & Jetpack Compose*
