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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import x10.io.CustomSerialization;
import x10.rtt.Types;
import x10.runtime.impl.java.Runtime;
import x10.serialization.SerializationDictionary.LocalSerializationDictionary;

public final class X10JavaSerializer implements SerializationConstants {
        
    protected final DataOutputStream out;
    protected final ByteArrayOutputStream b_out;
    
    protected byte[] dictBytes;
    protected byte[] dataBytes;
    
    protected boolean messagePrepared = false;
    
    // When a Object is serialized record its position
    // N.B. use custom IdentityHashMap class, as standard one has poor performance on J9
    X10IdentityHashMap<Object, Integer> objectMap = new X10IdentityHashMap<Object, Integer>();
    int counter = 0;
    
    // [GlobalGC] Table to remember serialized GlobalRefs, set and used in GlobalRef.java and InitDispatcher.java
    X10IdentityHashMap<x10.core.GlobalRef<?>, Integer> grefMap = new X10IdentityHashMap<x10.core.GlobalRef<?>, Integer>(); // for GlobalGC
    public void addToGrefMap(x10.core.GlobalRef<?> gr, int weight) { grefMap.put(gr, weight); }
    public java.util.Map<x10.core.GlobalRef<?>, Integer> getGrefMap() { return grefMap; }
    
    // per-message id dictionary
    protected final LocalSerializationDictionary idDictionary;
    
    public X10JavaSerializer() {
        this.b_out = new ByteArrayOutputStream();
        this.out = new DataOutputStream(this.b_out);
        this.idDictionary = new LocalSerializationDictionary(SharedDictionaries.getSerializationDictionary(), FIRST_DYNAMIC_ID);
    }
    
    /*
     * for use by @NativeClass code in x10.io.Serializer
     */
    public X10JavaSerializer(System[] ignored) {
        this();
    }
    public X10JavaSerializer x10$serialization$X10JavaSerializer$$init$S() {
        // init handled by System[] constructor; nothing else to do.
        return this;
    }
    
    @SuppressWarnings("rawtypes")
    public x10.core.Rail toRail() {
        try {
            prepareMessage(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] dictBytes = getDictionaryBytes();
        byte[] dataBytes = getDataBytes();
        byte[] messageBytes = new byte[getTotalMessageBytes()];
        System.arraycopy(dictBytes, 0, messageBytes, 0, dictBytes.length);
        System.arraycopy(dataBytes, 0, messageBytes, dictBytes.length, dataBytes.length);
        return new x10.core.Rail(Types.BYTE, messageBytes.length, messageBytes);
    }
    
    
    public DataOutput getOutForHadoop() {
        return out;
    }
    
    public int numDataBytesWritten() {
        return b_out.size();
    }
    
    public void prepareMessage(boolean dataOnly) throws IOException {
        if (messagePrepared) return; // make it cheap to call prepareMessage multiple times
        out.close();
        
        if (dataOnly) {
            dictBytes = new byte[0];
        } else {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Encoding per-message serialization ids: "+idDictionary);
            }
            dictBytes = idDictionary.encode();
        }
        dataBytes = b_out.toByteArray();
        messagePrepared = true;
    }
    
    public boolean mustSendDictionary() { 
        return idDictionary.dict.size() > 0;
    }
    
    public byte[] getDictionaryBytes() {
        if (Runtime.TRACE_SER && !messagePrepared) {
            Runtime.printTraceMessage("Fatal error: getDictionaryBytes call before prepareMessage)");
        }
            
        assert messagePrepared : "Must call prepareMessage before asking for dictBytes";
        return dictBytes;
    }
    
    public byte[] getDataBytes() {
        if (Runtime.TRACE_SER && !messagePrepared) {
            Runtime.printTraceMessage("Fatal error: getDataBytes call before prepareMessage)");
        }
            
        assert messagePrepared : "Must call prepareMessage before asking for dataBytes";
        return dataBytes;
    }
    
    public int getTotalMessageBytes() {
        return getDictionaryBytes().length + getDataBytes().length;
    }
        
    public short getSerializationId(Class<?> clazz, Object obj) {
        return idDictionary.getSerializationId(clazz, obj);
    }
    
    public void write(X10JavaSerializable obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }

        short i = getSerializationId(obj.getClass(), obj);
        if (i <= MAX_HARDCODED_ID) {
            switch (i) {
            case RTT_ANY_ID: 
                case RTT_BOOLEAN_ID:
                case RTT_BYTE_ID:
                case RTT_CHAR_ID:
                case RTT_DOUBLE_ID:
                case RTT_FLOAT_ID:
                case RTT_INT_ID:
                case RTT_LONG_ID:
                case RTT_SHORT_ID:
                case RTT_STRING_ID:
                case RTT_UBYTE_ID:
                case RTT_UINT_ID:
                case RTT_ULONG_ID: 
                case RTT_USHORT_ID:
                    out.writeShort(i);
                    if (Runtime.TRACE_SER) {
                        Runtime.printTraceMessage("Optimized serializing [**] a " + Runtime.ANSI_CYAN + "serialization_id_t" + Runtime.ANSI_RESET + ": " + i);
                    }
                    return;
            
            default:
                System.err.println("Unhanlded hardcoded serialization id");
                throw new RuntimeException("Unhandled hard-wired serialization id in write(X10JavaSerializable)");    
            }            
        }
        
        Integer pos = previous_position(obj, true);
        if (pos != null) {
            return;
        }
        
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "serialization_id_t" + Runtime.ANSI_RESET + ": " + i);
        }
        out.writeShort(i);
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET);
        }
        obj.$_serialize(this);
    }

    private void writeNull() throws IOException {
        write(NULL_ID);
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a null reference");
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
    
    public void write(Object[] obj) throws IOException {
        write(obj.length);
        for (Object o : obj) {
            write(o);
        }
    }

    public void write(int i) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [****] an " + Runtime.ANSI_CYAN + "int" + Runtime.ANSI_RESET + ": " + i);
        }
        out.writeInt(i);
    }

    public void write(Integer p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing an " + Runtime.ANSI_CYAN + "Integer" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(INTEGER_ID);
        out.writeInt(p.intValue());
    }

    public void write(int[] i) throws IOException {
        out.writeInt(i.length);
        for (int j : i) {
            out.writeInt(j);
        }
    }

    public void write(boolean b) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [*] a " + Runtime.ANSI_CYAN + "boolean" + Runtime.ANSI_RESET + ": " + b);
        }
        out.writeBoolean(b);
    }

    public void write(Boolean p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Boolean" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(BOOLEAN_ID);
        out.writeBoolean(p.booleanValue());
    }

    public void write(boolean[] v) throws IOException {
        out.writeInt(v.length);
        for (boolean b : v) {
            out.writeBoolean(b);
        }
    }

    public void write(char c) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "char" + Runtime.ANSI_RESET + ": " + c);
        }
        out.writeChar(c);
    }

    public void write(Character p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Character" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(CHARACTER_ID);
        out.writeChar(p.charValue());
    }

    public void write(char[] v) throws IOException {
        out.writeInt(v.length);
        for (char c : v) {
            out.writeChar(c);
        }
    }

    public void write(byte b) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [*] a " + Runtime.ANSI_CYAN + "byte" + Runtime.ANSI_RESET + ": " + b);
        }
        out.writeByte(b);
    }

    public void write(Byte p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Byte" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(BYTE_ID);
        out.writeByte(p.byteValue());
    }

    public void write(byte[] b) throws IOException {
        out.writeInt(b.length);
        _write(b);
    }

    public void _write(byte[] b) throws IOException {
        out.write(b);
    }

    public void write(short s) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "short" + Runtime.ANSI_RESET + ": " + s);
        }
        out.writeShort(s);
    }

    public void write(Short p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Short" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(SHORT_ID);
        out.writeShort(p.shortValue());
    }

    public void write(short[] v) throws IOException {
        out.writeInt(v.length);
        for (short s : v) {
            out.writeShort(s);
        }
    }

    public void write(long l) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [********] a " + Runtime.ANSI_CYAN + "long" + Runtime.ANSI_RESET + ": " + l);
        }
        out.writeLong(l);
    }

    public void write(Long p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Long" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(LONG_ID);
        out.writeLong(p.longValue());
    }

    public void write(long[] v) throws IOException {
        out.writeInt(v.length);
        for (long l : v) {
            out.writeLong(l);
        }
    }

    public void write(double d) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [********] a " + Runtime.ANSI_CYAN + "double" + Runtime.ANSI_RESET + ": " + d);
        }
        out.writeDouble(d);
    }

    public void write(Double p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Double" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(DOUBLE_ID);
        out.writeDouble(p.doubleValue());
    }

    public void write(double[] v) throws IOException {
        out.writeInt(v.length);
        for (double d : v) {
            out.writeDouble(d);
        }
    }

    public void write(float f) throws IOException {
        if (Runtime.TRACE_SER) {           
            Runtime.printTraceMessage("Serializing [****] a " + Runtime.ANSI_CYAN + "float" + Runtime.ANSI_RESET + ": " + f);
        }
        out.writeFloat(f);
    }

    public void write(Float p) throws IOException {
        if (p == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(p, true);
        if (pos != null) {
            return;
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Float" + Runtime.ANSI_RESET + ": " + p);
        }
        out.writeShort(FLOAT_ID);
        out.writeFloat(p.floatValue());
    }

    public void write(float[] v) throws IOException {
        out.writeInt(v.length);
        for (float f : v) {
            out.writeFloat(f);
        }
    }

    public void write(Object v) throws IOException {
        if (v == null) {
            writeNull();
            return;
        }
        if (v.getClass().isArray()) {
            writeArrayUsingReflectionWithType(v);
            return;
        }
        writeObjectUsingReflection(v);
    }
    
    // Called from x10.io.Serializer.
    // The point of this wrapper message is to avoid a throws java.io.IOException
    // in the X10 API for Serializer.writeAny(v:Any).
    public void writeAny(Object v) {
        try {
            write(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(String str) throws IOException {
        if (str == null) {
            writeNull();
            return;
        }

        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "String" + Runtime.ANSI_RESET + ": " + str);
        }

        Integer pos = previous_position(str, true);
        if (pos != null) {
            return;
        }
        write(STRING_ID);
        writeStringValue(str);
    }

    void writeStringValue(String str) throws IOException {
        write(str.length());
        out.write(str.getBytes());
    }

    public void write(String[] v) throws IOException {
        out.writeInt(v.length);
        for (String str : v) {
            write(str);
        }
    }

    public void recordReference(Object obj) throws IOException {
        previous_position(obj, false);
    }

    private Integer previous_position(Object obj, boolean writeRef) throws IOException {
        Integer pos = objectMap.get(obj);
        if (pos != null) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\t\tFound repeated reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET+ " at " + pos + " (absolute) in map");
            }
            // We have serialized this object before hence no need to do it again
            if (writeRef) {
                write(REPEATED_OBJECT_ID);
                write(pos.intValue());
            }
        } else {
            objectMap.put(obj, counter);
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("\t\tRecorded new reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET + " at " + counter + " (absolute) in map");
            }
            counter++;
        }
        return pos;
    }

    public <T> void writeObjectUsingReflection(T body) throws IOException {
        if (body == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(body, true);
        if (pos != null) {
            return;
        }
        
        // Special case: optimize transmission of RTT's for primitives
        if (body instanceof x10.rtt.RuntimeType<?>) {
            int id = ((x10.rtt.RuntimeType<?>) body).$_get_serialization_id();
            if (id <= MAX_HARDCODED_ID) {
                write(id);
                return;
            }   
        }

        try {
            Class<? extends Object> bodyClass = body.getClass();
            write(getSerializationId(bodyClass, body));
            SerializerThunk st = SerializerThunk.getSerializerThunk(bodyClass);
            st.serializeObject(body, bodyClass, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // This method is called from generated code when an X10 class has a Java superclass
    public <T> void serializeClassUsingReflection(T obj, Class<T> clazz) throws IOException {
        try {
            SerializerThunk st = SerializerThunk.getSerializerThunk(clazz);
            st.serializeObject(obj, clazz, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }       
    }

    static final boolean THROWABLES_SERIALIZE_MESSAGE = true;
    static final boolean THROWABLES_SERIALIZE_STACKTRACE = true;
    static final boolean THROWABLES_SERIALIZE_CAUSE = true;

    public void writeArrayUsingReflection(Object obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(obj, true);
        if (pos != null) {
            return;
        }
        write(JAVA_ARRAY_ID);
        int length = Array.getLength(obj);
        write(length);
        Class<?> componentType = obj.getClass().getComponentType();
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

    public void writeArrayUsingReflectionWithType(Object obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }
        Integer pos = previous_position(obj, true);
        if (pos != null) {
            return;
        }
        write(JAVA_ARRAY_ID);
        Class<?> componentType = obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            if ("int".equals(componentType.getName())) {
                write(INTEGER_ID);
                write((int[]) obj);
            } else if ("double".equals(componentType.getName())) {
                write(DOUBLE_ID);
                write((double[]) obj);
            } else if ("float".equals(componentType.getName())) {
                write(FLOAT_ID);
                write((float[]) obj);
            } else if ("boolean".equals(componentType.getName())) {
                write(BOOLEAN_ID);
                write((boolean[]) obj);
            } else if ("byte".equals(componentType.getName())) {
                write(BYTE_ID);
                write((byte[]) obj);
            } else if ("short".equals(componentType.getName())) {
                write(SHORT_ID);
                write((short[]) obj);
            } else if ("long".equals(componentType.getName())) {
                write(LONG_ID);
                write((long[]) obj);
            } else if ("char".equals(componentType.getName())) {
                write(CHARACTER_ID);
                write((char[]) obj);
            }
        } else if ("java.lang.String".equals(componentType.getName())) {
            write(STRING_ID);
            write((java.lang.String[]) obj);
        } else {
            write(getSerializationId(componentType, null));
            if (componentType.isArray()) {
                int length = Array.getLength(obj);
                write(length);
                for (int i = 0; i < length; ++i) {
                    Object o = Array.get(obj, i);
                    writeArrayUsingReflection(o);
                }
            } else {
                write((java.lang.Object[]) obj);
            }
        }
    }

    <T> void writeStringUsingReflection(Field field, T obj) throws IllegalAccessException, IOException {
        String str = (String) field.get(obj);
        write(str);
    }

    <T> void writePrimitiveUsingReflection(Field field, T obj) throws IllegalAccessException, IOException {
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

    // Write an object using java serialization. 
    // This is used by IMC to optimize the serialization of primitive arrays
    public void writeUsingObjectOutputStream(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(this.out);
        oos.writeObject(obj);
    }
}
