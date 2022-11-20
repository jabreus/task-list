package com.demos.tasklist.users;

import com.demos.tasklist.users.dtos.LoginRequest;
import com.demos.tasklist.users.dtos.LoginResponse;
import com.demos.tasklist.users.dtos.SignUpRequest;
import com.demos.tasklist.users.exceptions.UsernameInUseException;
import com.demos.tasklist.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final JwtUserDetailsService userDetailsService;

  private final JwtTokenUtil jwtTokenUtil;

  private final PasswordEncoder passwordEncoder;

  @PostMapping("/signin")
  public ResponseEntity<LoginResponse> signin(@Valid @RequestBody LoginRequest loginRequest) {
    var user = userDetailsService.loadUserByUsername(loginRequest.getUsername());
    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
      throw new UsernameNotFoundException(user.getUsername());
    return ResponseEntity.ok(new LoginResponse(jwtTokenUtil.generateToken(user)));
  }

  @PostMapping("/signup")
  public ResponseEntity<String> signin(@Valid @RequestBody SignUpRequest signupRequest)
      throws UsernameInUseException {
    var exists = userDetailsService.existsByUsername(signupRequest.getUsername());
    if (exists) throw new UsernameInUseException(signupRequest.getUsername());
    userDetailsService.register(signupRequest);
    return ResponseEntity.ok("Registered");
  }
}
