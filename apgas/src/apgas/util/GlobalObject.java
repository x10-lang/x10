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
  GlobalRef<T[]> ref; // package-private

  public static <T extends GlobalObject<T>> T make(
      Collection<? extends Place> places, int multiplicity,
      SerializableFunction<? super Area, T> initializer) {
    final GlobalRef<T[]> r = new GlobalRef<T[]>(places, () -> {
      return (T[]) new GlobalObject[multiplicity];
    });
    finish(() -> {
      for (final Place p : places) {
        Constructs.asyncat(p, () -> {
          final T[] a = r.get();
          for (int area = 0; area < multiplicity; ++area) {
            a[area] = initializer.apply(new Area(here(), area, multiplicity));
            a[area].ref = r;
          }
        });
      }
    });
    return r.get()[0];
  }

  public final GlobalRef<T[]> ref() {
    return ref;
  }

  public static <T extends GlobalObject<T>> T make(
      Collection<? extends Place> places,
      SerializableFunction<? super Area, T> initializer) {
    return make(places, 1, initializer);
  }

  public static <T extends GlobalObject<T>> T make(
      Collection<? extends Place> places, SerializableCallable<T> initializer) {
    return make(places, 1, area -> initializer.call());
  }

  public static class Area extends Place {
    private static final long serialVersionUID = 193406403512190763L;

    public final int area;
    public final int multiplicity;

    public Area(Place p, int area, int multiplicity) {
      super(p.id);
      this.area = area;
      this.multiplicity = multiplicity;
    }

    public Area(int id, int multiplicity) {
      super(id / multiplicity);
      this.area = id % multiplicity;
      this.multiplicity = multiplicity;
    }

    public int lid() {
      return id * multiplicity + area;
    }
  }

  public static interface SerializableConsumer<T> extends Serializable {
    void accept(T t) throws Exception;
  }

  public static interface SerializableFunction<T, SerializableT> extends
      Serializable {
    SerializableT apply(T t) throws Exception;
  }

  public void asyncat(Place p, SerializableConsumer<T> f) {
    final int area = p instanceof Area ? ((Area) p).area : 0;
    final GlobalRef<T[]> r = ref;
    Constructs.asyncat(p, () -> {
      f.accept(r.get()[area]);
    });
  }

  public void uncountedasyncat(Place p, SerializableConsumer<T> f) {
    final int area = p instanceof Area ? ((Area) p).area : 0;
    final GlobalRef<T[]> r = ref;
    Constructs.uncountedasyncat(p, () -> {
      f.accept(r.get()[area]);
    });
  }

  public <SerializableT extends Serializable> SerializableT at(Place p,
      SerializableFunction<T, SerializableT> f) {
    final int area = p instanceof Area ? ((Area) p).area : 0;
    final GlobalRef<T[]> r = ref;
    return Constructs.at(p, () -> {
      return f.apply(r.get()[area]);
    });
  }
}
