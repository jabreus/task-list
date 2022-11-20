package com.demos.tasklist.users;

import com.demos.tasklist.users.dtos.LoginRequest;
import com.demos.tasklist.users.dtos.LoginResponse;
import com.demos.tasklist.users.dtos.SignUpRequest;
import com.demos.tasklist.utils.MyMongoDbContainer;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
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
@DisplayName("Authentication tests")
class UserControllerTest {

  @Container static MyMongoDbContainer mongo = MyMongoDbContainer.getInstance();

  @Autowired JwtUserDetailsService userDetailsService;

  @LocalServerPort private int serverPort;

  private final RestTemplate client = new RestTemplate();

  private static String username;

  private static final String DEFAULT_PASSWORD = "1234";

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", () -> mongo.getConnectionString() + "/instashared-dev");
  }

  @BeforeEach
  void setUp() {
    username = RandomStringUtils.randomAlphabetic(6);
    var request = SignUpRequest.builder().username(username).password(DEFAULT_PASSWORD).build();
    userDetailsService.register(request);
  }

  @Test
  @DisplayName("Non-existing user cannot sign-in")
  void signinNonExistingUserThenUnauthorized() {
    var randomUsername = RandomStringUtils.randomAlphabetic(6);
    var randomPassword = RandomStringUtils.randomAlphanumeric(10);
    var request = SignUpRequest.builder().username(randomUsername).password(randomPassword).build();
    Assert.assertThrows(
        HttpClientErrorException.Unauthorized.class,
        () ->
            client
                .postForEntity(
                    format("http://localhost:%d/v1/users/signin", serverPort),
                    request,
                    LoginResponse.class)
                .getStatusCode());
  }

  @Test
  @DisplayName("Existing user can sign-in")
  void signinExistingUser() {
    var request = LoginRequest.builder().username(username).password(DEFAULT_PASSWORD).build();
    var response =
        client.postForEntity(
            format("http://localhost:%d/v1/users/signin", serverPort),
            request,
            LoginResponse.class);
    MatcherAssert.assertThat(
        "Must return an accessToken", response.getBody().getAccessToken(), Matchers.notNullValue());
  }

  @Test
  @DisplayName("Cannot register with existing user")
  void cannotRegisterExistingUser() {
    var request = SignUpRequest.builder().username(username).password(DEFAULT_PASSWORD).build();
    Assert.assertThrows(
        HttpClientErrorException.Conflict.class,
        () ->
            client
                .postForEntity(
                    format("http://localhost:%d/v1/users/signup", serverPort),
                    request,
                    String.class)
                .getStatusCode());
  }

  @Test
  @DisplayName("Sign up with non-existing user")
  void canSignUp() {
    var request =
        SignUpRequest.builder()
            .username(RandomStringUtils.randomAlphabetic(10))
            .password(DEFAULT_PASSWORD)
            .build();
    var httpStatus =
        client
            .postForEntity(
                format("http://localhost:%d/v1/users/signup", serverPort), request, String.class)
            .getStatusCode();
    MatcherAssert.assertThat("Response must be 200", httpStatus, Matchers.is(HttpStatus.OK));
  }
}
