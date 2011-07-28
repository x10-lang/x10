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

import x10.runtime.impl.java.Runtime;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class X10JavaDeserializer {

    // When a Object is deserialized record its position
    private List<Object> objectList;
    public static final int ref = Integer.parseInt("FFFFFFF", 16);
    private DataInputStream in;
    private int counter = 0;

    public X10JavaDeserializer(DataInputStream in) {
        this.in = in;
        objectList = new ArrayList<Object>();
    }

    public int record_reference(Object obj) {
        objectList.add(counter, obj);
        counter++;
        return counter;
    }

    public void update_reference(int pos, Object obj) {
        objectList.set(pos, obj);
    }


    public Object getObjectAtPosition(int pos) {
        Object o = objectList.get(pos);
        if (Runtime.TRACE_SER) {
            System.out.println("\t\tRetrieving repeated reference  of type " + o.getClass() + " at " + pos + "  (absolute) in map");
        }
        return o;
    }

    public Object readRef() throws IOException {
        int serializationID = readInt();
        if (serializationID == ref) {
            return getObjectAtPosition(readInt());
        }
        return DeserializationDispatcher.getInstanceForId(serializationID, this);
    }

    public <T> void readArray(T[] array) throws IOException {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            array[i] = (T) readRef();
        }
    }

    public int readInt() throws IOException {
        int v = in.readInt();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a int: " + v);
        }
        return v;
    }

    public int[] readIntArray() throws IOException {
        int length = in.readInt();
        int[] v = new int[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readInt();
        }
        return v;
    }

    public boolean readBoolean() throws IOException {
        boolean v = in.readBoolean();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a boolean: " + v);
        }
        return v;
    }

    public boolean[] readBooleanArray() throws IOException {
        int length = in.readInt();
        boolean[] v = new boolean[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readBoolean();
        }
        return v;
    }

    public char readChar() throws IOException {
        char v = in.readChar();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a char: " + v);
        }
        return v;
    }

    public char[] readCharArray() throws IOException {
        int length = in.readInt();
        char[] v = new char[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readChar();
        }
        return v;
    }

    public byte readByte() throws IOException {
        byte v = in.readByte();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a byte: " + v);
        }
        return v;
    }

    public byte[] readByteArray() throws IOException {
        int length = in.readInt();
        byte[] v = new byte[length];
        _readByteArray(length, v);
        return v;
    }

    public void _readByteArray(int length, byte[] v) throws IOException {
        int read = 0;
        while (read < length) {
                read += in.read(v, read, length-read);
        }
    }

    public short readShort() throws IOException {
        short v = in.readShort();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a short: " + v);
        }
        return v;
    }

    public short[] readShortArray() throws IOException {
        int length = in.readInt();
        short[] v = new short[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readShort();
        }
        return v;
    }

    public long readLong() throws IOException {
        long v = in.readLong();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a long: " + v);
        }
        return v;
    }

    public long[] readLongArray() throws IOException {
        int length = in.readInt();
        long[] v = new long[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readLong();
        }
        return v;
    }

    public double readDouble() throws IOException {
        double v = in.readDouble();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a double: " + v);
        }
        return v;
    }

    public double[] readDoubleArray() throws IOException {
        int length = in.readInt();
        double[] v = new double[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readDouble();
        }
        return v;
    }

    public float readFloat() throws IOException {
        float v = in.readFloat();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a float: " + v);
        }
        return v;
    }

    public float[] readFloatArray() throws IOException {
        int length = in.readInt();
        float[] v = new float[length];
        for (int i = 0; i < length; i++) {
            v[i] = in.readFloat();
        }
        return v;
    }

    public String readString() throws IOException {
        int classID = in.readInt();
        if (classID == ref) {
            return (String) getObjectAtPosition(readInt());
        } else if (classID == DeserializationDispatcher.NULL_ID) {
            if (Runtime.TRACE_SER) {
                System.out.println("Deserializing a null reference");
            }
            return null;
        }
        String str = readStringValue();
        if (Runtime.TRACE_SER) {
            System.out.println("Deserializing a String: " + str);
        }
        record_reference(str);
        return str;
    }

    public String readStringValue() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];
        _readByteArray(length, bytes);
        return new String(bytes);
    }

    public String[] readStringArray() throws IOException {
        int length = in.readInt();
        String[] v = new String[length];
        for (int i = 0; i < length; i++) {
            v[i] = readString();
        }
        return v;
    }

    public Object deSerialize() throws IOException {
        int serializationID = readInt();
        if (serializationID == ref) {
            return getObjectAtPosition(readInt());
        }
        return DeserializationDispatcher.getInstanceForId(serializationID, this);
    }
}
