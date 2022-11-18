package com.demos.tasklist.tasks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

  @Query("{ userId: ?0 }")
  Page<Task> findAllByUserId(String userId, PageRequest pageable);

  @Query("{ taskId: ?0, userId: ?1 }")
  Optional<Task> findByIdAndUserId(String taskId, String getId);
}
