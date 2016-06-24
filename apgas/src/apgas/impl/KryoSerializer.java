package apgas.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.objenesis.strategy.SerializingInstantiatorStrategy;

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

import apgas.Place;
import apgas.util.GlobalID;
import apgas.util.PlaceLocalObject;

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
        @SuppressWarnings({ "rawtypes", "unchecked" })
        protected Serializer newDefaultSerializer(Class type) {
          try {
            type.getMethod("writeReplace");
            return new CustomSerializer();
          } catch (final NoSuchMethodException e) {
          }
          return super.newDefaultSerializer(type);
        }
      };
      kryo.addDefaultSerializer(DefaultFinish.class,
          new DefaultFinishSerializer());
      kryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
      kryo.register(Task.class);
      kryo.register(UncountedTask.class);
      kryo.register(Place.class);
      kryo.register(GlobalID.class);
      kryo.register(java.lang.invoke.SerializedLambda.class);
      try {
        kryo.register(Class.forName(Kryo.class.getName() + "$Closure"),
            new ClosureSerializer());
        kryo.register(Class
            .forName(PlaceLocalObject.class.getName() + "$ObjectReference"));
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

  private static class CustomSerializer extends Serializer<Object> {
    @Override
    public void write(Kryo kryo, Output output, Object object) {
      try {
        final Method writeReplace = object.getClass().getMethod("writeReplace");
        object = writeReplace.invoke(object);
      } catch (final Exception e) {
      }
      kryo.writeClassAndObject(output, object);
    }

    @Override
    public Object read(Kryo kryo, Input input, Class<Object> type) {
      Object object = kryo.readClassAndObject(input);
      try {
        final Method readResolve = object.getClass()
            .getDeclaredMethod("readResolve");
        readResolve.setAccessible(true);
        object = readResolve.invoke(object);
      } catch (final Exception e) {
      }
      return object;
    }
  }

  private static class DefaultFinishSerializer
      extends Serializer<DefaultFinish> {
    @Override
    public void write(Kryo kryo, Output output, DefaultFinish object) {
      object.writeReplace();
      kryo.writeObject(output, object.id);
    }

    @Override
    public DefaultFinish read(Kryo kryo, Input input,
        Class<DefaultFinish> type) {
      final DefaultFinish f = kryo.newInstance(type);
      f.id = kryo.readObject(input, GlobalID.class);
      return (DefaultFinish) f.readResolve();
    }
  }
}
