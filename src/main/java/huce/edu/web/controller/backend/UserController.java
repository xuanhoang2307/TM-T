package huce.edu.web.controller.backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import huce.edu.web.model.Role;
import huce.edu.web.model.User;
import huce.edu.web.model.UserRole;
import huce.edu.web.services.RoleService;
import huce.edu.web.services.UserService;

@Controller
@RequestMapping("/backend")
public class UserController {
	
//	@RequestMapping("/login")
//	public String login() {
//		return "backend/login";
//	}
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
    @GetMapping("/users")
    public String displayUserList(Model model) {
        List<User> listUser = userService.getAll();
        model.addAttribute("listUser", listUser);
        return "backend/users/index";
    }

    @GetMapping("/user-add")
    public String addUserForm(Model model) {
        User user = new User();
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("user", user);
        model.addAttribute("userRoles", user.getUserRoles());
        return "backend/users/user-add";
    }

    @PostMapping("/user-add")
	public String addUser(@Validated @ModelAttribute("user") User user, BindingResult result,
			RedirectAttributes redirectAttributes) {
    	if (this.userService.create(user)) {
    		return "redirect:/backend/users";
    	}
    	user.setPassWord(passwordEncoder.encode(user.getPassWord()));
    	return "backend/users/user-add";
	}
    
    @GetMapping("/user-edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
    	User user = this.userService.findById(id);
    	List<Role> roles = roleService.getAllRoles();
    	model.addAttribute("user", user);
    	model.addAttribute("roles", roles);
//        model.addAttribute("userRoles", user.getUserRoles());
        return "backend/users/user-edit";
    }
    @PostMapping("/user-edit")
    public String editUser(@Validated @ModelAttribute("user") User user, BindingResult result,
                           RedirectAttributes redirectAttributes, Model model) {
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
     
        if (this.userService.update(user)) {
			return "redirect:/backend/users";
		}
		return "backend/users/user-edit";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        if (userService.delete(id)) {
            return "redirect:/backend/users";
        }
        return "backend/users";
    }
}
