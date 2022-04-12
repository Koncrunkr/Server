
alter table message add column edited TIMESTAMP WITHOUT TIME ZONE;
-- noinspection SqlWithoutWhere
update message set edited = time;
ALTER TABLE message ALTER COLUMN edited SET NOT NULL;

ALTER TABLE message rename column time to created;