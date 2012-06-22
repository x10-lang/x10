package x10.util.concurrent;


@x10.core.X10Generated public class Lock extends x10.core.Ref implements x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Lock.class);
    
    public static final x10.rtt.RuntimeType<Lock> $RTT = x10.rtt.NamedType.<Lock> make(
    "x10.util.concurrent.Lock", /* base class */Lock.class
    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new Lock($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Lock $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Lock.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Lock $_obj = new Lock((java.lang.System[]) null);
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
    public Lock(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public java.util.concurrent.locks.ReentrantLock __NATIVE_FIELD__;
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public Lock(final java.util.concurrent.locks.ReentrantLock id0) {super();
                                                                                                                                                                            {
                                                                                                                                                                               
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
this.__NATIVE_FIELD__ = id0;
                                                                                                                                                                           }}
        
        
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public Lock() {super();
                                                                                                                          {
                                                                                                                             
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
this.__NATIVE_FIELD__ = new java.util.concurrent.locks.ReentrantLock();
                                                                                                                         }}
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public void
                                                                                                        lock(
                                                                                                        ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final java.util.concurrent.locks.ReentrantLock t64031 =
              this.
                __NATIVE_FIELD__;
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
t64031.lock();
        }
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public boolean
                                                                                                        tryLock(
                                                                                                        ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final java.util.concurrent.locks.ReentrantLock t64032 =
              this.
                __NATIVE_FIELD__;
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final boolean t64033 =
              t64032.tryLock();
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
return t64033;
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public void
                                                                                                        unlock(
                                                                                                        ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final java.util.concurrent.locks.ReentrantLock t64034 =
              this.
                __NATIVE_FIELD__;
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
t64034.unlock();
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public int
                                                                                                        getHoldCount(
                                                                                                        ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final java.util.concurrent.locks.ReentrantLock t64035 =
              this.
                __NATIVE_FIELD__;
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final int t64036 =
              t64035.getHoldCount();
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
return t64036;
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public x10.io.SerialData
                                                                                                        serialize(
                                                                                                        ){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final java.lang.String t64037 =
              x10.rtt.Types.typeName(this);
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final java.lang.String t64038 =
              (("Cannot serialize ") + (t64037));
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final x10.lang.UnsupportedOperationException t64039 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t64038)));
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
throw t64039;
        }
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
public Lock(final x10.io.SerialData id$184) {super();
                                                                                                                                                        {
                                                                                                                                                           
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"

                                                                                                                                                           
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                       }}
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
final public x10.util.concurrent.Lock
                                                                                                        x10$util$concurrent$Lock$$x10$util$concurrent$Lock$this(
                                                                                                        ){
            
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Lock.x10"
return x10.util.concurrent.Lock.this;
        }
        
        }
        