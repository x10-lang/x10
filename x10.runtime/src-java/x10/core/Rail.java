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

package x10.core;

import java.io.IOException;
import java.util.Arrays;

import x10.core.fun.Fun_0_1;
import x10.core.fun.VoidFun_0_0;
import x10.lang.LongRange;
import x10.lang.RailIterator;
import x10.lang.GlobalRail;
import x10.rtt.NamedType;
import x10.rtt.ParameterizedType;
import x10.rtt.RuntimeType;
import x10.rtt.StaticVoidFunType;
import x10.rtt.Type;
import x10.rtt.Types;
import x10.rtt.UnresolvedType;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

@SuppressWarnings("rawtypes")
public final class Rail<T> extends Ref implements x10.lang.Iterable, 
                                                           Fun_0_1,
                                                           X10JavaSerializable {
    
    // synthetic types for parameter mangling
    public static final class __0$1x10$lang$Rail$$T$2 { }
    public static final class __1$1x10$lang$Long$3x10$lang$Rail$$T$2 { }
    public static final class __1x10$lang$Rail$$T { }

    @SuppressWarnings("unchecked")
    public static final RuntimeType<Rail> $RTT = 
            NamedType.<Rail> make("x10.lang.Rail", Rail.class, 1,
                                  new Type[] {
                                      ParameterizedType.make(x10.lang.Iterable.$RTT, UnresolvedType.PARAM(0)),
                                      ParameterizedType.make(Fun_0_1.$RTT, Types.LONG, UnresolvedType.PARAM(0)) 
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
    @SuppressWarnings("unchecked")
    public T[] getGenericArray() {
        return (T[]) value;
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

    public Rail(Type T, long size) {
        this.T = T;
        this.size = size;
        this.value = T.makeArray(allocCheckSize(size));
        if (!Types.hasNaturalZero(T)) {
            Object zeroValue = Types.zeroValue(T);
            java.util.Arrays.fill((Object[])value, zeroValue);
        }
    }

    public Rail(Type T, long size, T init, __1x10$lang$Rail$$T $dummy) {
        this.T = T;
        this.size = size;
        this.value = T.makeArray(allocCheckSize(size));
        fillHelper(init);
    }

    public Rail(Type T, long size, Fun_0_1<Long, T> init, __1$1x10$lang$Long$3x10$lang$Rail$$T$2 $dummy) {
        this.T = T;
        this.size = size;
        this.value = T.makeArray(allocCheckSize(size));

        for (int i=0; i<(int)size; i++) {
            T v =  init.$apply(Long.$box(i), Types.LONG);
            T.setArray(this.value, i, v);
        }
    }

    public Rail(Type T, long size, x10.xrx.Runtime.MemoryAllocator alloc) {
        this(T, size);
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
            throw new IllegalArgumentException("Rail size must be less than Integer.MAX_VALUE");
        }
        return (int)size;
    }

    /*
     * Serialization
     */

    private Object writeReplace() throws java.io.ObjectStreamException {
        return new x10.serialization.SerializationProxy(this);
    }

    public void $_serialize(X10JavaSerializer serializer) throws java.io.IOException {
        serializer.write(T);
        serializer.write(size);

        // If the T is a java primitive type, we use default java serialization here
        // cause its much faster than writing a single element at a time
        if (Types.isPrimitiveType(T)) {
            serializer.writeUsingObjectOutputStream(value);
        } else if (Types.isStringType(T)) {
            String[] castValue = (String[]) value;
            for (String v : castValue) {
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
        obj.T = deserializer.readObject();
        obj.size = deserializer.readLong();

        // If the T is a java primitive type, we use default java serialization here
        // cause its much faster than reading a single element at a time
        if (Types.isPrimitiveType(obj.T)) {
            obj.value = deserializer.readUsingObjectInputStream(false);
        } else if (Types.isStringType(obj.T)) {
            String[] values = (String[]) obj.T.makeArray(allocCheckSize(obj.size));
            for (int i = 0; i < (int)obj.size; i++) {
                values[i] = deserializer.readString();
            }
            obj.value = values;
        } else {
            Object[] values = (Object[]) obj.T.makeArray(allocCheckSize(obj.size));
            for (int i = 0; i < (int)obj.size; i++) {
                values[i] = deserializer.readObject();
            }
            obj.value = values;
        }
        return obj;
   }

   /*
    * Source-level instance methods
    */
   
    // dispatcher for method abstract public (Z1)=>U.operator()(a1:Z1){}:U
    public Object $apply(Object a1, Type t1) {
        return $apply$G(Long.$unbox(a1));
    }

    public T $apply$G(long index) {
        return (T) T.getArray(value, (int)index);
    }

    public T $set__1x10$lang$Rail$$T$G(long index, T v) {
        T.setArray(value, (int)index, v);
        return v;
    }
    public void fill__0x10$lang$Rail$$T(T v) {
        fillHelper(v);
    }

    private void fillHelper(T v) {
        if (value instanceof boolean[]) {
            Arrays.fill(getBooleanArray(), Boolean.$unbox(v));
        } else if (value instanceof byte[]) {
            // T is either Byte or UByte
            Arrays.fill(getByteArray(), ((java.lang.Number)v).byteValue());
        } else if (value instanceof char[]) {
            Arrays.fill(getCharArray(), Char.$unbox(v));
        } else if (value instanceof short[]) {
            // T is either Short or UShort
            Arrays.fill(getShortArray(), ((java.lang.Number)v).shortValue());
        } else if (value instanceof int[]) {
            // T is either Int or UInt
            Arrays.fill(getIntArray(), ((java.lang.Number)v).intValue());
        } else if (value instanceof float[]) {
            Arrays.fill(getFloatArray(), Float.$unbox(v));
        } else if (value instanceof long[]) {
            // T is either Long or ULong
            Arrays.fill(getLongArray(), ((java.lang.Number)v).longValue());
        } else if (value instanceof double[]) {
            Arrays.fill(getDoubleArray(), Double.$unbox(v));
        } else {
            Arrays.fill(getObjectArray(), v);
        }
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
                                                                              long srcIndex,
                                                                              Rail<T> dst,
                                                                              long dstIndex, 
                                                                              long numElems) {
        System.arraycopy(src.value, (int)srcIndex, dst.value, (int)dstIndex, (int)numElems);
    }


    public static <T> void asyncCopy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T, Rail<T> src, final long srcIndexL, final GlobalRail<T> dst, final long dstIndexL, final long numElemsL) {
        int srcIndex = (int)srcIndexL;
        int dstIndex = (int)dstIndexL;
        int numElems = (int)numElemsL;
        // synchronous version for the same place
        if (dst.rail.home.id == x10.xrx.Runtime.home().id) {
            System.arraycopy(src.value, srcIndex, dst.$apply().value, dstIndex, numElems);
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

        VoidFun_0_0 copyBody = new $Closure$0(dataToCopy, dst, dstIndex, numElems, null);

        x10.xrx.Runtime.runAsync(dst.rail.home, copyBody, null);
    }

    // static nested class version of copyBody
    public static class $Closure$0<T> extends Ref implements VoidFun_0_0 {
        public Object srcData;
        public GlobalRail<T> dst;
        public int dstIndex;
        public int numElems;
        public VoidFun_0_0 notif;

        // Just for allocation
        $Closure$0() {
        }
        $Closure$0(Object srcData, GlobalRail<T> dst, int dstIndex, int numElems, VoidFun_0_0 notif) {
            this.srcData = srcData;
            this.dst = dst;
            this.dstIndex = dstIndex;
            this.numElems = numElems;
            this.notif = notif;
        }
        public void $apply() {
            if (numElems > 0) {
                Object dstData = dst.$apply().getBackingArray();
                System.arraycopy(srcData, 0, dstData, dstIndex, numElems);
            }
            if (notif != null) {
                notif.$apply();
            }
        }
        public static final RuntimeType<$Closure$0> $RTT = StaticVoidFunType.<$Closure$0> make($Closure$0.class, new Type[] { VoidFun_0_0.$RTT });
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
                $serializer.write(this.srcData);
            }
            $serializer.write(this.dst);
            $serializer.write(this.dstIndex);
            $serializer.write(this.notif);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
            $Closure$0 $_obj = new $Closure$0();
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$0 $_obj, X10JavaDeserializer $deserializer) throws IOException {
            $_obj.numElems = $deserializer.readInt();
            if ($_obj.numElems > 0) {
                $_obj.srcData = $deserializer.readObject();
            }
            $_obj.dst = $deserializer.readObject();
            $_obj.dstIndex = $deserializer.readInt();
            $_obj.notif = $deserializer.readObject();
            return $_obj;
        }
    }

    public static <T> void uncountedCopy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T, Rail<T> src, long srcIndexL, GlobalRail<T> dst, long dstIndexL, long numElemsL, VoidFun_0_0 notifier) {
        int srcIndex = (int)srcIndexL;
        int dstIndex = (int)dstIndexL;
        int numElems = (int)numElemsL;
        // synchronous version for the same place
        if (dst.rail.home.id == x10.xrx.Runtime.home().id) {
            System.arraycopy(src.value, srcIndex, dst.$apply().value, dstIndex, numElems);
            notifier.$apply();
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

        VoidFun_0_0 copyBody = new $Closure$0(dataToCopy, dst, dstIndex, numElems, notifier);

        x10.xrx.Runtime.runUncountedAsync(dst.rail.home, copyBody, null);
    }

    public static <T> void asyncCopy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T, final GlobalRail<T> src, final long srcIndexL, Rail<T> dst, final long dstIndexL, final long numElemsL) {
        int srcIndex = (int)srcIndexL;
        int dstIndex = (int)dstIndexL;
        int numElems = (int)numElemsL;
        // synchronous version for the same place
        if (src.rail.home.id == x10.xrx.Runtime.home().id) {
            System.arraycopy(src.$apply().value, srcIndex, dst.value, dstIndex, numElems);
            return;
        }

        final GlobalRail<T> dstWrapper = new GlobalRail<T>(ParameterizedType.make(Rail.$RTT, dst.T), dst, null);

        VoidFun_0_0 copyBody1 = new $Closure$1<T>(src, srcIndex, dstWrapper, dstIndex, numElems, null);

        x10.xrx.Runtime.runAsync(src.rail.home, copyBody1, null);
    }

    // static nested class version of copyBody1
    public static class $Closure$1<T> extends Ref implements VoidFun_0_0 {
        public GlobalRail<T> src;
        public int srcIndex;
        public GlobalRail<T> dstWrapper;
        public int dstIndex;
        public int numElems;
        public VoidFun_0_0 notifier;

        //Just for allocation
        $Closure$1() {
        }
        $Closure$1(GlobalRail<T> src, int srcIndex, GlobalRail<T> dstWrapper, int dstIndex, int numElems, VoidFun_0_0 notifier) {
            this.src = src;
            this.srcIndex = srcIndex;
            this.dstWrapper = dstWrapper;
            this.dstIndex = dstIndex;
            this.numElems = numElems;
            this.notifier = notifier;
        }
        public void $apply() {
            // This body runs at src's home.  It accesses the data for src and then does
            // another async back to dstWrapper's home to transfer the data.
            Rail<T> srcData = src.$apply();

            // extra copy here simplifies logic and allows us to do this entirely at the Java level.
            // We'll eventually need to optimize this by writing custom native/JNI code instead of treating
            // it as just another async to execute remotely.
            final Object dataToCopy;
            if (numElems == srcData.size) {
                dataToCopy = srcData.getBackingArray();
            } else {
                dataToCopy = src.$apply().T.makeArray(numElems);
                System.arraycopy(srcData.getBackingArray(), srcIndex, dataToCopy, 0, numElems);
            }

            // N.B. copyBody2 is same as copyBody 
            VoidFun_0_0 copyBody2 = new $Closure$0(dataToCopy, dstWrapper, dstIndex, numElems, notifier);

            if (notifier != null) {
                x10.xrx.Runtime.runUncountedAsync(dstWrapper.rail.home, copyBody2, null);
            } else {
                x10.xrx.Runtime.runAsync(dstWrapper.rail.home, copyBody2, null);
            }
        }
        public static final RuntimeType<$Closure$1<?>> $RTT = StaticVoidFunType.<$Closure$1<?>> make($Closure$1.class, new Type[] { VoidFun_0_0.$RTT });
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
            $serializer.write(this.notifier);
        }

        public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) throws IOException {
            $Closure$1 $_obj = new $Closure$1();
            $deserializer.record_reference($_obj);
            return $_deserialize_body($_obj, $deserializer);
        }

        public static X10JavaSerializable $_deserialize_body($Closure$1 $_obj, X10JavaDeserializer $deserializer) throws IOException {
            $_obj.src = $deserializer.readObject();
            $_obj.srcIndex = $deserializer.readInt();
            $_obj.dstWrapper = $deserializer.readObject();
            $_obj.dstIndex = $deserializer.readInt();
            $_obj.numElems = $deserializer.readInt();
            $_obj.notifier = $deserializer.readObject();
            return $_obj;
        }
    }

    public static <T> void uncountedCopy__0$1x10$lang$Rail$$T$2__2$1x10$lang$Rail$$T$2(Type T, GlobalRail<T> src, long srcIndexL, Rail<T> dst, long dstIndexL, long numElemsL, VoidFun_0_0 notifier) {
        int srcIndex = (int)srcIndexL;
        int dstIndex = (int)dstIndexL;
        int numElems = (int)numElemsL;
        // synchronous version for the same place
        if (src.rail.home.id == x10.xrx.Runtime.home().id) {
            System.arraycopy(src.$apply().value, srcIndex, dst.value, dstIndex, numElems);
            notifier.$apply();
            return;
        }

        final GlobalRail<T> dstWrapper = new GlobalRail<T>(ParameterizedType.make(Rail.$RTT, dst.T), dst, null);

        VoidFun_0_0 copyBody1 = new $Closure$1<T>(src, srcIndex, dstWrapper, dstIndex, numElems, notifier);

        x10.xrx.Runtime.runUncountedAsync(src.rail.home, copyBody1, null);
    }
}
