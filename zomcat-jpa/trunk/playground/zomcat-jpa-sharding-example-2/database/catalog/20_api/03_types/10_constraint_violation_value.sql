CREATE TYPE constraint_violation_value AS (
  value text,
  property_path text,
  constraint_violation constraint_violation
);
