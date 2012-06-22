package x10.compiler.ws;


@x10.core.X10Generated final public class Worker extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Worker.class);
    
    public static final x10.rtt.RuntimeType<Worker> $RTT = x10.rtt.NamedType.<Worker> make(
    "x10.compiler.ws.Worker", /* base class */Worker.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Worker $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Worker.class + " calling"); } 
        x10.array.Array workers = (x10.array.Array) $deserializer.readRef();
        $_obj.workers = workers;
        x10.util.Random random = (x10.util.Random) $deserializer.readRef();
        $_obj.random = random;
        $_obj.id = $deserializer.readInt();
        x10.runtime.impl.java.Deque deque = (x10.runtime.impl.java.Deque) $deserializer.readRef();
        $_obj.deque = deque;
        x10.runtime.impl.java.Deque fifo = (x10.runtime.impl.java.Deque) $deserializer.readRef();
        $_obj.fifo = fifo;
        x10.util.concurrent.Lock lock = (x10.util.concurrent.Lock) $deserializer.readRef();
        $_obj.lock = lock;
        x10.core.X10Throwable throwable = (x10.core.X10Throwable) $deserializer.readRef();
        $_obj.throwable = throwable;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Worker $_obj = new Worker((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (workers instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.workers);
        } else {
        $serializer.write(this.workers);
        }
        if (random instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.random);
        } else {
        $serializer.write(this.random);
        }
        $serializer.write(this.id);
        if (deque instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.deque);
        } else {
        $serializer.write(this.deque);
        }
        if (fifo instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.fifo);
        } else {
        $serializer.write(this.fifo);
        }
        if (lock instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lock);
        } else {
        $serializer.write(this.lock);
        }
        if (throwable instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.throwable);
        } else {
        $serializer.write(this.throwable);
        }
        
    }
    
    // constructor just for allocation
    public Worker(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 12 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public x10.array.Array<x10.compiler.ws.Worker> workers;
        
//#line 13 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public x10.util.Random random;
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public int id;
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public x10.runtime.impl.java.Deque deque;
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public x10.runtime.impl.java.Deque fifo;
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public x10.util.concurrent.Lock lock;
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public x10.core.X10Throwable throwable;
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
// creation method for java code (1-phase java constructor)
        public Worker(final int i,
                      final x10.array.Array<x10.compiler.ws.Worker> workers, __1$1x10$compiler$ws$Worker$2 $dummy){this((java.lang.System[]) null);
                                                                                                                       $init(i,workers, (x10.compiler.ws.Worker.__1$1x10$compiler$ws$Worker$2) null);}
        
        // constructor for non-virtual call
        final public x10.compiler.ws.Worker x10$compiler$ws$Worker$$init$S(final int i,
                                                                           final x10.array.Array<x10.compiler.ws.Worker> workers, __1$1x10$compiler$ws$Worker$2 $dummy) { {
                                                                                                                                                                                 
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"

                                                                                                                                                                                 
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"

                                                                                                                                                                                 
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.__fieldInitializers49008();
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49163 =
                                                                                                                                                                                   ((i) << (((int)(8))));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49164 =
                                                                                                                                                                                   ((i) + (((int)(t49163))));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49165 =
                                                                                                                                                                                   ((i) << (((int)(16))));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49166 =
                                                                                                                                                                                   ((t49164) + (((int)(t49165))));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49167 =
                                                                                                                                                                                   ((i) << (((int)(24))));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49168 =
                                                                                                                                                                                   ((t49166) + (((int)(t49167))));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final long t49169 =
                                                                                                                                                                                   ((long)(((int)(t49168))));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.Random t49170 =
                                                                                                                                                                                   ((x10.util.Random)(new x10.util.Random((java.lang.System[]) null).$init(t49169)));
                                                                                                                                                                                 
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.random = ((x10.util.Random)(t49170));
                                                                                                                                                                                 
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.id = i;
                                                                                                                                                                                 
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.workers = ((x10.array.Array)(workers));
                                                                                                                                                                             }
                                                                                                                                                                             return this;
                                                                                                                                                                             }
        
        // constructor
        public x10.compiler.ws.Worker $init(final int i,
                                            final x10.array.Array<x10.compiler.ws.Worker> workers, __1$1x10$compiler$ws$Worker$2 $dummy){return x10$compiler$ws$Worker$$init$S(i,workers, $dummy);}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public void
                                                                                                      migrate(
                                                                                                      ){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.compiler.ws.RegularFrame k =
               null;
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Lock t49171 =
              ((x10.util.concurrent.Lock)(lock));
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49171.lock();
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
while (true) {
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49172 =
                  ((x10.runtime.impl.java.Deque)(deque));
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49173 =
                  t49172.steal();
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.RegularFrame t49174 =
                  ((x10.compiler.ws.RegularFrame) t49173);
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.RegularFrame t49175 =
                  k = t49174;
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49183 =
                  ((null) != (t49175));
                
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (!(t49183)) {
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Monitor t49276 =
                  ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49276.lock();
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.RegularFrame t49277 =
                  k;
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.FinishFrame x49278 =
                  ((x10.compiler.ws.FinishFrame)(t49277.
                                                   ff));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
;
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49279 =
                  x49278.
                    asyncs;
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49280 =
                  ((t49279) + (((int)(1))));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x49278.asyncs = t49280;
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Monitor t49281 =
                  ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49281.unlock();
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49282 =
                  ((x10.runtime.impl.java.Deque)(fifo));
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.RegularFrame t49283 =
                  k;
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49282.push(((x10.core.RefI)(t49283)));
            }
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Lock t49184 =
              ((x10.util.concurrent.Lock)(lock));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49184.unlock();
        }
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public void
                                                                                                      run(
                                                                                                      ){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
try {try {{
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
while (true) {
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI k =
                      this.find();
                    
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49185 =
                      ((null) == (k));
                    
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49185) {
                        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
return;
                    }
                    
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
try {try {{
                        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49186 =
                          ((x10.compiler.ws.Frame) k);
                        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.unroll(((x10.compiler.ws.Frame)(t49186)));
                    }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.compiler.Abort id$89) {
                        
                    }
                }
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable t) {
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final java.lang.String t49187 =
                  (("Uncaught exception at place ") + (x10.lang.Runtime.home()));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final java.lang.String t49188 =
                  ((t49187) + (" in WS worker: "));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final java.lang.String t49189 =
                  ((t49188) + (t));
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
java.lang.System.err.println(t49189);
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t.printStackTrace();
            }
        }
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public x10.core.RefI
                                                                                                      find(
                                                                                                      ){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.core.RefI k =
               null;
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49190 =
              ((x10.runtime.impl.java.Deque)(fifo));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49191 =
              t49190.steal();
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
k = t49191;
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
while (true) {
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49192 =
                  k;
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49218 =
                  ((null) == (t49192));
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (!(t49218)) {
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49284 =
                  x10.lang.Runtime.wsEnded$O();
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49284) {
                    
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
return null;
                }
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.Random t49285 =
                  ((x10.util.Random)(random));
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49286 =
                  x10.lang.Runtime.NTHREADS;
                
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int rand49287 =
                  t49285.nextInt$O((int)(t49286));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.array.Array<x10.compiler.ws.Worker> t49288 =
                  ((x10.array.Array)(workers));
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker victim49289 =
                  ((x10.array.Array<x10.compiler.ws.Worker>)t49288).$apply$G((int)(rand49287));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49290 =
                  ((x10.runtime.impl.java.Deque)(victim49289.
                                                   fifo));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49291 =
                  t49290.steal();
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
k = t49291;
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49292 =
                  k;
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49293 =
                  ((null) != (t49292));
                
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49293) {
                    
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Lock t49294 =
                  ((x10.util.concurrent.Lock)(victim49289.
                                                lock));
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49295 =
                  t49294.tryLock();
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49295) {
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49296 =
                      ((x10.runtime.impl.java.Deque)(victim49289.
                                                       deque));
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49297 =
                      t49296.steal();
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
k = t49297;
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49298 =
                      k;
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49299 =
                      ((null) != (t49298));
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49299) {
                        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49300 =
                          k;
                        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.compiler.ws.RegularFrame r49301 =
                          ((x10.compiler.ws.RegularFrame) t49300);
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Monitor t49302 =
                          ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49302.lock();
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.RegularFrame t49303 =
                          r49301;
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.FinishFrame x49304 =
                          ((x10.compiler.ws.FinishFrame)(t49303.
                                                           ff));
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
;
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49305 =
                          x49304.
                            asyncs;
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49306 =
                          ((t49305) + (((int)(1))));
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x49304.asyncs = t49306;
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Monitor t49307 =
                          ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49307.unlock();
                    }
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Lock t49308 =
                      ((x10.util.concurrent.Lock)(victim49289.
                                                    lock));
                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49308.unlock();
                }
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49309 =
                  k;
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49310 =
                  ((null) != (t49309));
                
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49310) {
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.runtime.impl.java.Runtime.eventProbe();
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49311 =
                  ((x10.runtime.impl.java.Deque)(fifo));
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49312 =
                  t49311.steal();
                
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
k = t49312;
            }
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.RefI t49219 =
              k;
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
return t49219;
        }
        
        
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public void
                                                                                                      unroll(
                                                                                                      x10.compiler.ws.Frame frame){
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.compiler.ws.Frame up =
               null;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
while (true) {
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49220 =
                  frame;
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49220.wrapResume(((x10.compiler.ws.Worker)(this)));
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49221 =
                  frame;
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49222 =
                  ((x10.compiler.ws.Frame)(t49221.
                                             up));
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
up = t49222;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49223 =
                  up;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49224 =
                  frame;
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49223.wrapBack(((x10.compiler.ws.Worker)(this)),
                                                                                                                            ((x10.compiler.ws.Frame)(t49224)));
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49225 =
                  frame;
                
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.deallocObject(((x10.core.RefI)(t49225)));
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Frame t49226 =
                  up;
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
frame = t49226;
            }
        }
        
        
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public static void
                                                                                                      wsRunAsync(
                                                                                                      final int id,
                                                                                                      final x10.core.fun.VoidFun_0_0 body){
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49227 =
              x10.lang.Runtime.hereInt$O();
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49228 =
              ((int) id) ==
            ((int) t49227);
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49228) {
                
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.fun.VoidFun_0_0 copy =
                  ((x10.core.fun.VoidFun_0_0)(x10.runtime.impl.java.Runtime.<x10.core.fun.VoidFun_0_0>deepCopy(body)));
                
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
((x10.core.fun.VoidFun_0_0)copy).$apply();
                
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(copy)));
            } else {
                
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(id)), body);
            }
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(body)));
        }
        
        
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public static void
                                                                                                       runAsyncAt(
                                                                                                       final x10.lang.Place place,
                                                                                                       final x10.compiler.ws.RegularFrame frame){
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.fun.VoidFun_0_0 body =
              ((x10.core.fun.VoidFun_0_0)(new x10.compiler.ws.Worker.$Closure$94(frame)));
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49230 =
              place.
                id;
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.compiler.ws.Worker.wsRunAsync((int)(t49230),
                                                                                                                                           ((x10.core.fun.VoidFun_0_0)(body)));
        }
        
        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public static void
                                                                                                       runAt(
                                                                                                       final x10.lang.Place place,
                                                                                                       final x10.compiler.ws.RegularFrame frame){
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.fun.VoidFun_0_0 body =
              ((x10.core.fun.VoidFun_0_0)(new x10.compiler.ws.Worker.$Closure$95(frame)));
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49232 =
              place.
                id;
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.compiler.ws.Worker.wsRunAsync((int)(t49232),
                                                                                                                                           ((x10.core.fun.VoidFun_0_0)(body)));
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.Abort t49233 =
              ((x10.compiler.Abort)(x10.compiler.Abort.getInitialized$ABORT()));
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
throw t49233;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public static void
                                                                                                       stop(
                                                                                                       ){
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.fun.VoidFun_0_0 body =
              ((x10.core.fun.VoidFun_0_0)(new x10.compiler.ws.Worker.$Closure$96()));
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
int i49316 =
              1;
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49317 =
                  i49316;
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49318 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49319 =
                  ((t49317) < (((int)(t49318))));
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (!(t49319)) {
                    
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49313 =
                  i49316;
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(t49313)), body);
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49314 =
                  i49316;
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49315 =
                  ((t49314) + (((int)(1))));
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
i49316 = t49315;
            }
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(body)));
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.wsEnd();
        }
        
        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public static x10.compiler.ws.Worker
                                                                                                       startHere(
                                                                                                       ){
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.wsInit();
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49241 =
              x10.lang.Runtime.NTHREADS;
            
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.array.Array<x10.compiler.ws.Worker> workers =
              ((x10.array.Array)(new x10.array.Array<x10.compiler.ws.Worker>((java.lang.System[]) null, x10.compiler.ws.Worker.$RTT).$init(((int)(t49241)))));
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
int i49330 =
              0;
            
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49331 =
                  i49330;
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49332 =
                  x10.lang.Runtime.NTHREADS;
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49333 =
                  ((t49331) < (((int)(t49332))));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (!(t49333)) {
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49320 =
                  i49330;
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49321 =
                  i49330;
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker t49322 =
                  ((x10.compiler.ws.Worker)(new x10.compiler.ws.Worker((java.lang.System[]) null).$init(t49321,
                                                                                                        ((x10.array.Array)(workers)), (x10.compiler.ws.Worker.__1$1x10$compiler$ws$Worker$2) null)));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
((x10.array.Array<x10.compiler.ws.Worker>)workers).$set__1x10$array$Array$$T$G((int)(t49320),
                                                                                                                                                                                            ((x10.compiler.ws.Worker)(t49322)));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49323 =
                  i49330;
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49324 =
                  ((t49323) + (((int)(1))));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
i49330 = t49324;
            }
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker t49251 =
              ((x10.array.Array<x10.compiler.ws.Worker>)workers).$apply$G((int)(0));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49252 =
              x10.lang.Runtime.wsFIFO();
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49251.fifo = ((x10.runtime.impl.java.Deque)(t49252));
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
int i49334 =
              1;
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49335 =
                  i49334;
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49336 =
                  x10.lang.Runtime.NTHREADS;
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49337 =
                  ((t49335) < (((int)(t49336))));
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (!(t49337)) {
                    
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49325 =
                  i49334;
                
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker worker49326 =
                  ((x10.array.Array<x10.compiler.ws.Worker>)workers).$apply$G((int)(t49325));
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.runAsync(((x10.core.fun.VoidFun_0_0)(new x10.compiler.ws.Worker.$Closure$97(worker49326))));
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49328 =
                  i49334;
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49329 =
                  ((t49328) + (((int)(1))));
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
i49334 = t49329;
            }
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker t49261 =
              ((x10.array.Array<x10.compiler.ws.Worker>)workers).$apply$G((int)(0));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
return t49261;
        }
        
        
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public static x10.compiler.ws.Worker
                                                                                                       start(
                                                                                                       ){
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker worker =
              x10.compiler.ws.Worker.startHere();
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
int i49343 =
              1;
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49344 =
                  i49343;
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49345 =
                  x10.lang.Place.getInitialized$MAX_PLACES();
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49346 =
                  ((t49344) < (((int)(t49345))));
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (!(t49346)) {
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
break;
                }
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49338 =
                  i49343;
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.lang.Place p49339 =
                  ((x10.lang.Place)(x10.lang.Place.place((int)(t49338))));
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(p49339)),
                                                                                                                                       ((x10.core.fun.VoidFun_0_0)(new x10.compiler.ws.Worker.$Closure$98())));
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49341 =
                  i49343;
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final int t49342 =
                  ((t49341) + (((int)(1))));
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
i49343 = t49342;
            }
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
return worker;
        }
        
        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public static void
                                                                                                       main(
                                                                                                       final x10.compiler.ws.MainFrame frame){
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker worker =
              ((x10.compiler.ws.Worker)(x10.compiler.ws.Worker.start()));
            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.FinishFrame ff =
              ((x10.compiler.ws.FinishFrame)(frame.
                                               ff));
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
boolean finalize =
              true;
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
try {try {{
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
frame.fast(((x10.compiler.ws.Worker)(worker)));
            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.compiler.Abort t) {
                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
finalize = false;
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
worker.run();
            }catch (final x10.core.X10Throwable t) {
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
ff.caught(((x10.core.X10Throwable)(t)));
            }finally {{
                 
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49270 =
                   finalize;
                 
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49270) {
                     
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.compiler.ws.Worker.stop();
                 }
             }}
            
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
ff.check();
            }
        
        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
public void
                                                                                                       rethrow(
                                                                                                       ){
            
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.X10Throwable t49271 =
              ((x10.core.X10Throwable)(throwable));
            
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final boolean t49272 =
              ((null) != (t49271));
            
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
if (t49272) {
                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.core.X10Throwable t =
                  ((x10.core.X10Throwable)(throwable));
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.throwable = null;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
throw t;
            }
        }
        
        
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final public x10.compiler.ws.Worker
                                                                                                      x10$compiler$ws$Worker$$x10$compiler$ws$Worker$this(
                                                                                                      ){
            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
return x10.compiler.ws.Worker.this;
        }
        
        
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final public void
                                                                                                      __fieldInitializers49008(
                                                                                                      ){
            
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49273 =
              ((x10.runtime.impl.java.Deque)(new x10.runtime.impl.java.Deque()));
            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.deque = ((x10.runtime.impl.java.Deque)(t49273));
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49274 =
              ((x10.runtime.impl.java.Deque)(deque));
            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.fifo = t49274;
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.util.concurrent.Lock t49275 =
              ((x10.util.concurrent.Lock)(new x10.util.concurrent.Lock()));
            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.lock = ((x10.util.concurrent.Lock)(t49275));
            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.throwable = null;
        }
        
        @x10.core.X10Generated public static class $Closure$94 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$94.class);
            
            public static final x10.rtt.RuntimeType<$Closure$94> $RTT = x10.rtt.StaticVoidFunType.<$Closure$94> make(
            /* base class */$Closure$94.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$94 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$94.class + " calling"); } 
                x10.compiler.ws.RegularFrame frame = (x10.compiler.ws.RegularFrame) $deserializer.readRef();
                $_obj.frame = frame;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$94 $_obj = new $Closure$94((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (frame instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.frame);
                } else {
                $serializer.write(this.frame);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$94(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49229 =
                      x10.lang.Runtime.wsFIFO();
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49229.push(((x10.core.RefI)(this.
                                                                                                                                                frame)));
                }
                
                public x10.compiler.ws.RegularFrame frame;
                
                public $Closure$94(final x10.compiler.ws.RegularFrame frame) { {
                                                                                      this.frame = ((x10.compiler.ws.RegularFrame)(frame));
                                                                                  }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$95 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$95.class);
            
            public static final x10.rtt.RuntimeType<$Closure$95> $RTT = x10.rtt.StaticVoidFunType.<$Closure$95> make(
            /* base class */$Closure$95.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$95 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$95.class + " calling"); } 
                x10.compiler.ws.RegularFrame frame = (x10.compiler.ws.RegularFrame) $deserializer.readRef();
                $_obj.frame = frame;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$95 $_obj = new $Closure$95((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (frame instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.frame);
                } else {
                $serializer.write(this.frame);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$95(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49231 =
                      x10.lang.Runtime.wsFIFO();
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49231.push(((x10.core.RefI)(this.
                                                                                                                                                frame)));
                }
                
                public x10.compiler.ws.RegularFrame frame;
                
                public $Closure$95(final x10.compiler.ws.RegularFrame frame) { {
                                                                                      this.frame = ((x10.compiler.ws.RegularFrame)(frame));
                                                                                  }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$96 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$96.class);
            
            public static final x10.rtt.RuntimeType<$Closure$96> $RTT = x10.rtt.StaticVoidFunType.<$Closure$96> make(
            /* base class */$Closure$96.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$96 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$96.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$96 $_obj = new $Closure$96((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$96(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
x10.lang.Runtime.wsEnd();
                }
                
                public $Closure$96() { {
                                              
                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$97 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$97.class);
            
            public static final x10.rtt.RuntimeType<$Closure$97> $RTT = x10.rtt.StaticVoidFunType.<$Closure$97> make(
            /* base class */$Closure$97.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$97 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$97.class + " calling"); } 
                x10.compiler.ws.Worker worker49326 = (x10.compiler.ws.Worker) $deserializer.readRef();
                $_obj.worker49326 = worker49326;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$97 $_obj = new $Closure$97((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (worker49326 instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.worker49326);
                } else {
                $serializer.write(this.worker49326);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$97(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.runtime.impl.java.Deque t49327 =
                      x10.lang.Runtime.wsFIFO();
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.
                                                                                                                   worker49326.fifo = ((x10.runtime.impl.java.Deque)(t49327));
                    
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
this.
                                                                                                                   worker49326.run();
                }
                
                public x10.compiler.ws.Worker worker49326;
                
                public $Closure$97(final x10.compiler.ws.Worker worker49326) { {
                                                                                      this.worker49326 = ((x10.compiler.ws.Worker)(worker49326));
                                                                                  }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$98 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$98.class);
            
            public static final x10.rtt.RuntimeType<$Closure$98> $RTT = x10.rtt.StaticVoidFunType.<$Closure$98> make(
            /* base class */$Closure$98.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$98 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$98.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$98 $_obj = new $Closure$98((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$98(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
final x10.compiler.ws.Worker t49340 =
                      x10.compiler.ws.Worker.startHere();
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/Worker.x10"
t49340.run();
                }
                
                public $Closure$98() { {
                                              
                                          }}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$compiler$ws$Worker$2 {}
        
        }
        