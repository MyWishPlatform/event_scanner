CREATE TABLE joule_registration (
  id SERIAL PRIMARY KEY ,
  contract_id INTEGER REFERENCES contracts_contract (id) NOT NULL ,
  invocation_at TIMESTAMP WITHOUT TIME ZONE NOT NULL ,
  state VARCHAR(63) NOT NULL ,
  published_at TIMESTAMP WITHOUT TIME ZONE NULL ,
  tx_hash VARCHAR(66) NULL
);