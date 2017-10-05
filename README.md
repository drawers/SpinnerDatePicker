Spinner DatePicker
-----

## Motivation

The default Material Design DatePicker has poor usability for choosing a date of birth. It seems it is hard for users to find the "year" button and they will often simply swipe left or right through the months in order to find their date of birth. The previous Holo DatePicker with sliding NumberPickers is much more suitable for this use case however it is no longer available for Marshmallow devices and up. 

This type of DatePicker is still available in the standard open source Contacts app where it is used as a DatePicker for the birthday of a contact. You can see the result with the following procedure:

1. Open the Contacts app
2. Click on a Contact to see more detail
3. Click on the floating action bar for edit
4. Scroll down and click on "More fields"
5. Scroll down and click on "Date"

This library is heavily based on this open source Contacts app DatePicker with the addition of being able to style the NumberPickers (the dials/spinners in the DatePicker). 

You can style the DatePicker easily with a style:

    <style name="NumbePickerStyle">
        <item name="android:textSize">22dp</item>
        <item name="android:textColorPrimary">@color/colorAccent</item>
        <item name="android:colorControlNormal" tools:targetApi="lollipop">@color/colorAccent</item>
    </style>

And then:

        new SpinnerDatePickerDialogBuilder()
                .context(MainActivity.this)
                .callback(MainActivity.this)
                .spinnerTheme(spinnerTheme)
                .year(year)
                .monthOfYear(monthOfYear)
                .dayOfMonth(dayOfMonth)
                .build()
                .show();
                    
## Usage in a project

Add the following to your **project** level `build.gradle`:
    
   ```gradle
   allprojects {
   	repositories {
   		maven { url "https://jitpack.io" }
   	}
   }
   ```

Add this to your app `build.gradle`:
    
   ```gradle
   dependencies {
   	compile 'com.github.drawers:SpinnerDatePicker:0.0.4'
   }
   ```             
                
License
=======

Copyright 2017 David Rawson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
