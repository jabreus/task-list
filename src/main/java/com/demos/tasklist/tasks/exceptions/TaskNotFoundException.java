package com.demos.tasklist.tasks.exceptions;

import lombok.NonNull;

public class TaskNotFoundException extends Exception {
  public TaskNotFoundException(@NonNull String taskId) {
    super(String.format("Task not found: %s", taskId));
  }
}
