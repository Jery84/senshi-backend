package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.dto.JudokaDto;
import fr.judo.shiai.mappers.JudokaMapper;
import fr.judo.shiai.repository.CategoryRepository;
import fr.judo.shiai.repository.JudokaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
public class JudokaController {

    @Autowired
    private JudokaRepository judokaRepository;

    @Autowired
    private JudokaMapper judokaMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("judoka")
    public Iterable<Judoka> findAll() {
        return judokaRepository.findAll();
    }

    @PostMapping(path = "judoka",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Judoka> create(@RequestBody JudokaDto judokaDto) {
        Judoka created = judokaRepository.save(judokaMapper.judokaDtoToJudoka(judokaDto, categoryRepository));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("judoka/{id}")
    public String delete(@PathVariable Integer id) {
        try {
            judokaRepository.deleteById(id);
            return "OK";
        } catch (Exception e) {
            return "KO";
        }
    }

}
