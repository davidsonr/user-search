package dwp.assessment.usersearch.city;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CityDao extends CrudRepository<CityEntity, Integer> {
  Optional<CityEntity> findByNameIgnoreCase(String name);
}
