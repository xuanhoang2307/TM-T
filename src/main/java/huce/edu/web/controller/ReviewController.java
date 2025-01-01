package huce.edu.web.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import huce.edu.web.model.OrderItem;
import huce.edu.web.model.Product;
import huce.edu.web.model.User;
import huce.edu.web.repository.OrderItemRepository;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.ReviewService;
import huce.edu.web.services.UserService;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @PostMapping("/reviews")
    public String submitReview(
            @RequestParam Long productId, 
            @RequestParam int rating, 
            @RequestParam String comment,
            @RequestParam Long orderItemId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("Người dùng không tồn tại");
        }
        // Lấy sản phẩm từ database
        Product product = productService.findById(productId);
        if (product == null) {
            throw new RuntimeException("Sản phẩm không tồn tại");
        }

        // Gọi service để thêm đánh giá
        reviewService.addReview(user, product, rating, comment, LocalDateTime.now());
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem không tồn tại"));
        orderItem.setReviewed(true);
        orderItemRepository.save(orderItem);
        return "redirect:/home/orders/orderitems/"+ orderItem.getOrder().getId();  
    }
}