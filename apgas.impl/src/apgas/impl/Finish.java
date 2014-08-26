package apgas.impl;

import java.util.List;

/**
 * The {@link Finish} interface.
 */
interface Finish {
  /**
   * Returns the home place of this {@link Finish} instance.
   *
   * @return the finish place
   */
  int home();

  /**
   * Must be called before a task is enqueued for execution (local task or
   * incoming remote task).
   *
   * @param p
   *          the place of the parent task
   */
  void submit(int p);

  /**
   * Must be called before a task is spawned at place p (local task or outgoing
   * remote task).
   *
   * @param p
   *          a place
   */
  void spawn(int p);

  /**
   * Must be called to undo the call to {@link #spawn(int)} if the attempt to
   * spawn the task at place p was unsuccessful.
   *
   * @param p
   *          a place
   */
  void unspawn(int p);

  /**
   * Must be called once a task has completed its execution.
   */
  void tell();

  /**
   * Reports an uncaught exception to this finish object.
   *
   * @param exception
   *          the uncaught exception
   */
  void addSuppressed(Throwable exception);

  /**
   * Returns true if the code and tasks in the finish scope have completed.
   * <p>
   * This method is intentionally not synchronized.
   *
   * @return true if terminated
   */
  boolean waiting();

  /**
   * Waits for the termination of the code and tasks in scope of the finish.
   * <p>
   * Blocks the calling thread.
   */
  void await();

  /**
   * Returns the list of exceptions collected by this finish object.
   *
   * @return the collected exceptions
   */
  List<Throwable> exceptions();
}
