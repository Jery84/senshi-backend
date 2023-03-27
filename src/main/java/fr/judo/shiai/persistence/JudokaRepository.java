package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Category;
import fr.judo.shiai.domain.Gender;
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
                            "        (LICENSE, FIRST_NAME , LAST_NAME, DATE_OF_BIRTH, WEIGHT, GENDER, CD_CATEGORY, ID_CLUB, FL_PRESENT)" +
                            " VALUES (     ? ,          ? ,         ? ,            ? ,     ? ,     ? ,          ? ,      ?,    ?     );");
            ps.setString(1, judoka.getLicense());
            ps.setString(2, judoka.getFirstName());
            ps.setString(3, judoka.getLastName());
            ps.setDate(4, java.sql.Date.valueOf(judoka.getDateOfBirth()));
            ps.setDouble(5, judoka.getWeight());
            ps.setString(6, judoka.getGender().toString());
            ps.setString(7, judoka.getCategory().toString());
            ps.setInt(8, judoka.getClub());
            ps.setString(9, (judoka.isPresent() ? "Y" : "N"));
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
                        "SET LICENSE = ?" +
                        ", FIRST_NAME = ?" +
                        ", LAST_NME = ?" +
                        ", DATE_OF_BIRTH = ?" +
                        ", WEIGHT = ?" +
                        ", GENDER = ?" +
                        ", ID_CATEGORY = ?" +
                        ", ID_CLUB = ?" +
                        ", FL_PRESENT = ?" +
                        " WHERE ID = ?;",
                judoka.getLicense(),
                judoka.getFirstName(),
                judoka.getLastName(),
                java.sql.Date.valueOf(judoka.getDateOfBirth()),
                judoka.getWeight(),
                judoka.getGender().toString(),
                judoka.getCategory().toString(),
                judoka.getClub(),
                (judoka.isPresent() ? "Y" : "N"),
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
                        " WHERE ID = ?;",
                judoka.getId()
        );
    }

    @Transactional
    public List<Judoka> getAllJudokas(final boolean isPresent) {
        log.debug("Get all judokas");
        String statement = "SELECT ID" +
                "                           , LICENSE" +
                "                           , FIRST_NAME" +
                "                           , LAST_NAME" +
                "                           , DATE_OF_BIRTH" +
                "                           , WEIGHT" +
                "                           , TRIM(GENDER) AS GENDER" +
                "                           , TRIM(CD_CATEGORY) AS CD_CATEGORY" +
                "                           , ID_CLUB" +
                "                           , FL_PRESENT" +
                "                        FROM TBL_JUDOKA";
        if (isPresent) {
            statement = statement + " WHERE FL_PRESENT = 'Y'";
        }
        statement = statement + ";";
        return jdbcTemplate.query(statement, new RowMapper<Judoka>() {
            public Judoka mapRow(ResultSet rs, int rowNum) throws SQLException {
                Judoka judoka = new Judoka();
                judoka.setId(rs.getInt("ID"));
                judoka.setLicense(rs.getString("LICENSE"));
                judoka.setFirstName(rs.getString("FIRST_NAME"));
                judoka.setLastName(rs.getString("LAST_NAME"));
                judoka.setDateOfBirth((rs.getDate("DATE_OF_BIRTH").toLocalDate()));
                judoka.setWeight(rs.getDouble("WEIGHT"));
                judoka.setGender(Gender.valueOf(rs.getString("GENDER")));
                judoka.setCategory(Category.valueOf(rs.getString("CD_CATEGORY")));
                judoka.setClub(rs.getInt("ID_CLUB"));
                judoka.setPresent(rs.getBoolean("FL_PRESENT"));
                return judoka;
            }
        });
    }


}
