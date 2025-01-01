package huce.edu.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import huce.edu.web.model.Role;
import huce.edu.web.model.User;
import huce.edu.web.model.UserRole;
import huce.edu.web.repository.RoleRepository;
import huce.edu.web.repository.UserRoleRepository;
import huce.edu.web.services.UserService;

@Controller
@RequestMapping("/register")
public class RegisterController {
	
	@Autowired
    private UserService userService;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @GetMapping
    public String showRegisForm(Model model) {
    	User user = new User();
        model.addAttribute("user", user);
        return "layout/login/register";
    }
    
    @PostMapping
    public String registerUser(@ModelAttribute("user") User user,     		 
    		Model model) {
    	try {
            // Kiểm tra xem người dùng đã tồn tại chưa
            if (userService.findByUserName(user.getUserName()) != null) {
                model.addAttribute("errorname", "Username đã tồn tại");
                return "layout/login/register";
            }
            if (userService.findByEmail(user.getEmail()) != null) {
                model.addAttribute("erroremail", "Email đã tồn tại");
                return "layout/login/register";
            }
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassWord(passwordEncoder.encode(user.getPassWord()));
            user.setIsLocked(false);
            user = userService.save(user);
            Role defaultRole = roleRepository.findById(2L).orElseThrow(() -> new RuntimeException("Default role not found"));

            // Gán role cho user
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(defaultRole);;
            userRoleRepository.save(userRole);
                    
            model.addAttribute("success", "User registered successfully!");
            return "redirect:/login"; // Điều hướng tới trang đăng nhập           
             
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "layout/login/register";
        }
    }
}
