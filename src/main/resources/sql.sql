CREATE TABLE currency_rate
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    currency_code VARCHAR(3) ,
    exchange_rate DECIMAL,
    update_time   TIMESTAMP
);