package dwp.assessment.usersearch.user;

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
public class UserDto {

  private final String firstName;
  private final String lastName;
  private final String email;
  private final Double latitude;
  private final Double longitude;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public UserDto(
      @JsonProperty("first_name") String firstName,
      @JsonProperty("last_name") String lastName,
      @JsonProperty("email") String email,
      @JsonProperty("latitude") Double latitude,
      @JsonProperty("longitude") Double longitude) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
