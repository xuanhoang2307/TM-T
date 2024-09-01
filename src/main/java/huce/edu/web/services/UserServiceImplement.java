package huce.edu.web.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import huce.edu.web.model.User;
import huce.edu.web.repository.UserRepository;

@Service
public class UserServiceImplement implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	@Override
	public User findByUserName(String userName) {
		// TODO Auto-generated method stub
		return userRepository.findByUserName(userName);
	}
	@Override
    public List<User> getAll() { 
        return userRepository.findAll();
    }
	 
    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).get();
    }

    @Override
    public boolean create(User user) {
        try {
			this.userRepository.save(user);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
    }

    @Override
    public boolean update(User user) {
    	try {
			this.userRepository.save(user);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
    }
    
    @Override
    public boolean delete(Long id) {
    	
    	try {
			this.userRepository.delete(findById(id));
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false; 
    }
}
