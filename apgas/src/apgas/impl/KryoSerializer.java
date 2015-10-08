package apgas.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.objenesis.strategy.SerializingInstantiatorStrategy;

import apgas.Place;
import apgas.util.ByRef;
import apgas.util.GlobalID;
import apgas.util.PlaceLocalObject;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

/**
 * The {@link KryoSerializer} implements serialization using Kryo.
 *
 */
class KryoSerializer implements StreamSerializer<Object> {
  private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>() {
    @Override
    protected Kryo initialValue() {
      final Kryo kryo = new Kryo() {
        @Override
        @SuppressWarnings("rawtypes")
        protected Serializer newDefaultSerializer(Class type) {
          if (PlaceLocalObject.class.isAssignableFrom(type)) {
            return new PlaceLocalSerializer();
          } else if (ByRef.class.isAssignableFrom(type)) {
            return new ByRefSerializer();
          } else {
            return super.newDefaultSerializer(type);
          }
        }
      };
      kryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
      kryo.register(Task.class);
      kryo.register(UncountedTask.class);
      kryo.register(Place.class);
      kryo.register(GlobalID.class);
      kryo.register(java.lang.invoke.SerializedLambda.class);
      try {
        kryo.register(Class.forName(Kryo.class.getName() + "$Closure"),
            new ClosureSerializer());
        kryo.register(Class.forName(PlaceLocalObject.class.getName()
            + "$ObjectReference"));
      } catch (final ClassNotFoundException e) {
      }
      return kryo;
    }
  };

  @Override
  public int getTypeId() {
    return 42;
  }

  @Override
  public void write(ObjectDataOutput objectDataOutput, Object object)
      throws IOException {
    final Output output = new UnsafeOutput((OutputStream) objectDataOutput);
    final Kryo kryo = kryoThreadLocal.get();
    kryo.writeClassAndObject(output, object);
    output.flush();
  }

  @Override
  public Object read(ObjectDataInput objectDataInput) throws IOException {
    final Input input = new UnsafeInput((InputStream) objectDataInput);
    final Kryo kryo = kryoThreadLocal.get();
    return kryo.readClassAndObject(input);
  }

  @Override
  public void destroy() {
  }

  private static class PlaceLocalSerializer<T extends PlaceLocalObject> extends
      Serializer<T> {
    static Class<?> objectReferenceClass;
    static Method writeReplace;
    static Method readResolve;
    static {
      try {
        objectReferenceClass = Class.forName(PlaceLocalObject.class.getName()
            + "$ObjectReference");
        writeReplace = PlaceLocalObject.class.getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        readResolve = objectReferenceClass.getDeclaredMethod("readResolve");
        readResolve.setAccessible(true);
      } catch (final Exception e) {
      }
    }

    @Override
    public void write(Kryo kryo, Output output, T object) {
      try {
        kryo.writeObject(output, writeReplace.invoke(object));
      } catch (final Exception e) {
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T read(Kryo kryo, Input input, Class<T> type) {
      try {
        return (T) readResolve.invoke(kryo.readObject(input,
            objectReferenceClass));
      } catch (final Exception e) {
        return null;
      }
    }
  }

  private static class ByRefSerializer<T extends ByRef<T>> extends
      Serializer<T> {
    @Override
    public void write(Kryo kryo, Output output, T object) {
      kryo.writeObject(output, object.id());
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> type) {
      return kryo.newInstance(type).resolve(
          kryo.readObject(input, GlobalID.class));
    }
  }
}