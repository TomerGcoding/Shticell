package ui.console.utils;

import java.text.DecimalFormat;

public class NumberFormatter {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    public static String formatNumber(double number) {
        // Check if the number is an integer
        if (number == (long) number) {
            // If it's an integer, use a format without decimals
            return String.format("%,d", (long) number);
        } else {
            // If it's a decimal number, use the decimal format with thousands separator
            return decimalFormat.format(number);
        }
    }
}