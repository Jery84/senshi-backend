package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Pool;
import fr.judo.shiai.persistence.PoolRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@CrossOrigin
@RestController
public class PoolController {

    @Autowired
    private PoolRepository poolRepository;

    /**
     * Get the list of all the pool stored in db
     * @return a list of {@link Pool}s
     */
    @GetMapping("/pools")
    public List<Pool> allPools() {
        return poolRepository.getAllPools();
    }

}
