package x10.util.concurrent;


@x10.core.X10Generated public class Latch extends x10.util.concurrent.Monitor implements x10.core.fun.Fun_0_0
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Latch.class);
    
    public static final x10.rtt.RuntimeType<Latch> $RTT = x10.rtt.NamedType.<Latch> make(
    "x10.util.concurrent.Latch", /* base class */Latch.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.Types.BOOLEAN), x10.util.concurrent.Monitor.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new Latch($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    // default deserialization constructor
    public Latch(final x10.io.SerialData a) { super(a); {
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"

        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.__fieldInitializers63899();
    }}
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Latch $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Latch.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Latch $_obj = new Latch((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println(" CustomSerialization : $_serialize of " + this + " calling"); } 
        $$serialdata = serialize(); 
        $serializer.write($$serialdata);
        
    }
    
    // dummy 2nd-phase constructor for non-splittable type
    public void $init(x10.io.SerialData $$serialdata) {
    
        throw new x10.lang.RuntimeException("dummy 2nd-phase constructor for non-splittable type should never be called.");
        
    }
    
    // constructor just for allocation
    public Latch(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // bridge for method abstract public ()=>U.operator()(){}:U
    public x10.core.Boolean
      $apply$G(){return x10.core.Boolean.$box($apply$O());}
    
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
public Latch() {super();
                                                                                                                            {
                                                                                                                               
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"

                                                                                                                               
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.__fieldInitializers63899();
                                                                                                                           }}
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
public Latch(final java.lang.Object id$183) {super();
                                                                                                                                                         {
                                                                                                                                                            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"

                                                                                                                                                            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.__fieldInitializers63899();
                                                                                                                                                            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                        }}
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
public boolean state;
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
public void
                                                                                                         release(
                                                                                                         ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.lock();
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.state = true;
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
super.release();
        }
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
public void
                                                                                                         await(
                                                                                                         ){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
x10.lang.Runtime.ensureNotInAtomic();
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
final boolean t63942 =
              state;
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
final boolean t63945 =
              !(t63942);
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
if (t63945) {
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.lock();
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
while (true) {
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
final boolean t63943 =
                      state;
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
final boolean t63944 =
                      !(t63943);
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
if (!(t63944)) {
                        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
break;
                    }
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
super.await();
                }
                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.unlock();
            }
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
public boolean
                                                                                                         $apply$O(
                                                                                                         ){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
final boolean t63946 =
              state;
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
return t63946;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
final public x10.util.concurrent.Latch
                                                                                                         x10$util$concurrent$Latch$$x10$util$concurrent$Latch$this(
                                                                                                         ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
return x10.util.concurrent.Latch.this;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
final public void
                                                                                                         __fieldInitializers63899(
                                                                                                         ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Latch.x10"
this.state = false;
        }
        
        public void
          x10$util$concurrent$Monitor$release$S(
          ){
            super.release();
        }
        
        public void
          x10$util$concurrent$Monitor$await$S(
          ){
            super.await();
        }
        
        }
        