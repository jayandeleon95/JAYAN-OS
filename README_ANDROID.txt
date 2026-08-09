JAYAN OS ANDROID LAUNCHER WRAPPER V0.6

WHAT WORKS IN THIS SCAFFOLD
- Registers Jayan OS as an Android HOME/launcher activity.
- Loads the Jayan OS web UI locally in WebView.
- Native JavaScript bridge can enumerate launchable installed apps.
- Jayan OS can open installed apps by package name.
- Native battery percentage is exposed to the web UI.
- Android app queries are scoped to launchable activities.

WHAT STILL REQUIRES ANDROID STUDIO / APK BUILD
- Compile/sign/install the APK.
- Choose Jayan OS as the default Home app.
- App icons from PackageManager are not yet transferred into the WebView.
- Android widgets, notification listener, wallpaper APIs, and deeper system toggles require additional native integrations and permissions.

SECURITY
The bridge only exposes:
1) installed launchable app labels/package names,
2) opening an installed app,
3) battery percentage.
No contacts, messages, photos, microphone, location, passwords, or files are exposed by this wrapper.
