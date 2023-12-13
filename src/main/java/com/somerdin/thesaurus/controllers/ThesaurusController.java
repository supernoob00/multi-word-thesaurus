package com.somerdin.thesaurus.controllers;

import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.model.SynonymList;
import com.somerdin.thesaurus.model.WordsDto;
import com.somerdin.thesaurus.structures.WordConnectionFinder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public SynonymList getAllSynonyms(@Valid @RequestBody WordsDto words) {
        WordConnectionFinder finder = new WordConnectionFinder(wordDao);
        finder.addWords(words.getWords());

        SynonymList synonyms = new SynonymList();
        synonyms.setWords(words.getWords());
        synonyms.setSynonyms(finder.getConnectedWords());
        return synonyms;
    }
}
