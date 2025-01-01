package huce.edu.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import huce.edu.web.model.Role;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	 Role findByName(String name);
}
