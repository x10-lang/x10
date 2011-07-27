/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core;

import java.io.IOException;
import java.util.Arrays;

import x10.core.fun.VoidFun_0_0;
import x10.lang.Place;
import x10.lang.UnsupportedOperationException;
import x10.rtt.BooleanType;
import x10.rtt.ByteType;
import x10.rtt.CharType;
import x10.rtt.DoubleType;
import x10.rtt.FloatType;
import x10.rtt.IntType;
import x10.rtt.LongType;
import x10.rtt.NamedType;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;
import x10.rtt.ShortType;
import x10.rtt.StringType;
import x10.rtt.Type;
import x10.x10rt.DeserializationDispatcher;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

public final class IndexedMemoryChunk<T> extends x10.core.Struct implements X10JavaSerializable {

	private static final long serialVersionUID = 1L;
	private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(IndexedMemoryChunk.class, "x10.util.IndexedMemoryChunk");

    public int length;
    public Object value;
    public Type<T> type;

    // constructor just for allocation
    public IndexedMemoryChunk(java.lang.System[] $dummy) {
        super($dummy);
    }

    public IndexedMemoryChunk<T> $init(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.value = value;
        return this;
    }

    public IndexedMemoryChunk(Type<T> type, int length, Object value) {
        this.length = length;
        this.type = type;
        this.value = value;
    }

    public IndexedMemoryChunk<T> $init(Type<T> type) {
        this.$init(type, 0, null);
        return this;
    }
    
    public IndexedMemoryChunk(Type<T> type) {
        this(type, 0, null);
    }

    // zero value constructor
    public IndexedMemoryChunk(Type<T> type, java.lang.System $dummy) {
        this(type);
    }

    private IndexedMemoryChunk(Type<T> type, int length, boolean zeroed) {
        this(type, length, type.makeArray(length));
        if (zeroed) {
            if (!x10.rtt.Types.hasNaturalZero(type)) {
                Object zeroValue = x10.rtt.Types.zeroValue(type);
                java.util.Arrays.fill((Object[]) value, zeroValue);
            }
        }
    }

    public static <T> IndexedMemoryChunk<T> allocate(Type<T> type, long length, boolean zeroed) {
        if (length > Integer.MAX_VALUE) {
            // TODO
            throw new x10.lang.OutOfMemoryError("Array length must be shorter than 2^31");
        }
        return new IndexedMemoryChunk<T>(type, (int) length, zeroed);
    }

    public static <T> IndexedMemoryChunk<T> allocate(Type<T> type, int length, boolean zeroed) {
        return new IndexedMemoryChunk<T>(type, length, zeroed);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IndexedMemoryChunk(");
        int sz = Math.min(length, 10);
        for (int i = 0; i < sz; i++) {
            if (i > 0)
                sb.append(",");
            sb.append($apply$G(i));
        }
        if (sz < length) sb.append("...(omitted " + (length - sz) + " elements)");
        sb.append(")");
        return sb.toString();
    }

    public T $apply$G(int i) {
        return type.getArray(value, i);
    }

    public void $set(int i, T v) {
        type.setArray(value, i, v);
    }

    public void set_unsafe(T v, int i) {
        $set(i, v);
    }

    public void clear(int start, int numElems) {
        if (numElems <= 0) return;
        if (value instanceof boolean[]) {
            Arrays.fill(getBooleanArray(), start, start+numElems, false);
        } else if (value instanceof byte[]) {
            Arrays.fill(getByteArray(), start, start+numElems, (byte)0);                 
        } else if (value instanceof char[]) {
            Arrays.fill(getCharArray(), start, start+numElems, (char)0);               
        } else if (value instanceof short[]) {
            Arrays.fill(getShortArray(), start, start+numElems, (short)0);
        } else if (value instanceof int[]) {
            Arrays.fill(getIntArray(), start, start+numElems, 0);
        } else if (value instanceof float[]) {
            Arrays.fill(getFloatArray(), start, start+numElems, 0.0F);
        } else if (value instanceof long[]) {
            Arrays.fill(getLongArray(), start, start+numElems, 0L);
        } else if (value instanceof double[]) {
            Arrays.fill(getDoubleArray(), start, start+numElems, 0.0);
        } else {
            Object zeroValue = x10.rtt.Types.zeroValue(type);
            Arrays.fill(getObjectArray(), start, start+numElems, zeroValue);
        }
    }

    public void deallocate() {
        value = null;
        length = 0;
    }
    
    public static <T> void asyncCopy(IndexedMemoryChunk<T> src, final int srcIndex, 
                                     final RemoteIndexedMemoryChunk<T> dst, final int dstIndex,
                                     final int numElems) {
        // extra copy here simplifies logic and allows us to do this entirely at the Java level.
        // We'll eventually need to optimize this by writing custom native/JNI code instead of treating
        // it as just another async to execute remotely.
        final Object dataToCopy;
        if (numElems == src.length) {
            dataToCopy = src.getBackingArray();
        } else {
            dataToCopy = allocate(src.type, numElems, false).getBackingArray();
            System.arraycopy(src.value, srcIndex, dataToCopy, 0, numElems);
        }
        
        /*
        // TODO translate copyBody to a static nested class
        VoidFun_0_0 copyBody = new VoidFun_0_0() {
            private static final long serialVersionUID = 1L;
            int dstId = dst.id;
            Object srcData = dataToCopy;
            
            public RuntimeType<?> $getRTT() { return VoidFun_0_0.$RTT; }
            public Type<?> $getParam(int i) { return null; }
            
            public void $apply() {
                Object dstData = RemoteIndexedMemoryChunk.getValue(dstId);
                System.arraycopy(srcData, 0, dstData, dstIndex, numElems);
            }
        };
        */
        VoidFun_0_0 copyBody = new $Closure$0(dataToCopy, dst.id, dstIndex, numElems);
        
        x10.lang.Runtime.runAsync(dst.home, copyBody);
    }
    
    // static nested class version of copyBody
    public static class $Closure$0 extends x10.core.Ref implements VoidFun_0_0 {
        private static final long serialVersionUID = 1L;
        private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher($Closure$0.class);
        public Object srcData;
        public int dstId;
        public int dstIndex;
        public int numElems;

        // Just for allocation
        $Closure$0() {
        }
        $Closure$0(Object srcData, int dstId, int dstIndex, int numElems) {
        	this.srcData = srcData;
        	this.dstId = dstId;
        	this.dstIndex = dstIndex;
        	this.numElems = numElems;
        }
        public void $apply() {
            Object dstData = RemoteIndexedMemoryChunk.getValue(dstId);
            System.arraycopy(srcData, 0, dstData, dstIndex, numElems);
        }
        public static final RuntimeType<$Closure$0> $RTT =
        	new x10.rtt.StaticVoidFunType<$Closure$0>($Closure$0.class, new Type[] { VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT });
        public RuntimeType<$Closure$0> $getRTT() { return $RTT; }

        //TODO Keith This is not compatible with C++ at the moment cause the java backend does not implement send_put
        public void $_serialize(X10JavaSerializer serializer) throws IOException {
            serializer.write(this.numElems);
            if (this.numElems > 0) {
                if (this.srcData instanceof int[]) {
                    serializer.write(DeserializationDispatcher.INTEGER_ID);
                    serializer.write((int[]) this.srcData);
                } else if (this.srcData instanceof double[]) {
                    serializer.write(DeserializationDispatcher.DOUBLE_ID);
                    serializer.write((double[]) this.srcData);
                } else if (this.srcData instanceof float[]) {
                    serializer.write(DeserializationDispatcher.FLOAT_ID);
                    serializer.write((float[]) this.srcData);
                } else if (this.srcData instanceof short[]) {
                    serializer.write(DeserializationDispatcher.SHORT_ID);
                    serializer.write((short[]) this.srcData);
                } else if (this.srcData instanceof char[]) {
                    serializer.write(DeserializationDispatcher.CHARACTER_ID);
                    serializer.write((char[]) this.srcData);
                } else if (this.srcData instanceof byte[]) {
                    serializer.write(DeserializationDispatcher.BYTE_ID);
                    serializer.write((byte[]) this.srcData);
                } else if (this.srcData instanceof String[]) {
                    serializer.write(DeserializationDispatcher.STRING_ID);
                    serializer.write((String[]) this.srcData);
                } else if (this.srcData instanceof long[]) {
                    serializer.write(DeserializationDispatcher.LONG_ID);
                    serializer.write((long[]) this.srcData);
                } else if (this.srcData instanceof boolean[]) {
                    serializer.write(DeserializationDispatcher.BOOLEAN_ID);
                    serializer.write((boolean[]) this.srcData);
                } else {
                    serializer.write((X10JavaSerializable[]) this.srcData);
                }
            }
            serializer.write(this.dstId);
            serializer.write(this.dstIndex);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
            $Closure$0 closure$0 = new $Closure$0();
            deserializer.record_reference(closure$0);
            return $_deserialize_body(closure$0, deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$0 closure$0, X10JavaDeserializer deserializer) throws IOException {
            Object srcData = null;
            int numElems = deserializer.readInt();
            if (numElems > 0) {
                int type = deserializer.readInt();
                if (type == DeserializationDispatcher.INTEGER_ID) {
                    srcData = deserializer.readIntArray();
                } else if (type == DeserializationDispatcher.DOUBLE_ID) {
                    srcData = deserializer.readDoubleArray();
                } else if (type == DeserializationDispatcher.FLOAT_ID) {
                    srcData = deserializer.readFloatArray();
                } else if (type == DeserializationDispatcher.SHORT_ID) {
                    srcData = deserializer.readShortArray();
                } else if (type == DeserializationDispatcher.CHARACTER_ID) {
                    srcData = deserializer.readCharArray();
                } else if (type == DeserializationDispatcher.BYTE_ID) {
                    srcData = deserializer.readByteArray();
                } else if (type == DeserializationDispatcher.STRING_ID) {
                    srcData = deserializer.readStringArray();
                } else if (type == DeserializationDispatcher.LONG_ID) {
                    srcData = deserializer.readLongArray();
                } else if (type == DeserializationDispatcher.BOOLEAN_ID) {
                    srcData = deserializer.readBooleanArray();
                } else  {
                    srcData = deserializer.readRef();
                }
            }
            int dstId = deserializer.readInt();
            int dstIndex = deserializer.readInt();
            closure$0.srcData = srcData;
            closure$0.dstId = dstId;
            closure$0.dstIndex = dstIndex;
            closure$0.numElems = numElems;
            return (X10JavaSerializable) closure$0;
        }

        public int $_get_serialization_id() {
            return _serialization_id;
        }
    }

    public static <T> void asyncCopy(IndexedMemoryChunk<T> src, int srcIndex, 
                                     RemoteIndexedMemoryChunk<T> dst, int dstIndex,
                                     int numElems, VoidFun_0_0 notifier) {
        throw new UnsupportedOperationException("asyncCopy with notifier not implemented for multivm");
        // notifier.$apply();
    }

    public static <T> void asyncCopy(final RemoteIndexedMemoryChunk<T> src, final int srcIndex, 
                                     IndexedMemoryChunk<T> dst, final int dstIndex,
                                     final int numElems) {
        // A really bad implementation!  Leaks dst!!  Non-optimized copies! Extra distributed async/finish traffic!
        final RemoteIndexedMemoryChunk<T> dstWrapper = RemoteIndexedMemoryChunk.wrap(dst);
        
        /*
        // TODO translate copyBody1 to a static nested class
        final int srcId = src.id;
        VoidFun_0_0 copyBody1 = new VoidFun_0_0() {
            private static final long serialVersionUID = 1L;
            public RuntimeType<?> $getRTT() { return VoidFun_0_0.$RTT; }
            public Type<?> $getParam(int i) { return null; }
            
            public void $apply() {
                // This body runs at src's home.  It accesses the data for src and then does
                // another async back to dstWrapper's home to transfer the data.
                Object srcData = RemoteIndexedMemoryChunk.getValue(srcId);
                 
                // extra copy here simplifies logic and allows us to do this entirely at the Java level.
                // We'll eventually need to optimize this by writing custom native/JNI code instead of treating
                // it as just another async to execute remotely.
                final Object dataToCopy;
                if (numElems == src.length) {
                    dataToCopy = srcData;
                } else {
                    dataToCopy = allocate(src.type, numElems, false).getBackingArray();
                    System.arraycopy(srcData, srcIndex, dataToCopy, 0, numElems);
                }
                
                // TODO translate this to a static nested class
                VoidFun_0_0 copyBody2 = new VoidFun_0_0() {
                    private static final long serialVersionUID = 1L;
                    int dstId = dstWrapper.id;
                    Object srcData = dataToCopy;
                    
                    public RuntimeType<?> $getRTT() { return VoidFun_0_0.$RTT; }
                    public Type<?> $getParam(int i) { return null; }
                    
                    public void $apply() {
                        // This body runs back at dst's home.  It does the actual assignment of values.
                        Object dstData = RemoteIndexedMemoryChunk.getValue(dstId);
                        System.arraycopy(srcData, 0, dstData, dstIndex, numElems);
                    }
                };
                
                x10.lang.Runtime.runAsync(dstWrapper.home, copyBody2);
            }
        };
        */
        VoidFun_0_0 copyBody1 = new $Closure$1<T>(src, srcIndex, dstWrapper, dstIndex, numElems);

        x10.lang.Runtime.runAsync(src.home, copyBody1);
    }
    
    // static nested class version of copyBody1
    public static class $Closure$1<T> extends x10.core.Ref implements VoidFun_0_0 {
        private static final long serialVersionUID = 1L;
        private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher($Closure$1.class);
        public int srcId;
        public int srcLength;
        public Type<T> srcType;
        public int srcIndex;
        public int dstWrapperId;
        public Place dstWrapperHome;
        public int dstIndex;
        public int numElems;

        //Just for allocation
        $Closure$1() {
        }
        $Closure$1(RemoteIndexedMemoryChunk<T> src, int srcIndex, RemoteIndexedMemoryChunk<T> dstWrapper, int dstIndex, int numElems) {
        	this.srcId = src.id;
        	this.srcLength = src.length;
        	this.srcType = src.type;
        	this.srcIndex = srcIndex;
        	this.dstWrapperId = dstWrapper.id;
        	this.dstWrapperHome = dstWrapper.home;
        	this.dstIndex = dstIndex;
        	this.numElems = numElems;
        }
        public void $apply() {
            // This body runs at src's home.  It accesses the data for src and then does
            // another async back to dstWrapper's home to transfer the data.
            Object srcData = RemoteIndexedMemoryChunk.getValue(srcId);
             
            // extra copy here simplifies logic and allows us to do this entirely at the Java level.
            // We'll eventually need to optimize this by writing custom native/JNI code instead of treating
            // it as just another async to execute remotely.
            final Object dataToCopy;
            if (numElems == srcLength) {
                dataToCopy = srcData;
            } else {
                dataToCopy = allocate(srcType, numElems, false).getBackingArray();
                System.arraycopy(srcData, srcIndex, dataToCopy, 0, numElems);
            }
            
            // N.B. copyBody2 is same as copyBody 
            VoidFun_0_0 copyBody2 = new $Closure$0(dataToCopy, dstWrapperId, dstIndex, numElems);

            x10.lang.Runtime.runAsync(dstWrapperHome, copyBody2);
        }
        public static final RuntimeType<$Closure$1<?>> $RTT =
        	new x10.rtt.StaticVoidFunType<$Closure$1<?>>($Closure$1.class, new Type[] { VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT });
        public RuntimeType<$Closure$1<?>> $getRTT() { return $RTT; }

        //TODO Keith This is not compatible with C++ at the moment cause the java backend does not implement send_put
        public void $_serialize(X10JavaSerializer serializer) throws IOException {
            serializer.write(this.srcId);
            serializer.write(this.srcLength);
            serializer.write(this.srcType);
            serializer.write(this.srcIndex);
            serializer.write(this.dstWrapperId);
            serializer.write(this.dstWrapperHome);
            serializer.write(this.dstIndex);
            serializer.write(this.numElems);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
            $Closure$1 closure$1 = new $Closure$1();
            deserializer.record_reference(closure$1);
            return $_deserialize_body(closure$1, deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$1 closure$1, X10JavaDeserializer deserializer) throws IOException {
            int srcId = deserializer.readInt();
            int srcLength = deserializer.readInt();
            Type srcType = (Type) deserializer.readRef();
            int srcIndex = deserializer.readInt();
            int dstWrapperId = deserializer.readInt();
            Place dstWrapperHome = (Place) deserializer.readRef();
            int dstIndex = deserializer.readInt();
            int numElems = deserializer.readInt();
            closure$1.srcId = srcId;
        	closure$1.srcLength = srcLength;
        	closure$1.srcType = srcType;
        	closure$1.srcIndex = srcIndex;
        	closure$1.dstWrapperId = dstWrapperId;
        	closure$1.dstWrapperHome = dstWrapperHome;
        	closure$1.dstIndex = dstIndex;
        	closure$1.numElems = numElems;
            return (X10JavaSerializable) closure$1;
        }

        public int $_get_serialization_id() {
            return _serialization_id;
        }
    }

    public static <T> void asyncCopy(RemoteIndexedMemoryChunk<T> src, int srcIndex, 
                                     IndexedMemoryChunk<T> dst, int dstIndex,
                                     int numElems, VoidFun_0_0 notifier) {
        throw new UnsupportedOperationException("asyncCopy with notifier not implemented for multivm");
        // notifier.$apply();
    }

    public static <T> void copy(IndexedMemoryChunk<T> src, int srcIndex, 
                                IndexedMemoryChunk<T> dst, int dstIndex,
                                int numElems) {
        System.arraycopy(src.value, srcIndex, dst.value, dstIndex, numElems);
    }

    public boolean _struct_equals$O(Object o) {
        return o != null && this.value == ((IndexedMemoryChunk<?>) o).value;
    }

    // TODO implement remote operations
    public RemoteIndexedMemoryChunk<T> getCongruentSibling(x10.lang.Place p) {
    	ThrowableUtilities.UnsupportedOperationException("Remote operations are not implemented.");
    	return null;
    }

    public static final RuntimeType<IndexedMemoryChunk<?>> $RTT = new NamedType<IndexedMemoryChunk<?>>(
        "x10.util.IndexedMemoryChunk",
        IndexedMemoryChunk.class,
        new RuntimeType.Variance[] { Variance.INVARIANT },
        new Type[] { x10.rtt.Types.STRUCT }
    );
    
    @Override
    public RuntimeType<IndexedMemoryChunk<?>> $getRTT() {
        return $RTT;
    }

    @Override
    public Type<?> $getParam(int i) {
        return i == 0 ? type : null;
    }


    // Methods to get the backing array.   May be called by generated code.
    public Object getBackingArray() { return value; }

    public boolean[] getBooleanArray() { return (boolean[]) value; }
    public byte[] getByteArray() { return (byte[]) value; }
    public short[] getShortArray() { return (short[]) value; }
    public char[] getCharArray() { return (char[]) value; }
    public int[] getIntArray() { return (int[]) value; }
    public long[] getLongArray() { return (long[]) value; }
    public float[] getFloatArray() { return (float[]) value; }
    public double[] getDoubleArray() { return (double[]) value; }
    public Object[] getObjectArray() { return (Object[]) value; }

	public void $_serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write(length);
        serializer.write(type);
        if (type instanceof FloatType) {
            float[] castValue = (float[]) value;
            for (float v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof IntType) {
            int[] castValue = (int[]) value;
            for (int v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof ByteType) {
            byte[] castValue = (byte[]) value;
            for (byte v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof DoubleType) {
            double[] castValue = (double[]) value;
            for (double v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof LongType) {
            long[] castValue = (long[]) value;
            for (long v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof CharType) {
            char[] castValue = (char[]) value;
            for (char v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof ShortType) {
            short[] castValue = (short[]) value;
            for (short v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof BooleanType) {
            boolean [] castValue = (boolean[]) value;
            for (boolean v : castValue) {
                serializer.write(v);
            }
        } else if (type instanceof StringType) {
            java.lang.String [] castValue = (java.lang.String[]) value;
            for (java.lang.String v : castValue) {
                serializer.write(v);
            }
        } else {
            Object [] castValue = (Object[]) value;
            for (Object v : castValue) {
                serializer.write((X10JavaSerializable)v);
            }
        }
	}

	public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        IndexedMemoryChunk imc = new IndexedMemoryChunk((java.lang.System[]) null);
        deserializer.record_reference(imc);
        return _deSerialize_body(imc, deserializer);
	}

	public int $_get_serialization_id() {
		return _serialization_id;
	}

    public static X10JavaSerializable _deSerialize_body(IndexedMemoryChunk imc, X10JavaDeserializer deserializer) throws IOException {
        int length = deserializer.readInt();
        imc.length = length;
        imc.type = (Type) deserializer.readRef();

        if (imc.type instanceof FloatType) {
            float[] values = (float[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readFloat();
            }
            imc.value = values;
        } else if (imc.type instanceof IntType) {
            int[] values = (int[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readInt();
            }
            imc.value = values;
        } else if (imc.type instanceof BooleanType) {
            boolean[] values = (boolean[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readBoolean();
            }
            imc.value = values;
        } else if (imc.type instanceof CharType) {
            char[] values = (char[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readChar();
            }
            imc.value = values;
        } else if (imc.type instanceof DoubleType) {
            double[] values = (double[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readDouble();
            }
            imc.value = values;
        }  else if (imc.type instanceof ShortType) {
            short[] values = (short[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readShort();
            }
            imc.value = values;
        } else if (imc.type instanceof LongType) {
            long[] values = (long[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readLong();
            }
            imc.value = values;
        } else if (imc.type instanceof ByteType) {
            byte[] values = (byte[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readByte();
            }
            imc.value = values;
        } else if (imc.type instanceof StringType) {
            java.lang.String[] values = (java.lang.String[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                values[i] = deserializer.readString();
            }
            imc.value = values;
        } else  {
            Object[] values = (Object[]) imc.type.makeArray(length);
            for (int i = 0; i < length; i++) {
                   values[i] = deserializer.readRef();
            }
            imc.value = values;
        }
        return imc;
    }

    // this is broken
    /*
    public Object[] getBoxedArray() {
        if (value instanceof boolean[]) {
            boolean[] a = (boolean[]) value;
            Boolean[] b = new Boolean[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof byte[]) {
            byte[] a = (byte[]) value;
            Byte[] b = new Byte[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof char[]) {
            char[] a = (char[]) value;
            Character[] b = new Character[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof short[]) {
            short[] a = (short[]) value;
            Short[] b = new Short[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof int[]) {
            int[] a = (int[]) value;
            Integer[] b = new Integer[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof long[]) {
            long[] a = (long[]) value;
            Long[] b = new Long[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof float[]) {
            float[] a = (float[]) value;
            Float[] b = new Float[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        if (value instanceof double[]) {
            double[] a = (double[]) value;
            Double[] b = new Double[a.length];
            for (int i = 0; i < a.length; i++) b[i] = a[i];
        }
        return (Object[]) value;
    }
    */

}
