package com.demos.tasklist.tasks;

import com.demos.tasklist.tasks.dtos.CreateTaskRequest;
import com.demos.tasklist.tasks.dtos.TaskDto;
import com.demos.tasklist.tasks.dtos.UpdateTaskRequest;
import com.demos.tasklist.tasks.exceptions.TaskNotFoundException;
import com.demos.tasklist.users.User;
import com.demos.tasklist.utils.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  private final ModelMapper modelMapper;

  @GetMapping
  public Page<TaskDto> getAll(
      @RequestParam(required = false, defaultValue = "0") Integer page,
      @RequestParam(required = false, defaultValue = "10") Integer size,
      @CurrentUser User user) {
    var pageable = PageRequest.of(page, size);
    var taskPage = taskService.getAllByUsers(user.getId(), pageable);
    return taskPage.map(it -> modelMapper.map(it, TaskDto.class));
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<TaskDto> getById(@PathVariable String taskId, @CurrentUser User user)
      throws TaskNotFoundException {
    var task = taskService.getById(taskId, user);
    return ResponseEntity.ok(modelMapper.map(task, TaskDto.class));
  }

  @PostMapping
  public ResponseEntity<TaskDto> create(
    @Valid @RequestBody CreateTaskRequest createTaskRequest, @CurrentUser User user) {
    var task = taskService.create(createTaskRequest, user);
    return ResponseEntity.ok(modelMapper.map(task, TaskDto.class));
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<TaskDto> updateById(
      @PathVariable String taskId,
      @Valid @RequestBody UpdateTaskRequest updateTaskRequest,
      @CurrentUser User user)
      throws TaskNotFoundException {
    var task = taskService.update(taskId, updateTaskRequest, user);
    return ResponseEntity.ok(modelMapper.map(task, TaskDto.class));
  }

  @DeleteMapping("/{taskId}")
  public void deleteById(@PathVariable String taskId, @CurrentUser User user)
      throws TaskNotFoundException {
    taskService.deleteById(taskId, user);
  }
}
