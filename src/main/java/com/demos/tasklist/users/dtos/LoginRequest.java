package com.demos.tasklist.users.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
  @NotBlank String username;
  @NotBlank String password;
}
