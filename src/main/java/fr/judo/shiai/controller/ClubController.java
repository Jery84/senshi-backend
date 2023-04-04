package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Club;
import fr.judo.shiai.repository.ClubRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class ClubController {

    @Autowired
    private ClubRepository clubRepository;

    @GetMapping("club")
    public Iterable<Club> findAll() {
        return clubRepository.findAll();
    }
}
