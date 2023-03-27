package fr.judo.shiai.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@Data
@PlanningSolution
public class PoolDispatchingSolution {

    @PlanningEntityCollectionProperty
    private List<Judoka> judokaList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "poolRange")
    private List<Pool> poolList;

    @PlanningScore
    private HardSoftScore score;
}
