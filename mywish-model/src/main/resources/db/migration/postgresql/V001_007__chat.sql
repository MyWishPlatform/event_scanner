CREATE TABLE chat (
  id SERIAL PRIMARY KEY ,
  chat_id BIGINT UNIQUE NOT NULL,
  bot VARCHAR (50) NOT NULL
);