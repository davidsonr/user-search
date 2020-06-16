package dwp.assessment.usersearch.heroku;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
public class HerokuClient {

  private final String herokuUrl;

  private final RestTemplate restTemplate;

  public HerokuClient(
      @Value("${application.heroku-url}") String herokuUrl, RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
    this.herokuUrl = herokuUrl;
  }

  /**
   * Send a http Request to Heroku server for a single user for a given id
   * @param id to lookup
   * @return HerokuUserDto instance
   * @throws HerokuClientException client exception
   */
  public HerokuUserDto getUser(Long id) throws HerokuClientException {
    String url = herokuUrl + "/user/" + id;
    log.info("HerokuUser GET client request to {} ", url);
    try {
      ResponseEntity<HerokuUserDto> result = restTemplate.getForEntity(url, HerokuUserDto.class);
      return result.getBody();
    } catch (HttpClientErrorException httpClientErrorException) {
      throw new HerokuClientException("Error finding user for ID " + id, httpClientErrorException);
    }
  }

  /**
   * Send a http request to Heroku server to get All Users.
   *
   * @return HerokuUserDto list of users
   * @throws HerokuClientException client exception
   */
  public List<HerokuUserDto> getAllUsers() throws HerokuClientException {
    String url = herokuUrl + "/users";
    log.info("HerokuUser GET client request to {} ", url);
    try {
      ParameterizedTypeReference<List<HerokuUserDto>> typeRef = new ParameterizedTypeReference<>() {};
      ResponseEntity<List<HerokuUserDto>> rateResponse =
          restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, typeRef);

      List<HerokuUserDto> response = rateResponse.getBody();
      if (isEmpty(response)) {
        throw new HerokuClientException("No users found");
      }
      log.info("Found {} results", response.size());

      return rateResponse.getBody();
    } catch (HttpClientErrorException httpClientErrorException) {
      throw new HerokuClientException(
          "Error finding all users " + httpClientErrorException.getMessage(),
          httpClientErrorException);
    }
  }

  /**
   * Send a http request to Heroku server to get all users for a given city name.
   *
   * @return HerokuUserDto list of users
   * @throws HerokuClientException client exception
   */
  public List<HerokuUserDto> getUsersInCity(String cityName) throws HerokuClientException {
    String url = herokuUrl + "/city/" + cityName + "/users";
    log.info("HerokuUser GET client request to {} ", url);
    try {
      ParameterizedTypeReference<List<HerokuUserDto>> typeRef = new ParameterizedTypeReference<>() {};
      ResponseEntity<List<HerokuUserDto>> rateResponse =
          restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, typeRef);
      List<HerokuUserDto> response = rateResponse.getBody();
      if (isEmpty(response)) {
        log.warn("No users found for city: {} ", cityName);
        return new ArrayList<>();
      }
      log.info("Found {} results", response.size());
      return response;
    } catch (HttpClientErrorException httpClientErrorException) {
      throw new HerokuClientException(
          "Error finding users in city " + cityName, httpClientErrorException);
    }
  }
}
