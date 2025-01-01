package huce.edu.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import huce.edu.web.model.Order;
import huce.edu.web.model.User;
import huce.edu.web.repository.OrderRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public Order getOrderById(Long id) {
    	return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

	@Override
	public Order save(Order order) {
        return orderRepository.save(order);
	}
	
	@Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAllOrdersWithUsers(pageable);
    }

	@Override
	public Page<Order> findOrdersByUser(User user, Pageable pageable) {
		// TODO Auto-generated method stub
		 return orderRepository.findByUser(user, pageable);
	}
	@Override
	public Order searchOrdersById(Long id) {
        return orderRepository.findOrderById(id);
    }
	@Override
    public Page<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderRepository.findOrdersByDateRange(startDate, endDate, pageable);
    }
	@Override
    public Page<Order> searchOrdersByDateAndStatus(LocalDate startDate, LocalDate endDate, String orderStatus, Pageable pageable) {
        return orderRepository.findOrdersByDateAndStatus(startDate, endDate, orderStatus, pageable);
    }

    @Override
    public Page<Order> searchOrdersByStatus(String orderStatus, Pageable pageable) {
        return orderRepository.findOrdersByStatus(orderStatus, pageable);
    }
    
    @Override
    public Order findUnpaidOrderByUser(User user) {
        // Tìm đơn hàng chưa thanh toán của người dùng
        return orderRepository.findFirstByUserAndOrderStatusOrderByOrderDateAsc(user, "Đang chờ thanh toán")
                .orElse(null); 
    }
    @Override
    public List<Map<String, Object>> getMonthlyStatisticsWithDefaults(int year) {
    	List<Object[]> rawData = orderRepository.getMonthlyStatistics(year);

        // Tạo danh sách mặc định
        Map<Integer, Map<String, Object>> monthlyData = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> defaultData = new HashMap<>();
            defaultData.put("month", i);
            defaultData.put("revenue", 0.0);
            defaultData.put("orders", 0L);
            monthlyData.put(i, defaultData);
        }
        // Ghi đè dữ liệu thực từ cơ sở dữ liệu vào danh sách mặc định
        for (Object[] record : rawData) {
            int month = (Integer) record[0];
            double revenue = (Double) record[1];
            long orders = (Long) record[2];
            Map<String, Object> data = monthlyData.get(month);
            data.put("revenue", revenue);
            data.put("orders", orders);
        }

        // Trả về dữ liệu theo tháng
        return new ArrayList<>(monthlyData.values());
    }
}

