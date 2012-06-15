package x10.compiler.ws;


@x10.core.X10Generated final public class ThrowFrame extends x10.compiler.ws.Frame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ThrowFrame.class);
    
    public static final x10.rtt.RuntimeType<ThrowFrame> $RTT = x10.rtt.NamedType.<ThrowFrame> make(
    "x10.compiler.ws.ThrowFrame", /* base class */ThrowFrame.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.Frame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ThrowFrame $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ThrowFrame.class + " calling"); } 
        x10.compiler.ws.Frame.$_deserialize_body($_obj, $deserializer);
        x10.core.X10Throwable throwable = (x10.core.X10Throwable) $deserializer.readRef();
        $_obj.throwable = throwable;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ThrowFrame $_obj = new ThrowFrame((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (throwable instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.throwable);
        } else {
        $serializer.write(this.throwable);
        }
        
    }
    
    // constructor just for allocation
    public ThrowFrame(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 6 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
public x10.core.X10Throwable throwable;
        
        
//#line 8 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
// creation method for java code (1-phase java constructor)
        public ThrowFrame(final x10.compiler.ws.Frame up,
                          final x10.core.X10Throwable throwable){this((java.lang.System[]) null);
                                                                     $init(up,throwable);}
        
        // constructor for non-virtual call
        final public x10.compiler.ws.ThrowFrame x10$compiler$ws$ThrowFrame$$init$S(final x10.compiler.ws.Frame up,
                                                                                   final x10.core.X10Throwable throwable) { {
                                                                                                                                   
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
super.$init(((x10.compiler.ws.Frame)(up)));
                                                                                                                                   
//#line 8 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"

                                                                                                                                   
//#line 10 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
this.throwable = ((x10.core.X10Throwable)(throwable));
                                                                                                                               }
                                                                                                                               return this;
                                                                                                                               }
        
        // constructor
        public x10.compiler.ws.ThrowFrame $init(final x10.compiler.ws.Frame up,
                                                final x10.core.X10Throwable throwable){return x10$compiler$ws$ThrowFrame$$init$S(up,throwable);}
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
public void
                                                                                                          wrapResume(
                                                                                                          final x10.compiler.ws.Worker worker){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
final x10.core.X10Throwable t49007 =
              ((x10.core.X10Throwable)(throwable));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
worker.throwable = ((x10.core.X10Throwable)(t49007));
        }
        
        
//#line 5 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
final public x10.compiler.ws.ThrowFrame
                                                                                                         x10$compiler$ws$ThrowFrame$$x10$compiler$ws$ThrowFrame$this(
                                                                                                         ){
            
//#line 5 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/ThrowFrame.x10"
return x10.compiler.ws.ThrowFrame.this;
        }
    
}
