package huce.edu.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class controller {
	
	@RequestMapping("home")
	public String display() {
		return "index";
	}
}
