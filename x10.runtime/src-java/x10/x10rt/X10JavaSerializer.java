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

import x10.core.UInt;
import x10.io.CustomSerialization;
import x10.rtt.Type;
import x10.x10rt.DeserializationDispatcher;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.IdentityHashMap;
import java.util.Map;

public class X10JavaSerializer {

    //TODO Keith check what the C++ side do on arrays

    // When a Object is serialized record its position
    IdentityHashMap<Object, Integer> objectMap = new IdentityHashMap<Object, Integer>();
    static final int refValue = Integer.parseInt("FFFFFFF", 16);
    ObjectOutput out;
    int counter = 0;

    public X10JavaSerializer(ObjectOutput out) {
        this.out = out;
    }

    public void write(X10JavaSerializable obj) throws IOException {
        if (obj == null) {
            out.writeInt(DeserializationDispatcher.NULL_ID);
            return;
        }

        if (obj.getClass().toString().equals("java.lang.Object")) {
            return;
        }
        Integer pos;
        if ((pos = objectMap.get(obj)) != null) {
            // We have serialized this object before hence no need to do it again
            // In the C++ backend the value used is 0xFFFFFFFF
            // TODO keith Make this compliant with C++ value also make the position relative
            out.writeInt(refValue);
            out.writeInt(pos);
        } else {
            objectMap.put(obj, counter);
            counter++;
            int i = obj._get_serialization_id();
            write(i);
            obj._serialize(this);
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
        out.writeInt(i);
    }

    public void write(int[] i) throws IOException {
        out.writeInt(i.length);
        for (int j : i) {
            out.writeInt(j);
        }
    }

    public void write(boolean b) throws IOException {
        out.writeBoolean(b);
    }

    public void write(boolean v[]) throws IOException {
        out.writeInt(v.length);
        for (boolean b : v) {
            out.writeBoolean(b);
        }
    }

    public void write(char c) throws IOException {
        out.writeChar(c);
    }

    public void write(char[] v) throws IOException {
        out.writeInt(v.length);
        for (char c : v) {
            out.writeChar(c);
        }
    }

    public void write(byte b) throws IOException {
        out.writeByte(b);
    }

    public void write(byte[] b) throws IOException {
        out.writeInt(b.length);
        out.write(b);
    }

    public void write(short s) throws IOException {
        out.writeShort(s);
    }

    public void write(short[] v) throws IOException {
        out.writeInt(v.length);
        for (short s : v) {
            out.writeShort(s);
        }
    }

    public void write(long l) throws IOException {
        out.writeLong(l);
    }

    public void write(long[] v) throws IOException {
        out.writeInt(v.length);
        for (long l : v) {
            out.writeLong(l);
        }
    }

    public void write(double d) throws IOException {
        out.writeDouble(d);
    }

    public void write(double[] v) throws IOException {
        out.writeInt(v.length);
        for (double d : v) {
            out.writeDouble(d);
        }
    }

    public void write(float f) throws IOException {
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
            out.writeInt(DeserializationDispatcher.NULL_ID);
            return;
        }

        Integer pos;
        if ((pos = objectMap.get(str)) != null) {
            // We have serialized this object beofre hence no need to do it again
            // In the C++ backend the value used is 0xFFFFFFFF
            // TODO keith Make this compliant with C++ value also make the position relative
            out.writeInt(refValue);
            out.writeInt(pos);
            return;
        }

        objectMap.put(str, counter);
        counter++;
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
            out.writeInt(DeserializationDispatcher.NULL_ID);
            return;
        }
        Integer pos;
        if ((pos = objectMap.get(p)) != null) {
            // We have serialized this object beofre hence no need to do it again
            // In the C++ backend the value used is 0xFFFFFFFF
            // TODO keith Make this compliant with C++ value also make the position relative
            out.writeInt(refValue);
            out.writeInt(pos);
            return;
        }
        objectMap.put(p, counter);
        counter++;
        int id = DeserializationDispatcher.getIDForClassName(p.getClass().getName());
        if (id == DeserializationDispatcher.STRING_ID) {
            writeStringValue(p.toString());
            return;
        }
        out.writeInt(id);
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
}
