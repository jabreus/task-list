package com.demos.tasklist.tasks.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto {
  private String id;
  private String content;
  private String userId;
}
