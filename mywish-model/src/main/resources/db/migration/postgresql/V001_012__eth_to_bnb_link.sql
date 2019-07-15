CREATE TABLE eth_bnb_swap_link_entry (
  id SERIAL PRIMARY KEY ,
  symbol VARCHAR(10) NOT NULL,
  eth_address VARCHAR(50) NOT NULL ,
  bnb_address VARCHAR(50) NOT NULL,
  UNIQUE (symbol, eth_address)
);
