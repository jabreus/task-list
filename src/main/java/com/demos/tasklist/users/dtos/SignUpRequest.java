package com.demos.tasklist.users.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
  @NotBlank String username;
  @NotBlank String password;
}
