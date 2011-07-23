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

import x10.core.GlobalRef;
import x10.core.IndexedMemoryChunk;
import x10.core.X10Throwable;
import x10.io.CustomSerialization;
import x10.io.SerialData;
import x10.runtime.impl.java.Runtime;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.TreeSet;

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
        int i = obj.$_get_serialization_id();
        if (Runtime.TRACE_SER) {
            System.out.println("Serializing id " + i  + " of type " + obj.getClass());
        }
        write(i);
        obj.$_serialize(this);
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

    public <T> void writeObjectUsingReflection(T body) throws IllegalAccessException, IOException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        if (body == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(body);
        if (pos != null) {
            return;
        }
        Class<? extends Object> bodyClass = body.getClass();
        Class<?> superclass = bodyClass.getSuperclass();
        if ("x10.rtt.FloatType".equals(bodyClass.getName()) || "x10.rtt.IntType".equals(bodyClass.getName())
                || "x10.rtt.DoubleType".equals(bodyClass.getName())
                || "x10.rtt.LongType".equals(bodyClass.getName())
                || "x10.rtt.BooleanType".equals(bodyClass.getName())
                || "x10.rtt.CharType".equals(bodyClass.getName())
                || "x10.rtt.ByteType".equals(bodyClass.getName())
                || "x10.rtt.ShortType".equals(bodyClass.getName())
                || "x10.rtt.ObjectType".equals(bodyClass.getName())
                || "x10.rtt.UByteType".equals(bodyClass.getName())
                || "x10.rtt.UIntType".equals(bodyClass.getName())
                || "x10.rtt.ULongType".equals(bodyClass.getName())
                || "x10.rtt.UShortType".equals(bodyClass.getName())) {
            int id = DeserializationDispatcher.getIDForClassName(superclass.getName());
            write(id);
            // These classes dont implement the serialization/deserialization routines, hence we serialize the superclass
            serializeClassUsingReflection(body, superclass);
            return;
        }
        int id = DeserializationDispatcher.getIDForClassName(bodyClass.getName());
        write(id);
        serializeClassUsingReflection(body, bodyClass);
    }

    private <T> void serializeClassUsingReflection(T body, Class<? extends Object> bodyClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException, NoSuchFieldException {

        // We need to handle these classes in a special way cause there implementation of serialization/deserialization is
        // not straight forward. Hence we just call into the custom serialization of these classes.
        if ("x10.rtt.NamedType".equals(bodyClass.getName())) {
            serializeClassUsingReflection(body, bodyClass.getSuperclass());
            Field typeNameField = bodyClass.getDeclaredField("typeName");
            String typeName = (String) typeNameField.get(body);
            int classId = DeserializationDispatcher.getIDForClassName(typeName);
            write(classId);
            return;
        } else if ("x10.rtt.RuntimeType".equals(bodyClass.getName())) {
            Field implField = bodyClass.getDeclaredField("impl");
            Class<?> impl = (Class<?>) implField.get(body);
            int classId = DeserializationDispatcher.getIDForClassName(impl.getName());
            write(classId);
            return;
        } else if ("x10.core.IndexedMemoryChunk".equals(bodyClass.getName())) {
            ((IndexedMemoryChunk) body).$_serialize(this);
            return;
        } else if ("x10.core.IndexedMemoryChunk$$Closure$0".equals(bodyClass.getName())) {
            ((IndexedMemoryChunk.$Closure$0) body).$_serialize(this);
            return;
        } else if ("x10.core.IndexedMemoryChunk$$Closure$1".equals(bodyClass.getName())) {
            ((IndexedMemoryChunk.$Closure$1) body).$_serialize(this);
            return;
        } else if (GlobalRef.class.getName().equals(bodyClass.getName())) {
            ((GlobalRef) body).$_serialize(this);
            return;
        } else if (X10Throwable.class.getName().equals(bodyClass.getName())) {
            ((X10Throwable) body).$_serialize(this);
            return;
        }

        Class[] interfaces = bodyClass.getInterfaces();
        boolean isCustomSerializable = false;
        for (Class aInterface : interfaces) {
            if ("x10.io.CustomSerialization".equals(aInterface.getName())) {
                isCustomSerializable = true;
                break;
            }
        }

        Class<?> superclass = bodyClass.getSuperclass();
        if (!isCustomSerializable && !("java.lang.Object".equals(superclass.getName()) || "x10.core.Ref".equals(superclass.getName()) || "x10.core.Struct".equals(superclass.getName()))) {
            // We need to serialize the super class first
            serializeClassUsingReflection(body, superclass);
        }

        // We need to sort the fields first. Cause the order here could depend on the JVM.
        Field[] declaredFields = bodyClass.getDeclaredFields();
        Set<Field> fields = new TreeSet<Field>(new FieldComparator());
        for (Field field : declaredFields) {
            if (field.isSynthetic())
                continue;
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }
            fields.add(field);
        }

        for (Field field: fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (type.isPrimitive()) {
                writePrimitiveUsingReflection(field, body);
            } else if (type.isArray()) {
                writeArrayUsingReflection(field.get(body));
            } else if ("java.lang.String".equals(type.getName())) {
                writeStringUsingReflection(field, body);
            } else {
                writeObjectUsingReflection(field.get(body));
            }
        }

        if (isCustomSerializable) {
            CustomSerialization cs = (CustomSerialization)body;
            SerialData serialData = cs.serialize();
            writeObjectUsingReflection(serialData);
        }
    }


    private void writeArrayUsingReflection(Object obj) throws IllegalAccessException, IOException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Class<?> componentType = obj.getClass().getComponentType();
        int length = Array.getLength(obj);
        write(length);
        if (componentType.isPrimitive()) {
            if ("int".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getInt(obj, i));
                }
            } else if ("double".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getDouble(obj, i));
                }
            } else if ("float".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getFloat(obj, i));
                }
            } else if ("boolean".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getBoolean(obj, i));
                }
            } else if ("byte".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getByte(obj, i));
                }
            } else if ("short".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getShort(obj, i));
                }
            } else if ("long".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getLong(obj, i));
                }
            } else if ("char".equals(componentType.getName())) {
                for (int i = 0; i < length; i++) {
                    write(Array.getChar(obj, i));
                }
            }
        } else if ("java.lang.String".equals(componentType.getName())) {
            for (int i = 0; i < length; i++) {
                String str = (String) Array.get(obj, i);
                write(str);
            }
        } else if (componentType.isArray()) {
            for (int i = 0; i < length; i++) {
                Object o = Array.get(obj, i);
                writeArrayUsingReflection(o);
            }
        } else {
            for (int i = 0; i < length; i++) {
                Object o = Array.get(obj, i);
                writeObjectUsingReflection(o);
            }
        }
    }

    private <T> void writeStringUsingReflection(Field field, T obj) throws IllegalAccessException, IOException {
        String str = (String) field.get(obj);
        write(str);
    }

    private <T> void writePrimitiveUsingReflection(Field field, T obj) throws IllegalAccessException, IOException {
        Class<?> type = field.getType();
        if ("int".equals(type.getName())) {
            write(field.getInt(obj));
        } else if ("double".equals(type.getName())) {
            write(field.getDouble(obj));
        } else if ("float".equals(type.getName())) {
            write(field.getFloat(obj));
        } else if ("boolean".equals(type.getName())) {
            write(field.getBoolean(obj));
        } else if ("byte".equals(type.getName())) {
            write(field.getByte(obj));
        } else if ("short".equals(type.getName())) {
            write(field.getShort(obj));
        } else if ("long".equals(type.getName())) {
            write(field.getLong(obj));
        } else if ("char".equals(type.getName())) {
            write(field.getChar(obj));
        }
    }
}
