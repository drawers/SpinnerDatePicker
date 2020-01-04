[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=18) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SpinnerDatePicker-red.svg?style=plastic)](https://android-arsenal.com/details/1/6319) [![Release](https://jitpack.io/v/drawers/SpinnerDatePicker.svg)](https://jitpack.io/#drawers/SpinnerDatePicker)

~~Spinner DatePicker~~
-----

# Deprecation
 
This repo is no longer being maintained due to time constraints. #sorrynotsorry

If you wish to transfer ownership then please contact the author via issues.

## Summary

The old "spinner" style DatePicker for newer devices.

![ScreenShot](https://i.imgur.com/TMiivVq.png)

## Motivation

The default Material Design DatePicker has poor usability for choosing a date of birth. It seems it is hard for users to find the "year" button and they will often simply swipe left or right through the months in order to find their date of birth. 

![MaterialDesign](https://i.imgur.com/8lmZhbd.png?1)

The previous Holo DatePicker with sliding NumberPickers is much more suitable for this use case however it is no longer available for Marshmallow devices and up. 

This library is heavily based on the latest [Android Open Source Project](https://source.android.com/) DatePicker (source code [here](http://androidxref.com/8.0.0_r4/xref/frameworks/base/core/java/android/widget/DatePickerSpinnerDelegate.java)) with the addition of being able to style the NumberPickers (the dials/spinners in the DatePicker). 

## Adding styles

The DatePicker is the simple aggregation of three NumberPickers. You can style the DatePicker easily with a NumberPicker style (in styles.xml in the values folder):

    <style name="NumberPickerStyle">
        <item name="android:textSize">22dp</item>
        <item name="android:textColorPrimary">@color/colorAccent</item>
        <item name="android:colorControlNormal" tools:targetApi="lollipop">@color/colorAccent</item>
    </style>

where `colorControlNormal` is the color of the horizontal bars (dividers) in the `NumberPicker`. See [this StackOverflow question](https://stackoverflow.com/q/20148671/5241933)

And then:

        new SpinnerDatePickerDialogBuilder()
                .context(MainActivity.this)
                .callback(MainActivity.this)
                .spinnerTheme(R.style.NumberPickerStyle)
                .showTitle(true)
                .customTitle("My custom title")
                .showDaySpinner(true)
                .defaultDate(2017, 0, 1)
                .maxDate(2020, 0, 1)
                .minDate(2000, 0, 1)
                .build()
                .show();

The example project should make it clear - get it by cloning the repo.                    

Note that full support is only for API >= 18. API < 18 you'll get the DatePicker but there is no easy way to style it correctly.                     
                    
## Usage in a project

Add the following to your **project** level `build.gradle`:
    
   ```gradle
   allprojects {
       repositories {
           maven { url "https://jitpack.io" }
   	   }
   }
   ```

Add this to your **app level** `build.gradle`:
    
   ```gradle
   dependencies {
       compile 'com.github.drawers:SpinnerDatePicker:2.0.1'
   }
   ```             

Philosophy
==========

The aim of this project is to produce a lightweight and robust DatePicker with an API similar to that of the standard Android DatePicker. Hence the library has no external dependencies and no fancy features. Espresso automated UI testing is performed on the sample project using Firebase test lab.

Contributing
============

Please open an issue first before making a pull request. Pull requests should be accompanied by tests if possible.

License
=======

Copyright 2017 AOSP, David Rawson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
