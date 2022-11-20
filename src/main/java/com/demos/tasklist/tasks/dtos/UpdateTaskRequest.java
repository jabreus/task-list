package com.demos.tasklist.tasks.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskRequest {
  @NotBlank(message = "[content] property is required")
  private String content;
}
