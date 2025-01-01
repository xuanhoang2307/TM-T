package huce.edu.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import huce.edu.web.model.Product;

public interface ProductService {
	
	Boolean create(Product product);
	Product findById(Long id);
	Boolean update(Product product);
	Boolean delete(Long id);
	Page<Product> getAllPage(Integer pageNo);
	Page<Product> searchProduct(String keywordP, Integer pageNo);
	Page<Product> filterProductsByCategories(List<String> categories, int pageNo);
	Page<Product> getActiveProducts( int pageNo);
	Boolean hiddenProduct(Long productId);
	Page<Product> getProductsByPriceRange(double minPrice, double maxPrice, int pageNo);
	Page<Product> filterProductsByColors(List<String> colors, int pageNo);
	Page<Product> filterProductsByMultipleCriteria(List<String> categories,List<String> colors, double minPrice, double maxPrice,  int pageNo);
}
