package com.yan.java.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T min(T v1, T v2, T v3) {
        T smaller = v1.compareTo(v2) > 0 ? v2 : v1;
        return smaller.compareTo(v3) > 0 ? v3 : smaller;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable> T max(T v1, T v2, T v3) {
        T bigger = v1.compareTo(v2) > 0 ? v1 : v2;
        return bigger.compareTo(v3) > 0 ? bigger : v3;
    }



}
