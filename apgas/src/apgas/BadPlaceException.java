package apgas;

/**
 * A {@link BadPlaceException} is thrown when attempting to reference a
 * {@link Place} that doesn't exist or when attempting to dereference a
 * {@link apgas.util.GlobalRef} that is not defined {@code here}.
 */
public class BadPlaceException extends RuntimeException {
  private static final long serialVersionUID = 8639251079580877933L;

  /**
   * Constructs a new {@link BadPlaceException}.
   */
  public BadPlaceException() {
  }
}
