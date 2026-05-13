package dev.kharraz.orama.marketdata.provider;

import dev.kharraz.orama.marketdata.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.NaturalId;

import java.util.Locale;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNull;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireValidText;

@Entity
@Table(
    name = "provider",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_provider_code",
        columnNames = "code"
    )
)
@Getter
public class Provider extends BaseEntity {

    private static final String PROVIDER_CODE_REGEX = "^[A-Z0-9]+$";

    @NaturalId
    @Column(name = "code", nullable = false, updatable = false, length = 32)
    @Size(max = 32)
    @Pattern(regexp = PROVIDER_CODE_REGEX)
    private String code;

    @Column(name = "name", nullable = false, updatable = false, length = 128)
    @NotBlank
    @Size(max = 128)
    private String name;

    protected Provider() {
    }

    public Provider(String name, String code) {
        this.name = requireValidName(name);
        this.code = requireValidCode(code);
    }

    private static String requireValidName(String name) {
        return requireValidText(name, 128, "provider name");
    }

    private static String requireValidCode(String code) {
        String normalizedCode = requireNonNull(code, "provider code")
                .strip()
                .toUpperCase(Locale.ROOT);

        if (normalizedCode.isBlank()) {
            throw new IllegalArgumentException("provider code must not be blank");
        }
        if (normalizedCode.length() > 32) {
            throw new IllegalArgumentException("provider code must not be longer than 32 characters");
        }

        if (!normalizedCode.matches(PROVIDER_CODE_REGEX)) {
            throw new IllegalArgumentException("provider code must contain only uppercase letters and digits");
        }

        return normalizedCode;
    }

}
