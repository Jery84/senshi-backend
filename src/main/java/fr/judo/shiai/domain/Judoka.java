package fr.judo.shiai.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;

@Data
@PlanningEntity
public class Judoka {
    @PlanningId
    private Integer id;
    private String license;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private double weight;
    private Gender gender;
    private Integer club;
    private Category category;
    private boolean present;

    @PlanningVariable(valueRangeProviderRefs = "pools")
    private Pool pool;
}
