package x10.lang;


@x10.core.X10Generated public class System extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, System.class);
    
    public static final x10.rtt.RuntimeType<System> $RTT = x10.rtt.NamedType.<System> make(
    "x10.lang.System", /* base class */System.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(System $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + System.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        System $_obj = new System((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public System(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
// creation method for java code (1-phase java constructor)
        public System(){this((java.lang.System[]) null);
                            $init();}
        
        // constructor for non-virtual call
        final public x10.lang.System x10$lang$System$$init$S() { {
                                                                        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"

                                                                        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"

                                                                    }
                                                                    return this;
                                                                    }
        
        // constructor
        public x10.lang.System $init(){return x10$lang$System$$init$S();}
        
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static long
                                                                                               currentTimeMillis$O(
                                                                                               ){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
final long t56629 =
              java.lang.System.currentTimeMillis();
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return t56629;
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static long
                                                                                               nanoTime$O(
                                                                                               ){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
final long t56630 =
              java.lang.System.nanoTime();
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return t56630;
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static void
                                                                                               exit(
                                                                                               final int code){try {java.lang.System.exit(code);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static void
                                                                                               exit(
                                                                                               ){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
java.lang.System.exit(((int)(-1)));
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static void
                                                                                               setExitCode(
                                                                                               final int exitCode){try {x10.runtime.impl.java.Runtime.setExitCode(exitCode);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static long
                                                                                               heapSize$O(
                                                                                               ){try {return java.lang.Runtime.getRuntime().totalMemory();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static void
                                                                                               gc(
                                                                                               ){try {java.lang.System.gc();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static x10.util.Map<java.lang.String, java.lang.String>
                                                                                               getenv(
                                                                                               ){
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
final x10.util.HashMap<java.lang.String, java.lang.String> t56632 =
              ((x10.util.HashMap)(x10.lang.Runtime.env));
            
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return t56632;
        }
        
        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static void
                                                                                                setProperty(
                                                                                                final java.lang.String p,
                                                                                                final java.lang.String v){try {java.lang.System.setProperty(p,v);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static boolean
                                                                                                sleep$O(
                                                                                                final long millis){
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
try {try {{
                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
x10.lang.Runtime.increaseParallelism();
                
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
x10.runtime.impl.java.Thread.sleep((long)(millis));
                
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
x10.lang.Runtime.decreaseParallelism((int)(1));
                
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return true;
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.InterruptedException e) {
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
x10.lang.Runtime.decreaseParallelism((int)(1));
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return false;
            }
        }
        
        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
public static boolean
                                                                                                threadSleep$O(
                                                                                                final long millis){
            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
try {try {{
                
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
x10.runtime.impl.java.Thread.sleep((long)(millis));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return true;
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Exception $exc$) {throw x10.core.ThrowableUtilities.convertJavaException($exc$);}}catch (final x10.lang.InterruptedException e) {
                
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return false;
            }
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
final public x10.lang.System
                                                                                               x10$lang$System$$x10$lang$System$this(
                                                                                               ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/System.x10"
return x10.lang.System.this;
        }
    
}
