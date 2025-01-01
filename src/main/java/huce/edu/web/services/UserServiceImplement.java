package huce.edu.web.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import huce.edu.web.model.User;
import huce.edu.web.repository.UserRepository;
import huce.edu.web.repository.UserRoleRepository;

@Service
public class UserServiceImplement implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	@Lazy
    private BCryptPasswordEncoder passwordEncoder; 
	
	@Override
	public User getUserById(Long userId) {
	    return userRepository.findById(userId).orElse(new User());
	}
	@Override
	public User findByUserName(String userName) {
		// TODO Auto-generated method stub
		return userRepository.findByUserName(userName);
	}
	@Override
	public Page<User> getAllUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByUserNameContainingIgnoreCase(keyword, pageable);
    }
	 
    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).get();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
    @Override
    public Boolean delete(Long id) {    	
    	try {
    		User user = findById(id);
            if (user != null) {
                userRoleRepository.deleteById(id);
                this.userRepository.delete(user);
                return true;
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
    	return false; 
    }
	@Override
	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email);
	}
	@Override
	public User getCurrentUser() {
        // Lấy thông tin người dùng từ SecurityContext
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Lấy tên đăng nhập từ phiên
        return userRepository.findByUserName(username);
    }
	@Override
	public void lockUser(Long id) {
		User user = userRepository.findById(id).orElse(null);
        user.setIsLocked(true);  // Cập nhật trạng thái khóa
        userRepository.save(user);
    }
	@Override
	public void unlockUser(Long id) {
		User user = userRepository.findById(id).orElse(null);
        user.setIsLocked(false);  // Cập nhật trạng thái mở khóa
        userRepository.save(user);
    }
}
