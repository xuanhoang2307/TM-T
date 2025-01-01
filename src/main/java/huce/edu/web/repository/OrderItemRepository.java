package huce.edu.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import huce.edu.web.model.Order;
import huce.edu.web.model.OrderItem;
import huce.edu.web.model.Product;
import huce.edu.web.model.User;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
	List<OrderItem> findByOrder(Order order);
	List<OrderItem> findByProductAndOrder_UserAndReviewedFalse(Product product, User user);
}
