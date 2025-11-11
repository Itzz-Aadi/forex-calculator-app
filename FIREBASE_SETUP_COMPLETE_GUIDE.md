# 🔥 Firebase Vertex AI Setup - Complete Guide

## 🚨 CRITICAL: You MUST complete this setup for Gemini to work!

The migration to Firebase Vertex AI is **complete**, but you need to add the `google-services.json`
file for it to work.

---

## ✅ Step-by-Step Setup (5 minutes)

### Step 1: Go to Firebase Console

1. Open your browser and go to: **https://console.firebase.google.com/**
2. Sign in with your Google account

### Step 2: Create a Firebase Project

1. Click **"Add project"** or **"Create a project"**
2. Enter project name: `Forex Calculator` (or any name you prefer)
3. Click **Continue**
4. **Disable Google Analytics** (optional, not needed for this app)
5. Click **Create project**
6. Wait for setup to complete (~30 seconds)
7. Click **Continue**

### Step 3: Add Android App to Your Project

1. In the Firebase console, click the **Android icon** (looks like an Android robot)
2. Fill in the form:
    - **Android package name**: `com.example.forexcalculatorapp` ⚠️ **MUST be exactly this!**
    - **App nickname** (optional): `Forex Calculator`
    - **Debug signing certificate SHA-1** (optional): Leave blank for now
3. Click **Register app**

### Step 4: Download google-services.json

1. Click **Download google-services.json**
2. **IMPORTANT**: Save this file to your computer
3. Click **Next** (you can skip the other steps in Firebase console)
4. Click **Continue to console**

### Step 5: Add the File to Your Project

1. Locate the downloaded `google-services.json` file on your computer
2. Copy it to your Android Studio project at:
   ```
   C:/Users/aadit/AndroidStudioProjects/forexcalculatorapp/app/google-services.json
   ```
3. The file structure should look like:
   ```
   forexcalculatorapp/
   ├── app/
   │   ├── src/
   │   ├── build.gradle.kts
   │   └── google-services.json  ← Add it here!
   ├── gradle/
   └── build.gradle.kts
   ```

### Step 6: Enable Vertex AI in Firebase

1. In the Firebase Console, go to **Build** → **Vertex AI in Firebase** (left sidebar)
2. Click **Get started** or **Enable**
3. Click **Upgrade project** (this enables the Blaze plan)
    - ⚠️ **Don't worry!** Vertex AI has a generous free tier
    - Free tier: **1500 requests per day**
    - You won't be charged unless you exceed the free quota
4. Click **Continue**
5. Select your billing country and accept terms
6. Click **Continue**
7. Wait for Vertex AI to be enabled

### Step 7: Sync and Build

1. In Android Studio, click **File → Sync Project with Gradle Files**
2. Wait for sync to complete
3. Click **Build → Rebuild Project**
4. Run the app!

---

## 🎉 Test the Integration

1. **Run your app**
2. **Tap "Gemini Chat"** button on the main screen
3. **Try asking**: "What factors affect currency exchange rates?"
4. **You should get a response!** 🎊

---

## 🆓 Free Tier Limits

Firebase Vertex AI (Gemini) free tier includes:

- ✅ **1,500 requests per day**
- ✅ **1 million tokens per month**
- ✅ **No credit card required** (unless you upgrade)

This is **more than enough** for development and testing!

---

## 🔒 Security Note

The `google-services.json` file contains your Firebase project configuration. It's safe to commit to
version control (it's not a secret key), but if you want to keep it private:

1. Add to `.gitignore`:
   ```
   app/google-services.json
   ```

2. Or keep it (it's standard practice to commit this file)

---

## ❌ Common Issues & Solutions

### Issue 1: "google-services.json is missing"

**Solution**: Make sure you copied the file to `app/google-services.json` (not
`app/src/google-services.json`)

### Issue 2: "Default Firebase app is not initialized"

**Solution**:

- Ensure `google-services.json` is in the correct location
- Sync Gradle files (File → Sync Project with Gradle Files)
- Clean and rebuild (Build → Clean Project, then Build → Rebuild Project)

### Issue 3: "Vertex AI is not enabled"

**Solution**:

- Go to Firebase Console
- Navigate to **Build → Vertex AI in Firebase**
- Click **Enable** or **Get started**
- You may need to upgrade to Blaze plan (free tier is generous)

### Issue 4: Build errors after adding google-services.json

**Solution**:

```bash
cd C:/Users/aadit/AndroidStudioProjects/forexcalculatorapp
./gradlew clean
./gradlew build
```

---

## 📱 What You'll Get

Once setup is complete, your app will have:

- ✅ **AI-powered forex assistant**
- ✅ **Currency pair analysis**
- ✅ **Market insights**
- ✅ **Forex trading tips**
- ✅ **Natural language chat**

---

## 🚀 Next Steps After Setup

Once everything works:

1. ✅ Test different questions
2. ✅ Analyze currency pairs
3. ✅ Get forex tips
4. ✅ Customize the prompts in `GeminiService.kt`

---

## 📚 Additional Resources

- Firebase Console: https://console.firebase.google.com/
- Firebase Docs: https://firebase.google.com/docs/vertex-ai/get-started?platform=android
- Vertex AI Pricing: https://firebase.google.com/docs/vertex-ai/pricing

---

## ⚠️ IMPORTANT

**Without `google-services.json`, the app WILL NOT work!**

The 404 error you experienced was because:

1. Firebase needs the configuration file to know which project to connect to
2. Without it, the SDK doesn't know where to send requests
3. This causes 404 errors or "app not initialized" errors

**Once you add `google-services.json` and enable Vertex AI, everything will work perfectly!** 🎉
