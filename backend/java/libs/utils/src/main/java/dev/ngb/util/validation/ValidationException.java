package dev.ngb.util.validation;

import java.util.List;

public class ValidationException extends IllegalArgumentException {

    private final List<ValidationError> errors;

    public ValidationException(List<ValidationError> errors) {
        super(buildMessage(errors));
        this.errors = List.copyOf(errors);
    }

    public List<ValidationError> errors() {
        return errors;
    }

    private static String buildMessage(List<ValidationError> errors) {
        return errors.stream()
                .map(error -> error.field() + ": " + error.message())
                .reduce((left, right) -> left + ", " + right)
                .orElse("Validation failed");
    }
}
