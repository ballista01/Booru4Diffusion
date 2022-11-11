package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.ERole;
import link.wizapp.booru4diffusion.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@Repository
public class RoleTgw implements IRoleTgw{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Role findByName(ERole name) {
        try {
            Role role = jdbcTemplate.queryForObject("SELECT * FROM roles WHERE name = ?",
                    BeanPropertyRowMapper.newInstance(Role.class), name.toString());
            return role;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
    public int save(Role role) {
        int res = 0;
        try {
            Role existingRole = findByName(role.getName());
            if(existingRole!=null){
                return 0;
            }

            String insertQuery = "INSERT INTO roles (name) VALUES (?) RETURNING id";
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            res += jdbcTemplate.update(conn -> {
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, role.getName().toString());
                return preparedStatement;
            }, generatedKeyHolder);
            Integer newId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
            role.setId(newId);

            res += jdbcTemplate.update("INSERT INTO roles (name) VALUES (?)", role.getName());
            return res;
        } catch (IncorrectResultSizeDataAccessException e) {
            return -1;
        }
    }
}
