# Bixlight

[![Build Status](https://travis-ci.org/Shingyx/Bixlight.svg?branch=master)](https://travis-ci.org/Shingyx/Bixlight)

**Note:** After the AQG5 update in late July, Bixlight no longer works as Samsung have stopped window state changes for Bixby from being sent to Accessibility Services. I don't plan to fix this in the near future unless I find an approach that does not involve polling.

This application allows you to remap the Bixby button on the Galaxy S8/S8+ to toggle the torch.

Download the APK from the releases tab (link for the lazy: https://github.com/Shingyx/Bixlight/releases)


## How does it work?

You can probably figure it out by looking at the code - there's not much of it. It runs an accessibility service in the background which listens for window changes until the active window is from the package `com.samsung.android.app.spage`. If this condition is met, it simulates pressing the back button and toggles the torch's state. This means that Bixby will probably flash open before closing again, but this is currently the best approach to remap the button on an unrooted phone without hindering performance, especially after Samsung removed the ability to see the button's key event.

In my testing, Bixlight is the best compromise in terms of how fast Bixby closes and how it affects the system's overall performance when compared to other Bixby button remappers on the Play Store (I only tested those with no ads). However, Bixlight's only feature is turning the torch on and off, because that's all I wanted on my personal phone.


## Why "Bixlight"?

Combination of Bixby and light... ¯\\\_(ツ)_/¯


## Why are you releasing it on GitHub instead of the Play Store?

Because I'm a strong believer of open source software and it lets me reassure users that I'm not violating anyone's privacy by collecting personal information. This thing doesn't even ask for internet access!

Actually, it's just because I'm too cheap to pay the registration fee for the Play Store.

See my other project to find out how I set up Travis to create signed APKs as GitHub releases: https://github.com/Shingyx/travis-signed-apk-release-example


## Thanks
Thanks to [Dantee296](https://github.com/Dantee296) for providing the fix for the AQG5 update.
