package huce.edu.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import huce.edu.web.services.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private CustomUserDetailService customUserDetailService;

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests((authorize) -> authorize
			.requestMatchers("/*").permitAll()
			.requestMatchers("/backend/**").permitAll()
//			.requestMatchers("/backend/**").hasAuthority("ADMIN")
			.anyRequest().authenticated())
			.formLogin(login->login.loginPage("/login")
							.loginProcessingUrl("/login")
							.usernameParameter("username")
							.passwordParameter("password")
							.defaultSuccessUrl("/backend", true))
						.logout(logout->logout.logoutUrl("/admin-logout").logoutSuccessUrl("/login"));
		return http.build();
	}

	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web)->web.ignoring().requestMatchers("/static/**", "/fontend/**", "assets/**","uploads/**");

	}
}
