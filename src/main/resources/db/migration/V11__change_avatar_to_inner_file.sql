create table inner_file
(
    id              bigint generated by default as identity
        primary key,
    name varchar(255),
    type integer not null,
    link text    not null
);


alter table person add column avatar_id bigint;
alter table chat add column avatar_id bigint;
alter table message add column file_id bigint;

alter table person add constraint FK_PERSON_ON_FILE foreign key (avatar_id) references inner_file(id);
alter table chat add constraint FK_CHAT_ON_FILE foreign key (avatar_id) references inner_file(id);
alter table message add constraint FK_MESSAGE_ON_FILE foreign key (file_id) references inner_file(id);


insert into inner_file(name, type, link) (select 'avatar', 0, person.avatar from person);
insert into inner_file(name, type, link) (select 'avatar', 0, chat.avatar from chat);

update person set avatar_id=inner_file.id from inner_file where inner_file.link=person.avatar;
update chat set avatar_id=inner_file.id from inner_file where inner_file.link=chat.avatar;

alter table person drop column avatar;
alter table chat drop column avatar;
