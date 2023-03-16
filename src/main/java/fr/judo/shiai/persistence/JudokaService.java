package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Judoka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;

@Slf4j
@Service
public class JudokaService {

    private final JdbcTemplate jdbcTemplate;

    public JudokaService(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public long judokaSignIn(final Judoka judoka) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("insert into TBL_JUDOKA " +
                            "(license, first_name , last_name, date_of_birth, weight, gender, id_category, id_club)");
            ps.setString(1, judoka.getLicense());
            ps.setString(2, judoka.getFirstName());
            ps.setString(3, judoka.getLastName());
            ps.setDate(4, java.sql.Date.valueOf(judoka.getDateOfBirth()));
            ps.setDouble(5, judoka.getWeight());
            ps.setString(6, judoka.getGender().toString());
            ps.setString(7, judoka.getCategory().toString());
            ps.setString(8, judoka.getClub());
            return ps;
        }, keyHolder);
        return (long) keyHolder.getKey();
    }

}
