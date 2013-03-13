/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

package x10.core;

import java.io.IOException;
import java.util.Arrays;

import x10.core.fun.VoidFun_0_0;
import x10.lang.LongRange;
import x10.lang.RailIterator;
import x10.rtt.NamedType;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.rtt.UnresolvedType;
import x10.serialization.SerializationConstants;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

@SuppressWarnings("rawtypes")
public final class Rail<T> extends x10.core.Ref implements x10.lang.Iterable, 
                                                           x10.core.fun.Fun_0_1,
                                                           X10JavaSerializable {
    
    // synthetic types for parameter mangling
    public static final class __0$1x10$lang$Rail$$T$2 { }
    public static final class __1$1x10$lang$Int$3x10$lang$Rail$$T$2 { }
    public static final class __1$1x10$lang$Long$3x10$lang$Rail$$T$2 { }
    public static final class __1x10$lang$Rail$$T { }

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public static final RuntimeType<Rail> $RTT = 
            NamedType.<Rail> make("x10.lang.Rail", /* base class */
                                  Rail.class,
                                  RuntimeType.INVARIANTS(1),
                                  new Type[] {
                                      ParameterizedType.make(x10.lang.Iterable.$RTT, UnresolvedType.PARAM(0)),
                                      ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, Types.INT, UnresolvedType.PARAM(0)),
                                      ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, Types.LONG, UnresolvedType.PARAM(0)) 
            });

    private Type T;
    
    public Type<?> $getParam(int i) { 
        return i == 0 ? T : null;
    }

    public RuntimeType<?> $getRTT() {
        return $RTT;
    }

    public long size;

    public Object value; // Will be a Java [] 
    
    public Object getBackingArray() { 
        return value;
    }
    public boolean[] getBooleanArray() {
        return (boolean[]) value;
    }
    public byte[] getByteArray() {
        return (byte[]) value;
    }
    public short[] getShortArray() {
        return (short[]) value;
    }
    public char[] getCharArray() {
        return (char[]) value;
    }
    public int[] getIntArray() {
        return (int[]) value;
    }
    public long[] getLongArray() {
        return (long[]) value;
    }
    public float[] getFloatArray() {
        return (float[]) value;
    }
    public double[] getDoubleArray() {
        return (double[]) value;
    }
    public Object[] getObjectArray() {
        return (Object[]) value;
    }

    /*
     * Constructors
     */
    
    // constructor just for allocation
    public Rail(java.lang.System[] $dummy, Type T) {
        this.T = T;
    }

    public Rail(Type T) {
        this.T = T;
        this.size = 0L;
        this.value = T.makeArray(0);
    }

    // For Java interop:  value is a Java[] of some form.
    public Rail(Type T, int size, Object value) {
        this.T = T;
        this.size = size;
        this.value = value;
    }

    public Rail(Type T, int size) {
        this.T = T;
        this.size = size;
        this.value = T.makeArray(size);
     }

    public Rail(Type T, int size, T init, __1x10$lang$Rail$$T $dummy) {
        this.T = T;
        this.size = size;
        this.value = T.makeArray(size);
        for (int i=0; i<size; i++) {
            T.setArray(this.value, i, init);
        }
    }

    public Rail(Type T, int size, x10.core.fun.Fun_0_1<x10.core.Int, T> init, __1$1x10$lang$Int$3x10$lang$Rail$$T$2 $dummy) {
        this.T = T;
        this.size = size;
        this.value = T.makeArray(size);

        for (int i=0; i<size; i++) {
            T v =  init.$apply(x10.core.Int.$box(i), Types.INT);
            T.setArray(this.value, i, v);
        }
    }

    public Rail(Type T, long size) {
        this(T, allocCheckSize(size));
    }

    public Rail(Type T, long size, T init, __1x10$lang$Rail$$T $dummy) {
        this(T, allocCheckSize(size), init, $dummy);
    }

    public Rail(Type T, long size, x10.core.fun.Fun_0_1<x10.core.Long, T> init, __1$1x10$lang$Long$3x10$lang$Rail$$T$2 $dummy) {
        this.T = T;
        this.size = size;
        this.value = T.makeArray(allocCheckSize(size));

        for (int i=0; i<(int)size; i++) {
            T v =  init.$apply(x10.core.Long.$box(i), Types.LONG);
            T.setArray(this.value, i, v);
        }
    }

    public Rail(Type T, x10.core.IndexedMemoryChunk<T> backingStore, __0$1x10$lang$Rail$$T$2 dummy) {
        this.T = T;
        this.size = backingStore.length;
        this.value = T.makeArray(allocCheckSize(size));
        System.arraycopy(backingStore.value, 0, value, 0, (int)this.size);
    }

    public Rail(Type T, Rail<T> src, __0$1x10$lang$Rail$$T$2 dummy) {
        this.T = T;
        this.size = src.size;
        this.value = T.makeArray(allocCheckSize(src.size));
        System.arraycopy(src.value, 0, value, 0, (int)this.size);
    }

    public static <T> Rail<T> makeUnsafe(Type T, long size, boolean allocateZeroed) {
        Rail<T> me = new Rail<T>(T, size);
        if (allocateZeroed && !Types.hasNaturalZero(T)) {
            Object zeroValue = Types.zeroValue(T);
            java.util.Arrays.fill((Object[])me.value, zeroValue);
        }
        return me;
    }

    private static int allocCheckSize(long size) {
        if (size >= (long)java.lang.Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Array size must be less than Integer.MAX_VALUE");
        }
        return (int)size;
    }

    /*
     * Serialization
     */

    public void $_serialize(X10JavaSerializer serializer) throws java.io.IOException {
        serializer.write(T);
        serializer.write(size);

        // If the T is a java primitive type, we use default java serialization here
        // cause its much faster than writing a single element at a time
        if (Types.isPrimitiveType(T)) {
            serializer.writeUsingObjectOutputStream(value);
        } else if (Types.isStringType(T)) {
            java.lang.String[] castValue = (java.lang.String[]) value;
            for (java.lang.String v : castValue) {
                serializer.write(v);
            }
        } else {
            Object[] castValue = (Object[]) value;
            for (Object v : castValue) {
                if (v instanceof X10JavaSerializable) {
                    serializer.write((X10JavaSerializable) v);
                } else {
                    serializer.write(v);
                }
            }
        }        
    }
    
    @SuppressWarnings("unchecked")
    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws java.io.IOException {
        Rail obj = new Rail((java.lang.System[]) null, (Type) null);
        deserializer.record_reference(obj);
        return $_deserialize_body(obj, deserializer);
    }
    
    public static <T> X10JavaSerializable $_deserialize_body(Rail<T> obj, 
                                                             X10JavaDeserializer deserializer) throws java.io.IOException {
        obj.T = deserializer.readRef();
        obj.size = deserializer.readLong();

        // If the T is a java primitive type, we use default java serialization here
        // cause its much faster than reading a single element at a time
        if (Types.isPrimitiveType(obj.T)) {
            obj.value = deserializer.readUsingObjectInputStream();
        } else if (Types.isStringType(obj.T)) {
            java.lang.String[] values = (java.lang.String[]) obj.T.makeArray(allocCheckSize(obj.size));
            for (int i = 0; i < (int)obj.size; i++) {
                values[i] = deserializer.readString();
            }
            obj.value = values;
        } else {
            Object[] values = (Object[]) obj.T.makeArray(allocCheckSize(obj.size));
            for (int i = 0; i < (int)obj.size; i++) {
                values[i] = deserializer.readRef();
            }
            obj.value = values;
        }
        return obj;
   }

   /*
    * Source-level instance methods
    */
   
    // dispatcher for method abstract public (Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(java.lang.Object a1, Type t1) {
        if (t1.equals(Types.LONG)) {
            return $apply$G(x10.core.Long.$unbox(a1));
        }
        if (t1.equals(Types.INT)) {
            return $apply$G(x10.core.Int.$unbox(a1));
        }
        throw new java.lang.Error("dispatch mechanism not completely implemented for contra-variant types.");
    }

    public T $apply$G(int index) {
        return (T) T.getArray(value, index);
    }

    public T $apply$G(long index) {
        return (T) T.getArray(value, (int)index);
    }

    public T $set__1x10$lang$Rail$$T$G(int index, T v) {
        T.setArray(value, index, v);
        return v;
    }

    public T $set__1x10$lang$Rail$$T$G(long index, T v) {
        T.setArray(value, (int)index, v);
        return v;
    }

    public void clear() {
        clear(0, size);
    }

    public void clear(long start, long numElems) {
        if (numElems <= 0)
            return;
        int begin = (int)start;
        int end = (int)(start + numElems);
        if (value instanceof boolean[]) {
            Arrays.fill(getBooleanArray(), begin, end, false);
        } else if (value instanceof byte[]) {
            Arrays.fill(getByteArray(), begin, end, (byte) 0);
        } else if (value instanceof char[]) {
            Arrays.fill(getCharArray(), begin, end, (char) 0);
        } else if (value instanceof short[]) {
            Arrays.fill(getShortArray(), begin, end, (short) 0);
        } else if (value instanceof int[]) {
            Arrays.fill(getIntArray(), begin, end, 0);
        } else if (value instanceof float[]) {
            Arrays.fill(getFloatArray(), begin, end, 0.0F);
        } else if (value instanceof long[]) {
            Arrays.fill(getLongArray(), begin, end, 0L);
        } else if (value instanceof double[]) {
            Arrays.fill(getDoubleArray(), begin, end, 0.0);
        } else {
            Object zeroValue = Types.zeroValue(T);
            Arrays.fill(getObjectArray(), begin, end, zeroValue);
        }
    }

    public void clear(int start, int numElems) {
        if (numElems <= 0)
            return;
        int begin = start;
        int end = start + numElems;
        if (value instanceof boolean[]) {
            Arrays.fill(getBooleanArray(), begin, end, false);
        } else if (value instanceof byte[]) {
            Arrays.fill(getByteArray(), begin, end, (byte) 0);
        } else if (value instanceof char[]) {
            Arrays.fill(getCharArray(), begin, end, (char) 0);
        } else if (value instanceof short[]) {
            Arrays.fill(getShortArray(), begin, end, (short) 0);
        } else if (value instanceof int[]) {
            Arrays.fill(getIntArray(), begin, end, 0);
        } else if (value instanceof float[]) {
            Arrays.fill(getFloatArray(), begin, end, 0.0F);
        } else if (value instanceof long[]) {
            Arrays.fill(getLongArray(), begin, end, 0L);
        } else if (value instanceof double[]) {
            Arrays.fill(getDoubleArray(), begin, end, 0.0);
        } else {
            Object zeroValue = Types.zeroValue(T);
            Arrays.fill(getObjectArray(), begin, end, zeroValue);
        }
    }

    public x10.lang.Iterator iterator() {
       return new RailIterator<T>(T, this, null);
    }
    
    public x10.lang.LongRange range() {
        return new LongRange(0, size-1);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int sz = size > 10 ? 10 : (int)size;
        for (int i = 0; i < sz; i++) {
            if (i > 0)
                sb.append(",");
            sb.append($apply$G(i));
        }
        if (sz < size)
            sb.append("...(omitted " + (size - sz) + " elements)");
        sb.append("]");
        return sb.toString();

    }
    
    /*
     * Static methods
     */

    public static <T> void copy__0$1x10$lang$Rail$$T$2__1$1x10$lang$Rail$$T$2(Type T,
                                                                              Rail<T> src,
                                                                              Rail<T> dst) {

        if (src.size != dst.size) {
            throw new java.lang.IllegalArgumentException("source and destination do not have equal sizes");
        }

        System.arraycopy(src.value, 0, dst.value, 0, (int)src.size);
    }

    public static <T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T,
                                                                              Rail<T> src,
                                                                              int srcIndex,
                                                                              Rail<T> dst,
                                                                              int dstIndex, 
                                                                              int numElems) {
        System.arraycopy(src.value, srcIndex, dst.value, dstIndex, numElems);
    }

    public static <T> void copy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T,
                                                                              Rail<T> src,
                                                                              long srcIndex,
                                                                              Rail<T> dst,
                                                                              long dstIndex, 
                                                                              long numElems) {
        System.arraycopy(src.value, (int)srcIndex, dst.value, (int)dstIndex, (int)numElems);
    }


    public static <T> void asyncCopy(Rail<T> src, final int srcIndex, final GlobalRef<Rail<T>> dst, final int dstIndex, final int numElems) {
        // synchronous version for the same place
        if (dst.home.id == x10.lang.Runtime.home().id) {
            System.arraycopy(src.value, srcIndex, dst.$apply$G().value, dstIndex, numElems);
            return;
        }

        // extra copy here simplifies logic and allows us to do this entirely at the Java level.
        // We'll eventually need to optimize this by writing custom native/JNI code instead of treating
        // it as just another async to execute remotely.
        final Object dataToCopy;
        if (numElems == src.size) {
            dataToCopy = src.getBackingArray();
        } else {
            dataToCopy = src.T.makeArray(numElems);
            System.arraycopy(src.getBackingArray(), srcIndex, dataToCopy, 0, numElems);
        }

        VoidFun_0_0 copyBody = new $Closure$0(dataToCopy, dst, dstIndex, numElems);

        x10.lang.Runtime.runAsync(dst.home, copyBody, null);
    }

    // static nested class version of copyBody
    public static class $Closure$0<T> extends x10.core.Ref implements VoidFun_0_0 {
        private static final long serialVersionUID = 1L;
        public Object srcData;
        public GlobalRef<Rail<T>> dst;
        public int dstIndex;
        public int numElems;

        // Just for allocation
        $Closure$0() {
        }
        $Closure$0(Object srcData, GlobalRef<Rail<T>> dst, int dstIndex, int numElems) {
            this.srcData = srcData;
            this.dst = dst;
            this.dstIndex = dstIndex;
            this.numElems = numElems;
        }
        public void $apply() {
            Object dstData = dst.$apply$G().getBackingArray();
            System.arraycopy(srcData, 0, dstData, dstIndex, numElems);
        }
        public static final RuntimeType<$Closure$0> $RTT = x10.rtt.StaticVoidFunType.<$Closure$0> make($Closure$0.class, new Type[] { VoidFun_0_0.$RTT });
        public RuntimeType<$Closure$0> $getRTT() {
            return $RTT;
        }
        public Type<?> $getParam(int i) {
            return null; // should return T if i = 0
        }

        //TODO Keith This is not compatible with C++ at the moment cause the java backend does not implement send_put
        public void $_serialize(X10JavaSerializer $serializer) throws IOException {
            $serializer.write(this.numElems);
            if (this.numElems > 0) {
                Class<?> componentType = this.srcData.getClass().getComponentType();
                if (componentType.isPrimitive()) {
                    $serializer.write(SerializationConstants.JAVA_OBJECT_STREAM_ID);
                    $serializer.writeUsingObjectOutputStream(this.srcData);
                } else if (componentType.equals(java.lang.String.class)) {
                    $serializer.write(SerializationConstants.STRING_ID);
                    $serializer.write((java.lang.String[]) this.srcData);
                } else if (this.srcData instanceof X10JavaSerializable[]) {
                    $serializer.write((X10JavaSerializable[]) this.srcData);
                } else {
                    $serializer.write((Object[]) this.srcData);
                }
            }
            $serializer.write(this.dst);
            $serializer.write(this.dstIndex);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
            $Closure$0 $_obj = new $Closure$0();
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$0 $_obj, X10JavaDeserializer $deserializer) throws IOException {
            $_obj.numElems = $deserializer.readInt();
            if ($_obj.numElems > 0) {
                short serializationID = $deserializer.readShort();
                if (serializationID == SerializationConstants.JAVA_OBJECT_STREAM_ID) {
                    $_obj.srcData = $deserializer.readUsingObjectInputStream();
                } else if (serializationID == SerializationConstants.STRING_ID) {
                    $_obj.srcData = $deserializer.readStringArray();
                } else {
                    $_obj.srcData = $deserializer.readRef();
                }
            }
            $_obj.dst = $deserializer.readRef();
            $_obj.dstIndex = $deserializer.readInt();
            return $_obj;
        }
    }

    public static <T> void asyncCopy(Rail<T> src, int srcIndex, GlobalRef<Rail<T>> dst, int dstIndex, int numElems, VoidFun_0_0 notifier) {
        // synchronous version for the same place
        if (dst.home.id == x10.lang.Runtime.home().id) {
            System.arraycopy(src.value, srcIndex, dst.$apply$G().value, dstIndex, numElems);
            notifier.$apply();
            return;
        }

        throw new java.lang.UnsupportedOperationException("asyncCopy with notifier not implemented for multivm");
        // notifier.$apply();
    }

    public static <T> void asyncCopy(final GlobalRef<Rail<T>> src, final int srcIndex, Rail<T> dst, final int dstIndex, final int numElems) {
        // synchronous version for the same place
        if (src.home.id == x10.lang.Runtime.home().id) {
            System.arraycopy(src.$apply$G().value, srcIndex, dst.value, dstIndex, numElems);
            return;
        }

        // A really bad implementation!  Leaks dst!!  Non-optimized copies! Extra distributed async/finish traffic!
        final GlobalRef<Rail<T>> dstWrapper = new GlobalRef<Rail<T>>(x10.rtt.ParameterizedType.make(Rail.$RTT, dst.T), dst, null);

        VoidFun_0_0 copyBody1 = new $Closure$1<T>(src, srcIndex, dstWrapper, dstIndex, numElems);

        x10.lang.Runtime.runAsync(src.home, copyBody1, null);
    }

    // static nested class version of copyBody1
    public static class $Closure$1<T> extends x10.core.Ref implements VoidFun_0_0 {
        private static final long serialVersionUID = 1L;
        public GlobalRef<Rail<T>> src;
        public int srcIndex;
        public GlobalRef<Rail<T>> dstWrapper;
        public int dstIndex;
        public int numElems;

        //Just for allocation
        $Closure$1() {
        }
        $Closure$1(GlobalRef<Rail<T>> src, int srcIndex, GlobalRef<Rail<T>> dstWrapper, int dstIndex, int numElems) {
            this.src = src;
            this.srcIndex = srcIndex;
            this.dstWrapper = dstWrapper;
            this.dstIndex = dstIndex;
            this.numElems = numElems;
        }
        public void $apply() {
            // This body runs at src's home.  It accesses the data for src and then does
            // another async back to dstWrapper's home to transfer the data.
            Rail<T> srcData = src.$apply$G();

            // extra copy here simplifies logic and allows us to do this entirely at the Java level.
            // We'll eventually need to optimize this by writing custom native/JNI code instead of treating
            // it as just another async to execute remotely.
            final Object dataToCopy;
            if (numElems == srcData.size) {
                dataToCopy = srcData.getBackingArray();
            } else {
                dataToCopy = src.$apply$G().T.makeArray(numElems);
                System.arraycopy(srcData.getBackingArray(), srcIndex, dataToCopy, 0, numElems);
            }

            // N.B. copyBody2 is same as copyBody 
            VoidFun_0_0 copyBody2 = new $Closure$0(dataToCopy, dstWrapper, dstIndex, numElems);

            x10.lang.Runtime.runAsync(dstWrapper.home, copyBody2, null);
        }
        public static final RuntimeType<$Closure$1<?>> $RTT = x10.rtt.StaticVoidFunType.<$Closure$1<?>> make($Closure$1.class, new Type[] { VoidFun_0_0.$RTT });
        public RuntimeType<$Closure$1<?>> $getRTT() {
            return $RTT;
        }
        public Type<?> $getParam(int i) {
            return null; // should return T if i = 0
        }

        //TODO Keith This is not compatible with C++ at the moment cause the java backend does not implement send_put
        public void $_serialize(X10JavaSerializer $serializer) throws IOException {
            $serializer.write(this.src);
            $serializer.write(this.srcIndex);
            $serializer.write(this.dstWrapper);
            $serializer.write(this.dstIndex);
            $serializer.write(this.numElems);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
            $Closure$1 $_obj = new $Closure$1();
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$1 $_obj, X10JavaDeserializer $deserializer) throws IOException {
            $_obj.src = $deserializer.readRef();
            $_obj.srcIndex = $deserializer.readInt();
            $_obj.dstWrapper = $deserializer.readRef();
            $_obj.dstIndex = $deserializer.readInt();
            $_obj.numElems = $deserializer.readInt();
            return $_obj;
        }
    }

    public static <T> void asyncCopy(GlobalRef<Rail<T>> src, int srcIndex, Rail<T> dst, int dstIndex, int numElems, VoidFun_0_0 notifier) {
        // synchronous version for the same place
        if (src.home.id == x10.lang.Runtime.home().id) {
            System.arraycopy(src.$apply$G().value, srcIndex, dst.value, dstIndex, numElems);
            notifier.$apply();
            return;
        }

        throw new java.lang.UnsupportedOperationException("asyncCopy with notifier not implemented for multivm");
        // notifier.$apply();
    }
}
