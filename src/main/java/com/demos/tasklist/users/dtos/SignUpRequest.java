package com.demos.tasklist.users.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
public class SignUpRequest {
  @NotBlank String username;
  @NotBlank String password;
}
