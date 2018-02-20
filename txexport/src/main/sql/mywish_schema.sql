--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: account_emailaddress; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE account_emailaddress (
    id integer NOT NULL,
    email character varying(254) NOT NULL,
    verified boolean NOT NULL,
    "primary" boolean NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE account_emailaddress OWNER TO lastwill_new;

--
-- Name: account_emailaddress_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE account_emailaddress_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE account_emailaddress_id_seq OWNER TO lastwill_new;

--
-- Name: account_emailaddress_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE account_emailaddress_id_seq OWNED BY account_emailaddress.id;


--
-- Name: account_emailconfirmation; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE account_emailconfirmation (
    id integer NOT NULL,
    created timestamp with time zone NOT NULL,
    sent timestamp with time zone,
    key character varying(64) NOT NULL,
    email_address_id integer NOT NULL
);


ALTER TABLE account_emailconfirmation OWNER TO lastwill_new;

--
-- Name: account_emailconfirmation_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE account_emailconfirmation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE account_emailconfirmation_id_seq OWNER TO lastwill_new;

--
-- Name: account_emailconfirmation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE account_emailconfirmation_id_seq OWNED BY account_emailconfirmation.id;


--
-- Name: auth_group; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE auth_group (
    id integer NOT NULL,
    name character varying(80) NOT NULL
);


ALTER TABLE auth_group OWNER TO lastwill_new;

--
-- Name: auth_group_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE auth_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_group_id_seq OWNER TO lastwill_new;

--
-- Name: auth_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE auth_group_id_seq OWNED BY auth_group.id;


--
-- Name: auth_group_permissions; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE auth_group_permissions (
    id integer NOT NULL,
    group_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE auth_group_permissions OWNER TO lastwill_new;

--
-- Name: auth_group_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE auth_group_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_group_permissions_id_seq OWNER TO lastwill_new;

--
-- Name: auth_group_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE auth_group_permissions_id_seq OWNED BY auth_group_permissions.id;


--
-- Name: auth_permission; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE auth_permission (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    content_type_id integer NOT NULL,
    codename character varying(100) NOT NULL
);


ALTER TABLE auth_permission OWNER TO lastwill_new;

--
-- Name: auth_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE auth_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_permission_id_seq OWNER TO lastwill_new;

--
-- Name: auth_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE auth_permission_id_seq OWNED BY auth_permission.id;


--
-- Name: auth_user; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE auth_user (
    id integer NOT NULL,
    password character varying(128) NOT NULL,
    last_login timestamp with time zone,
    is_superuser boolean NOT NULL,
    username character varying(150) NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(30) NOT NULL,
    email character varying(254) NOT NULL,
    is_staff boolean NOT NULL,
    is_active boolean NOT NULL,
    date_joined timestamp with time zone NOT NULL
);


ALTER TABLE auth_user OWNER TO lastwill_new;

--
-- Name: auth_user_groups; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE auth_user_groups (
    id integer NOT NULL,
    user_id integer NOT NULL,
    group_id integer NOT NULL
);


ALTER TABLE auth_user_groups OWNER TO lastwill_new;

--
-- Name: auth_user_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE auth_user_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_user_groups_id_seq OWNER TO lastwill_new;

--
-- Name: auth_user_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE auth_user_groups_id_seq OWNED BY auth_user_groups.id;


--
-- Name: auth_user_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE auth_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_user_id_seq OWNER TO lastwill_new;

--
-- Name: auth_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE auth_user_id_seq OWNED BY auth_user.id;


--
-- Name: auth_user_user_permissions; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE auth_user_user_permissions (
    id integer NOT NULL,
    user_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE auth_user_user_permissions OWNER TO lastwill_new;

--
-- Name: auth_user_user_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE auth_user_user_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE auth_user_user_permissions_id_seq OWNER TO lastwill_new;

--
-- Name: auth_user_user_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE auth_user_user_permissions_id_seq OWNED BY auth_user_user_permissions.id;


--
-- Name: authtoken_token; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE authtoken_token (
    key character varying(40) NOT NULL,
    created timestamp with time zone NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE authtoken_token OWNER TO lastwill_new;

--
-- Name: contracts_contract; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_contract (
    id integer NOT NULL,
    name character varying(200),
    owner_address character varying(50),
    state character varying(63) NOT NULL,
    created_date timestamp with time zone NOT NULL,
    balance numeric(78,0),
    cost numeric(78,0) NOT NULL,
    contract_type integer NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE contracts_contract OWNER TO lastwill_new;

--
-- Name: contracts_contract_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_contract_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_contract_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_contract_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_contract_id_seq OWNED BY contracts_contract.id;


--
-- Name: contracts_contractdetailsdelayedpayment; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_contractdetailsdelayedpayment (
    id integer NOT NULL,
    date timestamp with time zone NOT NULL,
    user_address character varying(50) NOT NULL,
    recepient_address character varying(50) NOT NULL,
    recepient_email character varying(200),
    contract_id integer NOT NULL,
    eth_contract_id integer
);


ALTER TABLE contracts_contractdetailsdelayedpayment OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailsdelayedpayment_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_contractdetailsdelayedpayment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_contractdetailsdelayedpayment_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailsdelayedpayment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_contractdetailsdelayedpayment_id_seq OWNED BY contracts_contractdetailsdelayedpayment.id;


--
-- Name: contracts_contractdetailsico; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_contractdetailsico (
    id integer NOT NULL,
    soft_cap numeric(78,0),
    hard_cap numeric(78,0),
    token_name character varying(512) NOT NULL,
    token_short_name character varying(64) NOT NULL,
    is_transferable_at_once boolean NOT NULL,
    contract_id integer NOT NULL,
    eth_contract_crowdsale_id integer,
    eth_contract_token_id integer,
    admin_address character varying(50) NOT NULL,
    start_date integer NOT NULL,
    stop_date integer NOT NULL,
    decimals integer NOT NULL,
    rate integer NOT NULL,
    platform_as_admin boolean NOT NULL
);


ALTER TABLE contracts_contractdetailsico OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailsico_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_contractdetailsico_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_contractdetailsico_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailsico_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_contractdetailsico_id_seq OWNED BY contracts_contractdetailsico.id;


--
-- Name: contracts_contractdetailslastwill; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_contractdetailslastwill (
    id integer NOT NULL,
    user_address character varying(50),
    check_interval integer NOT NULL,
    active_to timestamp with time zone NOT NULL,
    last_check timestamp with time zone,
    next_check timestamp with time zone,
    contract_id integer NOT NULL,
    eth_contract_id integer
);


ALTER TABLE contracts_contractdetailslastwill OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailslastwill_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_contractdetailslastwill_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_contractdetailslastwill_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailslastwill_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_contractdetailslastwill_id_seq OWNED BY contracts_contractdetailslastwill.id;


--
-- Name: contracts_contractdetailslostkey; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_contractdetailslostkey (
    id integer NOT NULL,
    user_address character varying(50),
    check_interval integer NOT NULL,
    active_to timestamp with time zone NOT NULL,
    last_check timestamp with time zone,
    next_check timestamp with time zone,
    contract_id integer NOT NULL,
    eth_contract_id integer
);


ALTER TABLE contracts_contractdetailslostkey OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailslostkey_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_contractdetailslostkey_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_contractdetailslostkey_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailslostkey_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_contractdetailslostkey_id_seq OWNED BY contracts_contractdetailslostkey.id;


--
-- Name: contracts_contractdetailspizza; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_contractdetailspizza (
    id integer NOT NULL,
    user_address character varying(50) NOT NULL,
    pizzeria_address character varying(50) NOT NULL,
    timeout integer NOT NULL,
    code integer NOT NULL,
    salt character varying(78) NOT NULL,
    pizza_cost numeric(78,0) NOT NULL,
    order_id numeric(50,0) NOT NULL,
    contract_id integer NOT NULL,
    eth_contract_id integer
);


ALTER TABLE contracts_contractdetailspizza OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailspizza_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_contractdetailspizza_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_contractdetailspizza_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_contractdetailspizza_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_contractdetailspizza_id_seq OWNED BY contracts_contractdetailspizza.id;


--
-- Name: contracts_ethcontract; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_ethcontract (
    id integer NOT NULL,
    address character varying(50),
    source_code text NOT NULL,
    bytecode text NOT NULL,
    abi jsonb NOT NULL,
    compiler_version character varying(200)
);


ALTER TABLE contracts_ethcontract OWNER TO lastwill_new;

--
-- Name: contracts_ethcontract_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_ethcontract_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_ethcontract_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_ethcontract_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_ethcontract_id_seq OWNED BY contracts_ethcontract.id;


--
-- Name: contracts_heir; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_heir (
    id integer NOT NULL,
    address character varying(50) NOT NULL,
    percentage integer NOT NULL,
    email character varying(200),
    contract_id integer NOT NULL
);


ALTER TABLE contracts_heir OWNER TO lastwill_new;

--
-- Name: contracts_heir_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_heir_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_heir_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_heir_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_heir_id_seq OWNED BY contracts_heir.id;


--
-- Name: contracts_tokenholder; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE contracts_tokenholder (
    id integer NOT NULL,
    address character varying(50) NOT NULL,
    amount numeric(78,0),
    freeze_date integer,
    contract_id integer NOT NULL,
    name character varying(512)
);


ALTER TABLE contracts_tokenholder OWNER TO lastwill_new;

--
-- Name: contracts_tokenholder_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE contracts_tokenholder_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE contracts_tokenholder_id_seq OWNER TO lastwill_new;

--
-- Name: contracts_tokenholder_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE contracts_tokenholder_id_seq OWNED BY contracts_tokenholder.id;


--
-- Name: django_admin_log; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE django_admin_log (
    id integer NOT NULL,
    action_time timestamp with time zone NOT NULL,
    object_id text,
    object_repr character varying(200) NOT NULL,
    action_flag smallint NOT NULL,
    change_message text NOT NULL,
    content_type_id integer,
    user_id integer NOT NULL,
    CONSTRAINT django_admin_log_action_flag_check CHECK ((action_flag >= 0))
);


ALTER TABLE django_admin_log OWNER TO lastwill_new;

--
-- Name: django_admin_log_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE django_admin_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE django_admin_log_id_seq OWNER TO lastwill_new;

--
-- Name: django_admin_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE django_admin_log_id_seq OWNED BY django_admin_log.id;


--
-- Name: django_content_type; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE django_content_type (
    id integer NOT NULL,
    app_label character varying(100) NOT NULL,
    model character varying(100) NOT NULL
);


ALTER TABLE django_content_type OWNER TO lastwill_new;

--
-- Name: django_content_type_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE django_content_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE django_content_type_id_seq OWNER TO lastwill_new;

--
-- Name: django_content_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE django_content_type_id_seq OWNED BY django_content_type.id;


--
-- Name: django_migrations; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE django_migrations (
    id integer NOT NULL,
    app character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    applied timestamp with time zone NOT NULL
);


ALTER TABLE django_migrations OWNER TO lastwill_new;

--
-- Name: django_migrations_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE django_migrations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE django_migrations_id_seq OWNER TO lastwill_new;

--
-- Name: django_migrations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE django_migrations_id_seq OWNED BY django_migrations.id;


--
-- Name: django_session; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE django_session (
    session_key character varying(40) NOT NULL,
    session_data text NOT NULL,
    expire_date timestamp with time zone NOT NULL
);


ALTER TABLE django_session OWNER TO lastwill_new;

--
-- Name: django_site; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE django_site (
    id integer NOT NULL,
    domain character varying(100) NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE django_site OWNER TO lastwill_new;

--
-- Name: django_site_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE django_site_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE django_site_id_seq OWNER TO lastwill_new;

--
-- Name: django_site_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE django_site_id_seq OWNED BY django_site.id;


--
-- Name: other_sentence; Type: TABLE; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE TABLE other_sentence (
    id integer NOT NULL,
    username character varying(200) NOT NULL,
    email character varying(200) NOT NULL,
    contract_name character varying(200) NOT NULL,
    message text NOT NULL
);


ALTER TABLE other_sentence OWNER TO lastwill_new;

--
-- Name: other_sentence_id_seq; Type: SEQUENCE; Schema: public; Owner: lastwill_new
--

CREATE SEQUENCE other_sentence_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE other_sentence_id_seq OWNER TO lastwill_new;

--
-- Name: other_sentence_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: lastwill_new
--

ALTER SEQUENCE other_sentence_id_seq OWNED BY other_sentence.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY account_emailaddress ALTER COLUMN id SET DEFAULT nextval('account_emailaddress_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY account_emailconfirmation ALTER COLUMN id SET DEFAULT nextval('account_emailconfirmation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_group ALTER COLUMN id SET DEFAULT nextval('auth_group_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_group_permissions ALTER COLUMN id SET DEFAULT nextval('auth_group_permissions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_permission ALTER COLUMN id SET DEFAULT nextval('auth_permission_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_user ALTER COLUMN id SET DEFAULT nextval('auth_user_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_user_groups ALTER COLUMN id SET DEFAULT nextval('auth_user_groups_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_user_user_permissions ALTER COLUMN id SET DEFAULT nextval('auth_user_user_permissions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contract ALTER COLUMN id SET DEFAULT nextval('contracts_contract_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailsdelayedpayment ALTER COLUMN id SET DEFAULT nextval('contracts_contractdetailsdelayedpayment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailsico ALTER COLUMN id SET DEFAULT nextval('contracts_contractdetailsico_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailslastwill ALTER COLUMN id SET DEFAULT nextval('contracts_contractdetailslastwill_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailslostkey ALTER COLUMN id SET DEFAULT nextval('contracts_contractdetailslostkey_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailspizza ALTER COLUMN id SET DEFAULT nextval('contracts_contractdetailspizza_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_ethcontract ALTER COLUMN id SET DEFAULT nextval('contracts_ethcontract_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_heir ALTER COLUMN id SET DEFAULT nextval('contracts_heir_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_tokenholder ALTER COLUMN id SET DEFAULT nextval('contracts_tokenholder_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY django_admin_log ALTER COLUMN id SET DEFAULT nextval('django_admin_log_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY django_content_type ALTER COLUMN id SET DEFAULT nextval('django_content_type_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY django_migrations ALTER COLUMN id SET DEFAULT nextval('django_migrations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY django_site ALTER COLUMN id SET DEFAULT nextval('django_site_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY other_sentence ALTER COLUMN id SET DEFAULT nextval('other_sentence_id_seq'::regclass);


--
-- Name: account_emailaddress_email_key; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY account_emailaddress
    ADD CONSTRAINT account_emailaddress_email_key UNIQUE (email);


--
-- Name: account_emailaddress_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY account_emailaddress
    ADD CONSTRAINT account_emailaddress_pkey PRIMARY KEY (id);


--
-- Name: account_emailconfirmation_key_key; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY account_emailconfirmation
    ADD CONSTRAINT account_emailconfirmation_key_key UNIQUE (key);


--
-- Name: account_emailconfirmation_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY account_emailconfirmation
    ADD CONSTRAINT account_emailconfirmation_pkey PRIMARY KEY (id);


--
-- Name: auth_group_name_key; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_group
    ADD CONSTRAINT auth_group_name_key UNIQUE (name);


--
-- Name: auth_group_permissions_group_id_permission_id_0cd325b0_uniq; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_group_id_permission_id_0cd325b0_uniq UNIQUE (group_id, permission_id);


--
-- Name: auth_group_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_pkey PRIMARY KEY (id);


--
-- Name: auth_group_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_group
    ADD CONSTRAINT auth_group_pkey PRIMARY KEY (id);


--
-- Name: auth_permission_content_type_id_codename_01ab375a_uniq; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_permission
    ADD CONSTRAINT auth_permission_content_type_id_codename_01ab375a_uniq UNIQUE (content_type_id, codename);


--
-- Name: auth_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_permission
    ADD CONSTRAINT auth_permission_pkey PRIMARY KEY (id);


--
-- Name: auth_user_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_user_groups
    ADD CONSTRAINT auth_user_groups_pkey PRIMARY KEY (id);


--
-- Name: auth_user_groups_user_id_group_id_94350c0c_uniq; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_user_groups
    ADD CONSTRAINT auth_user_groups_user_id_group_id_94350c0c_uniq UNIQUE (user_id, group_id);


--
-- Name: auth_user_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_user
    ADD CONSTRAINT auth_user_pkey PRIMARY KEY (id);


--
-- Name: auth_user_user_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permissions_pkey PRIMARY KEY (id);


--
-- Name: auth_user_user_permissions_user_id_permission_id_14a6b632_uniq; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permissions_user_id_permission_id_14a6b632_uniq UNIQUE (user_id, permission_id);


--
-- Name: auth_user_username_key; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY auth_user
    ADD CONSTRAINT auth_user_username_key UNIQUE (username);


--
-- Name: authtoken_token_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY authtoken_token
    ADD CONSTRAINT authtoken_token_pkey PRIMARY KEY (key);


--
-- Name: authtoken_token_user_id_key; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY authtoken_token
    ADD CONSTRAINT authtoken_token_user_id_key UNIQUE (user_id);


--
-- Name: contracts_contract_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_contract
    ADD CONSTRAINT contracts_contract_pkey PRIMARY KEY (id);


--
-- Name: contracts_contractdetailsdelayedpayment_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_contractdetailsdelayedpayment
    ADD CONSTRAINT contracts_contractdetailsdelayedpayment_pkey PRIMARY KEY (id);


--
-- Name: contracts_contractdetailsico_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_contractdetailsico
    ADD CONSTRAINT contracts_contractdetailsico_pkey PRIMARY KEY (id);


--
-- Name: contracts_contractdetailslastwill_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_contractdetailslastwill
    ADD CONSTRAINT contracts_contractdetailslastwill_pkey PRIMARY KEY (id);


--
-- Name: contracts_contractdetailslostkey_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_contractdetailslostkey
    ADD CONSTRAINT contracts_contractdetailslostkey_pkey PRIMARY KEY (id);


--
-- Name: contracts_contractdetailspizza_order_id_key; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_contractdetailspizza
    ADD CONSTRAINT contracts_contractdetailspizza_order_id_key UNIQUE (order_id);


--
-- Name: contracts_contractdetailspizza_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_contractdetailspizza
    ADD CONSTRAINT contracts_contractdetailspizza_pkey PRIMARY KEY (id);


--
-- Name: contracts_ethcontract_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_ethcontract
    ADD CONSTRAINT contracts_ethcontract_pkey PRIMARY KEY (id);


--
-- Name: contracts_heir_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_heir
    ADD CONSTRAINT contracts_heir_pkey PRIMARY KEY (id);


--
-- Name: contracts_tokenholder_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY contracts_tokenholder
    ADD CONSTRAINT contracts_tokenholder_pkey PRIMARY KEY (id);


--
-- Name: django_admin_log_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY django_admin_log
    ADD CONSTRAINT django_admin_log_pkey PRIMARY KEY (id);


--
-- Name: django_content_type_app_label_model_76bd3d3b_uniq; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY django_content_type
    ADD CONSTRAINT django_content_type_app_label_model_76bd3d3b_uniq UNIQUE (app_label, model);


--
-- Name: django_content_type_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY django_content_type
    ADD CONSTRAINT django_content_type_pkey PRIMARY KEY (id);


--
-- Name: django_migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY django_migrations
    ADD CONSTRAINT django_migrations_pkey PRIMARY KEY (id);


--
-- Name: django_session_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY django_session
    ADD CONSTRAINT django_session_pkey PRIMARY KEY (session_key);


--
-- Name: django_site_domain_a2e37b91_uniq; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY django_site
    ADD CONSTRAINT django_site_domain_a2e37b91_uniq UNIQUE (domain);


--
-- Name: django_site_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY django_site
    ADD CONSTRAINT django_site_pkey PRIMARY KEY (id);


--
-- Name: other_sentence_pkey; Type: CONSTRAINT; Schema: public; Owner: lastwill_new; Tablespace: 
--

ALTER TABLE ONLY other_sentence
    ADD CONSTRAINT other_sentence_pkey PRIMARY KEY (id);


--
-- Name: account_emailaddress_email_03be32b2_like; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX account_emailaddress_email_03be32b2_like ON account_emailaddress USING btree (email varchar_pattern_ops);


--
-- Name: account_emailaddress_user_id_2c513194; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX account_emailaddress_user_id_2c513194 ON account_emailaddress USING btree (user_id);


--
-- Name: account_emailconfirmation_email_address_id_5b7f8c58; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX account_emailconfirmation_email_address_id_5b7f8c58 ON account_emailconfirmation USING btree (email_address_id);


--
-- Name: account_emailconfirmation_key_f43612bd_like; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX account_emailconfirmation_key_f43612bd_like ON account_emailconfirmation USING btree (key varchar_pattern_ops);


--
-- Name: auth_group_name_a6ea08ec_like; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_group_name_a6ea08ec_like ON auth_group USING btree (name varchar_pattern_ops);


--
-- Name: auth_group_permissions_group_id_b120cbf9; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_group_permissions_group_id_b120cbf9 ON auth_group_permissions USING btree (group_id);


--
-- Name: auth_group_permissions_permission_id_84c5c92e; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_group_permissions_permission_id_84c5c92e ON auth_group_permissions USING btree (permission_id);


--
-- Name: auth_permission_content_type_id_2f476e4b; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_permission_content_type_id_2f476e4b ON auth_permission USING btree (content_type_id);


--
-- Name: auth_user_groups_group_id_97559544; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_user_groups_group_id_97559544 ON auth_user_groups USING btree (group_id);


--
-- Name: auth_user_groups_user_id_6a12ed8b; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_user_groups_user_id_6a12ed8b ON auth_user_groups USING btree (user_id);


--
-- Name: auth_user_user_permissions_permission_id_1fbb5f2c; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_user_user_permissions_permission_id_1fbb5f2c ON auth_user_user_permissions USING btree (permission_id);


--
-- Name: auth_user_user_permissions_user_id_a95ead1b; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_user_user_permissions_user_id_a95ead1b ON auth_user_user_permissions USING btree (user_id);


--
-- Name: auth_user_username_6821ab7c_like; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX auth_user_username_6821ab7c_like ON auth_user USING btree (username varchar_pattern_ops);


--
-- Name: authtoken_token_key_10f0b77e_like; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX authtoken_token_key_10f0b77e_like ON authtoken_token USING btree (key varchar_pattern_ops);


--
-- Name: contracts_contract_user_id_df2380cc; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contract_user_id_df2380cc ON contracts_contract USING btree (user_id);


--
-- Name: contracts_contractdetailsd_eth_contract_id_040f05c2; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailsd_eth_contract_id_040f05c2 ON contracts_contractdetailsdelayedpayment USING btree (eth_contract_id);


--
-- Name: contracts_contractdetailsdelayedpayment_contract_id_98562be7; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailsdelayedpayment_contract_id_98562be7 ON contracts_contractdetailsdelayedpayment USING btree (contract_id);


--
-- Name: contracts_contractdetailsico_contract_id_d874f77c; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailsico_contract_id_d874f77c ON contracts_contractdetailsico USING btree (contract_id);


--
-- Name: contracts_contractdetailsico_eth_contract_crowdsale_id_194897ed; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailsico_eth_contract_crowdsale_id_194897ed ON contracts_contractdetailsico USING btree (eth_contract_crowdsale_id);


--
-- Name: contracts_contractdetailsico_eth_contract_token_id_c097461b; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailsico_eth_contract_token_id_c097461b ON contracts_contractdetailsico USING btree (eth_contract_token_id);


--
-- Name: contracts_contractdetailslastwill_contract_id_bc08c2cb; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailslastwill_contract_id_bc08c2cb ON contracts_contractdetailslastwill USING btree (contract_id);


--
-- Name: contracts_contractdetailslastwill_eth_contract_id_26adcd1c; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailslastwill_eth_contract_id_26adcd1c ON contracts_contractdetailslastwill USING btree (eth_contract_id);


--
-- Name: contracts_contractdetailslostkey_contract_id_53107287; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailslostkey_contract_id_53107287 ON contracts_contractdetailslostkey USING btree (contract_id);


--
-- Name: contracts_contractdetailslostkey_eth_contract_id_f5c728a4; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailslostkey_eth_contract_id_f5c728a4 ON contracts_contractdetailslostkey USING btree (eth_contract_id);


--
-- Name: contracts_contractdetailspizza_contract_id_88c756e6; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailspizza_contract_id_88c756e6 ON contracts_contractdetailspizza USING btree (contract_id);


--
-- Name: contracts_contractdetailspizza_eth_contract_id_a8f793a6; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_contractdetailspizza_eth_contract_id_a8f793a6 ON contracts_contractdetailspizza USING btree (eth_contract_id);


--
-- Name: contracts_heir_contract_id_573129fe; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_heir_contract_id_573129fe ON contracts_heir USING btree (contract_id);


--
-- Name: contracts_tokenholder_contract_id_24a63df2; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX contracts_tokenholder_contract_id_24a63df2 ON contracts_tokenholder USING btree (contract_id);


--
-- Name: django_admin_log_content_type_id_c4bce8eb; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX django_admin_log_content_type_id_c4bce8eb ON django_admin_log USING btree (content_type_id);


--
-- Name: django_admin_log_user_id_c564eba6; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX django_admin_log_user_id_c564eba6 ON django_admin_log USING btree (user_id);


--
-- Name: django_session_expire_date_a5c62663; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX django_session_expire_date_a5c62663 ON django_session USING btree (expire_date);


--
-- Name: django_session_session_key_c0390e0f_like; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX django_session_session_key_c0390e0f_like ON django_session USING btree (session_key varchar_pattern_ops);


--
-- Name: django_site_domain_a2e37b91_like; Type: INDEX; Schema: public; Owner: lastwill_new; Tablespace: 
--

CREATE INDEX django_site_domain_a2e37b91_like ON django_site USING btree (domain varchar_pattern_ops);


--
-- Name: account_emailaddress_user_id_2c513194_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY account_emailaddress
    ADD CONSTRAINT account_emailaddress_user_id_2c513194_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: account_emailconfirm_email_address_id_5b7f8c58_fk_account_e; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY account_emailconfirmation
    ADD CONSTRAINT account_emailconfirm_email_address_id_5b7f8c58_fk_account_e FOREIGN KEY (email_address_id) REFERENCES account_emailaddress(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_group_permissio_permission_id_84c5c92e_fk_auth_perm; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permissio_permission_id_84c5c92e_fk_auth_perm FOREIGN KEY (permission_id) REFERENCES auth_permission(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_group_permissions_group_id_b120cbf9_fk_auth_group_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_group_id_b120cbf9_fk_auth_group_id FOREIGN KEY (group_id) REFERENCES auth_group(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_permission_content_type_id_2f476e4b_fk_django_co; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_permission
    ADD CONSTRAINT auth_permission_content_type_id_2f476e4b_fk_django_co FOREIGN KEY (content_type_id) REFERENCES django_content_type(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_groups_group_id_97559544_fk_auth_group_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_user_groups
    ADD CONSTRAINT auth_user_groups_group_id_97559544_fk_auth_group_id FOREIGN KEY (group_id) REFERENCES auth_group(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_groups_user_id_6a12ed8b_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_user_groups
    ADD CONSTRAINT auth_user_groups_user_id_6a12ed8b_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm FOREIGN KEY (permission_id) REFERENCES auth_permission(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: authtoken_token_user_id_35299eff_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY authtoken_token
    ADD CONSTRAINT authtoken_token_user_id_35299eff_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contract_user_id_df2380cc_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contract
    ADD CONSTRAINT contracts_contract_user_id_df2380cc_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_contract_id_53107287_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailslostkey
    ADD CONSTRAINT contracts_contractde_contract_id_53107287_fk_contracts FOREIGN KEY (contract_id) REFERENCES contracts_contract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_contract_id_88c756e6_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailspizza
    ADD CONSTRAINT contracts_contractde_contract_id_88c756e6_fk_contracts FOREIGN KEY (contract_id) REFERENCES contracts_contract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_contract_id_98562be7_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailsdelayedpayment
    ADD CONSTRAINT contracts_contractde_contract_id_98562be7_fk_contracts FOREIGN KEY (contract_id) REFERENCES contracts_contract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_contract_id_bc08c2cb_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailslastwill
    ADD CONSTRAINT contracts_contractde_contract_id_bc08c2cb_fk_contracts FOREIGN KEY (contract_id) REFERENCES contracts_contract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_contract_id_d874f77c_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailsico
    ADD CONSTRAINT contracts_contractde_contract_id_d874f77c_fk_contracts FOREIGN KEY (contract_id) REFERENCES contracts_contract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_eth_contract_crowdsa_194897ed_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailsico
    ADD CONSTRAINT contracts_contractde_eth_contract_crowdsa_194897ed_fk_contracts FOREIGN KEY (eth_contract_crowdsale_id) REFERENCES contracts_ethcontract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_eth_contract_id_040f05c2_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailsdelayedpayment
    ADD CONSTRAINT contracts_contractde_eth_contract_id_040f05c2_fk_contracts FOREIGN KEY (eth_contract_id) REFERENCES contracts_ethcontract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_eth_contract_id_26adcd1c_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailslastwill
    ADD CONSTRAINT contracts_contractde_eth_contract_id_26adcd1c_fk_contracts FOREIGN KEY (eth_contract_id) REFERENCES contracts_ethcontract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_eth_contract_id_a8f793a6_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailspizza
    ADD CONSTRAINT contracts_contractde_eth_contract_id_a8f793a6_fk_contracts FOREIGN KEY (eth_contract_id) REFERENCES contracts_ethcontract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_eth_contract_id_f5c728a4_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailslostkey
    ADD CONSTRAINT contracts_contractde_eth_contract_id_f5c728a4_fk_contracts FOREIGN KEY (eth_contract_id) REFERENCES contracts_ethcontract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_contractde_eth_contract_token_i_c097461b_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_contractdetailsico
    ADD CONSTRAINT contracts_contractde_eth_contract_token_i_c097461b_fk_contracts FOREIGN KEY (eth_contract_token_id) REFERENCES contracts_ethcontract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_heir_contract_id_573129fe_fk_contracts_contract_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_heir
    ADD CONSTRAINT contracts_heir_contract_id_573129fe_fk_contracts_contract_id FOREIGN KEY (contract_id) REFERENCES contracts_contract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: contracts_tokenholde_contract_id_24a63df2_fk_contracts; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY contracts_tokenholder
    ADD CONSTRAINT contracts_tokenholde_contract_id_24a63df2_fk_contracts FOREIGN KEY (contract_id) REFERENCES contracts_contract(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: django_admin_log_content_type_id_c4bce8eb_fk_django_co; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY django_admin_log
    ADD CONSTRAINT django_admin_log_content_type_id_c4bce8eb_fk_django_co FOREIGN KEY (content_type_id) REFERENCES django_content_type(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: django_admin_log_user_id_c564eba6_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: lastwill_new
--

ALTER TABLE ONLY django_admin_log
    ADD CONSTRAINT django_admin_log_user_id_c564eba6_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

