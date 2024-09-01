package huce.edu.web.services;

import java.util.List;

import huce.edu.web.model.User;

public interface UserService {
	
	User findByUserName(String userName);
	List<User> getAll();
    User findById(Long id);
    boolean create(User user);
    boolean update(User user);
    boolean delete(Long id);
}
