package apgas;

import java.util.Collection;

/**
 * A {@link MultipleException} is thrown by the {@code finish} construct upon
 * termination when the code or tasks in its scope have uncaught exceptions. The
 * uncaught exceptions may be retrieved using the {@link #getSuppressed()}
 * method of this {@link MultipleException}.
 */
public class MultipleException extends RuntimeException {
  private static final long serialVersionUID = 5931977184541245168L;

  /**
   * Constructs a new {@link MultipleException} from the specified
   * {@code exceptions}.
   *
   * @param exceptions
   *          the uncaught exceptions that contributed to this
   *          {@code MultipleException}
   */
  public MultipleException(Collection<Throwable> exceptions) {
    for (final Throwable t : exceptions) {
      addSuppressed(t);
    }
  }
}
