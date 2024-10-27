CREATE TABLE note
(
    id         uuid PRIMARY KEY,
    appuser_id uuid REFERENCES appuser (id) NOT NULL,
    title      text                         NOT NULL,
    content    text                         NOT NULL
);