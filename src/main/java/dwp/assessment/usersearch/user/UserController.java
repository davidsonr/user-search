package dwp.assessment.usersearch.user;

import dwp.assessment.usersearch.city.CityNotFoundException;
import dwp.assessment.usersearch.heroku.HerokuClientException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Get User By ID")
  @GetMapping(path = "/user/{id}")
  public ResponseEntity getUser(@PathVariable("id") Long id) {
    try {
      return ResponseEntity.ok(userService.findUser(id));
    } catch (HerokuClientException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @Operation(summary = "Get users in City")
  @GetMapping("/user/city/{city}")
  public ResponseEntity getUsersInCity(
      @PathVariable("city") String city,
      @RequestParam(name = "milesRadius", required = false) Integer milesRadius) {
    try {
      if (null == milesRadius || milesRadius == 0) {
        return ResponseEntity.ok(userService.findUsersInCity(city));
      }
      return ResponseEntity.ok(userService.findUsersInCityRadius(city, milesRadius));
    } catch (HerokuClientException | CityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }
}
