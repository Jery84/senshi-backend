package fr.judo.shiai.domain;

import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Getter
@Setter
@PlanningEntity
public class Pool {

    private Long id;

    private String name;

    private Category category;

    @InverseRelationShadowVariable(sourceVariableName = "pool")
    private List<Judoka> judokaList = new ArrayList<>();

    public double getWeightMin() {
        return judokaList.stream()
                .mapToDouble(Judoka::getWeight)
                .min().orElse(0);
    }

    public double getWeightMax() {
        return judokaList.stream()
                .mapToDouble(Judoka::getWeight)
                .max().orElse(0);
    }
}
