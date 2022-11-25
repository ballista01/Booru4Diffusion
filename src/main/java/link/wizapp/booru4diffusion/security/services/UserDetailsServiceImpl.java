package link.wizapp.booru4diffusion.security.services;

import link.wizapp.booru4diffusion.model.User;
import link.wizapp.booru4diffusion.tdg.IUserTdg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    IUserTdg userTdg;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userTdg.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Username %s Not Found!", username));
        }

        return UserDetailsImpl.build(user);
    }

}