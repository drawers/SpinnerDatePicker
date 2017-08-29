package com.tsongkha.spinnerdatepicker;

public class ICU {

    /**
     * This method is directly copied from {libcore.icu.ICU}. The method is simple enough
     * that it probably won't change.
     */
    public static char[] getDateFormatOrder(String pattern) {
        char[] result = new char[3];
        int resultIndex = 0;
        boolean sawDay = false;
        boolean sawMonth = false;
        boolean sawYear = false;

        for (int i = 0; i < pattern.length(); ++i) {
            char ch = pattern.charAt(i);
            if (ch == 'd' || ch == 'L' || ch == 'M' || ch == 'y') {
                if (ch == 'd' && !sawDay) {
                    result[resultIndex++] = 'd';
                    sawDay = true;
                } else if ((ch == 'L' || ch == 'M') && !sawMonth) {
                    result[resultIndex++] = 'M';
                    sawMonth = true;
                } else if ((ch == 'y') && !sawYear) {
                    result[resultIndex++] = 'y';
                    sawYear = true;
                }
            } else if (ch == 'G') {
                // Ignore the era specifier, if present.
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                throw new IllegalArgumentException("Bad pattern character '"
                        + ch + "' in " + pattern);
            } else if (ch == '\'') {
                if (i < pattern.length() - 1 && pattern.charAt(i + 1) == '\'') {
                    ++i;
                } else {
                    i = pattern.indexOf('\'', i + 1);
                    if (i == -1) {
                        throw new IllegalArgumentException("Bad quoting in " + pattern);
                    }
                    ++i;
                }
            } else {
                // Ignore spaces and punctuation.
            }
        }
        return result;
    }

}
