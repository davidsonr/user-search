package dwp.assessment.usersearch.user;

import dwp.assessment.usersearch.city.CityDao;
import dwp.assessment.usersearch.city.CityEntity;
import dwp.assessment.usersearch.city.CityNotFoundException;
import dwp.assessment.usersearch.fixtures.HerokuUserFixture;
import dwp.assessment.usersearch.heroku.HerokuClient;
import dwp.assessment.usersearch.heroku.HerokuClientException;
import dwp.assessment.usersearch.heroku.HerokuUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dwp.assessment.usersearch.fixtures.HerokuUserFixture.mockUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GIVEN UserService")
class UserServiceTest {

  private HerokuClient herokuClient;
  private CityDao cityDao;

  @BeforeEach
  public void setup() {
    herokuClient = mock(HerokuClient.class);
    cityDao = mock(CityDao.class);
  }

  @Test
  @DisplayName("AND findUser called THEN return a User")
  void findUser() throws HerokuClientException {
    UserService userService = new UserService(herokuClient, cityDao);

    when(herokuClient.getUser(1l)).thenReturn(HerokuUserFixture.mockUser());
    UserDto userDto = userService.findUser(1l);

    assertThat(userDto.getFirstName(), equalTo("testFirstName1"));
    assertThat(userDto.getLastName(), equalTo("testLastName1"));
    assertThat(userDto.getEmail(), equalTo("testing1@email.com"));
    assertThat(userDto.getLatitude(), equalTo(53.4807593));
    assertThat(userDto.getLongitude(), equalTo(-2.2426305));
  }

  @Test
  @DisplayName("AND findUsersInCity THEN return users in the city")
  void findUsersInCity() throws HerokuClientException {

    UserService userService = new UserService(herokuClient, cityDao);

    when(herokuClient.getUsersInCity("London")).thenReturn(HerokuUserFixture.mockUsers());

    List<UserDto> users = userService.findUsersInCity("London");
    assertThat(users.size(), equalTo(2));
    assertThat(users.get(0).getFirstName(), equalTo("testFirstName1"));
    assertThat(users.get(0).getLastName(), equalTo("testLastName1"));
    assertThat(users.get(0).getEmail(), equalTo("test1@email.com"));
    assertThat(users.get(0).getLatitude(), equalTo(-0.1277583));
    assertThat(users.get(0).getLongitude(), equalTo(-0.1277583));

    assertThat(users.get(1).getFirstName(), equalTo("testFirstName2"));
    assertThat(users.get(1).getLastName(), equalTo("testLastName2"));
    assertThat(users.get(1).getEmail(), equalTo("test2@email.com"));
    assertThat(users.get(1).getLatitude(), equalTo(-0.1277555));
    assertThat(users.get(1).getLongitude(), equalTo(-0.1277566));
  }

  @Test
  @DisplayName("AND findUsersInCity WHEN city not found THEN throw a CityNotFoundException")
  void findUsersInCityRadiusCityNotFound() {

    UserService userService = new UserService(herokuClient, cityDao);

    when(cityDao.findByNameIgnoreCase("NotARealCity")).thenReturn(Optional.empty());

    final CityNotFoundException thrown =
        assertThrows(
            CityNotFoundException.class,
            () -> userService.findUsersInCityRadius("NotARealCity", 50));

    assertEquals("Unable to find city NotARealCity", thrown.getMessage());
  }

  @Test
  @DisplayName("AND findUsersInCityRadius THEN return only users in the radius of the city")
  void findUsersInCityRadius() throws HerokuClientException, CityNotFoundException {

    UserService userService = new UserService(herokuClient, cityDao);

    CityEntity manchester = new CityEntity(1, "Manchester", 53.4807593, -2.2426305);

    when(cityDao.findByNameIgnoreCase("Manchester")).thenReturn(Optional.of(manchester));

    when(herokuClient.getAllUsers()).thenReturn(mockLondonManchesterUsers());

    List<UserDto> usersInManchester = userService.findUsersInCityRadius("Manchester", 50);

    List<String> userEmailsInManchester =
        usersInManchester.stream().map(UserDto::getEmail).collect(Collectors.toList());
    assertThat(userEmailsInManchester.size(), equalTo(4));
    assertThat(userEmailsInManchester, hasItem("sale@manc-email.com"));
    assertThat(userEmailsInManchester, hasItem("oxfordroad@manc-email.com"));
    assertThat(userEmailsInManchester, hasItem("peters-square@manc-email.com"));
    assertThat(userEmailsInManchester, hasItem("airport@manc-email.com"));
  }

  @Test
  @DisplayName(
      "AND findUsersInCityRadius AND location same stored city coordinated "
          + "THEN return only users in the radius of the city")
  void findUsersInCityRadiusWithSameCoordinates()
      throws HerokuClientException, CityNotFoundException {

    UserService userService = new UserService(herokuClient, cityDao);

    CityEntity london = new CityEntity(1, "London", 51.5073509, -0.1277583);

    when(cityDao.findByNameIgnoreCase("London")).thenReturn(Optional.of(london));

    when(herokuClient.getAllUsers()).thenReturn(mockLondonManchesterUsers());

    List<UserDto> usersInLondon = userService.findUsersInCityRadius("London", 50);

    List<String> usersEmailsInLondon =
        usersInLondon.stream().map(UserDto::getEmail).collect(Collectors.toList());
    assertThat(usersInLondon.size(), equalTo(3));
    assertThat(usersEmailsInLondon, hasItem("downingstreet@london-email.com"));
    assertThat(usersEmailsInLondon, hasItem("euston@london-email.com"));
    assertThat(usersEmailsInLondon, hasItem("centre-of-london@london-email.com"));
  }

  public List<HerokuUserDto> mockLondonManchesterUsers() {

    // 10 Downing street
    HerokuUserDto userLondon1 =
        mockUser(1l, "downingstreet@london-email.com", 51.5033635, -0.1276248);

    // London Euston
    HerokuUserDto userLondon2 =
            mockUser(2l, "euston@london-email.com", 51.5280991, -0.1332084);

    // London Similar Euston
    HerokuUserDto userLondon3 =
        mockUser(3l, "centre-of-london@london-email.com", 51.5073509, -0.1277583);

    // Manchester Sale
    HerokuUserDto userManchester1 =
            mockUser(4l, "sale@manc-email.com", 53.4276778, -2.3088056);

    // Manchester Oxford Road
    HerokuUserDto userManchester2 =
        mockUser(5l, "oxfordroad@manc-email.com", 53.4750767, -2.2409506);

    // St peters square
    HerokuUserDto userManchester3 =
        mockUser(6l, "peters-square@manc-email.com", 53.4773782, -2.2449969);

    // Manchester Airport
    HerokuUserDto userManchester4 = mockUser(7l, "airport@manc-email.com", 53.3588026, -2.2727303);

    List<HerokuUserDto> users = new ArrayList<>();
    users.add(userLondon1);
    users.add(userLondon2);
    users.add(userLondon3);
    users.add(userManchester1);
    users.add(userManchester2);
    users.add(userManchester3);
    users.add(userManchester4);
    return users;
  }
}
