package x10.util.concurrent;


@x10.core.X10Generated public class IntLatch extends x10.util.concurrent.Monitor implements x10.core.fun.Fun_0_0
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, IntLatch.class);
    
    public static final x10.rtt.RuntimeType<IntLatch> $RTT = x10.rtt.NamedType.<IntLatch> make(
    "x10.util.concurrent.IntLatch", /* base class */IntLatch.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.Types.INT), x10.util.concurrent.Monitor.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new IntLatch($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    // default deserialization constructor
    public IntLatch(final x10.io.SerialData a) { super(a); {
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"

        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.__fieldInitializers63851();
    }}
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(IntLatch $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + IntLatch.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        IntLatch $_obj = new IntLatch((java.lang.System[]) null);
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
    public IntLatch(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    // bridge for method abstract public ()=>U.operator()(){}:U
    public x10.core.Int
      $apply$G(){return x10.core.Int.$box($apply$O());}
    
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
public IntLatch() {super();
                                                                                                                                  {
                                                                                                                                     
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"

                                                                                                                                     
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.__fieldInitializers63851();
                                                                                                                                 }}
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
public IntLatch(final java.lang.Object id$182) {super();
                                                                                                                                                               {
                                                                                                                                                                  
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"

                                                                                                                                                                  
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.__fieldInitializers63851();
                                                                                                                                                                  
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                              }}
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
public int value;
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
public void
                                                                                                            $set(
                                                                                                            final int i){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.set((int)(i));
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
public void
                                                                                                            set(
                                                                                                            final int i){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.lock();
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.value = i;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
super.release();
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
public void
                                                                                                            await(
                                                                                                            ){
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
x10.lang.Runtime.ensureNotInAtomic();
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
final int t63894 =
              value;
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
final boolean t63897 =
              ((int) t63894) ==
            ((int) 0);
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
if (t63897) {
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.lock();
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
while (true) {
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
final int t63895 =
                      value;
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
final boolean t63896 =
                      ((int) t63895) ==
                    ((int) 0);
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
if (!(t63896)) {
                        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
break;
                    }
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
super.await();
                }
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.unlock();
            }
        }
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
public int
                                                                                                            $apply$O(
                                                                                                            ){
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
final int t63898 =
              value;
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
return t63898;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
final public x10.util.concurrent.IntLatch
                                                                                                            x10$util$concurrent$IntLatch$$x10$util$concurrent$IntLatch$this(
                                                                                                            ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
return x10.util.concurrent.IntLatch.this;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
final public void
                                                                                                            __fieldInitializers63851(
                                                                                                            ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/IntLatch.x10"
this.value = 0;
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
        