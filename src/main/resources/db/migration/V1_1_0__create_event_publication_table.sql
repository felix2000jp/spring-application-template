CREATE TABLE IF NOT EXISTS event_publication
(
    id               uuid PRIMARY KEY         NOT NULL,
    listener_id      text                     NOT NULL,
    event_type       text                     NOT NULL,
    serialized_event text                     NOT NULL,
    publication_date timestamp WITH TIME ZONE NOT NULL,
    completion_date  timestamp WITH TIME ZONE
);

CREATE INDEX event_publication_serialized_event_hash_idx ON event_publication USING hash (serialized_event);
CREATE INDEX event_publication_by_completion_date_idx ON event_publication (completion_date);