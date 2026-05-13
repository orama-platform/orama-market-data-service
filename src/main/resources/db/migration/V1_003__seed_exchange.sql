-- exchange reference data
-- based on ISO 10383 MIC codes (major venues)

insert into exchange (name, mic, country_code)
values
    -- United States
    ('NASDAQ', 'XNAS', 'US'),
    ('New York Stock Exchange', 'XNYS', 'US'),

    -- United Kingdom
    ('London Stock Exchange', 'XLON', 'GB'),

    -- France
    ('Euronext Paris', 'XPAR', 'FR'),

    -- Netherlands
    ('Euronext Amsterdam', 'XAMS', 'NL'),

    -- Belgium
    ('Euronext Brussels', 'XBRU', 'BE'),

    -- Portugal
    ('Euronext Lisbon', 'XLIS', 'PT'),

    -- Ireland
    ('Euronext Dublin', 'XDUB', 'IE'),

    -- Germany
    ('Xetra', 'XETR', 'DE'),

    -- Switzerland
    ('SIX Swiss Exchange', 'XSWX', 'CH'),

    -- Japan
    ('Tokyo Stock Exchange', 'XTKS', 'JP')

on conflict (mic) do update
set
    name = excluded.name,
    country_code = excluded.country_code;
