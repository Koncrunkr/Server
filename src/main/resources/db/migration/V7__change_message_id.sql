ALTER TABLE message
    DROP COLUMN id;

ALTER TABLE message
    ADD CONSTRAINT pk_message PRIMARY KEY (x, y, chat_id);