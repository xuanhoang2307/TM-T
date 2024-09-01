package huce.edu.web.controller.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/backend")
public class BackEndController {
	
	@GetMapping
	public String index() {
		return "redirect:/backend/";
	}
	
	@RequestMapping("/")
	public String admin() {
		return "backend/index";
	}
}
