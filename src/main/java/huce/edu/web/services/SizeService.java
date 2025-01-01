package huce.edu.web.services;

import java.util.List;

import huce.edu.web.model.Size;

public interface SizeService {
	Size findById(Long id);
	List<Size> getAll();
}
