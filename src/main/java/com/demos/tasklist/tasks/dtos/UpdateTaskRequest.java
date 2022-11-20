package com.demos.tasklist.tasks.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UpdateTaskRequest {
  @NotBlank(message = "[content] property is required")
  private String content;
}
