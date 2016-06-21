package apgas.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.OutputStream;

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
import apgas.util.Replaceable;
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
      final Kryo kryo = new Kryo();
      kryo.addDefaultSerializer(Replaceable.class,
          new ReplaceableSerializer());
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

  private static class ReplaceableSerializer extends Serializer<Replaceable> {
    @Override
    public void write(Kryo kryo, Output output, Replaceable object) {
      try {
        object = (Replaceable) object.writeReplace();
      } catch (final ObjectStreamException e) {
      }
      object.write(kryo, output);
    }

    @Override
    public Replaceable read(Kryo kryo, Input input,
        Class<Replaceable> type) {
      Replaceable object = kryo.newInstance(type);
      object.read(kryo, input);
      try {
        object = (Replaceable) object.readResolve();
      } catch (final ObjectStreamException e) {
      }
      kryo.reference(object);
      return object;
    }
  }
}
