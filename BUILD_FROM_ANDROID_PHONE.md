# Jayan OS — Build the APK using only your Android phone

## One-time setup
1. Create/sign in to a GitHub account in your phone browser.
2. Create a new PRIVATE repository, for example `jayan-os`.
3. Upload the CONTENTS of this `android-launcher-wrapper` folder to the repository.
   Important: `.github/workflows/build-apk.yml` must exist in the repository.
4. Open the repository's Actions tab.
5. Open `Build Jayan OS APK`.
6. Tap `Run workflow` and run it.

## Get the APK
When the build finishes successfully:
1. Open the completed workflow run.
2. Under Artifacts, download `Jayan-OS-APK`.
3. Extract the downloaded artifact ZIP.
4. Install `app-debug.apk` on your Android phone.
5. Android may ask you to allow installation from your browser/files app.
6. Press Home and select Jayan OS when Android offers Home-app choices.

## Permissions after installation
- Enable Notification Access if you want native notification badges/center.
- Allow microphone if you want voice commands.
- Camera permission may be requested for flashlight/camera-related functions.

## Updating later
Replace/upload the changed project files to the same repository. A push to `main`
that changes app/build files automatically starts a new APK build. You can also
run the workflow manually from Actions.

The workflow builds a DEBUG APK. This is appropriate for your own testing.
A production/release APK should use a private signing key that must never be
committed to the repository.
