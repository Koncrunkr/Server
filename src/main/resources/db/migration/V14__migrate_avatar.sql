alter table person
    drop constraint FK_PERSON_ON_FILE;
alter table chat
    drop constraint FK_CHAT_ON_FILE;
alter table message
    drop constraint FK_MESSAGE_ON_FILE;

alter table person
    add constraint FK_PERSON_ON_FILE foreign key (avatar_id) references inner_file (id) ON UPDATE CASCADE on DELETE CASCADE;
alter table chat
    add constraint FK_CHAT_ON_FILE foreign key (avatar_id) references inner_file (id) ON UPDATE CASCADE on DELETE CASCADE;
alter table message
    add constraint FK_MESSAGE_ON_FILE foreign key (file_id) references inner_file (id) ON UPDATE CASCADE on DELETE CASCADE;