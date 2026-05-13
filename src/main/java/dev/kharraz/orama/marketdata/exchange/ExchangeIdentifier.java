package dev.kharraz.orama.marketdata.exchange;

import dev.kharraz.orama.marketdata.common.persistence.BaseEntity;
import dev.kharraz.orama.marketdata.provider.Provider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.Locale;

import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireNonNull;
import static dev.kharraz.orama.marketdata.common.validation.DomainChecks.requireValidText;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(
    name = "exchange_identifier",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ex_id_exchange_provider",
            columnNames = {"exchange_id", "provider_id"}
        ),
        @UniqueConstraint(
            name = "uk_ex_id_provider_provider_code",
            columnNames = {"provider_id", "provider_code"}
        )
    }
)
@Getter
public class ExchangeIdentifier extends BaseEntity {

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
            name = "exchange_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_ex_id_exchange")
    )
    private Exchange exchange;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(
            name = "provider_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_ex_id_provider")
    )
    private Provider provider;

    @Column(name = "provider_code", nullable = false, updatable = false, length = 32)
    @NotBlank
    @Size(max = 32)
    private String providerCode;

    protected ExchangeIdentifier() {
    }

    public ExchangeIdentifier(Exchange exchange, Provider provider, String providerCode) {
        this.exchange = requireNonNull(exchange, "exchange");
        this.provider = requireNonNull(provider, "provider");
        this.providerCode = requireValidProviderCode(providerCode);
    }

    private static String requireValidProviderCode(String providerCode) {
        return requireValidText(providerCode, 32, "provider code")
                .toUpperCase(Locale.ROOT);
    }

}
