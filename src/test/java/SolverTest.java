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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@SpringBootTest(classes = ShiaiApp.class)
class SolverTest {
    @Autowired
    private SenshiSolver senshiSolver;

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

    public static void printSolution(PoolDispatchingSolution poolDispatchingSolution) {
        for (Pool pool : poolDispatchingSolution.getPoolList()) {
            log.info("Pool " + pool.getId());
            for (Judoka judoka : pool.getJudokaList()) {
                log.info("--> " + judoka.getPool().getId() + " "
                        + judoka.getGender() + " "
                        + judoka.getCategory() + " "
                        + judoka.getWeight() + " "
                        + judoka.getClub() + " "
                        + judoka.getFirstName() + " " + judoka.getLastName());
            }
        }
    }

    @Test
    void firstTest() {
        printSolution(senshiSolver.solve(generateDemoData()));
    }
}