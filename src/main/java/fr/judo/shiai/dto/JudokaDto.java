package fr.judo.shiai.dto;

import fr.judo.shiai.domain.Gender;
import lombok.Data;

/**
 * Dto for the Judoka
 */
@Data
public class JudokaDto {
    private String firstName;
    private String lastName;
    private String birthDate;
    private Double weight;
    private Gender gender;
    private Integer club;
    private String category;
    private String license;
    private Boolean present;
}
