import fr.judo.shiai.ShiaiApp;
import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.Pool;
import fr.judo.shiai.domain.PoolDispatchingSolution;
import fr.judo.shiai.solver.SenshiSolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kie.internal.io.ResourceFactory;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest(classes = ShiaiApp.class)
public class SolverTest {


    public static PoolDispatchingSolution generateDemoData() {
        PoolDispatchingSolution poolDispatchingSolution = new PoolDispatchingSolution();
        List<Judoka> judokas = new ArrayList<>();
        try {
            CsvParser.separator(';')
                    .mapWith(
                            CsvMapperFactory
                                    .newInstance()
                                    .defaultDateFormat("dd/MM/yyyy")
                                    .newMapper(Judoka.class))
                    .stream(ResourceFactory.newClassPathResource("Poussins.csv").getReader())
                    .collect(Collectors.toCollection(() -> judokas));
            log.info("" + judokas.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Pool> poolList = new ArrayList<>();
        for (int i = 0; i < judokas.size() / 4 + 1; i++) {
            Pool pool = new Pool();
            pool.setId(Long.valueOf(i));
            poolList.add(pool);
        }
        poolDispatchingSolution.setPoolList(poolList);
        poolDispatchingSolution.setJudokaList(judokas);
        return poolDispatchingSolution;
    }

    public static boolean printSolution(PoolDispatchingSolution poolDispatchingSolution) {
        int judokasCount = 0;
        boolean res = true;
        for (Pool pool : poolDispatchingSolution.getPoolList()) {
            log.info("Pool " + pool.getId() + " is valid " + isPoolValid(pool));
            res = res && isPoolValid(pool);
            judokasCount = judokasCount + pool.getJudokaList().size();
            for (Judoka judoka : pool.getJudokaList()) {
                log.info("--> " + judoka.getPool().getId() + " "
                        + judoka.getGender() + " "
                        + judoka.getCategory() + " "
                        + judoka.getWeight() + " "
                        + judoka.getClub() + " "
                        + judoka.getFirstName() + " " + judoka.getLastName());
            }
        }
        log.info("Judokas count : " + judokasCount + " / " + poolDispatchingSolution.getJudokaList().size());
        return res;
    }

    private static boolean isPoolValid(final Pool pool) {

        return (pool.getJudokaList().size() > 2 && pool.getJudokaList().size() < 5)
                && (4 > pool.getJudokaList().stream().max(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight()
                - pool.getJudokaList().stream().min(Comparator.comparing(Judoka::getWeight)).orElseThrow(IllegalStateException::new).getWeight());
    }

    @Test
    void firstTest() {
        SenshiSolver senshiSolver= new SenshiSolver();
        printSolution(senshiSolver.solve(generateDemoData()));
    }
}