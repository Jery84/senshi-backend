package fr.judo.shiai.controller;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.Pool;
import fr.judo.shiai.domain.PoolDispatchingSolution;
import fr.judo.shiai.repository.JudokaRepository;
import fr.judo.shiai.solver.SenshiSolver;
import fr.judo.shiai.solver.SenshiSolverSecondChoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
public class PoolController {

    private static final int MAX_JUDOKAS_PER_POOL = 4;

    @Autowired
    private JudokaRepository judokaRepository;


    @Autowired
    private SenshiSolver senshiSolver;

    @Autowired
    private SenshiSolverSecondChoice senshiSolverSecondChoice;

    @PostMapping("/pools")
    public List<Pool> computeAllPools() {
        PoolDispatchingSolution poolDispatchingSolution = new PoolDispatchingSolution();
        try {
            // Retrieve judokas
            List<Judoka> judokas = judokaRepository.findAllPresentAndWeightedJudoka();
            log.info("Judokas available : " + judokas.size());

            // Declare pools
            List<Pool> pools = computePoolsList(judokas.size());
            // Declare problem/solution container
            poolDispatchingSolution.setPoolList(pools);
            poolDispatchingSolution.setJudokaList(judokas);
            poolDispatchingSolution = senshiSolver.solve(poolDispatchingSolution);

            if (!allPoolsAreValid(poolDispatchingSolution.getPoolList())) {
                pools.clear();
                log.info("Recompute pools to try to get a better result");
                poolDispatchingSolution = new PoolDispatchingSolution();
                // Declare pools
                pools = computePoolsList(judokas.size());
                // Declare problem/solution container
                poolDispatchingSolution.setPoolList(pools);
                poolDispatchingSolution.setJudokaList(judokas);
                poolDispatchingSolution = senshiSolverSecondChoice.solve(poolDispatchingSolution);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return poolDispatchingSolution.getPoolList();
    }

    /**
     * @param judokasCount count all judokas in list
     * @return list of pools according max size
     */
    private List<Pool> computePoolsList(final int judokasCount) {
        List<Pool> poolList = new ArrayList<>();
        for (int i = 0; i < judokasCount / MAX_JUDOKAS_PER_POOL + 1; i++) {
            Pool pool = new Pool();
            pool.setId((long) i);
            poolList.add(pool);
        }
        return poolList;
    }

    private boolean isPoolValid(final Pool pool) {
        double maxWeight = pool.getJudokaList().stream().max(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight();
        double minWeight = pool.getJudokaList().stream().min(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight();
        return (pool.getJudokaList().size() >= 2 && pool.getJudokaList().size() < 5)
                && (maxWeight * 0.1 > maxWeight - minWeight);
    }

    private boolean allPoolsAreValid(final List<Pool> pools) {
        boolean res = true;
        for (Pool pool : pools) {
            res = res && isPoolValid(pool);
        }
        return res;
    }
}
