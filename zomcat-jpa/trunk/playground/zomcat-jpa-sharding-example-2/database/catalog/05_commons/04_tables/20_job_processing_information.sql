create table zcat_commons.job_processing_information
(
  jpi_job_name text,
  jpi_end_time timestamp without time zone,
  jpi_last_modified timestamp not null default clock_timestamp(),
  primary key (jpi_job_name)
);
comment on table zcat_commons.job_processing_information IS 'Table to allow jobs to store processing data for the next call.';
