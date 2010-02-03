package x10.x10rt;

/**
 * An exception raised by the {@link MessageRegistry} when it is unable
 * to find an ActiveMessage that has been registered for an argument Method.
 */
public class UnknownMessageException extends Exception {

  private static final long serialVersionUID = 6184004558021405106L;

  public UnknownMessageException(String message) {
    super(message);
  }

  public UnknownMessageException(Exception cause) {
    super(cause);
  }

}
