package com.somerdin.thesaurus.controllers;

import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.model.WordsDto;
import com.somerdin.thesaurus.structures.WordGraph;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/wordgraph")
public class GraphController {
    private WordDao dao;

    public GraphController(WordDao dao) {
        this.dao = dao;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "")
    @CrossOrigin
    public String getWordGraph(@Valid @RequestBody WordsDto words) {
        WordGraph wordGraph = new WordGraph(dao);
        for (String word : words.getWords()) {
            wordGraph.addWord(word, WordGraph.DEFAULT_SEARCH_DEPTH);
        }
        return WordGraph.toDotFormat(wordGraph);
    }
}
