package fr.judo.shiai.solver;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.Pool;
import fr.judo.shiai.domain.PoolDispatchingSolution;
import lombok.extern.slf4j.Slf4j;
import org.kie.internal.io.ResourceFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SenshiSolver {

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
                    .stream(ResourceFactory.newClassPathResource("judokas.csv").getReader())
                    .collect(Collectors.toCollection(() -> judokas));
            log.info("" + judokas.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Pool> poolList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Pool pool = new Pool();
            pool.setId(Long.valueOf(i));
            poolList.add(pool);
        }
        poolDispatchingSolution.setPoolList(poolList);
        poolDispatchingSolution.setJudokaList(judokas);
        return poolDispatchingSolution;
    }

    public static void printSolution(PoolDispatchingSolution poolDispatchingSolution) {
        for (Judoka judoka : poolDispatchingSolution.getJudokaList()) {
            log.info("Id " + judoka.getPool().getId());

        }
    }

    public SenshiSolver() {
        log.info("Create solver");
        SolverFactory<PoolDispatchingSolution> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(PoolDispatchingSolution.class)
                .withEntityClasses(Judoka.class)
                .withConstraintProviderClass(SenshiConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(1)));

        log.info("Load data");
        // Load the problem
        PoolDispatchingSolution problem = generateDemoData();

        log.info("Solve problem");
        // Solve the problem
        Solver<PoolDispatchingSolution> solver = solverFactory.buildSolver();
        PoolDispatchingSolution solution = solver.solve(problem);

        log.info("Display result");
        // Visualize the solution
        printSolution(solution);
    }
}
