package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserTgw implements IUserTgw{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public User findByUsername(String username) {
        try {
            // TODO: fill user roles field.
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE username=?",
                    BeanPropertyRowMapper.newInstance(User.class), username);
            return user;
        } catch (IncorrectResultSizeDataAccessException e){
            return null;
        }
    }

    @Override
    public Boolean existsByUsername(String username) {
        if(findByUsername(username) != null){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean existsByEmail(String email) {
        if(findByEmail(email) != null){
            return true;
        } else {
            return false;
        }
    }

    public User findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?",
                    BeanPropertyRowMapper.newInstance(User.class), email);
            return user;
        } catch(IncorrectResultSizeDataAccessException e){
            return null;
        }
    }
    @Override
    public int save(User user) {
        return jdbcTemplate.update("INSERT INTO users (email, password, username) VALUES(?, ?, ?)",
                new Object[]{ user.getEmail(), user.getPassword(), user.getUsername() });
    }
}
