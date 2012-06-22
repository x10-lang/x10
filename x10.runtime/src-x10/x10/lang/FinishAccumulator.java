package x10.lang;


@x10.core.X10Generated final public class FinishAccumulator<$T> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishAccumulator.class);
    
    public static final x10.rtt.RuntimeType<FinishAccumulator> $RTT = x10.rtt.NamedType.<FinishAccumulator> make(
    "x10.lang.FinishAccumulator", /* base class */FinishAccumulator.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishAccumulator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FinishAccumulator.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FinishAccumulator $_obj = new FinishAccumulator((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        
    }
    
    // constructor just for allocation
    public FinishAccumulator(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.lang.FinishAccumulator.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final FinishAccumulator $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishAccumulator.x10"
final public x10.lang.FinishAccumulator<$T>
                                                                                                          x10$lang$FinishAccumulator$$x10$lang$FinishAccumulator$this(
                                                                                                          ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishAccumulator.x10"
return x10.lang.FinishAccumulator.this;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishAccumulator.x10"
// creation method for java code (1-phase java constructor)
        public FinishAccumulator(final x10.rtt.Type $T){this((java.lang.System[]) null, $T);
                                                            $init();}
        
        // constructor for non-virtual call
        final public x10.lang.FinishAccumulator<$T> x10$lang$FinishAccumulator$$init$S() { {
                                                                                                  
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishAccumulator.x10"

                                                                                                  
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishAccumulator.x10"

                                                                                              }
                                                                                              return this;
                                                                                              }
        
        // constructor
        public x10.lang.FinishAccumulator<$T> $init(){return x10$lang$FinishAccumulator$$init$S();}
        
    
}
