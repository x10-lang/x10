/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import x10.core.Byte;
import x10.core.Rail;
import x10.io.Serializer;
import x10.rtt.Types;
import x10.runtime.impl.java.Runtime;
import x10.serialization.DeserializationDictionary.LocalDeserializationDictionary;

public final class X10JavaDeserializer implements SerializationConstants {
        
    // When a Object is deserialized record its position
    private final ArrayList<Object> objectList = new ArrayList<Object>();
    private int counter = 0;
    
    protected DataInputStream in;
    protected DeserializationDictionary dict; 
    
    private void init(DataInputStream in, boolean readMessageDictionary) {
        this.in = in;
        if (readMessageDictionary) {
            dict = new LocalDeserializationDictionary(this, SharedDictionaries.getDeserializationDictionary());
        } else {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\tMessage has no per-message serialization ids");                
            }
            dict = SharedDictionaries.getDeserializationDictionary();
        }
    }
    
    private void init(X10JavaSerializer js) {
        in = new DataInputStream(new ByteArrayInputStream(js.getDataBytes()));
        dict = new LocalDeserializationDictionary(js.idDictionary, SharedDictionaries.getDeserializationDictionary());
    }
    
    public X10JavaDeserializer(DataInputStream in, boolean readMessageDictionary) {
        init(in, readMessageDictionary);
    }
        
    /**
     * Specialized constructor for use by deep copy. 
     * As much as possible, attempt to streamline the movement of serialized data when staying in the same place.
     * @param js
     */
    public X10JavaDeserializer(X10JavaSerializer js) throws IOException {
        init(js);
    }
    

    /*
     * Constructor/init for usage as the backing @NativeClass for x10.io.Deserializer
     */
    public X10JavaDeserializer(System[] ignored) {
        // for use by generated code; $init methods will set instance fields
    }
    public X10JavaDeserializer x10$serialization$X10JavaDeserializer$$init$S(x10.core.Rail<Byte> data,
                                                                             x10.io.Deserializer.__0$1x10$lang$Byte$2 ignored) {
        init(new DataInputStream(new ByteArrayInputStream(data.getByteArray())), true);
        return this;
    }    
    public X10JavaDeserializer x10$serialization$X10JavaDeserializer$$init$S(x10.io.Serializer xjs) {
        init(xjs.__NATIVE_FIELD__);
        return this;
    }
    public java.lang.Object readAny() {
        try {
            return readRef();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public DataInput getInpForHadoop() {
        return in;
    }
    
    public int record_reference(Object obj) {
        if (Runtime.TRACE_SER) {
            String className = obj == null ? "null" : obj.getClass().getName();
            Runtime.printTraceMessage("\t\tRecorded new reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + className + Runtime.ANSI_RESET + " at " + counter + " (absolute) in map");
        }
        objectList.add(counter, obj);
        counter++;
        return counter - 1;
    }

    public void update_reference(int pos, Object obj) {
        objectList.set(pos, obj);
    }

    public Object getObjectAtPosition(int pos) {
        Object obj = objectList.get(pos);
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("\t\tRetrieving repeated reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET + " at " + pos + " (absolute) in map");
        }
        return obj;
    }

    public Class<?> getClassForID(short sid) {
        return dict.getClassForID(sid);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T readRef() throws IOException {
        short serializationID = readShort();
        return (T) readRef(serializationID);
    }

    public Object readRef(short serializationID) throws IOException {
        if (serializationID == NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserializing a null reference");
            }
            return null;
        }
        
        if (serializationID == REPEATED_OBJECT_ID) {
            return getObjectAtPosition(readInt());
        }
        
        if (serializationID <= MAX_HARDCODED_ID) {
            return X10JavaDeserializer.deserializePrimitive(serializationID, this);
        }

        if (serializationID == JAVA_ARRAY_ID) {
            short componentTypeID = readShort();
            if (componentTypeID == INTEGER_ID) {
                return readIntArray();
            } else if (componentTypeID == DOUBLE_ID) {
                return readDoubleArray();
            } else if (componentTypeID == FLOAT_ID) {
                return readFloatArray();
            } else if (componentTypeID == BOOLEAN_ID) {
                return readBooleanArray();
            } else if (componentTypeID == BYTE_ID) {
                return readByteArray();
            } else if (componentTypeID == SHORT_ID) {
                return readShortArray();
            } else if (componentTypeID == LONG_ID) {
                return readLongArray();
            } else if (componentTypeID == CHARACTER_ID) {
                return readCharArray();
            } else if (componentTypeID == STRING_ID) {
                return readStringArray();
            } else {
                Class<?> componentType = getClassForID(componentTypeID);
                int length = readInt();
                Object obj = Array.newInstance(componentType, length);
                record_reference(obj);
                if (componentType.isArray()) {
                    for (int i = 0; i < length; ++i) {
                        Array.set(obj, i, readArrayUsingReflection(componentType));
                    }
                } else {
                    for (int i = 0; i < length; ++i) {
                        Array.set(obj, i, readRefUsingReflection());
                    }
                }
                return obj;
            }
        }
        
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + serializationID);
        }
        
        try {
            Method method = dict.getMethod(serializationID);
            if (method != null) {
                // A class that implements X10JavaSerializable.  Call _deserialize
                return method.invoke(null, this);
            } else {
                Class<?> clazz = dict.getClassForID(serializationID);
                DeserializerThunk dt = DeserializerThunk.getDeserializerThunk(clazz);
                return dt.deserializeObject(clazz, this);
            }
        } catch (InvocationTargetException e) {
            // This should never happen
            throw new RuntimeException("Error in deserializing non-null value with id " + serializationID, e);
        } catch (SecurityException e) {
            // This should never happen
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // This should never happen
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            // This should never happen
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            // This should never happen
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void readArray(T[] array) throws IOException {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            array[i] = (T) readRef();
        }
    }

    public int readInt() throws IOException {
        int v = in.readInt();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [****] an " + Runtime.ANSI_CYAN + "int" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public int[] readIntArray() throws IOException {
        int length = in.readInt();
        int[] v = new int[length];
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = in.readInt();
        }
        return v;
    }

    public boolean readBoolean() throws IOException {
        boolean v = in.readBoolean();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [*] a " + Runtime.ANSI_CYAN + "boolean" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public boolean[] readBooleanArray() throws IOException {
        int length = in.readInt();
        boolean[] v = new boolean[length];
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = in.readBoolean();
        }
        return v;
    }

    public char readChar() throws IOException {
        char v = in.readChar();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [**] a " + Runtime.ANSI_CYAN + "char" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public char[] readCharArray() throws IOException {
        int length = in.readInt();
        char[] v = new char[length];
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = in.readChar();
        }
        return v;
    }

    public byte readByte() throws IOException {
        byte v = in.readByte();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [*] a " + Runtime.ANSI_CYAN + "byte" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public byte[] readByteArray() throws IOException {
        int length = in.readInt();
        byte[] v = new byte[length];
        record_reference(v);
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
            Runtime.printTraceMessage("Deserializing [**] a " + Runtime.ANSI_CYAN + "short" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public short[] readShortArray() throws IOException {
        int length = in.readInt();
        short[] v = new short[length];
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = in.readShort();
        }
        return v;
    }

    public long readLong() throws IOException {
        long v = in.readLong();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [********] a " + Runtime.ANSI_CYAN + "long" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public long[] readLongArray() throws IOException {
        int length = in.readInt();
        long[] v = new long[length];
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = in.readLong();
        }
        return v;
    }

    public double readDouble() throws IOException {
        double v = in.readDouble();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [********] a " + Runtime.ANSI_CYAN + "double" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public double[] readDoubleArray() throws IOException {
        int length = in.readInt();
        double[] v = new double[length];
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = in.readDouble();
        }
        return v;
    }

    public float readFloat() throws IOException {
        float v = in.readFloat();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [****] a " + Runtime.ANSI_CYAN + "float" + Runtime.ANSI_RESET + ": " + v);
        }
        return v;
    }

    public float[] readFloatArray() throws IOException {
        int length = in.readInt();
        float[] v = new float[length];
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = in.readFloat();
        }
        return v;
    }

    public String readString() throws IOException {
        short serializationID = readShort();
        if (serializationID == NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserializing a null reference");
            }
            return null;
        }
        if (serializationID == REPEATED_OBJECT_ID) {
            return (String) getObjectAtPosition(readInt());
        }
        assert serializationID == STRING_ID;
        String str = readStringValue();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing a " + Runtime.ANSI_CYAN + "String" + Runtime.ANSI_RESET + ": " + str);
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
        record_reference(v);
        for (int i = 0; i < length; i++) {
            v[i] = readString();
        }
        return v;
    }

    public Object readRefUsingReflection() throws IOException {
        short serializationID = readShort();
        if (serializationID == NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserializing a null reference");
            }
            return null;
        }
        if (serializationID == REPEATED_OBJECT_ID) {
            return getObjectAtPosition(readInt());
        }
        if (serializationID <= MAX_HARDCODED_ID) {
            return X10JavaDeserializer.deserializePrimitive(serializationID, this);
        }

        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + serializationID);
        }
        Class<?> clazz = getClassForID(serializationID);
            
        try {
            DeserializerThunk thunk = DeserializerThunk.getDeserializerThunk(clazz);
            return thunk.deserializeObject(clazz, this);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
    
    // This method is called from generated code when an X10 class has a Java superclass.
    public <T> Object deserializeClassUsingReflection(Class<? extends Object> clazz, T obj, int i) throws IOException {
        try {
            DeserializerThunk thunk = DeserializerThunk.getDeserializerThunk(clazz);
            return thunk.deserializeObject(clazz, obj, i, this);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
    
    <T> void readPrimitiveUsingReflection(Field field, T obj) throws IOException, IllegalAccessException {
        Class<?> type = field.getType();
        if ("int".equals(type.getName())) {
            field.setInt(obj, readInt());
        } else if ("double".equals(type.getName())) {
            field.setDouble(obj, readDouble());
        } else if ("float".equals(type.getName())) {
            field.setFloat(obj, readFloat());
        } else if ("boolean".equals(type.getName())) {
            field.setBoolean(obj, readBoolean());
        } else if ("byte".equals(type.getName())) {
            field.setByte(obj, readByte());
        } else if ("short".equals(type.getName())) {
            field.setShort(obj, readShort());
        } else if ("long".equals(type.getName())) {
            field.setLong(obj, readLong());
        } else if ("char".equals(type.getName())) {
            field.setChar(obj, readChar());
        }
    }

    public Object readArrayUsingReflection(Class<?> componentType) throws IOException {
        short serializationID = readShort();
        if (serializationID == NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserializing a null array");
            }
            return null;
        }
        if (serializationID == REPEATED_OBJECT_ID) {
            return getObjectAtPosition(readInt());
        }
        if (componentType.isPrimitive()) {
            if ("int".equals(componentType.getName())) {
                return readIntArray();
            } else if ("double".equals(componentType.getName())) {
                return readDoubleArray();
            } else if ("float".equals(componentType.getName())) {
                return readFloatArray();
            } else if ("boolean".equals(componentType.getName())) {
                return readBooleanArray();
            } else if ("byte".equals(componentType.getName())) {
                return readByteArray();
            } else if ("short".equals(componentType.getName())) {
                return readShortArray();
            } else if ("long".equals(componentType.getName())) {
                return readLongArray();
            } else if ("char".equals(componentType.getName())) {
                return readCharArray();
            }
        } else if ("java.lang.String".equals(componentType.getName())) {
            return readStringArray();
        } else {
            int length = readInt();
            Object o = Array.newInstance(componentType, length);
            record_reference(o);
            if (componentType.isArray()) {
                for (int i = 0; i < length; i++) {
                    Array.set(o, i, readArrayUsingReflection(componentType));
                }
            } else {
                for (int i = 0; i < length; i++) {
                    Array.set(o, i, readRefUsingReflection());
                }
            }
            return o;
        }
        return null;
    }

    String readStringUsingReflection() throws IOException {
        return readString();
    }

    // Read an object using java serialization. 
    // This is used by IMC to optimize the serialization of primitive arrays
    public Object readUsingObjectInputStream() throws IOException {
        ObjectInputStream ois = new ObjectInputStream(this.in);
        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Object deserializePrimitive(short serializationID, X10JavaDeserializer deserializer) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing primitive/special value with id " + serializationID);
        }
        Object obj = null;
        switch (serializationID) {
            case STRING_ID:
                obj = deserializer.readStringValue();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case FLOAT_ID:
                obj = deserializer.readFloat();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case DOUBLE_ID:
                obj = deserializer.readDouble();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case INTEGER_ID:
                obj = deserializer.readInt();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case BOOLEAN_ID:
                obj = deserializer.readBoolean();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case BYTE_ID:
                obj = deserializer.readByte();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case SHORT_ID:
                obj = deserializer.readShort();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case CHARACTER_ID:
                obj = deserializer.readChar();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case LONG_ID:
                obj = deserializer.readLong();
                deserializer.record_reference(obj); // TODO: consider avoid recording this as a reference; it can't be cyclic.
                break;
            case RTT_ANY_ID:
                obj = Types.ANY;
                break;
            case RTT_BOOLEAN_ID:
                obj = Types.BOOLEAN;
                break;
            case RTT_BYTE_ID:
                obj = Types.BYTE;
                break;
            case RTT_CHAR_ID:
                obj = Types.CHAR;
                break;
            case RTT_DOUBLE_ID:
                obj = Types.DOUBLE;
                break;
            case RTT_FLOAT_ID:
                obj = Types.FLOAT;
                break;
            case RTT_INT_ID:
                obj = Types.INT;
                break;
            case RTT_LONG_ID:
                obj = Types.LONG;
                break;
            case RTT_SHORT_ID:
                obj = Types.SHORT;
                break;
            case RTT_STRING_ID:
                obj = Types.STRING;
                break;
            case RTT_UBYTE_ID:
                obj = Types.UBYTE;
                break;
            case RTT_UINT_ID:
                obj = Types.UINT;
                break;
            case RTT_ULONG_ID:
                obj = Types.ULONG;
                break;
            case RTT_USHORT_ID:
                obj = Types.USHORT;
                break;
            default:
                throw new RuntimeException("Unhandled hard-wired serialization id in readPrimitive!");    
        }
        return obj;
    }
}
