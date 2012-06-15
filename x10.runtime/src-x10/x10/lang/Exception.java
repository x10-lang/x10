package x10.lang;

@x10.core.X10Generated public class Exception extends x10.core.X10Throwable implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Exception.class);
    
    public static final x10.rtt.RuntimeType<Exception> $RTT = x10.rtt.NamedType.<Exception> make(
    "x10.lang.Exception", /* base class */Exception.class
    , /* parents */ new x10.rtt.Type[] {x10.core.X10Throwable.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Exception $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Exception.class + " calling"); } 
        x10.core.X10Throwable.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Exception $_obj = new Exception((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        
    }
    
    // constructor just for allocation
    public Exception(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
public Exception() {super();
                                                                                                                         {
                                                                                                                            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"

                                                                                                                        }}
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
public Exception(final java.lang.String message) {super(((java.lang.String)(message)));
                                                                                                                                                       {
                                                                                                                                                          
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"

                                                                                                                                                      }}
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
public Exception(final java.lang.String message,
                                                                                                                 final x10.core.X10Throwable cause) {super(((java.lang.String)(message)),
                                                                                                                                                           ((x10.core.X10Throwable)(cause)));
                                                                                                                                                          {
                                                                                                                                                             
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"

                                                                                                                                                         }}
        
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
public Exception(final x10.core.X10Throwable cause) {super(((x10.core.X10Throwable)(cause)));
                                                                                                                                                          {
                                                                                                                                                             
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"

                                                                                                                                                         }}
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
public java.lang.String
                                                                                                  toString(
                                                                                                  ){
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
final java.lang.String m =
              this.getMessage$O();
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
final boolean t51925 =
              ((m) == (null));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
java.lang.String t51926 =
               null;
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
if (t51925) {
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
t51926 = x10.rtt.Types.typeName(this);
            } else {
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
final java.lang.String t51922 =
                  x10.rtt.Types.typeName(this);
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
final java.lang.String t51923 =
                  ((t51922) + (": "));
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
final java.lang.String t51924 =
                  this.getMessage$O();
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
t51926 = ((t51923) + (t51924));
            }
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
final java.lang.String t51927 =
              t51926;
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
return t51927;
        }
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
final public x10.lang.Exception
                                                                                                  x10$lang$Exception$$x10$lang$Exception$this(
                                                                                                  ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Exception.x10"
return x10.lang.Exception.this;
        }
        
        }
        