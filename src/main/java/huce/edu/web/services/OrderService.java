package huce.edu.web.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import huce.edu.web.model.Order;
import huce.edu.web.model.User;

public interface OrderService {
	Order save(Order order);
    Order getOrderById(Long id);
    Page<Order> getAllOrders(Pageable pageable);
    Order updateOrderStatus(Long orderId, String status);
    Page<Order> findOrdersByUser(User user, Pageable pageable);
    Order searchOrdersById(Long id);
    Page<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<Order> searchOrdersByDateAndStatus( LocalDate startDate, LocalDate endDate, String orderStatus, Pageable pageable);
    Page<Order> searchOrdersByStatus(String orderStatus, Pageable pageable);
    Order findUnpaidOrderByUser(User user);
    List<Map<String, Object>> getMonthlyStatisticsWithDefaults(int year);
    
}
