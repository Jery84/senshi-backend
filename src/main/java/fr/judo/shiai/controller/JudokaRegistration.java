package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Club;
import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.persistence.ClubRepository;
import fr.judo.shiai.persistence.JudokaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
public class JudokaRegistration {

    @Autowired
    private JudokaRepository judokaRepository;

    @Autowired
    private ClubRepository clubRepository;

    @GetMapping("/clubs")
    public List<Club> allClubs() {
        return clubRepository.getAllClubs();
    }

    @GetMapping("/judokas")
    public List<Judoka> allJudokas() {
        return judokaRepository.getAllEligibleJudokas(false);
    }

    @PostMapping("/judoka")
    public Judoka newJudoka(@RequestBody final Judoka judoka) {
        judoka.setId(judokaRepository.createJudoka(judoka));
        return judoka;
    }

    @PutMapping("/judoka/{id}")
    public void updateJudoka(@RequestBody final Judoka judoka) {
        judokaRepository.updateJudoka(judoka);
    }

    @DeleteMapping("/judoka/{id}")
    public void deleteJudoka(@RequestBody final Judoka judoka) {
        judokaRepository.deleteJudoka(judoka);
    }
}
