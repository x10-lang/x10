package apgas;

import java.io.Serializable;

/**
 * A serializable functional interface with no arguments and no return value.
 * <p>
 * The functional method is {@link #run()}.
 */
@FunctionalInterface
public interface VoidFun extends Serializable {
  /**
   * Runs the function or throws an exception if unable to do so.
   *
   * @throws Exception
   *           if unable to run the function
   */
  public void run() throws Exception;
}
