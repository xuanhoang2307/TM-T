package huce.edu.web.controller;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import huce.edu.web.model.User;
import huce.edu.web.model.UserRole;
import huce.edu.web.repository.UserRoleRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
public class LoginController {
	
	@Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
	@GetMapping("/login")
	public String showLoginForm(@RequestParam(value = "error", required = false) String error, 
            					@RequestParam(value = "logout", required = false) String logout,
            					Model model) {
		if (error != null) {
	        model.addAttribute("error", "Tên người dùng hoặc mật khẩu không chính xác.");
	    }
	    if (logout != null) {
	        model.addAttribute("message", "Đăng xuất thành công.");
	    }
		return "layout/login/login"; 
	}

	@PostMapping("/login")
    public String loginUser(@RequestParam("username") String username, 
                            @RequestParam("password") String password,
                            Model model, 
                            HttpSession session) {
        try {
            // Xác thực thông qua AuthenticationManager
        	Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        	User user = (User) userDetailsService.loadUserByUsername(username);  // Lấy thông tin User
            List<UserRole> userRoles = userRoleRepository.findByUser(user);  // Kiểm tra quyền trong UserRole

            if (userRoles.stream().noneMatch(role -> role.getRole().getId() == 2)) {  // Kiểm tra quyền User
                model.addAttribute("error", "Bạn không có quyền truy cập.");
                return "layout/login/login";
            }
            // Lưu thông tin người dùng vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home";  
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Sai username hoặc password! Hãy nhập lại.");
            return "layout/login/login";  
        }
    }
}
