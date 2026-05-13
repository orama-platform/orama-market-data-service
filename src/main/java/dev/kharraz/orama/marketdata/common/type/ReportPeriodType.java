package dev.kharraz.orama.marketdata.common.type;

import lombok.Getter;

public enum ReportPeriodType {
    Y("YEAR"),
    Q("QUARTER");

    @Getter
    private final String label;

    ReportPeriodType(String label) {
        this.label = label;
    }
}
