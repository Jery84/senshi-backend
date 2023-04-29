package fr.judo.shiai.controller;

import fr.judo.shiai.controller.request.ResetRequest;
import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.dto.JudokaDto;
import fr.judo.shiai.mappers.JudokaMapper;
import fr.judo.shiai.repository.JudokaRepository;
import fr.judo.shiai.service.LoaderService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Slf4j
@CrossOrigin
@RestController
public class JudokaController {

    @Autowired
    private JudokaRepository judokaRepository;

    @Autowired
    private JudokaMapper judokaMapper;

    @Autowired
    private LoaderService loaderService;

    @GetMapping("judoka")
    public Iterable<JudokaDto> findAll() {
        return judokaMapper.toDto(judokaRepository.findAll());
    }

    @PostMapping(path = "judoka",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Judoka> create(@RequestBody JudokaDto judokaDto) {
        Judoka created = judokaRepository.save(judokaMapper.toEntity(judokaDto));
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping(path = "judoka/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Judoka> update(@PathVariable Integer id, @RequestBody JudokaDto judokaDto) {

        Judoka updated = judokaRepository.findById(id).get();
        updated.setWeight(judokaDto.getWeight());
        updated.setPresent(judokaDto.getPresent());
        judokaRepository.save(updated);

        return new ResponseEntity<>(updated, HttpStatus.ACCEPTED);
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

    @Transactional
    @PostMapping(path = "judoka/reset",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<JudokaDto> resetPresence(@RequestBody ResetRequest resetRequest) {
        if(resetRequest.getCategoryName().isEmpty()) {
            judokaRepository.resetPresence();
        } else {
            judokaRepository.resetPresence(resetRequest.getCategoryName());
        }
        return judokaMapper.toDto(judokaRepository.findAll());
    }

    @PostMapping(path = "judoka/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<JudokaDto> importFromDatFile(@RequestParam("importFile") MultipartFile importFile, @RequestParam("clubNames[]") String[] clubNames, @RequestParam("minYear") Integer minYear, @RequestParam("maxYear") Integer maxYear) {
        loaderService.loadFromDatFile(importFile, Arrays.stream(clubNames).toList(), minYear, maxYear);
        return judokaMapper.toDto(judokaRepository.findAll());
    }

}
