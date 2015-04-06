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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

import apgas.Constructs;
import apgas.Place;

@SuppressWarnings("javadoc")
public class MultiObject<T> implements Serializable {
  private static final class ProxyObject implements Serializable {
    private static final long serialVersionUID = 4387253164554524128L;

    private final GlobalID id;

    private ProxyObject(GlobalID id) {
      this.id = id;
    }

    private Object readResolve() throws ObjectStreamException {
      return Array.get(id.getHere(), 0);
    }
  }

  public static interface SerializableFunction<S, T> extends Serializable {
    T apply(S arg) throws Exception;
  }

  @SuppressWarnings("unchecked")
  public static <T extends MultiObject<T>> T make(
      Collection<? extends Place> places, int n,
      SerializableFunction<Integer, T> initializer, T... a) {
    final GlobalID id = new GlobalID();
    finish(() -> {
      for (final Place p : places) {
        Constructs.asyncat(p, () -> {
          final T[] on = Arrays.copyOf(a, n);
          for (int i = 0; i < n; ++i) {
            final T t = initializer.apply(i);
            t.id = id;
            t.on = on;
            on[i] = t;
          }
          id.putHere(on);
        });
      }
    });
    return (T) Array.get(id.getHere(), 0);
  }

  protected GlobalID id;

  protected T[] on;

  protected Object writeReplace() throws ObjectStreamException {
    return new ProxyObject(id);
  }
}
