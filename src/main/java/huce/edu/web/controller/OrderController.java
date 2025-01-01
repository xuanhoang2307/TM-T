package huce.edu.web.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import huce.edu.web.model.Address;
import huce.edu.web.model.Order;
import huce.edu.web.model.OrderItem;
import huce.edu.web.model.Product;
import huce.edu.web.model.Size;
import huce.edu.web.model.User;
import huce.edu.web.repository.OrderItemRepository;
import huce.edu.web.services.AddressService;
import huce.edu.web.services.CartService;
import huce.edu.web.services.OrderService;
import huce.edu.web.services.PayPalService;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.SizeService;
import huce.edu.web.services.UserService;

@Controller
@RequestMapping("/home")
public class OrderController {
	
	@Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private ProductService productService;
    @Autowired
    private SizeService sizeService;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PayPalService payPalService;

    @GetMapping("/orders")
    public String getOrdersForUser(@RequestParam(defaultValue = "0") int page, Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        Pageable pageable = PageRequest.of(page, 10);
        Page<Order> ordersPage = orderService.findOrdersByUser(user, pageable);
        
        // Thêm các thuộc tính vào model
        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("totalPages", ordersPage.getTotalPages());
        model.addAttribute("currentPage", page);

        return "layout/orders/orders"; 
    }

    @PostMapping("/orders/create")
    public String createOrder(
		    		@RequestParam Long addressId,
		            @RequestParam String notes,
		            @RequestParam("productIds") List<Long> productIds,
		            @RequestParam("sizeIds") List<Long> sizeIds,
		            @RequestParam("quantities") List<Integer> quantities,
		            @RequestParam("totalPrice") double totalPrice,  
                    @RequestParam("totalDiscountedPrice") double totalDiscountedPrice, 
                    @RequestParam("totalItem") int totalItem,
                    @RequestParam("paymentMethod") String paymentMethod,
    				Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username); 
        
        Address shippingAddress = addressService.findById(addressId);
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryDate(LocalDateTime.now().plusDays(3));  // Giả sử giao hàng sau 3 ngày
        order.setShippingAddress(shippingAddress);
        order.setNotes(notes);
        
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Long sizeId = sizeIds.get(i);
            int quantity = quantities.get(i);
            // Lấy thông tin sản phẩm và kích thước
            Product product = productService.findById(productId);
            Size size = sizeService.findById(sizeId);
            // Tạo OrderItem và tính toán giá
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setSize(size);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice()); 
            orderItem.setOrder(order);  
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);
        order.setTotalPrice(totalPrice);
        order.setTotalDiscountedPrice(totalPrice); 
        order.setTotalItem(totalItem);
        
        if ("paypal".equals(paymentMethod)) {
            order.setPaymentMethod("paypal");
            order.setOrderStatus("Đang chờ thanh toán");
            orderService.save(order);
            // Tạo yêu cầu thanh toán PayPal và chuyển hướng người dùng đến PayPal
            String approvalUrl = payPalService.createPayment(order);
            return "redirect:" + approvalUrl;
        }
        // Nếu thanh toán bằng tiền mặt, chỉ lưu đơn hàng
        if ("cash".equals(paymentMethod)) {
            order.setPaymentMethod("cash");
            order.setOrderStatus("Đang chờ");
            orderService.save(order);
            cartService.clearCart(user);
            return "redirect:/home/orders"; 
        }
        cartService.clearCart(user);
        return "redirect:/home/orders";
    }
    
    @GetMapping("/orders/orderitems/{id}")
    public String getOrderById(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Đơn hàng không tồn tại.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        if (user != null) {
        	Address addresses = order.getShippingAddress();
            model.addAttribute("addresses", addresses);
        }
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Bạn không có quyền truy cập đơn hàng này.");
        }
        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());

        return "layout/orders/orderdetail";
    }
    @PostMapping("/orders/cancel/{orderId}")
    @ResponseBody
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        // Lấy thông tin đơn hàng theo ID
        Order order = orderService.getOrderById(orderId);

        // Kiểm tra nếu không tìm thấy đơn hàng
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Đơn hàng không tồn tại.");
        }
        // Kiểm tra trạng thái đơn hàng
        if (!order.getOrderStatus().equals("Đang chờ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chỉ có thể hủy đơn hàng ở trạng thái Đang chờ.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!order.getUser().getUserName().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền hủy đơn hàng này.");
        }
        order.setOrderStatus("Đã hủy");
        orderService.save(order);

        return ResponseEntity.ok("Đơn hàng đã được hủy.");
    }
    
    @PostMapping("/orders/execute-payment")
    public String executePayment(@RequestParam String paymentId, @RequestParam String payerId) {
        try {
            // Xác thực thanh toán qua PayPal
            boolean isPaymentSuccessful = payPalService.executePayment(paymentId, payerId);
            if (isPaymentSuccessful) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                User user = userService.findByUserName(username);
                
                // Tìm đơn hàng chưa thanh toán của người dùng
                Order order = orderService.findUnpaidOrderByUser(user);
                if (order != null && order.getOrderStatus().equals("Đang chờ thanh toán")) {
                    order.setOrderStatus("Đang chờ"); 
                    orderService.save(order); 
                    cartService.clearCart(user);
                }
                return "redirect:/home/orders";  
            } else {
                // Thanh toán thất bại
                return "redirect:/home/checkout";  
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/home/orders";  // Nếu có lỗi, quay lại trang đơn hàng
        }
    }


}
