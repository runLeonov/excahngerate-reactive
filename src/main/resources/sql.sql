CREATE TABLE currency_rate
(
    currency_code VARCHAR(3) PRIMARY KEY,
    exchange_rate DECIMAL,
    update_time   TIMESTAMP
);