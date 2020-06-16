package dwp.assessment.usersearch.heroku;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Data
@Setter(AccessLevel.NONE) // immutable
public class HerokuUserDto {

  private final Long id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String ipAddress;
  private final Double latitude;
  private final Double longitude;
  private final String city;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public HerokuUserDto(
      @JsonProperty("id") Long id,
      @JsonProperty("first_name") String firstName,
      @JsonProperty("last_name") String lastName,
      @JsonProperty("email") String email,
      @JsonProperty("ip_address") String ipAddress,
      @JsonProperty("latitude") Double latitude,
      @JsonProperty("longitude") Double longitude,
      @JsonProperty("city") String city) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.ipAddress = ipAddress;
    this.latitude = latitude;
    this.longitude = longitude;
    this.city = city;
  }
}
