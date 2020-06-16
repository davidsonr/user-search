package dwp.assessment.usersearch.heroku;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@JsonTest
@DisplayName("HerokuUser Json Test")
public class HerokuUserJsonTest {

  @Autowired ObjectMapper objectMapper;

  @Test
  @DisplayName("Parses a valid HerokuUser json")
  void getUserValidJson() throws JsonProcessingException {
    String json = fileContents("json/example-user.json");
    HerokuUserDto userDto = objectMapper.readValue(json, HerokuUserDto.class);
    assertThat(userDto.getId(), equalTo(4l));
    assertThat(userDto.getFirstName(), equalTo("firstName"));
    assertThat(userDto.getLastName(), equalTo("lastName"));
    assertThat(userDto.getEmail(), equalTo("peterssquare@email.com"));
    assertThat(userDto.getIpAddress(), equalTo("141.49.93.0"));
    assertThat(userDto.getLatitude(), equalTo(53.4773782));
    assertThat(userDto.getLongitude(), equalTo(-2.2449969));
    assertThat(userDto.getCity(), equalTo("testCity"));
  }

  @Test
  @DisplayName("Parses a HerokuUser json with null and missing fields")
  void getHandlesNullsAndMissingValues() throws JsonProcessingException {
    String json = fileContents("json/user-with-nulls.json");
    HerokuUserDto userDto = objectMapper.readValue(json, HerokuUserDto.class);
    assertThat(userDto.getFirstName(), equalTo("Maurise"));
    assertThat(userDto.getLastName(), equalTo(null));
    assertThat(userDto.getEmail(), equalTo("mshieldon0@squidoo.com"));
    assertThat(userDto.getIpAddress(), equalTo("192.57.232.111"));
    assertThat(userDto.getLatitude(), equalTo(34.003135));
    assertThat(userDto.getLongitude(), equalTo(-117.7228641));
    assertThat(userDto.getCity(), equalTo(null));
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
