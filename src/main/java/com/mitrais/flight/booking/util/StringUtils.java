package com.mitrais.flight.booking.util;

public class StringUtils {

    public static String paddingZeroString(String param, int length) {
        String result = param;
        if (result.length() == length) return result;

        String paddingZero = "";
        for (int i = 0; i < (length - result.length()); i++) {
            paddingZero = "0" + paddingZero;
        }

        return paddingZero + result;
    }

}
