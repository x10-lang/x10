package x10.compiler.ws;


@x10.core.X10Generated abstract public class RegularFrame extends x10.compiler.ws.Frame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RegularFrame.class);
    
    public static final x10.rtt.RuntimeType<RegularFrame> $RTT = x10.rtt.NamedType.<RegularFrame> make(
    "x10.compiler.ws.RegularFrame", /* base class */RegularFrame.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.Frame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RegularFrame $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RegularFrame.class + " calling"); } 
        x10.compiler.ws.Frame.$_deserialize_body($_obj, $deserializer);
        x10.compiler.ws.FinishFrame ff = (x10.compiler.ws.FinishFrame) $deserializer.readRef();
        $_obj.ff = ff;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (ff instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ff);
        } else {
        $serializer.write(this.ff);
        }
        
    }
    
    // constructor just for allocation
    public RegularFrame(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 12 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
public x10.compiler.ws.FinishFrame ff;
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"

        // constructor for non-virtual call
        final public x10.compiler.ws.RegularFrame x10$compiler$ws$RegularFrame$$init$S(final x10.compiler.ws.Frame up,
                                                                                       final x10.compiler.ws.FinishFrame ff) { {
                                                                                                                                      
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
super.$init(((x10.compiler.ws.Frame)(up)));
                                                                                                                                      
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"

                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
this.ff = ((x10.compiler.ws.FinishFrame)(ff));
                                                                                                                                  }
                                                                                                                                  return this;
                                                                                                                                  }
        
        // constructor
        public x10.compiler.ws.RegularFrame $init(final x10.compiler.ws.Frame up,
                                                  final x10.compiler.ws.FinishFrame ff){return x10$compiler$ws$RegularFrame$$init$S(up,ff);}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final public void
                                                                                                            push(
                                                                                                            final x10.compiler.ws.Worker worker){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final x10.runtime.impl.java.Deque t48973 =
              ((x10.runtime.impl.java.Deque)(worker.
                                               deque));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
t48973.push(((x10.core.RefI)(this)));
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final public void
                                                                                                            continueLater(
                                                                                                            final x10.compiler.ws.Worker worker){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
worker.migrate();
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
x10.compiler.ws.RegularFrame k =
              this;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final x10.compiler.ws.RegularFrame t48974 =
              k;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
x10.lang.Runtime.wsBlock(((x10.core.RefI)(t48974)));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final x10.compiler.Abort t48975 =
              ((x10.compiler.Abort)(x10.compiler.Abort.getInitialized$ABORT()));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
throw t48975;
        }
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final public void
                                                                                                            continueNow(
                                                                                                            final x10.compiler.ws.Worker worker){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
worker.migrate();
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
x10.compiler.ws.RegularFrame k =
              this;
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final x10.runtime.impl.java.Deque t48976 =
              ((x10.runtime.impl.java.Deque)(worker.
                                               fifo));
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final x10.compiler.ws.RegularFrame t48977 =
              k;
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
t48976.push(((x10.core.RefI)(t48977)));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final x10.compiler.Abort t48978 =
              ((x10.compiler.Abort)(x10.compiler.Abort.getInitialized$ABORT()));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
throw t48978;
        }
        
        
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
final public x10.compiler.ws.RegularFrame
                                                                                                            x10$compiler$ws$RegularFrame$$x10$compiler$ws$RegularFrame$this(
                                                                                                            ){
            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RegularFrame.x10"
return x10.compiler.ws.RegularFrame.this;
        }
    
}
