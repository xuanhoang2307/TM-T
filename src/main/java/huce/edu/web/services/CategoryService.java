package huce.edu.web.services;

import java.util.List;

import org.springframework.data.domain.Page;

import huce.edu.web.model.Category;

public interface CategoryService {
	List<Category> getAll();
	Boolean create(Category category);
	Category findById(Integer id);
	Boolean update(Category category);
	List<Category> searchCategory(String keyword);
	Page<Category> getAllPage(Integer pageNo);
	Page<Category> searchCategory(String keyword, Integer pageNo);
	List<Category> getAllActiveCategories();
}
