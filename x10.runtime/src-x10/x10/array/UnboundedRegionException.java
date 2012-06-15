package x10.array;

@x10.core.X10Generated public class UnboundedRegionException extends x10.lang.Exception implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UnboundedRegionException.class);
    
    public static final x10.rtt.RuntimeType<UnboundedRegionException> $RTT = x10.rtt.NamedType.<UnboundedRegionException> make(
    "x10.array.UnboundedRegionException", /* base class */UnboundedRegionException.class
    , /* parents */ new x10.rtt.Type[] {x10.lang.Exception.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(UnboundedRegionException $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + UnboundedRegionException.class + " calling"); } 
        x10.lang.Exception.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        UnboundedRegionException $_obj = new UnboundedRegionException((java.lang.System[]) null);
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
    public UnboundedRegionException(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UnboundedRegionException.x10"
public UnboundedRegionException(final java.lang.String msg) {super(((java.lang.String)(msg)));
                                                                                                                                                                                  {
                                                                                                                                                                                     
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UnboundedRegionException.x10"

                                                                                                                                                                                 }}
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UnboundedRegionException.x10"
public UnboundedRegionException() {super();
                                                                                                                                                        {
                                                                                                                                                           
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UnboundedRegionException.x10"

                                                                                                                                                       }}
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UnboundedRegionException.x10"
final public x10.array.UnboundedRegionException
                                                                                                                  x10$array$UnboundedRegionException$$x10$array$UnboundedRegionException$this(
                                                                                                                  ){
            
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/UnboundedRegionException.x10"
return x10.array.UnboundedRegionException.this;
        }
        
        }
        