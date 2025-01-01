package huce.edu.web.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import huce.edu.web.model.Role;
import huce.edu.web.model.User;
import huce.edu.web.model.UserRole;
import huce.edu.web.repository.UserRoleRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	private  UserService userService;
	
	@Autowired
    private UserRoleRepository userRoleRepository;
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userService.findByUserName(username);
		if (user == null) {
	        throw new UsernameNotFoundException("Tài khoản không tồn tại");
	    }
		
		List<UserRole> userRoles = userRoleRepository.findByUser(user);
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		for (UserRole userRole : userRoles) {
            Role role = userRole.getRole();
            authorities.add(new SimpleGrantedAuthority(role.getName()));  // Thêm quyền vào authorities
        }
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassWord(), authorities);
	}

}
