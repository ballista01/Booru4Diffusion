package link.wizapp.booru4diffusion.tdg;

import link.wizapp.booru4diffusion.model.User;

public interface IUserTdg {
    User findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    int save(User user);
}