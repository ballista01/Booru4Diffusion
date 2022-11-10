package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.User;

public interface IUserTgw {
    User findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    int save(User user);
}