package fr.judo.shiai.repository;

import fr.judo.shiai.domain.Pool;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolRepository extends CrudRepository<Pool, Long> {
}
