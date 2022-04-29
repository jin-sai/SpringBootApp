package com.saikumar.spboot.spboot.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.saikumar.spboot.spboot.User;
import com.saikumar.spboot.spboot.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userRepository.findByEmail(username);
		if(optionalUser.isPresent()) {
			User user = optionalUser.get();
			
			System.out.println(user.toString());
			
			List<String> roleList = new ArrayList<String>();
			roleList.add(user.getRole());
			
			System.out.println(roleList.toString());
			
			UserDetails user1 = org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
								.password(user.getPassword())
								.authorities(user.getRole())
								.build();
			
			return user1;
	        
//			return org.springframework.security.core.userdetails.User.builder()
//					.username(user.getEmail())
//					.password(user.getPassword())
//					.roles(roleList.toArray(new String[0]))
//					.build();
		}
		else {
			throw new UsernameNotFoundException("User Name is not Found");
		}
	}

	
}
