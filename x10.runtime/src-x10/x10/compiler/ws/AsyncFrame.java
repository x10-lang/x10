package x10.compiler.ws;


@x10.core.X10Generated abstract public class AsyncFrame extends x10.compiler.ws.Frame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AsyncFrame.class);
    
    public static final x10.rtt.RuntimeType<AsyncFrame> $RTT = x10.rtt.NamedType.<AsyncFrame> make(
    "x10.compiler.ws.AsyncFrame", /* base class */AsyncFrame.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.Frame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(AsyncFrame $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + AsyncFrame.class + " calling"); } 
        x10.compiler.ws.Frame.$_deserialize_body($_obj, $deserializer);
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
    public AsyncFrame(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"

        // constructor for non-virtual call
        final public x10.compiler.ws.AsyncFrame x10$compiler$ws$AsyncFrame$$init$S(final x10.compiler.ws.Frame up) { {
                                                                                                                            
//#line 10 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
super.$init(((x10.compiler.ws.Frame)(up)));
                                                                                                                            
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"

                                                                                                                        }
                                                                                                                        return this;
                                                                                                                        }
        
        // constructor
        public x10.compiler.ws.AsyncFrame $init(final x10.compiler.ws.Frame up){return x10$compiler$ws$AsyncFrame$$init$S(up);}
        
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final public x10.compiler.ws.FinishFrame
                                                                                                          ff(
                                                                                                          ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final x10.compiler.ws.Frame t48816 =
              ((x10.compiler.ws.Frame)(up));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final x10.compiler.ws.FinishFrame t48817 =
              ((x10.compiler.ws.FinishFrame) t48816);
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
return t48817;
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final public void
                                                                                                          poll(
                                                                                                          final x10.compiler.ws.Worker worker){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final x10.runtime.impl.java.Deque t48818 =
              ((x10.runtime.impl.java.Deque)(worker.
                                               deque));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final x10.core.RefI t48819 =
              t48818.poll();
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final boolean t48820 =
              ((null) == (t48819));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
if (t48820) {
                
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
this.pollSlow(((x10.compiler.ws.Worker)(worker)));
            }
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final public void
                                                                                                          pollSlow(
                                                                                                          final x10.compiler.ws.Worker worker){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final x10.util.concurrent.Lock lock =
              ((x10.util.concurrent.Lock)(worker.
                                            lock));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
lock.lock();
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
lock.unlock();
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
x10.compiler.ws.FinishFrame ff =
              this.ff();
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final x10.compiler.ws.FinishFrame t48821 =
              ff;
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
worker.unroll(((x10.compiler.ws.Frame)(t48821)));
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final public void
                                                                                                          caught(
                                                                                                          final x10.core.X10Throwable t){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final x10.compiler.ws.FinishFrame t48822 =
              this.ff();
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
t48822.caught(((x10.core.X10Throwable)(t)));
        }
        
        
//#line 8 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
final public x10.compiler.ws.AsyncFrame
                                                                                                         x10$compiler$ws$AsyncFrame$$x10$compiler$ws$AsyncFrame$this(
                                                                                                         ){
            
//#line 8 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AsyncFrame.x10"
return x10.compiler.ws.AsyncFrame.this;
        }
    
}
