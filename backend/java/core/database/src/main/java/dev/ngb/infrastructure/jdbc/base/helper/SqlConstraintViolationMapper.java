package dev.ngb.infrastructure.jdbc.base.helper;

import org.springframework.stereotype.Component;

@Component
public class SqlConstraintViolationMapper {

    public boolean matchesConstraint(Throwable throwable, String constraintName) {
        if (throwable == null || constraintName == null || constraintName.isBlank()) {
            return false;
        }

        String normalizedConstraint = constraintName.toLowerCase();
        Throwable cursor = throwable;
        while (cursor != null) {
            String message = cursor.getMessage();
            if (message != null && message.toLowerCase().contains(normalizedConstraint)) {
                return true;
            }
            cursor = cursor.getCause();
        }
        return false;
    }
}
