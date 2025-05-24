package com.mitrais.flight.booking.util;

public class StringUtils {

    public static String paddingZeroString(String param, int length) {
        if (param.length() == length) return param;

        StringBuilder paddingZero = new StringBuilder();
        for (int i = 0; i < (length - param.length()); i++) {
            paddingZero.insert(0, "0");
        }

        return paddingZero + param;
    }

}
