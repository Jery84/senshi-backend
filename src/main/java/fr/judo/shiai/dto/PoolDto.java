package fr.judo.shiai.dto;

import lombok.Data;

import java.util.List;

/**
 * Dto for the Pool
 */
@Data
public class PoolDto {

    private Integer id;
    private String title;
    private List<JudokaDto> judokas;
}
