CREATE TABLE role
(
  r_id serial NOT NULL,
  r_creation_date timestamp without time zone NOT NULL,
  r_modification_date timestamp without time zone NOT NULL,
  r_name text,
  r_version integer,
  r_user_id bigint NOT NULL,
  CONSTRAINT role_pkey PRIMARY KEY (r_id ),
  CONSTRAINT fk_role_r_user_id FOREIGN KEY (r_user_id)
      REFERENCES users (u_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)