CREATE TABLE airdrop_wish_transfer (
  id SERIAL PRIMARY KEY ,
  from_address VARCHAR(50) NOT NULL ,
  to_address VARCHAR(50) NOT NULL ,
  wish_amount DECIMAL(80) NOT NULL ,
  tx_hash VARCHAR(66) NOT NULL,
  block_number BIGINT NOT NULL
);