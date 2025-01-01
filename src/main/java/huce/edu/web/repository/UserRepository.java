package huce.edu.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import huce.edu.web.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByUserName(String userName);
	
	@Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
	User findByEmail(@Param("email") String email);
	
	Page<User> findByUserNameContainingIgnoreCase(String userName, Pageable pageable);
	
}
