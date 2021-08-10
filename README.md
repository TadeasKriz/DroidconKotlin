# Sessionize/Droidcon Mobile Clients

[![Build Status](https://dev.azure.com/touchlabApps/DroidconApp/_apis/build/status/touchlab.DroidconKotlin?branchName=master)](https://dev.azure.com/touchlabApps/DroidconApp/_build/latest?definitionId=1&branchName=master)

## General Info

This project has a pair of native mobile applications backed by the Sessionize data api for use in
events hosted by the Sessionize web application. These are specifically for Droidcon events, but can
be forked and customized for anything run on Sessionize.

> ## **We're Hiring!**
>
> Touchlab is looking for Android-focused mobile engineers, experienced with Kotlin and
> looking to get involved with Kotlin Multiplatorm in the near future. [More info here](https://on.touchlab.co/2KNeYYN).

## Libraries

Kotlin multiplatform libraries used:

* [SQLDelight](https://github.com/square/sqldelight) - SQL model generator from Square and
[AlecStrong](https://github.com/AlecStrong).

* [SQLiter](https://github.com/touchlab/SQLiter) - Lightly opinionated sqlite access driver. Powering
the sqldelight native driver.

* [multiplatform-settings](https://github.com/russhwolf/multiplatform-settings) - Shared settings for Android and iOS from
[russhwolf](https://github.com/russhwolf).

* [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization/)

* [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)

* [Stately](https://github.com/touchlab/Stately/) - Multiplatform threading state library.

## Media

[Medium - Droidcon NYC App!](https://medium.com/@kpgalligan/droidcon-nyc-app-da868bdef387)

[More Media ->](MEDIA.md)

## Building

## IntelliJ or Android Studio

You can use any recent version of Android Studio 2020.3.1+. You should be able to open the
project folder directly, or import the project as a gradle project.

## Xcode

The iOS project is in the "ios/Droidcon" folder. Follow [iOS-specific instructions](./IOSDEV.md) to get it working.

### Xcode Sync

There is an experimental plugin called Xcode Sync. It imports Kotlin files into the Xcode project.
You can safely ignore that for now, but if you'd like to have new Kotlin files available in Xcode,
run the task added by the plugin.

## Customizing

General instructions for [customizing](CUSTOMIZING.md) the app. This app is backed by Sessionize and with some tweaks would
be generally useful for any Sessionize event.

## Contributing

Check out the issues section to see what we're looking for. We will be adding a number of new features for
Droidcon NYC 2019, as well as keeping up with the latest additions to the Kotlin Multiplatform ecosystem.

## About

Sessionize/Droidcon brought to you by...

[![Touchlab Logo](tlsmall.png "Touchlab Logo")](https://touchlab.co)
