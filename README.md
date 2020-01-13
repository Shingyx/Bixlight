<p align="center"><img width="120" src="bixlight/src/main/ic_launcher-web.png"></p>

<h1 align="center">Bixlight <a href="https://travis-ci.com/Shingyx/Bixlight"><img src="https://travis-ci.com/Shingyx/Bixlight.svg?branch=master"></a></h1>

This application allows you to remap the Bixby button on the Galaxy S8/S8+ to toggle the torch.

Download the APK from the releases tab (link for the lazy: https://github.com/Shingyx/Bixlight/releases)


## How does it work?

You can probably figure it out by looking at the code - there's not much of it. It runs an accessibility service in the background which listens for window changes until the active window is from the package `com.samsung.android.app.spage` (now `com.samsung.android.bixby.agent` with the Android 9 update). If this condition is met, it simulates pressing the back button and toggles the torch's state. This means that Bixby will probably flash open before closing again, but this is currently the best approach to remap the button on an unrooted phone without hindering performance, especially after Samsung removed the ability to see the button's key event.

In my testing, Bixlight is the best compromise in terms of how fast Bixby closes and how it affects the system's overall performance when compared to other Bixby button remappers on the Play Store (I only tested those with no ads). However, Bixlight's only feature is turning the torch on and off, because that's all I wanted on my personal phone.


## Why "Bixlight"?

Combination of Bixby and light... ¯\\\_(ツ)_/¯


## Thanks
* Thanks to [Dantee296](https://github.com/Dantee296) for providing the fix for the AQG5 update.
* Thanks to [jcrisp](https://github.com/jcrisp) for fixing the flickering torch issue when the phone is under load and for raising an issue for Android 9 incompatibility.
