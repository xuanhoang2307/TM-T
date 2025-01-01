package huce.edu.web.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import huce.edu.web.model.User;

public interface UserService {
	
	User findByUserName(String userName);
	User getUserById(Long userId);
	User findByEmail(String email);
	Page<User> getAllUsers(String keyword, Pageable pageable);
    User findById(Long id);
    User save(User user);
    Boolean delete(Long id);
    User getCurrentUser();
    void lockUser(Long userId);
    void unlockUser(Long userId);
}
