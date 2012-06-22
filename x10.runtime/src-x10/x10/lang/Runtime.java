package x10.lang;


@x10.core.X10Generated final public class Runtime extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Runtime.class);
    
    public static final x10.rtt.RuntimeType<Runtime> $RTT = x10.rtt.NamedType.<Runtime> make(
    "x10.lang.Runtime", /* base class */Runtime.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Runtime $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Runtime.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Runtime $_obj = new Runtime((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public Runtime(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                println(
                                                                                                final java.lang.Object any){try {java.lang.System.err.println(any);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                println(
                                                                                                ){try {java.lang.System.err.println();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static <$T>void
                                                                                                printf__1x10$lang$Runtime$$T(
                                                                                                final x10.rtt.Type $T,
                                                                                                final java.lang.String fmt,
                                                                                                final $T t){try {java.lang.System.err.printf(fmt, t);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                x10rtSendMessage(
                                                                                                final int id,
                                                                                                final x10.core.fun.VoidFun_0_0 body){try {x10.runtime.impl.java.Runtime.runClosureAt(id, body);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                x10rtSendAsync(
                                                                                                final int id,
                                                                                                final x10.core.fun.VoidFun_0_0 body,
                                                                                                final x10.lang.FinishState finishState){try {x10.runtime.impl.java.Runtime.runAsyncAt(id, body, finishState);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                x10rtInit(
                                                                                                ){try {x10.x10rt.X10RT.registration_complete();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                x10rtProbe(
                                                                                                ){try {x10.runtime.impl.java.Runtime.eventProbe();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                x10rtBlockingProbe(
                                                                                                ){try {x10.runtime.impl.java.Runtime.blockingProbe();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                wsProcessEvents(
                                                                                                ){try {x10.runtime.impl.java.Runtime.eventProbe();}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static <$T>$T
                                                                                                 deepCopy__0x10$lang$Runtime$$T$G(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final $T o){try {return x10.runtime.impl.java.Runtime.<$T>deepCopy(o);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
        
        
        
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                 deallocObject(
                                                                                                 final x10.core.RefI o){
            
        }
        
        
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static <$T>void
                                                                                                 dealloc__0$1x10$lang$Runtime$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.core.fun.Fun_0_0<$T> o){
            
        }
        
        
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                 dealloc(
                                                                                                 final x10.core.fun.VoidFun_0_0 o){
            
        }
        
        
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static x10.util.HashMap<java.lang.String, java.lang.String> env = x10.runtime.impl.java.Runtime.loadenv();
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static boolean STRICT_FINISH = x10.lang.Configuration.strict_finish$O();
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static int NTHREADS = x10.lang.Configuration.nthreads$O();
        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static int MAX_THREADS = x10.lang.Configuration.max_threads$O();
        
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static boolean STATIC_THREADS = x10.lang.Configuration.static_threads$O();
        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static boolean WARN_ON_THREAD_CREATION = x10.lang.Configuration.warn_on_thread_creation$O();
        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static boolean BUSY_WAITING = x10.lang.Configuration.busy_waiting$O();
        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static x10.util.concurrent.Monitor staticMonitor = ((x10.util.concurrent.Monitor)(new x10.util.concurrent.Monitor()));
        
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static x10.util.concurrent.Monitor atomicMonitor = ((x10.util.concurrent.Monitor)(new x10.util.concurrent.Monitor()));
        
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static x10.lang.Runtime.Pool pool = ((x10.lang.Runtime.Pool)(new x10.lang.Runtime.Pool((java.lang.System[]) null).$init()));
        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static x10.lang.FinishState.FinishStates finishStates = ((x10.lang.FinishState.FinishStates)(new x10.lang.FinishState.FinishStates((java.lang.System[]) null).$init()));
        
        
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                 wsInit(
                                                                                                 ){
            
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.wsBlockedContinuations = ((x10.runtime.impl.java.Deque)(new x10.runtime.impl.java.Deque()));
        }
        
        
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.runtime.impl.java.Deque
                                                                                                 wsFIFO(
                                                                                                 ){
            
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.worker().
                                                                                                            wsfifo;
        }
        
        
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                 wsBlock(
                                                                                                 final x10.core.RefI k){
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.
                                                                                                     wsBlockedContinuations.push(((x10.core.RefI)(k)));
        }
        
        
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                 wsUnblock(
                                                                                                 ){
            
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.runtime.impl.java.Deque src =
              ((x10.runtime.impl.java.Deque)(x10.lang.Runtime.pool.
                                               wsBlockedContinuations));
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.runtime.impl.java.Deque dst =
              x10.lang.Runtime.wsFIFO();
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.core.RefI k =
               null;
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (((k = src.poll()) != (null)))
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
dst.push(((x10.core.RefI)(k)));
        }
        
        
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                 wsEnd(
                                                                                                 ){
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.wsEnd = true;
        }
        
        
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static boolean
                                                                                                 wsEnded$O(
                                                                                                 ){
            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.pool.
                                                                                                            wsEnd;
        }
        
        
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
@x10.core.X10Generated public static interface Mortal extends x10.core.Any
                                                                                               {
            public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Mortal.class);
            
            public static final x10.rtt.RuntimeType<Mortal> $RTT = x10.rtt.NamedType.<Mortal> make(
            "x10.lang.Runtime.Mortal", /* base class */Mortal.class
            , /* parents */ new x10.rtt.Type[] {}
            );
            
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
@x10.core.X10Generated final public static class Workers extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Workers.class);
            
            public static final x10.rtt.RuntimeType<Workers> $RTT = x10.rtt.NamedType.<Workers> make(
            "x10.lang.Runtime.Workers", /* base class */Workers.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Workers $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Workers.class + " calling"); } 
                x10.util.concurrent.Lock lock = (x10.util.concurrent.Lock) $deserializer.readRef();
                $_obj.lock = lock;
                x10.array.Array workers = (x10.array.Array) $deserializer.readRef();
                $_obj.workers = workers;
                x10.array.Array parkedWorkers = (x10.array.Array) $deserializer.readRef();
                $_obj.parkedWorkers = parkedWorkers;
                $_obj.count = $deserializer.readInt();
                $_obj.spareCount = $deserializer.readInt();
                $_obj.idleCount = $deserializer.readInt();
                $_obj.deadCount = $deserializer.readInt();
                $_obj.spareNeeded = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Workers $_obj = new Workers((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (lock instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.lock);
                } else {
                $serializer.write(this.lock);
                }
                if (workers instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.workers);
                } else {
                $serializer.write(this.workers);
                }
                if (parkedWorkers instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.parkedWorkers);
                } else {
                $serializer.write(this.parkedWorkers);
                }
                $serializer.write(this.count);
                $serializer.write(this.spareCount);
                $serializer.write(this.idleCount);
                $serializer.write(this.deadCount);
                $serializer.write(this.spareNeeded);
                
            }
            
            // constructor just for allocation
            public Workers(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.util.concurrent.Lock lock;
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.array.Array<x10.lang.Runtime.Worker> workers;
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.array.Array<x10.lang.Runtime.Worker> parkedWorkers;
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int count;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int spareCount;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int idleCount;
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int deadCount;
                
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int spareNeeded;
                
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         reduce(
                                                                                                         final int n){
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56551$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                          (int)(n));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                }
                
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int
                                                                                                         increase$O(
                                                                                                         ){
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((spareNeeded) > (((int)(0))))) {
                        
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56552$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                              (int)(1));
                        
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return 0;
                    } else {
                        
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((spareCount) > (((int)(0))))) {
                            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                              ((x10.lang.Runtime.Workers.$closure_apply56553$O(((x10.lang.Runtime.Workers)(this)),
                                                                               (int)(1))) + (((int)(idleCount))));
                            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Worker worker =
                              ((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$apply$G((int)(i));
                            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$set__1x10$array$Array$$T$G((int)(i),
                                                                                                                                                                                                         ((x10.lang.Runtime.Worker)(null)));
                            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                            
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
worker.unpark();
                            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return 0;
                        } else {
                            
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                              ((x10.lang.Runtime.Workers.$closure_apply56554$O(((x10.lang.Runtime.Workers)(this)),
                                                                               (int)(1))) - (((int)(1))));
                            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.check((int)(i));
                            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return i;
                        }
                    }
                }
                
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int
                                                                                                         promote$O(
                                                                                                         ){
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                      ((x10.lang.Runtime.Workers.$closure_apply56555$O(((x10.lang.Runtime.Workers)(this)),
                                                                       (int)(1))) - (((int)(1))));
                    
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56556$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                          (int)(1));
                    
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                    
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.check((int)(i));
                    
//#line 222 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return i;
                }
                
                
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         check(
                                                                                                         final int i){
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((i) >= (((int)(x10.lang.Runtime.MAX_THREADS))))) {
                        
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
java.lang.System.err.println(((x10.lang.Runtime.home()) + (": TOO MANY THREADS... ABORTING")));
                        
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
java.lang.System.exit(((int)(1)));
                    }
                    
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (x10.lang.Runtime.WARN_ON_THREAD_CREATION) {
                        
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
java.lang.System.err.println(((((((x10.lang.Runtime.home()) + (": WARNING: A new OS-level thread was discovered (there are now "))) + ((x10.core.Int.$box(i))))) + (" threads).")));
                    }
                }
                
                
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         convert(
                                                                                                         ){
                    
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (((spareNeeded) > (((int)(0)))) &&
                                                                                                                  ((idleCount) > (((int)(0))))) {
                        
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56557$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                              (int)(1));
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56558$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                              (int)(1));
                        
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56559$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                              (int)(1));
                    }
                }
                
                
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity
                                                                                                         yield(
                                                                                                         final x10.lang.Runtime.Worker worker){
                    
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((spareNeeded) <= (((int)(0))))) {
                        
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return null;
                    }
                    
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.convert();
                    
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((spareNeeded) <= (((int)(0))))) {
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                        
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return null;
                    }
                    
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56560$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                          (int)(1));
                    
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                      ((((x10.lang.Runtime.Workers.$closure_apply56561$O(((x10.lang.Runtime.Workers)(this)),
                                                                         (int)(1))) - (((int)(1))))) + (((int)(idleCount))));
                    
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$set__1x10$array$Array$$T$G((int)(i),
                                                                                                                                                                                                 ((x10.lang.Runtime.Worker)(worker)));
                    
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (x10.rtt.Equality.equalsequals((((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$apply$G((int)(i))),(worker))) {
                        
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                        
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Worker.park();
                        
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    }
                    
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                    
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return worker.
                                                                                                                    activity;
                }
                
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity
                                                                                                         take(
                                                                                                         final x10.lang.Runtime.Worker worker){
                    
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (x10.lang.Runtime.BUSY_WAITING) {
                        
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return null;
                    }
                    
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((((idleCount) - (((int)(spareNeeded))))) >= (((int)(((x10.lang.Runtime.NTHREADS) - (((int)(1))))))))) {
                        
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return null;
                    }
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.convert();
                    
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((idleCount) >= (((int)(((x10.lang.Runtime.NTHREADS) - (((int)(1))))))))) {
                        
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                        
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return null;
                    }
                    
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                      ((spareCount) + (((int)(((x10.lang.Runtime.Workers.$closure_apply56562$O(((x10.lang.Runtime.Workers)(this)),
                                                                                               (int)(1))) - (((int)(1))))))));
                    
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$set__1x10$array$Array$$T$G((int)(i),
                                                                                                                                                                                                 ((x10.lang.Runtime.Worker)(worker)));
                    
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (x10.rtt.Equality.equalsequals((((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$apply$G((int)(i))),(worker))) {
                        
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                        
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Worker.park();
                        
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    }
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return worker.
                                                                                                                    activity;
                }
                
                
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public boolean
                                                                                                         give$O(
                                                                                                         final x10.lang.Activity activity){
                    
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (x10.lang.Runtime.BUSY_WAITING) {
                        
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return false;
                    }
                    
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((((idleCount) - (((int)(spareNeeded))))) <= (((int)(0))))) {
                        
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return false;
                    }
                    
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.convert();
                    
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((idleCount) <= (((int)(0))))) {
                        
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                        
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return false;
                    }
                    
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                      ((spareCount) + (((int)(x10.lang.Runtime.Workers.$closure_apply56563$O(((x10.lang.Runtime.Workers)(this)),
                                                                                             (int)(1))))));
                    
//#line 299 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Worker worker =
                      ((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$apply$G((int)(i));
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
worker.activity = ((x10.lang.Activity)(activity));
                    
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$set__1x10$array$Array$$T$G((int)(i),
                                                                                                                                                                                                 ((x10.lang.Runtime.Worker)(null)));
                    
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                    
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
worker.unpark();
                    
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return true;
                }
                
                
//#line 308 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         reclaim(
                                                                                                         ){
                    
//#line 309 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.lock();
                    
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Workers.$closure_apply56564$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                          (int)(1));
                    
//#line 311 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (((idleCount) > (((int)(0))))) {
                        
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                          ((spareCount) + (((int)(x10.lang.Runtime.Workers.$closure_apply56565$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                 (int)(1))))));
                        
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Worker worker =
                          ((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$apply$G((int)(i));
                        
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$set__1x10$array$Array$$T$G((int)(i),
                                                                                                                                                                                                     ((x10.lang.Runtime.Worker)(null)));
                        
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
worker.unpark();
                    }
                    
//#line 317 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((spareCount) > (((int)(0))))) {
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Worker worker =
                          ((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$apply$G((int)(x10.lang.Runtime.Workers.$closure_apply56566$O(((x10.lang.Runtime.Workers)(this)),
                                                                                                                                                  (int)(1))));
                        
//#line 319 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)parkedWorkers).$set__1x10$array$Array$$T$G((int)(spareCount),
                                                                                                                                                                                                     ((x10.lang.Runtime.Worker)(null)));
                        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
worker.unpark();
                    }
                    
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
lock.unlock();
                }
                
                
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Runtime.Worker
                                                                                                         $apply(
                                                                                                         final int i){
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return ((x10.array.Array<x10.lang.Runtime.Worker>)workers).$apply$G((int)(i));
                }
                
                
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         $set(
                                                                                                         final int i,
                                                                                                         final x10.lang.Runtime.Worker worker){
                    
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.array.Array<x10.lang.Runtime.Worker>)workers).$set__1x10$array$Array$$T$G((int)(i),
                                                                                                                                                                                           ((x10.lang.Runtime.Worker)(worker)));
                }
                
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public x10.lang.Runtime.Workers
                                                                                                         x10$lang$Runtime$Workers$$x10$lang$Runtime$Workers$this(
                                                                                                         ){
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.Workers.this;
                }
                
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
// creation method for java code (1-phase java constructor)
                public Workers(){this((java.lang.System[]) null);
                                     $init();}
                
                // constructor for non-virtual call
                final public x10.lang.Runtime.Workers x10$lang$Runtime$Workers$$init$S() { {
                                                                                                  
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                  
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                  
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55878();
                                                                                              }
                                                                                              return this;
                                                                                              }
                
                // constructor
                public x10.lang.Runtime.Workers $init(){return x10$lang$Runtime$Workers$$init$S();}
                
                
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public void
                                                                                                         __fieldInitializers55878(
                                                                                                         ){
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.lock = ((x10.util.concurrent.Lock)(new x10.util.concurrent.Lock()));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.workers = ((x10.array.Array)(new x10.array.Array<x10.lang.Runtime.Worker>((java.lang.System[]) null, x10.lang.Runtime.Worker.$RTT).$init(((int)(x10.lang.Runtime.MAX_THREADS)))));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.parkedWorkers = ((x10.array.Array)(new x10.array.Array<x10.lang.Runtime.Worker>((java.lang.System[]) null, x10.lang.Runtime.Worker.$RTT).$init(((int)(x10.lang.Runtime.MAX_THREADS)))));
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.count = 0;
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.spareCount = 0;
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.idleCount = 0;
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.deadCount = 0;
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.spareNeeded = 0;
                }
                
                final private static int
                  $closure_apply56551$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareNeeded = ((x.
                                                                                                                                      spareNeeded) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56551$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56551$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56552$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareNeeded = ((x.
                                                                                                                                      spareNeeded) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56552$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56552$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56553$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareCount = ((x.
                                                                                                                                     spareCount) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56553$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56553$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56554$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.count = ((x.
                                                                                                                                count) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56554$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56554$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56555$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.count = ((x.
                                                                                                                                count) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56555$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56555$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56556$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.deadCount = ((x.
                                                                                                                                    deadCount) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56556$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56556$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56557$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareNeeded = ((x.
                                                                                                                                      spareNeeded) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56557$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56557$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56558$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.idleCount = ((x.
                                                                                                                                    idleCount) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56558$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56558$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56559$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareCount = ((x.
                                                                                                                                     spareCount) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56559$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56559$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56560$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareNeeded = ((x.
                                                                                                                                      spareNeeded) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56560$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56560$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56561$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareCount = ((x.
                                                                                                                                     spareCount) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56561$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56561$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56562$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.idleCount = ((x.
                                                                                                                                    idleCount) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56562$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56562$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56563$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.idleCount = ((x.
                                                                                                                                    idleCount) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56563$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56563$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56564$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.deadCount = ((x.
                                                                                                                                    deadCount) + (((int)(y))));
                }
                
                final public static int
                  $closure_apply56564$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56564$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56565$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.idleCount = ((x.
                                                                                                                                    idleCount) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56565$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56565$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
                
                final private static int
                  $closure_apply56566$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x.spareCount = ((x.
                                                                                                                                     spareCount) - (((int)(y))));
                }
                
                final public static int
                  $closure_apply56566$P$O(
                  final x10.lang.Runtime.Workers x,
                  final int y){
                    return x10.lang.Runtime.Workers.$closure_apply56566$O(((x10.lang.Runtime.Workers)(x)),
                                                                          (int)(y));
                }
            
        }
        
        
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
@x10.core.X10Generated final public static class Worker extends x10.runtime.impl.java.Thread implements x10.io.CustomSerialization
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Worker.class);
            
            public static final x10.rtt.RuntimeType<Worker> $RTT = x10.rtt.NamedType.<Worker> make(
            "x10.lang.Runtime.Worker", /* base class */Worker.class
            , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.runtime.impl.java.Thread.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            // custom serializer
            private transient x10.io.SerialData $$serialdata;
            private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
            private Object readResolve() { return new Worker($$serialdata); }
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.writeObject($$serialdata); }
            private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
            $$serialdata = (x10.io.SerialData) ois.readObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Worker $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Worker.class + " calling"); } 
                x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                $_obj.$init($$serialdata);
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
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println(" CustomSerialization : $_serialize of " + this + " calling"); } 
                $$serialdata = serialize(); 
                $serializer.write($$serialdata);
                
            }
            
            // dummy 2nd-phase constructor for non-splittable type
            public void $init(x10.io.SerialData $$serialdata) {
            
                throw new x10.lang.RuntimeException("dummy 2nd-phase constructor for non-splittable type should never be called.");
                
            }
            
            // constructor just for allocation
            public Worker(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public static int BOUND = 100;
                
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity activity;
                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.runtime.impl.java.Deque queue;
                
//#line 340 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.util.Random random;
                
//#line 343 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int workerId;
                
//#line 346 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.runtime.impl.java.Deque wsfifo;
                
                
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public Worker(final int workerId) {super((("thread-") + ((x10.core.Int.$box(workerId)))));
                                                                                                                                               {
                                                                                                                                                  
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                                                                  
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55879();
                                                                                                                                                  
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.workerId = workerId;
                                                                                                                                                  
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.random = ((x10.util.Random)(new x10.util.Random((java.lang.System[]) null).$init(((long)(((int)(((((((workerId) + (((int)(((workerId) << (((int)(8))))))))) + (((int)(((workerId) << (((int)(16))))))))) + (((int)(((workerId) << (((int)(24)))))))))))))));
                                                                                                                                              }}
                
                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public Worker(final int workerId,
                                                                                                                     final boolean dummy) {super();
                                                                                                                                                {
                                                                                                                                                   
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                                                                   
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55879();
                                                                                                                                                   
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.workerId = workerId;
                                                                                                                                                   
//#line 357 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.random = ((x10.util.Random)(new x10.util.Random((java.lang.System[]) null).$init(((long)(((int)(((((((workerId) + (((int)(((workerId) << (((int)(8))))))))) + (((int)(((workerId) << (((int)(16))))))))) + (((int)(((workerId) << (((int)(24)))))))))))))));
                                                                                                                                                   
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = ((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.Worker.$Closure$143())),
                                                                                                                                                                                                                                                                                                                                      ((x10.lang.FinishState)(x10.lang.FinishState.getInitialized$UNCOUNTED_FINISH())))));
                                                                                                                                               }}
                
                
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int
                                                                                                         size$O(
                                                                                                         ){
                    
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return queue.size$O();
                }
                
                
//#line 365 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity
                                                                                                         activity(
                                                                                                         ){
                    
//#line 365 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return activity;
                }
                
                
//#line 368 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity
                                                                                                         poll(
                                                                                                         ){
                    
//#line 368 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.rtt.Types.<x10.lang.Activity> cast(queue.poll(),x10.lang.Activity.$RTT);
                }
                
                
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity
                                                                                                         steal(
                                                                                                         ){
                    
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.rtt.Types.<x10.lang.Activity> cast(queue.steal(),x10.lang.Activity.$RTT);
                }
                
                
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         push(
                                                                                                         final x10.lang.Activity activity){
                    
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
queue.push(((x10.core.RefI)(activity)));
                }
                
                
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         $apply(
                                                                                                         ){
                    
//#line 378 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {try {{
                        
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (this.loop$O())
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
;
                    }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable t) {
                        
//#line 381 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
java.lang.System.err.println("Uncaught exception in worker thread");
                        
//#line 382 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
t.printStackTrace();
                    }finally {{
                         
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.release();
                     }}
                    }
                
                
//#line 389 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
private boolean
                                                                                                         loop$O(
                                                                                                         ){
                    
//#line 390 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
for (
//#line 390 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
int i =
                                                                                                                  0;
                                                                                                                ((i) < (((int)(x10.lang.Runtime.Worker.BOUND))));
                                                                                                                
//#line 390 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
i = ((i) + (((int)(1))))) {
                        
//#line 391 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = ((x10.lang.Activity)(this.poll()));
                        
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((activity) == (null))) {
                            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = ((x10.lang.Activity)(x10.lang.Runtime.pool.scan(((x10.util.Random)(random)),
                                                                                                                                                                                   ((x10.lang.Runtime.Worker)(this)))));
                            
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((activity) == (null))) {
                                
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return false;
                            }
                        }
                        
//#line 396 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
activity.run();
                        
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.deallocObject(((x10.core.RefI)(activity)));
                    }
                    
//#line 399 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return true;
                }
                
                public static boolean
                  loop$P$O(
                  final x10.lang.Runtime.Worker Worker){
                    return Worker.loop$O();
                }
                
                
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         probe(
                                                                                                         ){
                    
//#line 404 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity tmp =
                      ((x10.lang.Activity)(activity));
                    
//#line 405 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.eventProbe();
                    
//#line 406 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
for (;
                                                                                                                ;
                                                                                                                ) {
                        
//#line 407 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = ((x10.lang.Activity)(this.poll()));
                        
//#line 408 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((activity) == (null))) {
                            
//#line 409 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = ((x10.lang.Activity)(tmp));
                            
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return;
                        }
                        
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
activity.run();
                        
//#line 413 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.deallocObject(((x10.core.RefI)(activity)));
                    }
                }
                
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         join(
                                                                                                         final x10.util.concurrent.SimpleLatch latch){
                    
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity tmp =
                      ((x10.lang.Activity)(activity));
                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (this.loop2$O(((x10.util.concurrent.SimpleLatch)(latch))))
                        
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
;
                    
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = ((x10.lang.Activity)(tmp));
                }
                
                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
private boolean
                                                                                                         loop2$O(
                                                                                                         final x10.util.concurrent.SimpleLatch latch){
                    
//#line 426 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
for (
//#line 426 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
int i =
                                                                                                                  0;
                                                                                                                ((i) < (((int)(x10.lang.Runtime.Worker.BOUND))));
                                                                                                                
//#line 426 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
i = ((i) + (((int)(1))))) {
                        
//#line 427 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (latch.$apply$O()) {
                            
//#line 427 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return false;
                        }
                        
//#line 428 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = ((x10.lang.Activity)(this.poll()));
                        
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((activity) == (null))) {
                            
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return false;
                        }
                        
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
activity.run();
                        
//#line 435 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.deallocObject(((x10.core.RefI)(activity)));
                    }
                    
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return true;
                }
                
                public static boolean
                  loop2$P$O(
                  final x10.util.concurrent.SimpleLatch latch,
                  final x10.lang.Runtime.Worker Worker){
                    return Worker.loop2$O(((x10.util.concurrent.SimpleLatch)(latch)));
                }
                
                
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                         park(
                                                                                                         ){
                    
//#line 442 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.lang.Runtime.STATIC_THREADS)) {
                        
//#line 443 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Thread.park();
                    } else {
                        
//#line 445 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.probe();
                    }
                }
                
                
//#line 450 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                         unpark(
                                                                                                         ){
                    
//#line 451 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.lang.Runtime.STATIC_THREADS)) {
                        
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
super.unpark();
                    }
                }
                
                
//#line 460 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.io.SerialData
                                                                                                         serialize(
                                                                                                         ){
                    
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot serialize ") + (x10.rtt.Types.typeName(this))));
                }
                
                
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public Worker(final x10.io.SerialData a) {super(((x10.io.SerialData)(a)));
                                                                                                                                                      {
                                                                                                                                                         
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                                                                         
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55879();
                                                                                                                                                         
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                     }}
                
                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public x10.lang.Runtime.Worker
                                                                                                         x10$lang$Runtime$Worker$$x10$lang$Runtime$Worker$this(
                                                                                                         ){
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.Worker.this;
                }
                
                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public void
                                                                                                         __fieldInitializers55879(
                                                                                                         ){
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.activity = null;
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.queue = ((x10.runtime.impl.java.Deque)(new x10.runtime.impl.java.Deque()));
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.wsfifo = ((x10.runtime.impl.java.Deque)(new x10.runtime.impl.java.Deque()));
                }
                
                public static int
                  getInitialized$BOUND(
                  ){
                    return x10.lang.Runtime.Worker.BOUND;
                }
                
                @x10.core.X10Generated public static class $Closure$143 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$143.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$143> $RTT = x10.rtt.StaticVoidFunType.<$Closure$143> make(
                    /* base class */$Closure$143.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$143 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$143.class + " calling"); } 
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$143 $_obj = new $Closure$143((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$143(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
                        }
                        
                        public $Closure$143() { {
                                                       
                                                   }}
                        
                    }
                    
                
                public void
                  x10$lang$Thread$unpark$S(
                  ){
                    super.unpark();
                }
                
                }
                
                
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
@x10.core.X10Generated public static class Pool extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                                       {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Pool.class);
                    
                    public static final x10.rtt.RuntimeType<Pool> $RTT = x10.rtt.NamedType.<Pool> make(
                    "x10.lang.Runtime.Pool", /* base class */Pool.class
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Pool $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Pool.class + " calling"); } 
                        x10.util.concurrent.SimpleLatch latch = (x10.util.concurrent.SimpleLatch) $deserializer.readRef();
                        $_obj.latch = latch;
                        $_obj.wsEnd = $deserializer.readBoolean();
                        x10.lang.Runtime.Workers workers = (x10.lang.Runtime.Workers) $deserializer.readRef();
                        $_obj.workers = workers;
                        x10.runtime.impl.java.Deque wsBlockedContinuations = (x10.runtime.impl.java.Deque) $deserializer.readRef();
                        $_obj.wsBlockedContinuations = wsBlockedContinuations;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        Pool $_obj = new Pool((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (latch instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.latch);
                        } else {
                        $serializer.write(this.latch);
                        }
                        $serializer.write(this.wsEnd);
                        if (workers instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.workers);
                        } else {
                        $serializer.write(this.workers);
                        }
                        if (wsBlockedContinuations instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.wsBlockedContinuations);
                        } else {
                        $serializer.write(this.wsBlockedContinuations);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public Pool(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
//#line 475 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.util.concurrent.SimpleLatch latch;
                        
//#line 477 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public boolean wsEnd;
                        
//#line 479 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Runtime.Workers workers;
                        
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.runtime.impl.java.Deque wsBlockedContinuations;
                        
                        
//#line 483 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                                 $apply(
                                                                                                                 final int n){
                            
//#line 484 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.count = n;
                            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.$set((int)(0),
                                                                                                                                ((x10.lang.Runtime.Worker)(x10.lang.Runtime.worker())));
                            
//#line 486 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
for (
//#line 486 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
int i =
                                                                                                                          1;
                                                                                                                        ((i) < (((int)(n))));
                                                                                                                        
//#line 486 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
i = ((i) + (((int)(1))))) {
                                
//#line 487 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.$set((int)(i),
                                                                                                                                    ((x10.lang.Runtime.Worker)(new x10.lang.Runtime.Worker(i))));
                            }
                            
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
for (
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
int i =
                                                                                                                          1;
                                                                                                                        ((i) < (((int)(n))));
                                                                                                                        
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
i = ((i) + (((int)(1))))) {
                                
//#line 490 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.$apply((int)(i)).start();
                            }
                            
//#line 492 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.$apply((int)(0)).$apply();
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
while (((workers.
                                                                                                                              count) > (((int)(workers.
                                                                                                                                                 deadCount)))))
                                
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.Worker.park();
                        }
                        
                        
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                                 increase(
                                                                                                                 ){
                            
//#line 498 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                              workers.increase$O();
                            
//#line 499 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((i) > (((int)(0))))) {
                                
//#line 501 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Worker worker =
                                  ((x10.lang.Runtime.Worker)(new x10.lang.Runtime.Worker(((int)(i)))));
                                
//#line 502 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.$set((int)(i),
                                                                                                                                    ((x10.lang.Runtime.Worker)(worker)));
                                
//#line 503 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
worker.start();
                            }
                        }
                        
                        
//#line 508 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Runtime.Worker
                                                                                                                 wrapNativeThread(
                                                                                                                 ){
                            
//#line 509 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final int i =
                              workers.promote$O();
                            
//#line 510 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Worker worker =
                              ((x10.lang.Runtime.Worker)(new x10.lang.Runtime.Worker(((int)(i)),
                                                                                     ((boolean)(false)))));
                            
//#line 511 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.$set((int)(i),
                                                                                                                                ((x10.lang.Runtime.Worker)(worker)));
                            
//#line 512 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return worker;
                        }
                        
                        
//#line 516 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                                 decrease(
                                                                                                                 final int n){
                            
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.reduce((int)(n));
                        }
                        
                        
//#line 521 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public boolean
                                                                                                                 deal$O(
                                                                                                                 final x10.lang.Activity activity){
                            
//#line 521 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return workers.give$O(((x10.lang.Activity)(activity)));
                        }
                        
                        
//#line 524 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public void
                                                                                                                 release(
                                                                                                                 ){
                            
//#line 525 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.reclaim();
                            
//#line 526 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) workers.
                                                                                                                                count) ==
                                                                                                                       ((int) workers.
                                                                                                                                deadCount)) {
                                
//#line 526 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
workers.$apply((int)(0)).unpark();
                            }
                        }
                        
                        
//#line 530 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity
                                                                                                                 scan(
                                                                                                                 final x10.util.Random random,
                                                                                                                 final x10.lang.Runtime.Worker worker){
                            
//#line 531 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Activity activity =
                              null;
                            
//#line 532 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
int next =
                              random.nextInt$O((int)(workers.
                                                       count));
                            
//#line 533 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
int i =
                              2;
                            
//#line 534 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
for (;
                                                                                                                        ;
                                                                                                                        ) {
                                
//#line 535 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((null) != (activity)) ||
                                                                                                                           latch.$apply$O()) {
                                    
//#line 535 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return activity;
                                }
                                
//#line 537 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
activity = workers.yield(((x10.lang.Runtime.Worker)(worker)));
                                
//#line 538 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((null) != (activity)) ||
                                                                                                                           latch.$apply$O()) {
                                    
//#line 538 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return activity;
                                }
                                
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.eventProbe();
                                
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
activity = worker.poll();
                                
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((null) != (activity)) ||
                                                                                                                           latch.$apply$O()) {
                                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return activity;
                                }
                                
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((next) < (((int)(x10.lang.Runtime.MAX_THREADS)))) &&
                                                                                                                           ((null) != (workers.$apply((int)(next))))) {
                                    
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
activity = workers.$apply((int)(next)).steal();
                                }
                                
//#line 547 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) (next = ((next) + (((int)(1)))))) ==
                                                                                                                           ((int) workers.
                                                                                                                                    count)) {
                                    
//#line 547 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
next = 0;
                                }
                                
//#line 548 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) ((i = ((i) - (((int)(1))))) + (((int)(1))))) ==
                                                                                                                           ((int) 0)) {
                                    
//#line 549 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((null) != (activity)) ||
                                                                                                                               latch.$apply$O()) {
                                        
//#line 549 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return activity;
                                    }
                                    
//#line 550 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
activity = workers.take(((x10.lang.Runtime.Worker)(worker)));
                                    
//#line 551 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
i = 2;
                                }
                            }
                        }
                        
                        
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public int
                                                                                                                 size$O(
                                                                                                                 ){
                            
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return workers.
                                                                                                                            count;
                        }
                        
                        
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public x10.lang.Runtime.Pool
                                                                                                                 x10$lang$Runtime$Pool$$x10$lang$Runtime$Pool$this(
                                                                                                                 ){
                            
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.Pool.this;
                        }
                        
                        
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
// creation method for java code (1-phase java constructor)
                        public Pool(){this((java.lang.System[]) null);
                                          $init();}
                        
                        // constructor for non-virtual call
                        final public x10.lang.Runtime.Pool x10$lang$Runtime$Pool$$init$S() { {
                                                                                                    
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                    
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                    
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55880();
                                                                                                }
                                                                                                return this;
                                                                                                }
                        
                        // constructor
                        public x10.lang.Runtime.Pool $init(){return x10$lang$Runtime$Pool$$init$S();}
                        
                        
                        
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public void
                                                                                                                 __fieldInitializers55880(
                                                                                                                 ){
                            
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.latch = ((x10.util.concurrent.SimpleLatch)(new x10.util.concurrent.SimpleLatch()));
                            
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.wsEnd = false;
                            
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.workers = ((x10.lang.Runtime.Workers)(new x10.lang.Runtime.Workers((java.lang.System[]) null).$init()));
                            
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.wsBlockedContinuations = null;
                        }
                    
                }
                
                
                
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.Runtime.Worker
                                                                                                         worker(
                                                                                                         ){
                    
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.rtt.Types.<x10.lang.Runtime.Worker> cast(x10.runtime.impl.java.Thread.currentThread(),x10.lang.Runtime.Worker.$RTT);
                }
                
                
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static int
                                                                                                         workerId$O(
                                                                                                         ){
                    
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.worker().
                                                                                                                    workerId;
                }
                
                
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static int
                                                                                                         poolSize$O(
                                                                                                         ){
                    
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.pool.size$O();
                }
                
                
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.Activity
                                                                                                         activity(
                                                                                                         ){
                    
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.worker().activity();
                }
                
                
//#line 584 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.Place
                                                                                                         home(
                                                                                                         ){
                    
//#line 585 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.runtime.impl.java.Thread.currentThread().home();
                }
                
                
//#line 590 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static int
                                                                                                         hereInt$O(
                                                                                                         ){
                    
//#line 591 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.home().
                                                                                                                    id;
                }
                
                
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static int
                                                                                                         surplusActivityCount$O(
                                                                                                         ){
                    
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.worker().size$O();
                }
                
                
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                         start(
                                                                                                         final x10.core.fun.VoidFun_0_0 init,
                                                                                                         final x10.core.fun.VoidFun_0_0 body){
                    
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {{
                        
//#line 609 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.x10rt.X10RT.registration_complete();
                        
//#line 611 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) x10.lang.Runtime.hereInt$O()) ==
                                                                                                                   ((int) 0)) {
                            
//#line 612 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState.Finish rootFinish =
                              ((x10.lang.FinishState.Finish)(new x10.lang.FinishState.Finish((java.lang.System[]) null).$init(((x10.util.concurrent.SimpleLatch)(x10.lang.Runtime.pool.
                                                                                                                                                                   latch)))));
                            
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.executeLocal(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$144(init,
                                                                                                                                                                                                                                                                                           body))),
                                                                                                                                                                                                                             ((x10.lang.FinishState)(rootFinish))))));
                            
//#line 618 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.$apply((int)(x10.lang.Runtime.NTHREADS));
                            
//#line 621 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {{
                                
//#line 622 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
rootFinish.waitForFinish();
                            }}finally {{
                                  
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
for (
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
int i =
                                                                                                                                1;
                                                                                                                              ((i) < (((int)(x10.lang.Place.getInitialized$MAX_PLACES()))));
                                                                                                                              
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
i = ((i) + (((int)(1))))) {
                                      
//#line 626 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(i)), new x10.lang.Runtime.$Closure$145());
                                  }
                              }}
                            } else {
                                
//#line 632 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.$apply((int)(x10.lang.Runtime.NTHREADS));
                            }
                        }}finally {{
                              
//#line 635 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.GlobalCounters.printStats();
                          }}
                    }
                    
                    
//#line 648 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                             runAsync__1$1x10$lang$Clock$2(
                                                                                                             final x10.lang.Place place,
                                                                                                             final x10.array.Array<x10.lang.Clock> clocks,
                                                                                                             final x10.core.fun.VoidFun_0_0 body){
                        
//#line 650 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                          x10.lang.Runtime.activity();
                        
//#line 651 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.ensureNotInAtomic();
                        
//#line 653 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState state =
                          a.finishState();
                        
//#line 654 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity.ClockPhases clockPhases =
                          ((x10.lang.Activity.ClockPhases)(a.clockPhases().make__0$1x10$lang$Clock$2(((x10.array.Array)(clocks)))));
                        
//#line 655 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
state.notifySubActivitySpawn(((x10.lang.Place)(place)));
                        
//#line 656 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) place.
                                                                                                                            id) ==
                                                                                                                   ((int) x10.lang.Runtime.hereInt$O())) {
                            
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.executeLocal(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(x10.runtime.impl.java.Runtime.<x10.core.fun.VoidFun_0_0>deepCopy(body))),
                                                                                                                                                                                                                             ((x10.lang.FinishState)(state)),
                                                                                                                                                                                                                             ((x10.lang.Activity.ClockPhases)(clockPhases))))));
                        } else {
                            
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 closure =
                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$146(body,
                                                                                            state,
                                                                                            clockPhases)));
                            
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(place.
                                                                                                                                                                       id)), closure);
                            
//#line 661 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                        }
                        
//#line 663 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(body)));
                    }
                    
                    
//#line 666 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                             runAsync(
                                                                                                             final x10.lang.Place place,
                                                                                                             final x10.core.fun.VoidFun_0_0 body){
                        
//#line 668 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                          x10.lang.Runtime.activity();
                        
//#line 669 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.ensureNotInAtomic();
                        
//#line 671 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState state =
                          a.finishState();
                        
//#line 672 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
state.notifySubActivitySpawn(((x10.lang.Place)(place)));
                        
//#line 673 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) place.
                                                                                                                            id) ==
                                                                                                                   ((int) x10.lang.Runtime.hereInt$O())) {
                            
//#line 674 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.executeLocal(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(x10.runtime.impl.java.Runtime.<x10.core.fun.VoidFun_0_0>deepCopy(body))),
                                                                                                                                                                                                                             ((x10.lang.FinishState)(state))))));
                        } else {
                            
//#line 676 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runAsyncAt(((int)(place.
                                                                                                                                                                     id)), body, state);
                        }
                        
//#line 678 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(body)));
                    }
                    
                    
//#line 684 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                             runAsync__0$1x10$lang$Clock$2(
                                                                                                             final x10.array.Array<x10.lang.Clock> clocks,
                                                                                                             final x10.core.fun.VoidFun_0_0 body){
                        
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                          x10.lang.Runtime.activity();
                        
//#line 687 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.ensureNotInAtomic();
                        
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState state =
                          a.finishState();
                        
//#line 690 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity.ClockPhases clockPhases =
                          ((x10.lang.Activity.ClockPhases)(a.clockPhases().make__0$1x10$lang$Clock$2(((x10.array.Array)(clocks)))));
                        
//#line 691 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
state.notifySubActivitySpawn(((x10.lang.Place)(x10.lang.Runtime.home())));
                        
//#line 692 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.executeLocal(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(body)),
                                                                                                                                                                                                                         ((x10.lang.FinishState)(state)),
                                                                                                                                                                                                                         ((x10.lang.Activity.ClockPhases)(clockPhases))))));
                    }
                    
                    
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                             runAsync(
                                                                                                             final x10.core.fun.VoidFun_0_0 body){
                        
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                          x10.lang.Runtime.activity();
                        
//#line 698 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.ensureNotInAtomic();
                        
//#line 700 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState state =
                          a.finishState();
                        
//#line 701 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
state.notifySubActivitySpawn(((x10.lang.Place)(x10.lang.Runtime.home())));
                        
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.executeLocal(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(body)),
                                                                                                                                                                                                                         ((x10.lang.FinishState)(state))))));
                    }
                    
                    
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                             runFinish(
                                                                                                             final x10.core.fun.VoidFun_0_0 body){
                        {
                            
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.ensureNotInAtomic();
                            
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState x10$__var11 =
                              x10.lang.Runtime.startFinish();
                            
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {try {{
                                {
                                    
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)body).$apply();
                                }
                            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                                
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                                
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.Exception();
                            }finally {{
                                 
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var11)));
                             }}
                            }
                        }
                    
                    
//#line 712 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                             runUncountedAsync(
                                                                                                             final x10.lang.Place place,
                                                                                                             final x10.core.fun.VoidFun_0_0 body){
                        
//#line 714 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                          x10.lang.Runtime.activity();
                        
//#line 715 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.ensureNotInAtomic();
                        
//#line 717 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) place.
                                                                                                                            id) ==
                                                                                                                   ((int) x10.lang.Runtime.hereInt$O())) {
                            
//#line 718 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.executeLocal(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(x10.runtime.impl.java.Runtime.<x10.core.fun.VoidFun_0_0>deepCopy(body))),
                                                                                                                                                                                                                             ((x10.lang.FinishState)(x10.lang.FinishState.getInitialized$UNCOUNTED_FINISH()))))));
                        } else {
                            
//#line 720 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 closure =
                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$147(body)));
                            
//#line 721 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(place.
                                                                                                                                                                       id)), closure);
                            
//#line 722 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                        }
                        
//#line 724 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(body)));
                    }
                    
                    
//#line 730 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                             runUncountedAsync(
                                                                                                             final x10.core.fun.VoidFun_0_0 body){
                        
//#line 732 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                          x10.lang.Runtime.activity();
                        
//#line 733 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.ensureNotInAtomic();
                        
//#line 735 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.executeLocal(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(body)),
                                                                                                                                                                                                                         ((x10.lang.FinishState)(new x10.lang.FinishState.UncountedFinish((java.lang.System[]) null).$init()))))));
                    }
                    
                    
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
@x10.core.X10Generated public static class RemoteControl extends x10.util.concurrent.SimpleLatch implements x10.lang.Runtime.Mortal
                                                                                                           {
                        private static final long serialVersionUID = 1L;
                        private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteControl.class);
                        
                        public static final x10.rtt.RuntimeType<RemoteControl> $RTT = x10.rtt.NamedType.<RemoteControl> make(
                        "x10.lang.Runtime.RemoteControl", /* base class */RemoteControl.class
                        , /* parents */ new x10.rtt.Type[] {x10.lang.Runtime.Mortal.$RTT, x10.util.concurrent.SimpleLatch.$RTT}
                        );
                        public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                        
                        
                        // custom serializer
                        private transient x10.io.SerialData $$serialdata;
                        private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
                        private Object readResolve() { return new RemoteControl($$serialdata); }
                        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
                        oos.writeObject($$serialdata); }
                        private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
                        $$serialdata = (x10.io.SerialData) ois.readObject(); }
                        // default deserialization constructor
                        public RemoteControl(final x10.io.SerialData a) { super(a); {
                            
//#line 742 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                            
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55881();
                        }}
                        public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteControl $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + RemoteControl.class + " calling"); } 
                            x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                            $_obj.$init($$serialdata);
                            return $_obj;
                            
                        }
                        
                        public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            RemoteControl $_obj = new RemoteControl((java.lang.System[]) null);
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
                        public RemoteControl(final java.lang.System[] $dummy) { 
                        super($dummy);
                        }
                        
                            
                            
//#line 742 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public RemoteControl() {super();
                                                                                                                                                {
                                                                                                                                                   
//#line 742 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                                                                   
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55881();
                                                                                                                                               }}
                            
                            
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public RemoteControl(final java.lang.Object id$41) {super();
                                                                                                                                                                            {
                                                                                                                                                                               
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                                                                                               
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55881();
                                                                                                                                                                               
//#line 744 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(this))));
                                                                                                                                                                           }}
                            
                            
//#line 746 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.core.X10Throwable e;
                            
//#line 747 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.lang.Activity.ClockPhases clockPhases;
                            
                            
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public x10.lang.Runtime.RemoteControl
                                                                                                                     x10$lang$Runtime$RemoteControl$$x10$lang$Runtime$RemoteControl$this(
                                                                                                                     ){
                                
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.RemoteControl.this;
                            }
                            
                            
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public void
                                                                                                                     __fieldInitializers55881(
                                                                                                                     ){
                                
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.e = null;
                                
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.clockPhases = null;
                            }
                            
                            }
                            
                        
                        
//#line 753 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                 runAt(
                                                                                                                 final x10.lang.Place place,
                                                                                                                 final x10.core.fun.VoidFun_0_0 body){
                            
//#line 754 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.ensureNotInAtomic();
                            
//#line 755 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) place.
                                                                                                                                id) ==
                                                                                                                       ((int) x10.lang.Runtime.hereInt$O())) {
                                
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {try {{
                                    
//#line 757 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)x10.runtime.impl.java.Runtime.<x10.core.fun.VoidFun_0_0>deepCopy(body)).$apply();
                                    
//#line 758 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return;
                                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable t) {
                                    
//#line 760 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw x10.runtime.impl.java.Runtime.<x10.core.X10Throwable>deepCopy(t);
                                }
                            }
                            
//#line 763 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.RemoteControl me =
                              ((x10.lang.Runtime.RemoteControl)(new x10.lang.Runtime.RemoteControl()));
                            
//#line 764 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box =
                              new x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>(x10.lang.Runtime.RemoteControl.$RTT, ((x10.lang.Runtime.RemoteControl)
                                                                                                                            me), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null);
                            
//#line 765 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity.ClockPhases clockPhases =
                              ((x10.lang.Activity.ClockPhases)(x10.lang.Runtime.activity().
                                                                 clockPhases));
                            
//#line 766 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(place)),
                                                                                                                                             ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$150(clockPhases,
                                                                                                                                                                                                           body,
                                                                                                                                                                                                           box, (x10.lang.Runtime.$Closure$150.__2$1x10$lang$Runtime$RemoteControl$2) null))));
                            
//#line 789 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me.await();
                            
//#line 790 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(body)));
                            
//#line 791 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.activity().clockPhases = ((x10.lang.Activity.ClockPhases)(me.
                                                                                                                                                                                                clockPhases));
                            
//#line 792 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((null) != (me.
                                                                                                                                     e))) {
                                
//#line 793 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw me.
                                                                                                                               e;
                            }
                        }
                        
                        
//#line 801 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                 runAtSimple(
                                                                                                                 final x10.lang.Place place,
                                                                                                                 final x10.core.fun.VoidFun_0_0 body,
                                                                                                                 final boolean toWait){
                            
//#line 803 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) place.
                                                                                                                                id) ==
                                                                                                                       ((int) x10.lang.Runtime.hereInt$O())) {
                                
//#line 804 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)x10.runtime.impl.java.Runtime.<x10.core.fun.VoidFun_0_0>deepCopy(body)).$apply();
                                
//#line 805 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return;
                            }
                            
//#line 807 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (toWait) {
                                
//#line 808 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.RemoteControl me =
                                  ((x10.lang.Runtime.RemoteControl)(new x10.lang.Runtime.RemoteControl()));
                                
//#line 809 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box =
                                  new x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>(x10.lang.Runtime.RemoteControl.$RTT, ((x10.lang.Runtime.RemoteControl)
                                                                                                                                me), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null);
                                
//#line 810 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 latchedBody =
                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$152(body,
                                                                                                box, (x10.lang.Runtime.$Closure$152.__1$1x10$lang$Runtime$RemoteControl$2) null)));
                                
//#line 819 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(place.
                                                                                                                                                                           id)), latchedBody);
                                
//#line 820 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(latchedBody)));
                                
//#line 821 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me.await();
                            } else {
                                
//#line 823 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 simpleBody =
                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$153(body)));
                                
//#line 824 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(place.
                                                                                                                                                                           id)), simpleBody);
                                
//#line 825 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(simpleBody)));
                            }
                            
//#line 828 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(body)));
                        }
                        
                        
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
@x10.core.X10Generated public static class Remote<$T> extends x10.lang.Runtime.RemoteControl implements x10.x10rt.X10JavaSerializable
                                                                                                               {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Remote.class);
                            
                            public static final x10.rtt.RuntimeType<Remote> $RTT = x10.rtt.NamedType.<Remote> make(
                            "x10.lang.Runtime.Remote", /* base class */Remote.class, 
                            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                            , /* parents */ new x10.rtt.Type[] {x10.lang.Runtime.RemoteControl.$RTT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                            // custom serializer
                            private transient x10.io.SerialData $$serialdata;
                            private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
                            private Object readResolve() { return new Remote($T, $$serialdata); }
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
                            oos.writeObject($T);
                            oos.writeObject($$serialdata); }
                            private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
                            $T = (x10.rtt.Type) ois.readObject();
                            $$serialdata = (x10.io.SerialData) ois.readObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Remote $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Remote.class + " calling"); } 
                                x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                                x10.rtt.Type $T = ( x10.rtt.Type ) $deserializer.readRef();
                                $_obj.$T = $T;
                                $_obj.$init($$serialdata);
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                Remote $_obj = new Remote((java.lang.System[]) null, (x10.rtt.Type) null);
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
                                $serializer.write($T);
                                
                            }
                            
                            // dummy 2nd-phase constructor for non-splittable type
                            public void $init(x10.io.SerialData $$serialdata) {
                            
                                throw new x10.lang.RuntimeException("dummy 2nd-phase constructor for non-splittable type should never be called.");
                                
                            }
                            
                            // constructor just for allocation
                            public Remote(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                            super($dummy);
                            x10.lang.Runtime.Remote.$initParams(this, $T);
                            }
                            
                                private x10.rtt.Type $T;
                                // initializer of type parameters
                                public static void $initParams(final Remote $this, final x10.rtt.Type $T) {
                                $this.$T = $T;
                                }
                                
                                
                                
//#line 835 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public Remote(final x10.rtt.Type $T) {super();
                                                                                                                                                                 x10.lang.Runtime.Remote.$initParams(this, $T);
                                                                                                                                                                  {
                                                                                                                                                                     
//#line 835 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                                                                                     
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55882();
                                                                                                                                                                 }}
                                
                                
//#line 836 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public Remote(final x10.rtt.Type $T,
                                                                                                                                     final x10.io.SerialData id$42) {super();
                                                                                                                                                                         x10.lang.Runtime.Remote.$initParams(this, $T);
                                                                                                                                                                          {
                                                                                                                                                                             
//#line 836 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                                                                                                             
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.__fieldInitializers55882();
                                                                                                                                                                             
//#line 837 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.UnsupportedOperationException((("Cannot deserialize ") + (x10.rtt.Types.typeName(((x10.core.RefI)(this))))));
                                                                                                                                                                         }}
                                
                                
//#line 839 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public x10.util.Box<$T> t;
                                
                                
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public x10.lang.Runtime.Remote<$T>
                                                                                                                         x10$lang$Runtime$Remote$$x10$lang$Runtime$Remote$this(
                                                                                                                         ){
                                    
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.Remote.this;
                                }
                                
                                
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public void
                                                                                                                         __fieldInitializers55882(
                                                                                                                         ){
                                    
//#line 834 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
this.t = null;
                                }
                                
                                }
                                
                            
                            
//#line 845 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static <$T>$T
                                                                                                                     evalAt__1$1x10$lang$Runtime$$T$2$G(
                                                                                                                     final x10.rtt.Type $T,
                                                                                                                     final x10.lang.Place place,
                                                                                                                     final x10.core.fun.Fun_0_0<$T> eval){
                                
//#line 846 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.ensureNotInAtomic();
                                
//#line 847 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((int) place.
                                                                                                                                    id) ==
                                                                                                                           ((int) x10.lang.Runtime.hereInt$O())) {
                                    
//#line 848 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {try {{
                                        
//#line 850 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.runtime.impl.java.Runtime.<$T>deepCopy(((x10.core.fun.Fun_0_0<$T>)x10.runtime.impl.java.Runtime.<x10.core.fun.Fun_0_0<$T>>deepCopy(eval)).$apply$G());
                                    }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable t) {
                                        
//#line 852 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw x10.runtime.impl.java.Runtime.<x10.core.X10Throwable>deepCopy(t);
                                    }
                                }
                                
//#line 855 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Remote<$T> me =
                                  ((x10.lang.Runtime.Remote)(new x10.lang.Runtime.Remote<$T>($T)));
                                
//#line 856 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box =
                                  ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>(x10.rtt.ParameterizedType.make(x10.lang.Runtime.Remote.$RTT, $T), ((x10.lang.Runtime.Remote<$T>)
                                                                                                                                                                                me), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                                
//#line 857 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity.ClockPhases clockPhases =
                                  ((x10.lang.Activity.ClockPhases)(x10.lang.Runtime.activity().
                                                                     clockPhases));
                                
//#line 858 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(place)),
                                                                                                                                                 ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$156<$T>($T, clockPhases,
                                                                                                                                                                                                                   eval,
                                                                                                                                                                                                                   box, (x10.lang.Runtime.$Closure$156.__1$1x10$lang$Runtime$$Closure$156$$T$2__2$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$156$$T$2$2) null))));
                                
//#line 883 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me.await();
                                
//#line 884 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.<$T>dealloc__0$1x10$lang$Runtime$$T$2($T, ((x10.core.fun.Fun_0_0)(eval)));
                                
//#line 885 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.activity().clockPhases = ((x10.lang.Activity.ClockPhases)(me.
                                                                                                                                                                                                    clockPhases));
                                
//#line 886 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((null) != (me.
                                                                                                                                         e))) {
                                    
//#line 887 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw me.
                                                                                                                                   e;
                                }
                                
//#line 889 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return ((x10.util.Box<$T>)((x10.lang.Runtime.Remote<$T>)me).
                                                                                                                                                   t).
                                                                                                                                value;
                            }
                            
                            
//#line 894 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     StaticInitBroadcastDispatcherLock(
                                                                                                                     ){
                                
//#line 895 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.staticMonitor.lock();
                            }
                            
                            
//#line 898 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     StaticInitBroadcastDispatcherAwait(
                                                                                                                     ){
                                
//#line 899 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.staticMonitor.await();
                            }
                            
                            
//#line 902 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     StaticInitBroadcastDispatcherUnlock(
                                                                                                                     ){
                                
//#line 903 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.staticMonitor.unlock();
                            }
                            
                            
//#line 906 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     StaticInitBroadcastDispatcherNotify(
                                                                                                                     ){
                                
//#line 907 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.staticMonitor.release();
                            }
                            
                            
//#line 912 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     enterAtomic(
                                                                                                                     ){
                                
//#line 913 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.atomicMonitor.lock();
                                
//#line 914 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                                  x10.lang.Runtime.activity();
                                
//#line 915 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((a) != (null))) {
                                    
//#line 916 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.pushAtomic();
                                }
                            }
                            
                            
//#line 919 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     ensureNotInAtomic(
                                                                                                                     ){
                                
//#line 920 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                                  x10.lang.Runtime.activity();
                                
//#line 921 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((a) != (null))) {
                                    
//#line 922 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.ensureNotInAtomic();
                                }
                            }
                            
                            
//#line 925 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     exitAtomic(
                                                                                                                     ){
                                
//#line 926 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                                  x10.lang.Runtime.activity();
                                
//#line 927 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((a) != (null))) {
                                    
//#line 928 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.popAtomic();
                                }
                                
//#line 929 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((null) != (x10.lang.Runtime.pool.
                                                                                                                                         wsBlockedContinuations))) {
                                    
//#line 929 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.wsUnblock();
                                }
                                
//#line 930 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.atomicMonitor.release();
                            }
                            
                            
//#line 933 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     exitWSWhen(
                                                                                                                     final boolean b){
                                
//#line 934 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                                  x10.lang.Runtime.activity();
                                
//#line 935 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (((a) != (null))) {
                                    
//#line 936 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
a.popAtomic();
                                }
                                
//#line 937 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (b) {
                                    
//#line 938 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.wsUnblock();
                                    
//#line 939 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.atomicMonitor.release();
                                } else {
                                    
//#line 941 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.atomicMonitor.unlock();
                                }
                            }
                            
                            
//#line 945 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     awaitAtomic(
                                                                                                                     ){
                                
//#line 946 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.atomicMonitor.await();
                            }
                            
                            
//#line 955 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.FinishState
                                                                                                                     startFinish(
                                                                                                                     ){
                                
//#line 956 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.activity().swapFinish(((x10.lang.FinishState)(new x10.lang.FinishState.Finish((java.lang.System[]) null).$init())));
                            }
                            
                            
//#line 959 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.FinishState
                                                                                                                     startFinish(
                                                                                                                     final int pragma){
                                
//#line 960 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState f;
                                
//#line 961 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
switch (pragma) {
                                    
//#line 962 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
case x10.compiler.Pragma.FINISH_ASYNC:
                                        
//#line 963 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
f = ((x10.lang.FinishState)(new x10.lang.FinishState.FinishAsync((java.lang.System[]) null).$init()));
                                        
//#line 963 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
break;
                                    
//#line 964 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
case x10.compiler.Pragma.FINISH_HERE:
                                        
//#line 965 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
f = ((x10.lang.FinishState)(new x10.lang.FinishState.FinishHere((java.lang.System[]) null).$init()));
                                        
//#line 965 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
break;
                                    
//#line 966 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
case x10.compiler.Pragma.FINISH_SPMD:
                                        
//#line 967 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
f = ((x10.lang.FinishState)(new x10.lang.FinishState.FinishSPMD((java.lang.System[]) null).$init()));
                                        
//#line 967 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
break;
                                    
//#line 968 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
case x10.compiler.Pragma.FINISH_LOCAL:
                                        
//#line 969 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
f = ((x10.lang.FinishState)(new x10.lang.FinishState.LocalFinish((java.lang.System[]) null).$init()));
                                        
//#line 969 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
break;
                                    
//#line 970 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
default:
                                        
//#line 971 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
f = ((x10.lang.FinishState)(new x10.lang.FinishState.Finish((java.lang.System[]) null).$init()));
                                }
                                
//#line 973 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.activity().swapFinish(((x10.lang.FinishState)(f)));
                            }
                            
                            
//#line 976 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.FinishState
                                                                                                                     startLocalFinish(
                                                                                                                     ){
                                
//#line 977 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.activity().swapFinish(((x10.lang.FinishState)(new x10.lang.FinishState.LocalFinish((java.lang.System[]) null).$init())));
                            }
                            
                            
//#line 980 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.FinishState
                                                                                                                     startSimpleFinish(
                                                                                                                     ){
                                
//#line 981 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.activity().swapFinish(((x10.lang.FinishState)(new x10.lang.FinishState.Finish((java.lang.System[]) null).$init())));
                            }
                            
                            
//#line 990 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                     stopFinish(
                                                                                                                     final x10.lang.FinishState f){
                                
//#line 991 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Activity a =
                                  x10.lang.Runtime.activity();
                                
//#line 992 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState finishState =
                                  ((x10.lang.FinishState)(a.swapFinish(((x10.lang.FinishState)(f)))));
                                
//#line 993 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
finishState.waitForFinish();
                                
//#line 994 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.deallocObject(((x10.core.RefI)(finishState)));
                            }
                            
                            
//#line 1001 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                      pushException(
                                                                                                                      final x10.core.X10Throwable t){
                                
//#line 1002 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.activity().finishState().pushException(((x10.core.X10Throwable)(t)));
                            }
                            
                            
//#line 1004 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static <$T>x10.lang.FinishState
                                                                                                                      startCollectingFinish__0$1x10$lang$Runtime$$T$2(
                                                                                                                      final x10.rtt.Type $T,
                                                                                                                      final x10.lang.Reducible<$T> r){
                                
//#line 1005 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.activity().swapFinish(((x10.lang.FinishState)(new x10.lang.FinishState.CollectingFinish<$T>((java.lang.System[]) null, $T).$init(((x10.lang.Reducible<$T>)(r)), (x10.lang.FinishState.CollectingFinish.__0$1x10$lang$FinishState$CollectingFinish$$T$2) null))));
                            }
                            
                            
//#line 1008 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static <$T>void
                                                                                                                      makeOffer__0x10$lang$Runtime$$T(
                                                                                                                      final x10.rtt.Type $T,
                                                                                                                      final $T t){
                                
//#line 1009 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState state =
                                  x10.lang.Runtime.activity().finishState();
                                
//#line 1011 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.lang.FinishState.CollectingFinish<$T>)x10.rtt.Types.<x10.lang.FinishState.CollectingFinish<$T>> cast(state,x10.rtt.ParameterizedType.make(x10.lang.FinishState.CollectingFinish.$RTT, $T))).accept__0x10$lang$FinishState$CollectingFinish$$T((($T)(t)),
                                                                                                                                                                                                                                                                                                                                                                            (int)(x10.lang.Runtime.workerId$O()));
                            }
                            
                            
//#line 1014 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static <$T>$T
                                                                                                                      stopCollectingFinish$G(
                                                                                                                      final x10.rtt.Type $T,
                                                                                                                      final x10.lang.FinishState f){
                                
//#line 1015 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState state =
                                  ((x10.lang.FinishState)(x10.lang.Runtime.activity().swapFinish(((x10.lang.FinishState)(f)))));
                                
//#line 1016 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return ((x10.lang.FinishState.CollectingFinish<$T>)x10.rtt.Types.<x10.lang.FinishState.CollectingFinish<$T>> cast(state,x10.rtt.ParameterizedType.make(x10.lang.FinishState.CollectingFinish.$RTT, $T))).waitForFinishExpr$G();
                            }
                            
                            
//#line 1020 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                      execute(
                                                                                                                      final x10.lang.Activity activity){
                                
//#line 1021 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.worker().push(((x10.lang.Activity)(activity)));
                            }
                            
                            
//#line 1024 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                      executeLocal(
                                                                                                                      final x10.lang.Activity activity){
                                
//#line 1025 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.lang.Runtime.pool.deal$O(((x10.lang.Activity)(activity))))) {
                                    
//#line 1025 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.worker().push(((x10.lang.Activity)(activity)));
                                }
                            }
                            
                            
//#line 1029 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                      execute(
                                                                                                                      final x10.core.fun.VoidFun_0_0 body,
                                                                                                                      final x10.lang.FinishState finishState){
                                
//#line 1030 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.execute(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(((x10.core.fun.VoidFun_0_0)(body)),
                                                                                                                                                                                                                             ((x10.lang.FinishState)(finishState))))));
                            }
                            
                            
//#line 1033 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                      probe(
                                                                                                                      ){
                                
//#line 1034 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.worker().probe();
                            }
                            
                            
//#line 1038 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                      increaseParallelism(
                                                                                                                      ){
                                
//#line 1039 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.lang.Runtime.STATIC_THREADS)) {
                                    
//#line 1040 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.increase();
                                }
                            }
                            
                            
//#line 1045 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static void
                                                                                                                      decreaseParallelism(
                                                                                                                      final int n){
                                
//#line 1046 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.lang.Runtime.STATIC_THREADS)) {
                                    
//#line 1047 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.decrease((int)(n));
                                }
                            }
                            
                            
//#line 1051 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
public static x10.lang.Runtime.Worker
                                                                                                                      wrapNativeThread(
                                                                                                                      ){
                                
//#line 1052 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.pool.wrapNativeThread();
                            }
                            
                            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final public x10.lang.Runtime
                                                                                                                    x10$lang$Runtime$$x10$lang$Runtime$this(
                                                                                                                    ){
                                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return x10.lang.Runtime.this;
                            }
                            
                            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
// creation method for java code (1-phase java constructor)
                            public Runtime(){this((java.lang.System[]) null);
                                                 $init();}
                            
                            // constructor for non-virtual call
                            final public x10.lang.Runtime x10$lang$Runtime$$init$S() { {
                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                              
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"

                                                                                          }
                                                                                          return this;
                                                                                          }
                            
                            // constructor
                            public x10.lang.Runtime $init(){return x10$lang$Runtime$$init$S();}
                            
                            
                            final private static x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>
                              $closure_apply56567__0$1x10$lang$Runtime$RemoteControl$2(
                              final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> __desugarer__var__52__){
                                
//#line 771 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.rtt.Equality.equalsequals(((__desugarer__var__52__).home),(x10.lang.Runtime.home())))) {
                                    
//#line 771 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.Runtime.RemoteControl]{self.home==here}");
                                }
                                
//#line 771 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return __desugarer__var__52__;
                            }
                            
                            final public static x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>
                              $closure_apply56567$P__0$1x10$lang$Runtime$RemoteControl$2(
                              final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> __desugarer__var__52__){
                                return x10.lang.Runtime.$closure_apply56567__0$1x10$lang$Runtime$RemoteControl$2(((x10.core.GlobalRef)(__desugarer__var__52__)));
                            }
                            
                            final private static x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>
                              $closure_apply56568__0$1x10$lang$Runtime$RemoteControl$2(
                              final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> __desugarer__var__53__){
                                
//#line 779 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.rtt.Equality.equalsequals(((__desugarer__var__53__).home),(x10.lang.Runtime.home())))) {
                                    
//#line 779 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.Runtime.RemoteControl]{self.home==here}");
                                }
                                
//#line 779 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return __desugarer__var__53__;
                            }
                            
                            final public static x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>
                              $closure_apply56568$P__0$1x10$lang$Runtime$RemoteControl$2(
                              final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> __desugarer__var__53__){
                                return x10.lang.Runtime.$closure_apply56568__0$1x10$lang$Runtime$RemoteControl$2(((x10.core.GlobalRef)(__desugarer__var__53__)));
                            }
                            
                            final private static x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>
                              $closure_apply56569__0$1x10$lang$Runtime$RemoteControl$2(
                              final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> __desugarer__var__54__){
                                
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.rtt.Equality.equalsequals(((__desugarer__var__54__).home),(x10.lang.Runtime.home())))) {
                                    
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.Runtime.RemoteControl]{self.home==here}");
                                }
                                
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return __desugarer__var__54__;
                            }
                            
                            final public static x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>
                              $closure_apply56569$P__0$1x10$lang$Runtime$RemoteControl$2(
                              final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> __desugarer__var__54__){
                                return x10.lang.Runtime.$closure_apply56569__0$1x10$lang$Runtime$RemoteControl$2(((x10.core.GlobalRef)(__desugarer__var__54__)));
                            }
                            
                            final private static <$T>x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>
                              $closure_apply56570__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2(
                              final x10.rtt.Type $T,
                              final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> __desugarer__var__55__){
                                
//#line 863 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.rtt.Equality.equalsequals(((__desugarer__var__55__).home),(x10.lang.Runtime.home())))) {
                                    
//#line 863 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.Runtime.Remote[T]]{self.home==here}");
                                }
                                
//#line 863 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return __desugarer__var__55__;
                            }
                            
                            final public static <$T>x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>
                              $closure_apply56570$P__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2(
                              final x10.rtt.Type $T,
                              final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> __desugarer__var__55__){
                                return x10.lang.Runtime.<$T>$closure_apply56570__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2($T, ((x10.core.GlobalRef)(__desugarer__var__55__)));
                            }
                            
                            final private static <$T>$T
                              $closure_apply56571__0x10$lang$Runtime$$T__1$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2$G(
                              final x10.rtt.Type $T,
                              final $T __desugarer__var__56__,
                              final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box){
                                
//#line 865 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.rtt.Equality.equalsequals(((box).home),(x10.lang.Runtime.home())))) {
                                    
//#line 865 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.FailedDynamicCheckException("T{box.home==here}");
                                }
                                
//#line 865 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return (($T)
                                                                                                                                __desugarer__var__56__);
                            }
                            
                            final public static <$T>$T
                              $closure_apply56571$P__0x10$lang$Runtime$$T__1$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2$G(
                              final x10.rtt.Type $T,
                              final $T __desugarer__var__56__,
                              final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box){
                                return x10.lang.Runtime.<$T>$closure_apply56571__0x10$lang$Runtime$$T__1$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2$G($T, (($T)(__desugarer__var__56__)),
                                                                                                                                                             ((x10.core.GlobalRef)(box)));
                            }
                            
                            final private static <$T>x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>
                              $closure_apply56572__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2(
                              final x10.rtt.Type $T,
                              final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> __desugarer__var__57__){
                                
//#line 873 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
if (!(x10.rtt.Equality.equalsequals(((__desugarer__var__57__).home),(x10.lang.Runtime.home())))) {
                                    
//#line 873 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.Runtime.Remote[T]]{self.home==here}");
                                }
                                
//#line 873 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
return __desugarer__var__57__;
                            }
                            
                            final public static <$T>x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>
                              $closure_apply56572$P__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2(
                              final x10.rtt.Type $T,
                              final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> __desugarer__var__57__){
                                return x10.lang.Runtime.<$T>$closure_apply56572__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2($T, ((x10.core.GlobalRef)(__desugarer__var__57__)));
                            }
                            
                            @x10.core.X10Generated public static class $Closure$144 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                            {
                                private static final long serialVersionUID = 1L;
                                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$144.class);
                                
                                public static final x10.rtt.RuntimeType<$Closure$144> $RTT = x10.rtt.StaticVoidFunType.<$Closure$144> make(
                                /* base class */$Closure$144.class
                                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                );
                                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                
                                
                                private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$144 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                
                                    if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$144.class + " calling"); } 
                                    x10.core.fun.VoidFun_0_0 init = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
                                    $_obj.init = init;
                                    x10.core.fun.VoidFun_0_0 body = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
                                    $_obj.body = body;
                                    return $_obj;
                                    
                                }
                                
                                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                
                                    $Closure$144 $_obj = new $Closure$144((java.lang.System[]) null);
                                    $deserializer.record_reference($_obj);
                                    return $_deserialize_body($_obj, $deserializer);
                                    
                                }
                                
                                public short $_get_serialization_id() {
                                
                                     return $_serialization_id;
                                    
                                }
                                
                                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                
                                    if (init instanceof x10.x10rt.X10JavaSerializable) {
                                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                                    } else {
                                    $serializer.write(this.init);
                                    }
                                    if (body instanceof x10.x10rt.X10JavaSerializable) {
                                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.body);
                                    } else {
                                    $serializer.write(this.body);
                                    }
                                    
                                }
                                
                                // constructor just for allocation
                                public $Closure$144(final java.lang.System[] $dummy) { 
                                super($dummy);
                                }
                                
                                    
                                    public void
                                      $apply(
                                      ){
                                        {
                                            
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.ensureNotInAtomic();
                                            
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.FinishState x10$__var10 =
                                              x10.lang.Runtime.startFinish();
                                            
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {try {{
                                                {
                                                    
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)this.
                                                                                                                                                                        init).$apply();
                                                }
                                            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                                                
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                                                
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
throw new x10.lang.Exception();
                                            }finally {{
                                                 
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var10)));
                                             }}
                                            }
                                        
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)this.
                                                                                                                                                            body).$apply();
                                        }
                                    
                                    public x10.core.fun.VoidFun_0_0 init;
                                    public x10.core.fun.VoidFun_0_0 body;
                                    
                                    public $Closure$144(final x10.core.fun.VoidFun_0_0 init,
                                                        final x10.core.fun.VoidFun_0_0 body) { {
                                                                                                      this.init = ((x10.core.fun.VoidFun_0_0)(init));
                                                                                                      this.body = ((x10.core.fun.VoidFun_0_0)(body));
                                                                                                  }}
                                    
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$145 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$145.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$145> $RTT = x10.rtt.StaticVoidFunType.<$Closure$145> make(
                                    /* base class */$Closure$145.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$145 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$145.class + " calling"); } 
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$145 $_obj = new $Closure$145((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$145(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 626 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.pool.
                                                                                                                                     latch.release();
                                        }
                                        
                                        public $Closure$145() { {
                                                                       
                                                                   }}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$146 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$146.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$146> $RTT = x10.rtt.StaticVoidFunType.<$Closure$146> make(
                                    /* base class */$Closure$146.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$146 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$146.class + " calling"); } 
                                        x10.core.fun.VoidFun_0_0 body = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
                                        $_obj.body = body;
                                        x10.lang.FinishState state = (x10.lang.FinishState) $deserializer.readRef();
                                        $_obj.state = state;
                                        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
                                        $_obj.clockPhases = clockPhases;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$146 $_obj = new $Closure$146((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (body instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.body);
                                        } else {
                                        $serializer.write(this.body);
                                        }
                                        if (state instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.state);
                                        } else {
                                        $serializer.write(this.state);
                                        }
                                        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
                                        } else {
                                        $serializer.write(this.clockPhases);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$146(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.execute(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(this.
                                                                                                                                                                                                                                          body,
                                                                                                                                                                                                                                        this.
                                                                                                                                                                                                                                          state,
                                                                                                                                                                                                                                        ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                                                                                                                                           clockPhases))))));
                                        }
                                        
                                        public x10.core.fun.VoidFun_0_0 body;
                                        public x10.lang.FinishState state;
                                        public x10.lang.Activity.ClockPhases clockPhases;
                                        
                                        public $Closure$146(final x10.core.fun.VoidFun_0_0 body,
                                                            final x10.lang.FinishState state,
                                                            final x10.lang.Activity.ClockPhases clockPhases) { {
                                                                                                                      this.body = ((x10.core.fun.VoidFun_0_0)(body));
                                                                                                                      this.state = ((x10.lang.FinishState)(state));
                                                                                                                      this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                  }}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$147 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$147.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$147> $RTT = x10.rtt.StaticVoidFunType.<$Closure$147> make(
                                    /* base class */$Closure$147.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$147 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$147.class + " calling"); } 
                                        x10.core.fun.VoidFun_0_0 body = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
                                        $_obj.body = body;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$147 $_obj = new $Closure$147((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (body instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.body);
                                        } else {
                                        $serializer.write(this.body);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$147(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 720 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.execute(((x10.lang.Activity)(new x10.lang.Activity((java.lang.System[]) null).$init(this.
                                                                                                                                                                                                                                          body,
                                                                                                                                                                                                                                        ((x10.lang.FinishState)(x10.lang.FinishState.getInitialized$UNCOUNTED_FINISH()))))));
                                        }
                                        
                                        public x10.core.fun.VoidFun_0_0 body;
                                        
                                        public $Closure$147(final x10.core.fun.VoidFun_0_0 body) { {
                                                                                                          this.body = ((x10.core.fun.VoidFun_0_0)(body));
                                                                                                      }}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$148 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$148.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$148> $RTT = x10.rtt.StaticVoidFunType.<$Closure$148> make(
                                    /* base class */$Closure$148.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$148 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$148.class + " calling"); } 
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
                                        $_obj.clockPhases = clockPhases;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$148 $_obj = new $Closure$148((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
                                        } else {
                                        $serializer.write(this.clockPhases);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$148(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 771 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.RemoteControl me2 =
                                              (((x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>)(x10.lang.Runtime.$closure_apply56567__0$1x10$lang$Runtime$RemoteControl$2(((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.Runtime.RemoteControl.$RTT),this.
                                                                                                                                                                                                                                                                                                                                                box)))))))).$apply$G();
                                            
//#line 772 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.clockPhases = ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                                                        clockPhases));
                                            
//#line 773 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.release();
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box;
                                        public x10.lang.Activity.ClockPhases clockPhases;
                                        
                                        public $Closure$148(final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box,
                                                            final x10.lang.Activity.ClockPhases clockPhases, __0$1x10$lang$Runtime$RemoteControl$2 $dummy) { {
                                                                                                                                                                    this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                    this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                                                                }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$Runtime$RemoteControl$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$149 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$149.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$149> $RTT = x10.rtt.StaticVoidFunType.<$Closure$149> make(
                                    /* base class */$Closure$149.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$149 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$149.class + " calling"); } 
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        x10.core.X10Throwable e = (x10.core.X10Throwable) $deserializer.readRef();
                                        $_obj.e = e;
                                        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
                                        $_obj.clockPhases = clockPhases;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$149 $_obj = new $Closure$149((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        if (e instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.e);
                                        } else {
                                        $serializer.write(this.e);
                                        }
                                        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
                                        } else {
                                        $serializer.write(this.clockPhases);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$149(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 779 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.RemoteControl me2 =
                                              (((x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>)(x10.lang.Runtime.$closure_apply56568__0$1x10$lang$Runtime$RemoteControl$2(((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.Runtime.RemoteControl.$RTT),this.
                                                                                                                                                                                                                                                                                                                                                box)))))))).$apply$G();
                                            
//#line 780 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.e = ((x10.core.X10Throwable)(this.
                                                                                                                                                                      e));
                                            
//#line 781 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.clockPhases = ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                                                        clockPhases));
                                            
//#line 782 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.release();
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box;
                                        public x10.core.X10Throwable e;
                                        public x10.lang.Activity.ClockPhases clockPhases;
                                        
                                        public $Closure$149(final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box,
                                                            final x10.core.X10Throwable e,
                                                            final x10.lang.Activity.ClockPhases clockPhases, __0$1x10$lang$Runtime$RemoteControl$2 $dummy) { {
                                                                                                                                                                    this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                    this.e = ((x10.core.X10Throwable)(e));
                                                                                                                                                                    this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                                                                }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$Runtime$RemoteControl$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$150 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$150.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$150> $RTT = x10.rtt.StaticVoidFunType.<$Closure$150> make(
                                    /* base class */$Closure$150.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$150 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$150.class + " calling"); } 
                                        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
                                        $_obj.clockPhases = clockPhases;
                                        x10.core.fun.VoidFun_0_0 body = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
                                        $_obj.body = body;
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$150 $_obj = new $Closure$150((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
                                        } else {
                                        $serializer.write(this.clockPhases);
                                        }
                                        if (body instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.body);
                                        } else {
                                        $serializer.write(this.body);
                                        }
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$150(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 767 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.activity().clockPhases = ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                                                                                clockPhases));
                                            
//#line 768 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {try {{
                                                
//#line 769 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)this.
                                                                                                                                                                    body).$apply();
                                                
//#line 770 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 closure =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$148(((x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>)(this.
                                                                                                                                                                        box)),
                                                                                                                ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                   clockPhases)), (x10.lang.Runtime.$Closure$148.__0$1x10$lang$Runtime$RemoteControl$2) null)));
                                                
//#line 775 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)((this.
                                                                                                                                                                                            box).home.
                                                                                                                                                                                           id)), closure);
                                                
//#line 776 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                                            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable e) {
                                                
//#line 778 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 closure =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$149(((x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>)(this.
                                                                                                                                                                        box)),
                                                                                                                e,
                                                                                                                ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                   clockPhases)), (x10.lang.Runtime.$Closure$149.__0$1x10$lang$Runtime$RemoteControl$2) null)));
                                                
//#line 784 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)((this.
                                                                                                                                                                                            box).home.
                                                                                                                                                                                           id)), closure);
                                                
//#line 785 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                                            }
                                            
//#line 787 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.activity().clockPhases = null;
                                        }
                                        
                                        public x10.lang.Activity.ClockPhases clockPhases;
                                        public x10.core.fun.VoidFun_0_0 body;
                                        public x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box;
                                        
                                        public $Closure$150(final x10.lang.Activity.ClockPhases clockPhases,
                                                            final x10.core.fun.VoidFun_0_0 body,
                                                            final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box, __2$1x10$lang$Runtime$RemoteControl$2 $dummy) { {
                                                                                                                                                                                 this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                                                                                 this.body = ((x10.core.fun.VoidFun_0_0)(body));
                                                                                                                                                                                 this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                             }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __2$1x10$lang$Runtime$RemoteControl$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$151 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$151.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$151> $RTT = x10.rtt.StaticVoidFunType.<$Closure$151> make(
                                    /* base class */$Closure$151.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$151 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$151.class + " calling"); } 
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$151 $_obj = new $Closure$151((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$151(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 813 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.RemoteControl me2 =
                                              (((x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>)(x10.lang.Runtime.$closure_apply56569__0$1x10$lang$Runtime$RemoteControl$2(((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.Runtime.RemoteControl.$RTT),this.
                                                                                                                                                                                                                                                                                                                                                box)))))))).$apply$G();
                                            
//#line 814 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.release();
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box;
                                        
                                        public $Closure$151(final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box, __0$1x10$lang$Runtime$RemoteControl$2 $dummy) { {
                                                                                                                                                                                 this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                             }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$Runtime$RemoteControl$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$152 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$152.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$152> $RTT = x10.rtt.StaticVoidFunType.<$Closure$152> make(
                                    /* base class */$Closure$152.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$152 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$152.class + " calling"); } 
                                        x10.core.fun.VoidFun_0_0 body = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
                                        $_obj.body = body;
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$152 $_obj = new $Closure$152((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (body instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.body);
                                        } else {
                                        $serializer.write(this.body);
                                        }
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$152(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 811 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)this.
                                                                                                                                                                body).$apply();
                                            
//#line 812 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 closure =
                                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$151(((x10.core.GlobalRef<x10.lang.Runtime.RemoteControl>)(this.
                                                                                                                                                                    box)), (x10.lang.Runtime.$Closure$151.__0$1x10$lang$Runtime$RemoteControl$2) null)));
                                            
//#line 816 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)((this.
                                                                                                                                                                                        box).home.
                                                                                                                                                                                       id)), closure);
                                            
//#line 817 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                                        }
                                        
                                        public x10.core.fun.VoidFun_0_0 body;
                                        public x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box;
                                        
                                        public $Closure$152(final x10.core.fun.VoidFun_0_0 body,
                                                            final x10.core.GlobalRef<x10.lang.Runtime.RemoteControl> box, __1$1x10$lang$Runtime$RemoteControl$2 $dummy) { {
                                                                                                                                                                                 this.body = ((x10.core.fun.VoidFun_0_0)(body));
                                                                                                                                                                                 this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                             }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __1$1x10$lang$Runtime$RemoteControl$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$153 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$153.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$153> $RTT = x10.rtt.StaticVoidFunType.<$Closure$153> make(
                                    /* base class */$Closure$153.class
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$153 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$153.class + " calling"); } 
                                        x10.core.fun.VoidFun_0_0 body = (x10.core.fun.VoidFun_0_0) $deserializer.readRef();
                                        $_obj.body = body;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$153 $_obj = new $Closure$153((java.lang.System[]) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        if (body instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.body);
                                        } else {
                                        $serializer.write(this.body);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$153(final java.lang.System[] $dummy) { 
                                    super($dummy);
                                    }
                                    
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 823 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
((x10.core.fun.VoidFun_0_0)this.
                                                                                                                                                                body).$apply();
                                        }
                                        
                                        public x10.core.fun.VoidFun_0_0 body;
                                        
                                        public $Closure$153(final x10.core.fun.VoidFun_0_0 body) { {
                                                                                                          this.body = ((x10.core.fun.VoidFun_0_0)(body));
                                                                                                      }}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$154<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$154.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$154> $RTT = x10.rtt.StaticVoidFunType.<$Closure$154> make(
                                    /* base class */$Closure$154.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$154 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$154.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        $_obj.result = $deserializer.readRef();
                                        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
                                        $_obj.clockPhases = clockPhases;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$154 $_obj = new $Closure$154((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        if (result instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.result);
                                        } else {
                                        $serializer.write(this.result);
                                        }
                                        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
                                        } else {
                                        $serializer.write(this.clockPhases);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$154(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.Runtime.$Closure$154.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$154 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 863 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Remote<$T> me2 =
                                              (((x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>)(x10.lang.Runtime.<$T>$closure_apply56570__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2($T, ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.rtt.ParameterizedType.make(x10.lang.Runtime.Remote.$RTT, $T)),this.
                                                                                                                                                                                                                                                                                                                                                                                                  box)))))))).$apply$G();
                                            
//#line 865 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.t = ((x10.util.Box)(new x10.util.Box<$T>((java.lang.System[]) null, $T).$init(x10.lang.Runtime.<$T>$closure_apply56571__0x10$lang$Runtime$$T__1$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2$G($T, (($T)((($T)
                                                                                                                                                                                                                                                                                                                                                       this.
                                                                                                                                                                                                                                                                                                                                                         result))),
                                                                                                                                                                                                                                                                                                                                           ((x10.core.GlobalRef)(this.
                                                                                                                                                                                                                                                                                                                                                                   box))), (x10.util.Box.__0x10$util$Box$$T) null)));
                                            
//#line 866 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.clockPhases = ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                                                        clockPhases));
                                            
//#line 867 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.release();
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box;
                                        public $T result;
                                        public x10.lang.Activity.ClockPhases clockPhases;
                                        
                                        public $Closure$154(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box,
                                                            final $T result,
                                                            final x10.lang.Activity.ClockPhases clockPhases, __0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$154$$T$2$2__1x10$lang$Runtime$$Closure$154$$T $dummy) {x10.lang.Runtime.$Closure$154.$initParams(this, $T);
                                                                                                                                                                                                                                 {
                                                                                                                                                                                                                                    this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                                                                                    this.result = (($T)(result));
                                                                                                                                                                                                                                    this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                                                                                                                                }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$154$$T$2$2__1x10$lang$Runtime$$Closure$154$$T {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$155<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$155.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$155> $RTT = x10.rtt.StaticVoidFunType.<$Closure$155> make(
                                    /* base class */$Closure$155.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$155 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$155.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        x10.core.X10Throwable e = (x10.core.X10Throwable) $deserializer.readRef();
                                        $_obj.e = e;
                                        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
                                        $_obj.clockPhases = clockPhases;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$155 $_obj = new $Closure$155((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        if (e instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.e);
                                        } else {
                                        $serializer.write(this.e);
                                        }
                                        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
                                        } else {
                                        $serializer.write(this.clockPhases);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$155(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.Runtime.$Closure$155.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$155 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 873 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.lang.Runtime.Remote<$T> me2 =
                                              (((x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>)(x10.lang.Runtime.<$T>$closure_apply56572__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$T$2$2($T, ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.rtt.ParameterizedType.make(x10.lang.Runtime.Remote.$RTT, $T)),this.
                                                                                                                                                                                                                                                                                                                                                                                                  box)))))))).$apply$G();
                                            
//#line 874 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.e = ((x10.core.X10Throwable)(this.
                                                                                                                                                                      e));
                                            
//#line 875 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.clockPhases = ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                                                        clockPhases));
                                            
//#line 876 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
me2.release();
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box;
                                        public x10.core.X10Throwable e;
                                        public x10.lang.Activity.ClockPhases clockPhases;
                                        
                                        public $Closure$155(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box,
                                                            final x10.core.X10Throwable e,
                                                            final x10.lang.Activity.ClockPhases clockPhases, __0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$155$$T$2$2 $dummy) {x10.lang.Runtime.$Closure$155.$initParams(this, $T);
                                                                                                                                                                                              {
                                                                                                                                                                                                 this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                                                 this.e = ((x10.core.X10Throwable)(e));
                                                                                                                                                                                                 this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                                                                                             }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$155$$T$2$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$156<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$156.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$156> $RTT = x10.rtt.StaticVoidFunType.<$Closure$156> make(
                                    /* base class */$Closure$156.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$156 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$156.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.lang.Activity.ClockPhases clockPhases = (x10.lang.Activity.ClockPhases) $deserializer.readRef();
                                        $_obj.clockPhases = clockPhases;
                                        x10.core.fun.Fun_0_0 eval = (x10.core.fun.Fun_0_0) $deserializer.readRef();
                                        $_obj.eval = eval;
                                        x10.core.GlobalRef box = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.box = box;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$156 $_obj = new $Closure$156((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (clockPhases instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.clockPhases);
                                        } else {
                                        $serializer.write(this.clockPhases);
                                        }
                                        if (eval instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.eval);
                                        } else {
                                        $serializer.write(this.eval);
                                        }
                                        if (box instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.box);
                                        } else {
                                        $serializer.write(this.box);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$156(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.Runtime.$Closure$156.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$156 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 859 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.activity().clockPhases = ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                                                                                clockPhases));
                                            
//#line 860 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
try {try {{
                                                
//#line 861 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final $T result =
                                                  (($T)(((x10.core.fun.Fun_0_0<$T>)this.
                                                                                     eval).$apply$G()));
                                                
//#line 862 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 closure =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$154<$T>($T, ((x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>)(this.
                                                                                                                                                                             box)),
                                                                                                                    result,
                                                                                                                    ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                       clockPhases)), (x10.lang.Runtime.$Closure$154.__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$154$$T$2$2__1x10$lang$Runtime$$Closure$154$$T) null)));
                                                
//#line 869 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)((this.
                                                                                                                                                                                            box).home.
                                                                                                                                                                                           id)), closure);
                                                
//#line 870 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                                            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable e) {
                                                
//#line 872 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
final x10.core.fun.VoidFun_0_0 closure =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.Runtime.$Closure$155<$T>($T, ((x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>>)(this.
                                                                                                                                                                             box)),
                                                                                                                    e,
                                                                                                                    ((x10.lang.Activity.ClockPhases)(this.
                                                                                                                                                       clockPhases)), (x10.lang.Runtime.$Closure$155.__0$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$155$$T$2$2) null)));
                                                
//#line 878 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)((this.
                                                                                                                                                                                            box).home.
                                                                                                                                                                                           id)), closure);
                                                
//#line 879 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                                            }
                                            
//#line 881 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Runtime.x10"
x10.lang.Runtime.activity().clockPhases = null;
                                        }
                                        
                                        public x10.lang.Activity.ClockPhases clockPhases;
                                        public x10.core.fun.Fun_0_0<$T> eval;
                                        public x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box;
                                        
                                        public $Closure$156(final x10.rtt.Type $T,
                                                            final x10.lang.Activity.ClockPhases clockPhases,
                                                            final x10.core.fun.Fun_0_0<$T> eval,
                                                            final x10.core.GlobalRef<x10.lang.Runtime.Remote<$T>> box, __1$1x10$lang$Runtime$$Closure$156$$T$2__2$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$156$$T$2$2 $dummy) {x10.lang.Runtime.$Closure$156.$initParams(this, $T);
                                                                                                                                                                                                                                               {
                                                                                                                                                                                                                                                  this.clockPhases = ((x10.lang.Activity.ClockPhases)(clockPhases));
                                                                                                                                                                                                                                                  this.eval = ((x10.core.fun.Fun_0_0)(eval));
                                                                                                                                                                                                                                                  this.box = ((x10.core.GlobalRef)(box));
                                                                                                                                                                                                                                              }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __1$1x10$lang$Runtime$$Closure$156$$T$2__2$1x10$lang$Runtime$Remote$1x10$lang$Runtime$$Closure$156$$T$2$2 {}
                                        
                                    }
                                    
                                
                                }
                                