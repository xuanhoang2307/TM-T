package huce.edu.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import huce.edu.web.model.User;


public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByUserName(String userName);
	
}
