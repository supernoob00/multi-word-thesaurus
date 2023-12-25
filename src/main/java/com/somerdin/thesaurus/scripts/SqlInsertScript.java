package com.somerdin.thesaurus.scripts;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class SqlInsertScript {
    public static final String TEXT_FILE = "mobythes.txt";
    public static final String OUT_FILE = "database/words_table_data.sql";
    public static final String OUT_FILE_2 = "database/synonyms_table_data.sql";

    public static final int MAX_WORD_LENGTH = 50;

    // TODO: FAILS IF DUPLICATES ARE AT LAST LINES OF FILE
    private void createWordsScript() throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(TEXT_FILE));
             PrintWriter out = new PrintWriter(OUT_FILE)) {
            Set<String> alreadyRead = new HashSet<>();

            out.write("INSERT INTO words (word) VALUES ");

            String word = nextWord(in);
            while (word != null) {
                alreadyRead.add(word);

                out.append("('");
                out.append(putEscape(word));

                while ((word = nextWord(in)) != null
                        && alreadyRead.contains(word))
                    ;

                out.append(word == null ? "');" : "'),\n");
            }
        }
    }

    private void createSynonymsTable() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(TEXT_FILE));
             BufferedWriter out = new BufferedWriter(new FileWriter(OUT_FILE_2))) {

            String line = reader.readLine();
            String next = "";

            out.append("INSERT INTO word_synonyms (word, synonym) VALUES ");
            do {
                    next = reader.readLine();

                    String[] words = line.split(",");
                    if (words[0].length() > MAX_WORD_LENGTH) {
                        words[0] = line.substring(0, MAX_WORD_LENGTH);
                    }
                    for (int j = 1; j < words.length; j++) {
                        if (words[j].equals(words[0])) {
                            continue;
                        }

                        out.append("('");
                        out.append(words[0]);
                        out.append("', '");
                        out.append(putEscape(words[j]));

                        if (j == words.length - 1 && next == null) {
                            out.append("')\n");
                        } else {
                            out.append("'),\n");
                        }
                    }
                    line = next;
            } while (next != null);

            // some of the words in the text file have duplicate synonyms,
            // so this will just skip adding them to the table
            out.append("ON CONFLICT ON CONSTRAINT pk_word_synonym DO " +
                    "NOTHING;");
        }
    }

    /* gets next word from reader while adhering to 50-character limit */
    private String nextWord(BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();

        int c;
        while ((c = in.read()) != -1 && c != ',' && c != '\n' && c != '\r') {
            sb.append((char) c);
        }

        String word = sb.toString().trim();

        if (word.length() > MAX_WORD_LENGTH) {
            word = sb.substring(MAX_WORD_LENGTH);
        }

        if (word.isEmpty() && c == -1) {
            return null;
        }
        return word;
    }

    /* puts escape character into string for apostrophe */
    // TODO: fails for multiple apostrophes
    private String putEscape(String word) {
        int i = word.indexOf('\'');
        if (i != -1) {
            word = word.substring(0, i) + "'" + word.substring(i);
            System.out.println(word);
        }
        return word;
    }

    public static void main(String[] args) {
        SqlInsertScript script = new SqlInsertScript();
        try {
            script.createWordsScript();
            script.createSynonymsTable();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
