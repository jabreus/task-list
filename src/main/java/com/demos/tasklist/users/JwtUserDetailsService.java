package com.demos.tasklist.users;

import com.demos.tasklist.users.dtos.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    return new User(user.getUsername(), user.getPassword(), List.of());
  }

  public com.demos.tasklist.users.User getByUsername(String username)
      throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public com.demos.tasklist.users.User register(SignUpRequest signupRequest) {
    return userRepository.save(
        new com.demos.tasklist.users.User(
            null,
            signupRequest.getUsername(),
            passwordEncoder.encode(signupRequest.getPassword())));
  }
}
