package dwp.assessment.usersearch.heroku;

public class HerokuClientException extends Exception {

  public HerokuClientException(String message) {
    super(message);
  }

  public HerokuClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
