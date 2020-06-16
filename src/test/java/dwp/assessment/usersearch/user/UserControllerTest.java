package dwp.assessment.usersearch.user;

import dwp.assessment.usersearch.city.CityNotFoundException;
import dwp.assessment.usersearch.heroku.HerokuClientException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("GIVEN UserController")
class UserControllerTest {

  @MockBean UserService userService;

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("AND a valid getUser request made THEN success http status returned")
  void getUser() throws Exception {
    this.mockMvc.perform(get("/user/1")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("AND getUser requested AND invalid user id requested THEN Client Error returned")
  void getUserInvalidUserId() throws Exception {
    this.mockMvc.perform(get("/user/inv@lid")).andExpect(status().is4xxClientError());
  }

  @Test
  @DisplayName("AND getUser requested AND Heroku Client Error THEN Internal Server Error returned")
  void getUserHandleHerokuError() throws Exception {
    when(userService.findUser(1l)).thenThrow(new HerokuClientException("Test Exception"));
    this.mockMvc.perform(get("/user/1")).andExpect(status().is5xxServerError());
  }

  @Test
  @DisplayName("AND a valid getUsersInCity request made THEN success http status returned")
  void getUsersInCity() throws Exception {
    this.mockMvc.perform(get("/user/city/London")).andExpect(status().isOk());
  }

  @Test
  @DisplayName(
      "AND getUsersInCity requested AND Heroku Client Error THEN Internal Server Error returned")
  void getUsersInCityHandleHerokuError() throws Exception {
    when(userService.findUsersInCity("London"))
        .thenThrow(new HerokuClientException("Test Exception"));
    this.mockMvc.perform(get("/user/city/London")).andExpect(status().is5xxServerError());
  }

  @Test
  @DisplayName("AND a valid getUsersInCityRadius request made THEN success http status  returned")
  void getUsersInCityWithRadius() throws Exception {
    this.mockMvc.perform(get("/user/city/London?milesRadius=50")).andExpect(status().isOk());
  }

  @Test
  @DisplayName(
      "AND getUsersInCity requested AND City Not loaded THEN Internal Server Error returned")
  void getUsersInCityHandleNotFoundCityError() throws Exception {
    when(userService.findUsersInCityRadius("London", 50))
        .thenThrow(new CityNotFoundException("Test Exception"));
    this.mockMvc
        .perform(get("/user/city/London?milesRadius=50"))
        .andExpect(status().is5xxServerError());
  }

  @Test
  @DisplayName(
      "AND getUsersInCityRadius requested AND invalid milesRadius entered THEN ClientError returned")
  void getUsersInCityInvalidRadius() throws Exception {
    this.mockMvc
        .perform(get("/user/city/London?milesRadius=Fifty"))
        .andExpect(status().is4xxClientError());
  }
}
