package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.ERole;
import link.wizapp.booru4diffusion.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
