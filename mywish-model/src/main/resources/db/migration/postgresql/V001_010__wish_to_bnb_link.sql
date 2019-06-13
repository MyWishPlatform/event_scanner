CREATE TABLE wish_bnb_swap_link_entry (
  id SERIAL PRIMARY KEY ,
  eth_address VARCHAR(50) NOT NULL ,
  bnb_address VARCHAR(50) NOT NULL
);
