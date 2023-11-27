package com.somerdin.thesaurus.util;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DatabaseInsertUtil {
    public static final String TEXT_FILE = "mobythes.txt";
    public static final int MAX_WORD_LENGTH = 50;

    private final JdbcTemplate template;

    private DatabaseInsertUtil() {
        template = new JdbcTemplate(SqlDataSourceUtil.getDataSource());
    }

    // TODO: FAILS IF DUPLICATES ARE AT LAST LINES OF FILE
    private void createWordTable() throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(TEXT_FILE));) {
            int batchAmount = 100_000;

            String word = nextWord(in);
            String next = "";
            do {
                StringBuilder sql = new StringBuilder("INSERT INTO words (word) VALUES ");
                for (int i = 0; i < batchAmount && (next = nextWord(in)) != null; i++) {
                    sql.append("('");
                    sql.append(word);
                    sql.append("'),\n");

                    while (word.equals(next)) {
                        next = nextWord(in);
                    }
                    word = next;
                }
                sql.append("('");
                sql.append(word);
                sql.append("');");
                template.update(sql.toString());
            } while (next != null);
        }
    }

    private void createSynonymsTable() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE));) {
            int batchAmount = 100_000;

            String line = reader.readLine();
            String next = "";
            do {
                StringBuilder sql = new StringBuilder("INSERT INTO " +
                        "word_synonyms" +
                        " (word, synonym) VALUES ");
                for (int i = 0; i < batchAmount && next != null; i++) {
                    next = reader.readLine();

                    String[] words = line.split(",");
                    if (words[0].length() > MAX_WORD_LENGTH) {
                        words[0] = line.substring(0, MAX_WORD_LENGTH);
                    }
                    for (int j = 1; j < words.length; j++) {
                        sql.append("('");
                        sql.append(words[0]);
                        sql.append("', '");
                        sql.append(putEscape(words[j]));

                        if (j == words.length - 1 && next == null) {
                            sql.append("')\n");
                        } else {
                            sql.append("'),\n");
                        }
                    }
                    line = next;
                }
                sql.append("ON CONFLICT ON CONSTRAINT word_synonyms_pkey DO " +
                        "NOTHING;");
                template.update(sql.toString());
            } while (next != null);
        }
    }

    private String nextWord(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            return null;
        }
        String word = line.substring(0, line.indexOf(','));
        if (word.length() > MAX_WORD_LENGTH) {
            word = line.substring(0, MAX_WORD_LENGTH);
        }
        return word;
    }

    private String putEscape(String word) {
        int i = word.indexOf('\'');
        if (i != -1) {
            word = word.substring(0, i) + "''" + word.substring(i + 1);
        }
        return word;
    }

    public static void main(String[] args) {
        DatabaseInsertUtil db = new DatabaseInsertUtil();
        try {
            db.createSynonymsTable();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
