CREATE USER ether WITH PASSWORD 'ether';
CREATE DATABASE ether WITH OWNER ether ENCODING 'SQL_ASCII' TEMPLATE template0;

CREATE TABLE transaction (
  id           BYTEA PRIMARY KEY,
  block_number BIGINT    NOT NULL,
  value        DECIMAL(36, 18) NOT NULL,
  from_address BYTEA     NOT NULL,
  to_address   BYTEA DEFAULT decode('0000000000000000000000000000000000000000', 'hex'),
  nonce        BIGINT NOT NULL ,
  gas          DECIMAL(36, 18),
  gas_price    DECIMAL(36, 18)
);

CREATE TABLE block (
  id BIGINT PRIMARY KEY ,
  timestamp INT NOT NULL ,
  miner_address BYTEA NOT NULL ,
  difficulty BIGINT NOT NULL
);

CREATE TABLE balance (
  block_number BIGINT NOT NULL ,
  address BYTEA NOT NULL ,
  amount DECIMAL(36, 18),
  PRIMARY KEY (block_number, address)
)
