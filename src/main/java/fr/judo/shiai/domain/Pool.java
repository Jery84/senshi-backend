package fr.judo.shiai.domain;

import lombok.Data;

import java.util.Collection;

@Data
public class Pool {

    private Long id;
    private String title;
    private Collection<Judoka> players;
}
