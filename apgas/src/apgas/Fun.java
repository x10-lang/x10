package apgas;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * A generic serializable functional interface with no arguments and a return
 * value.
 * <p>
 * The functional method is {@link #call()}.
 *
 * @param <T>
 *          the type of the result
 */
@FunctionalInterface
public interface Fun<T> extends Callable<T>, Serializable {
  @Override
  public T call() throws Exception;
}
