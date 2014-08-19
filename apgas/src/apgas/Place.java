package apgas;

import java.io.Serializable;

/**
 * The {@link Place} class represents an APGAS place.
 */
public class Place implements Serializable {
  private static final long serialVersionUID = -7210312031258955537L;

  /**
   * The integer ID of this place.
   */
  public final int id;

  /**
   * Constructs a {@link Place} with the specified ID.
   * <p>
   * Prefer {@link Constructs#place(int)} to avoid constructing unnecessary
   * {@link Place} objects.
   *
   * @param id
   *          the desired place ID
   */
  public Place(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "place(" + id + ")";
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof Place ? id == ((Place) that).id : false;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }
}
