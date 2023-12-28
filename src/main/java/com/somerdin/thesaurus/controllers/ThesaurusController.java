package com.somerdin.thesaurus.controllers;

import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.model.MultiSynonymList;
import com.somerdin.thesaurus.model.SynonymList;
import com.somerdin.thesaurus.model.WordsDto;
import com.somerdin.thesaurus.structures.WordConnectionFinder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collection;

@CrossOrigin
@RestController
@RequestMapping("/words")
public class ThesaurusController {
    private final WordDao wordDao;

    public ThesaurusController(WordDao wordDao) {
        this.wordDao = wordDao;
    }

//    @ResponseStatus(HttpStatus.ACCEPTED)
//    @RequestMapping(path = "", method = RequestMethod.POST)
//    public MultiSynonymList getAllSynonyms(@Valid @RequestBody WordsDto words) {
//        WordConnectionFinder finder = new WordConnectionFinder(wordDao);
//        finder.addWords(words.getWords());
//
//        MultiSynonymList synonyms = new MultiSynonymList();
//        synonyms.setWords(words.getWords());
//        synonyms.setSynonyms(finder.getConnectedWords());
//        return synonyms;
//    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/{word}", method = RequestMethod.GET)
    public SynonymList getSynonyms(@PathVariable String word) {
        Collection<String> synonyms = wordDao.getWordSynonyms(word);
        return new SynonymList(word, synonyms);
    }
}
