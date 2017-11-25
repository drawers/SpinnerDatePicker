package com.tsongkha.spinnerdatepicker;/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;

/**
 * This is a fork of the standard Android DatePicker that additionally allows toggling the year
 * on/off.
 *
 * A view for selecting a month / year / day based on a calendar like layout.
 *
 * <p>See the <a href="{@docRoot}resources/tutorials/views/hello-datepicker.html">Date Picker
 * tutorial</a>.</p>
 *
 * For a dialog using this view, see {@link android.app.DatePickerDialog}.
 */
public class DatePicker extends FrameLayout {
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final TwoDigitFormatter sTwoDigitFormatter = new TwoDigitFormatter();
    /** Magic year that represents "no year" */
    public static int NO_YEAR = 0;
    /* UI Components */
    private final LinearLayout mPickerContainer;
    private final NumberPicker mDayPicker;
    private final NumberPicker mMonthPicker;
    private final NumberPicker mYearPicker;

    /**
     * How we notify users the date has changed.
     */
    private OnDateChangedListener mOnDateChangedListener;

    private boolean mYearOptional;
    private boolean mHasYear;
    private Calendar mMinDate;
    private Calendar mMaxDate;
    private Calendar mTempDate;
    private Calendar mCurrentDate;

    private String[] mShortMonths;

    public DatePicker(Context context, ViewGroup root, int numberPickerStyle) {
        super(context, null, 0);

        LayoutInflater inflater = (LayoutInflater) new ContextThemeWrapper(context, numberPickerStyle).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.date_picker_container, this, true);

        mPickerContainer = findViewById(R.id.parent);

        mDayPicker = inflater.inflate(R.layout.number_picker_day_month, mPickerContainer, true).findViewById(R.id.number_picker);
        mDayPicker.setId(R.id.day);
        mDayPicker.setFormatter(sTwoDigitFormatter);
        mDayPicker.setOnLongPressUpdateInterval(100);
        mDayPicker.setOnValueChangedListener(new OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
                int maxDayOfMonth = mTempDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (oldVal == maxDayOfMonth && newVal == 1) {
                    mTempDate.add(Calendar.DAY_OF_MONTH, 1);
                } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                    mTempDate.add(Calendar.DAY_OF_MONTH, -1);
                } else {
                    mTempDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
                }
                updateDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH), mTempDate.get(Calendar.DAY_OF_MONTH));
            }
        });
        mMonthPicker = inflater.inflate(R.layout.number_picker_day_month, mPickerContainer).findViewById(R.id.number_picker);
        mMonthPicker.setId(R.id.month);
        mMonthPicker.setFormatter(sTwoDigitFormatter);
        DateFormatSymbols dfs = new DateFormatSymbols();
        mShortMonths = dfs.getShortMonths();

        mMonthPicker.setMinValue(1);
        mMonthPicker.setMaxValue(12);

        mMonthPicker.setDisplayedValues(mShortMonths);
        mMonthPicker.setOnLongPressUpdateInterval(200);
        mMonthPicker.setOnValueChangedListener(new OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
                if (oldVal == 11 && newVal == 0) {
                    mTempDate.add(Calendar.MONTH, 1);
                } else if (oldVal == 0 && newVal == 11) {
                    mTempDate.add(Calendar.MONTH, -1);
                } else {
                    mTempDate.add(Calendar.MONTH, newVal - oldVal);
                }
                updateDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH), mTempDate.get(Calendar.DAY_OF_MONTH));
                updateDaySpinner();
            }
        });
        mYearPicker = inflater.inflate(R.layout.number_picker_year, mPickerContainer).findViewById(R.id.number_picker);
        mYearPicker.setId(R.id.year);
        mYearPicker.setOnLongPressUpdateInterval(100);
        mYearPicker.setOnValueChangedListener(new OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
                mTempDate.set(Calendar.YEAR, newVal);
                // now set the date to the adjusted one
                updateDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH), mTempDate.get(Calendar.DAY_OF_MONTH));
                updateDaySpinner();
            }
        });
        mYearPicker.setMinValue(DEFAULT_START_YEAR);
        mYearPicker.setMaxValue(DEFAULT_END_YEAR);

        // set the min date giving priority of the minDate over startYear
        mTempDate = Calendar.getInstance();
        mMaxDate = Calendar.getInstance();
        mMinDate = Calendar.getInstance();
        mCurrentDate = Calendar.getInstance();
        mTempDate.set(DEFAULT_START_YEAR, 0, 1);

        setMinDate(mTempDate.getTimeInMillis());

        // set the max date giving priority of the maxDate over endYear
        mTempDate.clear();
        mTempDate.set(DEFAULT_END_YEAR, 11, 31);

        setMaxDate(mTempDate.getTimeInMillis());
        // initialize to current date
        Calendar cal = Calendar.getInstance();
        init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), false, null);

        updateSpinners();

        mPickerContainer.setLayoutTransition(new LayoutTransition());
        if (!isEnabled()) {
            setEnabled(false);
        }

        root.addView(this);
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mDayPicker.setEnabled(enabled);
        mMonthPicker.setEnabled(enabled);
        mYearPicker.setEnabled(enabled);
    }

    public Calendar getMinDate() {
        final Calendar minDate = Calendar.getInstance();
        minDate.setTimeInMillis(mMinDate.getTimeInMillis());
        return minDate;
    }

    public void setMinDate(long minDate) {
        mTempDate.setTimeInMillis(minDate);
        if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR) && mTempDate.get(Calendar.DAY_OF_YEAR) == mMinDate.get(Calendar.DAY_OF_YEAR)) {
            // Same day, no-op.
            return;
        }
        mMinDate.setTimeInMillis(minDate);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        }
        updateSpinners();
    }

    public Calendar getMaxDate() {
        final Calendar maxDate = Calendar.getInstance();
        maxDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        return maxDate;
    }

    public void setMaxDate(long maxDate) {
        mTempDate.setTimeInMillis(maxDate);
        if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR) && mTempDate.get(Calendar.DAY_OF_YEAR) == mMaxDate.get(Calendar.DAY_OF_YEAR)) {
            // Same day, no-op.
            return;
        }
        mMaxDate.setTimeInMillis(maxDate);
        if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
        updateSpinners();
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        if (mCurrentDate.get(Calendar.YEAR) != year
                || mCurrentDate.get(Calendar.MONTH) != monthOfYear
                || mCurrentDate.get(Calendar.DAY_OF_MONTH) != dayOfMonth) {
            mCurrentDate.set((mYearOptional && year == NO_YEAR) ? getCurrentYear() : year, monthOfYear, dayOfMonth);
            if (mCurrentDate.before(mMinDate)) {
                mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
            } else if (mCurrentDate.after(mMaxDate)) {
                mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
            }
            updateSpinners();
            notifyDateChanged();
        }
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Override so we are in complete control of save / restore for this widget.
     */
    @Override protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        return new SavedState(superState, mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH), mCurrentDate.get(Calendar.DAY_OF_MONTH), mHasYear,
                mYearOptional);
    }

    @Override protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mCurrentDate = Calendar.getInstance();
        mCurrentDate.set(ss.getYear(), ss.getMonth(), ss.getDay());
        mHasYear = ss.hasYear();
        mYearOptional = ss.isYearOptional();
        updateSpinners();
    }

    /**
     * Initialize the state.
     *
     * @param year The initial year or {@link #NO_YEAR} if no year has been specified
     * @param monthOfYear The initial month.
     * @param dayOfMonth The initial day of the month.
     * @param yearOptional True if the user can toggle the year
     * @param onDateChangedListener How user is notified date is changed by user, can be null.
     */
    public void init(int year, int monthOfYear, int dayOfMonth, boolean yearOptional, OnDateChangedListener onDateChangedListener) {
        mCurrentDate = Calendar.getInstance();
        mCurrentDate.set((yearOptional && year == NO_YEAR) ? getCurrentYear() : year, monthOfYear, dayOfMonth);
        mYearOptional = yearOptional;
        mHasYear = yearOptional ? (year != NO_YEAR) : true;
        mOnDateChangedListener = onDateChangedListener;
        updateSpinners();
    }

    private void updateSpinners() {
        // set the spinner ranges respecting the min and max dates
        if (mCurrentDate.equals(mMinDate)) {
            mDayPicker.setMinValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
            mDayPicker.setMaxValue(mCurrentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            mDayPicker.setWrapSelectorWheel(false);
            mMonthPicker.setDisplayedValues(null);
            mMonthPicker.setMinValue(mCurrentDate.get(Calendar.MONTH));
            mMonthPicker.setMaxValue(mCurrentDate.getActualMaximum(Calendar.MONTH));
            mMonthPicker.setWrapSelectorWheel(false);
        } else if (mCurrentDate.equals(mMaxDate)) {
            mDayPicker.setMinValue(mCurrentDate.getActualMinimum(Calendar.DAY_OF_MONTH));
            mDayPicker.setMaxValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
            mDayPicker.setWrapSelectorWheel(false);
            mMonthPicker.setDisplayedValues(null);
            mMonthPicker.setMinValue(mCurrentDate.getActualMinimum(Calendar.MONTH));
            mMonthPicker.setMaxValue(mCurrentDate.get(Calendar.MONTH));
            mMonthPicker.setWrapSelectorWheel(false);
        } else {
            mDayPicker.setMinValue(1);
            mDayPicker.setMaxValue(mCurrentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            mDayPicker.setWrapSelectorWheel(true);
            mMonthPicker.setDisplayedValues(null);
            mMonthPicker.setMinValue(0);
            mMonthPicker.setMaxValue(11);
            mMonthPicker.setWrapSelectorWheel(true);
        }

        // make sure the month names are a zero based array
        // with the months in the month Picker
        String[] displayedValues = Arrays.copyOfRange(mShortMonths, mMonthPicker.getMinValue(), mMonthPicker.getMaxValue() + 1);
        mMonthPicker.setDisplayedValues(displayedValues);

        // year Picker range does not change based on the current date
        if (isYearOptional()) mYearPicker.setVisibility(View.GONE);
        mYearPicker.setMinValue(mMinDate.get(Calendar.YEAR));
        mYearPicker.setMaxValue(mMaxDate.get(Calendar.YEAR));
        mYearPicker.setWrapSelectorWheel(false);

        // set the Picker values
        mYearPicker.setValue(mCurrentDate.get(Calendar.YEAR));
        mMonthPicker.setValue(mCurrentDate.get(Calendar.MONTH));
        mDayPicker.setValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
    }

    private void updateDaySpinner() {
        Calendar cal = Calendar.getInstance();
        // if year was not set, use 2000 as it was a leap year
        cal.set(mHasYear ? mCurrentDate.get(Calendar.YEAR) : 2000, mCurrentDate.get(Calendar.MONTH), 1);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        mDayPicker.setMinValue(1);
        mDayPicker.setMaxValue(max);
        mDayPicker.setValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
    }

    public int getYear() {
        return (mYearOptional && !mHasYear) ? NO_YEAR : mCurrentDate.get(Calendar.YEAR);
    }

    public boolean isYearOptional() {
        return mYearOptional;
    }

    public int getMonth() {
        return mCurrentDate.get(Calendar.MONTH);
    }

    public int getDayOfMonth() {
        return mCurrentDate.get(Calendar.DAY_OF_MONTH);
    }

    private void adjustMaxDay() {
        Calendar cal = Calendar.getInstance();
        // if year was not set, use 2000 as it was a leap year
        cal.set(Calendar.YEAR, mHasYear ? mCurrentDate.get(Calendar.YEAR) : 2000);
        cal.set(Calendar.MONTH, mCurrentDate.get(Calendar.MONTH));
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (mCurrentDate.get(Calendar.DAY_OF_MONTH) > max) {
            mCurrentDate.set(Calendar.DAY_OF_MONTH, max);
        }
    }

    private void notifyDateChanged() {
        if (mOnDateChangedListener != null) {
            int year = (mYearOptional && !mHasYear) ? NO_YEAR : mCurrentDate.get(Calendar.YEAR);
            mOnDateChangedListener.onDateChanged(DatePicker.this, year, mCurrentDate.get(Calendar.MONTH), mCurrentDate.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * The callback used to indicate the user changes the date.
     */
    public interface OnDateChangedListener {

        /**
         * @param view The view associated with this listener.
         * @param year The year that was set or {@link DatePicker#NO_YEAR} if no year was set
         * @param monthOfYear The month that was set (0-11) for compatibility
         * with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    private static class SavedState extends BaseSavedState {

        @SuppressWarnings("unused") public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private final int mYear;
        private final int mMonth;
        private final int mDay;
        private final boolean mHasYear;
        private final boolean mYearOptional;

        /**
         * Constructor called from {@link DatePicker#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, int year, int month, int day, boolean hasYear, boolean yearOptional) {
            super(superState);
            mYear = year;
            mMonth = month;
            mDay = day;
            mHasYear = hasYear;
            mYearOptional = yearOptional;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mYear = in.readInt();
            mMonth = in.readInt();
            mDay = in.readInt();
            mHasYear = in.readInt() != 0;
            mYearOptional = in.readInt() != 0;
        }

        public int getYear() {
            return mYear;
        }

        public int getMonth() {
            return mMonth;
        }

        public int getDay() {
            return mDay;
        }

        public boolean hasYear() {
            return mHasYear;
        }

        public boolean isYearOptional() {
            return mYearOptional;
        }

        @Override public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mYear);
            dest.writeInt(mMonth);
            dest.writeInt(mDay);
            dest.writeInt(mHasYear ? 1 : 0);
            dest.writeInt(mYearOptional ? 1 : 0);
        }
    }
}