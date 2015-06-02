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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;

import apgas.Constructs;
import apgas.DeadPlaceException;
import apgas.Place;
import apgas.SerializableCallable;

/**
 * A {@link PlaceLocalObject} instance maintains an implicit map from places to
 * objects.
 * <p>
 * Serializing a place local object across places does not replicate the object
 * as usual but instead transfer the {@link GlobalID} of the place local object
 * instance. This id is resolved at the destination place to the object local to
 * the place.
 */
public class PlaceLocalObject implements Serializable {
  private static final class ObjectReference implements Serializable {
    private static final long serialVersionUID = -2416972795695833335L;

    private final GlobalID id;

    private ObjectReference(GlobalID id) {
      this.id = id;
    }

    private Object readResolve() throws ObjectStreamException {
      return id.getHere();
    }
  }

  /**
   * Constructs a {@link PlaceLocalObject} instance.
   *
   * @param <T>
   *          the type of the constructed place local object
   * @param places
   *          a collection of places with no repetition
   * @param initializer
   *          the function to evaluate to initialize the objects
   * @return the place local object instance
   */
  @SuppressWarnings("unchecked")
  public static <T extends PlaceLocalObject> T make(
      Collection<? extends Place> places, SerializableCallable<T> initializer) {
    final GlobalID id = new GlobalID();
    try {
      finish(() -> {
        for (final Place p : places) {
          Constructs.asyncAt(p, () -> {
            final T t = initializer.call();
            t.id = id;
            id.putHere(t);
          });
        }
      });
    } catch (final DeadPlaceException e) {
      id.remove(places);
      throw e;
    }
    return (T) id.getHere();
  }

  /**
   * The {@link GlobalID} of this {@link PlaceLocalObject} instance.
   */
  GlobalID id; // package private

  /**
   * Returns the {@link GlobalID} of the given {@link PlaceLocalObject}
   * instance.
   *
   * @param object
   *          a place local object
   * @return the global ID of this object
   */
  public static GlobalID getId(PlaceLocalObject object) {
    return object.id;
  }

  /**
   * Constructs a reference to this {@link PlaceLocalObject} instance.
   *
   * @return the object reference
   * @throws ObjectStreamException
   *           N/A
   */
  protected Object writeReplace() throws ObjectStreamException {
    return new ObjectReference(id);
  }
}
