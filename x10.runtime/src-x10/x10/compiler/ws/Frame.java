package x10.compiler.ws;


@x10.core.X10Generated abstract public class Frame extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Frame.class);
    
    public static final x10.rtt.RuntimeType<Frame> $RTT = x10.rtt.NamedType.<Frame> make(
    "x10.compiler.ws.Frame", /* base class */Frame.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Frame $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Frame.class + " calling"); } 
        x10.compiler.ws.Frame up = (x10.compiler.ws.Frame) $deserializer.readRef();
        $_obj.up = up;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (up instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.up);
        } else {
        $serializer.write(this.up);
        }
        
    }
    
    // constructor just for allocation
    public Frame(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 12 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
public static <$T, $U>$U
                                                                                                     cast__0x10$compiler$ws$Frame$$T$G(
                                                                                                     final x10.rtt.Type $T,
                                                                                                     final x10.rtt.Type $U,
                                                                                                     final $T x){try {return (($U) x);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
public x10.compiler.ws.Frame up;
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"

        // constructor for non-virtual call
        final public x10.compiler.ws.Frame x10$compiler$ws$Frame$$init$S(final x10.compiler.ws.Frame up) { {
                                                                                                                  
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"

                                                                                                                  
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"

                                                                                                                  
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
this.up = ((x10.compiler.ws.Frame)(up));
                                                                                                              }
                                                                                                              return this;
                                                                                                              }
        
        // constructor
        public x10.compiler.ws.Frame $init(final x10.compiler.ws.Frame up){return x10$compiler$ws$Frame$$init$S(up);}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
public void
                                                                                                     back(
                                                                                                     final x10.compiler.ws.Worker worker,
                                                                                                     final x10.compiler.ws.Frame frame){
            
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
public void
                                                                                                     wrapBack(
                                                                                                     final x10.compiler.ws.Worker worker,
                                                                                                     final x10.compiler.ws.Frame frame){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
final x10.core.X10Throwable t48969 =
              ((x10.core.X10Throwable)(worker.
                                         throwable));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
final boolean t48970 =
              ((null) != (t48969));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
if (t48970) {
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
return;
            }
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
this.back(((x10.compiler.ws.Worker)(worker)),
                                                                                                                 ((x10.compiler.ws.Frame)(frame)));
        }
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
public void
                                                                                                     resume(
                                                                                                     final x10.compiler.ws.Worker worker){
            
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
public void
                                                                                                     wrapResume(
                                                                                                     final x10.compiler.ws.Worker worker){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
final x10.core.X10Throwable t48971 =
              ((x10.core.X10Throwable)(worker.
                                         throwable));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
final boolean t48972 =
              ((null) != (t48971));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
if (t48972) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
return;
            }
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
try {try {{
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
this.resume(((x10.compiler.ws.Worker)(worker)));
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.compiler.Abort t) {
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
throw t;
            }catch (final x10.core.X10Throwable t) {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
worker.throwable = ((x10.core.X10Throwable)(t));
            }
        }
        
        
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
final public x10.compiler.ws.Frame
                                                                                                     x10$compiler$ws$Frame$$x10$compiler$ws$Frame$this(
                                                                                                     ){
            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Frame.x10"
return x10.compiler.ws.Frame.this;
        }
    
}
