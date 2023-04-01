package fr.judo.shiai.domain;

import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@PlanningEntity
public class Pool {

    private Long id;

    private String name;

    @InverseRelationShadowVariable(sourceVariableName = "pool")
    private List<Judoka> judokaList = new ArrayList<>();
}
