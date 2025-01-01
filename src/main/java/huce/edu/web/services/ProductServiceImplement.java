package huce.edu.web.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import huce.edu.web.model.Product;
import huce.edu.web.repository.ProductRepository;

@Service
public class ProductServiceImplement implements ProductService{
	
	@Autowired
	private ProductRepository productRepository;

	@Override
	public Boolean create(Product product) {
		try {
		        Product savedProduct = this.productRepository.save(product);
		        return savedProduct != null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Product findById(Long id) {
		return this.productRepository.findById(id).get();
	}

	@Override
	public Boolean update(Product product) {
		try {
			this.productRepository.save(product);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Boolean delete(Long id) {
		// TODO Auto-generated method stub
		try {
			this.productRepository.delete(findById(id));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false; 
	}
	
	@Override
	public Page<Product> getAllPage(Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo -1, 5);
		return this.productRepository.findAll(pageable);
	}

	@Override
	public Page<Product> searchProduct(String keywordP, Integer pageNo) {
	    Pageable pageable = PageRequest.of(pageNo - 1, 5); 
	    return productRepository.searchProduct(keywordP, pageable);
	}

	@Override
	public Page<Product> filterProductsByCategories(List<String> categories, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5); 
        return productRepository.findByCategoryIn(categories, pageable);
    }
	@Override
	public Page<Product> getActiveProducts( int pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 5);
        return productRepository.findByIsActiveTrue(pageable);
    }
	@Override
    public Boolean hiddenProduct(Long productId) {
        try {
        	Product product = productRepository.findById(productId).orElse(null);
            product.setIsActive(false); // Chuyển trạng thái sang không hoạt động
            productRepository.save(product);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false; 
    }
	
	@Override
	public Page<Product> getProductsByPriceRange(double minPrice, double maxPrice, int pageNo) {
	    Pageable pageable = PageRequest.of(pageNo - 1, 5, Sort.by("price").ascending());
	    return productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
	}
	@Override
	public Page<Product> filterProductsByColors(List<String> colors, int pageNo) {
	    // Lọc sản phẩm theo màu sắc
	    return productRepository.findByColorIn(colors, PageRequest.of(pageNo - 1, 5));
	}
	@Override
	public Page<Product> filterProductsByMultipleCriteria(List<String> categories,List<String> colors, double minPrice, double maxPrice,  int pageNo) {
	    // Lọc kết hợp theo danh mục, giá, và màu sắc
	    return productRepository.findByFilters(categories,colors, minPrice, maxPrice,  PageRequest.of(pageNo - 1, 5));
	}
}
