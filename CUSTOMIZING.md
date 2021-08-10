# Customizing The App

If you're using **Sessionize** for your event, you can use the app pretty easily. Customization parameters are used throughout the app to make this relatively easy. You can find and tweak them in `shared/src/commonMain/kotlin/co/touchlab/droidcon/Constants.kt`.

Primarily you'll need to point to your data urls, change the data seed files for speakers/sessions/sponsors, and
change the color settings.

## Time Zone
The time zone of the conference is set in `shared/src/commonMain/kotlin/co/touchlab/droidcon/Constants.kt` and is shared between platforms. To see a non-exhaustive list of supported time zones, check out [TimeZones.txt](./TimeZones.txt).

## UI
UI is separate for iOS and Android, so the colors need to be changed separately to match the theme you're going for.

### Android
The **theme** is located in `android/src/main/java/co/touchlab/droidcon/android/ui/theme/Theme.kt` and **colors** in the same folder, file `Colors.kt`.

### iOS
After opening `ios/Droidcon/Droidcon.xcworkspace` in Xcode, `Assets.xcassets` contains all the **icons** and **colors** the app uses.

## Attribution
It would be super great if you could keep us in the about section of your app. We're a consulting company that turns
project revenue into open source stuff, so we need eyeballs. Thanks XOXO. Speaking of...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
