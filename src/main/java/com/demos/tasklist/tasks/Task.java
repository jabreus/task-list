package com.demos.tasklist.tasks;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks")
public class Task {

  @Id private String id;
  private String content;
}
