package huce.edu.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import huce.edu.web.model.Role;
import huce.edu.web.repository.RoleRepository;
@Service
public class RoleServiceImplement implements RoleService{
	
	@Autowired
    private RoleRepository roleRepository;
	
	@Override
	public List<Role> getAllRoles() {
		// TODO Auto-generated method stub
		return this.roleRepository.findAll();
	}

	@Override
	public Role findById(Long id) {
		// TODO Auto-generated method stub
		return this.roleRepository.findById(id).get();
	}

}
