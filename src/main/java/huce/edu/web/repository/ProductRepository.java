package huce.edu.web.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import huce.edu.web.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category.categoryName IN :categories")
    Page<Product> findByCategoryIn(@Param("categories") List<String> categories, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.isActive = true AND p.productName LIKE %:keywordP%")
	Page<Product> searchProduct(String keywordP, Pageable pageable);
	
	Page<Product> findByIsActiveTrue(Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice")
	public Page<Product> findByPriceBetween(double minPrice, double maxPrice, Pageable pageable);
	

	@Query("SELECT p FROM Product p WHERE p.isActive = true AND p.color IN :colors")
	Page<Product> findByColorIn(@Param("colors") List<String> colors, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.isActive = true "
		     + "AND (:categories IS NULL OR p.category.categoryName IN :categories) "
		     + "AND (:colors IS NULL OR p.color IN :colors) "
		     + "AND (:minPrice IS NULL OR p.price >= :minPrice) "
		     + "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
	Page<Product> findByFilters(
		    @Param("categories") List<String> categories,
		    @Param("colors") List<String> colors,
		    @Param("minPrice") Double minPrice,
		    @Param("maxPrice") Double maxPrice,
		    Pageable pageable);
}
