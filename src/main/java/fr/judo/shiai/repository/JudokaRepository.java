package fr.judo.shiai.repository;

import fr.judo.shiai.domain.Judoka;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JudokaRepository extends CrudRepository<Judoka, Integer> {

    @Query("SELECT j FROM Judoka j WHERE j.present = true and j.weight is not null and j.weight > 0")
    List<Judoka> findAllPresentandWeightedJudoka();
}
