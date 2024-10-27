CREATE TABLE IF NOT EXISTS appuser
(
    id       uuid PRIMARY KEY,
    username text UNIQUE NOT NULL,
    password text        NOT NULL
);

CREATE TABLE IF NOT EXISTS appuser_authority
(
    id         uuid PRIMARY KEY,
    appuser_id uuid REFERENCES appuser (id),
    authority_value  text NOT NULL
);

CREATE INDEX IF NOT EXISTS ix_appuser_id_authority ON appuser_authority (appuser_id, authority_value);