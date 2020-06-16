package dwp.assessment.usersearch.heroku;

import dwp.assessment.usersearch.fixtures.HerokuUserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GIVEN a HerokuClient")
class HerokuClientTest {

  RestTemplate mockRestTemplate;

  @BeforeEach
  void setup() {
    mockRestTemplate = mock(RestTemplate.class);
  }

  @Test
  @DisplayName("AND a valid getUser by id request is made")
  void getUser() throws HerokuClientException {

    HerokuClient herokuClient = new HerokuClient("testUrl", mockRestTemplate);
    when(mockRestTemplate.getForEntity(contains("/user/1"), any()))
        .thenReturn(ResponseEntity.ok(HerokuUserFixture.mockUser()));

    HerokuUserDto herokuUserDto = herokuClient.getUser(1l);
    assertThat(herokuUserDto.getId(), equalTo(1l));
    assertThat(herokuUserDto.getFirstName(), equalTo("testFirstName1"));
    assertThat(herokuUserDto.getLastName(), equalTo("testLastName1"));
    assertThat(herokuUserDto.getEmail(), equalTo("testing1@email.com"));
    assertThat(herokuUserDto.getIpAddress(), equalTo("10.0.0.1"));
    assertThat(herokuUserDto.getLatitude(), equalTo(53.4807593));
    assertThat(herokuUserDto.getLongitude(), equalTo(-2.2426305));
    assertThat(herokuUserDto.getCity(), equalTo("testCity"));
  }

  @Test
  @DisplayName("AND user id not found THEN client exception is thrown")
  void getUserNotFoundTrowsClientException() {
    HerokuClient herokuClient = new HerokuClient("testUrl", mockRestTemplate);
    when(mockRestTemplate.getForEntity(contains("/user/99999"), any()))
        .thenThrow(new HttpClientErrorException(HttpStatus.valueOf(404)));

    final HerokuClientException thrown =
        assertThrows(HerokuClientException.class, () -> herokuClient.getUser(99999l));

    assertEquals("Error finding user for ID 99999", thrown.getMessage());
  }

  @Test
  @DisplayName("AND all users fetched OK")
  void getAllUsers() throws HerokuClientException {
    HerokuClient herokuClient = new HerokuClient("testUrl", mockRestTemplate);
    when(mockRestTemplate.exchange(
            contains("/users"),
            ArgumentMatchers.eq(HttpMethod.GET),
            any(HttpEntity.class),
            ArgumentMatchers.<ParameterizedTypeReference<List<HerokuUserDto>>>any()))
        .thenReturn(ResponseEntity.ok(HerokuUserFixture.mockUsers()));

    List<HerokuUserDto> herokuUsers = herokuClient.getAllUsers();

    assertThat(herokuUsers.get(0).getFirstName(), equalTo("testFirstName1"));
    assertThat(herokuUsers.get(1).getFirstName(), equalTo("testFirstName2"));
  }

  @Test
  @DisplayName("AND all users fetched WHERE No users found THEN an HerokuClientException is thrown")
  void getAllUsersException() {

    HerokuClient herokuClient = new HerokuClient("testUrl", mockRestTemplate);

    when(mockRestTemplate.exchange(
            contains("/users"),
            ArgumentMatchers.eq(HttpMethod.GET),
            any(HttpEntity.class),
            ArgumentMatchers.<ParameterizedTypeReference<List<HerokuUserDto>>>any()))
        .thenReturn(ResponseEntity.ok(new ArrayList<>()));

    final HerokuClientException thrown =
        assertThrows(HerokuClientException.class, herokuClient::getAllUsers);

    assertEquals("No users found", thrown.getMessage());
  }

  @Test
  @DisplayName(
      "AND all users fetched AND client connection issue THEN a HerokuClientException is thrown")
  void getAllUsersNoUsersFound() {
    HerokuClient herokuClient = new HerokuClient("testUrl", mockRestTemplate);

    when(mockRestTemplate.exchange(
            contains("/users"),
            ArgumentMatchers.eq(HttpMethod.GET),
            any(HttpEntity.class),
            ArgumentMatchers.<ParameterizedTypeReference<List<HerokuUserDto>>>any()))
        .thenThrow(new HttpClientErrorException(HttpStatus.valueOf(408)));

    final HerokuClientException thrown =
        assertThrows(HerokuClientException.class, herokuClient::getAllUsers);

    assertEquals("Error finding all users 408 REQUEST_TIMEOUT", thrown.getMessage());
  }

  @Test
  @DisplayName("AND users returned when searching by city")
  void getUsersInCity() throws HerokuClientException {
    HerokuClient herokuClient = new HerokuClient("testUrl", mockRestTemplate);
    when(mockRestTemplate.exchange(
            contains("/city/London/users"),
            ArgumentMatchers.eq(HttpMethod.GET),
            any(HttpEntity.class),
            ArgumentMatchers.<ParameterizedTypeReference<List<HerokuUserDto>>>any()))
        .thenReturn(ResponseEntity.ok(HerokuUserFixture.mockUsers()));
    List<HerokuUserDto> herokuUsers = herokuClient.getUsersInCity("London");
    assertThat(herokuUsers.size(), equalTo(2));
    assertThat(herokuUsers.get(0).getFirstName(), equalTo("testFirstName1"));
    assertThat(herokuUsers.get(1).getFirstName(), equalTo("testFirstName2"));
  }

  @Test
  @DisplayName("AND no users returned when searching by city")
  void getUsersInCityNonFound() throws HerokuClientException {
    HerokuClient herokuClient = new HerokuClient("testUrl", mockRestTemplate);
    when(mockRestTemplate.exchange(
            contains("/city/London/users"),
            ArgumentMatchers.eq(HttpMethod.GET),
            any(HttpEntity.class),
            ArgumentMatchers.<ParameterizedTypeReference<List<HerokuUserDto>>>any()))
        .thenReturn(ResponseEntity.ok(new ArrayList<>()));
    List<HerokuUserDto> herokuUsers = herokuClient.getUsersInCity("London");
    assertThat(herokuUsers.size(), equalTo(0));
  }
}
