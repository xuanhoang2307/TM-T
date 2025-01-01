package huce.edu.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import huce.edu.web.model.User;
import huce.edu.web.model.WishList;

public interface WishListRepository extends JpaRepository<WishList, Long>{
	WishList findByUserIdAndProductId(Long userId, Long productId);
	
	Page<WishList> findByUser(User user, Pageable pageable);
}
