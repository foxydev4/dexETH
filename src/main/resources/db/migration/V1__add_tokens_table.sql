CREATE TABLE IF NOT EXISTS "tokens"
(
    "id"                  BIGSERIAL PRIMARY KEY,
    "name"                TEXT                     NOT NULL,
    "pool_id"             TEXT                     NOT NULL,
    "pool_token0"         TEXT                     NOT NULL,
    "pool_token1"         TEXT                     NOT NULL,
    "pool_type"           TEXT                     NOT NULL,
    "address"             TEXT                     NOT NULL,
    "ticker"              TEXT                     NOT NULL,
    "logo"                TEXT                     NOT NULL,
    "chain"               TEXT                     NOT NULL,
    "decimals"            INT                      NOT NULL,
    "creation_timestamp"  TIMESTAMP WITH TIME ZONE NOT NULL,
    "score"               INT                      NOT NULL,
    "paid_listing"        BOOLEAN                  NOT NULL DEFAULT FALSE,
    "website"             TEXT,
    "telegram"            TEXT,
    "discord"             TEXT,
    "twitter"             TEXT,
    "supply"              TEXT                     NOT NULL,
    "created_at"          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);
