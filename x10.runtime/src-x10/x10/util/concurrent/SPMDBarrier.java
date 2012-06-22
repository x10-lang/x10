package x10.util.concurrent;

@x10.core.X10Generated final public class SPMDBarrier extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, SPMDBarrier.class);
    
    public static final x10.rtt.RuntimeType<SPMDBarrier> $RTT = x10.rtt.NamedType.<SPMDBarrier> make(
    "x10.util.concurrent.SPMDBarrier", /* base class */SPMDBarrier.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(SPMDBarrier $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + SPMDBarrier.class + " calling"); } 
        x10.core.concurrent.AtomicInteger alive = (x10.core.concurrent.AtomicInteger) $deserializer.readRef();
        $_obj.alive = alive;
        x10.array.Array workers = (x10.array.Array) $deserializer.readRef();
        $_obj.workers = workers;
        $_obj.index = $deserializer.readInt();
        $_obj.count = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        SPMDBarrier $_obj = new SPMDBarrier((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (alive instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.alive);
        } else {
        $serializer.write(this.alive);
        }
        if (workers instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.workers);
        } else {
        $serializer.write(this.workers);
        }
        $serializer.write(this.index);
        $serializer.write(this.count);
        
    }
    
    // constructor just for allocation
    public SPMDBarrier(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
public int count;
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
public x10.core.concurrent.AtomicInteger alive;
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
public x10.array.Array<x10.lang.Runtime.Worker> workers;
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
public int index;
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
// creation method for java code (1-phase java constructor)
        public SPMDBarrier(final int count){this((java.lang.System[]) null);
                                                $init(count);}
        
        // constructor for non-virtual call
        final public x10.util.concurrent.SPMDBarrier x10$util$concurrent$SPMDBarrier$$init$S(final int count) { {
                                                                                                                       
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"

                                                                                                                       
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
this.count = count;
                                                                                                                       
                                                                                                                       
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
this.__fieldInitializers64166();
                                                                                                                       
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
assert ((x10.lang.Runtime.NTHREADS) >= (((int)(count)))): "SPMDBarrier constructor invoked with task count greater than Runtime.NTHREADS";
                                                                                                                   }
                                                                                                                   return this;
                                                                                                                   }
        
        // constructor
        public x10.util.concurrent.SPMDBarrier $init(final int count){return x10$util$concurrent$SPMDBarrier$$init$S(count);}
        
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
public void
                                                                                                               register(
                                                                                                               ){
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.array.Array<x10.lang.Runtime.Worker> t64202 =
              ((x10.array.Array)(workers));
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.util.concurrent.SPMDBarrier x64197 =
              ((x10.util.concurrent.SPMDBarrier)(this));
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
;
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64199 =
              x64197.
                index;
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64200 =
              ((t64199) + (((int)(1))));
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64201 =
              x64197.index = t64200;
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64203 =
              ((t64201) - (((int)(1))));
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.lang.Runtime.Worker t64204 =
              x10.lang.Runtime.worker();
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)t64202).$set__1x10$array$Array$$T$G((int)(t64203),
                                                                                                                                                                                                ((x10.lang.Runtime.Worker)(t64204)));
        }
        
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
public void
                                                                                                               advance(
                                                                                                               ){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.core.concurrent.AtomicInteger t64205 =
              ((x10.core.concurrent.AtomicInteger)(alive));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64206 =
              t64205.decrementAndGet();
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final boolean t64222 =
              ((int) t64206) ==
            ((int) 0);
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
if (t64222) {
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.core.concurrent.AtomicInteger t64207 =
                  ((x10.core.concurrent.AtomicInteger)(alive));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64208 =
                  count;
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
t64207.set(((int)(t64208)));
                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.lang.Runtime.Worker me =
                  x10.lang.Runtime.worker();
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
int i64236 =
                  0;
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
for (;
                                                                                                                          true;
                                                                                                                          ) {
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64237 =
                      i64236;
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64238 =
                      count;
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final boolean t64239 =
                      ((t64237) < (((int)(t64238))));
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
if (!(t64239)) {
                        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
break;
                    }
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.array.Array<x10.lang.Runtime.Worker> t64227 =
                      ((x10.array.Array)(workers));
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64228 =
                      i64236;
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.lang.Runtime.Worker t64229 =
                      ((x10.array.Array<x10.lang.Runtime.Worker>)t64227).$apply$G((int)(t64228));
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final boolean t64230 =
                      (!x10.rtt.Equality.equalsequals((t64229),(me)));
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
if (t64230) {
                        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.array.Array<x10.lang.Runtime.Worker> t64231 =
                          ((x10.array.Array)(workers));
                        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64232 =
                          i64236;
                        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.lang.Runtime.Worker t64233 =
                          ((x10.array.Array<x10.lang.Runtime.Worker>)t64231).$apply$G((int)(t64232));
                        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
t64233.unpark();
                    }
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64234 =
                      i64236;
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64235 =
                      ((t64234) + (((int)(1))));
                    
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
i64236 = t64235;
                }
            } else {
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
x10.lang.Runtime.Worker.park();
            }
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final public x10.util.concurrent.SPMDBarrier
                                                                                                               x10$util$concurrent$SPMDBarrier$$x10$util$concurrent$SPMDBarrier$this(
                                                                                                               ){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
return x10.util.concurrent.SPMDBarrier.this;
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final public void
                                                                                                               __fieldInitializers64166(
                                                                                                               ){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64223 =
              count;
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.core.concurrent.AtomicInteger t64224 =
              ((x10.core.concurrent.AtomicInteger)(new x10.core.concurrent.AtomicInteger(((int)(t64223)))));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
this.alive = t64224;
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final int t64225 =
              count;
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
final x10.array.Array<x10.lang.Runtime.Worker> t64226 =
              ((x10.array.Array)(new x10.array.Array<x10.lang.Runtime.Worker>((java.lang.System[]) null, x10.lang.Runtime.Worker.$RTT).$init(((int)(t64225)))));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
this.workers = ((x10.array.Array)(t64226));
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/SPMDBarrier.x10"
this.index = 0;
        }
    
}
