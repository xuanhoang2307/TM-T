package huce.edu.web.controller.backend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import huce.edu.web.model.Category;
import huce.edu.web.model.Product;
import huce.edu.web.services.CategoryService;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.StorageService;


@Controller
@RequestMapping("/backend")
public class ProductController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("/product")
	public String displayindex(Model model) {
		
		List<Product> listCategory = this.productService.getAll();
		model.addAttribute("listCategory", listCategory);
		return "backend/product/index";
			
	}
	
	@GetMapping("/product-add")
	public String add(Model model) {
		
		Product product = new Product();
		model.addAttribute("product", product);
		List<Category> listCate = this.categoryService.getAll();
		model.addAttribute("listCate", listCate);
		return "backend/product/add";
	}
	
	@PostMapping("/product-add")
	public String save(@ModelAttribute("product") Product product, @RequestParam("fileImage") MultipartFile file) {
		
		//upload file
		if (!file.isEmpty()) { 
	        this.storageService.store(file);
	        String fileName = file.getOriginalFilename();
	        product.setImage(fileName);
	    }
		if (this.productService.create(product)) {
			return "redirect:/backend/product";
		}
		return "backend/product/add";
	}
	
	@GetMapping("/edit-product/{id}")
	public String edit(Model model, @PathVariable("id") Integer id) {

		Product product = this.productService.findById(id);
		model.addAttribute("product", product);
		List<Category> listCate = this.categoryService.getAll();
		model.addAttribute("listCate", listCate);
		return "/backend/product/edit";
	}
	
	@PostMapping("/product-edit")
	public String update(@ModelAttribute("product") Product product, @RequestParam("fileImage") MultipartFile file) {
		
		//upload file
		this.storageService.store(file);
		String fileName = file.getOriginalFilename();
		Boolean isEmpty = fileName == null || fileName.trim().length() == 0;
		if(isEmpty) {
			this.storageService.store(file);
			product.setImage(fileName);
		}
		product.setImage(fileName);
		if (this.productService.update(product)) {
			return "redirect:/backend/product";
		}
		return "backend/product/edit";
	}
	
	@GetMapping("/delete-product/{id}")
	public String delete(@PathVariable("id") Integer id) {
		if (this.productService.delete(id)) {
			return "redirect:/backend/product";
		} else {
			return "redirect:/backend/product";
		}
		
	}
	
}
