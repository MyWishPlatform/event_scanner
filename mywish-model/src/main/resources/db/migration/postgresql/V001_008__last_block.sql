CREATE TABLE last_block (
  id SERIAL PRIMARY KEY ,
  network VARCHAR UNIQUE NOT NULL,
  block_number BIGINT NOT NULL
);