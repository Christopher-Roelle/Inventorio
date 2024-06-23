# Inventorio Enhancement - Enhancement of CS-360-R4871
Capstone enhancement of my inventory management app 'Inventorio'

## Table of Contents
- [About](#about)
- [Enhancements](#enhancements)
- [Prerequisites](#prerequisites)
- [Building](#building)
- [Usage](#usage)
- [Known Bugs](#known-bugs)

## About
This app was written as a means to learn Android App development. The app is designed to manage Inventory for a store.
The user can add products to the database, then adjust the metadata at any point, including changing the product quantity.
The app also supports text notifications for low stock (fires every 5 minutes), and is based on the user's low-stock threshold.

## Enhancements
- Resolved a bug with the modal where it would close if new or updated product information didnt pass validation.
- Added new metadata fields for Brand and Pricing.
- Added DB support for the new fields for the products.
- Added helper methods for truncating columns when in Portrait mode.
- Updated the table layout to have better support for longer names by removing vowels and unecessary whitespace when in Portrait mode.
- Brands are now prepended to the product name. i.e. Gil-Razors would be Gillette Razors.
- Landscape mode now supports full column lengths, allowing for no truncation.
- Adjusted table colors for Dark-mode devices.

## Prerequisites
- Android Studio
- Android SDK 28 or greater (SDK 34 recommended)
- Git/Github for Desktop (Or Site Clone)

## Building
A pre-compiled APK is available on the Github page.
If you want to compile yourself, please follow the below steps:

### Build Steps
1. Clone this repository
    ```bash
    git clone https://github.com/Christopher-Roelle/Inventorio.git
    ```
2. Open the project in Android Studio
3. Wait for Gradle to build
4. In the Menu bar at the top of the screen (may need to click the hamburger menu), select Build -> Select Build Variant...
5. Select Build Variant from sidebar (Debug or Release)
6. In the Menu bar at the top of the screen (may need to click the hamburger menu), select Build -> Build Bundle(s) / APK(s) -> Build APK(s)
7. Wait for Gradle to Build
8. Navigate to the project folder, then to ..\app\build\outputs\apk\
9. Open the folder corresponding to Build Variant (Debug or Release)
10. Side-load app on to Device or Emulator

## Usage
Side load the app on to a Device or Emulator.
In some cases, this requires Developer Mode to enable.
Follow this to do so: https://developer.android.com/studio/run/device

Copy the APK to the device, then use an explorer utility to find and open it on the device to install it.

## Known Bugs
- Backing out from the Inventory Screen to the Login Screen causes "logging in..." message to still display