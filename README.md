# Hide-Reels

A lightweight Android app that lets you browse Instagram through a WebView while hiding the Reels navigation link.

## Features

- **Reels navigation hiding** — Hides the Reels navigation link on Instagram.
- **Dynamic page support** — Uses a `MutationObserver` to reapply hiding when Instagram updates its page.
- **Persistent login session** — Uses WebView cookies to help maintain your Instagram session.
- **Custom back navigation** — Provides more predictable navigation between Instagram pages and the home feed.
- **Native Android app** — Built with Kotlin and Android's WebView.

## How It Works

Reels-Free loads Instagram in an Android WebView and injects a small JavaScript script that hides the Reels navigation link.

Since Instagram dynamically updates its interface, a `MutationObserver` watches for relevant page changes and hides matching links when they are added or recreated.

## Requirements

- Android 8.0 (API 26) or later
- Android Studio
- Internet connection
- An Instagram account for account-specific features

## Build and Run

1. Clone this repository:

   ```bash
   git clone https://github.com/Divyam2806/hide-reels.git
   ```

2. Open the project in Android Studio.
3. Allow Gradle to sync and download the required dependencies.
4. Connect an Android device or start an emulator.
5. Run the app from Android Studio.

You can also build an APK using **Build → Build APK(s)** in Android Studio.

## Limitations

- This app hides the Reels navigation link; it does **not** guarantee that all Reels content is blocked.
- Reels may still be accessible through direct links or other parts of Instagram.
- Instagram may change its website, which could require updates to the hiding script.
- Because this app relies on Instagram's website, functionality may vary with website updates.

## Privacy and Security

The app loads Instagram directly inside a WebView. Login credentials and session handling are managed through Instagram's website and the WebView environment.

The project is intended for personal use. Review the source code before building or installing it, and avoid sharing APKs from untrusted sources.

## Disclaimer

Reels-Free is an independent, unofficial project and is not affiliated with, endorsed by, or sponsored by Instagram or Meta. Instagram is a trademark of Meta Platforms, Inc.

## License

No license has been added yet. Unless a license is included, others generally do not have explicit permission to reuse, modify, or distribute this project's code.
