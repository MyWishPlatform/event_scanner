CREATE TABLE eosish_airdrop (
  id SERIAL PRIMARY KEY ,
  eth_address VARCHAR(50) UNIQUE NOT NULL ,
  eos_address VARCHAR(50) NOT NULL ,
  wish_amount DECIMAL(80) NOT NULL ,
  eosish_amount DECIMAL(18, 4) NULL ,
  in_processing BOOLEAN DEFAULT FALSE ,
  tx_hash VARCHAR(66) NULL,
  sent_at TIMESTAMP WITHOUT TIME ZONE NULL ,
  block_number BIGINT NULL
);