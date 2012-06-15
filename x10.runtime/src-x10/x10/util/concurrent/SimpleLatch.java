package x10.util.concurrent;


@x10.core.X10Generated public class SimpleLatch extends x10.util.concurrent.Lock implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SimpleLatch.class);
    
    public static final x10.rtt.RuntimeType<SimpleLatch> $RTT = x10.rtt.NamedType.<SimpleLatch> make(
    "x10.util.concurrent.SimpleLatch", /* base class */SimpleLatch.class
    , /* parents */ new x10.rtt.Type[] {x10.util.concurrent.Lock.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new SimpleLatch($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(SimpleLatch $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + SimpleLatch.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        SimpleLatch $_obj = new SimpleLatch((java.lang.System[]) null);
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
    public SimpleLatch(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public SimpleLatch() {super();
                                                                                                                                        {
                                                                                                                                           
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"

                                                                                                                                           
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.__fieldInitializers64337();
                                                                                                                                       }}
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public x10.io.SerialData
                                                                                                               serialize(
                                                                                                               ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final java.lang.String t64422 =
              x10.rtt.Types.typeName(this);
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final java.lang.String t64423 =
              (("Cannot serialize ") + (t64422));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final x10.lang.UnsupportedOperationException t64424 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t64423)));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
throw t64424;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public SimpleLatch(final x10.io.SerialData id$187) {super();
                                                                                                                                                                      {
                                                                                                                                                                         
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"

                                                                                                                                                                         
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.__fieldInitializers64337();
                                                                                                                                                                         
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                                     }}
        
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public x10.lang.Runtime.Worker worker;
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public boolean state;
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public void
                                                                                                               await(
                                                                                                               ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final boolean t64425 =
              state;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
if (t64425) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
return;
            }
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.lock();
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
x10.lang.Runtime.increaseParallelism();
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final x10.lang.Runtime.Worker t64426 =
              x10.lang.Runtime.worker();
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.worker = ((x10.lang.Runtime.Worker)(t64426));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
while (true) {
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final boolean t64427 =
                  state;
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final boolean t64428 =
                  !(t64427);
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
if (!(t64428)) {
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
break;
                }
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.unlock();
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
x10.lang.Runtime.Worker.park();
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.lock();
            }
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.unlock();
        }
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public void
                                                                                                               release(
                                                                                                               ){
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.lock();
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.state = true;
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final x10.lang.Runtime.Worker t64429 =
              ((x10.lang.Runtime.Worker)(worker));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final boolean t64431 =
              ((t64429) != (null));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
if (t64431) {
                
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
x10.lang.Runtime.decreaseParallelism((int)(1));
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final x10.lang.Runtime.Worker t64430 =
                  ((x10.lang.Runtime.Worker)(worker));
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
t64430.unpark();
            }
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.unlock();
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
public boolean
                                                                                                               $apply$O(
                                                                                                               ){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final boolean t64432 =
              state;
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
return t64432;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final public x10.util.concurrent.SimpleLatch
                                                                                                               x10$util$concurrent$SimpleLatch$$x10$util$concurrent$SimpleLatch$this(
                                                                                                               ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
return x10.util.concurrent.SimpleLatch.this;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
final public void
                                                                                                               __fieldInitializers64337(
                                                                                                               ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.worker = null;
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleLatch.x10"
this.state = false;
        }
        
        }
        