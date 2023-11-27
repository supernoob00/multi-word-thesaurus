BEGIN TRANSACTION;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
	user_id serial NOT NULL,
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(50),
	role varchar(20),
	CONSTRAINT pk_users PRIMARY KEY (user_id),
	CONSTRAINT uq_username UNIQUE (username)
);

INSERT INTO users (username,password_hash,role) VALUES ('user1','user1','ROLE_USER'); -- 1
INSERT INTO users (username,password_hash,role) VALUES ('user2','user2','ROLE_USER'); -- 2
INSERT INTO users (username,password_hash,role) VALUES ('user3','user3','ROLE_USER'); -- 3

DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
    transaction_id serial NOT NULL,
    amount bigint NOT NULL,
    transaction_type varchar(20) NOT NULL,
    status varchar(20) NOT NULL,
    sender_id integer not null,
    receiver_id integer not null,

    CONSTRAINT pk_transactions PRIMARY KEY (transaction_id),
    CONSTRAINT fk_transactions_sender foreign key (sender_id) REFERENCES users (user_id),
    CONSTRAINT fk_transactions_receiver foreign key (receiver_id) REFERENCES users (user_id),
    CONSTRAINT sender_receiver_id_different CHECK (sender_id != receiver_id),
    CONSTRAINT amount_greater_zero CHECK (amount > 0),
    CONSTRAINT valid_status CHECK (status ilike 'approved' or status ilike 'pending' or status ilike 'rejected'),
    CONSTRAINT valid_transaction_type CHECK (transaction_type ilike 'sending' or transaction_type ilike 'request')
);

INSERT INTO transactions (amount, transaction_type, status, sender_id, receiver_id)
    VALUES (50, 'sending', 'approved', 1, 2);
INSERT INTO transactions (amount, transaction_type, status, sender_id, receiver_id)
    VALUES (100, 'request', 'pending', 2, 1);

COMMIT TRANSACTION;

ALTER SEQUENCE transactions_transaction_id_seq RESTART WITH 3;
