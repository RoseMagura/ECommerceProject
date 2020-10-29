package com.example.demo.controllers;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
//@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public static final Logger log = LogManager.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		//to see which user is being fetched
		log.info("Fetching user: {}", userRepository.findById(id));
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		log.debug("Processing {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null){
			log.error(username, " not found");
			return ResponseEntity.notFound().build();
		}
		else {
			log.info(user.getUsername() + " found");
			return ResponseEntity.ok(user);
		}
	}

	@PostMapping("/signup")
//	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
	public
//			void
	ResponseEntity<User>
	createUser(@RequestParam MultiValueMap data) {

		log.info("Trying to create a new user from User Controller with {}", data);
		User user = new User();
//		log.debug("Username set with: {} ", createUserRequest.getUsername());
		String username = (String) data.getFirst("username");
		String password = (String) data.getFirst("password");
		String confirmPassword = (String) data.getFirst("confirmPassword");
		System.out.println("Username: " + username);
		System.out.println("Password: " + password);
		System.out.println("ConfirmPassword: " + confirmPassword);

		user.setUsername(username);
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		assert password != null;
		if(password.length() < 7) {
			log.error("Error with user password: too short. Password must be at least 7 characters" +
					" Cannot create user {}", username);
			return ResponseEntity.badRequest().build();
		}
		else if(!password.equals(confirmPassword)){
			log.error("Error with user password: passwords do not match. Cannot create user {}", username);
			return ResponseEntity.badRequest().build();
		}
		assert username != null;
		user.setPassword(bCryptPasswordEncoder
				.encode(password
						+ new StringBuffer(username.toLowerCase()).reverse().toString()
				));
		log.debug("Is User being created correctly? {}", user.toString());
		userRepository.save(user);
		return ResponseEntity.ok(user);
//		return null;
	}
	
}
