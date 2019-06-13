CREATE TABLE wish_bnb_swap_swap_entry (
  id SERIAL PRIMARY KEY ,
  amount BIGINT ,
  eth_tx_hash VARCHAR(66) ,
  bnb_tx_hash VARCHAR(66) ,
  link_entry_id SERIAL REFERENCES wish_bnb_swap_link_entry (id)
);
