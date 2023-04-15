import fr.judo.shiai.ShiaiApp;
import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.Pool;
import fr.judo.shiai.domain.PoolDispatchingSolution;
import fr.judo.shiai.repository.JudokaRepository;
import fr.judo.shiai.solver.SenshiConstraintProvider;
import fr.judo.shiai.solver.SenshiConstraintProviderSecondChoice;
import fr.judo.shiai.solver.SenshiSolver;
import fr.judo.shiai.solver.SenshiSolverSecondChoice;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@SpringBootTest(classes = ShiaiApp.class)
@ExtendWith(SpringExtension.class)
public class SolverTest {

    @Autowired
    JudokaRepository judokaRepository;

    /**
     * @param pool
     * @return true is the pool complies to hard constraints (pool's size and judoka's weights)
     */
    private static boolean isPoolValid(final Pool pool) {
        return (pool.getJudokaList().size() > 2 && pool.getJudokaList().size() < 5)
                && (4 > pool.getJudokaList().stream().max(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight()
                - pool.getJudokaList().stream().min(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight());
    }

    /**
     * @param poolDispatchingSolution
     * @return true if all pools are valid
     */
    public boolean printSolution(PoolDispatchingSolution poolDispatchingSolution) {
        int judokasCount = 0;
        boolean res = true;
        for (Pool pool : poolDispatchingSolution.getPoolList()) {
            log.info("Pool " + pool.getName() + " is valid " + isPoolValid(pool));
            res = res && isPoolValid(pool);
            judokasCount = judokasCount + pool.getJudokaList().size();
            for (Judoka judoka : pool.getJudokaList()) {
                log.info("--> " + judoka.getPool().getId() + " "
                        + judoka.getGender() + " "
                        + judoka.getCategory().getName() + " "
                        + judoka.getWeight() + " "
                        + judoka.getClub().getName() + " "
                        + judoka.getFirstName() + " " + judoka.getLastName());
            }
        }
        log.info("Judokas count : " + judokasCount + " / " + poolDispatchingSolution.getJudokaList().size());
        return res;
    }


    /**
     * @param judokas
     * @return
     */
    public List<Pool> getPoolList(final List<Judoka> judokas, final int size) {
        List<Pool> poolList = new ArrayList<>();
        for (int i = 0; i < judokas.size() / size
                + (judokas.size() % size > 0 ? 1 : 0); i++) {
            Pool pool = new Pool();
            pool.setId(Long.valueOf(i));
            poolList.add(pool);
        }
        return poolList;
    }

    /**
     * @param judokaList
     * @return true if the current problem is solved properly
     */
    public boolean makeTest(final List<Judoka> judokaList) {
        SenshiSolver senshiSolver = new SenshiSolver();
        PoolDispatchingSolution poolDispatchingSolution = new PoolDispatchingSolution();
        poolDispatchingSolution.setJudokaList(judokaList);
        poolDispatchingSolution.setPoolList(getPoolList(judokaList, SenshiConstraintProvider.MAX_PREFERED_POOL_SIZE));
        return printSolution(senshiSolver.solve(poolDispatchingSolution));
    }


    @Test
    void testPoussins() {
        Assertions.assertEquals(makeTest(judokaRepository.findPoussins()), true);
    }

    @Test
    void testBejamins() {
        Assertions.assertEquals(makeTest(judokaRepository.findBenjamins()), true);
    }


    @Test
    void testBejamins2nd() {
        Assertions.assertEquals(makeSecondTest(judokaRepository.findBenjamins()), true);
    }

    @Test
    void testBejamines() {
        Assertions.assertEquals(makeTest(judokaRepository.findBenjamines()), true);
    }

    /**
     * @param judokaList
     * @return true if the current problem is solved properly
     */
    public boolean makeSecondTest(final List<Judoka> judokaList) {
        SenshiSolverSecondChoice senshiSolver = new SenshiSolverSecondChoice();
        PoolDispatchingSolution poolDispatchingSolution = new PoolDispatchingSolution();
        poolDispatchingSolution.setJudokaList(judokaList);
        poolDispatchingSolution.setPoolList(getPoolList(judokaList, SenshiConstraintProviderSecondChoice.MAX_PREFERED_POOL_SIZE));
        return printSolution(senshiSolver.solve(poolDispatchingSolution));
    }
}