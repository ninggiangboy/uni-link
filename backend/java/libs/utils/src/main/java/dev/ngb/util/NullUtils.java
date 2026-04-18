package dev.ngb.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class NullUtils {
    public static <T> T getOr(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static String getOrEmpty(String s) {
        return s == null ? "" : s;
    }

    public static Long getOrZero(Long l) {
        return l == null ? 0 : l;
    }

    public static Integer getOrZero(Integer i) {
        return i == null ? 0 : i;
    }

    public static Double getOrZero(Double d) {
        return d == null ? 0 : d;
    }
}
