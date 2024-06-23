CREATE TABLE IF NOT EXISTS "token_recent_swaps"
(
    "id"        BIGSERIAL PRIMARY KEY,
    "token_id"  BIGINT                   NOT NULL,
    "price"     DOUBLE PRECISION         NOT NULL,
    "volume"    DOUBLE PRECISION         NOT NULL,
    "timestamp" TIMESTAMP WITH TIME ZONE NOT NULL,

    FOREIGN KEY ("token_id") REFERENCES "tokens" ("id")
);
