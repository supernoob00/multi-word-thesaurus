DROP TABLE IF EXISTS word_synonyms;
DROP TABLE IF EXISTS words;

CREATE TABLE IF NOT EXISTS words (
	word varchar(30) PRIMARY KEY 
);

CREATE TABLE IF NOT EXISTS word_synonyms (
	word varchar(30),
	synonym varchar(30),
	PRIMARY KEY(word, synonym),
	FOREIGN KEY (word) REFERENCES words (word)
);