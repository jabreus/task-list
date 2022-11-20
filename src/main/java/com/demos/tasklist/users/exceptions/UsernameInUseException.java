package com.demos.tasklist.users.exceptions;

import javax.validation.constraints.NotBlank;

public class UsernameInUseException extends Exception {
  public UsernameInUseException(@NotBlank String username) {
    super(String.format("Username in use: %s", username));
  }
}
