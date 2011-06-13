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
import x10.rtt.Type;
import x10.x10rt.DeserializationDispatcher;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.IdentityHashMap;
import java.util.Map;

public class X10JavaSerializer {

    //TODO Keith check what the C++ side do on arrays
    // When a Object is serialized record its position
    Map<X10JavaSerializable, Integer> objectMap = new IdentityHashMap<X10JavaSerializable, Integer>();
    ObjectOutput out;
    int counter = 0;

    public X10JavaSerializer(ObjectOutput out) {
        this.out = out;
    }

    public ObjectOutput getOutputStream() {
        return out;
    }

    public void write(X10JavaSerializable obj) throws IOException {
        if (obj == null) {
            if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
                System.out.println("Object is null");
            }
            out.writeInt(DeserializationDispatcher.NULL_ID);
            return;
        }

        if (obj.getClass().toString().equals("java.lang.Object")) {
            return;
        }
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Serializing  Class : " + obj.getClass());
            System.out.print("Serialization id : ");
        }
        int i = obj._get_serialization_id();
        if (i ==0) {
            System.out.println();
        }
        write(i);
//		Integer pos;
//		if ((pos = objectMap.get(obj)) != null) {
//			System.out.println("Object already in map pos : " + pos);
//			// We have serialized this object beofre hence no need to do it again
//			// In the C++ backend the value used is 0xFFFFFFFF
//			// TODO keith Make this compliant with C++ value also make the position relative
//				out.writeInt(Integer.parseInt("FFFFFFFF", 16));
//				out.writeInt(pos);
//		} else {
//			System.out.println("Object not in map serializing " + counter);
//			objectMap.put(obj, counter);
//			counter++;
        obj._serialize(this);
//		}
    }

    public void write(CustomSerialization obj) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("INSIDE method CustomSerialization ");
        }
        write((X10JavaSerializable)obj);
    }

    public void write(X10JavaSerializable obj[]) throws IOException {

        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.print("Length of array : ");
        }
        write(obj.length);
        for (X10JavaSerializable o : obj) {
            if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
                System.out.println("Element of array : ");
            }
            write(o);
        }
    }

    public void javaSerialize(Object obj) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Gonna do default serialization for " + obj.getClass());
        }
        out.writeObject(obj);
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Done with the default serialization");
        }
    }

    public void write(int i) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Int : " + i);
        }
        out.writeInt(i);
    }

    public void write(int[] i) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Int[] length  : " + i.length);
        }
        out.writeInt(i.length);
        for (int j : i) {
            out.writeInt(j);
        }
    }

    public void write(boolean b) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Boolean : " + b);
        }
        out.writeBoolean(b);
    }

    public void write(boolean v[]) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Boolean[] length : " + v.length);
        }
        out.writeInt(v.length);
        for (boolean b: v) {
            out.writeBoolean(b);
        }
    }

    public void write(char c) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Char : " + c);
        }
        out.writeChar(c);
    }

    public void write(char[] v) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Char[] length : " + v.length);
        }
        out.writeInt(v.length);
        for (char c:v) {
            out.writeChar(c);
        }
    }

    public void write(byte b) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Byte : " + b);
        }
        out.writeByte(b);
    }

    public void write(byte[] b) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Byte : " + b);
        }
        out.writeInt(b.length);
        out.write(b);
    }

    public void write(short s) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Short : " + s);
        }
        out.writeShort(s);
    }

    public void write(short[] v) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Short[] length : " + v.length);
        }
        out.writeInt(v.length);
        for (short s:v) {
            out.writeShort(s);
        }
    }

    public void write(long l) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Long : " + l);
        }
        out.writeLong(l);
    }

    public void write(long[] v) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Long[] length : " + v.length);
        }
        out.writeInt(v.length);
        for (long l:v) {
            out.writeLong(l);
        }
    }

    public void write(double d) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Double : " + d);
        }
        out.writeDouble(d);
    }

    public void write(double[] v) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Double[] length : " + v.length);
        }
        out.writeInt(v.length);
        for (double d:v) {
            out.writeDouble(d);
        }
    }

    public void write(float f) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Float : " + f);
        }
        out.writeFloat(f);
    }

    public void write(float[] v) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Float[] length : " + v.length);
        }
        out.writeInt(v.length);
        for (float f : v) {
            out.writeFloat(f);
        }
    }

    public void write(String str) throws IOException {
        if (str == null) {
            if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
                System.out.println("String is null ");
            }
            out.writeInt(DeserializationDispatcher.NULL_ID);
            return;
        }

        out.writeInt(DeserializationDispatcher.STRING_ID);
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.print("String length : ");
        }
        write(str.length());
        out.write(str.getBytes());
    }

    public void write(String[] v) throws IOException {
        out.writeInt(DeserializationDispatcher.STRING_ID);
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.print("String[] length : " + v.length);
        }
        out.writeInt(v.length);
        for (String str: v) {
            write(str);
        }
    }

    public  <T> void write(T p) throws IOException {
        if (p == null) {
             out.writeInt(DeserializationDispatcher.NULL_ID);
            return;
        }
        int id = DeserializationDispatcher.getIDForClassName(p.getClass().getName());
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
                System.out.println("Class name : " + p.getClass().getName() + " , id : " + id);
        }

        if (id == DeserializationDispatcher.STRING_ID) {
            write(p.toString());
            return;
        }
        out.writeInt(id);
        if (id == DeserializationDispatcher.FLOAT_ID) {
            out.writeFloat((Float)p);
        } else if (id == DeserializationDispatcher.DOUBLE_ID) {
            out.writeDouble((Double) p);
        } else if (id == DeserializationDispatcher.INTEGER_ID) {
            out.writeInt((Integer) p);
        } else if (id == DeserializationDispatcher.BOOLEAN_ID) {
            out.writeBoolean((Boolean) p);
        } else if (id == DeserializationDispatcher.BYTE_ID) {
            out.writeByte((Byte) p);
        } else if (id == DeserializationDispatcher.CHARACTER_ID) {
            out.writeChar((Character)p);
        } else {
            throw  new RuntimeException("################## Need to handle " + p.getClass().getName());
        }
    }
}
