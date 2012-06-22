package x10.util.concurrent;


@x10.core.X10Generated public class Monitor extends x10.util.concurrent.Lock implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Monitor.class);
    
    public static final x10.rtt.RuntimeType<Monitor> $RTT = x10.rtt.NamedType.<Monitor> make(
    "x10.util.concurrent.Monitor", /* base class */Monitor.class
    , /* parents */ new x10.rtt.Type[] {x10.util.concurrent.Lock.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new Monitor($$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Monitor $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Monitor.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Monitor $_obj = new Monitor((java.lang.System[]) null);
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
    public Monitor(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
public Monitor() {super();
                                                                                                                                {
                                                                                                                                   
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"

                                                                                                                                   
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
this.__fieldInitializers64040();
                                                                                                                               }}
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
public x10.io.SerialData
                                                                                                           serialize(
                                                                                                           ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final java.lang.String t64143 =
              x10.rtt.Types.typeName(this);
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final java.lang.String t64144 =
              (("Cannot serialize ") + (t64143));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.lang.UnsupportedOperationException t64145 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t64144)));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
throw t64145;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
public Monitor(final x10.io.SerialData id$185) {super();
                                                                                                                                                              {
                                                                                                                                                                 
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"

                                                                                                                                                                 
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
this.__fieldInitializers64040();
                                                                                                                                                                 
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                             }}
        
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
/**
     * Parked workers
     */
        public x10.util.Stack<x10.lang.Runtime.Worker> workers;
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
public void
                                                                                                           await(
                                                                                                           ){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
x10.lang.Runtime.increaseParallelism();
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.lang.Runtime.Worker worker =
              x10.lang.Runtime.worker();
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.util.Stack<x10.lang.Runtime.Worker> t64146 =
              ((x10.util.Stack)(workers));
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
((x10.util.Stack<x10.lang.Runtime.Worker>)t64146).push__0x10$util$Stack$$T$O(((x10.lang.Runtime.Worker)(worker)));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
while (true) {
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.util.Stack<x10.lang.Runtime.Worker> t64147 =
                  ((x10.util.Stack)(workers));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final boolean t64148 =
                  ((x10.util.ArrayList<x10.lang.Runtime.Worker>)t64147).contains__0x10$util$ArrayList$$T$O(((x10.lang.Runtime.Worker)(worker)));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
if (!(t64148)) {
                    
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
break;
                }
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
super.unlock();
                
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
x10.lang.Runtime.Worker.park();
                
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
super.lock();
            }
        }
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
public void
                                                                                                           release(
                                                                                                           ){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.util.Stack<x10.lang.Runtime.Worker> t64149 =
              ((x10.util.Stack)(workers));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final int size =
              ((x10.util.ArrayList<x10.lang.Runtime.Worker>)t64149).size$O();
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final boolean t64157 =
              ((size) > (((int)(0))));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
if (t64157) {
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
x10.lang.Runtime.decreaseParallelism((int)(size));
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
int i64163 =
                  0;
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
for (;
                                                                                                                      true;
                                                                                                                      ) {
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final int t64164 =
                      i64163;
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final boolean t64165 =
                      ((t64164) < (((int)(size))));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
if (!(t64165)) {
                        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
break;
                    }
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.util.Stack<x10.lang.Runtime.Worker> t64159 =
                      ((x10.util.Stack)(workers));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.lang.Runtime.Worker t64160 =
                      ((x10.util.Stack<x10.lang.Runtime.Worker>)t64159).pop$G();
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
t64160.unpark();
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final int t64161 =
                      i64163;
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final int t64162 =
                      ((t64161) + (((int)(1))));
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
i64163 = t64162;
                }
            }
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
super.unlock();
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final public x10.util.concurrent.Monitor
                                                                                                           x10$util$concurrent$Monitor$$x10$util$concurrent$Monitor$this(
                                                                                                           ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
return x10.util.concurrent.Monitor.this;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final public void
                                                                                                           __fieldInitializers64040(
                                                                                                           ){
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
final x10.util.Stack<x10.lang.Runtime.Worker> t64158 =
              ((x10.util.Stack)(new x10.util.Stack<x10.lang.Runtime.Worker>((java.lang.System[]) null, x10.lang.Runtime.Worker.$RTT).$init()));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Monitor.x10"
this.workers = ((x10.util.Stack)(t64158));
        }
        
        public void
          x10$util$concurrent$Lock$unlock$S(
          ){
            super.unlock();
        }
        
        public void
          x10$util$concurrent$Lock$lock$S(
          ){
            super.lock();
        }
        
        }
        