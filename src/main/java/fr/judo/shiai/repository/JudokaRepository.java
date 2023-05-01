package fr.judo.shiai.repository;

import fr.judo.shiai.domain.Judoka;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JudokaRepository extends CrudRepository<Judoka, Integer> {

    @Query("SELECT j FROM Judoka j WHERE j.present = true and j.weight is not null and j.weight > 0")
    List<Judoka> findAllPresentAndWeightedJudoka();

    @Query("SELECT j FROM Judoka j WHERE j.present =  true AND j.category.name = 'POUSSIN' AND j.gender = 'MALE' and j.weight is not null and j.weight > 0")
    List<Judoka> findPoussins();

    @Query("SELECT j FROM Judoka j WHERE j.present =  true AND j.category.name = 'BENJAMIN' AND j.gender = 'MALE' and j.weight is not null and j.weight > 0")
    List<Judoka> findBenjamins();

    @Query("SELECT j FROM Judoka j WHERE j.present =  true AND j.category.name = 'BENJAMIN' AND j.gender = 'FEMALE' and j.weight is not null and j.weight > 0")
    List<Judoka> findBenjamines();

    @Modifying
    @Query("update Judoka j set j.present = false")
    void resetPresence();

    @Modifying
    @Query("update Judoka j set j.present = false where j.category.name = :categoryName")
    void resetPresence(@Param("categoryName") String categoryName);

    @Query("SELECT j FROM Judoka j WHERE j.present =  true AND j.category.name = 'BENJAMIN' and j.weight is not null and j.weight > 0")
    List<Judoka> findBenjaminesAndBenjamins();

}
