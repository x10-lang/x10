package x10.util.concurrent;


@x10.core.X10Generated public class SimpleIntLatch extends x10.util.concurrent.Lock implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SimpleIntLatch.class);
    
    public static final x10.rtt.RuntimeType<SimpleIntLatch> $RTT = x10.rtt.NamedType.<SimpleIntLatch> make(
    "x10.util.concurrent.SimpleIntLatch", /* base class */SimpleIntLatch.class
    , /* parents */ new x10.rtt.Type[] {x10.util.concurrent.Lock.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new SimpleIntLatch($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(SimpleIntLatch $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + SimpleIntLatch.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        SimpleIntLatch $_obj = new SimpleIntLatch((java.lang.System[]) null);
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
    public SimpleIntLatch(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public SimpleIntLatch() {super();
                                                                                                                                              {
                                                                                                                                                 
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"

                                                                                                                                                 
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.__fieldInitializers64240();
                                                                                                                                             }}
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public x10.io.SerialData
                                                                                                                  serialize(
                                                                                                                  ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final java.lang.String t64325 =
              x10.rtt.Types.typeName(this);
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final java.lang.String t64326 =
              (("Cannot serialize ") + (t64325));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final x10.lang.UnsupportedOperationException t64327 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t64326)));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
throw t64327;
        }
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public SimpleIntLatch(final x10.io.SerialData id$186) {super();
                                                                                                                                                                            {
                                                                                                                                                                               
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"

                                                                                                                                                                               
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.__fieldInitializers64240();
                                                                                                                                                                               
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                                           }}
        
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public x10.lang.Runtime.Worker worker;
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public int value;
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public void
                                                                                                                  await(
                                                                                                                  ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final int t64328 =
              value;
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final boolean t64329 =
              ((int) t64328) !=
            ((int) 0);
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
if (t64329) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
return;
            }
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.lock();
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
x10.lang.Runtime.increaseParallelism();
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final x10.lang.Runtime.Worker t64330 =
              x10.lang.Runtime.worker();
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.worker = ((x10.lang.Runtime.Worker)(t64330));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
while (true) {
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final int t64331 =
                  value;
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final boolean t64332 =
                  ((int) t64331) ==
                ((int) 0);
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
if (!(t64332)) {
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
break;
                }
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.unlock();
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
x10.lang.Runtime.Worker.park();
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.lock();
            }
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.unlock();
        }
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public void
                                                                                                                  $set(
                                                                                                                  final int v){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.set((int)(v));
        }
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public void
                                                                                                                  set(
                                                                                                                  final int v){
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.lock();
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.value = v;
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final x10.lang.Runtime.Worker t64333 =
              ((x10.lang.Runtime.Worker)(worker));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final boolean t64335 =
              ((t64333) != (null));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
if (t64335) {
                
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
x10.lang.Runtime.decreaseParallelism((int)(1));
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final x10.lang.Runtime.Worker t64334 =
                  ((x10.lang.Runtime.Worker)(worker));
                
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
t64334.unpark();
            }
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.unlock();
        }
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
public int
                                                                                                                  $apply$O(
                                                                                                                  ){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final int t64336 =
              value;
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
return t64336;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final public x10.util.concurrent.SimpleIntLatch
                                                                                                                  x10$util$concurrent$SimpleIntLatch$$x10$util$concurrent$SimpleIntLatch$this(
                                                                                                                  ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
return x10.util.concurrent.SimpleIntLatch.this;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
final public void
                                                                                                                  __fieldInitializers64240(
                                                                                                                  ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.worker = null;
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SimpleIntLatch.x10"
this.value = 0;
        }
        
        }
        