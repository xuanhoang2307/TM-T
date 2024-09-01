package huce.edu.web.services;

import java.util.List;

import huce.edu.web.model.Product;

public interface ProductService {
	List<Product> getAll();
	Boolean create(Product product);
	Product findById(Integer id);
	Boolean update(Product product);
	Boolean delete(Integer id);
}
