package com.demos.tasklist.tasks;

import com.demos.tasklist.tasks.dtos.CreateTaskRequest;
import com.demos.tasklist.tasks.dtos.TaskDto;
import com.demos.tasklist.tasks.dtos.UpdateTaskRequest;
import com.demos.tasklist.users.JwtUserDetailsService;
import com.demos.tasklist.users.User;
import com.demos.tasklist.users.dtos.LoginResponse;
import com.demos.tasklist.users.dtos.SignUpRequest;
import com.demos.tasklist.utils.MyMongoDbContainer;
import com.demos.tasklist.utils.RestPageImpl;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.testcontainers.shaded.org.hamcrest.MatcherAssert;
import org.testcontainers.shaded.org.hamcrest.Matchers;

import static java.lang.String.format;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Task tests")
class TaskControllerTest {

  @Container static MyMongoDbContainer mongo = MyMongoDbContainer.getInstance();

  @Autowired JwtUserDetailsService userDetailsService;

  @Autowired TaskService taskService;

  @LocalServerPort private int serverPort;

  private final RestTemplate client = new RestTemplate();

  private static String username;

  private static String accessToken;

  private static User validUser;

  private static final String DEFAULT_PASSWORD = "1234";

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", () -> mongo.getConnectionString() + "/instashared-dev");
  }

  @BeforeEach
  void setUp() {
    username = RandomStringUtils.randomAlphabetic(6);
    var request = SignUpRequest.builder().username(username).password(DEFAULT_PASSWORD).build();
    validUser = userDetailsService.register(request);
    accessToken =
        client
            .postForEntity(
                format("http://localhost:%d/v1/users/signin", serverPort),
                request,
                LoginResponse.class)
            .getBody()
            .getAccessToken();
  }

  @Test
  @DisplayName("Must return an empty page when the user has no tasks")
  void getAllNoTasks() {

    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    var request = new HttpEntity<Void>(headers);
    ParameterizedTypeReference<RestPageImpl<TaskDto>> responseType =
        new ParameterizedTypeReference<>() {};
    var response =
        client.exchange(
            format("http://localhost:%d/v1/tasks", serverPort),
            HttpMethod.GET,
            request,
            responseType);
    var tasks = response.getBody().getContent();
    MatcherAssert.assertThat("Empty list of tasks must be returned", tasks.size(), Matchers.is(0));
  }

  @Test
  @DisplayName("Get task with at least one")
  void getAllTask() {

    taskService.create(CreateTaskRequest.builder().content("Hello").build(), validUser);
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ParameterizedTypeReference<RestPageImpl<TaskDto>> responseType =
        new ParameterizedTypeReference<>() {};
    var request = new HttpEntity<>(headers);
    var response =
        client.exchange(
            format("http://localhost:%d/v1/tasks", serverPort),
            HttpMethod.GET,
            request,
            responseType);

    var tasks = response.getBody().getContent();

    MatcherAssert.assertThat("List must contain only one element", tasks.size(), Matchers.is(1));
  }

  @Test
  @DisplayName("Requesting a non-existing task must throw 404")
  void getByIdNotFound() {

    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    var request = new HttpEntity<Void>(headers);
    Assert.assertThrows(
        HttpClientErrorException.NotFound.class,
        () ->
            client.exchange(
                format("http://localhost:%d/v1/tasks/%s", serverPort, new ObjectId()),
                HttpMethod.GET,
                request,
                TaskDto.class));
  }

  @Test
  @DisplayName("Requesting an existing task must succeed")
  void getById() {

    var task = taskService.create(CreateTaskRequest.builder().content("Hello").build(), validUser);
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    var request = new HttpEntity<>(headers);
    var response =
        client.exchange(
            format("http://localhost:%d/v1/tasks/%s", serverPort, task.getId()),
            HttpMethod.GET,
            request,
            TaskDto.class);

    MatcherAssert.assertThat(
        "Task must be updated", response.getBody().getId(), Matchers.is(task.getId()));
  }

  @Test
  @DisplayName("Create task succeed")
  void create() {

    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    var taskContent = "A brand new task";
    var requestBody = CreateTaskRequest.builder().content(taskContent).build();
    var request = new HttpEntity<>(requestBody, headers);
    var response =
        client.exchange(
            format("http://localhost:%d/v1/tasks", serverPort),
            HttpMethod.POST,
            request,
            TaskDto.class);

    MatcherAssert.assertThat(
        "Retrieve task content must match",
        response.getBody().getContent(),
        Matchers.is(taskContent));
  }

  @Test
  @DisplayName("Cannot update non-existing task")
  void updateByIdNonExisting() {

    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    var body = UpdateTaskRequest.builder().content("Updated").build();
    var request = new HttpEntity<>(body, headers);
    Assert.assertThrows(
        HttpClientErrorException.NotFound.class,
        () ->
            client.exchange(
                format("http://localhost:%d/v1/tasks/%s", serverPort, new ObjectId()),
                HttpMethod.PUT,
                request,
                Void.class));
  }

  @Test
  @DisplayName("Can update existing task")
  void updateByIdExisting() {

    var task = taskService.create(CreateTaskRequest.builder().content("Hello").build(), validUser);
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    var newContent = "Updated";
    var body = UpdateTaskRequest.builder().content(newContent).build();
    var request = new HttpEntity<>(body, headers);
    var response =
        client.exchange(
            format("http://localhost:%d/v1/tasks/%s", serverPort, task.getId()),
            HttpMethod.PUT,
            request,
            TaskDto.class);

    MatcherAssert.assertThat(
        "Task must be updated", response.getBody().getContent(), Matchers.is(newContent));
  }

  @Test
  @DisplayName("Cannot delete a non-existing task")
  void deleteByIdNotFound() {
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    var request = new HttpEntity<Void>(headers);
    Assert.assertThrows(
        HttpClientErrorException.NotFound.class,
        () ->
            client.exchange(
                format("http://localhost:%d/v1/tasks/%s", serverPort, new ObjectId()),
                HttpMethod.DELETE,
                request,
                Void.class));
  }

  @Test
  @DisplayName("Can delete existing task")
  void deleteExistingTask() {

    var task = taskService.create(CreateTaskRequest.builder().content("Hello").build(), validUser);
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    var request = new HttpEntity<>(headers);

    var deleteResponse =
        client.exchange(
            format("http://localhost:%d/v1/tasks/%s", serverPort, task.getId()),
            HttpMethod.DELETE,
            request,
            Void.class);

    MatcherAssert.assertThat(
        "Delete succeeded", deleteResponse.getStatusCode(), Matchers.is(HttpStatus.OK));
  }
}
