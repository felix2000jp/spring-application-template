CREATE TABLE IF NOT EXISTS note
(
    id         uuid PRIMARY KEY,
    appuser_id uuid,
    title      text NOT NULL,
    content    text NOT NULL
);