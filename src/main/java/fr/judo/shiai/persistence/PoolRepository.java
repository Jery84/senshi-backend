package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Pool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class PoolRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PoolRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<Pool> getAllPools() {
        log.warn("getAllPools method not implemented yet");

        return new ArrayList<>();
    }
}
