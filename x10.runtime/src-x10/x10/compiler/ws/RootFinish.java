package x10.compiler.ws;


@x10.core.X10Generated final public class RootFinish extends x10.compiler.ws.FinishFrame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RootFinish.class);
    
    public static final x10.rtt.RuntimeType<RootFinish> $RTT = x10.rtt.NamedType.<RootFinish> make(
    "x10.compiler.ws.RootFinish", /* base class */RootFinish.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.FinishFrame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RootFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RootFinish.class + " calling"); } 
        x10.compiler.ws.FinishFrame.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        RootFinish $_obj = new RootFinish((java.lang.System[]) null);
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
    public RootFinish(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 7 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
// creation method for java code (1-phase java constructor)
        public RootFinish(){this((java.lang.System[]) null);
                                $init();}
        
        // constructor for non-virtual call
        final public x10.compiler.ws.RootFinish x10$compiler$ws$RootFinish$$init$S() { {
                                                                                              
//#line 8 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
super.$init(((x10.compiler.ws.Frame)(null)));
                                                                                              
//#line 7 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"

                                                                                              
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
this.asyncs = 1;
                                                                                          }
                                                                                          return this;
                                                                                          }
        
        // constructor
        public x10.compiler.ws.RootFinish $init(){return x10$compiler$ws$RootFinish$$init$S();}
        
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
public void
                                                                                                          wrapResume(
                                                                                                          final x10.compiler.ws.Worker worker){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
super.wrapResume(((x10.compiler.ws.Worker)(worker)));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
x10.compiler.ws.Worker.stop();
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
final x10.compiler.Abort t49006 =
              ((x10.compiler.Abort)(x10.compiler.Abort.getInitialized$ABORT()));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
throw t49006;
        }
        
        
//#line 6 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
final public x10.compiler.ws.RootFinish
                                                                                                         x10$compiler$ws$RootFinish$$x10$compiler$ws$RootFinish$this(
                                                                                                         ){
            
//#line 6 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RootFinish.x10"
return x10.compiler.ws.RootFinish.this;
        }
        
        public void
          x10$compiler$ws$FinishFrame$wrapResume$S(
          final x10.compiler.ws.Worker a0){
            super.wrapResume(((x10.compiler.ws.Worker)(a0)));
        }
    
}
