package huce.edu.web.controller.admin;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class AdminController {

	@GetMapping("/admin")
	public String index() {
		return "admin/index";
	}
	
}
