package x10.interop;


@x10.core.X10Generated public class Java extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Java.class);
    
    public static final x10.rtt.RuntimeType<Java> $RTT = x10.rtt.NamedType.<Java> make(
    "x10.interop.Java", /* base class */Java.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Java $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Java.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Java $_obj = new Java((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public Java(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public Java() {super();
                                                                                                                 {
                                                                                                                    
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"

                                                                                                                }}
        
        
        
        
        
        
        
        
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
;
        
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static <$T>$T[]
                                                                                                newArray(
                                                                                                final x10.rtt.Type $T,
                                                                                                final int d0){try {return ($T[])$T.makeArray(d0);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static <$T>$T[][]
                                                                                                newArray(
                                                                                                final x10.rtt.Type $T,
                                                                                                final int d0,
                                                                                                final int d1){try {return ($T[][])$T.makeArray(d0,d1);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static <$T>$T[][][]
                                                                                                newArray(
                                                                                                final x10.rtt.Type $T,
                                                                                                final int d0,
                                                                                                final int d1,
                                                                                                final int d2){try {return ($T[][][])$T.makeArray(d0,d1,d2);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static <$T>$T[][][][]
                                                                                                newArray(
                                                                                                final x10.rtt.Type $T,
                                                                                                final int d0,
                                                                                                final int d1,
                                                                                                final int d2,
                                                                                                final int d3){try {return ($T[][][][])$T.makeArray(d0,d1,d2,d3);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static <$T>java.lang.Class
                                                                                                javaClass(
                                                                                                final x10.rtt.Type $T){try {return ((x10.rtt.RuntimeType<?>)$T).getJavaClass();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static void
                                                                                                throwException(
                                                                                                final java.lang.Throwable e){try {do { throw e; } while (false);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static boolean
                                                                                                convert$O(
                                                                                                final boolean b){
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return b;
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static byte
                                                                                                convert$O(
                                                                                                final byte b){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return b;
        }
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static short
                                                                                                convert$O(
                                                                                                final short s){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return s;
        }
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static int
                                                                                                convert$O(
                                                                                                final int i){
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return i;
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static long
                                                                                                convert$O(
                                                                                                final long l){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return l;
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static float
                                                                                                convert$O(
                                                                                                final float f){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return f;
        }
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static double
                                                                                                convert$O(
                                                                                                final double d){
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return d;
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static char
                                                                                                convert$O(
                                                                                                final char c){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return c;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static java.lang.String
                                                                                                convert$O(
                                                                                                final java.lang.String s){try {return s;}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static <$T>$T[]
                                                                                                convert__0$1x10$interop$Java$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final x10.array.Array<$T> a){try {return ($T[])a.raw.getBackingArray();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
public static <$T>x10.array.Array<$T>
                                                                                                convert__0$1x10$interop$Java$$T$2(
                                                                                                final x10.rtt.Type $T,
                                                                                                final $T[] a){try {return new x10.array.Array((java.lang.System[]) null, $T).x10$array$Array$$init$S(new x10.core.IndexedMemoryChunk($T, a.length, a), (x10.array.Array.__0$1x10$array$Array$$T$2) null);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 7 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
final public x10.interop.Java
                                                                                               x10$interop$Java$$x10$interop$Java$this(
                                                                                               ){
            
//#line 7 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/interop/Java.x10"
return x10.interop.Java.this;
        }
        
    }
    