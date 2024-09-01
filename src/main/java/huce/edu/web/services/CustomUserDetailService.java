package huce.edu.web.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import huce.edu.web.model.CustomUserDetails;
import huce.edu.web.model.Role;
import huce.edu.web.model.User;
import huce.edu.web.model.UserRole;

@Service
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	private  UserService userService;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userService.findByUserName(username);
		if (user == null) {
			throw new UsernameNotFoundException("sai");
		}
		Collection<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
		Set<UserRole> userRoles = user.getUserRoles();
		for (UserRole userRole : userRoles) {
//			grantedAuthoritySet.add(new SimpleGrantedAuthority(userRoles.getClass().getName()));
			Role role = userRole.getRole();
            grantedAuthoritySet.add(new SimpleGrantedAuthority(role.getName()));
		}
		return new CustomUserDetails(user,grantedAuthoritySet);
	}

}
