package huce.edu.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import huce.edu.web.model.User;
import huce.edu.web.services.UserService;

@Controller
@RequestMapping("/admin")
public class UserController {
	
	@Autowired
    private UserService userService;	
	
    @GetMapping("/users")
    public String displayUserList(@RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
    		Model model) {
    	Page<User> userPage = userService.getAllUsers(keyword, PageRequest.of(page, size));

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "admin/users/index";
    }

    @GetMapping("/user-lock/{id}")
    public String lockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.lockUser(id);
            redirectAttributes.addFlashAttribute("message", "Khóa tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi khóa tài khoản.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/user-unlock/{id}")
    public String unlockUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.unlockUser(id);
            redirectAttributes.addFlashAttribute("message", "Mở khóa tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi mở khóa tài khoản.");
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/user-delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        if (userService.delete(id)) {
            return "redirect:/admin/users";
        }
        return "admin/users/index";
    }
}
