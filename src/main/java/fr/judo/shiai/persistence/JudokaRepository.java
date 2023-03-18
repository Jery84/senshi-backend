package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Club;
import fr.judo.shiai.domain.Judoka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public Integer createJudoka(final Judoka judoka) {
        log.debug("Create judoka : " + judoka.toString());
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement("INSERT INTO TBL_JUDOKA " +
                            "        (license, first_name , last_name, date_of_birth, weight, gender, id_category, id_club)" +
                            " VALUES (     ? ,          ? ,         ? ,            ? ,     ? ,     ? ,          ? ,      ?);");
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
        return (Integer) keyHolder.getKey();
    }

    /**
     * @param judoka
     * @return count of updated judoka. Must be 1
     */
    @Transactional
    public long updateJudoka(final Judoka judoka) {
        log.debug("Update judoka : " + judoka.toString());
        return jdbcTemplate.update("UPDATE TBL_JUDOKA " +
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
        return jdbcTemplate.update("DELETE FROM TBL_JUDOKA " +
                        " WHERE id = ?;",
                judoka.getId()
        );
    }

    @Transactional
    public List<Judoka> getAllClubs() {
        log.debug("Get all judokas");
        return jdbcTemplate.query("SELECT id as id  FROM TBL_CLUB;", new RowMapper<Judoka>() {
            public Judoka mapRow(ResultSet rs, int rowNum) throws SQLException {
                Judoka judoka = new Judoka();
                judoka.setId(rs.getInt("id"));
                judoka.setFirstName(rs.getString("name"));
                return judoka;
            }
        });
    }
}
