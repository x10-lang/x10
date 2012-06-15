package x10.io;


@x10.core.X10Generated public class Console extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Console.class);
    
    public static final x10.rtt.RuntimeType<Console> $RTT = x10.rtt.NamedType.<Console> make(
    "x10.io.Console", /* base class */Console.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Console $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Console.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Console $_obj = new Console((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public Console(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
private static x10.core.io.OutputStream
                                                                                              realOut(
                                                                                              ){try {return new x10.core.io.OutputStream(java.lang.System.out);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        public static x10.core.io.OutputStream
          realOut$P(
          ){
            return new x10.core.io.OutputStream(java.lang.System.out);
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
private static x10.core.io.OutputStream
                                                                                              realErr(
                                                                                              ){try {return new x10.core.io.OutputStream(java.lang.System.err);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        public static x10.core.io.OutputStream
          realErr$P(
          ){
            return new x10.core.io.OutputStream(java.lang.System.err);
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
private static x10.core.io.InputStream
                                                                                              realIn(
                                                                                              ){try {return new x10.core.io.InputStream(java.lang.System.in);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        public static x10.core.io.InputStream
          realIn$P(
          ){
            return new x10.core.io.InputStream(java.lang.System.in);
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
final public static x10.io.Printer OUT = ((x10.io.Printer)(new x10.io.Printer((java.lang.System[]) null).$init(((x10.io.Writer)(new x10.io.OutputStreamWriter((java.lang.System[]) null).$init(new x10.core.io.OutputStream(java.lang.System.out)))))));
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
final public static x10.io.Printer ERR = ((x10.io.Printer)(new x10.io.Printer((java.lang.System[]) null).$init(((x10.io.Writer)(new x10.io.OutputStreamWriter((java.lang.System[]) null).$init(new x10.core.io.OutputStream(java.lang.System.err)))))));
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
final public static x10.io.Reader IN = ((x10.io.Reader)(new x10.io.InputStreamReader((java.lang.System[]) null).$init(new x10.core.io.InputStream(java.lang.System.in))));
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
final public x10.io.Console
                                                                                              x10$io$Console$$x10$io$Console$this(
                                                                                              ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
return x10.io.Console.this;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"
// creation method for java code (1-phase java constructor)
        public Console(){this((java.lang.System[]) null);
                             $init();}
        
        // constructor for non-virtual call
        final public x10.io.Console x10$io$Console$$init$S() { {
                                                                      
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"

                                                                      
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/io/Console.x10"

                                                                  }
                                                                  return this;
                                                                  }
        
        // constructor
        public x10.io.Console $init(){return x10$io$Console$$init$S();}
        
    
}
