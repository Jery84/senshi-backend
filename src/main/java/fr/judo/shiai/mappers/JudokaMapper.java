package fr.judo.shiai.mappers;

import fr.judo.shiai.domain.Judoka;
import fr.judo.shiai.dto.JudokaDto;
import fr.judo.shiai.repository.CategoryRepository;
import fr.judo.shiai.repository.ClubRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses=CategoryRepository.class)
public abstract class JudokaMapper {

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected ClubRepository clubRepository;

    @Mapping(target="category", expression = "java(categoryRepository.findById(source.getCategory()).get())")
    @Mapping(target="club", expression = "java(clubRepository.findById(source.getClub()).get())")
    public abstract Judoka judokaDtoToJudoka(JudokaDto source, @Context CategoryRepository categoryRepository);
}
