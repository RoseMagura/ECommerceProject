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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		log.info("Trying to create a new user");
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		log.debug("Username set with: {} ", createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if(createUserRequest.getPassword().length() < 7) {
			log.error("Error with user password: too short. Password must be at least 7 characters" +
					" Cannot create user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		else if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.error("Error with user password: passwords do not match. Cannot create user {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder
				.encode(createUserRequest.getPassword()
						+ new StringBuffer(createUserRequest.getUsername().toLowerCase()).reverse().toString()
				));
		log.debug("Is User being created correctly? {}", user.toString());
		userRepository.save(user);
		return ResponseEntity.ok(user);
	}
	
}
