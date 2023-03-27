package fr.judo.shiai.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
public class Pool {

    private Long id;

    private Category category;
    private List<Judoka> judokas =  new ArrayList<>();


    @InverseRelationShadowVariable(sourceVariableName = "pools")
    public List<Judoka> getJudokas() {
        return judokas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setJudokas(List<Judoka> judokas) {
        this.judokas = judokas;
    }
}
