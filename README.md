#Spinner DatePicker

The default Material Design DatePicker has poor usability for choosing a date of birth. It seems it is hard for users to find the "year" button and they will often simply swipe left or right through the months in order to find their date of birth. The previous Holo DatePicker with sliding NumberPickers is much more suitable for this use case however it is no longer available for Marshmallow devices and up. 

This type of DatePicker is still available in the standard open source Contacts app where it is used as a DatePicker for the birthday of a contact. You can see the result with the following procedure:

1. Open the Contacts app
2. Click on a Contact to see more detail
3. Click on the floating action bar for edit
4. Scroll down and click on "More fields"
5. Scroll down and click on "Date"

This library is heavily based on this open source Contacts app DatePicker with the addition of being able to style the NumberPickers (the dials/spinners in the DatePicker). 

You can style the DatePicker easily with a style:

    <style name="NumbePrickerStyle">
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
