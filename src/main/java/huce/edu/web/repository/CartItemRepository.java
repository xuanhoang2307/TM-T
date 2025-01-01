package huce.edu.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import huce.edu.web.model.CartItem;
import jakarta.transaction.Transactional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{
	@Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_items WHERE id = ?1", nativeQuery = true)
    void deleteCartItemById(Long cartItemId);
}
