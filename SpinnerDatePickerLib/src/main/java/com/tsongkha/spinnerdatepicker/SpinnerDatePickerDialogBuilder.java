package com.tsongkha.spinnerdatepicker;

import android.content.Context;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class SpinnerDatePickerDialogBuilder {

    private Context context;
    private DatePickerDialog.OnDateSetListener callBack;
    private boolean isDayShown = true;
    private boolean isTitleShown = true;
    private int theme = -1;                 //default theme
    private int spinnerTheme = -1;          //default theme
    private Calendar defaultDate = new GregorianCalendar(1980, 0, 1);
    private Calendar minDate = new GregorianCalendar(1900, 0, 1);
    private Calendar maxDate = new GregorianCalendar(2100, 0, 1);
    private String mTitleCaption = "";


    public SpinnerDatePickerDialogBuilder context(Context context) {
        this.context = context;
        return this;
    }

    public SpinnerDatePickerDialogBuilder callback(DatePickerDialog.OnDateSetListener callBack) {
        this.callBack = callBack;
        return this;
    }

    public SpinnerDatePickerDialogBuilder dialogTheme(int theme) {
        this.theme = theme;
        return this;
    }

    public SpinnerDatePickerDialogBuilder spinnerTheme(int spinnerTheme) {
        this.spinnerTheme = spinnerTheme;
        return this;
    }

    public SpinnerDatePickerDialogBuilder defaultDate(int year, int monthIndexedFromZero, int day) {
        this.defaultDate = new GregorianCalendar(year, monthIndexedFromZero, day);
        return this;
    }

    public SpinnerDatePickerDialogBuilder minDate(int year, int monthIndexedFromZero, int day) {
        this.minDate = new GregorianCalendar(year, monthIndexedFromZero, day);
        return this;
    }

    public SpinnerDatePickerDialogBuilder maxDate(int year, int monthIndexedFromZero, int day) {
        this.maxDate = new GregorianCalendar(year, monthIndexedFromZero, day);
        return this;
    }

    public SpinnerDatePickerDialogBuilder showDaySpinner(boolean showDaySpinner) {
        this.isDayShown = showDaySpinner;
        return this;
    }

    public SpinnerDatePickerDialogBuilder showTitle(boolean showTitle) {
        this.isTitleShown = showTitle;
        return this;
    }

    public SpinnerDatePickerDialogBuilder setTitleCaption(String titleCaption) {
        this.mTitleCaption = titleCaption;
        return this;
    }

    public DatePickerDialog build() {
        if (context == null) throw new IllegalArgumentException("Context must not be null");
        if (maxDate.getTime().getTime() <= minDate.getTime().getTime()) throw new IllegalArgumentException("Max date is not after Min date");

        return new DatePickerDialog(context, theme, spinnerTheme, callBack, defaultDate, minDate, maxDate, isDayShown, isTitleShown, mTitleCaption);
    }
}