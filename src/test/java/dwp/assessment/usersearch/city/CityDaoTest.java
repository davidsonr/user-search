package dwp.assessment.usersearch.city;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@DisplayName("Given CityDao")
class CityDaoTest {

  @Autowired CityDao cityDao;

  @Test
  @Sql({"/test-data.sql"})
  @DisplayName("AND findByName THEN a successful database entity is returned")
  void findByName() {
    Optional<CityEntity> city = cityDao.findByNameIgnoreCase("TestCityName1");
    assertThat(city.isPresent(), equalTo(true));
    assertThat(city.get().getName(), equalTo("TestCityName1"));
    assertThat(city.get().getLatitude(), equalTo(51.5073509));
    assertThat(city.get().getLongitude(), equalTo(-0.1277583));
  }

  @Test
  @Sql({"/test-data.sql"})
  @DisplayName("AND findByName AND different letter case THEN a successful database entity is still returned")
  void findByNameIgnoreCase() {
    Optional<CityEntity> city = cityDao.findByNameIgnoreCase("london");
    assertThat(city.isPresent(), equalTo(true));
    assertThat(city.get().getName(), equalTo("London"));
    assertThat(city.get().getLatitude(), equalTo(56.5073509));
    assertThat(city.get().getLongitude(), equalTo(-0.9997583));
  }

  @Test
  @Sql({"/test-data.sql"})
  @DisplayName("AND findByName WHEN unknown city THEN an empty optional is returned")
  void findByNameNotFound() {
    Optional<CityEntity> city = cityDao.findByNameIgnoreCase("DoesNotExist");
    assertThat(city.isPresent(), equalTo(false));
  }
}
