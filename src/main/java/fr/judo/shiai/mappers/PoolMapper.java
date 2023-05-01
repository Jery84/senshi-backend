package fr.judo.shiai.mappers;

import fr.judo.shiai.domain.Pool;
import fr.judo.shiai.dto.PoolDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {JudokaMapper.class})
public abstract class PoolMapper {

    @Mapping(target="title", source = "name")
    @Mapping(target="judokas", source = "judokaList")
    public abstract PoolDto toDto(Pool source);

    public abstract Iterable<PoolDto> toDto(Iterable<Pool> source);
}
