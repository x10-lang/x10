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

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.IdentityHashMap;
import java.util.Map;

public class X10JavaSerializer {

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
        write(obj._get_serialization_id());
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

    public void write(CustomSerialization obj) {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("INSIDE method CustomSerialization ");
        }
    }

    public void write(X10JavaSerializable obj[]) throws IOException {
        //TODO Keith check what the C++ side do on arrays
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

    public void write(boolean b) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Boolean : " + b);
        }
        out.writeBoolean(b);
    }

    public void write(char c) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Char : " + c);
        }
        out.writeChar(c);
    }

    public void write(byte b) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Byte : " + b);
        }
        out.writeByte(b);
    }

    public void write(short s) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Short : " + s);
        }
        out.writeShort(s);
    }

    public void write(long l) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Long : " + l);
        }
        out.writeLong(l);
    }

    public void write(double d) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Double : " + d);
        }
        out.writeDouble(d);
    }

    public void write(float f) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Float : " + f);
        }
        out.writeFloat(f);
    }

    public void writeWithID(Float f) throws IOException {
        if (x10.runtime.impl.java.Runtime.TRACE_SER_DETAIL) {
            System.out.println("Float : " + f);
        }
        out.writeInt(DeserializationDispatcher.FLOAT_ID);
        out.writeFloat(f);
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
}
