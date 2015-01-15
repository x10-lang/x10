/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import x10.core.GlobalRef;
import x10.core.Rail;
import x10.core.StructI;
import x10.io.SerializationException;
import x10.rtt.Types;
import x10.runtime.impl.java.Runtime;
import x10.serialization.SerializationDictionary.LocalSerializationDictionary;

public final class X10JavaSerializer implements SerializationConstants {
    
    
    static final boolean THROWABLES_SERIALIZE_MESSAGE = true;
    static final boolean THROWABLES_SERIALIZE_STACKTRACE = true;
    static final boolean THROWABLES_SERIALIZE_CAUSE = true;
        
    /**
     * The primary output stream for writing; use this instead of the backing streams
     * when writing data to the serializer!
     */
    protected DataOutputStream out;
    
    /*
     * Alternative backing output streams.  
     * Exactly one of these will be non-null.
     */
    private ByteArrayOutputStream b_out;
    private OutputStream o_out;
        
    // When a Object is serialized record its position
    // N.B. use custom IdentityHashMap class, as standard one has poor performance on J9
    X10IdentityHashMap<Object, Integer> objectMap = new X10IdentityHashMap<Object, Integer>();
    int counter = 0;
    
    // [GlobalGC] Table to remember serialized GlobalRefs, set and used in GlobalRef.java and InitDispatcher.java
    X10IdentityHashMap<GlobalRef<?>, Integer> grefMap = new X10IdentityHashMap<GlobalRef<?>, Integer>(); // for GlobalGC
    public void addToGrefMap(GlobalRef<?> gr, int weight) { grefMap.put(gr, weight); }
    public java.util.Map<GlobalRef<?>, Integer> getGrefMap() { return grefMap; }
    
    // per-message id dictionary
    protected LocalSerializationDictionary idDictionary;
    
    
    /*
     * For use in Managed X10 serialization code; parallels the make/init constructors used
     * from generated code
     */
    public X10JavaSerializer() {
        x10$serialization$X10JavaSerializer$$init$S();
    }
    public X10JavaSerializer(x10.io.OutputStreamWriter os) {
        x10$serialization$X10JavaSerializer$$init$S(os);
    }
    /*
     * for use by generated code in two-phase construction. 
     */
    public X10JavaSerializer(System[] ignored) {
        // for use by generated code; will be followed by call to $init in generated code
    }
    
    /*
     * Initialization code
     */
    public X10JavaSerializer x10$serialization$X10JavaSerializer$$init$S() {
        this.b_out = new ByteArrayOutputStream();
        initCommon(b_out);
        return this;
    }
    public X10JavaSerializer x10$serialization$X10JavaSerializer$$init$S(x10.io.OutputStreamWriter os) {
        this.o_out = os.getNativeOutputStream().stream;
        initCommon(o_out);
        return this;
    }
    public void initCommon(java.io.OutputStream os) {
        this.out = new DataOutputStream(os);
        this.idDictionary = new LocalSerializationDictionary(SharedDictionaries.getSerializationDictionary(), FIRST_DYNAMIC_ID);
    }

    
    public Rail<x10.core.Byte> toRail() {
        byte[] dataBytes = getDataBytes();
        return new Rail<x10.core.Byte>(Types.BYTE, dataBytes.length, dataBytes);
    }
    
    
    public DataOutput getOutForHadoop() {
        return out;
    }
    
    public int dataBytesWritten$O() {
        return dataBytesWritten();
    }
    public int dataBytesWritten() {
        return out.size();
    }
    
    public byte[] getDataBytes() {
        try {
            out.flush();
        } catch (IOException e) {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Suppressed IOException when flushing backing streams");
                if (Runtime.TRACE_SER_DETAIL) {
                    e.printStackTrace();
                }
            }
        }
        if (b_out == null) {
            throw new java.lang.UnsupportedOperationException("Cannot call getDataBytes() on Serializer that is backed with an OutputStreamWriter");
        } else {
            return b_out.toByteArray();
        }
    }
    
    public void newObjectGraph() {
        try {
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("RESETTING OBJECT GRAPH IDS");
            }
            out.writeShort(RESET_OBJECT_GRAPH_BOUNDARY_ID);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
        objectMap.clear();
        counter = 0;
    }
    
    public void addDeserializeCount(long extraCount) {
        // [GlobalGC] Adjust speculative increment of remoteCounts of GlobalRefs
        if (extraCount < 0L || extraCount > Integer.MAX_VALUE)
            throw new SerializationException("extraCount " + extraCount + " is out of range");
        x10.core.GlobalRef.adjustRemoteCountsInMap(getGrefMap(), (int)extraCount);
    }
    
    // Called from x10.io.Serializer.
    // The only goal of this wrapper message is to avoid a throws java.io.IOException
    // in the X10 API for Serializer.writeAny(v:Any).
    public void writeAny(Object v) {
        try {
            write(v);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
    }
        
    public short getSerializationId(Class<?> clazz, Object obj) throws IOException {
        return idDictionary.getSerializationId(clazz, obj, out);
    }
    
    public void writeNull() throws IOException {
        writeSerializationId(NULL_ID);
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a null reference");
        }
    }
    
    public void write(int i) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [****] an " + Runtime.ANSI_CYAN + "int" + Runtime.ANSI_RESET + ": " + i);
        }
        out.writeInt(i);
    }

    public void write(boolean b) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [*] a " + Runtime.ANSI_CYAN + "boolean" + Runtime.ANSI_RESET + ": " + b);
        }
        out.writeBoolean(b);
    }

    public void write(char c) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "char" + Runtime.ANSI_RESET + ": " + c);
        }
        out.writeChar(c);
    }

    public void write(byte b) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [*] a " + Runtime.ANSI_CYAN + "byte" + Runtime.ANSI_RESET + ": " + b);
        }
        out.writeByte(b);
    }

    public void write(short s) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "short" + Runtime.ANSI_RESET + ": " + s);
        }
        out.writeShort(s);
    }

    public void writeSerializationId(short sid) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [**] a " + Runtime.ANSI_CYAN + "serialization id" + Runtime.ANSI_RESET + ": " + sid);
        }
        out.writeShort(sid);
    }

    public void write(long l) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [********] a " + Runtime.ANSI_CYAN + "long" + Runtime.ANSI_RESET + ": " + l);
        }
        out.writeLong(l);
    }

    public void write(double d) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing [********] a " + Runtime.ANSI_CYAN + "double" + Runtime.ANSI_RESET + ": " + d);
        }
        out.writeDouble(d);
    }

    public void write(float f) throws IOException {
        if (Runtime.TRACE_SER) {           
            Runtime.printTraceMessage("Serializing [****] a " + Runtime.ANSI_CYAN + "float" + Runtime.ANSI_RESET + ": " + f);
        }
        out.writeFloat(f);
    }

    public void write(Object obj) throws IOException {
        if (obj == null) {
            writeNull();
            return;
        }

        Class<? extends Object> objClass = obj.getClass();
        if (objClass.isArray()) {
            Integer pos = previous_position(obj, true);
            if (pos != null) {
                return;
            }            
            serializeArray(obj);
            return;
        }

        short sid = getSerializationId(objClass, obj);
        if (sid <= MAX_HARDCODED_ID) {
            serializeSpecialType(sid, obj);
            return;
        }
        
        if (!(obj instanceof StructI)) {
            Integer pos = previous_position(obj, true);
            if (pos != null) {
                return;
            }
        }

        writeSerializationId(sid);
                
        if (obj instanceof X10JavaSerializable) {
             if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + Runtime.ANSI_BOLD + obj.getClass().getName() + Runtime.ANSI_RESET);
            }
            ((X10JavaSerializable)obj).$_serialize(this);
        } else {
            try {
                SerializerThunk st = SerializerThunk.getSerializerThunk(objClass);
                st.serializeObject(obj, objClass, this);
            } catch (SecurityException e) {
                e.printStackTrace();
                throw new SerializationException(e);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                throw new SerializationException(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new SerializationException(e);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new SerializationException(e);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new SerializationException(e);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                throw new SerializationException(e);
            }

        }
    }

    void writeStringValue(String str) throws IOException {
        write(str.length());
        out.write(str.getBytes());
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
                writeSerializationId(REPEATED_OBJECT_ID);
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


    // This method is called from generated code when an X10 class has a Java superclass
    // It continues the serialization process at the fields of clazz and goes up
    // the class hierarchy from there
    public <T> void serializeFieldsStartingFromClass(T obj, Class<T> clazz) throws IOException {
        try {
            SerializerThunk st = SerializerThunk.getSerializerThunk(clazz);
            st.serializeObject(obj, clazz, this);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }       
    }

    private void serializeArray(Object obj) throws IOException {
        if (Runtime.TRACE_SER) {
            Runtime.printTraceMessage("Serializing a Java Array");
        }
        writeSerializationId(JAVA_ARRAY_ID);
        Class<?> componentType = obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            if ("int".equals(componentType.getName())) {
                writeSerializationId(INTEGER_ID);
                int[] array = (int[])obj;
                out.writeInt(array.length);
                for (int elem : array) {
                    out.writeInt(elem);
                }
            } else if ("double".equals(componentType.getName())) {
                writeSerializationId(DOUBLE_ID);
                double[] array = (double[])obj;
                out.writeInt(array.length);
                for (double elem : array) {
                    out.writeDouble(elem);
                }
            } else if ("float".equals(componentType.getName())) {
                writeSerializationId(FLOAT_ID);
                float[] array = (float[])obj;
                out.writeInt(array.length);
                for (float elem : array) {
                    out.writeFloat(elem);
                }
            } else if ("boolean".equals(componentType.getName())) {
                writeSerializationId(BOOLEAN_ID);
                boolean[] array = (boolean[])obj;
                out.writeInt(array.length);
                for (boolean elem : array) {
                    out.writeBoolean(elem);
                }
            } else if ("byte".equals(componentType.getName())) {
                writeSerializationId(BYTE_ID);
                byte[] array = (byte[])obj;
                out.writeInt(array.length);
                for (byte elem : array) {
                    out.writeByte(elem);
                }
            } else if ("short".equals(componentType.getName())) {
                writeSerializationId(SHORT_ID);
                short[] array = (short[])obj;
                out.writeInt(array.length);
                for (short elem : array) {
                    out.writeShort(elem);
                }
            } else if ("long".equals(componentType.getName())) {
                writeSerializationId(LONG_ID);
                long[] array = (long[])obj;
                out.writeInt(array.length);
                for (long elem : array) {
                    out.writeLong(elem);
                }
            } else if ("char".equals(componentType.getName())) {
                writeSerializationId(CHARACTER_ID);
                char[] array = (char[])obj;
                out.writeInt(array.length);
                for (char elem : array) {
                    out.writeChar(elem);
                }
            }
        } else if ("java.lang.String".equals(componentType.getName())) {
            writeSerializationId(STRING_ID);
            String [] array = (String[])obj;
            out.writeInt(array.length);
            for (String elem : array) {
                write(elem);
            }
        } else {
            writeSerializationId(getSerializationId(componentType, null));
            int length = Array.getLength(obj);
            write(length);
            for (int i = 0; i < length; ++i) {
                Object o = Array.get(obj, i);
                write(o);
            }
        }
    }
    
    private void serializeSpecialType(short sid, Object obj) throws IOException {
        switch (sid) {
        case STRING_ID:
            // Preserve reference identity for Strings by looking for repeated objects
            Integer pos = previous_position(obj, true);
            if (pos != null) {
                return; 
            }
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "String" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(STRING_ID);
            writeStringValue((String)obj);
            break;
            
        case FLOAT_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing an " + Runtime.ANSI_CYAN + "Float" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(FLOAT_ID);
            out.writeFloat(((Float)obj).floatValue());
            break;
            
        case DOUBLE_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Double" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(DOUBLE_ID);
            out.writeDouble(((Double)obj).doubleValue());
            break;

        case INTEGER_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing an " + Runtime.ANSI_CYAN + "Integer" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(INTEGER_ID);
            out.writeInt(((Integer)obj).intValue());
            break;
            
        case BOOLEAN_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Boolean" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(BOOLEAN_ID);
            out.writeBoolean(((Boolean)obj).booleanValue());
            break;
            
        case BYTE_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Byte" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(BYTE_ID);
            out.writeByte(((Byte)obj).byteValue());
            break;
            
        case SHORT_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Short" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(SHORT_ID);
            out.writeShort(((Short)obj).shortValue());
            break;

        case LONG_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Long" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(LONG_ID);
            out.writeLong(((Long)obj).longValue());
            break;

        case CHARACTER_ID:
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Serializing a " + Runtime.ANSI_CYAN + "Character" + Runtime.ANSI_RESET + ": " + obj);
            }
            out.writeShort(CHARACTER_ID);
            out.writeChar(((Character)obj).charValue());
            break;

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
            if (Runtime.TRACE_SER) {
                Runtime.printTraceMessage("Optimized serializing [**] a " + Runtime.ANSI_CYAN + "serialization_id_t" + Runtime.ANSI_RESET + ": " + sid);
            }
            out.writeShort(sid);
            break;

        default:
            System.err.println("Unhandled hard-wired serialization id "+sid);
            throw new SerializationException("Unhandled hard-wired serialization id "+sid+" (class is "+obj.getClass()+")");    
        }            

    }

    // Write an object using java serialization. 
    // This is used by Rail to optimize the serialization of primitive arrays
    // and to allow optional forcing of usage of Java serialization for Java types.
    public void writeUsingObjectOutputStream(Object obj) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(this.out);
        oos.writeObject(obj);
    }
}
