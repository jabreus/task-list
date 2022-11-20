package com.demos.tasklist.tasks.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequest {
  @NotBlank(message = "[content] property is required")
  String content;
}
