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

import x10.io.CustomSerialization;
import x10.runtime.impl.java.Runtime;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

public class X10JavaSerializer {

    //TODO Keith check what the C++ side do on arrays

    // When a Object is serialized record its position
    IdentityHashMap<Object, Integer> objectMap = new IdentityHashMap<Object, Integer>();
    static final int refValue = Integer.parseInt("FFFFFFF", 16);
    DataOutputStream out;
    int counter = 0;

    public X10JavaSerializer(DataOutputStream out) {
        this.out = out;
    }

    public void write(X10JavaSerializable obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }

        if (obj.getClass().toString().equals("java.lang.Object")) {
            return;
        }
        Integer pos = previous_position(obj);
        if (pos !=null) {
            return;
        }
        int i = obj._get_serialization_id();
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing id " + i  + " of type " + obj.getClass());
        }
        write(i);
        obj._serialize(this);
        if (Runtime.TRACE_SER) {
            System.out.println("Completed serialization of type " + obj.getClass());
        }
    }

    private void writeNull() throws IOException {
        out.writeInt(DeserializationDispatcher.NULL_ID);
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a null reference");
        }
    }

    public void write(CustomSerialization obj) throws IOException {
        write((X10JavaSerializable) obj);
    }

    public void write(X10JavaSerializable obj[]) throws IOException {

        write(obj.length);
        for (X10JavaSerializable o : obj) {
            write(o);
        }
    }

    public void write(int i) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a int: " + i);
        }
        out.writeInt(i);
    }

    public void write(int[] i) throws IOException {
        out.writeInt(i.length);
        for (int j : i) {
            out.writeInt(j);
        }
    }

    public void write(boolean b) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a boolean: " + b);
        }
        out.writeBoolean(b);
    }

    public void write(boolean v[]) throws IOException {
        out.writeInt(v.length);
        for (boolean b : v) {
            out.writeBoolean(b);
        }
    }

    public void write(char c) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a chat: " + c);
        }
        out.writeChar(c);
    }

    public void write(char[] v) throws IOException {
        out.writeInt(v.length);
        for (char c : v) {
            out.writeChar(c);
        }
    }

    public void write(byte b) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a byte: " + b);
        }
        out.writeByte(b);
    }

    public void write(byte[] b) throws IOException {
        out.writeInt(b.length);
        out.write(b);
    }

    public void write(short s) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a short: " + s);
        }
        out.writeShort(s);
    }

    public void write(short[] v) throws IOException {
        out.writeInt(v.length);
        for (short s : v) {
            out.writeShort(s);
        }
    }

    public void write(long l) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a long: " + l);
        }
        out.writeLong(l);
    }

    public void write(long[] v) throws IOException {
        out.writeInt(v.length);
        for (long l : v) {
            out.writeLong(l);
        }
    }

    public void write(double d) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a double: " + d);
        }
        out.writeDouble(d);
    }

    public void write(double[] v) throws IOException {
        out.writeInt(v.length);
        for (double d : v) {
            out.writeDouble(d);
        }
    }

    public void write(float f) throws IOException {
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a float: " + f);
        }
        out.writeFloat(f);
    }

    public void write(float[] v) throws IOException {
        out.writeInt(v.length);
        for (float f : v) {
            out.writeFloat(f);
        }
    }

    public void write(String str) throws IOException {
        if (str == null) {
            writeNull();
            return;
        }

        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a String: " + str);
        }

        Integer pos = previous_position(str);
        if (pos != null) {
            return;
        }
        writeStringValue(str);
    }

    private void writeStringValue(String str) throws IOException {
        out.writeInt(DeserializationDispatcher.STRING_ID);
        write(str.length());
        out.write(str.getBytes());
    }

    public void write(String[] v) throws IOException {
        out.writeInt(DeserializationDispatcher.STRING_ID);
        out.writeInt(v.length);
        for (String str : v) {
            write(str);
        }
    }

    public <T> void write(T p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p);
        if (pos != null) {
            return;
        }
        int id = DeserializationDispatcher.getIDForClassName(p.getClass().getName());
        if (id == DeserializationDispatcher.STRING_ID) {
            writeStringValue(p.toString());
            return;
        }
        out.writeInt(id);
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing a " + p.getClass() + ": " + p);
        }
        switch (id) {
            case DeserializationDispatcher.FLOAT_ID:
                out.writeFloat((Float) p);
                break;
            case DeserializationDispatcher.DOUBLE_ID:
                out.writeDouble((Double) p);
                break;
            case DeserializationDispatcher.INTEGER_ID:
                out.writeInt((Integer) p);
                break;
            case DeserializationDispatcher.BOOLEAN_ID:
                out.writeBoolean((Boolean) p);
                break;
            case DeserializationDispatcher.BYTE_ID:
                out.writeByte((Byte) p);
                break;
            case DeserializationDispatcher.SHORT_ID:
                out.writeByte((Short) p);
                break;
            case DeserializationDispatcher.CHARACTER_ID:
                out.writeChar((Character) p);
                break;
            case DeserializationDispatcher.LONG_ID:
                out.writeLong((Long) p);
                break;
            default:
                throw new RuntimeException("################## Need to handle " + p.getClass().getName());
        }
    }

    private Integer previous_position(Object obj) throws IOException {
        Integer pos = objectMap.get(obj);
        if (pos != null) {
            if (Runtime.TRACE_SER) {
                System.out.println("\t\tFound repeated reference of type " + obj.getClass() + " at " + pos + " (absolute) in map");
            }
            // We have serialized this object beofre hence no need to do it again
            // In the C++ backend the value used is 0xFFFFFFFF
            // TODO keith Make this compliant with C++ value also make the position relative
            out.writeInt(refValue);
            out.writeInt(pos);
        } else {
            objectMap.put(obj, counter);
            if (Runtime.TRACE_SER) {
                System.out.println("\t\tRecorded new reference of type " + obj.getClass() + " at " + counter + " (absolute) in map");
            }
            counter++;
        }
        return pos;
    }
}
