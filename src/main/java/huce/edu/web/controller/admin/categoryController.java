package huce.edu.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import huce.edu.web.model.Category;
import huce.edu.web.services.CategoryService;

@Controller
@RequestMapping("/admin")
public class categoryController {

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/category")
	public String displayindex(Model model,@Param("keyword") String keyword,
			@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo) {
		Page<Category> list = this.categoryService.getAllPage(pageNo);
		
		if (keyword != null) {
			list = this.categoryService.searchCategory(keyword, pageNo);
			model.addAttribute("keyword", keyword);
		}
		model.addAttribute("totalPage", list.getTotalPages());
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("list", list);
		return "admin/category/index";
	}

	@GetMapping("/add-category")
	public String displayAdd(Model model) {

		Category category = new Category();
		model.addAttribute("category", category);
		category.setCategoryStatus(true);
		return "admin/category/add";
	}

	@PostMapping("/add-category")
	public String save(@ModelAttribute("category") Category category) {

		if (this.categoryService.create(category)) {
			return "redirect:/admin/category";
		} else {
			return "admin/category/add";
		}

	}

	@GetMapping("/edit-category/{id}")
	public String edit(Model model, @PathVariable("id") Integer id) {

		Category category = this.categoryService.findById(id);
		model.addAttribute("category", category);
		return "/admin/category/edit";
	}

	@PostMapping("/edit-category")
	public String update(@ModelAttribute("category") Category category) {

		if (this.categoryService.create(category)) {
			return "redirect:/admin/category";
		} else {
			return "admin/category/add";
		}

	}
	
}
