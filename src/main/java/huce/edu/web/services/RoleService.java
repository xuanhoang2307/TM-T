package huce.edu.web.services;

import java.util.List;


import huce.edu.web.model.Role;

public interface RoleService {
	List<Role> getAllRoles();
    Role findById(Long id);
}
