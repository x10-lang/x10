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

import java.io.Serializable;
import java.util.Collection;

import apgas.Constructs;
import apgas.Place;
import apgas.SerializableCallable;

@SuppressWarnings("javadoc")
public class GlobalObject<T extends GlobalObject<T>> {
  GlobalRef<T> ref; // package-private

  public static <T extends GlobalObject<T>> T make(
      Collection<? extends Place> places, SerializableCallable<T> initializer) {
    final GlobalRef<T> r = new GlobalRef<T>(places, initializer);
    finish(() -> {
      for (final Place p : places) {
        Constructs.asyncat(p, () -> {
          r.get().ref = r;
        });
      }
    });
    return r.get();
  }

  public static interface SerializableConsumer<T> extends Serializable {
    void accept(T t) throws Exception;
  }

  public static interface SerializableFunction<T, SerializableT> extends
      Serializable {
    SerializableT apply(T t) throws Exception;
  }

  public void asyncat(Place p, SerializableConsumer<T> f) {
    final GlobalRef<T> r = ref;
    Constructs.asyncat(p, () -> {
      f.accept(r.get());
    });
  }

  public void uncountedasyncat(Place p, SerializableConsumer<T> f) {
    final GlobalRef<T> r = ref;
    Constructs.uncountedasyncat(p, () -> {
      f.accept(r.get());
    });
  }

  public <SerializableT extends Serializable> SerializableT at(Place p,
      SerializableFunction<T, SerializableT> f) {
    final GlobalRef<T> r = ref;
    return Constructs.at(p, () -> {
      return f.apply(r.get());
    });
  }
}
