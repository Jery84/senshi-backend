package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.Pool;
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
public class PoolController {

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
    public List<Judoka> computeAllPools() {
        PoolDispatchingSolution poolDispatchingSolution = new PoolDispatchingSolution();
        try {
            // Retrieve judokas
            List<Judoka> judokas = judokaRepository.getAllJudokas(true);
            // Declare pools
            List<Pool> pools = computePoolsList(judokas.size());
            // Declare problem/solution container
            poolDispatchingSolution.setPoolList(pools);
            poolDispatchingSolution.setJudokaList(judokas);
            poolDispatchingSolution = senshiSolver.solve(poolDispatchingSolution);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return poolDispatchingSolution.getJudokaList();
    }

    /**
     *
     * @param judokasCount
     * @return
     */
    private List<Pool> computePoolsList(final int judokasCount) {
        List<Pool> poolList = new ArrayList<>();
        for (int i = 0; i < judokasCount / MAX_JUDOKAS_PER_POOL + 1; i++) {
            Pool pool = new Pool();
            pool.setId(Long.valueOf(i));
            poolList.add(pool);
        }
        return poolList;
    }
}
