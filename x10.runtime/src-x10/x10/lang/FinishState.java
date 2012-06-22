package x10.lang;


@x10.core.X10Generated abstract public class FinishState extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishState.class);
    
    public static final x10.rtt.RuntimeType<FinishState> $RTT = x10.rtt.NamedType.<FinishState> make(
    "x10.lang.FinishState", /* base class */FinishState.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishState $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FinishState.class + " calling"); } 
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public FinishState(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
abstract public void
                                                                                                    notifySubActivitySpawn(
                                                                                                    final x10.lang.Place place);
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
abstract public void
                                                                                                    notifyActivityCreation(
                                                                                                    );
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
abstract public void
                                                                                                    notifyActivityTermination(
                                                                                                    );
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
abstract public void
                                                                                                    pushException(
                                                                                                    final x10.core.X10Throwable t);
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
abstract public void
                                                                                                    waitForFinish(
                                                                                                    );
        
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
abstract public x10.util.concurrent.SimpleLatch
                                                                                                    simpleLatch(
                                                                                                    );
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public static <$T>$T
                                                                                                    deref__0$1x10$lang$FinishState$2$G(
                                                                                                    final x10.rtt.Type $T,
                                                                                                    final x10.core.GlobalRef<x10.lang.FinishState> root){
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> __desugarer__var__46__52226 =
              ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),root))));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.GlobalRef<x10.lang.FinishState> ret52227 =
               null;
            
//#line 36 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52879 =
              ((x10.lang.Place)((__desugarer__var__46__52226).home));
            
//#line 36 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52880 =
              x10.rtt.Equality.equalsequals((t52879),(x10.lang.Runtime.home()));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52881 =
              !(t52880);
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52881) {
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52882 =
                  true;
                
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52882) {
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FailedDynamicCheckException t52883 =
                      new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.FinishState]{self.home==here}");
                    
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t52883;
                }
            }
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52227 = ((x10.core.GlobalRef)(__desugarer__var__46__52226));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52295 =
              ((x10.core.GlobalRef)(ret52227));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52296 =
              (((x10.core.GlobalRef<x10.lang.FinishState>)(t52295))).$apply$G();
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52297 =
              (($T)(x10.rtt.Types.<$T> castConversion(t52296,$T)));
            
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52297;
        }
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class LocalFinish extends x10.lang.FinishState implements x10.x10rt.X10JavaSerializable
                                                                                                  {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LocalFinish.class);
            
            public static final x10.rtt.RuntimeType<LocalFinish> $RTT = x10.rtt.NamedType.<LocalFinish> make(
            "x10.lang.FinishState.LocalFinish", /* base class */LocalFinish.class
            , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(LocalFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LocalFinish.class + " calling"); } 
                x10.lang.FinishState.$_deserialize_body($_obj, $deserializer);
                x10.core.concurrent.AtomicInteger count = (x10.core.concurrent.AtomicInteger) $deserializer.readRef();
                $_obj.count = count;
                x10.util.concurrent.SimpleLatch latch = (x10.util.concurrent.SimpleLatch) $deserializer.readRef();
                $_obj.latch = latch;
                x10.util.Stack exceptions = (x10.util.Stack) $deserializer.readRef();
                $_obj.exceptions = exceptions;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                LocalFinish $_obj = new LocalFinish((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                if (count instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.count);
                } else {
                $serializer.write(this.count);
                }
                if (latch instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.latch);
                } else {
                $serializer.write(this.latch);
                }
                if (exceptions instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.exceptions);
                } else {
                $serializer.write(this.exceptions);
                }
                
            }
            
            // constructor just for allocation
            public LocalFinish(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.concurrent.AtomicInteger count;
                
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch latch;
                
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.Stack<x10.core.X10Throwable> exceptions;
                
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            notifySubActivitySpawn(
                                                                                                            final x10.lang.Place place){
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
assert ((int) place.
                                                                                                                              id) ==
                    ((int) x10.lang.Runtime.hereInt$O());
                    
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52298 =
                      ((x10.core.concurrent.AtomicInteger)(count));
                    
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.util.Util.eval(t52298.getAndIncrement());
                }
                
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            notifyActivityCreation(
                                                                                                            ){
                    
                }
                
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            notifyActivityTermination(
                                                                                                            ){
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52299 =
                      ((x10.core.concurrent.AtomicInteger)(count));
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52300 =
                      t52299.decrementAndGet();
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52302 =
                      ((int) t52300) ==
                    ((int) 0);
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52302) {
                        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52301 =
                          ((x10.util.concurrent.SimpleLatch)(latch));
                        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52301.release();
                    }
                }
                
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            pushException(
                                                                                                            final x10.core.X10Throwable t){
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52303 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52303.lock();
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52304 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52306 =
                      ((null) == (t52304));
                    
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52306) {
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52305 =
                          ((x10.util.Stack)(new x10.util.Stack<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = ((x10.util.Stack)(t52305));
                    }
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52307 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.Stack<x10.core.X10Throwable>)t52307).push__0x10$util$Stack$$T$O(((x10.core.X10Throwable)(t)));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52308 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52308.unlock();
                }
                
                
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            waitForFinish(
                                                                                                            ){
                    
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.notifyActivityTermination();
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52309 =
                      x10.lang.Runtime.STRICT_FINISH;
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52312 =
                      !(t52309);
                    
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52312) {
                        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Runtime.Worker t52310 =
                          x10.lang.Runtime.worker();
                        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52311 =
                          ((x10.util.concurrent.SimpleLatch)(latch));
                        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52310.join(((x10.util.concurrent.SimpleLatch)(t52311)));
                    }
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52313 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52313.await();
                    
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52314 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                      x10.lang.MultipleExceptions.make__0$1x10$lang$Throwable$2(((x10.util.Stack)(t52314)));
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52315 =
                      ((null) != (t));
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52315) {
                        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t;
                    }
                }
                
                
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch
                                                                                                            simpleLatch(
                                                                                                            ){
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52316 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52316;
                }
                
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.LocalFinish
                                                                                                            x10$lang$FinishState$LocalFinish$$x10$lang$FinishState$LocalFinish$this(
                                                                                                            ){
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.LocalFinish.this;
                }
                
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                public LocalFinish(){this((java.lang.System[]) null);
                                         $init();}
                
                // constructor for non-virtual call
                final public x10.lang.FinishState.LocalFinish x10$lang$FinishState$LocalFinish$$init$S() { {
                                                                                                                  
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                  
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                  
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51928();
                                                                                                              }
                                                                                                              return this;
                                                                                                              }
                
                // constructor
                public x10.lang.FinishState.LocalFinish $init(){return x10$lang$FinishState$LocalFinish$$init$S();}
                
                
                
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                            __fieldInitializers51928(
                                                                                                            ){
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.count = ((x10.core.concurrent.AtomicInteger)(new x10.core.concurrent.AtomicInteger(((int)(1)))));
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.latch = ((x10.util.concurrent.SimpleLatch)(new x10.util.concurrent.SimpleLatch()));
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = null;
                }
            
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class FinishSPMD extends x10.lang.FinishState.FinishSkeleton implements x10.io.CustomSerialization
                                                                                                  {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishSPMD.class);
            
            public static final x10.rtt.RuntimeType<FinishSPMD> $RTT = x10.rtt.NamedType.<FinishSPMD> make(
            "x10.lang.FinishState.FinishSPMD", /* base class */FinishSPMD.class
            , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.lang.FinishState.FinishSkeleton.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            // custom serializer
            private transient x10.io.SerialData $$serialdata;
            private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
            private Object readResolve() { return new FinishSPMD($$serialdata); }
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
            oos.writeObject($$serialdata); }
            private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
            $$serialdata = (x10.io.SerialData) ois.readObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishSPMD $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + FinishSPMD.class + " calling"); } 
                x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                $_obj.$init($$serialdata);
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                FinishSPMD $_obj = new FinishSPMD((java.lang.System[]) null);
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
            
            // constructor just for allocation
            public FinishSPMD(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                public FinishSPMD(){this((java.lang.System[]) null);
                                        $init();}
                
                // constructor for non-virtual call
                final public x10.lang.FinishState.FinishSPMD x10$lang$FinishState$FinishSPMD$$init$S() { {
                                                                                                                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinishSPMD t52884 =
                                                                                                                  ((x10.lang.FinishState.RootFinishSPMD)(new x10.lang.FinishState.RootFinishSPMD((java.lang.System[]) null).$init()));
                                                                                                                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.lang.FinishState.RootFinishSkeleton)(t52884)));
                                                                                                                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                            }
                                                                                                            return this;
                                                                                                            }
                
                // constructor
                public x10.lang.FinishState.FinishSPMD $init(){return x10$lang$FinishState$FinishSPMD$$init$S();}
                
                
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                public FinishSPMD(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){this((java.lang.System[]) null);
                                                                                                                              $init(ref, (x10.lang.FinishState.FinishSPMD.__0$1x10$lang$FinishState$2) null);}
                
                // constructor for non-virtual call
                final public x10.lang.FinishState.FinishSPMD x10$lang$FinishState$FinishSPMD$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                      
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                      
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                  }
                                                                                                                                                                                                  return this;
                                                                                                                                                                                                  }
                
                // constructor
                public x10.lang.FinishState.FinishSPMD $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$FinishSPMD$$init$S(ref, $dummy);}
                
                
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                public FinishSPMD(final x10.io.SerialData data){this((java.lang.System[]) null);
                                                                    $init(data);}
                
                // constructor for non-virtual call
                final public x10.lang.FinishState.FinishSPMD x10$lang$FinishState$FinishSPMD$$init$S(final x10.io.SerialData data) {x10$lang$FinishState$FinishSPMD$init_for_reflection(data);
                                                                                                                                        
                                                                                                                                        return this;
                                                                                                                                        }
                public void x10$lang$FinishState$FinishSPMD$init_for_reflection(x10.io.SerialData data) {
                     {
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final java.lang.Object t52890 =
                          ((java.lang.Object)(data.
                                                data));
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52891 =
                          ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52890))));
                        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(t52891)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52320 =
                          ((x10.core.GlobalRef)(ref));
                        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52321 =
                          ((x10.lang.Place)((t52320).home));
                        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52322 =
                          t52321.
                            id;
                        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52323 =
                          x10.lang.Runtime.hereInt$O();
                        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52334 =
                          ((int) t52322) ==
                        ((int) t52323);
                        
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52334) {
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52324 =
                              ((x10.core.GlobalRef)(ref));
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> __desugarer__var__47__52229 =
                              ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52324))));
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.GlobalRef<x10.lang.FinishState> ret52230 =
                               null;
                            
//#line 78 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52885 =
                              ((x10.lang.Place)((__desugarer__var__47__52229).home));
                            
//#line 78 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52886 =
                              x10.rtt.Equality.equalsequals((t52885),(x10.lang.Runtime.home()));
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52887 =
                              !(t52886);
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52887) {
                                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52888 =
                                  true;
                                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52888) {
                                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FailedDynamicCheckException t52889 =
                                      new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.FinishState]");
                                    
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t52889;
                                }
                            }
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52230 = ((x10.core.GlobalRef)(__desugarer__var__47__52229));
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52330 =
                              ((x10.core.GlobalRef)(ret52230));
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52331 =
                              (((x10.core.GlobalRef<x10.lang.FinishState>)(t52330))).$apply$G();
                            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52331));
                        } else {
                            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52332 =
                              ((x10.core.GlobalRef)(ref));
                            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteFinishSPMD t52333 =
                              ((x10.lang.FinishState.RemoteFinishSPMD)(new x10.lang.FinishState.RemoteFinishSPMD((java.lang.System[]) null).$init(((x10.core.GlobalRef<x10.lang.FinishState>)(t52332)), (x10.lang.FinishState.RemoteFinishSPMD.__0$1x10$lang$FinishState$2) null)));
                            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52333));
                        }
                    }}
                    
                // constructor
                public x10.lang.FinishState.FinishSPMD $init(final x10.io.SerialData data){return x10$lang$FinishState$FinishSPMD$$init$S(data);}
                
                
                
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.FinishSPMD
                                                                                                            x10$lang$FinishState$FinishSPMD$$x10$lang$FinishState$FinishSPMD$this(
                                                                                                            ){
                    
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.FinishSPMD.this;
                }
            // synthetic type for parameter mangling
            public abstract static class __0$1x10$lang$FinishState$2 {}
            
        }
        
        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RootFinishSPMD extends x10.lang.FinishState.RootFinishSkeleton implements x10.x10rt.X10JavaSerializable
                                                                                                  {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RootFinishSPMD.class);
            
            public static final x10.rtt.RuntimeType<RootFinishSPMD> $RTT = x10.rtt.NamedType.<RootFinishSPMD> make(
            "x10.lang.FinishState.RootFinishSPMD", /* base class */RootFinishSPMD.class
            , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.RootFinishSkeleton.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(RootFinishSPMD $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RootFinishSPMD.class + " calling"); } 
                x10.lang.FinishState.RootFinishSkeleton.$_deserialize_body($_obj, $deserializer);
                x10.util.concurrent.SimpleLatch latch = (x10.util.concurrent.SimpleLatch) $deserializer.readRef();
                $_obj.latch = latch;
                x10.core.concurrent.AtomicInteger count = (x10.core.concurrent.AtomicInteger) $deserializer.readRef();
                $_obj.count = count;
                x10.util.Stack exceptions = (x10.util.Stack) $deserializer.readRef();
                $_obj.exceptions = exceptions;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                RootFinishSPMD $_obj = new RootFinishSPMD((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                if (latch instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.latch);
                } else {
                $serializer.write(this.latch);
                }
                if (count instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.count);
                } else {
                $serializer.write(this.count);
                }
                if (exceptions instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.exceptions);
                } else {
                $serializer.write(this.exceptions);
                }
                
            }
            
            // constructor just for allocation
            public RootFinishSPMD(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch latch;
                
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.concurrent.AtomicInteger count;
                
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.Stack<x10.core.X10Throwable> exceptions;
                
                
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            notifySubActivitySpawn(
                                                                                                            final x10.lang.Place place){
                    
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52335 =
                      ((x10.core.concurrent.AtomicInteger)(count));
                    
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.util.Util.eval(t52335.incrementAndGet());
                }
                
                
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            notifyActivityTermination(
                                                                                                            ){
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52336 =
                      ((x10.core.concurrent.AtomicInteger)(count));
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52337 =
                      t52336.decrementAndGet();
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52339 =
                      ((int) t52337) ==
                    ((int) 0);
                    
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52339) {
                        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52338 =
                          ((x10.util.concurrent.SimpleLatch)(latch));
                        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52338.release();
                    }
                }
                
                
//#line 95 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                            pushException(
                                                                                                            final x10.core.X10Throwable t){
                    
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52340 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52340.lock();
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52341 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52343 =
                      ((null) == (t52341));
                    
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52343) {
                        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52342 =
                          ((x10.util.Stack)(new x10.util.Stack<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
                        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = ((x10.util.Stack)(t52342));
                    }
                    
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52344 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.Stack<x10.core.X10Throwable>)t52344).push__0x10$util$Stack$$T$O(((x10.core.X10Throwable)(t)));
                    
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52345 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52345.unlock();
                }
                
                
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                             waitForFinish(
                                                                                                             ){
                    
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.notifyActivityTermination();
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52346 =
                      x10.lang.Runtime.STRICT_FINISH;
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
boolean t52347 =
                      !(t52346);
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52347) {
                        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52347 = x10.lang.Runtime.STATIC_THREADS;
                    }
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52350 =
                      t52347;
                    
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52350) {
                        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Runtime.Worker t52348 =
                          x10.lang.Runtime.worker();
                        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52349 =
                          ((x10.util.concurrent.SimpleLatch)(latch));
                        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52348.join(((x10.util.concurrent.SimpleLatch)(t52349)));
                    }
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52351 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52351.await();
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52352 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                      x10.lang.MultipleExceptions.make__0$1x10$lang$Throwable$2(((x10.util.Stack)(t52352)));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52353 =
                      ((null) != (t));
                    
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52353) {
                        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t;
                    }
                }
                
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch
                                                                                                             simpleLatch(
                                                                                                             ){
                    
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52354 =
                      ((x10.util.concurrent.SimpleLatch)(latch));
                    
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52354;
                }
                
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RootFinishSPMD
                                                                                                            x10$lang$FinishState$RootFinishSPMD$$x10$lang$FinishState$RootFinishSPMD$this(
                                                                                                            ){
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RootFinishSPMD.this;
                }
                
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                public RootFinishSPMD(){this((java.lang.System[]) null);
                                            $init();}
                
                // constructor for non-virtual call
                final public x10.lang.FinishState.RootFinishSPMD x10$lang$FinishState$RootFinishSPMD$$init$S() { {
                                                                                                                        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51929();
                                                                                                                    }
                                                                                                                    return this;
                                                                                                                    }
                
                // constructor
                public x10.lang.FinishState.RootFinishSPMD $init(){return x10$lang$FinishState$RootFinishSPMD$$init$S();}
                
                
                
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                            __fieldInitializers51929(
                                                                                                            ){
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.latch = ((x10.util.concurrent.SimpleLatch)(new x10.util.concurrent.SimpleLatch()));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.count = ((x10.core.concurrent.AtomicInteger)(new x10.core.concurrent.AtomicInteger(((int)(1)))));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = null;
                }
            
        }
        
        
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RemoteFinishSPMD extends x10.lang.FinishState.RemoteFinishSkeleton implements x10.x10rt.X10JavaSerializable
                                                                                                   {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteFinishSPMD.class);
            
            public static final x10.rtt.RuntimeType<RemoteFinishSPMD> $RTT = x10.rtt.NamedType.<RemoteFinishSPMD> make(
            "x10.lang.FinishState.RemoteFinishSPMD", /* base class */RemoteFinishSPMD.class
            , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.RemoteFinishSkeleton.$RTT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteFinishSPMD $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RemoteFinishSPMD.class + " calling"); } 
                x10.lang.FinishState.RemoteFinishSkeleton.$_deserialize_body($_obj, $deserializer);
                x10.core.concurrent.AtomicInteger count = (x10.core.concurrent.AtomicInteger) $deserializer.readRef();
                $_obj.count = count;
                x10.util.Stack exceptions = (x10.util.Stack) $deserializer.readRef();
                $_obj.exceptions = exceptions;
                x10.util.concurrent.Lock lock = (x10.util.concurrent.Lock) $deserializer.readRef();
                $_obj.lock = lock;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                RemoteFinishSPMD $_obj = new RemoteFinishSPMD((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                if (count instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.count);
                } else {
                $serializer.write(this.count);
                }
                if (exceptions instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.exceptions);
                } else {
                $serializer.write(this.exceptions);
                }
                if (lock instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.lock);
                } else {
                $serializer.write(this.lock);
                }
                
            }
            
            // constructor just for allocation
            public RemoteFinishSPMD(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.concurrent.AtomicInteger count;
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.Stack<x10.core.X10Throwable> exceptions;
                
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.Lock lock;
                
                
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                public RemoteFinishSPMD(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                    $init(ref, (x10.lang.FinishState.RemoteFinishSPMD.__0$1x10$lang$FinishState$2) null);}
                
                // constructor for non-virtual call
                final public x10.lang.FinishState.RemoteFinishSPMD x10$lang$FinishState$RemoteFinishSPMD$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                                  
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.RemoteFinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                                  
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                  
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51930();
                                                                                                                                                                                                              }
                                                                                                                                                                                                              return this;
                                                                                                                                                                                                              }
                
                // constructor
                public x10.lang.FinishState.RemoteFinishSPMD $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$RemoteFinishSPMD$$init$S(ref, $dummy);}
                
                
                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                             notifySubActivitySpawn(
                                                                                                             final x10.lang.Place place){
                    
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
assert ((int) place.
                                                                                                                               id) ==
                    ((int) x10.lang.Runtime.hereInt$O());
                    
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52355 =
                      ((x10.core.concurrent.AtomicInteger)(count));
                    
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.util.Util.eval(t52355.getAndIncrement());
                }
                
                
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                             notifyActivityCreation(
                                                                                                             ){
                    
                }
                
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                             notifyActivityTermination(
                                                                                                             ){
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52356 =
                      ((x10.core.concurrent.AtomicInteger)(count));
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52357 =
                      t52356.decrementAndGet();
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52367 =
                      ((int) t52357) ==
                    ((int) 0);
                    
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52367) {
                        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52358 =
                          ((x10.util.Stack)(exceptions));
                        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                          x10.lang.MultipleExceptions.make__0$1x10$lang$Throwable$2(((x10.util.Stack)(t52358)));
                        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> ref =
                          ((x10.core.GlobalRef)(this.ref()));
                        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 closure;
                        
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52364 =
                          ((null) != (t));
                        
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52364) {
                            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52361 =
                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinishSPMD.$Closure$108(ref,
                                                                                                                 t, (x10.lang.FinishState.RemoteFinishSPMD.$Closure$108.__0$1x10$lang$FinishState$2) null)));
                            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52361));
                        } else {
                            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52363 =
                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinishSPMD.$Closure$109(ref, (x10.lang.FinishState.RemoteFinishSPMD.$Closure$109.__0$1x10$lang$FinishState$2) null)));
                            
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52363));
                        }
                        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52365 =
                          ((x10.lang.Place)((ref).home));
                        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52366 =
                          t52365.
                            id;
                        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(t52366)), closure);
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                    }
                }
                
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                             pushException(
                                                                                                             final x10.core.X10Throwable t){
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52368 =
                      ((x10.util.concurrent.Lock)(lock));
                    
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52368.lock();
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52369 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52371 =
                      ((null) == (t52369));
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52371) {
                        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52370 =
                          ((x10.util.Stack)(new x10.util.Stack<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
                        
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = ((x10.util.Stack)(t52370));
                    }
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52372 =
                      ((x10.util.Stack)(exceptions));
                    
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.Stack<x10.core.X10Throwable>)t52372).push__0x10$util$Stack$$T$O(((x10.core.X10Throwable)(t)));
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52373 =
                      ((x10.util.concurrent.Lock)(lock));
                    
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52373.unlock();
                }
                
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RemoteFinishSPMD
                                                                                                             x10$lang$FinishState$RemoteFinishSPMD$$x10$lang$FinishState$RemoteFinishSPMD$this(
                                                                                                             ){
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RemoteFinishSPMD.this;
                }
                
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                             __fieldInitializers51930(
                                                                                                             ){
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.count = ((x10.core.concurrent.AtomicInteger)(new x10.core.concurrent.AtomicInteger(((int)(1)))));
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = null;
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.lock = ((x10.util.concurrent.Lock)(new x10.util.concurrent.Lock()));
                }
                
                @x10.core.X10Generated public static class $Closure$108 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$108.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$108> $RTT = x10.rtt.StaticVoidFunType.<$Closure$108> make(
                    /* base class */$Closure$108.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$108 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$108.class + " calling"); } 
                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                        $_obj.ref = ref;
                        x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                        $_obj.t = t;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$108 $_obj = new $Closure$108((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                        } else {
                        $serializer.write(this.ref);
                        }
                        if (t instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                        } else {
                        $serializer.write(this.t);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$108(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52359 =
                              x10.lang.FinishState.<x10.lang.FinishState>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                               ref)));
                            
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52359.pushException(((x10.core.X10Throwable)(this.
                                                                                                                                                                       t)));
                            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52360 =
                              x10.lang.FinishState.<x10.lang.FinishState>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                               ref)));
                            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52360.notifyActivityTermination();
                        }
                        
                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                        public x10.lang.MultipleExceptions t;
                        
                        public $Closure$108(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                            final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                              this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                              this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                          }}
                        // synthetic type for parameter mangling
                        public abstract static class __0$1x10$lang$FinishState$2 {}
                        
                    }
                    
                @x10.core.X10Generated public static class $Closure$109 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$109.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$109> $RTT = x10.rtt.StaticVoidFunType.<$Closure$109> make(
                    /* base class */$Closure$109.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$109 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$109.class + " calling"); } 
                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                        $_obj.ref = ref;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$109 $_obj = new $Closure$109((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                        } else {
                        $serializer.write(this.ref);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$109(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52362 =
                              x10.lang.FinishState.<x10.lang.FinishState>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                               ref)));
                            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52362.notifyActivityTermination();
                        }
                        
                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                        
                        public $Closure$109(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                             this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                         }}
                        // synthetic type for parameter mangling
                        public abstract static class __0$1x10$lang$FinishState$2 {}
                        
                    }
                    
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$FinishState$2 {}
                
                }
                
            
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class FinishAsync extends x10.lang.FinishState.FinishSkeleton implements x10.io.CustomSerialization
                                                                                                       {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishAsync.class);
                
                public static final x10.rtt.RuntimeType<FinishAsync> $RTT = x10.rtt.NamedType.<FinishAsync> make(
                "x10.lang.FinishState.FinishAsync", /* base class */FinishAsync.class
                , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.lang.FinishState.FinishSkeleton.$RTT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                // custom serializer
                private transient x10.io.SerialData $$serialdata;
                private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
                private Object readResolve() { return new FinishAsync($$serialdata); }
                private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
                oos.writeObject($$serialdata); }
                private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
                $$serialdata = (x10.io.SerialData) ois.readObject(); }
                public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishAsync $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + FinishAsync.class + " calling"); } 
                    x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                    $_obj.$init($$serialdata);
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    FinishAsync $_obj = new FinishAsync((java.lang.System[]) null);
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
                
                // constructor just for allocation
                public FinishAsync(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                    public FinishAsync(){this((java.lang.System[]) null);
                                             $init();}
                    
                    // constructor for non-virtual call
                    final public x10.lang.FinishState.FinishAsync x10$lang$FinishState$FinishAsync$$init$S() { {
                                                                                                                      
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinishAsync t52892 =
                                                                                                                        ((x10.lang.FinishState.RootFinishAsync)(new x10.lang.FinishState.RootFinishAsync((java.lang.System[]) null).$init()));
                                                                                                                      
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.lang.FinishState.RootFinishSkeleton)(t52892)));
                                                                                                                      
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                  }
                                                                                                                  return this;
                                                                                                                  }
                    
                    // constructor
                    public x10.lang.FinishState.FinishAsync $init(){return x10$lang$FinishState$FinishAsync$$init$S();}
                    
                    
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                    public FinishAsync(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                   $init(ref, (x10.lang.FinishState.FinishAsync.__0$1x10$lang$FinishState$2) null);}
                    
                    // constructor for non-virtual call
                    final public x10.lang.FinishState.FinishAsync x10$lang$FinishState$FinishAsync$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                            
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                        }
                                                                                                                                                                                                        return this;
                                                                                                                                                                                                        }
                    
                    // constructor
                    public x10.lang.FinishState.FinishAsync $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$FinishAsync$$init$S(ref, $dummy);}
                    
                    
                    
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                    public FinishAsync(final x10.io.SerialData data){this((java.lang.System[]) null);
                                                                         $init(data);}
                    
                    // constructor for non-virtual call
                    final public x10.lang.FinishState.FinishAsync x10$lang$FinishState$FinishAsync$$init$S(final x10.io.SerialData data) {x10$lang$FinishState$FinishAsync$init_for_reflection(data);
                                                                                                                                              
                                                                                                                                              return this;
                                                                                                                                              }
                    public void x10$lang$FinishState$FinishAsync$init_for_reflection(x10.io.SerialData data) {
                         {
                            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final java.lang.Object t52898 =
                              ((java.lang.Object)(data.
                                                    data));
                            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52899 =
                              ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52898))));
                            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(t52899)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                            
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52377 =
                              ((x10.core.GlobalRef)(ref));
                            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52378 =
                              ((x10.lang.Place)((t52377).home));
                            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52379 =
                              t52378.
                                id;
                            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52380 =
                              x10.lang.Runtime.hereInt$O();
                            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52391 =
                              ((int) t52379) ==
                            ((int) t52380);
                            
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52391) {
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52381 =
                                  ((x10.core.GlobalRef)(ref));
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> __desugarer__var__48__52232 =
                                  ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52381))));
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.GlobalRef<x10.lang.FinishState> ret52233 =
                                   null;
                                
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52893 =
                                  ((x10.lang.Place)((__desugarer__var__48__52232).home));
                                
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52894 =
                                  x10.rtt.Equality.equalsequals((t52893),(x10.lang.Runtime.home()));
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52895 =
                                  !(t52894);
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52895) {
                                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52896 =
                                      true;
                                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52896) {
                                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FailedDynamicCheckException t52897 =
                                          new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.FinishState]");
                                        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t52897;
                                    }
                                }
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52233 = ((x10.core.GlobalRef)(__desugarer__var__48__52232));
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52387 =
                                  ((x10.core.GlobalRef)(ret52233));
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52388 =
                                  (((x10.core.GlobalRef<x10.lang.FinishState>)(t52387))).$apply$G();
                                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52388));
                            } else {
                                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52389 =
                                  ((x10.core.GlobalRef)(ref));
                                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteFinishAsync t52390 =
                                  ((x10.lang.FinishState.RemoteFinishAsync)(new x10.lang.FinishState.RemoteFinishAsync((java.lang.System[]) null).$init(((x10.core.GlobalRef<x10.lang.FinishState>)(t52389)), (x10.lang.FinishState.RemoteFinishAsync.__0$1x10$lang$FinishState$2) null)));
                                
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52390));
                            }
                        }}
                        
                    // constructor
                    public x10.lang.FinishState.FinishAsync $init(final x10.io.SerialData data){return x10$lang$FinishState$FinishAsync$$init$S(data);}
                    
                    
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.FinishAsync
                                                                                                                 x10$lang$FinishState$FinishAsync$$x10$lang$FinishState$FinishAsync$this(
                                                                                                                 ){
                        
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.FinishAsync.this;
                    }
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$FinishState$2 {}
                
            }
            
            
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RootFinishAsync extends x10.lang.FinishState.RootFinishSkeleton implements x10.x10rt.X10JavaSerializable
                                                                                                       {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RootFinishAsync.class);
                
                public static final x10.rtt.RuntimeType<RootFinishAsync> $RTT = x10.rtt.NamedType.<RootFinishAsync> make(
                "x10.lang.FinishState.RootFinishAsync", /* base class */RootFinishAsync.class
                , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.RootFinishSkeleton.$RTT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                public static x10.x10rt.X10JavaSerializable $_deserialize_body(RootFinishAsync $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RootFinishAsync.class + " calling"); } 
                    x10.lang.FinishState.RootFinishSkeleton.$_deserialize_body($_obj, $deserializer);
                    x10.util.concurrent.SimpleLatch latch = (x10.util.concurrent.SimpleLatch) $deserializer.readRef();
                    $_obj.latch = latch;
                    x10.core.X10Throwable exception = (x10.core.X10Throwable) $deserializer.readRef();
                    $_obj.exception = exception;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    RootFinishAsync $_obj = new RootFinishAsync((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    super.$_serialize($serializer);
                    if (latch instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.latch);
                    } else {
                    $serializer.write(this.latch);
                    }
                    if (exception instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.exception);
                    } else {
                    $serializer.write(this.exception);
                    }
                    
                }
                
                // constructor just for allocation
                public RootFinishAsync(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch latch;
                    
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.X10Throwable exception;
                    
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 notifySubActivitySpawn(
                                                                                                                 final x10.lang.Place place){
                        
                    }
                    
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 notifyActivityTermination(
                                                                                                                 ){
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52392 =
                          ((x10.util.concurrent.SimpleLatch)(latch));
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52392.release();
                    }
                    
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 pushException(
                                                                                                                 final x10.core.X10Throwable t){
                        
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exception = ((x10.core.X10Throwable)(t));
                    }
                    
                    
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 waitForFinish(
                                                                                                                 ){
                        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52393 =
                          ((x10.util.concurrent.SimpleLatch)(latch));
                        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52393.await();
                        
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.X10Throwable t52394 =
                          ((x10.core.X10Throwable)(exception));
                        
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                          x10.lang.MultipleExceptions.make(((x10.core.X10Throwable)(t52394)));
                        
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52395 =
                          ((null) != (t));
                        
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52395) {
                            
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t;
                        }
                    }
                    
                    
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch
                                                                                                                 simpleLatch(
                                                                                                                 ){
                        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52396 =
                          ((x10.util.concurrent.SimpleLatch)(latch));
                        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52396;
                    }
                    
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RootFinishAsync
                                                                                                                 x10$lang$FinishState$RootFinishAsync$$x10$lang$FinishState$RootFinishAsync$this(
                                                                                                                 ){
                        
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RootFinishAsync.this;
                    }
                    
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                    public RootFinishAsync(){this((java.lang.System[]) null);
                                                 $init();}
                    
                    // constructor for non-virtual call
                    final public x10.lang.FinishState.RootFinishAsync x10$lang$FinishState$RootFinishAsync$$init$S() { {
                                                                                                                              
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                              
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                              
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51931();
                                                                                                                          }
                                                                                                                          return this;
                                                                                                                          }
                    
                    // constructor
                    public x10.lang.FinishState.RootFinishAsync $init(){return x10$lang$FinishState$RootFinishAsync$$init$S();}
                    
                    
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                 __fieldInitializers51931(
                                                                                                                 ){
                        
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.latch = ((x10.util.concurrent.SimpleLatch)(new x10.util.concurrent.SimpleLatch()));
                        
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exception = null;
                    }
                
            }
            
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RemoteFinishAsync extends x10.lang.FinishState.RemoteFinishSkeleton implements x10.x10rt.X10JavaSerializable
                                                                                                       {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteFinishAsync.class);
                
                public static final x10.rtt.RuntimeType<RemoteFinishAsync> $RTT = x10.rtt.NamedType.<RemoteFinishAsync> make(
                "x10.lang.FinishState.RemoteFinishAsync", /* base class */RemoteFinishAsync.class
                , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.RemoteFinishSkeleton.$RTT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteFinishAsync $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RemoteFinishAsync.class + " calling"); } 
                    x10.lang.FinishState.RemoteFinishSkeleton.$_deserialize_body($_obj, $deserializer);
                    x10.core.X10Throwable exception = (x10.core.X10Throwable) $deserializer.readRef();
                    $_obj.exception = exception;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    RemoteFinishAsync $_obj = new RemoteFinishAsync((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    super.$_serialize($serializer);
                    if (exception instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.exception);
                    } else {
                    $serializer.write(this.exception);
                    }
                    
                }
                
                // constructor just for allocation
                public RemoteFinishAsync(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.X10Throwable exception;
                    
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                    public RemoteFinishAsync(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                         $init(ref, (x10.lang.FinishState.RemoteFinishAsync.__0$1x10$lang$FinishState$2) null);}
                    
                    // constructor for non-virtual call
                    final public x10.lang.FinishState.RemoteFinishAsync x10$lang$FinishState$RemoteFinishAsync$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                                        
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.RemoteFinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                                        
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                        
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51932();
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                    return this;
                                                                                                                                                                                                                    }
                    
                    // constructor
                    public x10.lang.FinishState.RemoteFinishAsync $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$RemoteFinishAsync$$init$S(ref, $dummy);}
                    
                    
                    
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 notifyActivityCreation(
                                                                                                                 ){
                        
                    }
                    
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 notifySubActivitySpawn(
                                                                                                                 final x10.lang.Place place){
                        
                    }
                    
                    
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 pushException(
                                                                                                                 final x10.core.X10Throwable t){
                        
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exception = ((x10.core.X10Throwable)(t));
                    }
                    
                    
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                 notifyActivityTermination(
                                                                                                                 ){
                        
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.X10Throwable t52397 =
                          ((x10.core.X10Throwable)(exception));
                        
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                          x10.lang.MultipleExceptions.make(((x10.core.X10Throwable)(t52397)));
                        
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> ref =
                          ((x10.core.GlobalRef)(this.ref()));
                        
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 closure;
                        
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52403 =
                          ((null) != (t));
                        
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52403) {
                            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52400 =
                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinishAsync.$Closure$110(ref,
                                                                                                                  t, (x10.lang.FinishState.RemoteFinishAsync.$Closure$110.__0$1x10$lang$FinishState$2) null)));
                            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52400));
                        } else {
                            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52402 =
                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinishAsync.$Closure$111(ref, (x10.lang.FinishState.RemoteFinishAsync.$Closure$111.__0$1x10$lang$FinishState$2) null)));
                            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52402));
                        }
                        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52404 =
                          ((x10.lang.Place)((ref).home));
                        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52405 =
                          t52404.
                            id;
                        
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(t52405)), closure);
                        
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                    }
                    
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RemoteFinishAsync
                                                                                                                 x10$lang$FinishState$RemoteFinishAsync$$x10$lang$FinishState$RemoteFinishAsync$this(
                                                                                                                 ){
                        
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RemoteFinishAsync.this;
                    }
                    
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                 __fieldInitializers51932(
                                                                                                                 ){
                        
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exception = null;
                    }
                    
                    @x10.core.X10Generated public static class $Closure$110 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                    {
                        private static final long serialVersionUID = 1L;
                        private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$110.class);
                        
                        public static final x10.rtt.RuntimeType<$Closure$110> $RTT = x10.rtt.StaticVoidFunType.<$Closure$110> make(
                        /* base class */$Closure$110.class
                        , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                        );
                        public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                        
                        
                        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                        public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$110 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$110.class + " calling"); } 
                            x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                            $_obj.ref = ref;
                            x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                            $_obj.t = t;
                            return $_obj;
                            
                        }
                        
                        public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            $Closure$110 $_obj = new $Closure$110((java.lang.System[]) null);
                            $deserializer.record_reference($_obj);
                            return $_deserialize_body($_obj, $deserializer);
                            
                        }
                        
                        public short $_get_serialization_id() {
                        
                             return $_serialization_id;
                            
                        }
                        
                        public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                        
                            if (ref instanceof x10.x10rt.X10JavaSerializable) {
                            $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                            } else {
                            $serializer.write(this.ref);
                            }
                            if (t instanceof x10.x10rt.X10JavaSerializable) {
                            $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                            } else {
                            $serializer.write(this.t);
                            }
                            
                        }
                        
                        // constructor just for allocation
                        public $Closure$110(final java.lang.System[] $dummy) { 
                        super($dummy);
                        }
                        
                            
                            public void
                              $apply(
                              ){
                                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52398 =
                                  x10.lang.FinishState.<x10.lang.FinishState>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                   ref)));
                                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52398.pushException(((x10.core.X10Throwable)(this.
                                                                                                                                                                           t)));
                                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52399 =
                                  x10.lang.FinishState.<x10.lang.FinishState>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                   ref)));
                                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52399.notifyActivityTermination();
                            }
                            
                            public x10.core.GlobalRef<x10.lang.FinishState> ref;
                            public x10.lang.MultipleExceptions t;
                            
                            public $Closure$110(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                  this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                  this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                              }}
                            // synthetic type for parameter mangling
                            public abstract static class __0$1x10$lang$FinishState$2 {}
                            
                        }
                        
                    @x10.core.X10Generated public static class $Closure$111 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                    {
                        private static final long serialVersionUID = 1L;
                        private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$111.class);
                        
                        public static final x10.rtt.RuntimeType<$Closure$111> $RTT = x10.rtt.StaticVoidFunType.<$Closure$111> make(
                        /* base class */$Closure$111.class
                        , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                        );
                        public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                        
                        
                        private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                        public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$111 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$111.class + " calling"); } 
                            x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                            $_obj.ref = ref;
                            return $_obj;
                            
                        }
                        
                        public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                        
                            $Closure$111 $_obj = new $Closure$111((java.lang.System[]) null);
                            $deserializer.record_reference($_obj);
                            return $_deserialize_body($_obj, $deserializer);
                            
                        }
                        
                        public short $_get_serialization_id() {
                        
                             return $_serialization_id;
                            
                        }
                        
                        public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                        
                            if (ref instanceof x10.x10rt.X10JavaSerializable) {
                            $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                            } else {
                            $serializer.write(this.ref);
                            }
                            
                        }
                        
                        // constructor just for allocation
                        public $Closure$111(final java.lang.System[] $dummy) { 
                        super($dummy);
                        }
                        
                            
                            public void
                              $apply(
                              ){
                                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52401 =
                                  x10.lang.FinishState.<x10.lang.FinishState>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                   ref)));
                                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52401.notifyActivityTermination();
                            }
                            
                            public x10.core.GlobalRef<x10.lang.FinishState> ref;
                            
                            public $Closure$111(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                 this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                             }}
                            // synthetic type for parameter mangling
                            public abstract static class __0$1x10$lang$FinishState$2 {}
                            
                        }
                        
                    // synthetic type for parameter mangling
                    public abstract static class __0$1x10$lang$FinishState$2 {}
                    
                    }
                    
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class FinishHere extends x10.lang.FinishState.FinishSkeleton implements x10.io.CustomSerialization
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishHere.class);
                    
                    public static final x10.rtt.RuntimeType<FinishHere> $RTT = x10.rtt.NamedType.<FinishHere> make(
                    "x10.lang.FinishState.FinishHere", /* base class */FinishHere.class
                    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.lang.FinishState.FinishSkeleton.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    // custom serializer
                    private transient x10.io.SerialData $$serialdata;
                    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
                    private Object readResolve() { return new FinishHere($$serialdata); }
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
                    oos.writeObject($$serialdata); }
                    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
                    $$serialdata = (x10.io.SerialData) ois.readObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishHere $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + FinishHere.class + " calling"); } 
                        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                        $_obj.$init($$serialdata);
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        FinishHere $_obj = new FinishHere((java.lang.System[]) null);
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
                    
                    // constructor just for allocation
                    public FinishHere(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public FinishHere(){this((java.lang.System[]) null);
                                                $init();}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.FinishHere x10$lang$FinishState$FinishHere$$init$S() { {
                                                                                                                        
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinishSPMD t52900 =
                                                                                                                          ((x10.lang.FinishState.RootFinishSPMD)(new x10.lang.FinishState.RootFinishSPMD((java.lang.System[]) null).$init()));
                                                                                                                        
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.lang.FinishState.RootFinishSkeleton)(t52900)));
                                                                                                                        
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                    }
                                                                                                                    return this;
                                                                                                                    }
                        
                        // constructor
                        public x10.lang.FinishState.FinishHere $init(){return x10$lang$FinishState$FinishHere$$init$S();}
                        
                        
                        
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public FinishHere(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                      $init(ref, (x10.lang.FinishState.FinishHere.__0$1x10$lang$FinishState$2) null);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.FinishHere x10$lang$FinishState$FinishHere$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                              
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                              
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                          }
                                                                                                                                                                                                          return this;
                                                                                                                                                                                                          }
                        
                        // constructor
                        public x10.lang.FinishState.FinishHere $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$FinishHere$$init$S(ref, $dummy);}
                        
                        
                        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public FinishHere(final x10.io.SerialData data){this((java.lang.System[]) null);
                                                                            $init(data);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.FinishHere x10$lang$FinishState$FinishHere$$init$S(final x10.io.SerialData data) {x10$lang$FinishState$FinishHere$init_for_reflection(data);
                                                                                                                                                
                                                                                                                                                return this;
                                                                                                                                                }
                        public void x10$lang$FinishState$FinishHere$init_for_reflection(x10.io.SerialData data) {
                             {
                                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final java.lang.Object t52906 =
                                  ((java.lang.Object)(data.
                                                        data));
                                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52907 =
                                  ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52906))));
                                
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(t52907)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52409 =
                                  ((x10.core.GlobalRef)(ref));
                                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52410 =
                                  ((x10.lang.Place)((t52409).home));
                                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52411 =
                                  t52410.
                                    id;
                                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52412 =
                                  x10.lang.Runtime.hereInt$O();
                                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52422 =
                                  ((int) t52411) ==
                                ((int) t52412);
                                
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52422) {
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52413 =
                                      ((x10.core.GlobalRef)(ref));
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> __desugarer__var__49__52235 =
                                      ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52413))));
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.GlobalRef<x10.lang.FinishState> ret52236 =
                                       null;
                                    
//#line 226 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52901 =
                                      ((x10.lang.Place)((__desugarer__var__49__52235).home));
                                    
//#line 226 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52902 =
                                      x10.rtt.Equality.equalsequals((t52901),(x10.lang.Runtime.home()));
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52903 =
                                      !(t52902);
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52903) {
                                        
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52904 =
                                          true;
                                        
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52904) {
                                            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FailedDynamicCheckException t52905 =
                                              new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.FinishState]");
                                            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t52905;
                                        }
                                    }
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52236 = ((x10.core.GlobalRef)(__desugarer__var__49__52235));
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52419 =
                                      ((x10.core.GlobalRef)(ret52236));
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52420 =
                                      (((x10.core.GlobalRef<x10.lang.FinishState>)(t52419))).$apply$G();
                                    
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52420));
                                } else {
                                    
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.UncountedFinish t52421 =
                                      ((x10.lang.FinishState.UncountedFinish)(x10.lang.FinishState.getInitialized$UNCOUNTED_FINISH()));
                                    
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52421));
                                }
                            }}
                            
                        // constructor
                        public x10.lang.FinishState.FinishHere $init(final x10.io.SerialData data){return x10$lang$FinishState$FinishHere$$init$S(data);}
                        
                        
                        
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.FinishHere
                                                                                                                     x10$lang$FinishState$FinishHere$$x10$lang$FinishState$FinishHere$this(
                                                                                                                     ){
                            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.FinishHere.this;
                        }
                    // synthetic type for parameter mangling
                    public abstract static class __0$1x10$lang$FinishState$2 {}
                    
                }
                
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class UncountedFinish extends x10.lang.FinishState implements x10.x10rt.X10JavaSerializable
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UncountedFinish.class);
                    
                    public static final x10.rtt.RuntimeType<UncountedFinish> $RTT = x10.rtt.NamedType.<UncountedFinish> make(
                    "x10.lang.FinishState.UncountedFinish", /* base class */UncountedFinish.class
                    , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(UncountedFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + UncountedFinish.class + " calling"); } 
                        x10.lang.FinishState.$_deserialize_body($_obj, $deserializer);
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        UncountedFinish $_obj = new UncountedFinish((java.lang.System[]) null);
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
                    public UncountedFinish(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifySubActivitySpawn(
                                                                                                                     final x10.lang.Place place){
                            
                        }
                        
                        
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityCreation(
                                                                                                                     ){
                            
                        }
                        
                        
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityTermination(
                                                                                                                     ){
                            
                        }
                        
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     pushException(
                                                                                                                     final x10.core.X10Throwable t){
                            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
java.lang.System.err.println("Uncaught exception in uncounted activity");
                            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t.printStackTrace();
                        }
                        
                        
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                     waitForFinish(
                                                                                                                     ){
                            
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
assert false;
                        }
                        
                        
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch
                                                                                                                     simpleLatch(
                                                                                                                     ){
                            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return null;
                        }
                        
                        
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.UncountedFinish
                                                                                                                     x10$lang$FinishState$UncountedFinish$$x10$lang$FinishState$UncountedFinish$this(
                                                                                                                     ){
                            
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.UncountedFinish.this;
                        }
                        
                        
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public UncountedFinish(){this((java.lang.System[]) null);
                                                     $init();}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.UncountedFinish x10$lang$FinishState$UncountedFinish$$init$S() { {
                                                                                                                                  
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                                  
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                              }
                                                                                                                              return this;
                                                                                                                              }
                        
                        // constructor
                        public x10.lang.FinishState.UncountedFinish $init(){return x10$lang$FinishState$UncountedFinish$$init$S();}
                        
                    
                }
                
                
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public static x10.lang.FinishState.UncountedFinish UNCOUNTED_FINISH;
                
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class FinishStates extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishStates.class);
                    
                    public static final x10.rtt.RuntimeType<FinishStates> $RTT = x10.rtt.NamedType.<FinishStates> make(
                    "x10.lang.FinishState.FinishStates", /* base class */FinishStates.class
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishStates $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FinishStates.class + " calling"); } 
                        x10.util.HashMap map = (x10.util.HashMap) $deserializer.readRef();
                        $_obj.map = map;
                        x10.util.concurrent.Lock lock = (x10.util.concurrent.Lock) $deserializer.readRef();
                        $_obj.lock = lock;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        FinishStates $_obj = new FinishStates((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (map instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.map);
                        } else {
                        $serializer.write(this.map);
                        }
                        if (lock instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lock);
                        } else {
                        $serializer.write(this.lock);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public FinishStates(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState> map;
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.Lock lock;
                        
                        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.lang.FinishState
                                                                                                                     $apply__0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$2(
                                                                                                                     final x10.core.GlobalRef root,
                                                                                                                     final x10.core.fun.Fun_0_0 factory){
                            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52423 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52423.lock();
                            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState> t52424 =
                              ((x10.util.HashMap)(map));
                            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.lang.FinishState f =
                              ((x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState>)t52424).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(((x10.core.GlobalRef)(root)),
                                                                                                                                                                                 ((x10.lang.FinishState)(null)));
                            
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52425 =
                              f;
                            
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52428 =
                              ((null) != (t52425));
                            
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52428) {
                                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52426 =
                                  ((x10.util.concurrent.Lock)(lock));
                                
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52426.unlock();
                                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52427 =
                                  f;
                                
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52427;
                            }
                            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52429 =
                              ((x10.core.fun.Fun_0_0<x10.lang.FinishState>)factory).$apply$G();
                            
//#line 261 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
f = t52429;
                            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState> t52430 =
                              ((x10.util.HashMap)(map));
                            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52431 =
                              f;
                            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState>)t52430).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(((x10.core.GlobalRef)(root)),
                                                                                                                                                                                                                                                                  ((x10.lang.FinishState)(t52431)));
                            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52432 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52432.unlock();
                            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52433 =
                              f;
                            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52433;
                        }
                        
                        
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     remove__0$1x10$lang$FinishState$2(
                                                                                                                     final x10.core.GlobalRef root){
                            
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52434 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52434.lock();
                            
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState> t52435 =
                              ((x10.util.HashMap)(map));
                            
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState>)t52435).remove__0x10$util$HashMap$$K(((x10.core.GlobalRef)(root)));
                            
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52436 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52436.unlock();
                        }
                        
                        
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.FinishStates
                                                                                                                     x10$lang$FinishState$FinishStates$$x10$lang$FinishState$FinishStates$this(
                                                                                                                     ){
                            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.FinishStates.this;
                        }
                        
                        
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public FinishStates(){this((java.lang.System[]) null);
                                                  $init();}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.FinishStates x10$lang$FinishState$FinishStates$$init$S() { {
                                                                                                                            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51933();
                                                                                                                        }
                                                                                                                        return this;
                                                                                                                        }
                        
                        // constructor
                        public x10.lang.FinishState.FinishStates $init(){return x10$lang$FinishState$FinishStates$$init$S();}
                        
                        
                        
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                     __fieldInitializers51933(
                                                                                                                     ){
                            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState> t52437 =
                              ((x10.util.HashMap)(new x10.util.HashMap<x10.core.GlobalRef<x10.lang.FinishState>, x10.lang.FinishState>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT), x10.lang.FinishState.$RTT).$init()));
                            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.map = ((x10.util.HashMap)(t52437));
                            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.lock = ((x10.util.concurrent.Lock)(new x10.util.concurrent.Lock()));
                        }
                    
                }
                
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated abstract public static class RootFinishSkeleton extends x10.lang.FinishState implements x10.lang.Runtime.Mortal, x10.x10rt.X10JavaSerializable
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RootFinishSkeleton.class);
                    
                    public static final x10.rtt.RuntimeType<RootFinishSkeleton> $RTT = x10.rtt.NamedType.<RootFinishSkeleton> make(
                    "x10.lang.FinishState.RootFinishSkeleton", /* base class */RootFinishSkeleton.class
                    , /* parents */ new x10.rtt.Type[] {x10.lang.Runtime.Mortal.$RTT, x10.lang.FinishState.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RootFinishSkeleton $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RootFinishSkeleton.class + " calling"); } 
                        x10.lang.FinishState.$_deserialize_body($_obj, $deserializer);
                        x10.core.GlobalRef xxxx = (x10.core.GlobalRef) $deserializer.readRef();
                        $_obj.xxxx = xxxx;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        return null;
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        super.$_serialize($serializer);
                        if (xxxx instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.xxxx);
                        } else {
                        $serializer.write(this.xxxx);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public RootFinishSkeleton(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.GlobalRef<x10.lang.FinishState> xxxx;
                        
                        
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.GlobalRef<x10.lang.FinishState>
                                                                                                                     ref(
                                                                                                                     ){
                            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52438 =
                              ((x10.core.GlobalRef)(xxxx));
                            
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52438;
                        }
                        
                        
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityCreation(
                                                                                                                     ){
                            
                        }
                        
                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RootFinishSkeleton
                                                                                                                     x10$lang$FinishState$RootFinishSkeleton$$x10$lang$FinishState$RootFinishSkeleton$this(
                                                                                                                     ){
                            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RootFinishSkeleton.this;
                        }
                        
                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                        // constructor for non-virtual call
                        final public x10.lang.FinishState.RootFinishSkeleton x10$lang$FinishState$RootFinishSkeleton$$init$S() { {
                                                                                                                                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51934();
                                                                                                                                    }
                                                                                                                                    return this;
                                                                                                                                    }
                        
                        // constructor
                        public x10.lang.FinishState.RootFinishSkeleton $init(){return x10$lang$FinishState$RootFinishSkeleton$$init$S();}
                        
                        
                        
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                     __fieldInitializers51934(
                                                                                                                     ){
                            
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52439 =
                              ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.lang.FinishState>(x10.lang.FinishState.$RTT, ((x10.lang.FinishState)(this)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.xxxx = ((x10.core.GlobalRef)(t52439));
                        }
                    
                }
                
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated abstract public static class RemoteFinishSkeleton extends x10.lang.FinishState implements x10.x10rt.X10JavaSerializable
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteFinishSkeleton.class);
                    
                    public static final x10.rtt.RuntimeType<RemoteFinishSkeleton> $RTT = x10.rtt.NamedType.<RemoteFinishSkeleton> make(
                    "x10.lang.FinishState.RemoteFinishSkeleton", /* base class */RemoteFinishSkeleton.class
                    , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteFinishSkeleton $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RemoteFinishSkeleton.class + " calling"); } 
                        x10.lang.FinishState.$_deserialize_body($_obj, $deserializer);
                        x10.core.GlobalRef xxxx = (x10.core.GlobalRef) $deserializer.readRef();
                        $_obj.xxxx = xxxx;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        return null;
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        super.$_serialize($serializer);
                        if (xxxx instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.xxxx);
                        } else {
                        $serializer.write(this.xxxx);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public RemoteFinishSkeleton(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.GlobalRef<x10.lang.FinishState> xxxx;
                        
                        
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                        // constructor for non-virtual call
                        final public x10.lang.FinishState.RemoteFinishSkeleton x10$lang$FinishState$RemoteFinishSkeleton$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> root, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                                                   
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                                                                                                                                   
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                                   
//#line 286 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.xxxx = ((x10.core.GlobalRef)(root));
                                                                                                                                                                                                                               }
                                                                                                                                                                                                                               return this;
                                                                                                                                                                                                                               }
                        
                        // constructor
                        public x10.lang.FinishState.RemoteFinishSkeleton $init(final x10.core.GlobalRef<x10.lang.FinishState> root, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$RemoteFinishSkeleton$$init$S(root, $dummy);}
                        
                        
                        
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.GlobalRef<x10.lang.FinishState>
                                                                                                                     ref(
                                                                                                                     ){
                            
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52440 =
                              ((x10.core.GlobalRef)(xxxx));
                            
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52440;
                        }
                        
                        
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     waitForFinish(
                                                                                                                     ){
                            
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
assert false;
                        }
                        
                        
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch
                                                                                                                     simpleLatch(
                                                                                                                     ){
                            
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return null;
                        }
                        
                        
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RemoteFinishSkeleton
                                                                                                                     x10$lang$FinishState$RemoteFinishSkeleton$$x10$lang$FinishState$RemoteFinishSkeleton$this(
                                                                                                                     ){
                            
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RemoteFinishSkeleton.this;
                        }
                    // synthetic type for parameter mangling
                    public abstract static class __0$1x10$lang$FinishState$2 {}
                    
                }
                
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated abstract public static class FinishSkeleton extends x10.lang.FinishState implements x10.x10rt.X10JavaSerializable
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FinishSkeleton.class);
                    
                    public static final x10.rtt.RuntimeType<FinishSkeleton> $RTT = x10.rtt.NamedType.<FinishSkeleton> make(
                    "x10.lang.FinishState.FinishSkeleton", /* base class */FinishSkeleton.class
                    , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FinishSkeleton $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FinishSkeleton.class + " calling"); } 
                        x10.lang.FinishState.$_deserialize_body($_obj, $deserializer);
                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                        $_obj.ref = ref;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        return null;
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        super.$_serialize($serializer);
                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                        } else {
                        $serializer.write(this.ref);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public FinishSkeleton(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.GlobalRef<x10.lang.FinishState> ref;
                        
                        
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public transient x10.lang.FinishState me;
                        
                        
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                        // constructor for non-virtual call
                        final public x10.lang.FinishState.FinishSkeleton x10$lang$FinishState$FinishSkeleton$$init$S(final x10.lang.FinishState.RootFinishSkeleton root) { {
                                                                                                                                                                                  
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                                                                                  
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52908 =
                                                                                                                                                                                    ((x10.core.GlobalRef)(root.ref()));
                                                                                                                                                                                  
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.ref = t52908;
                                                                                                                                                                                  
                                                                                                                                                                                  
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51935();
                                                                                                                                                                                  
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(root));
                                                                                                                                                                              }
                                                                                                                                                                              return this;
                                                                                                                                                                              }
                        
                        // constructor
                        public x10.lang.FinishState.FinishSkeleton $init(final x10.lang.FinishState.RootFinishSkeleton root){return x10$lang$FinishState$FinishSkeleton$$init$S(root);}
                        
                        
                        
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                        // constructor for non-virtual call
                        final public x10.lang.FinishState.FinishSkeleton x10$lang$FinishState$FinishSkeleton$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                                      
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                                                                                                                      
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.ref = ref;
                                                                                                                                                                                                                      
                                                                                                                                                                                                                      
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51935();
                                                                                                                                                                                                                      
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = null;
                                                                                                                                                                                                                  }
                                                                                                                                                                                                                  return this;
                                                                                                                                                                                                                  }
                        
                        // constructor
                        public x10.lang.FinishState.FinishSkeleton $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$FinishSkeleton$$init$S(ref, $dummy);}
                        
                        
                        
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.io.SerialData
                                                                                                                     serialize(
                                                                                                                     ){
                            
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52442 =
                              ((x10.core.GlobalRef)(ref));
                            
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.io.SerialData t52443 =
                              ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).$init(((java.lang.Object)(t52442)),
                                                                                                          ((x10.io.SerialData)(null)))));
                            
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52443;
                        }
                        
                        
//#line 305 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifySubActivitySpawn(
                                                                                                                     final x10.lang.Place place){
                            
//#line 305 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52444 =
                              ((x10.lang.FinishState)(me));
                            
//#line 305 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52444.notifySubActivitySpawn(((x10.lang.Place)(place)));
                        }
                        
                        
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityCreation(
                                                                                                                     ){
                            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52445 =
                              ((x10.lang.FinishState)(me));
                            
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52445.notifyActivityCreation();
                        }
                        
                        
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityTermination(
                                                                                                                     ){
                            
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52446 =
                              ((x10.lang.FinishState)(me));
                            
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52446.notifyActivityTermination();
                        }
                        
                        
//#line 308 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     pushException(
                                                                                                                     final x10.core.X10Throwable t){
                            
//#line 308 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52447 =
                              ((x10.lang.FinishState)(me));
                            
//#line 308 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52447.pushException(((x10.core.X10Throwable)(t)));
                        }
                        
                        
//#line 309 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     waitForFinish(
                                                                                                                     ){
                            
//#line 309 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52448 =
                              ((x10.lang.FinishState)(me));
                            
//#line 309 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52448.waitForFinish();
                        }
                        
                        
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch
                                                                                                                     simpleLatch(
                                                                                                                     ){
                            
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52449 =
                              ((x10.lang.FinishState)(me));
                            
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52450 =
                              t52449.simpleLatch();
                            
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52450;
                        }
                        
                        
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.FinishSkeleton
                                                                                                                     x10$lang$FinishState$FinishSkeleton$$x10$lang$FinishState$FinishSkeleton$this(
                                                                                                                     ){
                            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.FinishSkeleton.this;
                        }
                        
                        
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                     __fieldInitializers51935(
                                                                                                                     ){
                            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = null;
                        }
                    // synthetic type for parameter mangling
                    public abstract static class __0$1x10$lang$FinishState$2 {}
                    
                }
                
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class Finish extends x10.lang.FinishState.FinishSkeleton implements x10.io.CustomSerialization
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Finish.class);
                    
                    public static final x10.rtt.RuntimeType<Finish> $RTT = x10.rtt.NamedType.<Finish> make(
                    "x10.lang.FinishState.Finish", /* base class */Finish.class
                    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.lang.FinishState.FinishSkeleton.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    // custom serializer
                    private transient x10.io.SerialData $$serialdata;
                    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
                    private Object readResolve() { return new Finish($$serialdata); }
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
                    oos.writeObject($$serialdata); }
                    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
                    $$serialdata = (x10.io.SerialData) ois.readObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Finish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + Finish.class + " calling"); } 
                        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                        $_obj.$init($$serialdata);
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        Finish $_obj = new Finish((java.lang.System[]) null);
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
                    
                    // constructor just for allocation
                    public Finish(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public Finish(final x10.lang.FinishState.RootFinish root){this((java.lang.System[]) null);
                                                                                      $init(root);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.Finish x10$lang$FinishState$Finish$$init$S(final x10.lang.FinishState.RootFinish root) { {
                                                                                                                                                          
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.lang.FinishState.RootFinishSkeleton)(root)));
                                                                                                                                                          
//#line 315 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                      }
                                                                                                                                                      return this;
                                                                                                                                                      }
                        
                        // constructor
                        public x10.lang.FinishState.Finish $init(final x10.lang.FinishState.RootFinish root){return x10$lang$FinishState$Finish$$init$S(root);}
                        
                        
                        
//#line 318 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public Finish(final x10.util.concurrent.SimpleLatch latch){this((java.lang.System[]) null);
                                                                                       $init(latch);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.Finish x10$lang$FinishState$Finish$$init$S(final x10.util.concurrent.SimpleLatch latch) { {
                                                                                                                                                           
//#line 319 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52451 =
                                                                                                                                                             ((x10.lang.FinishState.RootFinish)(new x10.lang.FinishState.RootFinish((java.lang.System[]) null).$init(((x10.util.concurrent.SimpleLatch)(latch)))));
                                                                                                                                                           
//#line 319 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.$init(((x10.lang.FinishState.RootFinish)(t52451)));
                                                                                                                                                       }
                                                                                                                                                       return this;
                                                                                                                                                       }
                        
                        // constructor
                        public x10.lang.FinishState.Finish $init(final x10.util.concurrent.SimpleLatch latch){return x10$lang$FinishState$Finish$$init$S(latch);}
                        
                        
                        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public Finish(){this((java.lang.System[]) null);
                                            $init();}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.Finish x10$lang$FinishState$Finish$$init$S() { {
                                                                                                                
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52452 =
                                                                                                                  ((x10.lang.FinishState.RootFinish)(new x10.lang.FinishState.RootFinish((java.lang.System[]) null).$init()));
                                                                                                                
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.$init(((x10.lang.FinishState.RootFinish)(t52452)));
                                                                                                            }
                                                                                                            return this;
                                                                                                            }
                        
                        // constructor
                        public x10.lang.FinishState.Finish $init(){return x10$lang$FinishState$Finish$$init$S();}
                        
                        
                        
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public Finish(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                  $init(ref, (x10.lang.FinishState.Finish.__0$1x10$lang$FinishState$2) null);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.Finish x10$lang$FinishState$Finish$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                      
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                      
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                  }
                                                                                                                                                                                                  return this;
                                                                                                                                                                                                  }
                        
                        // constructor
                        public x10.lang.FinishState.Finish $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$Finish$$init$S(ref, $dummy);}
                        
                        
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public Finish(final x10.io.SerialData data){this((java.lang.System[]) null);
                                                                        $init(data);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.Finish x10$lang$FinishState$Finish$$init$S(final x10.io.SerialData data) {x10$lang$FinishState$Finish$init_for_reflection(data);
                                                                                                                                        
                                                                                                                                        return this;
                                                                                                                                        }
                        public void x10$lang$FinishState$Finish$init_for_reflection(x10.io.SerialData data) {
                             {
                                
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final java.lang.Object t52914 =
                                  ((java.lang.Object)(data.
                                                        data));
                                
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52915 =
                                  ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52914))));
                                
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(t52915)), (x10.lang.FinishState.FinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52455 =
                                  ((x10.core.GlobalRef)(ref));
                                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52456 =
                                  ((x10.lang.Place)((t52455).home));
                                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52457 =
                                  t52456.
                                    id;
                                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52458 =
                                  x10.lang.Runtime.hereInt$O();
                                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52472 =
                                  ((int) t52457) ==
                                ((int) t52458);
                                
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52472) {
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52459 =
                                      ((x10.core.GlobalRef)(ref));
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> __desugarer__var__50__52238 =
                                      ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52459))));
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.GlobalRef<x10.lang.FinishState> ret52239 =
                                       null;
                                    
//#line 330 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52909 =
                                      ((x10.lang.Place)((__desugarer__var__50__52238).home));
                                    
//#line 330 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52910 =
                                      x10.rtt.Equality.equalsequals((t52909),(x10.lang.Runtime.home()));
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52911 =
                                      !(t52910);
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52911) {
                                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52912 =
                                          true;
                                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52912) {
                                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FailedDynamicCheckException t52913 =
                                              new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.FinishState]");
                                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t52913;
                                        }
                                    }
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52239 = ((x10.core.GlobalRef)(__desugarer__var__50__52238));
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52465 =
                                      ((x10.core.GlobalRef)(ret52239));
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52466 =
                                      (((x10.core.GlobalRef<x10.lang.FinishState>)(t52465))).$apply$G();
                                    
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52466));
                                } else {
                                    
//#line 332 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> _ref =
                                      ((x10.core.GlobalRef)(ref));
                                    
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.FinishStates t52468 =
                                      ((x10.lang.FinishState.FinishStates)(x10.lang.Runtime.finishStates));
                                    
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52469 =
                                      ((x10.core.GlobalRef)(ref));
                                    
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.Fun_0_0<x10.lang.FinishState.RemoteFinish> t52470 =
                                      ((x10.core.fun.Fun_0_0)(new x10.lang.FinishState.Finish.$Closure$112(_ref, (x10.lang.FinishState.Finish.$Closure$112.__0$1x10$lang$FinishState$2) null)));
                                    
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52471 =
                                      t52468.$apply__0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$2(((x10.core.GlobalRef)(t52469)),
                                                                                                          ((x10.core.fun.Fun_0_0)(t52470)));
                                    
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52471));
                                }
                            }}
                            
                        // constructor
                        public x10.lang.FinishState.Finish $init(final x10.io.SerialData data){return x10$lang$FinishState$Finish$$init$S(data);}
                        
                        
                        
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.Finish
                                                                                                                     x10$lang$FinishState$Finish$$x10$lang$FinishState$Finish$this(
                                                                                                                     ){
                            
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.Finish.this;
                        }
                        
                        @x10.core.X10Generated public static class $Closure$112 extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$112.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$112> $RTT = x10.rtt.StaticFunType.<$Closure$112> make(
                            /* base class */$Closure$112.class
                            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.lang.FinishState.RemoteFinish.$RTT), x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$112 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$112.class + " calling"); } 
                                x10.core.GlobalRef _ref = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj._ref = _ref;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$112 $_obj = new $Closure$112((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (_ref instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this._ref);
                                } else {
                                $serializer.write(this._ref);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$112(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            // bridge for method abstract public ()=>U.operator()():U
                            public x10.lang.FinishState.RemoteFinish
                              $apply$G(){return $apply();}
                            
                                
                                public x10.lang.FinishState.RemoteFinish
                                  $apply(
                                  ){
                                    
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteFinish t52467 =
                                      ((x10.lang.FinishState.RemoteFinish)(new x10.lang.FinishState.RemoteFinish((java.lang.System[]) null).$init(((x10.core.GlobalRef<x10.lang.FinishState>)(this.
                                                                                                                                                                                                _ref)), (x10.lang.FinishState.RemoteFinish.__0$1x10$lang$FinishState$2) null)));
                                    
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52467;
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> _ref;
                                
                                public $Closure$112(final x10.core.GlobalRef<x10.lang.FinishState> _ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                      this._ref = ((x10.core.GlobalRef)(_ref));
                                                                                                                                                  }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2 {}
                                
                            }
                            
                        // synthetic type for parameter mangling
                        public abstract static class __0$1x10$lang$FinishState$2 {}
                        
                    }
                    
                
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RootFinish extends x10.lang.FinishState.RootFinishSkeleton implements x10.x10rt.X10JavaSerializable
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RootFinish.class);
                    
                    public static final x10.rtt.RuntimeType<RootFinish> $RTT = x10.rtt.NamedType.<RootFinish> make(
                    "x10.lang.FinishState.RootFinish", /* base class */RootFinish.class
                    , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.RootFinishSkeleton.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RootFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RootFinish.class + " calling"); } 
                        x10.lang.FinishState.RootFinishSkeleton.$_deserialize_body($_obj, $deserializer);
                        $_obj.count = $deserializer.readInt();
                        x10.util.Stack exceptions = (x10.util.Stack) $deserializer.readRef();
                        $_obj.exceptions = exceptions;
                        x10.core.IndexedMemoryChunk counts = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                        $_obj.counts = counts;
                        x10.core.IndexedMemoryChunk seen = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                        $_obj.seen = seen;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        RootFinish $_obj = new RootFinish((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        super.$_serialize($serializer);
                        $serializer.write(this.count);
                        if (exceptions instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.exceptions);
                        } else {
                        $serializer.write(this.exceptions);
                        }
                        if (counts instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.counts);
                        } else {
                        $serializer.write(this.counts);
                        }
                        if (seen instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.seen);
                        } else {
                        $serializer.write(this.seen);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public RootFinish(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
//#line 339 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public transient x10.util.concurrent.SimpleLatch latch;
                        
//#line 340 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public int count;
                        
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.Stack<x10.core.X10Throwable> exceptions;
                        
//#line 342 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.IndexedMemoryChunk<x10.core.Int> counts;
                        
//#line 343 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.IndexedMemoryChunk<x10.core.Boolean> seen;
                        
                        
//#line 344 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public RootFinish(){this((java.lang.System[]) null);
                                                $init();}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.RootFinish x10$lang$FinishState$RootFinish$$init$S() { {
                                                                                                                        
//#line 344 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                        
//#line 344 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                        
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51936();
                                                                                                                        
//#line 345 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.latch = ((x10.util.concurrent.SimpleLatch)(new x10.util.concurrent.SimpleLatch()));
                                                                                                                    }
                                                                                                                    return this;
                                                                                                                    }
                        
                        // constructor
                        public x10.lang.FinishState.RootFinish $init(){return x10$lang$FinishState$RootFinish$$init$S();}
                        
                        
                        
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public RootFinish(final x10.util.concurrent.SimpleLatch latch){this((java.lang.System[]) null);
                                                                                           $init(latch);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.RootFinish x10$lang$FinishState$RootFinish$$init$S(final x10.util.concurrent.SimpleLatch latch) { {
                                                                                                                                                                   
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                                                                   
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                   
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51936();
                                                                                                                                                                   
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.latch = ((x10.util.concurrent.SimpleLatch)(latch));
                                                                                                                                                               }
                                                                                                                                                               return this;
                                                                                                                                                               }
                        
                        // constructor
                        public x10.lang.FinishState.RootFinish $init(final x10.util.concurrent.SimpleLatch latch){return x10$lang$FinishState$RootFinish$$init$S(latch);}
                        
                        
                        
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifySubActivitySpawn(
                                                                                                                     final x10.lang.Place place){
                            
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place p =
                              ((x10.lang.Place)(place.parent()));
                            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52473 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52473.lock();
                            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52474 =
                              ((x10.core.GlobalRef)(this.ref()));
                            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52475 =
                              ((x10.lang.Place)((t52474).home));
                            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52479 =
                              x10.rtt.Equality.equalsequals((p),(t52475));
                            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52479) {
                                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish x52241 =
                                  ((x10.lang.FinishState.RootFinish)(this));
                                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52476 =
                                  x52241.
                                    count;
                                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52477 =
                                  ((t52476) + (((int)(1))));
                                
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x52241.count = t52477;
                                
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52478 =
                                  ((x10.util.concurrent.SimpleLatch)(latch));
                                
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52478.unlock();
                                
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                            }
                            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52480 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52481 =
                              ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52480))).length);
                            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52486 =
                              ((int) t52481) ==
                            ((int) 0);
                            
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52486) {
                                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52482 =
                                  x10.lang.Place.getInitialized$MAX_PLACES();
                                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52483 =
                                  ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(t52482)), true)));
                                
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.counts = ((x10.core.IndexedMemoryChunk)(t52483));
                                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52484 =
                                  x10.lang.Place.getInitialized$MAX_PLACES();
                                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t52485 =
                                  ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Boolean>allocate(x10.rtt.Types.BOOLEAN, ((int)(t52484)), true)));
                                
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.seen = ((x10.core.IndexedMemoryChunk)(t52485));
                            }
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> x52243 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int y52244 =
                              p.
                                id;
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int ret52247 =
                               0;
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52916 =
                              ((int[])x52243.value)[y52244];
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int r52917 =
                              ((t52916) + (((int)(1))));
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])x52243.value)[y52244] = r52917;
                            
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52247 = r52917;
                            
//#line 363 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52488 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 363 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52488.unlock();
                        }
                        
                        
//#line 365 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityTermination(
                                                                                                                     ){
                            
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52489 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52489.lock();
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish x52249 =
                              ((x10.lang.FinishState.RootFinish)(this));
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52490 =
                              x52249.
                                count;
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52491 =
                              ((t52490) - (((int)(1))));
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52492 =
                              x52249.count = t52491;
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52494 =
                              ((int) t52492) !=
                            ((int) 0);
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52494) {
                                
//#line 368 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52493 =
                                  ((x10.util.concurrent.SimpleLatch)(latch));
                                
//#line 368 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52493.unlock();
                                
//#line 369 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                            }
                            
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52495 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52496 =
                              ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52495))).length);
                            
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52508 =
                              ((int) t52496) !=
                            ((int) 0);
                            
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52508) {
                                
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i =
                                  0;
                                
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                                    
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52498 =
                                      i;
                                    
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52499 =
                                      x10.lang.Place.getInitialized$MAX_PLACES();
                                    
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52507 =
                                      ((t52498) < (((int)(t52499))));
                                    
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t52507)) {
                                        
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                    }
                                    
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52918 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52919 =
                                      i;
                                    
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52920 =
                                      ((int[])t52918.value)[t52919];
                                    
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52921 =
                                      ((int) t52920) !=
                                    ((int) 0);
                                    
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52921) {
                                        
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52922 =
                                          ((x10.util.concurrent.SimpleLatch)(latch));
                                        
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52922.unlock();
                                        
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                                    }
                                    
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52923 =
                                      i;
                                    
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52924 =
                                      ((t52923) + (((int)(1))));
                                    
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i = t52924;
                                }
                            }
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52509 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 379 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52509.unlock();
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52510 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52510.release();
                        }
                        
                        
//#line 382 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     process(
                                                                                                                     final x10.core.X10Throwable t){
                            
//#line 383 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52511 =
                              ((x10.util.Stack)(exceptions));
                            
//#line 383 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52513 =
                              ((null) == (t52511));
                            
//#line 383 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52513) {
                                
//#line 383 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52512 =
                                  ((x10.util.Stack)(new x10.util.Stack<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
                                
//#line 383 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = ((x10.util.Stack)(t52512));
                            }
                            
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52514 =
                              ((x10.util.Stack)(exceptions));
                            
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.Stack<x10.core.X10Throwable>)t52514).push__0x10$util$Stack$$T$O(((x10.core.X10Throwable)(t)));
                        }
                        
                        
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     pushException(
                                                                                                                     final x10.core.X10Throwable t){
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52515 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52515.lock();
                            
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process(((x10.core.X10Throwable)(t)));
                            
//#line 389 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52516 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 389 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52516.unlock();
                        }
                        
                        
//#line 391 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     waitForFinish(
                                                                                                                     ){
                            
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.notifyActivityTermination();
                            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52517 =
                              x10.lang.Runtime.STRICT_FINISH;
                            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
boolean t52521 =
                              !(t52517);
                            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52521) {
                                
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
boolean t52520 =
                                  x10.lang.Runtime.STATIC_THREADS;
                                
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t52520)) {
                                    
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52518 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52519 =
                                      ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52518))).length);
                                    
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52520 = ((int) t52519) ==
                                    ((int) 0);
                                }
                                
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52521 = t52520;
                            }
                            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52524 =
                              t52521;
                            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52524) {
                                
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Runtime.Worker t52522 =
                                  x10.lang.Runtime.worker();
                                
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52523 =
                                  ((x10.util.concurrent.SimpleLatch)(latch));
                                
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52522.join(((x10.util.concurrent.SimpleLatch)(t52523)));
                            }
                            
//#line 396 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52525 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 396 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52525.await();
                            
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52526 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52527 =
                              ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52526))).length);
                            
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52541 =
                              ((int) t52527) !=
                            ((int) 0);
                            
//#line 397 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52541) {
                                
//#line 398 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> root =
                                  ((x10.core.GlobalRef)(this.ref()));
                                
//#line 399 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 closure =
                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RootFinish.$Closure$113(root, (x10.lang.FinishState.RootFinish.$Closure$113.__0$1x10$lang$FinishState$2) null)));
                                
//#line 400 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t52529 =
                                  ((x10.core.IndexedMemoryChunk)(seen));
                                
//#line 400 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52530 =
                                  x10.lang.Runtime.hereInt$O();
                                
//#line 400 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((boolean[])t52529.value)[t52530] = false;
                                
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i52931 =
                                  0;
                                
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                                    
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52932 =
                                      i52931;
                                    
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52933 =
                                      x10.lang.Place.getInitialized$MAX_PLACES();
                                    
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52934 =
                                      ((t52932) < (((int)(t52933))));
                                    
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t52934)) {
                                        
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                    }
                                    
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t52925 =
                                      ((x10.core.IndexedMemoryChunk)(seen));
                                    
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52926 =
                                      i52931;
                                    
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52927 =
                                      ((boolean[])t52925.value)[t52926];
                                    
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52927) {
                                        
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52928 =
                                          i52931;
                                        
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(t52928)), closure);
                                    }
                                    
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52929 =
                                      i52931;
                                    
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52930 =
                                      ((t52929) + (((int)(1))));
                                    
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i52931 = t52930;
                                }
                                
//#line 404 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                            }
                            
//#line 406 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52542 =
                              ((x10.util.Stack)(exceptions));
                            
//#line 406 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                              x10.lang.MultipleExceptions.make__0$1x10$lang$Throwable$2(((x10.util.Stack)(t52542)));
                            
//#line 407 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52543 =
                              ((null) != (t));
                            
//#line 407 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52543) {
                                
//#line 407 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t;
                            }
                        }
                        
                        
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     process__0$1x10$lang$Int$2(
                                                                                                                     final x10.core.IndexedMemoryChunk rail){
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52550 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52544 =
                              ((x10.core.GlobalRef)(this.ref()));
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52545 =
                              ((x10.lang.Place)((t52544).home));
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52551 =
                              t52545.
                                id;
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52546 =
                              ((x10.core.GlobalRef)(this.ref()));
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52547 =
                              ((x10.lang.Place)((t52546).home));
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52548 =
                              t52547.
                                id;
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52549 =
                              ((int[])rail.value)[t52548];
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52552 =
                              (-(t52549));
                            
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])t52550.value)[t52551] = t52552;
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish x52251 =
                              ((x10.lang.FinishState.RootFinish)(this));
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52553 =
                              ((x10.core.GlobalRef)(this.ref()));
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52554 =
                              ((x10.lang.Place)((t52553).home));
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52555 =
                              t52554.
                                id;
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int y52252 =
                              ((int[])rail.value)[t52555];
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52556 =
                              x52251.
                                count;
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52557 =
                              ((t52556) + (((int)(y52252))));
                            
//#line 412 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x52251.count = t52557;
                            
//#line 413 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52558 =
                              count;
                            
//#line 413 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
boolean b =
                              ((int) t52558) ==
                            ((int) 0);
                            
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i52957 =
                              0;
                            {
                                
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int[] rail$value53060 =
                                  ((int[])rail.value);
                                
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                                    
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52958 =
                                      i52957;
                                    
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52959 =
                                      x10.lang.Place.getInitialized$MAX_PLACES();
                                    
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52960 =
                                      ((t52958) < (((int)(t52959))));
                                    
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t52960)) {
                                        
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                    }
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> x52939 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int y52940 =
                                      i52957;
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52941 =
                                      i52957;
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int z52942 =
                                      ((int)rail$value53060[t52941]);
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int ret52943 =
                                       0;
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52935 =
                                      ((int[])x52939.value)[y52940];
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int r52936 =
                                      ((t52935) + (((int)(z52942))));
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])x52939.value)[y52940] = r52936;
                                    
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52943 = r52936;
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> x52944 =
                                      ((x10.core.IndexedMemoryChunk)(seen));
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int y52945 =
                                      i52957;
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52946 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52947 =
                                      i52957;
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52948 =
                                      ((int[])t52946.value)[t52947];
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean z52949 =
                                      ((int) t52948) !=
                                    ((int) 0);
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
boolean ret52950 =
                                       false;
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52937 =
                                      ((boolean[])x52944.value)[y52945];
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean r52938 =
                                      ((t52937) | (((boolean)(z52949))));
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((boolean[])x52944.value)[y52945] = r52938;
                                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52950 = r52938;
                                    
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52951 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52952 =
                                      i52957;
                                    
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52953 =
                                      ((int[])t52951.value)[t52952];
                                    
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52954 =
                                      ((int) t52953) !=
                                    ((int) 0);
                                    
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52954) {
                                        
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
b = false;
                                    }
                                    
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52955 =
                                      i52957;
                                    
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52956 =
                                      ((t52955) + (((int)(1))));
                                    
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i52957 = t52956;
                                }
                            }
                            
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52576 =
                              b;
                            
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52576) {
                                
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52575 =
                                  ((x10.util.concurrent.SimpleLatch)(latch));
                                
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52575.release();
                            }
                        }
                        
                        
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notify__0$1x10$lang$Int$2(
                                                                                                                     final x10.core.IndexedMemoryChunk rail){
                            
//#line 423 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52577 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 423 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52577.lock();
                            
//#line 424 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process__0$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(rail)));
                            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52578 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52578.unlock();
                        }
                        
                        
//#line 428 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     process__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(
                                                                                                                     final x10.core.IndexedMemoryChunk rail){
                            
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i52983 =
                              0;
                            {
                                
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair[] rail$value53061 =
                                  ((x10.util.Pair[])rail.value);
                                
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                                    
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52984 =
                                      i52983;
                                    
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52985 =
                                      ((((x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>>)(rail))).length);
                                    
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52986 =
                                      ((t52984) < (((int)(t52985))));
                                    
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t52986)) {
                                        
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                    }
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> x52963 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52964 =
                                      i52983;
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair<x10.core.Int, x10.core.Int> t52965 =
                                      ((x10.util.Pair<x10.core.Int, x10.core.Int>)rail$value53061[t52964]);
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int y52966 =
                                      x10.core.Int.$unbox(x10.core.Int.$unbox(((x10.util.Pair<x10.core.Int, x10.core.Int>)t52965).
                                                                                first));
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52967 =
                                      i52983;
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair<x10.core.Int, x10.core.Int> t52968 =
                                      ((x10.util.Pair<x10.core.Int, x10.core.Int>)rail$value53061[t52967]);
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int z52969 =
                                      x10.core.Int.$unbox(x10.core.Int.$unbox(((x10.util.Pair<x10.core.Int, x10.core.Int>)t52968).
                                                                                second));
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int ret52970 =
                                       0;
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52961 =
                                      ((int[])x52963.value)[y52966];
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int r52962 =
                                      ((t52961) + (((int)(z52969))));
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])x52963.value)[y52966] = r52962;
                                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52970 = r52962;
                                    
//#line 431 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t52971 =
                                      ((x10.core.IndexedMemoryChunk)(seen));
                                    
//#line 431 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52972 =
                                      i52983;
                                    
//#line 431 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair<x10.core.Int, x10.core.Int> t52973 =
                                      ((x10.util.Pair<x10.core.Int, x10.core.Int>)rail$value53061[t52972]);
                                    
//#line 431 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52974 =
                                      x10.core.Int.$unbox(x10.core.Int.$unbox(((x10.util.Pair<x10.core.Int, x10.core.Int>)t52973).
                                                                                first));
                                    
//#line 431 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((boolean[])t52971.value)[t52974] = true;
                                    
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52975 =
                                      i52983;
                                    
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52976 =
                                      ((t52975) + (((int)(1))));
                                    
//#line 429 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i52983 = t52976;
                                }
                            }
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish x52271 =
                              ((x10.lang.FinishState.RootFinish)(this));
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52596 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52594 =
                              ((x10.core.GlobalRef)(this.ref()));
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52595 =
                              ((x10.lang.Place)((t52594).home));
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52597 =
                              t52595.
                                id;
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int y52272 =
                              ((int[])t52596.value)[t52597];
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52598 =
                              x52271.
                                count;
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52599 =
                              ((t52598) + (((int)(y52272))));
                            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x52271.count = t52599;
                            
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52602 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52600 =
                              ((x10.core.GlobalRef)(this.ref()));
                            
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52601 =
                              ((x10.lang.Place)((t52600).home));
                            
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52603 =
                              t52601.
                                id;
                            
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])t52602.value)[t52603] = 0;
                            
//#line 435 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52604 =
                              count;
                            
//#line 435 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52605 =
                              ((int) t52604) !=
                            ((int) 0);
                            
//#line 435 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52605) {
                                
//#line 435 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                            }
                            
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i52987 =
                              0;
                            
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                                
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52988 =
                                  i52987;
                                
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52989 =
                                  x10.lang.Place.getInitialized$MAX_PLACES();
                                
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52990 =
                                  ((t52988) < (((int)(t52989))));
                                
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t52990)) {
                                    
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                }
                                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52977 =
                                  ((x10.core.IndexedMemoryChunk)(counts));
                                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52978 =
                                  i52987;
                                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52979 =
                                  ((int[])t52977.value)[t52978];
                                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52980 =
                                  ((int) t52979) !=
                                ((int) 0);
                                
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52980) {
                                    
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                                }
                                
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52981 =
                                  i52987;
                                
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52982 =
                                  ((t52981) + (((int)(1))));
                                
//#line 436 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i52987 = t52982;
                            }
                            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52616 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52616.release();
                        }
                        
                        
//#line 442 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(
                                                                                                                     final x10.core.IndexedMemoryChunk rail){
                            
//#line 443 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52617 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 443 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52617.lock();
                            
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(rail)));
                            
//#line 445 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52618 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 445 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52618.unlock();
                        }
                        
                        
//#line 448 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notify__0$1x10$lang$Int$2(
                                                                                                                     final x10.core.IndexedMemoryChunk rail,
                                                                                                                     final x10.core.X10Throwable t){
                            
//#line 449 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52619 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 449 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52619.lock();
                            
//#line 450 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process(((x10.core.X10Throwable)(t)));
                            
//#line 451 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process__0$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(rail)));
                            
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52620 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52620.unlock();
                        }
                        
                        
//#line 455 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(
                                                                                                                     final x10.core.IndexedMemoryChunk rail,
                                                                                                                     final x10.core.X10Throwable t){
                            
//#line 456 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52621 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 456 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52621.lock();
                            
//#line 457 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process(((x10.core.X10Throwable)(t)));
                            
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(rail)));
                            
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52622 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52622.unlock();
                        }
                        
                        
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.concurrent.SimpleLatch
                                                                                                                     simpleLatch(
                                                                                                                     ){
                            
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52623 =
                              ((x10.util.concurrent.SimpleLatch)(latch));
                            
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52623;
                        }
                        
                        
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RootFinish
                                                                                                                     x10$lang$FinishState$RootFinish$$x10$lang$FinishState$RootFinish$this(
                                                                                                                     ){
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RootFinish.this;
                        }
                        
                        
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                     __fieldInitializers51936(
                                                                                                                     ){
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.latch = null;
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.count = 1;
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = null;
                            
//#line 342 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52624 =
                              (x10.core.IndexedMemoryChunk<x10.core.Int>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, x10.rtt.Types.INT));
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.counts = t52624;
                            
//#line 343 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t52625 =
                              (x10.core.IndexedMemoryChunk<x10.core.Boolean>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, x10.rtt.Types.BOOLEAN));
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.seen = t52625;
                        }
                        
                        @x10.core.X10Generated public static class $Closure$113 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$113.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$113> $RTT = x10.rtt.StaticVoidFunType.<$Closure$113> make(
                            /* base class */$Closure$113.class
                            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$113 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$113.class + " calling"); } 
                                x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj.root = root;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$113 $_obj = new $Closure$113((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (root instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                                } else {
                                $serializer.write(this.root);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$113(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            
                                
                                public void
                                  $apply(
                                  ){
                                    
//#line 399 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.FinishStates t52528 =
                                      ((x10.lang.FinishState.FinishStates)(x10.lang.Runtime.finishStates));
                                    
//#line 399 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52528.remove__0$1x10$lang$FinishState$2(((x10.core.GlobalRef)(this.
                                                                                                                                                                                                root)));
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> root;
                                
                                public $Closure$113(final x10.core.GlobalRef<x10.lang.FinishState> root, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                      this.root = ((x10.core.GlobalRef)(root));
                                                                                                                                                  }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2 {}
                                
                            }
                            
                        
                    }
                    
                
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RemoteFinish extends x10.lang.FinishState.RemoteFinishSkeleton implements x10.x10rt.X10JavaSerializable
                                                                                                           {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteFinish.class);
                    
                    public static final x10.rtt.RuntimeType<RemoteFinish> $RTT = x10.rtt.NamedType.<RemoteFinish> make(
                    "x10.lang.FinishState.RemoteFinish", /* base class */RemoteFinish.class
                    , /* parents */ new x10.rtt.Type[] {x10.lang.FinishState.RemoteFinishSkeleton.$RTT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RemoteFinish.class + " calling"); } 
                        x10.lang.FinishState.RemoteFinishSkeleton.$_deserialize_body($_obj, $deserializer);
                        x10.util.Stack exceptions = (x10.util.Stack) $deserializer.readRef();
                        $_obj.exceptions = exceptions;
                        $_obj.count = $deserializer.readInt();
                        x10.core.IndexedMemoryChunk counts = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                        $_obj.counts = counts;
                        x10.core.IndexedMemoryChunk places = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                        $_obj.places = places;
                        $_obj.length = $deserializer.readInt();
                        x10.core.concurrent.AtomicInteger local = (x10.core.concurrent.AtomicInteger) $deserializer.readRef();
                        $_obj.local = local;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        RemoteFinish $_obj = new RemoteFinish((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        super.$_serialize($serializer);
                        if (exceptions instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.exceptions);
                        } else {
                        $serializer.write(this.exceptions);
                        }
                        $serializer.write(this.count);
                        if (counts instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.counts);
                        } else {
                        $serializer.write(this.counts);
                        }
                        if (places instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.places);
                        } else {
                        $serializer.write(this.places);
                        }
                        $serializer.write(this.length);
                        if (local instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.local);
                        } else {
                        $serializer.write(this.local);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public RemoteFinish(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
//#line 466 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.util.Stack<x10.core.X10Throwable> exceptions;
                        
//#line 467 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public transient x10.util.concurrent.Lock lock;
                        
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public int count;
                        
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.IndexedMemoryChunk<x10.core.Int> counts;
                        
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.IndexedMemoryChunk<x10.core.Int> places;
                        
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public int length;
                        
//#line 472 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.concurrent.AtomicInteger local;
                        
                        
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                        public RemoteFinish(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                        $init(ref, (x10.lang.FinishState.RemoteFinish.__0$1x10$lang$FinishState$2) null);}
                        
                        // constructor for non-virtual call
                        final public x10.lang.FinishState.RemoteFinish x10$lang$FinishState$RemoteFinish$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy) { {
                                                                                                                                                                                                                  
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.RemoteFinishSkeleton.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                                  
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                  
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51937();
                                                                                                                                                                                                              }
                                                                                                                                                                                                              return this;
                                                                                                                                                                                                              }
                        
                        // constructor
                        public x10.lang.FinishState.RemoteFinish $init(final x10.core.GlobalRef<x10.lang.FinishState> ref, __0$1x10$lang$FinishState$2 $dummy){return x10$lang$FinishState$RemoteFinish$$init$S(ref, $dummy);}
                        
                        
                        
//#line 476 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityCreation(
                                                                                                                     ){
                            
//#line 477 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52626 =
                              ((x10.core.concurrent.AtomicInteger)(local));
                            
//#line 477 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.util.Util.eval(t52626.getAndIncrement());
                        }
                        
                        
//#line 479 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifySubActivitySpawn(
                                                                                                                     final x10.lang.Place place){
                            
//#line 480 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int id =
                              x10.lang.Runtime.hereInt$O();
                            
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52627 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 481 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52627.lock();
                            
//#line 482 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52628 =
                              place.
                                id;
                            
//#line 482 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52632 =
                              ((int) t52628) ==
                            ((int) id);
                            
//#line 482 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52632) {
                                
//#line 483 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteFinish x52273 =
                                  ((x10.lang.FinishState.RemoteFinish)(this));
                                
//#line 483 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                                
//#line 483 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52629 =
                                  x52273.
                                    count;
                                
//#line 483 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52630 =
                                  ((t52629) + (((int)(1))));
                                
//#line 483 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x52273.count = t52630;
                                
//#line 484 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52631 =
                                  ((x10.util.concurrent.Lock)(lock));
                                
//#line 484 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52631.unlock();
                                
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                            }
                            
//#line 487 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52633 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 487 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52634 =
                              ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52633))).length);
                            
//#line 487 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52640 =
                              ((int) t52634) ==
                            ((int) 0);
                            
//#line 487 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52640) {
                                
//#line 488 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52635 =
                                  x10.lang.Place.getInitialized$MAX_PLACES();
                                
//#line 488 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52636 =
                                  ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(t52635)), true)));
                                
//#line 488 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.counts = ((x10.core.IndexedMemoryChunk)(t52636));
                                
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52637 =
                                  x10.lang.Place.getInitialized$MAX_PLACES();
                                
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52638 =
                                  ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(t52637)), true)));
                                
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.places = ((x10.core.IndexedMemoryChunk)(t52638));
                                
//#line 490 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52639 =
                                  ((x10.core.IndexedMemoryChunk)(places));
                                
//#line 490 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])t52639.value)[0] = id;
                            }
                            
//#line 492 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52641 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 492 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52642 =
                              place.
                                id;
                            
//#line 492 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int old =
                              ((int[])t52641.value)[t52642];
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> x52275 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int y52276 =
                              place.
                                id;
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int ret52279 =
                               0;
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52991 =
                              ((int[])x52275.value)[y52276];
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int r52992 =
                              ((t52991) + (((int)(1))));
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])x52275.value)[y52276] = r52992;
                            
//#line 493 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52279 = r52992;
                            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
boolean t52645 =
                              ((int) old) ==
                            ((int) 0);
                            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52645) {
                                
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52644 =
                                  place.
                                    id;
                                
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52645 = ((int) id) !=
                                ((int) t52644);
                            }
                            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52652 =
                              t52645;
                            
//#line 494 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52652) {
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52649 =
                                  ((x10.core.IndexedMemoryChunk)(places));
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteFinish x52281 =
                                  ((x10.lang.FinishState.RemoteFinish)(this));
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52646 =
                                  x52281.
                                    length;
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52647 =
                                  ((t52646) + (((int)(1))));
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52648 =
                                  x52281.length = t52647;
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52650 =
                                  ((t52648) - (((int)(1))));
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52651 =
                                  place.
                                    id;
                                
//#line 495 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])t52649.value)[t52650] = t52651;
                            }
                            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52653 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 497 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52653.unlock();
                        }
                        
                        
//#line 499 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     pushException(
                                                                                                                     final x10.core.X10Throwable t){
                            
//#line 500 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52654 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 500 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52654.lock();
                            
//#line 501 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52655 =
                              ((x10.util.Stack)(exceptions));
                            
//#line 501 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52657 =
                              ((null) == (t52655));
                            
//#line 501 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52657) {
                                
//#line 501 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52656 =
                                  ((x10.util.Stack)(new x10.util.Stack<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
                                
//#line 501 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = ((x10.util.Stack)(t52656));
                            }
                            
//#line 502 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52658 =
                              ((x10.util.Stack)(exceptions));
                            
//#line 502 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.Stack<x10.core.X10Throwable>)t52658).push__0x10$util$Stack$$T$O(((x10.core.X10Throwable)(t)));
                            
//#line 503 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52659 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 503 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52659.unlock();
                        }
                        
                        
//#line 505 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                     notifyActivityTermination(
                                                                                                                     ){
                            
//#line 506 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52660 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 506 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52660.lock();
                            
//#line 507 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteFinish x52283 =
                              ((x10.lang.FinishState.RemoteFinish)(this));
                            
//#line 507 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                            
//#line 507 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52661 =
                              x52283.
                                count;
                            
//#line 507 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52662 =
                              ((t52661) - (((int)(1))));
                            
//#line 507 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x52283.count = t52662;
                            
//#line 508 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52663 =
                              ((x10.core.concurrent.AtomicInteger)(local));
                            
//#line 508 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52664 =
                              t52663.decrementAndGet();
                            
//#line 508 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52666 =
                              ((t52664) > (((int)(0))));
                            
//#line 508 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52666) {
                                
//#line 509 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52665 =
                                  ((x10.util.concurrent.Lock)(lock));
                                
//#line 509 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52665.unlock();
                                
//#line 510 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                            }
                            
//#line 512 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52667 =
                              ((x10.util.Stack)(exceptions));
                            
//#line 512 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                              x10.lang.MultipleExceptions.make__0$1x10$lang$Throwable$2(((x10.util.Stack)(t52667)));
                            
//#line 513 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> ref =
                              ((x10.core.GlobalRef)(this.ref()));
                            
//#line 514 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 closure;
                            
//#line 515 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52668 =
                              ((x10.core.IndexedMemoryChunk)(counts));
                            
//#line 515 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52669 =
                              ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52668))).length);
                            
//#line 515 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52717 =
                              ((int) t52669) !=
                            ((int) 0);
                            
//#line 515 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52717) {
                                
//#line 516 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52670 =
                                  ((x10.core.IndexedMemoryChunk)(counts));
                                
//#line 516 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52671 =
                                  x10.lang.Runtime.hereInt$O();
                                
//#line 516 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52672 =
                                  count;
                                
//#line 516 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])t52670.value)[t52671] = t52672;
                                
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52673 =
                                  length;
                                
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52674 =
                                  ((2) * (((int)(t52673))));
                                
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52675 =
                                  x10.lang.Place.getInitialized$MAX_PLACES();
                                
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52705 =
                                  ((t52674) > (((int)(t52675))));
                                
//#line 517 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52705) {
                                    
//#line 518 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52676 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 518 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52677 =
                                      ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52676))).length);
                                    
//#line 518 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> message =
                                      x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(t52677)), false);
                                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52679 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52678 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52680 =
                                      ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52678))).length);
                                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.IndexedMemoryChunk.<x10.core.Int>copy(t52679,((int)(0)),message,((int)(0)),((int)(t52680)));
                                    
//#line 520 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52685 =
                                      ((null) != (t));
                                    
//#line 520 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52685) {
                                        
//#line 521 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52682 =
                                          ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinish.$Closure$114(ref,
                                                                                                                         message,
                                                                                                                         t, (x10.lang.FinishState.RemoteFinish.$Closure$114.__0$1x10$lang$FinishState$2__1$1x10$lang$Int$2) null)));
                                        
//#line 521 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52682));
                                    } else {
                                        
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52684 =
                                          ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinish.$Closure$115(ref,
                                                                                                                         message, (x10.lang.FinishState.RemoteFinish.$Closure$115.__0$1x10$lang$FinishState$2__1$1x10$lang$Int$2) null)));
                                        
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52684));
                                    }
                                } else {
                                    
//#line 526 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52686 =
                                      length;
                                    
//#line 526 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message =
                                      x10.core.IndexedMemoryChunk.<x10.util.Pair<x10.core.Int, x10.core.Int>>allocate(x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), ((int)(t52686)), false);
                                    
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i52179min53006 =
                                      0;
                                    
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53007 =
                                      length;
                                    
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i52179max53008 =
                                      ((t53007) - (((int)(1))));
                                    
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i53003 =
                                      i52179min53006;
                                    {
                                        
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair[] message$value53062 =
                                          ((x10.util.Pair[])message.value);
                                        
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                        true;
                                                                                                                                        ) {
                                            
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53004 =
                                              i53003;
                                            
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t53005 =
                                              ((t53004) <= (((int)(i52179max53008))));
                                            
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t53005)) {
                                                
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                            }
                                            
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i53000 =
                                              i53003;
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52993 =
                                              ((x10.core.IndexedMemoryChunk)(places));
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52994 =
                                              ((int[])t52993.value)[i53000];
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52995 =
                                              ((x10.core.IndexedMemoryChunk)(counts));
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52996 =
                                              ((x10.core.IndexedMemoryChunk)(places));
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52997 =
                                              ((int[])t52996.value)[i53000];
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52998 =
                                              ((int[])t52995.value)[t52997];
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair<x10.core.Int, x10.core.Int> t52999 =
                                              new x10.util.Pair<x10.core.Int, x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT, x10.rtt.Types.INT).$init(x10.core.Int.$box(t52994),
                                                                                                                                                                   x10.core.Int.$box(t52998), (x10.util.Pair.__0x10$util$Pair$$T__1x10$util$Pair$$U) null);
                                            
//#line 528 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
message$value53062[i53000]=t52999;
                                            
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53001 =
                                              i53003;
                                            
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53002 =
                                              ((t53001) + (((int)(1))));
                                            
//#line 527 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i53003 = t53002;
                                        }
                                    }
                                    
//#line 530 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52704 =
                                      ((null) != (t));
                                    
//#line 530 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52704) {
                                        
//#line 531 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52701 =
                                          ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinish.$Closure$116(ref,
                                                                                                                         message,
                                                                                                                         t, (x10.lang.FinishState.RemoteFinish.$Closure$116.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2) null)));
                                        
//#line 531 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52701));
                                    } else {
                                        
//#line 533 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52703 =
                                          ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinish.$Closure$117(ref,
                                                                                                                         message, (x10.lang.FinishState.RemoteFinish.$Closure$117.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2) null)));
                                        
//#line 533 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52703));
                                    }
                                }
                                
//#line 536 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52707 =
                                  ((x10.core.IndexedMemoryChunk)(counts));
                                
//#line 536 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52706 =
                                  ((x10.core.IndexedMemoryChunk)(counts));
                                
//#line 536 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52708 =
                                  ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52706))).length);
                                
//#line 536 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
(((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52707))).clear(((int)(0)), ((int)(t52708)));
                                
//#line 537 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.length = 1;
                            } else {
                                
//#line 539 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message =
                                  x10.core.IndexedMemoryChunk.<x10.util.Pair<x10.core.Int, x10.core.Int>>allocate(x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), ((int)(1)), false);
                                
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52709 =
                                  x10.lang.Runtime.hereInt$O();
                                
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52710 =
                                  count;
                                
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair<x10.core.Int, x10.core.Int> t52711 =
                                  new x10.util.Pair<x10.core.Int, x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT, x10.rtt.Types.INT).$init(x10.core.Int.$box(t52709),
                                                                                                                                                       x10.core.Int.$box(t52710), (x10.util.Pair.__0x10$util$Pair$$T__1x10$util$Pair$$U) null);
                                
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.Pair[])message.value)[0] = t52711;
                                
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52716 =
                                  ((null) != (t));
                                
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52716) {
                                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52713 =
                                      ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinish.$Closure$118(ref,
                                                                                                                     message,
                                                                                                                     t, (x10.lang.FinishState.RemoteFinish.$Closure$118.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2) null)));
                                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52713));
                                } else {
                                    
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52715 =
                                      ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteFinish.$Closure$119(ref,
                                                                                                                     message, (x10.lang.FinishState.RemoteFinish.$Closure$119.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2) null)));
                                    
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52715));
                                }
                            }
                            
//#line 547 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.count = 0;
                            
//#line 548 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = null;
                            
//#line 549 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52718 =
                              ((x10.util.concurrent.Lock)(lock));
                            
//#line 549 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52718.unlock();
                            
//#line 550 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52719 =
                              ((x10.lang.Place)((ref).home));
                            
//#line 550 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52720 =
                              t52719.
                                id;
                            
//#line 550 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(t52720)), closure);
                            
//#line 551 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                        }
                        
                        
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RemoteFinish
                                                                                                                     x10$lang$FinishState$RemoteFinish$$x10$lang$FinishState$RemoteFinish$this(
                                                                                                                     ){
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RemoteFinish.this;
                        }
                        
                        
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                     __fieldInitializers51937(
                                                                                                                     ){
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = null;
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.lock = new x10.util.concurrent.Lock();
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.count = 0;
                            
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52721 =
                              (x10.core.IndexedMemoryChunk<x10.core.Int>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, x10.rtt.Types.INT));
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.counts = t52721;
                            
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52722 =
                              (x10.core.IndexedMemoryChunk<x10.core.Int>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, x10.rtt.Types.INT));
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.places = t52722;
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.length = 1;
                            
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.local = ((x10.core.concurrent.AtomicInteger)(new x10.core.concurrent.AtomicInteger(((int)(0)))));
                        }
                        
                        @x10.core.X10Generated public static class $Closure$114 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$114.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$114> $RTT = x10.rtt.StaticVoidFunType.<$Closure$114> make(
                            /* base class */$Closure$114.class
                            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$114 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$114.class + " calling"); } 
                                x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj.ref = ref;
                                x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.message = message;
                                x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                                $_obj.t = t;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$114 $_obj = new $Closure$114((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                } else {
                                $serializer.write(this.ref);
                                }
                                if (message instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                } else {
                                $serializer.write(this.message);
                                }
                                if (t instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                                } else {
                                $serializer.write(this.t);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$114(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            
                                
                                public void
                                  $apply(
                                  ){
                                    
//#line 521 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52681 =
                                      x10.lang.FinishState.<x10.lang.FinishState.RootFinish>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.RootFinish.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                                             ref)));
                                    
//#line 521 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52681.notify__0$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                 message)),
                                                                                                                                                                ((x10.core.X10Throwable)(this.
                                                                                                                                                                                           t)));
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                public x10.core.IndexedMemoryChunk<x10.core.Int> message;
                                public x10.lang.MultipleExceptions t;
                                
                                public $Closure$114(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                    final x10.core.IndexedMemoryChunk<x10.core.Int> message,
                                                    final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2 $dummy) { {
                                                                                                                                                         this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                         this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                         this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                                                     }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2 {}
                                
                            }
                            
                        @x10.core.X10Generated public static class $Closure$115 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$115.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$115> $RTT = x10.rtt.StaticVoidFunType.<$Closure$115> make(
                            /* base class */$Closure$115.class
                            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$115 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$115.class + " calling"); } 
                                x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj.ref = ref;
                                x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.message = message;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$115 $_obj = new $Closure$115((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                } else {
                                $serializer.write(this.ref);
                                }
                                if (message instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                } else {
                                $serializer.write(this.message);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$115(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            
                                
                                public void
                                  $apply(
                                  ){
                                    
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52683 =
                                      x10.lang.FinishState.<x10.lang.FinishState.RootFinish>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.RootFinish.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                                             ref)));
                                    
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52683.notify__0$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                 message)));
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                public x10.core.IndexedMemoryChunk<x10.core.Int> message;
                                
                                public $Closure$115(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                    final x10.core.IndexedMemoryChunk<x10.core.Int> message, __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2 $dummy) { {
                                                                                                                                                                             this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                             this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                         }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2 {}
                                
                            }
                            
                        @x10.core.X10Generated public static class $Closure$116 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$116.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$116> $RTT = x10.rtt.StaticVoidFunType.<$Closure$116> make(
                            /* base class */$Closure$116.class
                            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$116 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$116.class + " calling"); } 
                                x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj.ref = ref;
                                x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.message = message;
                                x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                                $_obj.t = t;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$116 $_obj = new $Closure$116((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                } else {
                                $serializer.write(this.ref);
                                }
                                if (message instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                } else {
                                $serializer.write(this.message);
                                }
                                if (t instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                                } else {
                                $serializer.write(this.t);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$116(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            
                                
                                public void
                                  $apply(
                                  ){
                                    
//#line 531 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52700 =
                                      x10.lang.FinishState.<x10.lang.FinishState.RootFinish>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.RootFinish.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                                             ref)));
                                    
//#line 531 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52700.notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                message)),
                                                                                                                                                                                               ((x10.core.X10Throwable)(this.
                                                                                                                                                                                                                          t)));
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                public x10.lang.MultipleExceptions t;
                                
                                public $Closure$116(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                    final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message,
                                                    final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 $dummy) { {
                                                                                                                                                                                        this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                        this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                        this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                                                                                    }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 {}
                                
                            }
                            
                        @x10.core.X10Generated public static class $Closure$117 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$117.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$117> $RTT = x10.rtt.StaticVoidFunType.<$Closure$117> make(
                            /* base class */$Closure$117.class
                            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$117 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$117.class + " calling"); } 
                                x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj.ref = ref;
                                x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.message = message;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$117 $_obj = new $Closure$117((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                } else {
                                $serializer.write(this.ref);
                                }
                                if (message instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                } else {
                                $serializer.write(this.message);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$117(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            
                                
                                public void
                                  $apply(
                                  ){
                                    
//#line 533 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52702 =
                                      x10.lang.FinishState.<x10.lang.FinishState.RootFinish>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.RootFinish.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                                             ref)));
                                    
//#line 533 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52702.notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                message)));
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                
                                public $Closure$117(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                    final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 $dummy) { {
                                                                                                                                                                                                                                         this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                                                                         this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                                                                     }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 {}
                                
                            }
                            
                        @x10.core.X10Generated public static class $Closure$118 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$118.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$118> $RTT = x10.rtt.StaticVoidFunType.<$Closure$118> make(
                            /* base class */$Closure$118.class
                            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$118 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$118.class + " calling"); } 
                                x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj.ref = ref;
                                x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.message = message;
                                x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                                $_obj.t = t;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$118 $_obj = new $Closure$118((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                } else {
                                $serializer.write(this.ref);
                                }
                                if (message instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                } else {
                                $serializer.write(this.message);
                                }
                                if (t instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                                } else {
                                $serializer.write(this.t);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$118(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            
                                
                                public void
                                  $apply(
                                  ){
                                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52712 =
                                      x10.lang.FinishState.<x10.lang.FinishState.RootFinish>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.RootFinish.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                                             ref)));
                                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52712.notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                message)),
                                                                                                                                                                                               ((x10.core.X10Throwable)(this.
                                                                                                                                                                                                                          t)));
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                public x10.lang.MultipleExceptions t;
                                
                                public $Closure$118(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                    final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message,
                                                    final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 $dummy) { {
                                                                                                                                                                                        this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                        this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                        this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                                                                                    }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 {}
                                
                            }
                            
                        @x10.core.X10Generated public static class $Closure$119 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                        {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$119.class);
                            
                            public static final x10.rtt.RuntimeType<$Closure$119> $RTT = x10.rtt.StaticVoidFunType.<$Closure$119> make(
                            /* base class */$Closure$119.class
                            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$119 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$119.class + " calling"); } 
                                x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                $_obj.ref = ref;
                                x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.message = message;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                $Closure$119 $_obj = new $Closure$119((java.lang.System[]) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                } else {
                                $serializer.write(this.ref);
                                }
                                if (message instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                } else {
                                $serializer.write(this.message);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public $Closure$119(final java.lang.System[] $dummy) { 
                            super($dummy);
                            }
                            
                                
                                public void
                                  $apply(
                                  ){
                                    
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootFinish t52714 =
                                      x10.lang.FinishState.<x10.lang.FinishState.RootFinish>deref__0$1x10$lang$FinishState$2$G(x10.lang.FinishState.RootFinish.$RTT, ((x10.core.GlobalRef)(this.
                                                                                                                                                                                             ref)));
                                    
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52714.notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                message)));
                                }
                                
                                public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                
                                public $Closure$119(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                    final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 $dummy) { {
                                                                                                                                                                                                                                         this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                                                                         this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                                                                     }}
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 {}
                                
                            }
                            
                        // synthetic type for parameter mangling
                        public abstract static class __0$1x10$lang$FinishState$2 {}
                        
                        }
                        
                        
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class StatefulReducer<$T> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                                                   {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, StatefulReducer.class);
                            
                            public static final x10.rtt.RuntimeType<StatefulReducer> $RTT = x10.rtt.NamedType.<StatefulReducer> make(
                            "x10.lang.FinishState.StatefulReducer", /* base class */StatefulReducer.class, 
                            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body(StatefulReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + StatefulReducer.class + " calling"); } 
                                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                x10.lang.Reducible reducer = (x10.lang.Reducible) $deserializer.readRef();
                                $_obj.reducer = reducer;
                                $_obj.result = $deserializer.readRef();
                                x10.core.IndexedMemoryChunk resultRail = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.resultRail = resultRail;
                                x10.core.IndexedMemoryChunk workerFlag = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                $_obj.workerFlag = workerFlag;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                StatefulReducer $_obj = new StatefulReducer((java.lang.System[]) null, (x10.rtt.Type) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                if (reducer instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.reducer);
                                } else {
                                $serializer.write(this.reducer);
                                }
                                if (result instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.result);
                                } else {
                                $serializer.write(this.result);
                                }
                                if (resultRail instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.resultRail);
                                } else {
                                $serializer.write(this.resultRail);
                                }
                                if (workerFlag instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.workerFlag);
                                } else {
                                $serializer.write(this.workerFlag);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public StatefulReducer(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                            super($dummy);
                            x10.lang.FinishState.StatefulReducer.$initParams(this, $T);
                            }
                            
                                private x10.rtt.Type $T;
                                // initializer of type parameters
                                public static void $initParams(final StatefulReducer $this, final x10.rtt.Type $T) {
                                $this.$T = $T;
                                }
                                
                                
//#line 556 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.lang.Reducible<$T> reducer;
                                
//#line 557 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public $T result;
                                
//#line 558 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.IndexedMemoryChunk<$T> resultRail;
                                
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.core.IndexedMemoryChunk<x10.core.Boolean> workerFlag;
                                
                                
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                                public StatefulReducer(final x10.rtt.Type $T,
                                                       final x10.lang.Reducible<$T> r, __0$1x10$lang$FinishState$StatefulReducer$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                                                  $init(r, (x10.lang.FinishState.StatefulReducer.__0$1x10$lang$FinishState$StatefulReducer$$T$2) null);}
                                
                                // constructor for non-virtual call
                                final public x10.lang.FinishState.StatefulReducer<$T> x10$lang$FinishState$StatefulReducer$$init$S(final x10.lang.Reducible<$T> r, __0$1x10$lang$FinishState$StatefulReducer$$T$2 $dummy) { {
                                                                                                                                                                                                                                   
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                                   
//#line 560 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                                   
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.__fieldInitializers51938();
                                                                                                                                                                                                                                   
//#line 561 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.reducer = ((x10.lang.Reducible)(r));
                                                                                                                                                                                                                                   
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> t52723 =
                                                                                                                                                                                                                                     ((x10.lang.Reducible)(reducer));
                                                                                                                                                                                                                                   
//#line 562 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T zero =
                                                                                                                                                                                                                                     (($T)(((x10.lang.Reducible<$T>)t52723).zero$G()));
                                                                                                                                                                                                                                   
//#line 563 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.result = (($T)(zero));
                                                                                                                                                                                                                                   
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52724 =
                                                                                                                                                                                                                                     x10.lang.Runtime.MAX_THREADS;
                                                                                                                                                                                                                                   
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t52725 =
                                                                                                                                                                                                                                     x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t52724)), false);
                                                                                                                                                                                                                                   
//#line 564 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.resultRail = ((x10.core.IndexedMemoryChunk)(t52725));
                                                                                                                                                                                                                                   
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i52195min53016 =
                                                                                                                                                                                                                                     0;
                                                                                                                                                                                                                                   
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t53017 =
                                                                                                                                                                                                                                     ((x10.core.IndexedMemoryChunk)(resultRail));
                                                                                                                                                                                                                                   
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53018 =
                                                                                                                                                                                                                                     ((((x10.core.IndexedMemoryChunk<$T>)(t53017))).length);
                                                                                                                                                                                                                                   
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i52195max53019 =
                                                                                                                                                                                                                                     ((t53018) - (((int)(1))));
                                                                                                                                                                                                                                   
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i53013 =
                                                                                                                                                                                                                                     i52195min53016;
                                                                                                                                                                                                                                   
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                                                                                                                                                                                                                   true;
                                                                                                                                                                                                                                                                                                                                   ) {
                                                                                                                                                                                                                                       
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53014 =
                                                                                                                                                                                                                                         i53013;
                                                                                                                                                                                                                                       
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t53015 =
                                                                                                                                                                                                                                         ((t53014) <= (((int)(i52195max53019))));
                                                                                                                                                                                                                                       
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t53015)) {
                                                                                                                                                                                                                                           
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                                                                                                                                                                                                                       }
                                                                                                                                                                                                                                       
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i53010 =
                                                                                                                                                                                                                                         i53013;
                                                                                                                                                                                                                                       
//#line 566 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t53009 =
                                                                                                                                                                                                                                         ((x10.core.IndexedMemoryChunk)(resultRail));
                                                                                                                                                                                                                                       
//#line 566 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t53009))).$set(((int)(i53010)), zero);
                                                                                                                                                                                                                                       
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53011 =
                                                                                                                                                                                                                                         i53013;
                                                                                                                                                                                                                                       
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53012 =
                                                                                                                                                                                                                                         ((t53011) + (((int)(1))));
                                                                                                                                                                                                                                       
//#line 565 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i53013 = t53012;
                                                                                                                                                                                                                                   }
                                                                                                                                                                                                                               }
                                                                                                                                                                                                                               return this;
                                                                                                                                                                                                                               }
                                
                                // constructor
                                public x10.lang.FinishState.StatefulReducer<$T> $init(final x10.lang.Reducible<$T> r, __0$1x10$lang$FinishState$StatefulReducer$$T$2 $dummy){return x10$lang$FinishState$StatefulReducer$$init$S(r, $dummy);}
                                
                                
                                
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             accept__0x10$lang$FinishState$StatefulReducer$$T(
                                                                                                                             final $T t){
                                    
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> t52734 =
                                      ((x10.lang.Reducible)(reducer));
                                    
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52735 =
                                      (($T)(result));
                                    
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52736 =
                                      (($T)((($T)
                                              ((x10.lang.Reducible<$T>)t52734).$apply((($T)(t52735)),$T,
                                                                                      (($T)(t)),$T))));
                                    
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.result = (($T)(t52736));
                                }
                                
                                
//#line 572 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             accept__0x10$lang$FinishState$StatefulReducer$$T(
                                                                                                                             final $T t,
                                                                                                                             final int id){
                                    
//#line 573 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
boolean t52738 =
                                      ((id) >= (((int)(0))));
                                    
//#line 573 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52738) {
                                        
//#line 573 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52737 =
                                          x10.lang.Runtime.MAX_THREADS;
                                        
//#line 573 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52738 = ((id) < (((int)(t52737))));
                                    }
                                    
//#line 573 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52745 =
                                      t52738;
                                    
//#line 573 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52745) {
                                        
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t52742 =
                                          ((x10.core.IndexedMemoryChunk)(resultRail));
                                        
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> t52740 =
                                          ((x10.lang.Reducible)(reducer));
                                        
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t52739 =
                                          ((x10.core.IndexedMemoryChunk)(resultRail));
                                        
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52741 =
                                          (($T)((((x10.core.IndexedMemoryChunk<$T>)(t52739))).$apply$G(((int)(id)))));
                                        
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52743 =
                                          (($T)((($T)
                                                  ((x10.lang.Reducible<$T>)t52740).$apply((($T)(t52741)),$T,
                                                                                          (($T)(t)),$T))));
                                        
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t52742))).$set(((int)(id)), t52743);
                                        
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t52744 =
                                          ((x10.core.IndexedMemoryChunk)(workerFlag));
                                        
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((boolean[])t52744.value)[id] = true;
                                    }
                                }
                                
                                
//#line 578 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             placeMerge(
                                                                                                                             ){
                                    
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i =
                                      0;
                                    
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                    true;
                                                                                                                                    ) {
                                        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52747 =
                                          i;
                                        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52748 =
                                          x10.lang.Runtime.MAX_THREADS;
                                        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52764 =
                                          ((t52747) < (((int)(t52748))));
                                        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t52764)) {
                                            
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                        }
                                        
//#line 580 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t53020 =
                                          ((x10.core.IndexedMemoryChunk)(workerFlag));
                                        
//#line 580 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53021 =
                                          i;
                                        
//#line 580 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t53022 =
                                          ((boolean[])t53020.value)[t53021];
                                        
//#line 580 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t53022) {
                                            
//#line 581 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> t53023 =
                                              ((x10.lang.Reducible)(reducer));
                                            
//#line 581 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t53024 =
                                              (($T)(result));
                                            
//#line 581 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t53025 =
                                              ((x10.core.IndexedMemoryChunk)(resultRail));
                                            
//#line 581 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53026 =
                                              i;
                                            
//#line 581 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t53027 =
                                              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t53025))).$apply$G(((int)(t53026)))));
                                            
//#line 581 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t53028 =
                                              (($T)((($T)
                                                      ((x10.lang.Reducible<$T>)t53023).$apply((($T)(t53024)),$T,
                                                                                              (($T)(t53027)),$T))));
                                            
//#line 581 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.result = (($T)(t53028));
                                            
//#line 582 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t53029 =
                                              ((x10.core.IndexedMemoryChunk)(resultRail));
                                            
//#line 582 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53030 =
                                              i;
                                            
//#line 582 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> t53031 =
                                              ((x10.lang.Reducible)(reducer));
                                            
//#line 582 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t53032 =
                                              (($T)(((x10.lang.Reducible<$T>)t53031).zero$G()));
                                            
//#line 582 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t53029))).$set(((int)(t53030)), t53032);
                                        }
                                        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53033 =
                                          i;
                                        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53034 =
                                          ((t53033) + (((int)(1))));
                                        
//#line 579 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i = t53034;
                                    }
                                }
                                
                                
//#line 586 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public $T
                                                                                                                             result$G(
                                                                                                                             ){
                                    
//#line 586 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52765 =
                                      (($T)(result));
                                    
//#line 586 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52765;
                                }
                                
                                
//#line 587 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             reset(
                                                                                                                             ){
                                    
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> t52766 =
                                      ((x10.lang.Reducible)(reducer));
                                    
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52767 =
                                      (($T)(((x10.lang.Reducible<$T>)t52766).zero$G()));
                                    
//#line 588 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.result = (($T)(t52767));
                                }
                                
                                
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.StatefulReducer<$T>
                                                                                                                             x10$lang$FinishState$StatefulReducer$$x10$lang$FinishState$StatefulReducer$this(
                                                                                                                             ){
                                    
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.StatefulReducer.this;
                                }
                                
                                
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public void
                                                                                                                             __fieldInitializers51938(
                                                                                                                             ){
                                    
//#line 558 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<$T> t52768 =
                                      (x10.core.IndexedMemoryChunk<$T>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T));
                                    
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.resultRail = t52768;
                                    
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52769 =
                                      x10.lang.Runtime.MAX_THREADS;
                                    
//#line 559 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Boolean> t52770 =
                                      ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Boolean>allocate(x10.rtt.Types.BOOLEAN, ((int)(t52769)), true)));
                                    
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.workerFlag = t52770;
                                }
                            // synthetic type for parameter mangling
                            public abstract static class __0$1x10$lang$FinishState$StatefulReducer$$T$2 {}
                            
                        }
                        
                        
//#line 592 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static interface CollectingFinishState<$T> extends x10.core.Any
                                                                                                                   {
                            public static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, CollectingFinishState.class);
                            
                            public static final x10.rtt.RuntimeType<CollectingFinishState> $RTT = x10.rtt.NamedType.<CollectingFinishState> make(
                            "x10.lang.FinishState.CollectingFinishState", /* base class */CollectingFinishState.class, 
                            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                            , /* parents */ new x10.rtt.Type[] {}
                            );
                            
                                
                                
                                
//#line 593 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
java.lang.Object
                                                                                                                             accept(
                                                                                                                             final java.lang.Object t,x10.rtt.Type t1,
                                                                                                                             final int id);
                            
                        }
                        
                        
//#line 596 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class CollectingFinish<$T> extends x10.lang.FinishState.Finish implements x10.lang.FinishState.CollectingFinishState, x10.io.CustomSerialization
                                                                                                                   {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, CollectingFinish.class);
                            
                            public static final x10.rtt.RuntimeType<CollectingFinish> $RTT = x10.rtt.NamedType.<CollectingFinish> make(
                            "x10.lang.FinishState.CollectingFinish", /* base class */CollectingFinish.class, 
                            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.FinishState.CollectingFinishState.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.io.CustomSerialization.$RTT, x10.lang.FinishState.Finish.$RTT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                            // custom serializer
                            private transient x10.io.SerialData $$serialdata;
                            private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
                            private Object readResolve() { return new CollectingFinish($T, $$serialdata); }
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
                            oos.writeObject($T);
                            oos.writeObject($$serialdata); }
                            private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
                            $T = (x10.rtt.Type) ois.readObject();
                            $$serialdata = (x10.io.SerialData) ois.readObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body(CollectingFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + CollectingFinish.class + " calling"); } 
                                x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
                                x10.rtt.Type $T = ( x10.rtt.Type ) $deserializer.readRef();
                                $_obj.$T = $T;
                                $_obj.$init($$serialdata);
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                CollectingFinish $_obj = new CollectingFinish((java.lang.System[]) null, (x10.rtt.Type) null);
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
                            
                            // constructor just for allocation
                            public CollectingFinish(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                            super($dummy);
                            x10.lang.FinishState.CollectingFinish.$initParams(this, $T);
                            }
                            // dispatcher for method abstract public x10.lang.FinishState.CollectingFinishState.accept(t:T,id:x10.lang.Int):void
                            public java.lang.Object accept(final java.lang.Object a1, final x10.rtt.Type t1, final int a2) {
                            accept__0x10$lang$FinishState$CollectingFinish$$T(($T)a1, a2);return null;
                            }
                            
                                private x10.rtt.Type $T;
                                // initializer of type parameters
                                public static void $initParams(final CollectingFinish $this, final x10.rtt.Type $T) {
                                $this.$T = $T;
                                }
                                
                                
//#line 597 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.lang.Reducible<$T> reducer;
                                
                                
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                                public CollectingFinish(final x10.rtt.Type $T,
                                                        final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$CollectingFinish$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                                                          $init(reducer, (x10.lang.FinishState.CollectingFinish.__0$1x10$lang$FinishState$CollectingFinish$$T$2) null);}
                                
                                // constructor for non-virtual call
                                final public x10.lang.FinishState.CollectingFinish<$T> x10$lang$FinishState$CollectingFinish$$init$S(final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$CollectingFinish$$T$2 $dummy) { {
                                                                                                                                                                                                                                            
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t53035 =
                                                                                                                                                                                                                                              ((x10.lang.FinishState.RootCollectingFinish)(new x10.lang.FinishState.RootCollectingFinish<$T>((java.lang.System[]) null, $T).$init(((x10.lang.Reducible<$T>)(reducer)), (x10.lang.FinishState.RootCollectingFinish.__0$1x10$lang$FinishState$RootCollectingFinish$$T$2) null)));
                                                                                                                                                                                                                                            
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.lang.FinishState.RootFinish)(t53035)));
                                                                                                                                                                                                                                            
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                                            
//#line 600 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.reducer = ((x10.lang.Reducible)(reducer));
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                        return this;
                                                                                                                                                                                                                                        }
                                
                                // constructor
                                public x10.lang.FinishState.CollectingFinish<$T> $init(final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$CollectingFinish$$T$2 $dummy){return x10$lang$FinishState$CollectingFinish$$init$S(reducer, $dummy);}
                                
                                
                                
//#line 602 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                                public CollectingFinish(final x10.rtt.Type $T,
                                                        final x10.io.SerialData data){this((java.lang.System[]) null, $T);
                                                                                          $init(data);}
                                
                                // constructor for non-virtual call
                                final public x10.lang.FinishState.CollectingFinish<$T> x10$lang$FinishState$CollectingFinish$$init$S(final x10.io.SerialData data) {x10$lang$FinishState$CollectingFinish$init_for_reflection(data);
                                                                                                                                                                        
                                                                                                                                                                        return this;
                                                                                                                                                                        }
                                public void x10$lang$FinishState$CollectingFinish$init_for_reflection(x10.io.SerialData data) {
                                     {
                                        
//#line 603 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.io.SerialData t53041 =
                                          ((x10.io.SerialData)(data.
                                                                 superclassData));
                                        
//#line 603 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final java.lang.Object t53042 =
                                          ((java.lang.Object)(t53041.
                                                                data));
                                        
//#line 603 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t53043 =
                                          ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t53042))));
                                        
//#line 603 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(t53043)), (x10.lang.FinishState.Finish.__0$1x10$lang$FinishState$2) null);
                                        
//#line 602 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                        
//#line 604 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final java.lang.Object t52775 =
                                          ((java.lang.Object)(data.
                                                                data));
                                        
//#line 604 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> tmpReducer =
                                          ((x10.lang.Reducible)(x10.rtt.Types.<x10.lang.Reducible<$T>> cast(t52775,x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, $T))));
                                        
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.reducer = ((x10.lang.Reducible)(tmpReducer));
                                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52776 =
                                          ((x10.core.GlobalRef)(ref));
                                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52777 =
                                          ((x10.lang.Place)((t52776).home));
                                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52778 =
                                          t52777.
                                            id;
                                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52779 =
                                          x10.lang.Runtime.hereInt$O();
                                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52793 =
                                          ((int) t52778) ==
                                        ((int) t52779);
                                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52793) {
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52780 =
                                              ((x10.core.GlobalRef)(ref));
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> __desugarer__var__51__52285 =
                                              ((x10.core.GlobalRef)(((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.lang.FinishState.$RTT),t52780))));
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.GlobalRef<x10.lang.FinishState> ret52286 =
                                               null;
                                            
//#line 607 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t53036 =
                                              ((x10.lang.Place)((__desugarer__var__51__52285).home));
                                            
//#line 607 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t53037 =
                                              x10.rtt.Equality.equalsequals((t53036),(x10.lang.Runtime.home()));
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t53038 =
                                              !(t53037);
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t53038) {
                                                
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t53039 =
                                                  true;
                                                
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t53039) {
                                                    
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FailedDynamicCheckException t53040 =
                                                      new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.lang.FinishState]");
                                                    
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
throw t53040;
                                                }
                                            }
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
ret52286 = ((x10.core.GlobalRef)(__desugarer__var__51__52285));
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52786 =
                                              ((x10.core.GlobalRef)(ret52286));
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52787 =
                                              (((x10.core.GlobalRef<x10.lang.FinishState>)(t52786))).$apply$G();
                                            
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52787));
                                        } else {
                                            
//#line 609 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> _ref =
                                              ((x10.core.GlobalRef)(ref));
                                            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.FinishStates t52789 =
                                              ((x10.lang.FinishState.FinishStates)(x10.lang.Runtime.finishStates));
                                            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> t52790 =
                                              ((x10.core.GlobalRef)(ref));
                                            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.Fun_0_0<x10.lang.FinishState.RemoteCollectingFinish<$T>> t52791 =
                                              ((x10.core.fun.Fun_0_0)(new x10.lang.FinishState.CollectingFinish.$Closure$120<$T>($T, _ref,
                                                                                                                                 tmpReducer, (x10.lang.FinishState.CollectingFinish.$Closure$120.__0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$CollectingFinish$$Closure$120$$T$2) null)));
                                            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52792 =
                                              t52789.$apply__0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$2(((x10.core.GlobalRef)(t52790)),
                                                                                                                  ((x10.core.fun.Fun_0_0)(t52791)));
                                            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.me = ((x10.lang.FinishState)(t52792));
                                        }
                                    }}
                                    
                                // constructor
                                public x10.lang.FinishState.CollectingFinish<$T> $init(final x10.io.SerialData data){return x10$lang$FinishState$CollectingFinish$$init$S(data);}
                                
                                
                                
//#line 613 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.io.SerialData
                                                                                                                             serialize(
                                                                                                                             ){
                                    
//#line 613 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Reducible<$T> t52794 =
                                      ((x10.lang.Reducible)(reducer));
                                    
//#line 613 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.io.SerialData t52795 =
                                      super.serialize();
                                    
//#line 613 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.io.SerialData t52796 =
                                      ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).$init(((java.lang.Object)(t52794)),
                                                                                                                  t52795)));
                                    
//#line 613 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52796;
                                }
                                
                                
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             accept__0x10$lang$FinishState$CollectingFinish$$T(
                                                                                                                             final $T t,
                                                                                                                             final int id){
                                    
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52797 =
                                      ((x10.lang.FinishState)(me));
                                    
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.CollectingFinishState<$T> t52798 =
                                      x10.rtt.Types.<x10.lang.FinishState.CollectingFinishState<$T>> cast(t52797,x10.rtt.ParameterizedType.make(x10.lang.FinishState.CollectingFinishState.$RTT, $T));
                                    
//#line 614 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.CollectingFinishState<$T>)t52798).accept((($T)(t)),$T,
                                                                                                                                                                                               (int)(id));
                                }
                                
                                
//#line 615 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public $T
                                                                                                                             waitForFinishExpr$G(
                                                                                                                             ){
                                    
//#line 615 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState t52799 =
                                      ((x10.lang.FinishState)(me));
                                    
//#line 615 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t52800 =
                                      x10.rtt.Types.<x10.lang.FinishState.RootCollectingFinish<$T>> cast(t52799,x10.rtt.ParameterizedType.make(x10.lang.FinishState.RootCollectingFinish.$RTT, $T));
                                    
//#line 615 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T t52801 =
                                      (($T)(((x10.lang.FinishState.RootCollectingFinish<$T>)t52800).waitForFinishExpr$G()));
                                    
//#line 615 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52801;
                                }
                                
                                
//#line 596 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.CollectingFinish<$T>
                                                                                                                             x10$lang$FinishState$CollectingFinish$$x10$lang$FinishState$CollectingFinish$this(
                                                                                                                             ){
                                    
//#line 596 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.CollectingFinish.this;
                                }
                                
                                @x10.core.X10Generated public static class $Closure$120<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$120.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$120> $RTT = x10.rtt.StaticFunType.<$Closure$120> make(
                                    /* base class */$Closure$120.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.lang.FinishState.RemoteCollectingFinish.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$120 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$120.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef _ref = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj._ref = _ref;
                                        x10.lang.Reducible tmpReducer = (x10.lang.Reducible) $deserializer.readRef();
                                        $_obj.tmpReducer = tmpReducer;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$120 $_obj = new $Closure$120((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (_ref instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this._ref);
                                        } else {
                                        $serializer.write(this._ref);
                                        }
                                        if (tmpReducer instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.tmpReducer);
                                        } else {
                                        $serializer.write(this.tmpReducer);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$120(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.FinishState.CollectingFinish.$Closure$120.$initParams(this, $T);
                                    }
                                    // bridge for method abstract public ()=>U.operator()():U
                                    public x10.lang.FinishState.RemoteCollectingFinish
                                      $apply$G(){return $apply();}
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$120 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public x10.lang.FinishState.RemoteCollectingFinish<$T>
                                          $apply(
                                          ){
                                            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteCollectingFinish<$T> t52788 =
                                              ((x10.lang.FinishState.RemoteCollectingFinish)(new x10.lang.FinishState.RemoteCollectingFinish<$T>((java.lang.System[]) null, $T).$init(((x10.core.GlobalRef<x10.lang.FinishState>)(this.
                                                                                                                                                                                                                                    _ref)),
                                                                                                                                                                                      ((x10.lang.Reducible<$T>)(this.
                                                                                                                                                                                                                  tmpReducer)), (x10.lang.FinishState.RemoteCollectingFinish.__0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$RemoteCollectingFinish$$T$2) null)));
                                            
//#line 610 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return t52788;
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.FinishState> _ref;
                                        public x10.lang.Reducible<$T> tmpReducer;
                                        
                                        public $Closure$120(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.FinishState> _ref,
                                                            final x10.lang.Reducible<$T> tmpReducer, __0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$CollectingFinish$$Closure$120$$T$2 $dummy) {x10.lang.FinishState.CollectingFinish.$Closure$120.$initParams(this, $T);
                                                                                                                                                                                                           {
                                                                                                                                                                                                              this._ref = ((x10.core.GlobalRef)(_ref));
                                                                                                                                                                                                              this.tmpReducer = ((x10.lang.Reducible)(tmpReducer));
                                                                                                                                                                                                          }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$CollectingFinish$$Closure$120$$T$2 {}
                                        
                                    }
                                    
                                
                                public x10.io.SerialData
                                  x10$lang$FinishState$FinishSkeleton$serialize$S(
                                  ){
                                    return super.serialize();
                                }
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$CollectingFinish$$T$2 {}
                                
                            }
                            
                        
//#line 618 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RootCollectingFinish<$T> extends x10.lang.FinishState.RootFinish implements x10.lang.FinishState.CollectingFinishState, x10.x10rt.X10JavaSerializable
                                                                                                                   {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RootCollectingFinish.class);
                            
                            public static final x10.rtt.RuntimeType<RootCollectingFinish> $RTT = x10.rtt.NamedType.<RootCollectingFinish> make(
                            "x10.lang.FinishState.RootCollectingFinish", /* base class */RootCollectingFinish.class, 
                            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.FinishState.CollectingFinishState.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.lang.FinishState.RootFinish.$RTT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body(RootCollectingFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RootCollectingFinish.class + " calling"); } 
                                x10.lang.FinishState.RootFinish.$_deserialize_body($_obj, $deserializer);
                                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                x10.lang.FinishState.StatefulReducer sr = (x10.lang.FinishState.StatefulReducer) $deserializer.readRef();
                                $_obj.sr = sr;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                RootCollectingFinish $_obj = new RootCollectingFinish((java.lang.System[]) null, (x10.rtt.Type) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                super.$_serialize($serializer);
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                if (sr instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.sr);
                                } else {
                                $serializer.write(this.sr);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public RootCollectingFinish(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                            super($dummy);
                            x10.lang.FinishState.RootCollectingFinish.$initParams(this, $T);
                            }
                            // dispatcher for method abstract public x10.lang.FinishState.CollectingFinishState.accept(t:T,id:x10.lang.Int):void
                            public java.lang.Object accept(final java.lang.Object a1, final x10.rtt.Type t1, final int a2) {
                            accept__0x10$lang$FinishState$RootCollectingFinish$$T(($T)a1, a2);return null;
                            }
                            
                                private x10.rtt.Type $T;
                                // initializer of type parameters
                                public static void $initParams(final RootCollectingFinish $this, final x10.rtt.Type $T) {
                                $this.$T = $T;
                                }
                                
                                
//#line 619 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.lang.FinishState.StatefulReducer<$T> sr;
                                
                                
//#line 620 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                                public RootCollectingFinish(final x10.rtt.Type $T,
                                                            final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$RootCollectingFinish$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                                                                  $init(reducer, (x10.lang.FinishState.RootCollectingFinish.__0$1x10$lang$FinishState$RootCollectingFinish$$T$2) null);}
                                
                                // constructor for non-virtual call
                                final public x10.lang.FinishState.RootCollectingFinish<$T> x10$lang$FinishState$RootCollectingFinish$$init$S(final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$RootCollectingFinish$$T$2 $dummy) { {
                                                                                                                                                                                                                                                        
//#line 621 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init();
                                                                                                                                                                                                                                                        
//#line 620 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                                                        
//#line 622 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52802 =
                                                                                                                                                                                                                                                          ((x10.lang.FinishState.StatefulReducer)(new x10.lang.FinishState.StatefulReducer<$T>((java.lang.System[]) null, $T).$init(((x10.lang.Reducible<$T>)(reducer)), (x10.lang.FinishState.StatefulReducer.__0$1x10$lang$FinishState$StatefulReducer$$T$2) null)));
                                                                                                                                                                                                                                                        
//#line 622 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.sr = ((x10.lang.FinishState.StatefulReducer)(t52802));
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                    return this;
                                                                                                                                                                                                                                                    }
                                
                                // constructor
                                public x10.lang.FinishState.RootCollectingFinish<$T> $init(final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$RootCollectingFinish$$T$2 $dummy){return x10$lang$FinishState$RootCollectingFinish$$init$S(reducer, $dummy);}
                                
                                
                                
//#line 624 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             accept__0x10$lang$FinishState$RootCollectingFinish$$T(
                                                                                                                             final $T t,
                                                                                                                             final int id){
                                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52803 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52803).accept__0x10$lang$FinishState$StatefulReducer$$T((($T)(t)),
                                                                                                                                                                                                                                   (int)(id));
                                }
                                
                                
//#line 627 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             notifyValue__0$1x10$lang$Int$2__1x10$lang$FinishState$RootCollectingFinish$$T(
                                                                                                                             final x10.core.IndexedMemoryChunk rail,
                                                                                                                             final $T v){
                                    
//#line 628 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52804 =
                                      ((x10.util.concurrent.SimpleLatch)(latch));
                                    
//#line 628 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52804.lock();
                                    
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52805 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52805).accept__0x10$lang$FinishState$StatefulReducer$$T((($T)(v)));
                                    
//#line 630 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process__0$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(rail)));
                                    
//#line 631 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52806 =
                                      ((x10.util.concurrent.SimpleLatch)(latch));
                                    
//#line 631 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52806.unlock();
                                }
                                
                                
//#line 633 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             notifyValue__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__1x10$lang$FinishState$RootCollectingFinish$$T(
                                                                                                                             final x10.core.IndexedMemoryChunk rail,
                                                                                                                             final $T v){
                                    
//#line 634 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52807 =
                                      ((x10.util.concurrent.SimpleLatch)(latch));
                                    
//#line 634 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52807.lock();
                                    
//#line 635 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52808 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 635 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52808).accept__0x10$lang$FinishState$StatefulReducer$$T((($T)(v)));
                                    
//#line 636 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.process__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(rail)));
                                    
//#line 637 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.SimpleLatch t52809 =
                                      ((x10.util.concurrent.SimpleLatch)(latch));
                                    
//#line 637 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52809.unlock();
                                }
                                
                                
//#line 639 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public $T
                                                                                                                             waitForFinishExpr$G(
                                                                                                                             ){
                                    
//#line 640 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.waitForFinish();
                                    
//#line 641 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52810 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 641 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52810).placeMerge();
                                    
//#line 642 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52811 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 642 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T result =
                                      (($T)(((x10.lang.FinishState.StatefulReducer<$T>)t52811).result$G()));
                                    
//#line 643 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52812 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 643 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52812).reset();
                                    
//#line 644 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return result;
                                }
                                
                                
//#line 618 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RootCollectingFinish<$T>
                                                                                                                             x10$lang$FinishState$RootCollectingFinish$$x10$lang$FinishState$RootCollectingFinish$this(
                                                                                                                             ){
                                    
//#line 618 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RootCollectingFinish.this;
                                }
                            // synthetic type for parameter mangling
                            public abstract static class __0$1x10$lang$FinishState$RootCollectingFinish$$T$2 {}
                            
                        }
                        
                        
//#line 648 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
@x10.core.X10Generated public static class RemoteCollectingFinish<$T> extends x10.lang.FinishState.RemoteFinish implements x10.lang.FinishState.CollectingFinishState, x10.x10rt.X10JavaSerializable
                                                                                                                   {
                            private static final long serialVersionUID = 1L;
                            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteCollectingFinish.class);
                            
                            public static final x10.rtt.RuntimeType<RemoteCollectingFinish> $RTT = x10.rtt.NamedType.<RemoteCollectingFinish> make(
                            "x10.lang.FinishState.RemoteCollectingFinish", /* base class */RemoteCollectingFinish.class, 
                            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.FinishState.CollectingFinishState.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.lang.FinishState.RemoteFinish.$RTT}
                            );
                            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                            
                            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                            public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteCollectingFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RemoteCollectingFinish.class + " calling"); } 
                                x10.lang.FinishState.RemoteFinish.$_deserialize_body($_obj, $deserializer);
                                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                x10.lang.FinishState.StatefulReducer sr = (x10.lang.FinishState.StatefulReducer) $deserializer.readRef();
                                $_obj.sr = sr;
                                return $_obj;
                                
                            }
                            
                            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                            
                                RemoteCollectingFinish $_obj = new RemoteCollectingFinish((java.lang.System[]) null, (x10.rtt.Type) null);
                                $deserializer.record_reference($_obj);
                                return $_deserialize_body($_obj, $deserializer);
                                
                            }
                            
                            public short $_get_serialization_id() {
                            
                                 return $_serialization_id;
                                
                            }
                            
                            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                            
                                super.$_serialize($serializer);
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                if (sr instanceof x10.x10rt.X10JavaSerializable) {
                                $serializer.write( (x10.x10rt.X10JavaSerializable) this.sr);
                                } else {
                                $serializer.write(this.sr);
                                }
                                
                            }
                            
                            // constructor just for allocation
                            public RemoteCollectingFinish(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                            super($dummy);
                            x10.lang.FinishState.RemoteCollectingFinish.$initParams(this, $T);
                            }
                            // dispatcher for method abstract public x10.lang.FinishState.CollectingFinishState.accept(t:T,id:x10.lang.Int):void
                            public java.lang.Object accept(final java.lang.Object a1, final x10.rtt.Type t1, final int a2) {
                            accept__0x10$lang$FinishState$RemoteCollectingFinish$$T(($T)a1, a2);return null;
                            }
                            
                                private x10.rtt.Type $T;
                                // initializer of type parameters
                                public static void $initParams(final RemoteCollectingFinish $this, final x10.rtt.Type $T) {
                                $this.$T = $T;
                                }
                                
                                
//#line 649 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public x10.lang.FinishState.StatefulReducer<$T> sr;
                                
                                
//#line 650 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
// creation method for java code (1-phase java constructor)
                                public RemoteCollectingFinish(final x10.rtt.Type $T,
                                                              final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                              final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$RemoteCollectingFinish$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                                                                                                 $init(ref,reducer, (x10.lang.FinishState.RemoteCollectingFinish.__0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$RemoteCollectingFinish$$T$2) null);}
                                
                                // constructor for non-virtual call
                                final public x10.lang.FinishState.RemoteCollectingFinish<$T> x10$lang$FinishState$RemoteCollectingFinish$$init$S(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                                                                                                                 final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$RemoteCollectingFinish$$T$2 $dummy) { {
                                                                                                                                                                                                                                                                                         
//#line 651 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
super.$init(((x10.core.GlobalRef<x10.lang.FinishState>)(ref)), (x10.lang.FinishState.RemoteFinish.__0$1x10$lang$FinishState$2) null);
                                                                                                                                                                                                                                                                                         
//#line 650 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                                                                                                                                                                                                         
//#line 652 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52813 =
                                                                                                                                                                                                                                                                                           ((x10.lang.FinishState.StatefulReducer)(new x10.lang.FinishState.StatefulReducer<$T>((java.lang.System[]) null, $T).$init(((x10.lang.Reducible<$T>)(reducer)), (x10.lang.FinishState.StatefulReducer.__0$1x10$lang$FinishState$StatefulReducer$$T$2) null)));
                                                                                                                                                                                                                                                                                         
//#line 652 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.sr = ((x10.lang.FinishState.StatefulReducer)(t52813));
                                                                                                                                                                                                                                                                                     }
                                                                                                                                                                                                                                                                                     return this;
                                                                                                                                                                                                                                                                                     }
                                
                                // constructor
                                public x10.lang.FinishState.RemoteCollectingFinish<$T> $init(final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                                                             final x10.lang.Reducible<$T> reducer, __0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$RemoteCollectingFinish$$T$2 $dummy){return x10$lang$FinishState$RemoteCollectingFinish$$init$S(ref,reducer, $dummy);}
                                
                                
                                
//#line 654 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             accept__0x10$lang$FinishState$RemoteCollectingFinish$$T(
                                                                                                                             final $T t,
                                                                                                                             final int id){
                                    
//#line 655 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52814 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 655 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52814).accept__0x10$lang$FinishState$StatefulReducer$$T((($T)(t)),
                                                                                                                                                                                                                                   (int)(id));
                                }
                                
                                
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
public void
                                                                                                                             notifyActivityTermination(
                                                                                                                             ){
                                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52815 =
                                      ((x10.util.concurrent.Lock)(lock));
                                    
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52815.lock();
                                    
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RemoteCollectingFinish<$T> x52288 =
                                      ((x10.lang.FinishState.RemoteCollectingFinish)(this));
                                    
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
;
                                    
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52816 =
                                      x52288.
                                        count;
                                    
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52817 =
                                      ((t52816) - (((int)(1))));
                                    
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x52288.count = t52817;
                                    
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.concurrent.AtomicInteger t52818 =
                                      ((x10.core.concurrent.AtomicInteger)(local));
                                    
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52819 =
                                      t52818.decrementAndGet();
                                    
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52821 =
                                      ((t52819) > (((int)(0))));
                                    
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52821) {
                                        
//#line 661 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52820 =
                                          ((x10.util.concurrent.Lock)(lock));
                                        
//#line 661 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52820.unlock();
                                        
//#line 662 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return;
                                    }
                                    
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Stack<x10.core.X10Throwable> t52822 =
                                      ((x10.util.Stack)(exceptions));
                                    
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.MultipleExceptions t =
                                      x10.lang.MultipleExceptions.make__0$1x10$lang$Throwable$2(((x10.util.Stack)(t52822)));
                                    
//#line 665 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.GlobalRef<x10.lang.FinishState> ref =
                                      ((x10.core.GlobalRef)(this.ref()));
                                    
//#line 666 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 closure;
                                    
//#line 667 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52823 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 667 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52823).placeMerge();
                                    
//#line 668 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52824 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 668 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final $T result =
                                      (($T)(((x10.lang.FinishState.StatefulReducer<$T>)t52824).result$G()));
                                    
//#line 669 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.StatefulReducer<$T> t52825 =
                                      ((x10.lang.FinishState.StatefulReducer)(sr));
                                    
//#line 669 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.StatefulReducer<$T>)t52825).reset();
                                    
//#line 670 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52826 =
                                      ((x10.core.IndexedMemoryChunk)(counts));
                                    
//#line 670 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52827 =
                                      ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52826))).length);
                                    
//#line 670 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52875 =
                                      ((int) t52827) !=
                                    ((int) 0);
                                    
//#line 670 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52875) {
                                        
//#line 671 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52828 =
                                          ((x10.core.IndexedMemoryChunk)(counts));
                                        
//#line 671 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52829 =
                                          x10.lang.Runtime.hereInt$O();
                                        
//#line 671 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52830 =
                                          count;
                                        
//#line 671 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((int[])t52828.value)[t52829] = t52830;
                                        
//#line 672 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52831 =
                                          length;
                                        
//#line 672 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52832 =
                                          ((2) * (((int)(t52831))));
                                        
//#line 672 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52833 =
                                          x10.lang.Place.getInitialized$MAX_PLACES();
                                        
//#line 672 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52863 =
                                          ((t52832) > (((int)(t52833))));
                                        
//#line 672 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52863) {
                                            
//#line 673 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52834 =
                                              ((x10.core.IndexedMemoryChunk)(counts));
                                            
//#line 673 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52835 =
                                              ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52834))).length);
                                            
//#line 673 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> message =
                                              x10.core.IndexedMemoryChunk.<x10.core.Int>allocate(x10.rtt.Types.INT, ((int)(t52835)), false);
                                            
//#line 674 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52837 =
                                              ((x10.core.IndexedMemoryChunk)(counts));
                                            
//#line 674 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52836 =
                                              ((x10.core.IndexedMemoryChunk)(counts));
                                            
//#line 674 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52838 =
                                              ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52836))).length);
                                            
//#line 674 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.core.IndexedMemoryChunk.<x10.core.Int>copy(t52837,((int)(0)),message,((int)(0)),((int)(t52838)));
                                            
//#line 675 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52843 =
                                              ((null) != (t));
                                            
//#line 675 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52843) {
                                                
//#line 676 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52840 =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteCollectingFinish.$Closure$121<$T>($T, ref,
                                                                                                                                               message,
                                                                                                                                               t, (x10.lang.FinishState.RemoteCollectingFinish.$Closure$121.__0$1x10$lang$FinishState$2__1$1x10$lang$Int$2) null)));
                                                
//#line 676 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52840));
                                            } else {
                                                
//#line 678 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52842 =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteCollectingFinish.$Closure$122<$T>($T, ref,
                                                                                                                                               message,
                                                                                                                                               result, (x10.lang.FinishState.RemoteCollectingFinish.$Closure$122.__0$1x10$lang$FinishState$2__1$1x10$lang$Int$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$122$$T) null)));
                                                
//#line 678 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52842));
                                            }
                                        } else {
                                            
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52844 =
                                              length;
                                            
//#line 681 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message =
                                              x10.core.IndexedMemoryChunk.<x10.util.Pair<x10.core.Int, x10.core.Int>>allocate(x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), ((int)(t52844)), false);
                                            
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i52211min53057 =
                                              0;
                                            
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53058 =
                                              length;
                                            
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i52211max53059 =
                                              ((t53058) - (((int)(1))));
                                            
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
int i53054 =
                                              i52211min53057;
                                            {
                                                
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair[] message$value53063 =
                                                  ((x10.util.Pair[])message.value);
                                                
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
for (;
                                                                                                                                                true;
                                                                                                                                                ) {
                                                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53055 =
                                                      i53054;
                                                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t53056 =
                                                      ((t53055) <= (((int)(i52211max53059))));
                                                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (!(t53056)) {
                                                        
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
break;
                                                    }
                                                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int i53051 =
                                                      i53054;
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t53044 =
                                                      ((x10.core.IndexedMemoryChunk)(places));
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53045 =
                                                      ((int[])t53044.value)[i53051];
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t53046 =
                                                      ((x10.core.IndexedMemoryChunk)(counts));
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t53047 =
                                                      ((x10.core.IndexedMemoryChunk)(places));
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53048 =
                                                      ((int[])t53047.value)[i53051];
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53049 =
                                                      ((int[])t53046.value)[t53048];
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair<x10.core.Int, x10.core.Int> t53050 =
                                                      new x10.util.Pair<x10.core.Int, x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT, x10.rtt.Types.INT).$init(x10.core.Int.$box(t53045),
                                                                                                                                                                           x10.core.Int.$box(t53049), (x10.util.Pair.__0x10$util$Pair$$T__1x10$util$Pair$$U) null);
                                                    
//#line 683 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
message$value53063[i53051]=t53050;
                                                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53052 =
                                                      i53054;
                                                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t53053 =
                                                      ((t53052) + (((int)(1))));
                                                    
//#line 682 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
i53054 = t53053;
                                                }
                                            }
                                            
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52862 =
                                              ((null) != (t));
                                            
//#line 685 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52862) {
                                                
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52859 =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteCollectingFinish.$Closure$123<$T>($T, ref,
                                                                                                                                               message,
                                                                                                                                               t, (x10.lang.FinishState.RemoteCollectingFinish.$Closure$123.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2) null)));
                                                
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52859));
                                            } else {
                                                
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52861 =
                                                  ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteCollectingFinish.$Closure$124<$T>($T, ref,
                                                                                                                                               message,
                                                                                                                                               result, (x10.lang.FinishState.RemoteCollectingFinish.$Closure$124.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$124$$T) null)));
                                                
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52861));
                                            }
                                        }
                                        
//#line 691 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52865 =
                                          ((x10.core.IndexedMemoryChunk)(counts));
                                        
//#line 691 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t52864 =
                                          ((x10.core.IndexedMemoryChunk)(counts));
                                        
//#line 691 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52866 =
                                          ((((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52864))).length);
                                        
//#line 691 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
(((x10.core.IndexedMemoryChunk<x10.core.Int>)(t52865))).clear(((int)(0)), ((int)(t52866)));
                                        
//#line 692 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.length = 1;
                                    } else {
                                        
//#line 694 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message =
                                          x10.core.IndexedMemoryChunk.<x10.util.Pair<x10.core.Int, x10.core.Int>>allocate(x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), ((int)(1)), false);
                                        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52867 =
                                          x10.lang.Runtime.hereInt$O();
                                        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52868 =
                                          count;
                                        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.Pair<x10.core.Int, x10.core.Int> t52869 =
                                          new x10.util.Pair<x10.core.Int, x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT, x10.rtt.Types.INT).$init(x10.core.Int.$box(t52867),
                                                                                                                                                               x10.core.Int.$box(t52868), (x10.util.Pair.__0x10$util$Pair$$T__1x10$util$Pair$$U) null);
                                        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.util.Pair[])message.value)[0] = t52869;
                                        
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final boolean t52874 =
                                          ((null) != (t));
                                        
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
if (t52874) {
                                            
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52871 =
                                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteCollectingFinish.$Closure$125<$T>($T, ref,
                                                                                                                                           message,
                                                                                                                                           t, (x10.lang.FinishState.RemoteCollectingFinish.$Closure$125.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2) null)));
                                            
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52871));
                                        } else {
                                            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.core.fun.VoidFun_0_0 t52873 =
                                              ((x10.core.fun.VoidFun_0_0)(new x10.lang.FinishState.RemoteCollectingFinish.$Closure$126<$T>($T, ref,
                                                                                                                                           message,
                                                                                                                                           result, (x10.lang.FinishState.RemoteCollectingFinish.$Closure$126.__0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$126$$T) null)));
                                            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
closure = ((x10.core.fun.VoidFun_0_0)(t52873));
                                        }
                                    }
                                    
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.count = 0;
                                    
//#line 703 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
this.exceptions = null;
                                    
//#line 704 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.util.concurrent.Lock t52876 =
                                      ((x10.util.concurrent.Lock)(lock));
                                    
//#line 704 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52876.unlock();
                                    
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.Place t52877 =
                                      ((x10.lang.Place)((ref).home));
                                    
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final int t52878 =
                                      t52877.
                                        id;
                                    
//#line 705 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.runtime.impl.java.Runtime.runClosureAt(((int)(t52878)), closure);
                                    
//#line 706 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
x10.lang.Runtime.dealloc(((x10.core.fun.VoidFun_0_0)(closure)));
                                }
                                
                                
//#line 648 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState.RemoteCollectingFinish<$T>
                                                                                                                             x10$lang$FinishState$RemoteCollectingFinish$$x10$lang$FinishState$RemoteCollectingFinish$this(
                                                                                                                             ){
                                    
//#line 648 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.RemoteCollectingFinish.this;
                                }
                                
                                @x10.core.X10Generated public static class $Closure$121<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$121.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$121> $RTT = x10.rtt.StaticVoidFunType.<$Closure$121> make(
                                    /* base class */$Closure$121.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$121 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$121.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.ref = ref;
                                        x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                        $_obj.message = message;
                                        x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                                        $_obj.t = t;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$121 $_obj = new $Closure$121((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                        } else {
                                        $serializer.write(this.ref);
                                        }
                                        if (message instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                        } else {
                                        $serializer.write(this.message);
                                        }
                                        if (t instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                                        } else {
                                        $serializer.write(this.t);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$121(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.FinishState.RemoteCollectingFinish.$Closure$121.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$121 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 676 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t52839 =
                                              x10.lang.FinishState.<x10.lang.FinishState.RootCollectingFinish<$T>>deref__0$1x10$lang$FinishState$2$G(x10.rtt.ParameterizedType.make(x10.lang.FinishState.RootCollectingFinish.$RTT, $T), ((x10.core.GlobalRef)(this.
                                                                                                                                                                                                                                                                 ref)));
                                            
//#line 676 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52839.notify__0$1x10$lang$Int$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                         message)),
                                                                                                                                                                        ((x10.core.X10Throwable)(this.
                                                                                                                                                                                                   t)));
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                        public x10.core.IndexedMemoryChunk<x10.core.Int> message;
                                        public x10.lang.MultipleExceptions t;
                                        
                                        public $Closure$121(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                            final x10.core.IndexedMemoryChunk<x10.core.Int> message,
                                                            final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2 $dummy) {x10.lang.FinishState.RemoteCollectingFinish.$Closure$121.$initParams(this, $T);
                                                                                                                                                              {
                                                                                                                                                                 this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                 this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                 this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                                                             }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$122<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$122.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$122> $RTT = x10.rtt.StaticVoidFunType.<$Closure$122> make(
                                    /* base class */$Closure$122.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$122 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$122.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.ref = ref;
                                        x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                        $_obj.message = message;
                                        $_obj.result = $deserializer.readRef();
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$122 $_obj = new $Closure$122((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                        } else {
                                        $serializer.write(this.ref);
                                        }
                                        if (message instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                        } else {
                                        $serializer.write(this.message);
                                        }
                                        if (result instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.result);
                                        } else {
                                        $serializer.write(this.result);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$122(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.FinishState.RemoteCollectingFinish.$Closure$122.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$122 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 678 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t52841 =
                                              x10.lang.FinishState.<x10.lang.FinishState.RootCollectingFinish<$T>>deref__0$1x10$lang$FinishState$2$G(x10.rtt.ParameterizedType.make(x10.lang.FinishState.RootCollectingFinish.$RTT, $T), ((x10.core.GlobalRef)(this.
                                                                                                                                                                                                                                                                 ref)));
                                            
//#line 678 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.RootCollectingFinish<$T>)t52841).notifyValue__0$1x10$lang$Int$2__1x10$lang$FinishState$RootCollectingFinish$$T(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                                                                                              message)),
                                                                                                                                                                                                                                                                             (($T)(this.
                                                                                                                                                                                                                                                                                     result)));
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                        public x10.core.IndexedMemoryChunk<x10.core.Int> message;
                                        public $T result;
                                        
                                        public $Closure$122(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                            final x10.core.IndexedMemoryChunk<x10.core.Int> message,
                                                            final $T result, __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$122$$T $dummy) {x10.lang.FinishState.RemoteCollectingFinish.$Closure$122.$initParams(this, $T);
                                                                                                                                                                                                        {
                                                                                                                                                                                                           this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                                           this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                                           this.result = (($T)(result));
                                                                                                                                                                                                       }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$FinishState$2__1$1x10$lang$Int$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$122$$T {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$123<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$123.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$123> $RTT = x10.rtt.StaticVoidFunType.<$Closure$123> make(
                                    /* base class */$Closure$123.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$123 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$123.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.ref = ref;
                                        x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                        $_obj.message = message;
                                        x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                                        $_obj.t = t;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$123 $_obj = new $Closure$123((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                        } else {
                                        $serializer.write(this.ref);
                                        }
                                        if (message instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                        } else {
                                        $serializer.write(this.message);
                                        }
                                        if (t instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                                        } else {
                                        $serializer.write(this.t);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$123(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.FinishState.RemoteCollectingFinish.$Closure$123.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$123 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t52858 =
                                              x10.lang.FinishState.<x10.lang.FinishState.RootCollectingFinish<$T>>deref__0$1x10$lang$FinishState$2$G(x10.rtt.ParameterizedType.make(x10.lang.FinishState.RootCollectingFinish.$RTT, $T), ((x10.core.GlobalRef)(this.
                                                                                                                                                                                                                                                                 ref)));
                                            
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52858.notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                        message)),
                                                                                                                                                                                                       ((x10.core.X10Throwable)(this.
                                                                                                                                                                                                                                  t)));
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                        public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                        public x10.lang.MultipleExceptions t;
                                        
                                        public $Closure$123(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                            final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message,
                                                            final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 $dummy) {x10.lang.FinishState.RemoteCollectingFinish.$Closure$123.$initParams(this, $T);
                                                                                                                                                                                             {
                                                                                                                                                                                                this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                                this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                                this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                                                                                            }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$124<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$124.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$124> $RTT = x10.rtt.StaticVoidFunType.<$Closure$124> make(
                                    /* base class */$Closure$124.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$124 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$124.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.ref = ref;
                                        x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                        $_obj.message = message;
                                        $_obj.result = $deserializer.readRef();
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$124 $_obj = new $Closure$124((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                        } else {
                                        $serializer.write(this.ref);
                                        }
                                        if (message instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                        } else {
                                        $serializer.write(this.message);
                                        }
                                        if (result instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.result);
                                        } else {
                                        $serializer.write(this.result);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$124(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.FinishState.RemoteCollectingFinish.$Closure$124.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$124 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t52860 =
                                              x10.lang.FinishState.<x10.lang.FinishState.RootCollectingFinish<$T>>deref__0$1x10$lang$FinishState$2$G(x10.rtt.ParameterizedType.make(x10.lang.FinishState.RootCollectingFinish.$RTT, $T), ((x10.core.GlobalRef)(this.
                                                                                                                                                                                                                                                                 ref)));
                                            
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.RootCollectingFinish<$T>)t52860).notifyValue__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__1x10$lang$FinishState$RootCollectingFinish$$T(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                                                                                                                             message)),
                                                                                                                                                                                                                                                                                                            (($T)(this.
                                                                                                                                                                                                                                                                                                                    result)));
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                        public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                        public $T result;
                                        
                                        public $Closure$124(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                            final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message,
                                                            final $T result, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$124$$T $dummy) {x10.lang.FinishState.RemoteCollectingFinish.$Closure$124.$initParams(this, $T);
                                                                                                                                                                                                                                       {
                                                                                                                                                                                                                                          this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                                                                          this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                                                                          this.result = (($T)(result));
                                                                                                                                                                                                                                      }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$124$$T {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$125<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$125.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$125> $RTT = x10.rtt.StaticVoidFunType.<$Closure$125> make(
                                    /* base class */$Closure$125.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$125 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$125.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.ref = ref;
                                        x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                        $_obj.message = message;
                                        x10.lang.MultipleExceptions t = (x10.lang.MultipleExceptions) $deserializer.readRef();
                                        $_obj.t = t;
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$125 $_obj = new $Closure$125((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                        } else {
                                        $serializer.write(this.ref);
                                        }
                                        if (message instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                        } else {
                                        $serializer.write(this.message);
                                        }
                                        if (t instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                                        } else {
                                        $serializer.write(this.t);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$125(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.FinishState.RemoteCollectingFinish.$Closure$125.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$125 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t52870 =
                                              x10.lang.FinishState.<x10.lang.FinishState.RootCollectingFinish<$T>>deref__0$1x10$lang$FinishState$2$G(x10.rtt.ParameterizedType.make(x10.lang.FinishState.RootCollectingFinish.$RTT, $T), ((x10.core.GlobalRef)(this.
                                                                                                                                                                                                                                                                 ref)));
                                            
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
t52870.notify__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                        message)),
                                                                                                                                                                                                       ((x10.core.X10Throwable)(this.
                                                                                                                                                                                                                                  t)));
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                        public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                        public x10.lang.MultipleExceptions t;
                                        
                                        public $Closure$125(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                            final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message,
                                                            final x10.lang.MultipleExceptions t, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 $dummy) {x10.lang.FinishState.RemoteCollectingFinish.$Closure$125.$initParams(this, $T);
                                                                                                                                                                                             {
                                                                                                                                                                                                this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                                this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                                this.t = ((x10.lang.MultipleExceptions)(t));
                                                                                                                                                                                            }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2 {}
                                        
                                    }
                                    
                                @x10.core.X10Generated public static class $Closure$126<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
                                {
                                    private static final long serialVersionUID = 1L;
                                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$126.class);
                                    
                                    public static final x10.rtt.RuntimeType<$Closure$126> $RTT = x10.rtt.StaticVoidFunType.<$Closure$126> make(
                                    /* base class */$Closure$126.class, 
                                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
                                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                                    );
                                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                                    
                                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
                                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$126 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$126.class + " calling"); } 
                                        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                                        x10.core.GlobalRef ref = (x10.core.GlobalRef) $deserializer.readRef();
                                        $_obj.ref = ref;
                                        x10.core.IndexedMemoryChunk message = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                                        $_obj.message = message;
                                        $_obj.result = $deserializer.readRef();
                                        return $_obj;
                                        
                                    }
                                    
                                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                                    
                                        $Closure$126 $_obj = new $Closure$126((java.lang.System[]) null, (x10.rtt.Type) null);
                                        $deserializer.record_reference($_obj);
                                        return $_deserialize_body($_obj, $deserializer);
                                        
                                    }
                                    
                                    public short $_get_serialization_id() {
                                    
                                         return $_serialization_id;
                                        
                                    }
                                    
                                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                                    
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                                        if (ref instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ref);
                                        } else {
                                        $serializer.write(this.ref);
                                        }
                                        if (message instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.message);
                                        } else {
                                        $serializer.write(this.message);
                                        }
                                        if (result instanceof x10.x10rt.X10JavaSerializable) {
                                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.result);
                                        } else {
                                        $serializer.write(this.result);
                                        }
                                        
                                    }
                                    
                                    // constructor just for allocation
                                    public $Closure$126(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
                                    super($dummy);
                                    x10.lang.FinishState.RemoteCollectingFinish.$Closure$126.$initParams(this, $T);
                                    }
                                    
                                        private x10.rtt.Type $T;
                                        // initializer of type parameters
                                        public static void $initParams(final $Closure$126 $this, final x10.rtt.Type $T) {
                                        $this.$T = $T;
                                        }
                                        
                                        
                                        public void
                                          $apply(
                                          ){
                                            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final x10.lang.FinishState.RootCollectingFinish<$T> t52872 =
                                              x10.lang.FinishState.<x10.lang.FinishState.RootCollectingFinish<$T>>deref__0$1x10$lang$FinishState$2$G(x10.rtt.ParameterizedType.make(x10.lang.FinishState.RootCollectingFinish.$RTT, $T), ((x10.core.GlobalRef)(this.
                                                                                                                                                                                                                                                                 ref)));
                                            
//#line 699 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
((x10.lang.FinishState.RootCollectingFinish<$T>)t52872).notifyValue__0$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__1x10$lang$FinishState$RootCollectingFinish$$T(((x10.core.IndexedMemoryChunk)(this.
                                                                                                                                                                                                                                                                                                                                             message)),
                                                                                                                                                                                                                                                                                                            (($T)(this.
                                                                                                                                                                                                                                                                                                                    result)));
                                        }
                                        
                                        public x10.core.GlobalRef<x10.lang.FinishState> ref;
                                        public x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message;
                                        public $T result;
                                        
                                        public $Closure$126(final x10.rtt.Type $T,
                                                            final x10.core.GlobalRef<x10.lang.FinishState> ref,
                                                            final x10.core.IndexedMemoryChunk<x10.util.Pair<x10.core.Int, x10.core.Int>> message,
                                                            final $T result, __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$126$$T $dummy) {x10.lang.FinishState.RemoteCollectingFinish.$Closure$126.$initParams(this, $T);
                                                                                                                                                                                                                                       {
                                                                                                                                                                                                                                          this.ref = ((x10.core.GlobalRef)(ref));
                                                                                                                                                                                                                                          this.message = ((x10.core.IndexedMemoryChunk)(message));
                                                                                                                                                                                                                                          this.result = (($T)(result));
                                                                                                                                                                                                                                      }}
                                        // synthetic type for parameter mangling
                                        public abstract static class __0$1x10$lang$FinishState$2__1$1x10$util$Pair$1x10$lang$Int$3x10$lang$Int$2$2__2x10$lang$FinishState$RemoteCollectingFinish$$Closure$126$$T {}
                                        
                                    }
                                    
                                // synthetic type for parameter mangling
                                public abstract static class __0$1x10$lang$FinishState$2__1$1x10$lang$FinishState$RemoteCollectingFinish$$T$2 {}
                                
                                }
                                
                                
                                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
final public x10.lang.FinishState
                                                                                                                            x10$lang$FinishState$$x10$lang$FinishState$this(
                                                                                                                            ){
                                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"
return x10.lang.FinishState.this;
                                }
                                
                                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                // constructor for non-virtual call
                                final public x10.lang.FinishState x10$lang$FinishState$$init$S() { {
                                                                                                          
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                          
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/FinishState.x10"

                                                                                                      }
                                                                                                      return this;
                                                                                                      }
                                
                                // constructor
                                public x10.lang.FinishState $init(){return x10$lang$FinishState$$init$S();}
                                
                                
                                public static short fieldId$UNCOUNTED_FINISH;
                                final public static x10.core.concurrent.AtomicInteger initStatus$UNCOUNTED_FINISH = new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
                                
                                public static void
                                  getDeserialized$UNCOUNTED_FINISH(
                                  x10.x10rt.X10JavaDeserializer deserializer){
                                    x10.lang.FinishState.UNCOUNTED_FINISH = ((x10.lang.FinishState.UncountedFinish)(x10.runtime.impl.java.InitDispatcher.deserializeField(((x10.x10rt.X10JavaDeserializer)(deserializer)))));
                                    x10.lang.FinishState.initStatus$UNCOUNTED_FINISH.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                                }
                                
                                public static x10.lang.FinishState.UncountedFinish
                                  getInitialized$UNCOUNTED_FINISH(
                                  ){
                                    if (((int) x10.lang.FinishState.initStatus$UNCOUNTED_FINISH.get()) ==
                                        ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                        return x10.lang.FinishState.UNCOUNTED_FINISH;
                                    }
                                    if (((int) x10.lang.Runtime.hereInt$O()) ==
                                        ((int) 0) &&
                                          x10.lang.FinishState.initStatus$UNCOUNTED_FINISH.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                                         (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                                        x10.lang.FinishState.UNCOUNTED_FINISH = ((x10.lang.FinishState.UncountedFinish)(new x10.lang.FinishState.UncountedFinish((java.lang.System[]) null).$init()));
                                        if (((boolean) x10.runtime.impl.java.InitDispatcher.TRACE_STATIC_INIT) ==
                                              ((boolean) true)) {
                                            x10.runtime.impl.java.InitDispatcher.printStaticInitMessage(((java.lang.String)("Doing static initialisation for field: x10.lang.FinishState.UNCOUNTED_FINISH")));
                                        }
                                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(x10.lang.FinishState.UNCOUNTED_FINISH)),
                                                                                                  (short)(x10.lang.FinishState.fieldId$UNCOUNTED_FINISH));
                                        x10.lang.FinishState.initStatus$UNCOUNTED_FINISH.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                                    } else {
                                        if (((int) x10.lang.FinishState.initStatus$UNCOUNTED_FINISH.get()) !=
                                            ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                            x10.runtime.impl.java.InitDispatcher.lockInitialized();
                                            while (((int) x10.lang.FinishState.initStatus$UNCOUNTED_FINISH.get()) !=
                                                   ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                                                x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                                            }
                                            x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                                        }
                                    }
                                    return x10.lang.FinishState.UNCOUNTED_FINISH;
                                }
                                
                                static {
                                           x10.lang.FinishState.fieldId$UNCOUNTED_FINISH = ((short)(((int)(x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("x10.lang.FinishState")),
                                                                                                                                                               ((java.lang.String)("UNCOUNTED_FINISH")))))));
                                       }
                                
                                }
                                