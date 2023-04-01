package fr.judo.shiai.repository;

import fr.judo.shiai.domain.Category;
import fr.judo.shiai.domain.Judoka;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, String> {
}
