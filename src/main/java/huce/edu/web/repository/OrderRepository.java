package huce.edu.web.repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import huce.edu.web.model.Order;
import huce.edu.web.model.Product;
import huce.edu.web.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	Page<Order> findByUser(User user, Pageable pageable);
	
	@Query("SELECT o FROM Order o JOIN FETCH o.user")
	Page<Order> findAllOrdersWithUsers(Pageable pageable);
	
	@Query("SELECT o FROM Order o WHERE o.id = :id")
	Order findOrderById(@Param("id") Long id);
	
	@Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Page<Order> findOrdersByDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate, 
                                      Pageable pageable);
	@Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.orderStatus = :orderStatus")
    Page<Order> findOrdersByDateAndStatus( 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate, 
                                            @Param("orderStatus") String orderStatus, 
                                            Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderStatus = :orderStatus")
    Page<Order> findOrdersByStatus(@Param("orderStatus") String orderStatus, Pageable pageable);
    
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
	           "FROM Order o JOIN o.orderItems oi " +
	           "WHERE o.user = :user AND oi.product = :product AND o.orderStatus = :status")
	boolean existsByUserAndProductAndStatus(@Param("user") User user, 
	                                            @Param("product") Product product, 
	                                            @Param("status") String status);
    
    Optional<Order> findFirstByUserAndOrderStatusOrderByOrderDateAsc(User user, String orderStatus);
    
    @Query("SELECT FUNCTION('MONTH', o.orderDate) as month, " +
    	       "SUM(o.totalDiscountedPrice) as revenue, " +
    	       "COUNT(o.id) as orders " +
    	       "FROM Order o " +
    	       "WHERE FUNCTION('YEAR', o.orderDate) = :year " +
    	       "GROUP BY FUNCTION('MONTH', o.orderDate) " +
    	       "ORDER BY FUNCTION('MONTH', o.orderDate)")
     List<Object[]> getMonthlyStatistics(@Param("year") int year);
}
