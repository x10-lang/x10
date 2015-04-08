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

@SuppressWarnings("javadoc")
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

  @SuppressWarnings("unchecked")
  public static <T extends PlaceLocalObject> T make(
      Collection<? extends Place> places, SerializableCallable<T> initializer) {
    final GlobalID id = new GlobalID();
    try {
      finish(() -> {
        for (final Place p : places) {
          Constructs.asyncat(p, () -> {
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

  GlobalID id; // package private

  public static GlobalID getId(PlaceLocalObject object) {
    return object.id;
  }

  Object writeReplace() throws ObjectStreamException { // package private
    return new ObjectReference(id);
  }
}
