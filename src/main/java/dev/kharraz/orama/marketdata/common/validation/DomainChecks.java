package dev.kharraz.orama.marketdata.common.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static java.math.BigDecimal.ZERO;

public final class DomainChecks {

    private DomainChecks() {
    }

    public static <T> T requireNonNull(T value, String label) {
        return Objects.requireNonNull(value, "%s must not be null".formatted(label));
    }

    public static BigDecimal requireNonNegative(BigDecimal value, String label) {
        requireNonNull(value, label);

        if (value.compareTo(ZERO) < 0)
            throw new IllegalArgumentException("%s must not be negative".formatted(label));

        return value;
    }

    public static Long requirePositive(Long value, String label) {
        requireNonNull(value, label);

        if (value <= 0)
            throw new IllegalArgumentException("%s must be positive".formatted(label));

        return value;
    }

    public static LocalDate requireOnOrAfter(LocalDate value, String label, LocalDate reference, String referenceLabel) {
        requireNonNull(value, label);
        requireNonNull(reference, referenceLabel);

        if (value.isBefore(reference))
            throw new IllegalArgumentException("%s must not be before %s".formatted(label, referenceLabel));

        return value;
    }

    public static String requireValidText(String text, int maxLength, String label) {
        String trimmed = requireNonNull(text, label)
                .strip();

        if (trimmed.isBlank())
            throw new IllegalArgumentException("%s must not be blank".formatted(label));

        if (trimmed.length() > maxLength)
            throw new IllegalArgumentException("%s must not be longer than %d characters".formatted(label, maxLength));

        return trimmed;
    }
}
