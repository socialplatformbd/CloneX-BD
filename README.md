# CloneX BD

**Clone Smarter. Manage Better.**
Made by Mohammad Rakib

A phone-first Android project scaffold using Kotlin, Jetpack Compose and GitHub Actions.

## Included in this starter
- Responsive Compose layout: compact bottom navigation and scrollable content; adapts to narrow and wide screens.
- Installed launcher-app discovery using Android package visibility declarations.
- English/Bangla language switch (saved locally).
- Light/Dark theme switch (saved locally).
- Clone manager placeholder with honest capability messaging.
- GitHub Actions workflows for debug APK and signed release APK.

## Important capability boundary
CloneX BD Safe Edition provides app discovery, selection, theme and language settings. It does not create independent clones of other apps or access their private data. It works as a regular Android app without Device Admin or Profile Owner privileges.

## Build locally
Requires JDK 17, Android SDK Platform 35 and Gradle 8.11.1:
```bash
gradle assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub setup
1. Create a new empty repository named `CloneX-BD`.
2. Upload/push the contents of this folder to the repository root.
3. Open **Actions** and run **Build Debug APK** (or push to `main`).
4. Download the `CloneX-BD-debug-apk` artifact from the workflow run.

## Signed release APK
Create a JKS keystore locally and add these GitHub Actions repository secrets:
- `ANDROID_KEYSTORE_BASE64`: base64-encoded JKS file
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Never commit the keystore or passwords. Run **Build Signed Release APK** manually. If signing secrets are absent, the workflow fails rather than producing a falsely-labelled signed APK.

## Project identity
- Application ID: `com.clonexbd.app`
- Version: `1.0.0`
- Minimum Android: 8.0 (API 26)
- Languages: বাংলা / English
- Developer: Mohammad Rakib
- Telegram: https://t.me/appdevbyrakib
- WhatsApp: https://chat.whatsapp.com/HkvJCqWFf6G4bcQRKspKta?s=cl&p=a&iam=2

## Notes
The package list is limited to launchable apps visible under Android package-visibility rules. Android 11+ restricts package visibility; this project intentionally avoids the broad `QUERY_ALL_PACKAGES` permission.
