package com.tsongkha.spinnerdatepicker;

import android.widget.NumberPicker;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Copy of {android.widget.NumberPicker.TwoDigitFormatter}, modified
 * so that it doesn't use libcore.
 *
 * Use a custom NumberPicker formatting callback to use two-digit minutes
 * strings like "01". Keeping a static formatter etc. is the most efficient
 * way to do this; it avoids creating temporary objects on every call to
 * format().
 */
public class TwoDigitFormatter implements NumberPicker.Formatter {
    final StringBuilder mBuilder = new StringBuilder();

    char mZeroDigit;
    java.util.Formatter mFmt;

    final Object[] mArgs = new Object[1];

    public TwoDigitFormatter() {
        final Locale locale = Locale.getDefault();
        init(locale);
    }

    private void init(Locale locale) {
        mFmt = createFormatter(locale);
        mZeroDigit = getZeroDigit(locale);
    }

    public String format(int value) {
        final Locale currentLocale = Locale.getDefault();
        if (mZeroDigit != getZeroDigit(currentLocale)) {
            init(currentLocale);
        }
        mArgs[0] = value;
        mBuilder.delete(0, mBuilder.length());
        mFmt.format("%02d", mArgs);
        return mFmt.toString();
    }

    private static char getZeroDigit(Locale locale) {
        // The original TwoDigitFormatter directly referenced LocaleData's value. Instead,
        // we need to use the public DecimalFormatSymbols API.
        return DecimalFormatSymbols.getInstance(locale).getZeroDigit();
    }

    private java.util.Formatter createFormatter(Locale locale) {
        return new java.util.Formatter(mBuilder, locale);
    }
}
