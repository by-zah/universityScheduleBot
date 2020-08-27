create table users
(
	chat_id bigint
		constraint users_pk
			primary key,
	local varchar,
	interfaculty_discipline varchar
);

create groups
(
	name varchar
		constraint groups_pk
			primary key,
	owner bigint
		constraint groups_users_chat_id_fk
			references users
				on update cascade on delete restrict
);

create table subscriptions
(
	"user" bigint
		constraint subscriptions_users_chat_id_fk
			references users
				on update cascade on delete restrict,
	"group" varchar
		constraint subscriptions_groups_name_fk
			references groups
				on update cascade on delete restrict
);

create unique index subscriptions_group_uindex
	on subscriptions ("group");

create unique index subscriptions_user_uindex
	on subscriptions ("user");