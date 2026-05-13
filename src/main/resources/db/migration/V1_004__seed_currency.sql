-- currency reference data
-- based on ISO 4217 (major + widely used currencies)

insert into currency (code, name, symbol)
values ('USD', 'US Dollar', '$'),
       ('EUR', 'Euro', '€'),
       ('GBP', 'Pound Sterling', '£'),
       ('CHF', 'Swiss Franc', 'CHF'),

       -- Europe
       ('SEK', 'Swedish Krona', 'kr'),
       ('NOK', 'Norwegian Krone', 'kr'),
       ('DKK', 'Danish Krone', 'kr'),
       ('PLN', 'Polish Zloty', 'zl'),
       ('CZK', 'Czech Koruna', 'Kc'),
       ('HUF', 'Hungarian Forint', 'Ft'),
       ('RON', 'Romanian Leu', 'lei'),
       ('BGN', 'Bulgarian Lev', 'lv'),

       -- Americas
       ('CAD', 'Canadian Dollar', '$'),
       ('MXN', 'Mexican Peso', '$'),
       ('BRL', 'Brazilian Real', 'R$'),
       ('ARS', 'Argentine Peso', '$'),
       ('CLP', 'Chilean Peso', '$'),
       ('COP', 'Colombian Peso', '$'),
       ('PEN', 'Peruvian Sol', 'S/'),

       -- Asia
       ('JPY', 'Japanese Yen', '¥'),
       ('CNY', 'Chinese Yuan', '¥'),
       ('HKD', 'Hong Kong Dollar', '$'),
       ('SGD', 'Singapore Dollar', '$'),
       ('KRW', 'South Korean Won', '₩'),
       ('INR', 'Indian Rupee', '₹'),
       ('IDR', 'Indonesian Rupiah', 'Rp'),
       ('MYR', 'Malaysian Ringgit', 'RM'),
       ('THB', 'Thai Baht', '฿'),
       ('PHP', 'Philippine Peso', '₱'),
       ('VND', 'Vietnamese Dong', '₫'),

       -- Middle East & Africa
       ('AED', 'UAE Dirham', 'د.إ'),
       ('SAR', 'Saudi Riyal', '﷼'),
       ('ILS', 'Israeli New Shekel', '₪'),
       ('TRY', 'Turkish Lira', '₺'),
       ('ZAR', 'South African Rand', 'R'),
       ('NGN', 'Nigerian Naira', '₦'),
       ('EGP', 'Egyptian Pound', '£'),
       ('MAD', 'Moroccan Dirham', 'د.م.'),

       -- Oceania
       ('AUD', 'Australian Dollar', '$'),
       ('NZD', 'New Zealand Dollar', '$')
on conflict (code) do update
set
    name = excluded.name,
    symbol = excluded.symbol;
