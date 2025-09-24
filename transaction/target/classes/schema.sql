CREATE TABLE IF NOT EXISTS operation_types (
    operation_type_id BIGINT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    direction varchar(50) NOT NULL
);

