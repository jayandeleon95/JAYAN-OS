# Build Jayan OS 1.6 APK

1. Install Android Studio.
2. Open the `android-launcher-wrapper` folder as the project.
3. Let Android Studio install/sync Android SDK 35 and Gradle dependencies.
4. If Android Studio asks for a Gradle JDK, select JDK 17.
5. Run the `app` configuration on your Android phone with USB debugging enabled.
6. Android should offer Jayan OS as a Home app because the manifest declares HOME + DEFAULT.
7. Grant microphone only if using voice commands.
8. Grant Notification Access from Android Settings for notification center/badges.
9. For a distributable APK use Build > Generate Signed App Bundle or APK > APK.

Important: this source package does not contain a fabricated/prebuilt APK. It is prepared for a real Android build and signing process.
