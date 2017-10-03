package com.tsongkha.spinnerdatepickerexample;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, false);

    MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = mainActivityActivityTestRule.launchActivity(null);
    }

    @Test
    public void testDefaultDatePickerDialogDisplays() {
        //act
        onView(withId(R.id.set_date_button)).perform(click());

        //assert
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.datePickerContainer))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.year))
                .check(NumberPickers.isDisplayed("1980"));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.month))
                .check(NumberPickers.isDisplayed("Jan"));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.day))
                .check(NumberPickers.isDisplayed("1"));
    }

    @Test
    public void testDaySpinner() throws Exception {
        //act
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showDate(1980, 0, 1, R.style.DatePickerSpinner);
            }
        });

        //assert
        onView(withId(R.id.day)).perform(NumberPickers.setNumber(10)).check(NumberPickers.isDisplayed("10"));
    }

    @Test
    public void testMonthSpinner() throws Exception {
        //act
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showDate(1980, 0, 1, R.style.DatePickerSpinner);
            }
        });

        //assert
        onView(withId(R.id.month)).perform(NumberPickers.setNumber(3)).check(NumberPickers.isDisplayed("Mar"));
    }

    @Test
    public void testYearSpinner() throws Exception {
        //act
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showDate(1980, 0, 1, R.style.DatePickerSpinner);
            }
        });

        //assert
        onView(withId(R.id.year)).perform(NumberPickers.setNumber(1970)).check(NumberPickers.isDisplayed("1970"));
    }

    @Test
    public void testCustomDate() throws Exception {
        //act
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showDate(1970, 11, 31, R.style.DatePickerSpinner);
            }
        });

        //assert
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.datePickerContainer))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.parent))
                .check(matches(isCompletelyDisplayed()));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.year))
                .check(NumberPickers.isDisplayed("1970"));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.month))
                .check(NumberPickers.isDisplayed("Dec"));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.day))
                .check(NumberPickers.isDisplayed("31"));
    }

    @Test
    public void testSetDate() throws Exception {
        //act
        onView(withId(R.id.set_date_button)).perform(click());
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.day))
                .perform(NumberPickers.setNumber(15));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.month))
                .perform(NumberPickers.setNumber(10));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.year))
                .perform(NumberPickers.setNumber(1960));
        onView(withText(android.R.string.ok)).perform(click());

        //assert
        onView(withId(R.id.date_textview)).check(matches(withText("15 10 1960")));
    }
}