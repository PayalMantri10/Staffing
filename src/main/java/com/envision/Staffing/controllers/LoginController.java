package com.envision.Staffing.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.envision.Staffing.model.AuthenticationResponse;
import com.envision.Staffing.model.UserAuth;
import com.envision.Staffing.services.JwtUtil;
import com.envision.Staffing.services.MyUserDetailsService;

@RestController
public class LoginController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private MyUserDetailsService userDetailsService;

	Logger log = Logger.getLogger(LoginController.class);

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestParam(value = "username") String username,
			@RequestParam(value = "pass") String password) throws Exception {

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (BadCredentialsException e) {
			log.error("Incorrect username or password", e);
			throw new Exception("Incorrect username or password", e);
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		final UserAuth userauth = userDetailsService.getUser(username);
		final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
		final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken, userauth.getRoles(),
				userauth.getId(), userauth.getName(), userauth.getEmail()));
	}

	// To Validate refresh token when access token expires
	@RequestMapping(value = "/token", method = RequestMethod.POST)
	public ResponseEntity<?> sendRefreshToken(@RequestParam(value = "refresh-token") String refreshToken,
			@RequestParam(value = "current-user") String username) throws Exception {

		final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		if (jwtTokenUtil.validateToken(refreshToken, userDetails)) {
			final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
			return ResponseEntity.ok(new AuthenticationResponse(accessToken));
		}
		return new ResponseEntity(HttpStatus.UNAUTHORIZED);
	}

}
