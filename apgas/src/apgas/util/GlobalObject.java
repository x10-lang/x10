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
  private static final class ProxyObject implements Serializable {
    private static final long serialVersionUID = -2416972795695833335L;

    private final GlobalID id;

    private ProxyObject(GlobalID id) {
      this.id = id;
    }

    private Object readResolve() throws ObjectStreamException {
      return id.getHere();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T extends GlobalObject<T>> T make(
      Collection<? extends Place> places, SerializableCallable<T> initializer) {
    final GlobalID id = new GlobalID();
    finish(() -> {
      for (final Place p : places) {
        Constructs.asyncat(p, () -> {
          final T t = initializer.call();
          t.id = id;
          id.putHere(t);
        });
      }
    });
    return (T) id.getHere();
  }

  protected GlobalID id;

  protected Object writeReplace() throws ObjectStreamException {
    return new ProxyObject(id);
  }
}
