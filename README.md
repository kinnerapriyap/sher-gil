# sher-gil
### Media picker library in kotlin for Android 🥳
[ ![Download](https://api.bintray.com/packages/kinnerapriyap/maven-android/sher-gil/images/download.svg) ](https://bintray.com/kinnerapriyap/maven-android/sher-gil/_latestVersion)
![kotlin](https://img.shields.io/badge/language-kotlin-orange)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![@kinnerapriyap](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Ftwitter.com%2Fkinnerapriyap)](https://twitter.com/kinnerapriyap)

Written in kotlin with Android architecture components, `sher-gil` aims to be a reliable library resource for media picking.

## Features

* Multiple media selection (currently only images)
* Custom MIME types
* Handles runtime permissions
* Returns media selection as `List<Uri>`
* Custom theme - *Coming soon*

## Download

### Gradle
Add the dependency to your project `build.gradle` file
```
repositories {
    jcenter()
}

dependencies {
    implementation 'com.kinnerapriyap:sher-gil:$latest_version'
}
```
Latest version: [ ![Download](https://api.bintray.com/packages/kinnerapriyap/maven-android/sher-gil/images/download.svg) ](https://bintray.com/kinnerapriyap/maven-android/sher-gil/_latestVersion)

#### Data binding should be enabled to use sher-gil

If your project uses a Android Gradle Plugin version **after 4.0.0-alpha05**:
```
android {
    ...
    buildFeatures {
        dataBinding = true
    }
}
```
If your project uses a Android Gradle Plugin version **before 4.0.0-alpha05**:
```
android {
    ...
    dataBinding {
        enabled = true
    }
}
```

## Usage

Sher-gil may be started from either an Activity or a Fragment.

```
Shergil.create(this)
    .mimeTypes(MimeType.IMAGES)
    .showDisallowedMimeTypes(false)
    .allowMultipleSelection(true)
    .numOfColumns(2)
    .theme(R.style.Shergil)
    .allowPreview(true)
    .withRequestCode(REQUEST_SHERGIL)
```

#### Get result

```
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data) 
    if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SHERGIL) {
        val mediaUris: List<Uri> = Shergil.getMediaUris(data)
    }
}
```

If you're on `API level > R`, you can use `registerForActivityResult(ActivityResultContract, ActivityResultCallback)` with the appropriate ActivityResultContract and handle the result in the callback.

**Min SDK:** sher-gil supports a minimum SDK of 18.

## Contributing
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code_of_conduct.md)

If you have any feedback or a suggestion for a better implementation, please don't hesitate to open an issue or a pull request. 
For major changes, please open an issue first to discuss what you would like to change.

## *aha*

#### Named after 
[Amrita Sher-Gil](https://artsandculture.google.com/entity/amrita-sher-gil/m09sphm?categoryId=artist&hl=en), a pre-eminent Hungarian-Indian early modernist painter who became known as the 'Indian Frida Kahlo'.

#### Inspired by 
The [Matisse](https://github.com/zhihu/Matisse) library
