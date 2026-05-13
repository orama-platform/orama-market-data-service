
-- Audit Helper (updated_at)
create or replace function set_updated_at()
returns trigger as $$
begin
    new.updated_at := statement_timestamp();
    return new;
end;
$$ language plpgsql;

-- country
create table country
(
    iso_code_2  varchar(2)      not null,
    iso_code_3  varchar(3)      not null,
    name        varchar(128)    not null,

    constraint pk_country
        primary key (iso_code_2),
    constraint uk_country_iso3
        unique (iso_code_3),
    constraint ck_country_iso2_format
        check (iso_code_2 ~ '^[A-Z]{2}$'),
    constraint ck_country_iso3_format
        check (iso_code_3 ~ '^[A-Z]{3}$'),
    constraint ck_country_name_not_blank
        check (btrim(name) <> '')
);

create index ix_country_name
    ON country (name);

-- currency
create table currency
(
    code    varchar(3)      not null,
    name    varchar(64)     not null,
    symbol  varchar(8),

    constraint pk_currency
        primary key (code),
    constraint ck_currency_code_format
        check (code ~ '^[A-Z]{3}$'),
    constraint ck_currency_name_not_blank
        check (btrim(name) <> ''),
    constraint ck_currency_name_trimmed
        check (btrim(name) = name),
    constraint ck_currency_symbol_not_blank
        check (symbol is null or btrim(symbol) <> ''),
    constraint ck_currency_symbol_trimmed
        check (symbol is null or btrim(symbol) = symbol)
);

-- exchange
create table exchange
(
    id  bigint generated always as identity,

    name            varchar(256)    not null,
    mic             varchar(4)      not null,
    country_code    varchar(2)      not null,

    constraint pk_exchange
        primary key (id),
    constraint fk_exchange_country
        foreign key (country_code) references country (iso_code_2)
        on update restrict
        on delete restrict,
    constraint uk_exchange_country_name
        unique (country_code, name),
    constraint uk_exchange_mic
        unique (mic),
    constraint ck_exchange_name_not_blank
        check (btrim(name) <> ''),
    constraint ck_exchange_mic_upper
        check (upper(mic) = mic),
    constraint ck_exchange_name_trimmed
        check (btrim(name) = name),
    constraint ck_exchange_mic_format
        check (mic ~ '^[A-Z0-9]{4}$')
);

create index ix_exchange_country
    on exchange(country_code);

-- provider
create table provider
(
    id  bigint generated always as identity,

    name    varchar(128)    not null,
    code    varchar(32)     not null,

    constraint pk_provider
        primary key (id),

    constraint uk_provider_code
        unique (code),

    constraint ck_provider_code_format
        check (code ~ '^[A-Z0-9]+$'),

    constraint ck_provider_code_not_blank
        check (btrim(code) <> ''),

    constraint ck_provider_code_trimmed
        check (btrim(code) = code),

    constraint ck_provider_name_not_blank
        check (btrim(name) <> ''),

    constraint ck_provider_name_trimmed
        check (btrim(name) = name)
);

-- exchange provider specific mapping
create table exchange_identifier
(
    id  bigint generated always as identity,

    exchange_id     bigint          not null,
    provider_id     bigint          not null,
    provider_code   varchar(32)     not null,

    constraint pk_ex_id
        primary key (id),

    constraint fk_ex_id_exchange
        foreign key (exchange_id) references exchange (id)
        on update restrict
        on delete cascade,

    constraint fk_ex_id_provider
        foreign key (provider_id) references provider (id)
        on update restrict
        on delete restrict,

    constraint uk_ex_id_exchange_provider
        unique (exchange_id, provider_id),

    constraint uk_ex_id_provider_provider_code
        unique (provider_id, provider_code),

    constraint ck_ex_id_provider_code_not_blank
        check (btrim(provider_code) <> ''),

    constraint ck_ex_id_provider_code_upper
        check (upper(provider_code) = provider_code),

    constraint ck_ex_id_provider_code_trimmed
        check (btrim(provider_code) = provider_code)
);

-- sector
create table sector
(
    id  bigint generated always as identity,

    name    varchar(128)    not null,

    constraint pk_sector
        primary key (id),

    constraint uk_sector_name
        unique (name),

    constraint ck_sector_name_not_blank
        check (btrim(name) <> '')
);

-- industry
create table industry
(
    id  bigint generated always as identity,

    name        varchar(128)    not null,
    sector_id   bigint          not null,

    constraint pk_industry
        primary key (id),

    constraint fk_industry_sector
        foreign key (sector_id) references sector (id)
        on update restrict
        on delete restrict,

    constraint uk_industry_sector_name
        unique (sector_id, name),

    constraint ck_industry_name_not_blank
        check (btrim(name) <> '')
);

-- corporate
create table corporate
(
    id  bigint generated always as identity,

    ticker          varchar(16)     not null,
    isin            varchar(12),
    name            varchar(256)    not null,
    country_code    varchar(2)      not null,
    currency_code   varchar(3)      not null,
    exchange_id     bigint          not null,
    industry_id     bigint          not null,

    created_at      timestamptz     not null default statement_timestamp(),
    updated_at      timestamptz     not null default statement_timestamp(),

    constraint pk_corporate
        primary key (id),

    constraint fk_corporate_country
        foreign key (country_code) references country (iso_code_2)
        on update restrict
        on delete restrict,

    constraint fk_corporate_currency
        foreign key (currency_code) references currency (code)
        on update restrict
        on delete restrict,

    constraint fk_corporate_exchange
        foreign key (exchange_id) references exchange (id)
        on update restrict
        on delete restrict,

    constraint fk_corporate_industry
        foreign key (industry_id) references industry (id)
        on update restrict
        on delete restrict,

    constraint uk_corporate_exchange_ticker
        unique (exchange_id, ticker),

    constraint uk_corporate_isin
        unique (isin),

    constraint ck_corporate_ticker_not_blank
        check (btrim(ticker) <> ''),

    constraint ck_corporate_ticker_upper
        check (upper(ticker) = ticker),

    constraint ck_corporate_ticker_trimmed
        check (btrim(ticker) = ticker),

    constraint ck_corporate_name_not_blank
        check (btrim(name) <> ''),

    constraint ck_corporate_name_trimmed
        check (btrim(name) = name),

    constraint ck_corporate_isin_format
        check (isin is null or isin ~ '^[A-Z]{2}[A-Z0-9]{9}[0-9]$')
);

create index ix_corporate_industry_id
    on corporate (industry_id);

create index ix_corporate_country
    on corporate (country_code);

create index ix_corporate_ticker
    on corporate (ticker);

create trigger trigger_corporate_set_updated_at
    before update
    on corporate
    for each row
    execute function set_updated_at();

-- quote
create table quote
(
    id  bigint generated always as identity,

    corporate_id    bigint          not null,

    as_of_ts        timestamptz     not null,

    price               numeric(19, 4)  not null,
    day_high            numeric(19, 4)  not null,
    day_low             numeric(19, 4)  not null,
    year_high           numeric(19, 4)  not null,
    year_low            numeric(19, 4)  not null,

    shares_outstanding  bigint          not null,

    created_at          timestamptz     not null default statement_timestamp(),

    constraint pk_quote
        primary key (id),

    constraint fk_quote_corporate
        foreign key (corporate_id) references corporate (id)
        on update restrict
        on delete cascade,

    constraint uk_quote_corporate_as_of
        unique (corporate_id, as_of_ts),

    constraint ck_quote_shares_outstanding_positive
        check (shares_outstanding > 0),

    constraint ck_quote_price_non_negative
        check (price >= 0),

    constraint ck_quote_day_high_non_negative
        check (day_high >= 0),

    constraint ck_quote_day_low_non_negative
        check (day_low >= 0),

    constraint ck_quote_year_high_non_negative
        check (year_high >= 0),

    constraint ck_quote_year_low_non_negative
        check (year_low >= 0),

    constraint ck_quote_day_range
        check (day_low <= day_high),

    constraint ck_quote_year_range
        check (year_low <= year_high),

    constraint ck_quote_price_within_day_range
        check (price >= day_low and price <= day_high),

    constraint ck_quote_price_within_year_range
        check (price >= year_low and price <= year_high),

    constraint ck_quote_day_high_within_year_high
        check (day_high <= year_high),

    constraint ck_quote_day_low_within_year_low
        check (day_low >= year_low)
);

-- balance sheet
create table balance_sheet
(
    id  bigint generated always as identity,

    corporate_id            bigint      not null,
    report_date             date        not null,
    report_period_type      varchar(1)  not null,

    accounts_receivable     numeric(19, 4),
    current_assets          numeric(19, 4),
    current_liabilities     numeric(19, 4),
    inventory               numeric(19, 4),
    non_current_assets      numeric(19, 4),
    non_current_liabilities numeric(19, 4),

    created_at              timestamptz not null default statement_timestamp(),
    updated_at              timestamptz not null default statement_timestamp(),

    constraint pk_balance_sheet
        primary key (id),

    constraint fk_balance_sheet_corporate
        foreign key (corporate_id) references corporate (id)
        on update restrict
        on delete cascade,

    constraint uk_balance_sheet_corporate_report_date_period_type
        unique (corporate_id, report_date, report_period_type),

    constraint ck_balance_sheet_report_period_type
        check (report_period_type in ('Y', 'Q')),

    constraint ck_balance_sheet_accounts_receivable_positive
        check (accounts_receivable is null or accounts_receivable >= 0),

    constraint ck_balance_sheet_current_assets_positive
        check (current_assets is null or current_assets >= 0),

    constraint ck_balance_sheet_current_liabilities_positive
        check (current_liabilities is null or current_liabilities >= 0),

    constraint ck_balance_sheet_inventory_positive
        check (inventory is null or inventory >= 0),

    constraint ck_balance_sheet_non_current_assets_positive
        check (non_current_assets is null or non_current_assets >= 0),

    constraint ck_balance_sheet_non_current_liabilities_positive
        check (non_current_liabilities is null or non_current_liabilities >= 0)
);

create index idx_balance_sheet_corporate_report_date_period_type
    on balance_sheet (corporate_id, report_period_type, report_date DESC);

create trigger trigger_balance_sheet_set_updated_at
    before update
    on balance_sheet
    for each row
    execute function set_updated_at();

-- income statement
create table income_statement
(
    id  bigint  generated always as identity,

    corporate_id        bigint          not null,
    report_date         date            not null,
    report_period_type  varchar(1)      not null,

    revenue             numeric(19, 4),
    ebit                numeric(19, 4),
    ebitda              numeric(19, 4),
    net_income          numeric(19, 4),
    operating_expenses  numeric(19, 4),

    created_at          timestamptz not null default statement_timestamp(),
    updated_at          timestamptz not null default statement_timestamp(),

    constraint pk_income_statement
        primary key (id),

    constraint fk_income_statement_corporate
        foreign key (corporate_id) references corporate (id)
        on update restrict
        on delete cascade,

    constraint uk_income_statement_corporate_report_date_period_type
        unique (corporate_id, report_date, report_period_type),

    constraint ck_income_statement_report_period_type
        check (report_period_type in ('Y', 'Q')),

    constraint ck_income_statement_revenue_positive
        check (revenue is null or revenue >= 0),

    constraint ck_income_statement_operating_expenses_positive
        check (operating_expenses is null or operating_expenses >= 0)
);

create trigger trigger_income_statement_set_updated_at
    before update
    on income_statement
    for each row
    execute function set_updated_at();

-- dividend
create table dividend
(
    id  bigint  generated always as identity,

    corporate_id        bigint  not null,
    ex_date             date    not null,
    payment_date        date,

    rate                numeric(19, 4)  not null ,
    five_year_avg       numeric(19, 4),
    forward_rate        numeric(19, 4),
    payout_ratio        numeric(19, 2),

    created_at          timestamptz not null default statement_timestamp(),
    updated_at          timestamptz not null default statement_timestamp(),

    constraint pk_dividend
        primary key (id),

    constraint fk_dividend_corporate
        foreign key (corporate_id) references corporate (id)
            on update restrict
            on delete cascade,

    constraint uk_dividend_corporate_ex_date
        unique (corporate_id, ex_date),

    constraint ck_dividend_rate_positive
        check (rate >= 0),

    constraint ck_dividend_payment_date_after_ex_date
        check (payment_date is null or payment_date >= ex_date),

    constraint ck_dividend_five_year_avg_positive
        check (five_year_avg is null or five_year_avg >= 0),

    constraint ck_dividend_forward_rate_positive
        check (forward_rate is null or forward_rate >= 0)
);

create trigger trigger_dividend_set_updated_at
    before update
    on dividend
    for each row
    execute function set_updated_at();
