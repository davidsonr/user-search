package dwp.assessment.usersearch.fixtures;

import dwp.assessment.usersearch.heroku.HerokuUserDto;

import java.util.ArrayList;
import java.util.List;

/**
 *  Fixtures class for creating Instances of HerokuUser to be used in the test suite
 *  */
public class HerokuUserFixture {

  public static HerokuUserDto mockUser() {
    return mockUser(1l, "testing1@email.com", 53.4807593, -2.2426305);
  }

  public static HerokuUserDto mockUser(Long id, String email, Double latitude, Double longitude) {
    return new HerokuUserDto(
        id,
        "testFirstName" + id,
        "testLastName" + id,
        email,
        "10.0.0.1",
        latitude,
        longitude,
        "testCity");
  }
  public static List<HerokuUserDto> mockUsers() {
    List<HerokuUserDto> users = new ArrayList<>();
    users.add(mockUser(1l, "test1@email.com", -0.1277583, -0.1277583));
    users.add(mockUser(2l, "test2@email.com", -0.1277555, -0.1277566));
    return users;
  }
}
