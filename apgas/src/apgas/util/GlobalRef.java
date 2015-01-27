/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package apgas.util;

import static apgas.Constructs.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;

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
   * <p>
   * This ID is null until the reference is first serialized.
   */
  protected GlobalID id;

  /**
   * The target object of this {@link GlobalRef} at the current place if any.
   */
  protected transient Object t;

  /**
   * The collection of places where this {@link GlobalRef} instance is valid or
   * null if it is only valid at the place of instantiation.
   */
  protected transient Collection<? extends Place> places;

  /**
   * Constructs a {@link GlobalRef} to the given object.
   *
   * @param t
   *          the target of the global reference
   */
  public GlobalRef(T t) {
    this.t = t;
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
    id = new GlobalID();
    this.places = places;
    finish(() -> {
      final GlobalID id = this.id;
      for (final Place p : places) {
        asyncat(p, () -> {
          id.putHere(initializer.call());
        });
      }
    });
    t = id.getOrDefaultHere(UNDEFINED);
  }

  /**
   * Frees a {@link GlobalRef}.
   * <p>
   * Must be called from the place where this {@link GlobalRef} was
   * instantiated.
   * <p>
   * Freeing a global reference removes the mapping from it's ID to local
   * objects in each place where is was defined.
   * <p>
   * Failing to invoke this method on a {@link GlobalRef} instance will prevent
   * the collection of the target objects of this global reference even after
   * the global reference itself has been collected.
   *
   * @throws BadPlaceException
   *           if not invoked from the home place of the global reference
   */
  public void free() {
    if (id == null) {
      return;
    }
    if (!id.home.equals(here())) {
      throw new BadPlaceException();
    }
    if (places == null) {
      id.removeHere();
    } else {
      final GlobalID id = this.id;
      finish(() -> {
        for (final Place p : places) {
          asyncat(p, () -> {
            id.removeHere();
          });
        }
      });
    }
  }

  /**
   * Returns the home {@link Place} of this {@link GlobalRef} instance.
   *
   * @return a place
   */
  public Place home() {
    if (id == null) {
      return here();
    } else {
      return id.home;
    }
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
    if (t == UNDEFINED) {
      throw new BadPlaceException();
    }
    return (T) t;
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

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    synchronized (this) {
      if (id == null) {
        id = new GlobalID();
        id.putHere(t);
      }
    }
    out.defaultWriteObject();
  }

  private void readObject(ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    t = id.getOrDefaultHere(UNDEFINED);
  }
}
