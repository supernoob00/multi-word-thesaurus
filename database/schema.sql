BEGIN TRANSACTION;

DROP TABLE IF EXISTS words, word_synonyms CASCADE;

CREATE TABLE words (
    word varchar(50) PRIMARY KEY
);

CREATE TABLE word_synonyms (
    word varchar(50) REFERENCES words (word) NOT NULL,
    synonym varchar(50) REFERENCES words (word) NOT NULL,

    CONSTRAINT pk_word_synonym PRIMARY KEY (word, synonym),
    CONSTRAINT synonym_different_from_word CHECK (word != synonym)
);

COMMIT TRANSACTION;