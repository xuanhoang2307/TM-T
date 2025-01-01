package huce.edu.web.controller.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.springframework.web.multipart.MultipartFile;
import huce.edu.web.model.Category;
import huce.edu.web.model.Product;
import huce.edu.web.model.Size;
import huce.edu.web.repository.SizeRepository;
import huce.edu.web.services.CategoryService;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.SizeService;
import huce.edu.web.services.StorageService;

@Controller
@RequestMapping("/admin")
public class ProductController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private SizeRepository sizeRepository;
	
	@Autowired
	private SizeService sizeService;
	
	@GetMapping("/product")
	public String displayindex(Model model, @Param("keywordP") String keywordP, 
			@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo) 
	{
		Page<Product> list = this.productService.getAllPage(pageNo);
		
		if (keywordP != null) {
			list = this.productService.searchProduct(keywordP, pageNo);
			model.addAttribute("keywordP", keywordP);
		}
		model.addAttribute("totalPageP", list.getTotalPages());
		model.addAttribute("currentPageP", pageNo);
		model.addAttribute("list", list);
		return "admin/product/index";			
	}
	
	@GetMapping("/product-add")
	public String add(Model model) {
		
		Product product = new Product();
		model.addAttribute("product", product);
		List<Category> listCate = this.categoryService.getAllActiveCategories();
		model.addAttribute("listCate", listCate);
		model.addAttribute("sizes", sizeRepository.findAll());
		return "admin/product/add";
	}
	
	@PostMapping("/product-add")
	public String save(@ModelAttribute("product") Product product,
			@RequestParam Set<Long> sizes,
			@RequestParam("fileImage") MultipartFile file,
			Model model) {
		
		//upload file
		if (!file.isEmpty()) { 
	        this.storageService.store(file);
	        String fileName = file.getOriginalFilename();
	        product.setImage(fileName);
	    }
		Set<Size> selectedSizes = new HashSet<>(sizeRepository.findAllById(sizes));
		product.setSizes(selectedSizes);
		if (this.productService.create(product)) {
			return "redirect:/admin/product";
		}
		return "admin/product/add";
	}
	
	@GetMapping("/product-edit/{id}")
	public String edit(Model model, @PathVariable("id") Long id) {

		Product product = this.productService.findById(id);
		model.addAttribute("product", product);
		List<Category> listCate = this.categoryService.getAllActiveCategories();
		model.addAttribute("listCate", listCate);
		List<Size> listSizes = this.sizeService.getAll();
	    model.addAttribute("listSizes", listSizes);
		return "/admin/product/edit";
	}
	
	@PostMapping("/product-edit")
	public String update(@ModelAttribute("product") Product product, 
			@RequestParam("fileImage") MultipartFile file,
			@RequestParam("sizeIds") Set<Long> sizeIds,
			Model model) {
		
		//upload file
		if (!file.isEmpty()) {
	        String fileName = this.storageService.store(file);
	        product.setImage(fileName); // Cập nhật ảnh mới
	    } else {
	        // Nếu không có file, giữ nguyên ảnh cũ
	        Product existingProduct = this.productService.findById(product.getId());
	        product.setImage(existingProduct.getImage()); // Giữ ảnh cũ
	    }
		Set<Size> sizes = new HashSet<>();
		    for (Long sizeId : sizeIds) {
		        Size size = this.sizeService.findById(sizeId);
		        if (size != null) {
		            sizes.add(size);
		        }
		    }
		product.setSizes(sizes);
		if (this.productService.update(product)) {
			return "redirect:/admin/product";
		}
		return "admin/product/edit";
	}
	
	@GetMapping("/delete-product/{id}")
	public String delete(@PathVariable("id") Long id) {
		if (this.productService.delete(id)) {
			return "redirect:/admin/product";
		} else {
			return "redirect:/admin/product";
		}
		
	}
	@GetMapping("/hidden/{id}")
	public String deleteProduct(@PathVariable Long id) {
	    if (this.productService.hiddenProduct(id)) {
			return "redirect:/admin/product";
		} else {
			return "redirect:/admin/product";
		}
	}
	
}
