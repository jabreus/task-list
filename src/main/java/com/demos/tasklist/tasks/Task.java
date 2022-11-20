package com.demos.tasklist.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(def = "{ id: 1, userId: 1 }")
public class Task {

  @Id private String id;
  private String content;
  private String userId;
}
