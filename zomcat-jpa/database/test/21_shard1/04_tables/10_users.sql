CREATE TABLE users
(
  u_id serial NOT NULL,
  u_creation_date timestamp without time zone NOT NULL,
  u_modification_date timestamp without time zone NOT NULL,
  u_name text,
  u_user_enum_type user_enum_type,
  u_other_enum_type other_enum_type_test,
  u_version integer NOT NULL,
  CONSTRAINT users_pkey PRIMARY KEY (u_id )
)