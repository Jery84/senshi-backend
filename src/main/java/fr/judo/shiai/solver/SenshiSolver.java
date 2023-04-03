package fr.judo.shiai.solver;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.domain.Pool;
import fr.judo.shiai.domain.PoolDispatchingSolution;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class SenshiSolver {

    private SolverFactory<PoolDispatchingSolution> solverFactory;

    public SenshiSolver() {
        log.info("Create solver");
        solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(PoolDispatchingSolution.class)
                .withEntityClasses(Judoka.class, Pool.class)
                .withConstraintProviderClass(SenshiConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofMinutes(3)));
    }

    public PoolDispatchingSolution solve(final PoolDispatchingSolution problem) {

        log.info("Solve problem");
        // Solve the problem
        Solver<PoolDispatchingSolution> solver = solverFactory.buildSolver();
        PoolDispatchingSolution solution = solver.solve(problem);
        return solution;
    }

}
