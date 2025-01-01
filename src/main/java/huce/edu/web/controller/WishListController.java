package huce.edu.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import huce.edu.web.model.Product;
import huce.edu.web.model.User;
import huce.edu.web.model.WishList;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.UserService;
import huce.edu.web.services.WishListService;

@Controller
@RequestMapping("/home")
public class WishListController {
	
	@Autowired
    private WishListService wishlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    
    @GetMapping("/wish-list")
    public String viewWishlist(Model model, 
    		@RequestParam(defaultValue = "0") int page, 
    		@RequestParam(defaultValue = "5") int size) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Lấy tên người dùng đã đăng nhập
        User user = userService.findByUserName(username);
        if (user == null) {
            model.addAttribute("message", "Người dùng không tồn tại.");
            return "layout/index"; 
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<WishList> wishlistPage = wishlistService.getWishlistByUser(user, pageable);
        
        model.addAttribute("wishlist", wishlistPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wishlistPage.getTotalPages());
        model.addAttribute("totalItems", wishlistPage.getTotalElements());
        return "layout/users/wishlist"; 
    }

    @PostMapping("/wish-list/add")
    public ResponseEntity<String> addToWishlist(@RequestParam("productId") Long productId) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        
        Product product = productService.findById(productId);

        if (user != null && product != null) {
            wishlistService.addToWishlist(user, product);
            return ResponseEntity.ok("Sản phẩm đã được thêm vào wishlist");
        }

        return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm hoặc người dùng");
    }
    @PostMapping("/wish-list/remove")
    public ResponseEntity<String> removeFromWishlist(@RequestParam Long wishlistId) {
        wishlistService.removeProductFromWishlist(wishlistId);
        return ResponseEntity.ok("Product removed from wishlist");
    }
    @DeleteMapping("/wish-list/remove")
    public ResponseEntity<String> removeProductFromWishlist(@RequestParam("productId") Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);

        Product product = productService.findById(productId);

        if (user != null && product != null) {
            wishlistService.removeFromWishlist(user, product);
            return ResponseEntity.ok("Sản phẩm đã được xóa khỏi wishlist");
        }

        return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm hoặc người dùng");
    }

}
