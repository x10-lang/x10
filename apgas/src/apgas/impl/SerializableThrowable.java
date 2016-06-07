package apgas.impl;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * The {@link SerializableThrowable} handles exception serialization failures.
 * <p>
 * It replaces non-serializable exceptions with
 * {@link java.io.NotSerializableException} exceptions.
 */
final class SerializableThrowable implements Serializable, KryoSerializable {
  private static final long serialVersionUID = -3821385333142091656L;

  /**
   * The wrapped exception.
   */
  Throwable t;

  /**
   * Constructs a new {@link SerializableThrowable}.
   *
   * @param t
   *          the exception to wrap
   */
  SerializableThrowable(Throwable t) {
    this.t = t;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    final NotSerializableException e = new NotSerializableException(
        t.getClass().getCanonicalName());
    e.setStackTrace(t.getStackTrace());
    out.writeObject(e);
    try {
      out.writeObject(t);
    } catch (final Throwable x) {
    }
  }

  private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    t = (Throwable) in.readObject();
    try {
      t = (Throwable) in.readObject();
    } catch (final Throwable x) {
    }
  }

  @Override
  public void write(Kryo kryo, Output output) {
    final NotSerializableException e = new NotSerializableException(
        t.getClass().getCanonicalName());
    e.setStackTrace(t.getStackTrace());
    kryo.writeClassAndObject(output, e);
    try {
      kryo.writeClassAndObject(output, t);
    } catch (final Throwable x) {
    }
  }

  @Override
  public void read(Kryo kryo, Input input) {
    t = (Throwable) kryo.readClassAndObject(input);
    try {
      t = (Throwable) kryo.readClassAndObject(input);
    } catch (final Throwable x) {
    }
  }
}
