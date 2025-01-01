package huce.edu.web.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import huce.edu.web.model.Product;
import huce.edu.web.model.Review;
import huce.edu.web.model.User;
import huce.edu.web.services.ProductService;
import huce.edu.web.services.ReviewService;
import huce.edu.web.services.UserService;
import huce.edu.web.services.WishListService;

@Controller
@RequestMapping("/home")
public class ProductHomeController {
	@Autowired
	private ProductService productService;
	
	@Autowired
    private UserService userService;

	@Autowired
	private WishListService wishListService;
	
	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("/products")
    public String displayProductList(Model model, 
    		@Param("keywordP") String keywordP,
    		@RequestParam(name = "minPrice", defaultValue = "0") double minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "1000") double maxPrice,
    		@RequestParam(name = "category", required = false) List<String> categories,
    		@RequestParam(name = "color", required = false) List<String> colors,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo ) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
		Page<Product> lists ;
        
        if (keywordP != null && !keywordP.isEmpty()) {
            lists = this.productService.searchProduct(keywordP, pageNo);
            model.addAttribute("keywordP", keywordP); // Lưu từ khóa vào Model
        } else if (categories != null && !categories.isEmpty()) { // Nếu có lọc theo thể loại
            lists = this.productService.filterProductsByCategories(categories, pageNo);
            model.addAttribute("categories", categories); 
        } else if (minPrice > 0 || maxPrice < 1000) {
        	lists = this.productService.getProductsByPriceRange(minPrice, maxPrice, pageNo);
        	model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
		} else if (colors != null && !colors.isEmpty()) {
	        lists = this.productService.filterProductsByColors(colors, pageNo);
	        model.addAttribute("colors", colors);
		} else if ((categories != null && !categories.isEmpty()) || 
	               (minPrice > 0 || maxPrice < 1000) || 
	               (colors != null && !colors.isEmpty())) {
	        lists = this.productService.filterProductsByMultipleCriteria(categories, colors, minPrice, maxPrice, pageNo);
	        model.addAttribute("categories", categories);
	        model.addAttribute("minPrice", minPrice);
	        model.addAttribute("maxPrice", maxPrice);
	        model.addAttribute("colors", colors);
	        
		} else {
            lists = this.productService.getActiveProducts(pageNo); 
        }
        List<Product> productsWithRatings = new ArrayList<>();
        for (Product product : lists.getContent()) {
            double averageRating = reviewService.calculateAverageRating(product); // Tính rating trung bình
            product.setAverageRating(averageRating); // Gán rating trung bình vào sản phẩm
            productsWithRatings.add(product); // Thêm vào danh sách sản phẩm với rating
            if (!"anonymousUser".equals(username)) {
                User user = userService.findByUserName(username);
                boolean inWishlist = wishListService.isProductInWishlist(user, product);
                model.addAttribute("inWishlist", inWishlist);
            } else {
                model.addAttribute("inWishlist", false);
            }
        }
        model.addAttribute("totalPageP", lists.getTotalPages());
        model.addAttribute("currentPageP", pageNo);
        model.addAttribute("lists", lists);
        
        return "layout/products/products"; 
    }
	@GetMapping("/products/{id}")
    public String viewProductDetails(@PathVariable("id") Long id, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String username = authentication.getName();
        Product product = productService.findById(id);

        if (product == null) {
            model.addAttribute("message", "Sản phẩm không tồn tại.");
            return "redirect:/home/products";
        }

        List<Review> reviews = reviewService.findReviewsByProduct(product);
        double averageRating = reviewService.calculateAverageRating(product);

        model.addAttribute("product", product);
        model.addAttribute("sizes", product.getSizes());
        model.addAttribute("reviews", reviews != null ? reviews : new ArrayList<>()); // Bảo vệ nếu reviews bị null
        model.addAttribute("averageRating", averageRating);

        if (!"anonymousUser".equals(username)) {
            User user = userService.findByUserName(username);
            boolean inWishlist = wishListService.isProductInWishlist(user, product);
            model.addAttribute("inWishlist", inWishlist);
        } else {
            model.addAttribute("inWishlist", false);
        }

        return "layout/products/items";
    }
	
}
