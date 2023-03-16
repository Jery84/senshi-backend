package fr.judo.shiai.domain;

import lombok.Data;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.util.Date;

@Data
@PlanningEntity
public class Judoka {
    @PlanningId
    private Long id;

    private String license;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private double weight;
    private Gender gender;
    private String club;
    private Category category;


    @PlanningVariable(valueRangeProviderRefs = "pools")
    private Pool pool;
}
