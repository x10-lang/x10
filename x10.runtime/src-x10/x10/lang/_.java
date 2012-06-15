package x10.lang;

@x10.core.X10Generated public class _ extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, _.class);
    
    public static final x10.rtt.RuntimeType<_> $RTT = x10.rtt.NamedType.<_> make(
    "x10.lang._", /* base class */_.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(_ $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + _.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        _ $_obj = new _((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public _(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/_.x10"
final public x10.lang._
                                                                                          x10$lang$_$$x10$lang$_$this(
                                                                                          ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/_.x10"
return x10.lang._.this;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/_.x10"
// creation method for java code (1-phase java constructor)
        public _(){this((java.lang.System[]) null);
                       $init();}
        
        // constructor for non-virtual call
        final public x10.lang._ x10$lang$_$$init$S() { {
                                                              
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/_.x10"

                                                              
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/_.x10"

                                                          }
                                                          return this;
                                                          }
        
        // constructor
        public x10.lang._ $init(){return x10$lang$_$$init$S();}
        
    
}
