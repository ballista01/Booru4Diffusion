package link.wizapp.booru4diffusion.tgw;

import link.wizapp.booru4diffusion.model.ERole;
import link.wizapp.booru4diffusion.model.Role;

public interface IRoleTdg {
    Role findByName(ERole name);
}
