package com.demos.tasklist.tasks.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CreateTaskRequest {
  @NotBlank(message = "[content] property is required")
  String content;
}
