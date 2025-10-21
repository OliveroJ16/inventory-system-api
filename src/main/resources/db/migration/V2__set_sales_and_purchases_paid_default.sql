-- ==========================================================
-- V2__set_sales_and_purchases_paid_default.sql
-- Updates the sales and purchases tables so that the status
-- is always 'PAID' by default.
-- ==========================================================

CREATE TYPE public.payment_type AS ENUM (
    'CASH',
    'YAPPY',
    'TRANSFER'
);

CREATE TYPE public.sale_status AS ENUM (
    'PENDING',
    'PAID'
);

CREATE TYPE public.token_type AS ENUM (
    'BEARER',
    'REFRESH'
);

CREATE TYPE public.user_role AS ENUM (
    'ADMIN',
    'CASHIER'
);

-- ==========================================================
-- TABLES
-- ==========================================================

CREATE TABLE public.articles (
    id_article uuid NOT NULL,
    name character varying(50) NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    stock integer NOT NULL,
    description text,
    unit_of_measurement character varying(50) NOT NULL,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status boolean NOT NULL,
    image_url character varying(300),
    id_category uuid NOT NULL,
    content numeric(10,2) NOT NULL
);

CREATE TABLE public.categories (
    id_category uuid NOT NULL,
    name character varying(100) NOT NULL,
    creation_date timestamp without time zone NOT NULL,
    status boolean NOT NULL
);

CREATE TABLE public.customers (
    id_customer uuid NOT NULL,
    name character varying(45) NOT NULL,
    last_name character varying(45) NOT NULL,
    phone character varying(20) NOT NULL,
    email character varying(100),
    address character varying(100),
    registration_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE public.purchase_details (
    id_purchase_detail uuid NOT NULL,
    quantity integer NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    subtotal numeric(10,2) GENERATED ALWAYS AS (((quantity)::numeric * unit_price)) STORED,
    id_purchase uuid NOT NULL,
    id_article uuid NOT NULL
);

CREATE TABLE public.purchase_payments (
    id_purchase_payment uuid NOT NULL,
    payment_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    amount_paid numeric(10,2) NOT NULL,
    payment_type public.payment_type NOT NULL,
    id_purchase uuid NOT NULL
);

CREATE TABLE public.purchases (
    id_purchase uuid NOT NULL,
    purchase_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total_amount numeric(10,2) NOT NULL,
    status public.sale_status DEFAULT 'PAID'::public.sale_status NOT NULL,
    id_supplier uuid NOT NULL,
    id_user uuid NOT NULL,
    CONSTRAINT chk_purchases_status_paid CHECK (status = 'PAID')
);

CREATE TABLE public.sale_details (
    id_sale_detail uuid NOT NULL,
    quantity integer NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    id_sale uuid NOT NULL,
    id_article uuid NOT NULL,
    subtotal numeric(10,2) GENERATED ALWAYS AS (((quantity)::numeric * unit_price)) STORED
);

CREATE TABLE public.sale_payments (
    id_sale_payment uuid NOT NULL,
    payment_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    amount_paid numeric(10,2) NOT NULL,
    payment_type public.payment_type NOT NULL,
    id_sale uuid NOT NULL
);

CREATE TABLE public.sales (
    id_sale uuid NOT NULL,
    date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    total_sale numeric(10,2) NOT NULL,
    status public.sale_status DEFAULT 'PAID'::public.sale_status NOT NULL,
    id_customer uuid,
    id_user uuid NOT NULL,
    CONSTRAINT chk_sales_status_paid CHECK (status = 'PAID')
);

CREATE TABLE public.suppliers (
    id_supplier uuid NOT NULL,
    full_name character varying(150) NOT NULL,
    phone character varying(20) NOT NULL,
    email character varying(100) NOT NULL,
    address character varying(100),
    registration_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status boolean NOT NULL
);

CREATE TABLE public.tokens (
    id_token uuid NOT NULL,
    token text NOT NULL,
    token_type public.token_type DEFAULT 'BEARER'::public.token_type NOT NULL,
    expired boolean DEFAULT false NOT NULL,
    revoked boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    id_user uuid NOT NULL
);

CREATE TABLE public.users (
    id_user uuid NOT NULL,
    user_name character varying(75) NOT NULL,
    full_name character varying(150) NOT NULL,
    email character varying(100) NOT NULL,
    role public.user_role NOT NULL,
    password character varying(250) NOT NULL,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status boolean NOT NULL
);

-- ==========================================================
-- PRIMARY KEYS, UNIQUE CONSTRAINTS, FOREIGN KEYS
-- ==========================================================

ALTER TABLE ONLY public.articles ADD CONSTRAINT articles_pkey PRIMARY KEY (id_article);
ALTER TABLE ONLY public.categories ADD CONSTRAINT categories_pkey PRIMARY KEY (id_category);
ALTER TABLE ONLY public.customers ADD CONSTRAINT customers_pkey PRIMARY KEY (id_customer);
ALTER TABLE ONLY public.purchase_details ADD CONSTRAINT purchase_details_pkey PRIMARY KEY (id_purchase_detail);
ALTER TABLE ONLY public.purchase_payments ADD CONSTRAINT purchase_payments_pkey PRIMARY KEY (id_purchase_payment);
ALTER TABLE ONLY public.purchases ADD CONSTRAINT purchases_pkey PRIMARY KEY (id_purchase);
ALTER TABLE ONLY public.sale_details ADD CONSTRAINT sale_details_pkey PRIMARY KEY (id_sale_detail);
ALTER TABLE ONLY public.sale_payments ADD CONSTRAINT sale_payments_pkey PRIMARY KEY (id_sale_payment);
ALTER TABLE ONLY public.sales ADD CONSTRAINT sales_pkey PRIMARY KEY (id_sale);
ALTER TABLE ONLY public.suppliers ADD CONSTRAINT suppliers_pkey PRIMARY KEY (id_supplier);
ALTER TABLE ONLY public.tokens ADD CONSTRAINT tokens_pkey PRIMARY KEY (id_token);
ALTER TABLE ONLY public.users ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);

ALTER TABLE ONLY public.customers ADD CONSTRAINT unique_customers_email UNIQUE (email);
ALTER TABLE ONLY public.customers ADD CONSTRAINT unique_customers_phone UNIQUE (phone);
ALTER TABLE ONLY public.users ADD CONSTRAINT unique_email UNIQUE (email);
ALTER TABLE ONLY public.suppliers ADD CONSTRAINT unique_suppliers_email UNIQUE (email);
ALTER TABLE ONLY public.suppliers ADD CONSTRAINT unique_suppliers_phone UNIQUE (phone);
ALTER TABLE ONLY public.users ADD CONSTRAINT unique_users_username UNIQUE (user_name);

ALTER TABLE ONLY public.articles
    ADD CONSTRAINT articles_id_category_fkey FOREIGN KEY (id_category) REFERENCES public.categories(id_category) ON DELETE RESTRICT;
ALTER TABLE ONLY public.purchase_details
    ADD CONSTRAINT purchase_details_id_article_fkey FOREIGN KEY (id_article) REFERENCES public.articles(id_article) ON DELETE RESTRICT;
ALTER TABLE ONLY public.purchase_details
    ADD CONSTRAINT purchase_details_id_purchase_fkey FOREIGN KEY (id_purchase) REFERENCES public.purchases(id_purchase) ON DELETE RESTRICT;
ALTER TABLE ONLY public.purchase_payments
    ADD CONSTRAINT purchase_payments_id_purchase_fkey FOREIGN KEY (id_purchase) REFERENCES public.purchases(id_purchase) ON DELETE RESTRICT;
ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT purchases_id_supplier_fkey FOREIGN KEY (id_supplier) REFERENCES public.suppliers(id_supplier) ON DELETE RESTRICT;
ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT purchases_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id_user) ON DELETE RESTRICT;
ALTER TABLE ONLY public.sale_details
    ADD CONSTRAINT sale_details_id_article_fkey FOREIGN KEY (id_article) REFERENCES public.articles(id_article) ON DELETE RESTRICT;
ALTER TABLE ONLY public.sale_details
    ADD CONSTRAINT sale_details_id_sale_fkey FOREIGN KEY (id_sale) REFERENCES public.sales(id_sale) ON DELETE RESTRICT;
ALTER TABLE ONLY public.sale_payments
    ADD CONSTRAINT sale_payments_id_sale_fkey FOREIGN KEY (id_sale) REFERENCES public.sales(id_sale) ON DELETE RESTRICT;
ALTER TABLE ONLY public.sales
    ADD CONSTRAINT sales_id_customer_fkey FOREIGN KEY (id_customer) REFERENCES public.customers(id_customer) ON DELETE RESTRICT;
ALTER TABLE ONLY public.sales
    ADD CONSTRAINT sales_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id_user) ON DELETE RESTRICT;
ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT tokens_id_user_fkey FOREIGN KEY (id_user) REFERENCES public.users(id_user) ON DELETE CASCADE;
