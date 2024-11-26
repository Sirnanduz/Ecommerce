package pe.com.kusaytech.ecommerce.core.auth.user.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import pe.com.kusaytech.ecommerce.config.Apis;
import pe.com.kusaytech.ecommerce.core.auth.roles.model.RolModel;
import pe.com.kusaytech.ecommerce.core.auth.roles.service.RolService;
import pe.com.kusaytech.ecommerce.core.auth.user.controller.dto.RequestEmailDTO;
import pe.com.kusaytech.ecommerce.core.auth.user.controller.dto.RequestResetPasswordDTO;
import pe.com.kusaytech.ecommerce.core.auth.user.controller.dto.ResponseDTO;
import pe.com.kusaytech.ecommerce.core.auth.user.model.UserModel;
import pe.com.kusaytech.ecommerce.core.auth.user.service.UserService;
import pe.com.kusaytech.ecommerce.core.auth.user.utils.Constants;
import pe.com.kusaytech.ecommerce.exceptions.ValidationException;
import pe.com.kusaytech.ecommerce.security.JwtRequest;
import pe.com.kusaytech.ecommerce.security.JwtResponse;
import pe.com.kusaytech.ecommerce.security.utils.JwtTokenUtil;

@RestController
@RequestMapping(Apis.SECURITY_API)
@Validated
public class UserController {

	private static final Logger LOGGER = LogManager.getLogger(UserController.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;
	
	@Autowired
	private RolService rolService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Operation(summary = "Request password reset")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.STATUS_200, description = "Password reset email sent"),
			@ApiResponse(responseCode = Constants.STATUS_400, description = "User not found", content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = "Internal server error", content = @Content) })
	@PostMapping("/request-password-reset")
	public ResponseEntity<ResponseDTO<Void>> requestPasswordReset(@Valid @RequestBody RequestEmailDTO emailRequest) {
		try {
			userService.requestPasswordReset(emailRequest.getEmail());
			return ResponseEntity.ok(new ResponseDTO<>(200, "Password reset email sent", null));
		} catch (ValidationException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(500, "Internal server error", null));
		}
	}

	@Operation(summary = "Reset password")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Password reset successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid token or password", content = @Content),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content) })
	@PostMapping("/reset-password")
	public ResponseEntity<ResponseDTO<Void>> resetPassword(
			@Valid @RequestBody RequestResetPasswordDTO resetPasswordRequest) {
		try {
			userService.resetPassword(resetPasswordRequest.getToken(), resetPasswordRequest.getNewPassword());
			return ResponseEntity.ok(new ResponseDTO<>(200, "Password reset successfully", null));
		} catch (ValidationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO<>(400, e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(500, "Internal server error", null));
		}
	}

	@Operation(summary = "Register a new user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = Constants.STATUS_200, description = Constants.MSG_USER_CREATE_SUCCESS),
			@ApiResponse(responseCode = Constants.STATUS_400, description = Constants.MSG_USER_ERROR_VALIDATION, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = Constants.MSG_USER_ERROR_SERVER, content = @Content) })
	@PostMapping("/register")
	public ResponseEntity<ResponseDTO<UserModel>> registerUser(@Valid @RequestBody UserModel userModel) {
		try {
			if (userService.existsByEmail(userModel.getEmail())) {
				throw new ValidationException(Constants.MSG_USER_DUPLICITY);
			}
			userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
			userModel.setCreatedAt(LocalDateTime.now());
			userModel.setStatus(Constants.FLAG_ACTIVE);
			
			if(userModel.getRole()!=null) {
				Optional<RolModel>rol=rolService.getRoleById(userModel.getRole().getIdRole()!=null?userModel.getRole().getIdRole():0);
				if(rol.isEmpty()) {
					throw new DataIntegrityViolationException("Rol ingresado no existe");
				}
			}						
			userModel=userService.saveUser(userModel);
			
			userModel.setRole(rolService.getRoleById(userModel.getRole().getIdRole()).get());
			
			LOGGER.log(Level.INFO, Constants.MSG_USER_CREATE_SUCCESS);
			
			return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), Constants.MSG_USER_CREATE_SUCCESS, userModel));
		} catch (ValidationException e) {
			LOGGER.log(Level.INFO, e.getMessage());
			return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
		} catch (DataIntegrityViolationException e) {
			LOGGER.log(Level.INFO, e.getMessage());
			return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage(), null));
		} catch (Exception e) {
			LOGGER.log(Level.INFO, Constants.MSG_USER_ERROR_SERVER, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.MSG_USER_ERROR_SERVER, null));
		}
	}

	@Operation(summary = "Authenticate a user and generate a JWT token")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = Constants.STATUS_200, description = Constants.MSG_USER_AUTH_SUCCESS),
			@ApiResponse(responseCode = Constants.STATUS_400, description = Constants.MSG_USER_CREDENTIALS_INVALID, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = Constants.MSG_USER_ERROR_SERVER, content = @Content) })
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO<JwtResponse>> createAuthenticationToken(
			@Valid @RequestBody JwtRequest authenticationRequest) {
		try {
			authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

			final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

			UserModel userModel = userService.getUserByUsername(userDetails.getUsername());

			final String token = jwtTokenUtil.generateToken(userDetails);
			
			return ResponseEntity.ok(
					new ResponseDTO<>(HttpStatus.OK.value(), Constants.MSG_USER_AUTH_SUCCESS,
					new JwtResponse(token, userModel)));
			
		} catch (DisabledException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_DISABLED, null));
		} catch (BadCredentialsException e) {										
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), e.getLocalizedMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.MSG_USER_ERROR_SERVER, null));
		}
	}

	private void authenticate(String email, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (DisabledException e) {
			throw new DisabledException(Constants.MSG_USER_DISABLED, e);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException(Constants.MSG_USER_CREDENTIALS_INVALID, e);
		}
	}

	@Operation(summary = "Get all users")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = Constants.STATUS_200, description = Constants.MSG_USER_READ_SUCCESS),
			@ApiResponse(responseCode = Constants.STATUS_401, description = Constants.MSG_USER_UNAUTHORIZED, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = Constants.MSG_USER_ERROR_SERVER, content = @Content) })
	@GetMapping("/users")
	public ResponseEntity<ResponseDTO<List<UserModel>>> getAllUsers() {
		try {
			List<UserModel> users = userService.getAllUsers();
			return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), Constants.MSG_USER_READ_SUCCESS, users));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_UNAUTHORIZED, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.MSG_USER_ERROR_SERVER, null));
		}
	}

	@Operation(summary = "Get a user by ID")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = Constants.STATUS_200, description = Constants.MSG_USER_READ_SUCCESS),
			@ApiResponse(responseCode = Constants.STATUS_401, description = Constants.MSG_USER_UNAUTHORIZED, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_404, description = Constants.MSG_USER_NOTFOUND, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = Constants.MSG_USER_ERROR_SERVER, content = @Content) })
	@GetMapping("/users/{id}")
	public ResponseEntity<ResponseDTO<UserModel>> getUserById(@PathVariable Long id) {
		try {
			Optional<UserModel> user = userService.getUserById(id);
			if (user.isPresent()) {
				return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), Constants.MSG_USER_READ_SUCCESS, user.get()));
			} else {
				throw new ValidationException(Constants.MSG_USER_NOTFOUND);
			}
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_UNAUTHORIZED, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.MSG_USER_ERROR_SERVER, null));
		}
	}

	@Operation(summary = "Create a new user")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = Constants.STATUS_200, description = Constants.MSG_USER_UPDATED_SUCCESS),
			@ApiResponse(responseCode = Constants.STATUS_401, description = Constants.MSG_USER_UNAUTHORIZED, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_404, description = Constants.MSG_USER_NOTFOUND, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = Constants.MSG_USER_ERROR_SERVER, content = @Content) })
	@PostMapping("/users")
	public ResponseEntity<ResponseDTO<UserModel>> createUser(@Valid @RequestBody UserModel userModel) {
		try {
			if (userService.existsByEmail(userModel.getEmail())) {
				throw new ValidationException(Constants.MSG_USER_DUPLICITY);
			}
			userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
			userModel.setCreatedAt(LocalDateTime.now());
			userModel.setStatus(Constants.FLAG_ACTIVE);
			UserModel savedUser = userService.saveUser(userModel);
			return ResponseEntity.ok(new ResponseDTO<>(200, Constants.MSG_USER_CREATE_SUCCESS, savedUser));
			
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_UNAUTHORIZED, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.MSG_USER_ERROR_SERVER, null));
		}
	}

	@Operation(summary = "Update a user by ID")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = Constants.STATUS_200, description = Constants.MSG_USER_UPDATED_SUCCESS),
			@ApiResponse(responseCode = Constants.STATUS_401, description = Constants.MSG_USER_UNAUTHORIZED, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_404, description = Constants.MSG_USER_NOTFOUND, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = Constants.MSG_USER_ERROR_SERVER, content = @Content) })
	@PutMapping("/users/{id}")
	public ResponseEntity<ResponseDTO<UserModel>> updateUser(@PathVariable Long id,
			@Valid @RequestBody UserModel updatedUser) {
		try {
			Optional<UserModel> existingUserOpt = userService.getUserById(id);
			if (existingUserOpt.isPresent()) {
				UserModel existingUser = existingUserOpt.get();

				if (updatedUser.getUsername() != null)
					existingUser.setUsername(updatedUser.getUsername());
				if (updatedUser.getPassword() != null)
					existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
				if (updatedUser.getEmail() != null)
					existingUser.setEmail(updatedUser.getEmail());
				if (updatedUser.getFirstName() != null)
					existingUser.setFirstName(updatedUser.getFirstName());
				if (updatedUser.getLastName() != null)
					existingUser.setLastName(updatedUser.getLastName());
				if (updatedUser.getPhone() != null)
					existingUser.setPhone(updatedUser.getPhone());
				if (updatedUser.getDocumentType() != null)
					existingUser.setDocumentType(updatedUser.getDocumentType());
				if (updatedUser.getDocumentNumber() != null)
					existingUser.setDocumentNumber(updatedUser.getDocumentNumber());
				if (updatedUser.getRole().getIdRole() != null)
					existingUser.setRole(updatedUser.getRole());

				existingUser.setUpdatedAt(LocalDateTime.now());

				UserModel savedUser = userService.saveUser(existingUser);
				return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), Constants.MSG_USER_UPDATED_SUCCESS, savedUser));
			} else {
				throw new ValidationException(Constants.MSG_USER_NOTFOUND + ":"+id);
			}
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_UNAUTHORIZED, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.MSG_USER_ERROR_SERVER, null));
		}
	}

	@Operation(summary = "Delete a user by ID")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = Constants.STATUS_200, description = Constants.MSG_USER_UPDATED_SUCCESS),
			@ApiResponse(responseCode = Constants.STATUS_401, description = Constants.MSG_USER_UNAUTHORIZED, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_404, description = Constants.MSG_USER_NOTFOUND, content = @Content),
			@ApiResponse(responseCode = Constants.STATUS_500, description = Constants.MSG_USER_ERROR_SERVER, content = @Content) })
	@DeleteMapping("/users/{id}")
	public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable Long id) {
		try {
			Optional<UserModel> existingUser = userService.getUserById(id);
			if (existingUser.isPresent()) {
				userService.deleteUser(id);
				return ResponseEntity.noContent().build();
			} else {
				throw new ValidationException(Constants.MSG_USER_NOTFOUND + ":"+id);
			}
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ResponseDTO<>(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_UNAUTHORIZED, null));
		} catch (ValidationException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseDTO<>(404, e.getMessage(), null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.MSG_USER_ERROR_SERVER, null));
		}
	}
}