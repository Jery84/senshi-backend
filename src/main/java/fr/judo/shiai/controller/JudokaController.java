package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.repository.JudokaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
public class JudokaController {

    @Autowired
    private JudokaRepository judokaRepository;

    @GetMapping("judoka")
    public Iterable<Judoka> findAll() {
        return judokaRepository.findAll();
    }

    @DeleteMapping("judoka/{id}")
    public String delete(@PathVariable Integer id) {
        try {
            judokaRepository.deleteById(id);
            return "OK";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "KO";
        }
    }
}
