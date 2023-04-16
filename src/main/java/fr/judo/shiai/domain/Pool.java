package fr.judo.shiai.domain;

import jakarta.persistence.*;
import lombok.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;
@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@PlanningEntity
public class Pool {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @InverseRelationShadowVariable(sourceVariableName = "pool")
    @OneToMany(
            cascade = CascadeType.DETACH
    )
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
