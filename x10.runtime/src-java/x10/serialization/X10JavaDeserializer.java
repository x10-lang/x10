/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import x10.core.Rail;
import x10.io.SerializationException;
import x10.rtt.Types;
import x10.runtime.impl.java.Runtime;
import x10.serialization.DeserializationDictionary.LocalDeserializationDictionary;

public final class X10JavaDeserializer implements SerializationConstants {
        
    // When a Object is deserialized record its position
    private final ArrayList<Object> objectList = new ArrayList<Object>();
    private int counter = 0;
    
    protected DataInputStream in;
    protected LocalDeserializationDictionary dict; 
    
    private void init(DataInputStream in) {
        this.in = in;
        dict = new LocalDeserializationDictionary(SharedDictionaries.getDeserializationDictionary());
    }
    
    private void init(X10JavaSerializer js) {
        in = new DataInputStream(new ByteArrayInputStream(js.getDataBytes()));
        dict = new LocalDeserializationDictionary(js.idDictionary, SharedDictionaries.getDeserializationDictionary());
    }
    
    public X10JavaDeserializer(DataInputStream in) {
        init(in);
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
    public X10JavaDeserializer x10$serialization$X10JavaDeserializer$$init$S(Rail<x10.core.Byte> data,
                                                                             x10.io.Deserializer.__0$1x10$lang$Byte$2 ignored) {
        init(new DataInputStream(new ByteArrayInputStream(data.getByteArray())));
        return this;
    }    
    public X10JavaDeserializer x10$serialization$X10JavaDeserializer$$init$S(x10.io.Serializer xjs) {
        init(xjs.__NATIVE_FIELD__);
        return this;
    }
    
    public X10JavaDeserializer x10$serialization$X10JavaDeserializer$$init$S(x10.io.InputStreamReader is) {
        init(new DataInputStream(is.stream().getJavaInputStream()));
        return this;
    }
        
    public Object readAny() {
        try {
            return readObject();
        } catch (SerializationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e; // don't wrap
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
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
        if (Runtime.TRACE_SER) {
            String className = obj == null ? "null" : obj.getClass().getName();
            Runtime.printTraceMessage("\t\tUpdated reference of type " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + className + Runtime.ANSI_RESET + " at " + pos + " (absolute) in map");
        }
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

    /**
     * Read the next serialization id that is not DYNAMIC_ID_ID
     * and return it.  Process all DYNAMIC_ID_ID entries encountered
     * when caller is expecting a serialization id.
     * @return the next serializationId that is not DYNAMIC_ID_ID
     */
    public short readSerializationId() throws IOException {
        short sid = in.readShort();
        while (sid == DYNAMIC_ID_ID) {
            // A dictionary entry; process it and keep looking for the actual sid
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Adding a dynamic serialization id to the dictionary");
            }
            dict.deserializeIdAssignment(this);
            sid = in.readShort();
        }
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserialized a serialization id "+sid);
        }
        if (sid == RESET_OBJECT_GRAPH_BOUNDARY_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("RESETTING OBJECT GRAPH IDS");
            }
            objectList.clear();
            counter = 0;
            return readSerializationId();
        }
        return sid;
    }    

    
    @SuppressWarnings("unchecked")
    public <T> T readObject() throws IOException {
        short serializationID = readSerializationId();

        if (serializationID == NULL_ID) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Deserializing a null reference");
            }
            return null;
        }
        
        if (serializationID == REPEATED_OBJECT_ID) {
            return (T)getObjectAtPosition(readInt());
        }
        
        if (serializationID <= MAX_HARDCODED_ID) {
            return (T)deserializeSpecialType(serializationID);
        }

        if (serializationID == JAVA_ARRAY_ID) {
            return (T)deserializeArray();
        }
        
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing non-null value with id " + serializationID);
        }
        
        try {
            Method method = dict.getMethod(serializationID);
            if (method != null) {
                // A class that implements X10JavaSerializable.  Call _deserialize
                return (T)method.invoke(null, this);
            } else {
                Class<?> clazz = dict.getClassForID(serializationID);
                DeserializerThunk dt = DeserializerThunk.getDeserializerThunk(clazz);
                return dt.deserializeObject(clazz, this);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause; // don't wrap
            } else {
                throw new SerializationException(cause != null ? cause : e);
            }
        } catch (SerializationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e; // don't wrap
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }

    private Object deserializeArray() throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing a Java Array");
        }
        short componentTypeID = readSerializationId();
        int length = in.readInt();
        if (componentTypeID == INTEGER_ID) {
            int[] v = new int[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = in.readInt();
            }
            return v;
        } else if (componentTypeID == DOUBLE_ID) {
            double[] v = new double[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = in.readDouble();
            }
            return v;
        } else if (componentTypeID == FLOAT_ID) {
            float[] v = new float[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = in.readFloat();
            }
            return v;
        } else if (componentTypeID == BOOLEAN_ID) {
            boolean[] v = new boolean[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = in.readBoolean();
            }
            return v;
        } else if (componentTypeID == BYTE_ID) {
            byte[] v = new byte[length];
            record_reference(v);
            _readByteArray(length, v);
            return v;
        } else if (componentTypeID == SHORT_ID) {
            short[] v = new short[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = in.readShort();
            }
            return v;
        } else if (componentTypeID == LONG_ID) {
            long[] v = new long[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = in.readLong();
            }
            return v;
        } else if (componentTypeID == CHARACTER_ID) {
            char[] v = new char[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = in.readChar();
            }
            return v;
        } else if (componentTypeID == STRING_ID) {
            String[] v = new String[length];
            record_reference(v);
            for (int i = 0; i < length; i++) {
                v[i] = readString();
            }
            return v;
        } else {
            Class<?> componentType = getClassForID(componentTypeID);
            Object obj = Array.newInstance(componentType, length);
            record_reference(obj);
            for (int i = 0; i < length; ++i) {
                Array.set(obj, i, readObject());
            }
            return obj;
        }
    }

    private void _readByteArray(int length, byte[] v) throws IOException {
        int read = 0;
        while (read < length) {
            read += in.read(v, read, length-read);
        }
    }

    public int readInt() throws IOException {
        int v = in.readInt();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [****] an " + Runtime.ANSI_CYAN + "int" + Runtime.ANSI_RESET + ": " + v);
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

    public char readChar() throws IOException {
        char v = in.readChar();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [**] a " + Runtime.ANSI_CYAN + "char" + Runtime.ANSI_RESET + ": " + v);
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

    public short readShort() throws IOException {
        short v = in.readShort();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [**] a " + Runtime.ANSI_CYAN + "short" + Runtime.ANSI_RESET + ": " + v);
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

    public double readDouble() throws IOException {
        double v = in.readDouble();
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing [********] a " + Runtime.ANSI_CYAN + "double" + Runtime.ANSI_RESET + ": " + v);
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

    public String readString() throws IOException {
        short serializationID = readSerializationId();
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

    String readStringValue() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];
        _readByteArray(length, bytes);
        return new String(bytes);
    }

    // This method is called from generated code when an X10 class has a Java superclass.
    // It continues the deserialization of an objects fields starting from the argument class.
    public <T> Object deserializeFieldsStartingFromClass(Class<? extends Object> clazz, T obj, int i) throws IOException {
        try {
            DeserializerThunk thunk = DeserializerThunk.getDeserializerThunk(clazz);
            return thunk.deserializeObject(clazz, obj, i, this);
        } catch (SerializationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e; // don't wrap
        } catch (Throwable e) {
            throw new SerializationException(e);
        }
    }
    
    // Read an object using java serialization. 
    // This is used to optimize the serialization of primitive arrays
    public Object readUsingObjectInputStream() throws IOException {
        ObjectInputStream ois = new ObjectInputStream(this.in);
        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }
    }
    
    private Object deserializeSpecialType(short serializationID) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Deserializing primitive/special value with id " + serializationID);
        }
        Object obj = null;
        switch (serializationID) {
            case STRING_ID:
                obj = readStringValue();
                record_reference(obj); // Preserve reference identity of Strings
                break;
            case FLOAT_ID:
                obj = readFloat();
                break;
            case DOUBLE_ID:
                obj = readDouble();
                break;
            case INTEGER_ID:
                obj = readInt();
                break;
            case BOOLEAN_ID:
                obj = readBoolean();
                break;
            case BYTE_ID:
                obj = readByte();
                break;
            case SHORT_ID:
                obj = readShort();
                 break;
            case CHARACTER_ID:
                obj = readChar();
                break;
            case LONG_ID:
                obj = readLong();
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
                String msg = "Unhandled hard-wired serialization id in readPrimitive!";
                System.err.println(msg);
                throw new SerializationException(msg);    
        }
        return obj;
    }
    
    public static void raiseSerializationProtocolError() {
        Runtime.printTraceMessage("Protocol error in custom deserialization; raising exception");
        throw new SerializationException("CustomSerialization protocol error");
    }
}
