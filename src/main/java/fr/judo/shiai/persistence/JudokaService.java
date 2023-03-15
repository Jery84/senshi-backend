package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Judoka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JudokaService {

    private final JdbcTemplate jdbcTemplate;

    public JudokaService(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public int judokaSignIn (final Judoka judoka) {
       // jdbcTemplate.update();
        return 0;
    }
}
