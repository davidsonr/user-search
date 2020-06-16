package dwp.assessment.usersearch.city;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@DisplayName("CityEntity")
class CityEntityTest {

  @Test
  @DisplayName("Test Constructor and Getters")
  void assertGetters() {
    CityEntity cityEntity = new CityEntity(1, "London", 51.5073509, -0.1277583);
    assertThat(cityEntity.getId(), equalTo(1));
    assertThat(cityEntity.getName(), equalTo("London"));
    assertThat(cityEntity.getLatitude(), equalTo(51.5073509));
    assertThat(cityEntity.getLongitude(), equalTo(-0.1277583));
  }

  @Test
  @DisplayName("Test Equals HashCode")
  void testEquals() {
    CityEntity cityEntity1 = new CityEntity(1, "London", 51.5073509, -0.1277583);
    CityEntity cityEntity2 = new CityEntity(1, "London", 51.5073509, -0.1277583);
    CityEntity cityEntity3 = new CityEntity(2, "London", 51.5073509, -0.1277583);
    assertThat(cityEntity1, equalTo(cityEntity2));
    assertThat(cityEntity1, not(equalTo(cityEntity3)));
  }
}
