CREATE TABLE eos_airdrop (
  id SERIAL PRIMARY KEY ,
  eos_address VARCHAR(50) NOT NULL UNIQUE ,
  eosish_amount DECIMAL(18, 4) NULL ,
  eos_amount DECIMAL(18, 4) NOT NULL ,
  in_processing BOOLEAN DEFAULT FALSE ,
  tx_hash VARCHAR(66) NULL,
  sent_at TIMESTAMP WITHOUT TIME ZONE NULL ,
  block_number BIGINT NULL
);