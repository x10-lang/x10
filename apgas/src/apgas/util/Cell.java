package apgas.util;

/**
 * The {@link Cell} class implements a mutable generic container.
 *
 * @param <T>
 *          the type of the contained object
 */
public final class Cell<T> {
  /**
   * The contained object.
   */
  private T t;

  /**
   * Returns the object in this {@link Cell} instance.
   *
   * @return the contained object
   */
  public T get() {
    return t;
  }

  /**
   * Sets the object in this {@link Cell} instance.
   *
   * @param t
   *          an object
   */
  public void set(T t) {
    this.t = t;
  }
}
