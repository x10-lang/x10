package apgas.impl;

/**
 * The {@link Semaphore} class extends the
 * {@link java.util.concurrent.Semaphore java.util.concurrent.Semaphore} class
 * to expose the {@link #reducePermits(int)} method.
 *
 */
final class Semaphore extends java.util.concurrent.Semaphore {
  private static final long serialVersionUID = -7522284546281301809L;

  /**
   * Constructs a new {@link Semaphore} with the given number of permits.
   *
   * @param permits
   *          the initial number of permits available
   */
  public Semaphore(int permits) {
    super(permits);
  }

  @Override
  public void reducePermits(int reduction) {
    super.reducePermits(reduction);
  }
}
