package dwp.assessment.usersearch;

import com.github.tomakehurst.wiremock.WireMockServer;
import dwp.assessment.usersearch.user.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"application.heroku-url=http://localhost:8099"})
@DisplayName("End to End Integration Tests with a Stub Heroku Server")
class IntegrationTest {

  WireMockServer herokuMockServer;
  TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  @BeforeEach
  public void setup() {
    restTemplate = new TestRestTemplate();
    herokuMockServer = new WireMockServer(8099);
    herokuMockServer.start();
  }

  @AfterEach
  public void tearDown() {
    herokuMockServer.stop();
  }

  @Test
  @DisplayName("Get One User")
  void getOnePerson()  {
    herokuMockServer.stubFor(
        get(urlEqualTo("/user/1"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(fileContents("json/example-user.json"))));

    String url = "http://localhost:" + port + "/user/1";

    ResponseEntity<UserDto> personHttpEntity = restTemplate.getForEntity(url, UserDto.class);

    UserDto person = personHttpEntity.getBody();

    assertThat(personHttpEntity.getStatusCode().value(), equalTo(200));

    assertThat(person.getFirstName(), equalTo("firstName"));
    assertThat(person.getLastName(), equalTo("lastName"));
    assertThat(person.getEmail(), equalTo("peterssquare@email.com"));
  }

  @Test
  @DisplayName("Get Users in London")
  void getAllPeopleInLondon() {
    herokuMockServer.stubFor(
        get(urlEqualTo("/city/London/users"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(fileContents("json/multiple-users.json"))));

    String url = "http://localhost:" + port + "/user/city/London";

    ParameterizedTypeReference<List<UserDto>> typeRef = new ParameterizedTypeReference<>() {};
    ResponseEntity<List<UserDto>> personHttpEntity =
        restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, typeRef);

    List<UserDto> people = personHttpEntity.getBody();
    assertThat(personHttpEntity.getStatusCode().value(), equalTo(200));
    assertThat(people.size(), equalTo(6));
  }

  @Test
  @DisplayName("Get Users IN London Within 20 miles")
  void getAllPeopleInManchesterWithin20Miles() {

    herokuMockServer.stubFor(
        get(urlEqualTo("/users"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(fileContents("json/manc-london-users.json"))));
    String url = "http://localhost:" + port + "/user/city/London?milesRadius=20";

    ParameterizedTypeReference<List<UserDto>> typeRef = new ParameterizedTypeReference<>() {};
    ResponseEntity<List<UserDto>> personHttpEntity =
            restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, typeRef);

    List<UserDto> users = personHttpEntity.getBody();
    assertThat(personHttpEntity.getStatusCode().value(), equalTo(200));

    List<String> userEmails = users.stream().map(UserDto::getEmail).collect(Collectors.toList());
    assertThat(userEmails.size(), equalTo(2));
    assertThat(userEmails, hasItem("downingstreet@london-email.com"));
    assertThat(userEmails, hasItem("euston@london-email.com"));
  }

  public String fileContents(String path) {
    Resource resource = new ClassPathResource(path);
    try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
