package dev.ngb.util.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class FluentValidator<T> {

    private final T target;
    private final List<Rule<T>> rules = new ArrayList<>();

    private FluentValidator(T target) {
        this.target = target;
    }

    public static <T> FluentValidator<T> of(T target) {
        return new FluentValidator<>(target);
    }

    public <V> RuleBuilder<T, V> ruleFor(String field, Function<T, V> accessor) {
        return new RuleBuilder<>(this, field, accessor);
    }

    public ValidationResult validate() {
        var errors = new ArrayList<ValidationError>();
        for (var rule : rules) {
            rule.validate(target, errors);
        }
        return new ValidationResult(errors);
    }

    public void validateAndThrow() {
        validate().throwIfInvalid();
    }

    private <V> void addRule(String field, Function<T, V> accessor, Predicate<V> predicate, String message) {
        rules.add((source, errors) -> {
            var value = accessor.apply(source);
            if (!predicate.test(value)) {
                errors.add(new ValidationError(field, message));
            }
        });
    }

    @FunctionalInterface
    private interface Rule<T> {
        void validate(T source, List<ValidationError> errors);
    }

    public static final class RuleBuilder<T, V> {

        private final FluentValidator<T> validator;
        private final String field;
        private final Function<T, V> accessor;

        private RuleBuilder(FluentValidator<T> validator, String field, Function<T, V> accessor) {
            this.validator = validator;
            this.field = field;
            this.accessor = accessor;
        }

        public RuleBuilder<T, V> must(Predicate<V> predicate, String message) {
            validator.addRule(field, accessor, predicate, message);
            return this;
        }

        public <NV> RuleBuilder<T, NV> ruleFor(String nextField, Function<T, NV> nextAccessor) {
            return validator.ruleFor(nextField, nextAccessor);
        }

        public RuleBuilder<T, V> notNull() {
            return must(Objects::nonNull, "must not be null");
        }

        public RuleBuilder<T, V> nullOr(Predicate<V> predicate, String message) {
            return must(value -> value == null || predicate.test(value), message);
        }

        public RuleBuilder<T, V> in(Collection<V> candidates) {
            return must(candidates::contains, "must be one of allowed values");
        }

        public RuleBuilder<T, V> notIn(Collection<V> candidates) {
            return must(value -> !candidates.contains(value), "contains forbidden value");
        }

        public RuleBuilder<T, V> notBlank() {
            return must(value -> value instanceof CharSequence sequence && !sequence.toString().isBlank(),
                    "must not be blank");
        }

        public RuleBuilder<T, V> notEmpty() {
            return must(value -> switch (value) {
                case CharSequence sequence -> !sequence.isEmpty();
                case Collection<?> collection -> !collection.isEmpty();
                case Map<?, ?> map -> !map.isEmpty();
                case null, default -> false;
            }, "must not be empty");
        }

        public RuleBuilder<T, V> notNullOrBlank() {
            return must(value -> value instanceof CharSequence sequence && !sequence.toString().isBlank(),
                    "must not be null or blank");
        }

        public RuleBuilder<T, V> minLength(int minLength) {
            return must(value -> value instanceof CharSequence sequence && sequence.length() >= minLength,
                    "length must be at least " + minLength);
        }

        public RuleBuilder<T, V> maxLength(int maxLength) {
            return must(value -> value instanceof CharSequence sequence && sequence.length() <= maxLength,
                    "length must be at most " + maxLength);
        }

        public RuleBuilder<T, V> matches(String regex) {
            var pattern = Pattern.compile(regex);
            return must(value -> value instanceof CharSequence sequence && pattern.matcher(sequence).matches(),
                    "format is invalid");
        }

        public RuleBuilder<T, V> matches(Pattern pattern) {
            return must(value -> value instanceof CharSequence sequence && pattern.matcher(sequence).matches(),
                    "format is invalid");
        }

        public RuleBuilder<T, V> email() {
            var emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            return must(value -> value instanceof CharSequence sequence
                            && emailPattern.matcher(sequence).matches(),
                    "must be a valid email");
        }

        public RuleBuilder<T, V> greaterThan(Number min) {
            return must(value -> value instanceof Number number && number.doubleValue() > min.doubleValue(),
                    "must be greater than " + min);
        }

        public RuleBuilder<T, V> greaterOrEqual(Number min) {
            return must(value -> value instanceof Number number && number.doubleValue() >= min.doubleValue(),
                    "must be >= " + min);
        }

        public RuleBuilder<T, V> lessThan(Number max) {
            return must(value -> value instanceof Number number && number.doubleValue() < max.doubleValue(),
                    "must be less than " + max);
        }

        public RuleBuilder<T, V> lessOrEqual(Number max) {
            return must(value -> value instanceof Number number && number.doubleValue() <= max.doubleValue(),
                    "must be <= " + max);
        }

        public RuleBuilder<T, V> positive() {
            return must(value -> value instanceof Number number && number.doubleValue() > 0, "must be positive");
        }

        public RuleBuilder<T, V> negative() {
            return must(value -> value instanceof Number number && number.doubleValue() < 0, "must be negative");
        }

        public RuleBuilder<T, V> between(Number min, Number max) {
            return must(value -> value instanceof Number number
                            && number.doubleValue() >= min.doubleValue()
                            && number.doubleValue() <= max.doubleValue(),
                    "must be between " + min + " and " + max);
        }

        public ValidationResult validate() {
            return validator.validate();
        }

        public void validateAndThrow() {
            validator.validateAndThrow();
        }
    }
}
