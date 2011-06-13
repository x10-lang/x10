/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.x10rt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.IdentityHashMap;
import java.util.Map;

public class X10JavaDeserializer {

    // When a Object is deserialized record its position
    Map<Integer, X10JavaSerializable> objectMap = new IdentityHashMap<Integer, X10JavaSerializable>();
    ObjectInputStream in;
    int counter = 0;

    public X10JavaDeserializer(ObjectInputStream in) {
        this.in = in;
    }

    public Object readRef() throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.print("Serialization id  : ");
        }
        int serializationID = readInt();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Class Name : " + DeserializationDispatcher.getClassNameForID(serializationID));
        }
        return DeserializationDispatcher.getInstanceForId(serializationID, this);
    }

    public <T> void readArray(T[] array) throws IOException {
        int length = array.length;
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Array length : " + length);
        }
        for (int i = 0; i < length; i++) {
            if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
                System.out.println("Deserializing element  : " + i);
            }
            array[i] = (T)readRef();
        }
    }

    public Object javaDeserialize() throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Starting Java Deserialization : ");
        }
        Object obj = null;
        try {
            obj = in.readObject();
        } catch (ClassNotFoundException e) {
            // This should never happen cause we write both ends of the code
            e.printStackTrace();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Finished Java Deserialization : ");
        }
        return obj;
    }

    public int readInt() throws IOException {
        int v = in.readInt();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Int : " + v);
        }
        return v;
    }

    public int[] readIntArray() throws IOException {
        int length = in.readInt();
        int[] v = new int[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readInt();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Int Array : " + v);
        }
        return v;
    }

    public boolean readBoolean() throws IOException {
        boolean v = in.readBoolean();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Boolean : " + v);
        }
        return v;
    }

    public boolean[] readBooleanArray() throws IOException {
        int length = in.readInt();
        boolean[] v = new boolean[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readBoolean();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Boolean Array : " + v);
        }
        return v;
    }

    public char readChar() throws IOException {
        char v = in.readChar();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Char : " + v);
        }
        return v;
    }

    public char[] readCharArray() throws IOException {
        int length = in.readInt();
        char[] v = new char[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readChar();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Char Array : " + v);
        }
        return v;
    }

    public byte readByte() throws IOException {
        byte v = in.readByte();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Byte : " + v);
        }
        return v;
    }

    public byte[] readByteArray() throws IOException {
        int length = in.readInt();
        byte[] v = new byte[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readByte();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Byte Array : " + v);
        }
        return v;
    }

    public short readShort() throws IOException {
        short v = in.readShort();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Short : " + v);
        }
        return v;
    }

    public short[] readShortArray() throws IOException {
        int length = in.readInt();
        short[] v = new short[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readShort();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Short Array : " + v);
        }
        return v;
    }

    public long readLong() throws IOException {
        long v = in.readLong();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Long : " + v);
        }
        return v;
    }

    public long[] readLongArray() throws IOException {
        int length = in.readInt();
        long[] v = new long[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readLong();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Long Array : " + v);
        }
        return v;
    }

    public double readDouble() throws IOException {
        double v = in.readDouble();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Double : " + v);
        }
        return v;
    }

    public double[] readDoubleArray() throws IOException {
        int length = in.readInt();
        double[] v = new double[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readDouble();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Double Array : " + v);
        }
        return v;
    }

    public float readFloat() throws IOException {
        float v = in.readFloat();
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Float : " + v);
        }
        return v;
    }

    public float[] readFloatArray() throws IOException {
        int length = in.readInt();
        float[] v = new float[length];
        for (int i = 0; i < length; i++) {
             v[i] = in.readFloat();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Float Array : " + v);
        }
        return v;
    }

    public String readString() throws IOException {
        int classID = in.readInt();
        if (classID == DeserializationDispatcher.NULL_ID) {
            if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
                System.out.println("String is null");
            }
            return null;
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.print("String length : ");
        }
        int length = readInt();
        byte[] bytes = new byte[length];
        in.read(bytes);
        return new String(bytes);
    }

    public String[] readStringArray() throws IOException {
        int length = in.readInt();
        String[] v = new String[length];
        for (int i = 0; i < length; i++) {
             v[i] = readString();
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("String Array : " + v);
        }
        return v;
    }

    public Object deSerialize() throws IOException {
        Object o = DeserializationDispatcher.getInstanceForId(readInt(), this);
        if (o == null) {
            System.out.println();
        }
        return o;
    }

}
