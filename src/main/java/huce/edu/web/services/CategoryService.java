package huce.edu.web.services;

import java.util.List;

import huce.edu.web.model.Category;

public interface CategoryService {
	List<Category> getAll();
	Boolean create(Category category);
	Category findById(Integer id);
	Boolean update(Category category);
	Boolean delete(Integer id);
	List<Category> searchCategory(String keyword);
}
