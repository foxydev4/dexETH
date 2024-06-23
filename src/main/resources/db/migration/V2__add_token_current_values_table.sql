CREATE TABLE IF NOT EXISTS "token_current_values"
(
    "token_id"           BIGSERIAL PRIMARY KEY,
    "current_price"      DOUBLE PRECISION         NOT NULL,
    "price_1h"           DOUBLE PRECISION         NOT NULL,
    "price_24h"           DOUBLE PRECISION         NOT NULL,
    "price_7d"           DOUBLE PRECISION         NOT NULL,
    "market_cap"         DOUBLE PRECISION         NOT NULL,
    "liquidity"          DOUBLE PRECISION         NOT NULL,
    "circulating_supply" TEXT                     NOT NULL,
    "holders"            BIGINT                   NOT NULL,
    "volume"             BIGINT                   NOT NULL,
    "timestamp"          TIMESTAMP WITH TIME ZONE NOT NULL,

    FOREIGN KEY ("token_id") REFERENCES "tokens" ("id")
);
