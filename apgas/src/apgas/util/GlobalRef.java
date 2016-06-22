/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2016.
 */

package apgas.util;

import static apgas.Constructs.*;

import java.io.Serializable;
import java.util.Collection;

import apgas.DeadPlaceException;
import apgas.Place;
import apgas.SerializableCallable;

/**
 * The {@link GlobalRef} class implements APGAS global references using
 * GlobalIDs.
 * <p>
 * A global reference can be serialized among places. In each place it resolves
 * to zero or one object local to the place.
 *
 * @param <T>
 *          the type of the reference
 */
public class GlobalRef<T> implements Serializable {
  private static final long serialVersionUID = 4462229293688114477L;

  private static final Object UNDEFINED = new Object();

  /**
   * The {@link GlobalID} instance for this {@link GlobalRef} instance.
   */
  protected final GlobalID id;

  /**
   * The collection of places used to construct the {@link GlobalRef} if any.
   */
  protected final transient Collection<? extends Place> places;

  /**
   * Constructs a {@link GlobalRef} to the given object.
   *
   * @param t
   *          the target of the global reference
   */
  public GlobalRef(T t) {
    if (t instanceof PlaceLocalObject) {
      id = ((PlaceLocalObject) t).id;
    } else {
      id = new GlobalID();
      id.putHere(t);
    }
    places = null;
  }

  /**
   * Constructs a {@link GlobalRef} over a collection of places.
   * <p>
   * This global reference is valid at any place in the given collection. The
   * target of the reference in each place is obtained by evaluating {@code f}
   * in each place at construction time.
   * <p>
   * This construction is a distributed operation with an implicit finish. The
   * invocation will only return after all tasks transitively spawned by
   * {@code f} have completed.
   *
   * @param places
   *          a collection of places with no repetition
   * @param initializer
   *          the function to evaluate to initialize the objects
   */
  public GlobalRef(Collection<? extends Place> places,
      SerializableCallable<T> initializer) {
    final GlobalID id = new GlobalID();
    this.id = id;
    this.places = places;
    finish(() -> {
      for (final Place p : places) {
        try {
          asyncAt(p, () -> {
            id.putHere(initializer.call());
          });
        } catch (final DeadPlaceException e) {
          async(() -> {
            throw e;
          });
        }
      }
    });
  }

  /**
   * Frees a {@link GlobalRef}.
   * <p>
   * Must be called from the place where this {@link GlobalRef} was
   * instantiated.
   * <p>
   * Freeing a global reference removes the mapping from it's ID to local
   * objects in each place where is was initially defined.
   * <p>
   * Failing to invoke this method on a {@link GlobalRef} instance will prevent
   * the collection of the target objects of this global reference even after
   * the global reference itself has been collected.
   *
   * @throws BadPlaceException
   *           if not invoked from the home place of the global reference
   */
  public void free() {
    if (!id.home.equals(here())) {
      throw new BadPlaceException();
    }
    if (places == null) {
      id.removeHere();
    } else {
      id.remove(places);
    }
  }

  /**
   * Returns the home {@link Place} of this {@link GlobalRef} instance.
   *
   * @return a place
   */
  public Place home() {
    return id.home;
  }

  /**
   * Dereferences this {@link GlobalRef} instance at the current place.
   *
   * @return the target object of the reference
   * @throws BadPlaceException
   *           if the reference is not valid here
   */
  @SuppressWarnings("unchecked")
  public T get() {
    final Object t = id.getOrDefaultHere(UNDEFINED);
    if (t == UNDEFINED) {
      throw new BadPlaceException();
    }
    return (T) t;
  }

  /**
   * Sets the target object for this {@link GlobalRef} instance at the current
   * place.
   *
   * @param t
   *          the target of the global reference
   */
  public void set(T t) {
    id.putHere(t);
  }

  /**
   * Removes the target object for this {@link GlobalRef} instance at the
   * current place.
   */
  public void unset() {
    id.removeHere();
  }

  @Override
  public String toString() {
    return "ref(" + id.gid() + ")";
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object that) {
    return that instanceof GlobalRef ? id == ((GlobalRef) that).id : false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
