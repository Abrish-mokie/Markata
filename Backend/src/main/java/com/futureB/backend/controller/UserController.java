package com.futureB.backend.controller;

import java.util.List;

import com.futureB.backend.Entity.ActivationToken;
import com.futureB.backend.Service.ActivationTokenService;
import com.futureB.backend.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.futureB.backend.Entity.User;
import com.futureB.backend.repository.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final ActivationTokenService activationTokenService;
	// get all Users
	@GetMapping("/Users")
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}

	// create User rest api
// send the Json in this form
//	{
//		"firstName": "asdfasdfas",
//			"lastName": "mm",
//			"emailId": "amir@gmail.com",
//			"password": "123123123",
//			"year" : "1",
//			"month" : "2",
//			"date" : "1"
//	}

	@PostMapping("/users")
	public ResponseEntity<String> createUser(@RequestBody User user) {
		// log
		//System.out.println("\n" + User + "\n");
		System.out.println(userRepository.findByEmailId(user.getEmailId()) + "\n");
		if(userRepository.findByEmailId(user.getEmailId())==null){
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			User userInDB = userRepository.save(user);

			ActivationToken activationToken = activationTokenService.createAndPersistActivationToken(userInDB);
			emailService.sendActivationEmail(userInDB, activationToken);

			return ResponseEntity.ok("Successful registration");
		}
		return ResponseEntity.status(409).body("User Already Exist. FirstName:" + user.getFirstName());

	}

	@GetMapping("/users/activate-account")
	public ResponseEntity<String> activateAccount(@RequestParam String token){
		if(activationTokenService.verifedAndAccountActivated(token)){
			return ResponseEntity.ok("Congrats you good to go!, you may try to login now");
		}else {
			return ResponseEntity.status(401).body("Opps! Something went wrong please try again");
		}
	}
	
	// get User by id rest api
//	@GetMapping("/Users/{emailid}")
//	public ResponseEntity<User> getUserByEmailId(@PathVariable String emailId) {
//		User user = userRepository.findByEmailId(emailId).orElseThrow(() -> new ResourceNotFoundException("User not exist with id :" + emailId));
////				orElseThrow(() -> new ResourceNotFoundException("User not exist with id :" + emailId));
//		return ResponseEntity.ok(user);
//	}
//
	// update User rest api
//	Not needed but here for future functionality
//	@PutMapping("/Users/{id}")
//	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails){
//		User user = userRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("User not exist with id :" + id));
//
//		user.setFirstName(userDetails.getFirstName());
//		user.setLastName(userDetails.getLastName());
//		user.setEmailId(userDetails.getEmailId());
//
//		User updatedUser = userRepository.save(user);
//		return ResponseEntity.ok(updatedUser);
//	}
	
	// delete User rest api
	// not needed but here for future functionality
//	@DeleteMapping("/Users/{id}")
//	public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id){
//		User user = userRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("User not exist with id :" + id));
//
//		userRepository.delete(user);
//		Map<String, Boolean> response = new HashMap<>();
//		response.put("deleted", Boolean.TRUE);
//		return ResponseEntity.ok(response);
//	}
	
	
}
