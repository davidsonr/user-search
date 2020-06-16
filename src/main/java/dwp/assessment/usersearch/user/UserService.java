package dwp.assessment.usersearch.user;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import dwp.assessment.usersearch.city.CityDao;
import dwp.assessment.usersearch.city.CityEntity;
import dwp.assessment.usersearch.city.CityNotFoundException;
import dwp.assessment.usersearch.heroku.HerokuClient;
import dwp.assessment.usersearch.heroku.HerokuClientException;
import dwp.assessment.usersearch.heroku.HerokuUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

  private final HerokuClient herokuClient;
  private final CityDao cityDao;

  public UserService(HerokuClient herokuClient, CityDao cityDao) {
    this.cityDao = cityDao;
    this.herokuClient = herokuClient;
  }

  /**
   * Find User for a given ID.
   *
   * @param userId user id to lookup
   *
   * @return UserDto instance
   * @throws HerokuClientException client exception
   */
  public UserDto findUser(Long userId) throws HerokuClientException {
    log.info("Finding userId: {} ", userId);
    return map(herokuClient.getUser(userId));
  }

  /**
   * Find users for a given city name.
   *
   * @param cityName name if city to lookup.
   *
   * @return List of UserDto instances.
   * @throws HerokuClientException client exception.
   */
  public List<UserDto> findUsersInCity(String cityName) throws HerokuClientException {
    log.info("Find All users");
    List<HerokuUserDto> users = herokuClient.getUsersInCity(cityName);
    log.info("mapping {} users ", users.size());
    return users.stream().map(this::map).collect(Collectors.toList());
  }

  /**
   * Finds Users for a given city name and miles radius of the city centre.
   *
   * @param cityName name of city to lookup
   * @param milesRadius miles from city centre to lookup.
   *
   * @return List of UserDto instances.
   * @throws HerokuClientException client exception.
   * @throws CityNotFoundException city not found in database exception.
   */
  public List<UserDto> findUsersInCityRadius(String cityName, Integer milesRadius)
      throws HerokuClientException, CityNotFoundException {

    Optional<CityEntity> optionalCity = cityDao.findByNameIgnoreCase(cityName);
    if (optionalCity.isEmpty()) {
      log.error("Unable to find user for {} does it exist in the database?", cityName);
      throw new CityNotFoundException("Unable to find city " + cityName);
    }

    CityEntity city = optionalCity.get();
    List<HerokuUserDto> allUsers = herokuClient.getAllUsers();

    return allUsers.stream()
        .filter(hu -> isInMilesRadius(hu, city, milesRadius))
        .map(this::map)
        .collect(Collectors.toList());
  }

  private UserDto map(HerokuUserDto herokuUser) {
    return new UserDto(herokuUser.getFirstName(),
        herokuUser.getLastName(),
        herokuUser.getEmail(),
        herokuUser.getLatitude(),
        herokuUser.getLongitude());
  }

  private boolean isInMilesRadius(HerokuUserDto herokuUser, CityEntity cityEntity, int milesRadius) {
    return distance(
            herokuUser.getLatitude(),
            herokuUser.getLongitude(),
            cityEntity.getLatitude(),
            cityEntity.getLongitude()) <= milesRadius;
  }

  private static double distance(double lat1, double lon1, double lat2, double lon2) {
    if ((lat1 == lat2) && (lon1 == lon2)) {
      return 0;
    } else {
      double theta = lon1 - lon2;
      double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                      + Math.cos(Math.toRadians(lat1))
                      * Math.cos(Math.toRadians(lat2))
                      * Math.cos(Math.toRadians(theta));
      dist = Math.acos(dist);
      dist = Math.toDegrees(dist);
      dist = dist * 60 * 1.1515;
      return dist;
    }
  }
}