package fr.judo.shiai.mappers;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.dto.JudokaDto;
import fr.judo.shiai.repository.CategoryRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface JudokaMapper {

    @Mapping(target="category", ignore = true)
    @Mapping(target="club", ignore = true)
    Judoka judokaDtoToJudoka(JudokaDto source, @Context CategoryRepository categoryRepository);

    @AfterMapping
    default void map(@MappingTarget Judoka target, JudokaDto source, @Context CategoryRepository categoryRepository) {
        System.out.println("called ?");
        target.setCategory( categoryRepository.findById(source.getCategory()).get());
    }
}
