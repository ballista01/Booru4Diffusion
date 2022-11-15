package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.Role;
import link.wizapp.booru4diffusion.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@Repository
public class UserTdg implements IUserTdg {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    RoleTdg roleTgw;

    @Override
    public User findByUsername(String username) {
        try {
            User user = jdbcTemplate.queryForObject(
                    """
SELECT users.id, username, email, password, ARRAY_AGG(roles.name) AS roles_name, ARRAY_AGG(roles.id) AS roles_id
FROM users
LEFT JOIN user_roles ON users.id = user_roles.user_id
LEFT JOIN roles ON user_roles.role_id = roles.id
WHERE username=?
GROUP BY users.id;
""",
                    UserRowMapper.getInstance(), username);
            return user;
        } catch (IncorrectResultSizeDataAccessException e){
            return null;
        }
    }

    @Override
    public Boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public Boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }

    public User findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(
                    """
SELECT users.id, username, email, password, ARRAY_AGG(roles.name) AS roles_name, ARRAY_AGG(roles.id) AS roles_id
FROM users
LEFT JOIN user_roles ON users.id = user_roles.user_id
LEFT JOIN roles ON user_roles.role_id = roles.id
WHERE email=?
GROUP BY users.id;
""",
                    UserRowMapper.getInstance(), email);
            return user;
        } catch (IncorrectResultSizeDataAccessException e){
            return null;
        }
    }
    @Override
    public int save(User user) {
        int res = 0;
        try{
//            long newId = jdbcTemplate.update("INSERT INTO users (email, password, username) VALUES(?, ?, ?) RETURNING id",
//                    new Object[]{ user.getEmail(), user.getPassword(), user.getUsername() });
//            user.setId(newId);

            String insertQuery = "INSERT INTO users (email, password, username) VALUES(?, ?, ?) RETURNING id";
            GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
            res += jdbcTemplate.update(conn -> {
                PreparedStatement preparedStatement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getUsername());
                return preparedStatement;
            }, generatedKeyHolder);
            long newId = Objects.requireNonNull(generatedKeyHolder.getKey()).longValue();
            user.setId(newId);

            res++;
            for(Role role: user.getRoles()) {
                // Create entries for roles which not yet exist in the roles table.
                if(role.getId() == null){
                    roleTgw.save(role);
                }

                res += jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES(?, ?)",
                        new Object[] {user.getId(), role.getId()});
            }
            return res;
        } catch (DataAccessException e) {
            return -1;
        }
    }
}
