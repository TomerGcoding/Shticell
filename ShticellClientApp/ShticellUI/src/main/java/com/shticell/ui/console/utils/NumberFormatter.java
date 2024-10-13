package com.shticell.ui.console.utils;

import java.text.DecimalFormat;

public class NumberFormatter {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public static String formatNumber(double number) {
        if (number == (long) number)
            return String.format("%,d", (long) number);
        else
            return decimalFormat.format(number);
    }
}