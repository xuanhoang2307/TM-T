package huce.edu.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import huce.edu.web.model.User;
import huce.edu.web.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>{
	List<UserRole> findByUser(User user); 
	void deleteByUserId(Long userId);
	Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);
}
