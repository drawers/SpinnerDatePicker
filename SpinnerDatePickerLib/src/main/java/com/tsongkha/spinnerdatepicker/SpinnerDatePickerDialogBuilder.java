package com.tsongkha.spinnerdatepicker;

import android.content.Context;
import android.widget.Spinner;

import java.util.Date;
import java.util.GregorianCalendar;

public class SpinnerDatePickerDialogBuilder {

    private Context context;
    private DatePickerDialog.OnDateSetListener callBack;
    private int year = 1980;
    private int monthOfYear = 0;            //months are indexed from 0
    private int dayOfMonth = 1;
    private boolean yearOptional = false;
    private int theme = -1;                 //default theme
    private int spinnerTheme = -1;          //default theme
    private Date minDate = new GregorianCalendar(1900, 0, 1).getTime();
    private Date maxDate = new GregorianCalendar(2100, 0, 1).getTime();

    public SpinnerDatePickerDialogBuilder context(Context context) {
        this.context = context;
        return this;
    }

    public SpinnerDatePickerDialogBuilder callback(DatePickerDialog.OnDateSetListener callBack) {
        this.callBack = callBack;
        return this;
    }

    public SpinnerDatePickerDialogBuilder year(int year) {
        this.year = year;
        return this;
    }

    public SpinnerDatePickerDialogBuilder monthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
        return this;
    }

    public SpinnerDatePickerDialogBuilder dayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
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
        throw new UnsupportedOperationException("Not implemented");
    }

    public SpinnerDatePickerDialogBuilder minDate(int year, int monthIndexedFromZero, int day) {
        this.minDate = new GregorianCalendar(year, monthIndexedFromZero, day).getTime();
        return this;
    }

    public SpinnerDatePickerDialogBuilder maxDate(int year, int monthIndexedFromZero, int day) {
        this.maxDate = new GregorianCalendar(year, monthIndexedFromZero, day).getTime();
        return this;
    }

    public DatePickerDialog build() {
        if (context == null) throw new IllegalArgumentException("Context must not be null");
        if (maxDate.getTime() <= minDate.getTime()) throw new IllegalArgumentException("Max date is not after Min date");

        return new DatePickerDialog(context, theme, spinnerTheme, callBack, year, monthOfYear, dayOfMonth, minDate, maxDate);
    }
}