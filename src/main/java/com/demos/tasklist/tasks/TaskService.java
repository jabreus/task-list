package com.demos.tasklist.tasks;

import com.demos.tasklist.tasks.dtos.CreateTaskRequest;
import com.demos.tasklist.tasks.dtos.UpdateTaskRequest;
import com.demos.tasklist.tasks.exceptions.TaskNotFoundException;
import com.demos.tasklist.users.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

  private final TaskRepository taskRepository;

  private final ModelMapper modelMapper;

  public Page<Task> getAllByUsers(@NonNull String userId, @NonNull Pageable pageable) {
    return taskRepository.findAllByUserId(userId, pageable);
  }

  public Task create(CreateTaskRequest createTaskRequest, User user) {
    var task = modelMapper.map(createTaskRequest, Task.class);
    task.setUserId(user.getId());
    return taskRepository.save(task);
  }

  public Task update(
      @NonNull String taskId, @NonNull UpdateTaskRequest updateTaskRequest, @NonNull User user)
      throws TaskNotFoundException {
    var task = getById(taskId, user);
    task.setContent(updateTaskRequest.getContent());
    return taskRepository.save(task);
  }

  public Task getById(String taskId, User user) throws TaskNotFoundException {
    return taskRepository
        .findByIdAndUserId(taskId, user.getId())
        .orElseThrow(() -> new TaskNotFoundException(taskId));
  }

  public void deleteById(@NonNull String taskId, @NonNull User user) throws TaskNotFoundException {
    if (!taskRepository.existsByIdAndUserId(taskId, user.getId()))
      throw new TaskNotFoundException(taskId);
    taskRepository.deleteById(taskId);
  }
}
