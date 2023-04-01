package fr.judo.shiai.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Category {

    @Id
    @Column(name = "code")
    private String name;

    @Column(name = "year_min", nullable = false)
    private Integer yearMin;

    @Column(name = "year_max", nullable = false)
    private Integer yearMax;
}
