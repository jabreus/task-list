package com.demos.tasklist.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  @Query("{ username:'?0' }")
  Optional<User> findByUsername(String username);

  @Query(value = "{ username:'?0' }", exists = true)
  boolean existsByUsername(String username);
}
