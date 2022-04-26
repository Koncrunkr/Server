CREATE TABLE person_authorities
(
    person_id   numeric(40) NOT NULL,
    authorities VARCHAR(255) NOT NULL
);

ALTER TABLE person_authorities
    ADD CONSTRAINT fk_person_authorities_on_person FOREIGN KEY (person_id) REFERENCES person (id);
