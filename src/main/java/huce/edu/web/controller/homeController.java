package huce.edu.web.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import huce.edu.web.model.Address;
import huce.edu.web.model.Cart;
import huce.edu.web.model.User;
import huce.edu.web.services.AddressService;
import huce.edu.web.services.CartService;
import huce.edu.web.services.UserService;
@Controller
@RequestMapping
public class homeController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
    private CartService cartService;
	
    @Autowired
    private AddressService addressService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

	@GetMapping("/home")
	public String display(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
	        return "redirect:/login";  
	    }
		return "index";
	}
	
	@GetMapping("/home/dashboard")
	public String ViewProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        model.addAttribute("user", user);
        
		return "/layout/users/dashboard";
	}
	@GetMapping("/home/account")
	public String ViewAccount(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        model.addAttribute("user", user);
		return "/layout/users/account";
	}
	@PostMapping("/home/account/change-password")
	public String changePassword(
	        @RequestParam("currentPassword") String currentPassword,
	        @RequestParam("newPassword") String newPassword,
	        @RequestParam("confirmPassword") String confirmPassword,
	        RedirectAttributes redirectAttributes) {
	    try {
	        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String username = authentication.getName();
	        User user = userService.findByUserName(username);

	        // Kiểm tra mật khẩu hiện tại
	        if (!passwordEncoder.matches(currentPassword, user.getPassWord())) {
	            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
	            return "redirect:/home/account";
	        }

	        // Kiểm tra xác nhận mật khẩu mới
	        if (!newPassword.equals(confirmPassword)) {
	            redirectAttributes.addFlashAttribute("error", "Xác nhận mật khẩu không khớp.");
	            return "redirect:/home/account";
	        }

	        // Cập nhật mật khẩu mới
	        user.setPassWord(passwordEncoder.encode(newPassword));
	        userService.save(user);

	        redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công.");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
	    }
	    return "redirect:/home/account";
	}
	@GetMapping("/home/cart")
    public String viewCart( Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // Lấy tên người dùng đã đăng nhập
        User user = userService.findByUserName(username);
        if (user == null) {
            model.addAttribute("message", "Người dùng không tồn tại.");
            return "layout/cart/cart"; 
        }
        Cart cart = cartService.findCartByUser(user);
        model.addAttribute("cart", cart);
        if (cart == null || cart.getCartItems().isEmpty()) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống.");
            model.addAttribute("cartItems", null);
        } else {
            model.addAttribute("cart", cart);
            model.addAttribute("cartItems", cart.getCartItems());
            model.addAttribute("totalPrice", cart.getTotalPrice());
            model.addAttribute("totalDiscountedPrice", cart.getTotalDiscountedPrice());
            model.addAttribute("totalItem", cart.getTotalItem());
        }
        return "layout/cart/cart";
    }
	@GetMapping("/home/cart/checkout")
	public String ViewCheckout(Model model) {
		
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  
        User user = userService.findByUserName(username);
        model.addAttribute("user", user);
  
        if (user != null) {
            List<Address> addresses = addressService.findByUser(user);
            Address defaultAddress = addresses.stream()
                                              .filter(Address::isDefault)
                                              .findFirst()
                                              .orElse(null);
            model.addAttribute("defaultAddress", defaultAddress);
        }
        List<Address> addresses = addressService.findByUser(user);
        if (addresses.isEmpty()) {
            // Thêm thông báo vào Model
            model.addAttribute("message", "Hiện tại bạn chưa có địa chỉ nào. Vui lòng thêm địa chỉ mới.");
        }
        model.addAttribute("addresses", addresses);
	    Cart cart = cartService.findCartByUser(user);
        model.addAttribute("cart", cart);
        if (cart == null || cart.getCartItems().isEmpty()) {
            model.addAttribute("message", "Giỏ hàng của bạn đang trống.");
            model.addAttribute("cartItems", null);
        } else {
            model.addAttribute("cart", cart);
            model.addAttribute("cartItems", cart.getCartItems());
            model.addAttribute("totalPrice", cart.getTotalPrice());
            model.addAttribute("totalDiscountedPrice", cart.getTotalDiscountedPrice());
            model.addAttribute("totalItem", cart.getTotalItem());
        }
		return "/layout/checkout/checkout";
	}
	
}
