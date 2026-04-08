package com.DESAFIOSpringBootestruturado.services;

import com.DESAFIOSpringBootestruturado.dto.UserDTO;
import com.DESAFIOSpringBootestruturado.entities.Role;
import com.DESAFIOSpringBootestruturado.entities.User;
import com.DESAFIOSpringBootestruturado.projections.UserDetailsProjection;
import com.DESAFIOSpringBootestruturado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		if (result.isEmpty()) {
			throw new UsernameNotFoundException("Email not found");
		}

		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}

		return user;
	}

	//------------------------------------------------------
	@Transactional(readOnly = true)
	protected User authenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username;

		Object principal = authentication.getPrincipal();
		if (principal instanceof Jwt jwt) {
			username = jwt.getClaimAsString("username"); // ou "sub" se for o padrão
		} else if (principal instanceof UserDetails userDetails) {
			username = userDetails.getUsername();
		} else {
			username = authentication.getName();
		}

		return repository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("Email not found"));
	}

	//------------------------------------------------------
	@Transactional(readOnly = true)
	public UserDTO getMe() {
		User user = authenticated();
		return new UserDTO(user);
	}
}