package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.PoolDispatchingSolution;
import fr.judo.shiai.persistence.JudokaRepository;
import fr.judo.shiai.solver.SenshiSolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class Pool {

    private static final int MAX_JUDOKAS_PER_POOL = 4;

    @Autowired
    private JudokaRepository judokaRepository;

    @Autowired
    private SenshiSolver senshiSolver;


    @GetMapping("/pools")
    public List<Judoka> allClubs() {
        //return clubRepository.getAllClubs();
        return new ArrayList<>();
    }


    @PostMapping("/pools")
    public void computeAllPools() {
        try {
            // Retrieve judokas
            List<Judoka> judokas = judokaRepository.getAllJudokas();
            PoolDispatchingSolution poolDispatchingSolution = new PoolDispatchingSolution();
            List<fr.judo.shiai.domain.Pool> poolList = new ArrayList<>();
            for (int i = 0; i < judokas.size() / MAX_JUDOKAS_PER_POOL + 1; i++) {
                fr.judo.shiai.domain.Pool pool = new fr.judo.shiai.domain.Pool();
                pool.setId(Long.valueOf(i));
                poolList.add(pool);
            }
            poolDispatchingSolution.setPoolList(poolList);
            poolDispatchingSolution.setJudokaList(judokas);
            senshiSolver.solve(poolDispatchingSolution);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
