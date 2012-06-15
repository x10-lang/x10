package x10.compiler.ws;


@x10.core.X10Generated abstract public class TryFrame extends x10.compiler.ws.RegularFrame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, TryFrame.class);
    
    public static final x10.rtt.RuntimeType<TryFrame> $RTT = x10.rtt.NamedType.<TryFrame> make(
    "x10.compiler.ws.TryFrame", /* base class */TryFrame.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.RegularFrame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(TryFrame $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + TryFrame.class + " calling"); } 
        x10.compiler.ws.RegularFrame.$_deserialize_body($_obj, $deserializer);
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
        
    }
    
    // constructor just for allocation
    public TryFrame(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"

        // constructor for non-virtual call
        final public x10.compiler.ws.TryFrame x10$compiler$ws$TryFrame$$init$S(final x10.compiler.ws.Frame up,
                                                                               final x10.compiler.ws.FinishFrame ff) { {
                                                                                                                              
//#line 10 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
super.$init(((x10.compiler.ws.Frame)(up)),
                                                                                                                                                                                                                                        ((x10.compiler.ws.FinishFrame)(ff)));
                                                                                                                              
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"

                                                                                                                          }
                                                                                                                          return this;
                                                                                                                          }
        
        // constructor
        public x10.compiler.ws.TryFrame $init(final x10.compiler.ws.Frame up,
                                              final x10.compiler.ws.FinishFrame ff){return x10$compiler$ws$TryFrame$$init$S(up,ff);}
        
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
public void
                                                                                                        wrapResume(
                                                                                                        final x10.compiler.ws.Worker worker){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
try {try {{
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
this.resume(((x10.compiler.ws.Worker)(worker)));
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.compiler.Abort t) {
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
throw t;
            }catch (final x10.core.X10Throwable t) {
                
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
worker.throwable = ((x10.core.X10Throwable)(t));
            }
        }
        
        
//#line 8 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
final public x10.compiler.ws.TryFrame
                                                                                                       x10$compiler$ws$TryFrame$$x10$compiler$ws$TryFrame$this(
                                                                                                       ){
            
//#line 8 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/TryFrame.x10"
return x10.compiler.ws.TryFrame.this;
        }
    
}
