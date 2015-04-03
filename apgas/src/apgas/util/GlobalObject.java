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
import apgas.Place;
import apgas.SerializableCallable;

@SuppressWarnings("javadoc")
public class GlobalObject<T> implements Serializable {
  private static final class ProxyObject<T> implements Serializable {
    private static final long serialVersionUID = -2416972795695833335L;

    private final GlobalRef<T> ref;

    private ProxyObject(GlobalRef<T> ref) {
      this.ref = ref;
    }

    private Object readResolve() throws ObjectStreamException {
      return ref.get();
    }
  }

  public static <T extends GlobalObject<T>> T make(
      Collection<? extends Place> places, SerializableCallable<T> initializer) {
    if (!places.contains(here())) {
      throw new BadPlaceException();
    }
    final GlobalRef<T> ref = new GlobalRef<T>(places, () -> initializer.call());
    finish(() -> {
      for (final Place p : places) {
        Constructs.asyncat(p, () -> ref.get().ref = ref);
      }
    });
    return ref.get();
  }

  GlobalRef<T> ref; // package-private

  public GlobalRef<T> ref() {
    return ref;
  }

  protected Object writeReplace() throws ObjectStreamException {
    return new ProxyObject<T>(ref);
  }
}
