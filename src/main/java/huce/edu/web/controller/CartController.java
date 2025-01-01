package huce.edu.web.controller;

import huce.edu.web.model.Cart;
import huce.edu.web.model.CartItem;
import huce.edu.web.model.Product;
import huce.edu.web.model.Size;
import huce.edu.web.services.CartService;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.SizeService;
import huce.edu.web.services.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import huce.edu.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/home/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SizeService sizeService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addItemToCart(
    						  @RequestParam("productId") Long productId,
    						  @RequestParam("size") Long sizeId, 					  
    						  @RequestParam("quantity") int quantity,
    						  Model model
                              ) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        
        Product product = productService.findById(productId);
        Size size = sizeService.findById(sizeId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sản phẩm không tồn tại.");
        }
        if (size == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kích thước không tồn tại.");
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
        }
        cartService.addItemToCart(user, product, quantity, size);
        return ResponseEntity.ok("Sản phẩm đã được thêm vào giỏ hàng.");
    }

    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<String> removeItemFromCart(
    		@RequestBody Map<String, Long> requestData) {
    	Long productId = requestData.get("productId");
        Long sizeId = requestData.get("sizeId");
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Lấy tên người dùng đã đăng nhập
        User user = userService.findByUserName(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại.");
        }

        try {
        	Cart cart = cartService.findCartByUser(user);
            // Tìm sản phẩm trong giỏ hàng
            CartItem cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getProduct().getId().equals(productId) &&
                                    item.getSize().getId().equals(sizeId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không có trong giỏ hàng."));

            cartService.removeItemFromCart(cartItem.getId());

            return ResponseEntity.ok("Sản phẩm đã được xóa khỏi giỏ hàng.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartItem(
    		@RequestBody Map<String, Long> requestData) {
    	Long productId = requestData.get("productId");
        Long sizeId = requestData.get("sizeId");
        int quantity = requestData.containsKey("quantity") ? requestData.get("quantity").intValue() : 0; // Kiểm tra nếu quantity tồn tại trong request

    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	String username = authentication.getName();
    	User user = userService.findByUserName(username);
        if (user == null) {
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Người dùng không tồn tại"));
        }
        try {
            Cart cart = cartService.findCartByUser(user); // Lấy giỏ hàng của người dùng
            CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));

            if (sizeId != null) {
                try {
                    cartService.updateCartItemSize(cartItem.getId(), sizeId);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Cập nhật kích thước thất bại"));
                }
            }
            if (quantity > 0) {
                try {
                    cartService.updateCartItemQuantity(cartItem.getId(), quantity);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("message", "Cập nhật số lượng thất bại"));
                }
            }
            // Tính lại tổng giá trị của giỏ hàng sau khi cập nhật
            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật thành công",
                    "totalPrice", cart.getTotalPrice(),
                    "totalItems", cart.getTotalItem()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Cập nhật thất bại"));
        }
    }
}
