package huce.edu.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import huce.edu.web.model.Product;
import huce.edu.web.model.Review;
import huce.edu.web.model.User;

public interface ReviewRepository extends JpaRepository<Review, Long>{
	
	boolean existsByUserAndProduct(User user, Product product);
	List<Review> findByProduct(Product product);
}
