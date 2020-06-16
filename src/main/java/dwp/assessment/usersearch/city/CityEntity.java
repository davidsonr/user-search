package dwp.assessment.usersearch.city;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "city")
@Data
@Setter(AccessLevel.NONE) // immutable
@AllArgsConstructor
@NoArgsConstructor
public class CityEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String name;
  private Double latitude;
  private Double longitude;
}
