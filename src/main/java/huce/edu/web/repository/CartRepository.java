package huce.edu.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import huce.edu.web.model.Cart;
import huce.edu.web.model.User;

public interface CartRepository extends JpaRepository<Cart, Long>{
	Cart findByUserId(Long userId);
	
	@Query("SELECT c FROM Cart c WHERE c.user = :user")
	Cart findByUser(@Param("user") User user);
	
}
