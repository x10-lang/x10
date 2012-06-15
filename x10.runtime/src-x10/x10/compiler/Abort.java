package x10.compiler;

@x10.core.X10Generated public class Abort extends x10.core.X10Throwable implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Abort.class);
    
    public static final x10.rtt.RuntimeType<Abort> $RTT = x10.rtt.NamedType.<Abort> make(
    "x10.compiler.Abort", /* base class */Abort.class
    , /* parents */ new x10.rtt.Type[] {x10.core.X10Throwable.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Abort $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Abort.class + " calling"); } 
        x10.core.X10Throwable.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Abort $_obj = new Abort((java.lang.System[]) null);
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
    public Abort(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Abort.x10"
public static x10.compiler.Abort ABORT;
        
        
//#line 13 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Abort.x10"
public Abort() {super();
                                                                                                                     {
                                                                                                                        
//#line 13 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Abort.x10"

                                                                                                                    }}
        
        
//#line 10 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Abort.x10"
final public x10.compiler.Abort
                                                                                                  x10$compiler$Abort$$x10$compiler$Abort$this(
                                                                                                  ){
            
//#line 10 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/Abort.x10"
return x10.compiler.Abort.this;
        }
        
        public static short fieldId$ABORT;
        final public static x10.core.concurrent.AtomicInteger initStatus$ABORT = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$ABORT(
          x10.x10rt.X10JavaDeserializer deserializer){
            x10.compiler.Abort.ABORT = ((x10.compiler.Abort)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
            x10.compiler.Abort.initStatus$ABORT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.compiler.Abort
          getInitialized$ABORT(
          ){
            if (((int) x10.compiler.Abort.initStatus$ABORT.get()) ==
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                return x10.compiler.Abort.ABORT;
            }
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0) &&
                  x10.compiler.Abort.initStatus$ABORT.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                    (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                x10.compiler.Abort.ABORT = ((x10.compiler.Abort)(new x10.compiler.Abort()));
                if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                      ((boolean) true)) {
                    x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.compiler.Abort.ABORT")));
                }
                x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.compiler.Abort.ABORT)),
                                                                          (short)(x10.compiler.Abort.fieldId$ABORT));
                x10.compiler.Abort.initStatus$ABORT.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            } else {
                if (((int) x10.compiler.Abort.initStatus$ABORT.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) x10.compiler.Abort.initStatus$ABORT.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
            }
            return x10.compiler.Abort.ABORT;
        }
        
        static {
                   x10.compiler.Abort.fieldId$ABORT = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.compiler.Abort")),
                                                                                                                          ((java.lang.String)("ABORT")))))));
               }
        
    }
    