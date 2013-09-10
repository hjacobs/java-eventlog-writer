create type multimedia as (
    version                    integer,
    code                       bigint,
    sku                        text,
    type_code                  text,
    is_external                boolean,
    path                       text,
    media_character_code       text,
    checksum                   text,
    width                      int,
    height                     int
);
