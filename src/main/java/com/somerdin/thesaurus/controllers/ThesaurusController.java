package com.somerdin.thesaurus.controllers;

import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.exception.DaoException;
import com.somerdin.thesaurus.model.SynonymList;
import com.somerdin.thesaurus.model.WordsDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/words")
public class ThesaurusController {

    private WordDao wordDao;

    public ThesaurusController(WordDao wordDao) {
        this.wordDao = wordDao;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "", method = RequestMethod.POST)
    public SynonymList add(@Valid @RequestBody WordsDto words) {
        SynonymList synonyms = new SynonymList();
        synonyms.setWords(words.getWords());

        return null;
    }
}
