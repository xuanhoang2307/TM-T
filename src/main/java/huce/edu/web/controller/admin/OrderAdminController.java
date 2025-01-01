package huce.edu.web.controller.admin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import huce.edu.web.model.Address;
import huce.edu.web.model.Order;
import huce.edu.web.model.User;
import huce.edu.web.repository.OrderItemRepository;
import huce.edu.web.services.AddressService;
import huce.edu.web.services.CartService;
import huce.edu.web.services.OrderService;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.SizeService;
import huce.edu.web.services.UserService;

@Controller
@RequestMapping("/admin")
public class OrderAdminController {

	@Autowired
    private OrderService orderService;
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private ProductService productService;
    
	@GetMapping("/orders")
	public String viewAllOrders(@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) Long id,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String orderStatus,
			Model model) {
		
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        if (id != null) {
        	Order order = orderService.searchOrdersById(id);
            if (order != null) {
                // Nếu tìm thấy đơn hàng, trả về Page chứa đơn hàng này
                orders = new PageImpl<>(List.of(order), pageable, 1);
            } else {
                // Nếu không tìm thấy, trả về trang rỗng
                orders = new PageImpl<>(List.of(), pageable, 0);
            }
        } else if (startDate == null && endDate == null && orderStatus == null) {
            orders = orderService.getAllOrders(pageable);
        } 
        else if ( startDate != null && endDate != null && orderStatus != null) {
            orders = orderService.searchOrdersByDateAndStatus( startDate, endDate, orderStatus, pageable);
        }
        // Nếu có ngày bắt đầu và kết thúc thì lọc theo ngày
        else if (startDate != null && endDate != null) {
            orders = orderService.getOrdersByDateRange(startDate, endDate, pageable);
        }
        else if (orderStatus != null) {
            orders = orderService.searchOrdersByStatus(orderStatus, pageable);
        } 
        else {
            orders = orderService.getAllOrders(pageable);
        }
        
        model.addAttribute("orders", orders.getContent());
        model.addAttribute("totalPages", orders.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("id", id);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("orderStatus", orderStatus);
		return "admin/orders/orders";
	}
	@GetMapping("/orderitems/{id}")
	public String displayOrderItems(@PathVariable Long id, Model model) {
		Order order = orderService.getOrderById(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Đơn hàng không tồn tại.");
        }
       
        Address addresses = order.getShippingAddress();
        model.addAttribute("addresses", addresses);
        
        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
		return "admin/orders/orderitem";
	}
	@PostMapping("/orderitems/{orderId}/update-status")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam String orderStatus) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Đơn hàng không tồn tại.");
        }

        // Cập nhật trạng thái cho đơn hàng
        order.setOrderStatus(orderStatus);
        orderService.save(order);

        // Redirect lại về trang chi tiết đơn hàng
        return "redirect:/admin/orderitems/" + orderId;
    }
	
	@GetMapping("/statistics")
    public String showStatisticsPage() {
        return "admin/orders/chair";
    }
	@GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(@RequestParam int year) {
		List<Map<String, Object>> statistics = orderService.getMonthlyStatisticsWithDefaults(year);
	    // Chuyển đổi dữ liệu thành danh sách riêng cho months, revenues, và orders
	    List<String> months = new ArrayList<>();
	    List<Double> revenues = new ArrayList<>();
	    List<Long> orders = new ArrayList<>();
	    for (Map<String, Object> record : statistics) {
	        months.add("Tháng " + record.get("month"));
	        revenues.add((Double) record.get("revenue"));
	        orders.add((Long) record.get("orders"));
	    }
	    Map<String, Object> response = new HashMap<>();
	    response.put("months", months);
	    response.put("revenues", revenues);
	    response.put("orders", orders);
	    return ResponseEntity.ok(response);
    }
}
