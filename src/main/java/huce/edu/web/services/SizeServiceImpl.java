package huce.edu.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import huce.edu.web.model.Size;
import huce.edu.web.repository.SizeRepository;

@Service
public class SizeServiceImpl implements SizeService{
	
	@Autowired
	private SizeRepository sizeRepository;
	@Override
	public Size findById(Long id) {
		// TODO Auto-generated method stub
		return sizeRepository.findById(id).orElse(null);
	}
	public List<Size> getAll() {
        return sizeRepository.findAll();
    }
}
