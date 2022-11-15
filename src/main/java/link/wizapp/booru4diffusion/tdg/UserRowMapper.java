package link.wizapp.booru4diffusion.tdg;

import link.wizapp.booru4diffusion.model.ERole;
import link.wizapp.booru4diffusion.model.Role;
import link.wizapp.booru4diffusion.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class UserRowMapper implements RowMapper<User> {

    private static UserRowMapper instance = null;
    private UserRowMapper(){
        super();
    }

    public static UserRowMapper getInstance(){
        if(instance == null){
            instance = new UserRowMapper();
        }
        return instance;
    }
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));

        String[] rolesNameArr = (String[]) rs.getArray("roles_name").getArray();
        Integer[] rolesIdArr = (Integer[]) rs.getArray("roles_id").getArray();
        HashSet<Role> roles = new HashSet<>(rolesIdArr.length);
        for(int i=0;i<rolesIdArr.length;i++){
            roles.add(new Role(ERole.valueOf(rolesNameArr[i]), rolesIdArr[i]));
        }

        user.setRoles(roles);

        if(user.getId() != null) return user;
        else return null;
    }
}
