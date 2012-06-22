package x10.lang;

@x10.core.X10Generated public class IllegalOperationException extends x10.lang.Exception implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, IllegalOperationException.class);
    
    public static final x10.rtt.RuntimeType<IllegalOperationException> $RTT = x10.rtt.NamedType.<IllegalOperationException> make(
    "x10.lang.IllegalOperationException", /* base class */IllegalOperationException.class
    , /* parents */ new x10.rtt.Type[] {x10.lang.Exception.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(IllegalOperationException $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + IllegalOperationException.class + " calling"); } 
        x10.lang.Exception.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        IllegalOperationException $_obj = new IllegalOperationException((java.lang.System[]) null);
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
    public IllegalOperationException(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IllegalOperationException.x10"
public IllegalOperationException() {super(((java.lang.String)("illegal operation exception")));
                                                                                                                                                         {
                                                                                                                                                            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IllegalOperationException.x10"

                                                                                                                                                        }}
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IllegalOperationException.x10"
public IllegalOperationException(final java.lang.String message) {super(((java.lang.String)(message)));
                                                                                                                                                                                       {
                                                                                                                                                                                          
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IllegalOperationException.x10"

                                                                                                                                                                                      }}
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IllegalOperationException.x10"
final public x10.lang.IllegalOperationException
                                                                                                                  x10$lang$IllegalOperationException$$x10$lang$IllegalOperationException$this(
                                                                                                                  ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/IllegalOperationException.x10"
return x10.lang.IllegalOperationException.this;
        }
        
        }
        