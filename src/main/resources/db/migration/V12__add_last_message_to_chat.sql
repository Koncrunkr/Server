ALTER TABLE chat
    ADD last_messagex INTEGER;

ALTER TABLE chat
    ADD last_messagey INTEGER;

ALTER TABLE person
    ADD username VARCHAR(24);

ALTER TABLE inner_file
    ADD CONSTRAINT uc_innerfile_link UNIQUE (link);

ALTER TABLE chat
    DROP COLUMN last_message_id;

ALTER TABLE person
    ALTER COLUMN avatar_id SET NOT NULL;