package com.saikumar.spboot.spboot;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.saikumar.spboot.spboot.models.AuthenticationRequest;
import com.saikumar.spboot.spboot.models.AuthenticationResponse;
import com.saikumar.spboot.spboot.services.MyUserDetailsService;
import com.saikumar.spboot.spboot.util.JwtUtil;

@RestController 
@RequestMapping(path="/start")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private MyUserDetailsService userDetailsService;
	
	@Autowired
	private JwtUtil jwtTokenUtil;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody String addNewUser (@RequestParam String name, @RequestParam String email) {
	    // @ResponseBody means the returned String is the response, not a view name
	    // @RequestParam means it is a parameter from the GET or POST request
	
	    User n = new User();
	    n.setName(name);
	    n.setEmail(email);
	    userRepository.save(n);
	    return "Saved";
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}
	
	@GetMapping(path="/user/{id}")
	public @ResponseBody Optional<User> getUserById(@PathVariable("id") Integer id) {
		// This returns a JSON or XML with the user
		return userRepository.findById(id);
	}
	
	@DeleteMapping(path="/delete/{id}")
	public @ResponseBody String deleteUserById(@PathVariable("id") Integer id) {
		userRepository.deleteById(id);
		return "deleted successfully";
	}
	
	@PutMapping(path="/update/{id}") 
	public @ResponseBody String updateUser (@PathVariable("id") Integer id, @RequestParam String name, @RequestParam String email) {
	    
	    Optional<User> userData = userRepository.findById(id);
	    if(userData.isPresent()) {
	    	User user = userData.get();
	    	user.setName(name);
	    	user.setEmail(email);
	    	userRepository.save(user);
		    return "Updated successfully";
	    }
	    else {
	    	return "cant find user with that id";
	    }
	}
	
	@GetMapping(path="/jello")
	public @ResponseBody String getAllUsers1() {
		// This returns a JSON or XML with the users
		return "<h1>Hello</h1>";
	}
	
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
			);
		}catch (BadCredentialsException e) {
			throw new Exception("Incorrect Username or password", e);
		}
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		
		System.out.println(userDetails.getUsername());
		
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		
		System.out.println(jwt);
		
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	

}
