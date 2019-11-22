package com.tsongkha.spinnerdatepickerexample;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    public static final int SCROLL_UP = 40;
    public static final int SCROLL_DOWN = -40;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(
            MainActivity.class, true, false);

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
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("January 1, 1980")));
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
        onView(withId(R.id.day)).perform(NumberPickers.scroll(SCROLL_DOWN)).check(
                NumberPickers.isDisplayed("2"));
        onView(withId(R.id.day)).perform(NumberPickers.scroll(SCROLL_UP)).check(
                NumberPickers.isDisplayed("1"));
        onView(withId(R.id.day)).perform(NumberPickers.scroll(SCROLL_UP)).check(
                NumberPickers.isDisplayed("31"));
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("December 31, 1979")));
    }

    @Test
    public void testDaySpinnerNotShown() throws Exception {
        //act
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SpinnerDatePickerDialogBuilder()
                        .context(mainActivity)
                        .showDaySpinner(false)
                        .spinnerTheme(R.style.DatePickerSpinner)
                        .build()
                        .show();
            }
        });

        //assert
        onView(withId(R.id.day)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.month)).perform(NumberPickers.scroll(SCROLL_UP)).check(
                NumberPickers.isDisplayed("Dec"));
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
        onView(withId(R.id.month)).perform(NumberPickers.scroll(SCROLL_DOWN)).check(
                NumberPickers.isDisplayed("Feb"));
        onView(withId(R.id.month)).perform(NumberPickers.scroll(SCROLL_UP)).check(
                NumberPickers.isDisplayed("Jan"));
        onView(withId(R.id.month)).perform(NumberPickers.scroll(SCROLL_UP)).check(
                NumberPickers.isDisplayed("Dec"));
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("December 1, 1979")));
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
        onView(withId(R.id.year)).perform(NumberPickers.scroll(SCROLL_UP)).check(
                NumberPickers.isDisplayed("1979"));
        onView(withId(R.id.year)).perform(NumberPickers.scroll(SCROLL_DOWN)).check(
                NumberPickers.isDisplayed("1980"));
        onView(withId(R.id.year)).perform(NumberPickers.scroll(SCROLL_DOWN)).check(
                NumberPickers.isDisplayed("1981"));
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("January 1, 1981")));
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
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("December 31, 1970")));
    }

    @Test
    public void testDaysInMonthDecreaseViaMonthChange() throws Exception {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showDate(1980, 0, 31, R.style.DatePickerSpinner);
            }
        });

        onView(withId(com.tsongkha.spinnerdatepicker.R.id.month))
                .perform(NumberPickers.scroll(SCROLL_DOWN));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.day))
                .check(NumberPickers.isDisplayed("29"));
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("February 29, 1980")));
    }

    @Test
    public void testDaysInMonthDecreaseViaYearChange() throws Exception {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showDate(1980, 1, 29, R.style.DatePickerSpinner);
            }
        });

        onView(withId(com.tsongkha.spinnerdatepicker.R.id.year))
                .perform(NumberPickers.scroll(SCROLL_DOWN));
        onView(withId(com.tsongkha.spinnerdatepicker.R.id.day))
                .check(NumberPickers.isDisplayed("1"));
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("March 1, 1981")));
    }

    @Test
    public void testTitleNotShown() throws Exception {
        //act
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SpinnerDatePickerDialogBuilder()
                        .context(mainActivity)
                        .showTitle(false)
                        .spinnerTheme(R.style.DatePickerSpinner)
                        .build()
                        .show();
            }
        });

        //assert
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText(" ")));
    }

    @Test
    public void testIsCustomTitleShown() throws Exception {
        //act
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SpinnerDatePickerDialogBuilder()
                        .context(mainActivity)
                        .showTitle(true)
                        .customTitle("My custom title")
                        .spinnerTheme(R.style.DatePickerSpinner)
                        .build()
                        .show();
            }
        });

        //assert
        onView(withClassName(containsString("DialogTitle"))).check(
                matches(withText("My custom title")));
    }
}