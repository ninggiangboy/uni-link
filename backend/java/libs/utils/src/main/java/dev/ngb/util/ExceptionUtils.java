package dev.ngb.util;

import lombok.experimental.UtilityClass;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

@UtilityClass
public final class ExceptionUtils {

    public static Throwable getRootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    public static String getMessage(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        return Optional.ofNullable(throwable.getMessage())
                .orElse(throwable.getClass().getSimpleName());
    }

    public static String getStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    public static RuntimeException wrap(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }

    public static boolean hasCause(Throwable throwable, Class<? extends Throwable> type) {
        if (throwable == null || type == null) {
            return false;
        }

        Throwable cause = throwable;
        while (cause != null) {
            if (type.isInstance(cause)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
