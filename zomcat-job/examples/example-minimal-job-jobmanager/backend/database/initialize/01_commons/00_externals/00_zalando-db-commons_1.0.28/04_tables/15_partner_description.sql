CREATE TABLE zz_commons.partner_description (
    pd_id serial PRIMARY KEY,
    pd_partner_id integer NOT NULL REFERENCES zz_commons.partner(p_id),
    pd_appdomain_id smallint NOT NULL,
    pd_delivery_time smallint,
    pd_last_modified timestamp without time zone NOT NULL DEFAULT clock_timestamp()
);

CREATE UNIQUE INDEX ON zz_commons.partner_description USING btree(pd_partner_id, pd_appdomain_id);
