package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Judoka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;

@Slf4j
@Repository
public class JudokaRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JudokaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param judoka
     * @return PK of created judoka
     */
    @Transactional
    public long createJudoka(final Judoka judoka) {
        log.debug("Create judoka : " + judoka.toString());
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("insert into TBL_JUDOKA " +
                            "        (license, first_name , last_name, date_of_birth, weight, gender, id_category, id_club)" +
                            " values (     ? ,          ? ,         ? ,            ? ,     ? ,     ? ,          ? ,      ?);");
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

    /**
     * @param judoka
     * @return count of updated judoka. Must be 1
     */
    @Transactional
    public long updateJudoka(final Judoka judoka) {
        log.debug("Update judoka : " + judoka.toString());
        return jdbcTemplate.update("update TBL_JUDOKA " +
                        "set license = ?" +
                        ", first_name = ?" +
                        ", last_name" +
                        ", date_of_birth = ?" +
                        ", weight = ?" +
                        ", gender = ?" +
                        ", id_category = ?" +
                        ", id_club = ?" +
                        " where id = ?;",
                judoka.getLicense(),
                judoka.getFirstName(),
                judoka.getLastName(),
                java.sql.Date.valueOf(judoka.getDateOfBirth()),
                judoka.getWeight(),
                judoka.getGender().toString(),
                judoka.getCategory().toString(),
                judoka.getClub(),
                judoka.getId()
        );
    }

    /**
     * @param judoka
     * @return count of deleted judoka. Must be 0 or 1
     */
    @Transactional
    public long deleteJudoka(final Judoka judoka) {
        log.debug("Delete judoka : " + judoka.toString());
        return jdbcTemplate.update("delete TBL_JUDOKA " +
                        " where id = ?;",
                judoka.getId()
        );
    }
}
