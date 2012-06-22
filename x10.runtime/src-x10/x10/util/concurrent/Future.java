package x10.util.concurrent;


@x10.core.X10Generated public class Future<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Future.class);
    
    public static final x10.rtt.RuntimeType<Future> $RTT = x10.rtt.NamedType.<Future> make(
    "x10.util.concurrent.Future", /* base class */Future.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Future $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Future.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
        $_obj.root = root;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Future $_obj = new Future((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (root instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
        } else {
        $serializer.write(this.root);
        }
        
    }
    
    // constructor just for allocation
    public Future(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.util.concurrent.Future.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final Future $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public x10.core.GlobalRef<x10.util.concurrent.Future<$T>> root;
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
/**
     * Latch for signaling and wait
     */
        public transient x10.util.concurrent.Latch latch;
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
/**
     * Set if the activity terminated with an exception.
     * Can only be of type Error or Exception
     *
     */
        public transient x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable> exception;
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public transient x10.util.GrowableIndexedMemoryChunk<$T> result;
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public transient x10.core.fun.Fun_0_0<$T> eval;
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public static <$T>x10.util.concurrent.Future<$T>
                                                                                                          make__0$1x10$util$concurrent$Future$$T$2(
                                                                                                          final x10.rtt.Type $T,
                                                                                                          final x10.core.fun.Fun_0_0<$T> eval){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
x10.lang.Runtime.ensureNotInAtomic();
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Future<$T> f =
              ((x10.util.concurrent.Future)(new x10.util.concurrent.Future<$T>((java.lang.System[]) null, $T).$init(((x10.core.fun.Fun_0_0)(eval)), (x10.util.concurrent.Future.__0$1x10$util$concurrent$Future$$T$2) null)));
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
x10.lang.Runtime.runAsync(((x10.core.fun.VoidFun_0_0)(new x10.util.concurrent.Future.$Closure$176<$T>($T, f, (x10.util.concurrent.Future.$Closure$176.__0$1x10$util$concurrent$Future$$Closure$176$$T$2) null))));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return f;
        }
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
// creation method for java code (1-phase java constructor)
        public Future(final x10.rtt.Type $T,
                      final x10.core.fun.Fun_0_0<$T> eval, __0$1x10$util$concurrent$Future$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                            $init(eval, (x10.util.concurrent.Future.__0$1x10$util$concurrent$Future$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.util.concurrent.Future<$T> x10$util$concurrent$Future$$init$S(final x10.core.fun.Fun_0_0<$T> eval, __0$1x10$util$concurrent$Future$$T$2 $dummy) { {
                                                                                                                                                                                  
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"

                                                                                                                                                                                  
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"

                                                                                                                                                                                  
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
this.__fieldInitializers63805();
                                                                                                                                                                                  
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
this.eval = ((x10.core.fun.Fun_0_0)(eval));
                                                                                                                                                                              }
                                                                                                                                                                              return this;
                                                                                                                                                                              }
        
        // constructor
        public x10.util.concurrent.Future<$T> $init(final x10.core.fun.Fun_0_0<$T> eval, __0$1x10$util$concurrent$Future$$T$2 $dummy){return x10$util$concurrent$Future$$init$S(eval, $dummy);}
        
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public boolean
                                                                                                          forced$O(
                                                                                                          ){
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.core.GlobalRef<x10.util.concurrent.Future<$T>> t63806 =
              ((x10.core.GlobalRef)(root));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.lang.Place t63811 =
              ((x10.lang.Place)((t63806).home));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final boolean t63812 =
              x10.core.Boolean.$unbox(x10.lang.Runtime.<x10.core.Boolean>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.Types.BOOLEAN, ((x10.lang.Place)(t63811)),
                                                                                                            ((x10.core.fun.Fun_0_0)(new x10.util.concurrent.Future.$Closure$177<$T>($T, ((x10.util.concurrent.Future)(this)),
                                                                                                                                                                                    root, (x10.util.concurrent.Future.$Closure$177.__0$1x10$util$concurrent$Future$$Closure$177$$T$2__1$1x10$util$concurrent$Future$1x10$util$concurrent$Future$$Closure$177$$T$2$2) null)))));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return t63812;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public $T
                                                                                                          $apply$G(
                                                                                                          ){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final $T t63825 =
              (($T)(this.force$G()));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return t63825;
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public $T
                                                                                                          force$G(
                                                                                                          ){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
x10.lang.Runtime.ensureNotInAtomic();
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.core.GlobalRef<x10.util.concurrent.Future<$T>> t63813 =
              ((x10.core.GlobalRef)(root));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.lang.Place t63817 =
              ((x10.lang.Place)((t63813).home));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final $T t63818 =
              (($T)(x10.lang.Runtime.<$T>evalAt__1$1x10$lang$Runtime$$T$2$G($T, ((x10.lang.Place)(t63817)),
                                                                            ((x10.core.fun.Fun_0_0)(new x10.util.concurrent.Future.$Closure$178<$T>($T, ((x10.util.concurrent.Future)(this)),
                                                                                                                                                    root, (x10.util.concurrent.Future.$Closure$178.__0$1x10$util$concurrent$Future$$Closure$178$$T$2__1$1x10$util$concurrent$Future$1x10$util$concurrent$Future$$Closure$178$$T$2$2) null))))));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return t63818;
        }
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
private $T
                                                                                                          forceLocal$G(
                                                                                                          ){
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Latch t63826 =
              ((x10.util.concurrent.Latch)(latch));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
t63826.await();
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable> t63827 =
              ((x10.util.GrowableIndexedMemoryChunk)(exception));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final int t63828 =
              ((x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable>)t63827).length$O();
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final boolean t63831 =
              ((t63828) > (((int)(0))));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
if (t63831) {
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable> t63829 =
                  ((x10.util.GrowableIndexedMemoryChunk)(exception));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.core.X10Throwable t63830 =
                  ((x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable>)t63829).$apply$G((int)(0));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
throw t63830;
            }
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t63832 =
              ((x10.util.GrowableIndexedMemoryChunk)(result));
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final $T t63833 =
              (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t63832).$apply$G((int)(0))));
            
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return t63833;
        }
        
        public static <$T>$T
          forceLocal$P__0$1x10$util$concurrent$Future$$T$2$G(
          final x10.rtt.Type $T,
          final x10.util.concurrent.Future<$T> Future){
            return ((x10.util.concurrent.Future<$T>)Future).forceLocal$G();
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
public void
                                                                                                          run(
                                                                                                          ){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
try {try {{
                {
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
x10.lang.Runtime.ensureNotInAtomic();
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.lang.FinishState x10$__var24 =
                      x10.lang.Runtime.startFinish();
                    
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
try {try {{
                        {
                            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t63835 =
                              ((x10.util.GrowableIndexedMemoryChunk)(result));
                            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.core.fun.Fun_0_0<$T> t63834 =
                              ((x10.core.fun.Fun_0_0)(eval));
                            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final $T t63836 =
                              (($T)(((x10.core.fun.Fun_0_0<$T>)t63834).$apply$G()));
                            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t63835).add__0x10$util$GrowableIndexedMemoryChunk$$T((($T)(t63836)));
                        }
                    }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
throw new x10.lang.Exception();
                    }finally {{
                         
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var24)));
                     }}
                    }
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Latch t63837 =
                  ((x10.util.concurrent.Latch)(latch));
                
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
t63837.release();
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (final x10.core.X10Throwable t) {
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable> t63838 =
                      ((x10.util.GrowableIndexedMemoryChunk)(exception));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
((x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable>)t63838).add__0x10$util$GrowableIndexedMemoryChunk$$T(((x10.core.X10Throwable)(t)));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Latch t63839 =
                      ((x10.util.concurrent.Latch)(latch));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
t63839.release();
                }
            }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final public x10.util.concurrent.Future<$T>
                                                                                                          x10$util$concurrent$Future$$x10$util$concurrent$Future$this(
                                                                                                          ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return x10.util.concurrent.Future.this;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final public void
                                                                                                          __fieldInitializers63805(
                                                                                                          ){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.core.GlobalRef<x10.util.concurrent.Future<$T>> t63840 =
              ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.util.concurrent.Future<$T>>(x10.rtt.ParameterizedType.make(x10.util.concurrent.Future.$RTT, $T), ((x10.util.concurrent.Future)(this)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
this.root = ((x10.core.GlobalRef)(t63840));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Latch t63841 =
              ((x10.util.concurrent.Latch)(new x10.util.concurrent.Latch()));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
this.latch = ((x10.util.concurrent.Latch)(t63841));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable> t63842 =
              ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<x10.core.X10Throwable>((java.lang.System[]) null, x10.core.X10Throwable.$RTT).$init()));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
this.exception = ((x10.util.GrowableIndexedMemoryChunk)(t63842));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t63843 =
              ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<$T>((java.lang.System[]) null, $T).$init()));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
this.result = ((x10.util.GrowableIndexedMemoryChunk)(t63843));
        }
        
        @x10.core.X10Generated public static class $Closure$176<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$176.class);
            
            public static final x10.rtt.RuntimeType<$Closure$176> $RTT = x10.rtt.StaticVoidFunType.<$Closure$176> make(
            /* base class */$Closure$176.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$176 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$176.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.concurrent.Future f = (x10.util.concurrent.Future) $deserializer.readRef();
                $_obj.f = f;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$176 $_obj = new $Closure$176((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (f instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.f);
                } else {
                $serializer.write(this.f);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$176(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.concurrent.Future.$Closure$176.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$176 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
((x10.util.concurrent.Future<$T>)this.
                                                                                                                                                       f).run();
                }
                
                public x10.util.concurrent.Future<$T> f;
                
                public $Closure$176(final x10.rtt.Type $T,
                                    final x10.util.concurrent.Future<$T> f, __0$1x10$util$concurrent$Future$$Closure$176$$T$2 $dummy) {x10.util.concurrent.Future.$Closure$176.$initParams(this, $T);
                                                                                                                                            {
                                                                                                                                               this.f = ((x10.util.concurrent.Future)(f));
                                                                                                                                           }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$concurrent$Future$$Closure$176$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$177<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$177.class);
            
            public static final x10.rtt.RuntimeType<$Closure$177> $RTT = x10.rtt.StaticFunType.<$Closure$177> make(
            /* base class */$Closure$177.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.Types.BOOLEAN), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$177 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$177.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.concurrent.Future out$$ = (x10.util.concurrent.Future) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.root = root;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$177 $_obj = new $Closure$177((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (root instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                } else {
                $serializer.write(this.root);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$177(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.concurrent.Future.$Closure$177.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.core.Boolean
              $apply$G(){return x10.core.Boolean.$box($apply$O());}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$177 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public boolean
                  $apply$O(
                  ){
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.core.GlobalRef<x10.util.concurrent.Future<$T>> t63807 =
                      ((x10.core.GlobalRef)(this.
                                              root));
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Future<$T> t63808 =
                      (((x10.core.GlobalRef<x10.util.concurrent.Future<$T>>)(t63807))).$apply$G();
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Latch t63809 =
                      ((x10.util.concurrent.Latch)(((x10.util.concurrent.Future<$T>)t63808).
                                                     latch));
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final boolean t63810 =
                      t63809.$apply$O();
                    
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return t63810;
                }
                
                public x10.util.concurrent.Future<$T> out$$;
                public x10.core.GlobalRef<x10.util.concurrent.Future<$T>> root;
                
                public $Closure$177(final x10.rtt.Type $T,
                                    final x10.util.concurrent.Future<$T> out$$,
                                    final x10.core.GlobalRef<x10.util.concurrent.Future<$T>> root, __0$1x10$util$concurrent$Future$$Closure$177$$T$2__1$1x10$util$concurrent$Future$1x10$util$concurrent$Future$$Closure$177$$T$2$2 $dummy) {x10.util.concurrent.Future.$Closure$177.$initParams(this, $T);
                                                                                                                                                                                                                                                  {
                                                                                                                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                                                                                                                     this.root = ((x10.core.GlobalRef)(root));
                                                                                                                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$concurrent$Future$$Closure$177$$T$2__1$1x10$util$concurrent$Future$1x10$util$concurrent$Future$$Closure$177$$T$2$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$178<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$178.class);
            
            public static final x10.rtt.RuntimeType<$Closure$178> $RTT = x10.rtt.StaticFunType.<$Closure$178> make(
            /* base class */$Closure$178.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$178 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$178.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.concurrent.Future out$$ = (x10.util.concurrent.Future) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.GlobalRef root = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.root = root;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$178 $_obj = new $Closure$178((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (root instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.root);
                } else {
                $serializer.write(this.root);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$178(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.concurrent.Future.$Closure$178.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$178 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public $T
                  $apply$G(
                  ){
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.core.GlobalRef<x10.util.concurrent.Future<$T>> t63814 =
                      ((x10.core.GlobalRef)(this.
                                              root));
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final x10.util.concurrent.Future<$T> t63815 =
                      (((x10.core.GlobalRef<x10.util.concurrent.Future<$T>>)(t63814))).$apply$G();
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
final $T t63816 =
                      (($T)(((x10.util.concurrent.Future<$T>)t63815).forceLocal$G()));
                    
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/concurrent/Future.x10"
return t63816;
                }
                
                public x10.util.concurrent.Future<$T> out$$;
                public x10.core.GlobalRef<x10.util.concurrent.Future<$T>> root;
                
                public $Closure$178(final x10.rtt.Type $T,
                                    final x10.util.concurrent.Future<$T> out$$,
                                    final x10.core.GlobalRef<x10.util.concurrent.Future<$T>> root, __0$1x10$util$concurrent$Future$$Closure$178$$T$2__1$1x10$util$concurrent$Future$1x10$util$concurrent$Future$$Closure$178$$T$2$2 $dummy) {x10.util.concurrent.Future.$Closure$178.$initParams(this, $T);
                                                                                                                                                                                                                                                  {
                                                                                                                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                                                                                                                     this.root = ((x10.core.GlobalRef)(root));
                                                                                                                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$concurrent$Future$$Closure$178$$T$2__1$1x10$util$concurrent$Future$1x10$util$concurrent$Future$$Closure$178$$T$2$2 {}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$util$concurrent$Future$$T$2 {}
        
        }
        