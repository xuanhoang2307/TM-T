package huce.edu.web.controller.backend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import huce.edu.web.model.Category;
import huce.edu.web.services.CategoryService;

@Controller
@RequestMapping("/backend")
public class categoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/category")
	public String displayindex(Model model,@Param("keyword") String keyword) {
		List<Category> list = this.categoryService.getAll();
		if (keyword != null) {
			list = this.categoryService.searchCategory(keyword);
			model.addAttribute("keyword", keyword);
		}
		model.addAttribute("list", list);
		return "backend/category/index";
	}

	@GetMapping("/add-category")
	public String displayAdd(Model model) {

		Category category = new Category();
		model.addAttribute("category", category);
		category.setCategoryStatus(true);
		return "backend/category/add";
	}

	@PostMapping("/add-category")
	public String save(@ModelAttribute("category") Category category) {

		if (this.categoryService.create(category)) {
			return "redirect:/backend/category";
		} else {
			return "backend/category/add";
		}

	}

	@GetMapping("/edit-category/{id}")
	public String edit(Model model, @PathVariable("id") Integer id) {

		Category category = this.categoryService.findById(id);
		model.addAttribute("category", category);
		return "/backend/category/edit";
	}

	@PostMapping("/edit-category")
	public String update(@ModelAttribute("category") Category category) {

		if (this.categoryService.create(category)) {
			return "redirect:/backend/category";
		} else {
			return "backend/category/add";
		}

	}
	
	@GetMapping("/delete-category/{id}")
	public String delete(@PathVariable("id") Integer id) {
		if (this.categoryService.delete(id)) {
			return "redirect:/backend/category";
		} else {
			return "redirect:/backend/category";
		}
		
	}
}
