package fr.judo.shiai.persistence;

import fr.judo.shiai.domain.Club;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class ClubRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClubRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<Club> getAllClubs() {
        log.debug("Get all clubs");
        return jdbcTemplate.query("SELECT * FROM peopleGroup", new RowMapper<Club>() {
            public Club mapRow(ResultSet rs, int rowNum) throws SQLException {
                Club club = new Club();
                club.setId(rs.getLong("id"));
                club.setName(rs.getString("name"));
                return club;
            }
        });
    }
}
