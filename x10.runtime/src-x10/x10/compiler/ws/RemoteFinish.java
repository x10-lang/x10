package x10.compiler.ws;


@x10.core.X10Generated final public class RemoteFinish extends x10.compiler.ws.FinishFrame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteFinish.class);
    
    public static final x10.rtt.RuntimeType<RemoteFinish> $RTT = x10.rtt.NamedType.<RemoteFinish> make(
    "x10.compiler.ws.RemoteFinish", /* base class */RemoteFinish.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.FinishFrame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteFinish $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RemoteFinish.class + " calling"); } 
        x10.compiler.ws.FinishFrame.$_deserialize_body($_obj, $deserializer);
        x10.core.GlobalRef ffRef = (x10.core.GlobalRef) $deserializer.readRef();
        $_obj.ffRef = ffRef;
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
        if (ffRef instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.ffRef);
        } else {
        $serializer.write(this.ffRef);
        }
        
    }
    
    // constructor just for allocation
    public RemoteFinish(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 10 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
public x10.core.GlobalRef<x10.compiler.ws.FinishFrame> ffRef;
        
        
//#line 12 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
// creation method for java code (1-phase java constructor)
        public RemoteFinish(final x10.compiler.ws.FinishFrame ff){this((java.lang.System[]) null);
                                                                      $init(ff);}
        
        // constructor for non-virtual call
        final public x10.compiler.ws.RemoteFinish x10$compiler$ws$RemoteFinish$$init$S(final x10.compiler.ws.FinishFrame ff) { {
                                                                                                                                      
//#line 13 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
super.$init(((x10.compiler.ws.Frame)(null)));
                                                                                                                                      
//#line 12 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"

                                                                                                                                      
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
this.asyncs = 1;
                                                                                                                                      
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.core.GlobalRef<x10.compiler.ws.FinishFrame> t48988 =
                                                                                                                                        ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.compiler.ws.FinishFrame>(x10.compiler.ws.FinishFrame.$RTT, ((x10.compiler.ws.FinishFrame)(ff)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                                                                                                                                      
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
this.ffRef = ((x10.core.GlobalRef)(t48988));
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.util.concurrent.Monitor t48989 =
                                                                                                                                        ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
t48989.lock();
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.compiler.ws.FinishFrame x48986 =
                                                                                                                                        ((x10.compiler.ws.FinishFrame)(ff));
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
;
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final int t48990 =
                                                                                                                                        x48986.
                                                                                                                                          asyncs;
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final int t48991 =
                                                                                                                                        ((t48990) + (((int)(1))));
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
x48986.asyncs = t48991;
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.util.concurrent.Monitor t48992 =
                                                                                                                                        ((x10.util.concurrent.Monitor)(x10.lang.Runtime.atomicMonitor));
                                                                                                                                      
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
t48992.unlock();
                                                                                                                                  }
                                                                                                                                  return this;
                                                                                                                                  }
        
        // constructor
        public x10.compiler.ws.RemoteFinish $init(final x10.compiler.ws.FinishFrame ff){return x10$compiler$ws$RemoteFinish$$init$S(ff);}
        
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
public void
                                                                                                            wrapResume(
                                                                                                            final x10.compiler.ws.Worker worker){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
super.wrapResume(((x10.compiler.ws.Worker)(worker)));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.core.GlobalRef<x10.compiler.ws.FinishFrame> t48993 =
              ((x10.core.GlobalRef)(ffRef));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.util.Stack<x10.core.X10Throwable> t48994 =
              ((x10.util.Stack)(stack));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
x10.compiler.ws.RemoteFinish.update__0$1x10$compiler$ws$FinishFrame$2__1$1x10$lang$Throwable$2(((x10.core.GlobalRef)(t48993)),
                                                                                                                                                                                                             ((x10.util.Stack)(t48994)));
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
worker.throwable = null;
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.compiler.Abort t48995 =
              ((x10.compiler.Abort)(x10.compiler.Abort.getInitialized$ABORT()));
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
throw t48995;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
public static void
                                                                                                            update__0$1x10$compiler$ws$FinishFrame$2__1$1x10$lang$Throwable$2(
                                                                                                            final x10.core.GlobalRef<x10.compiler.ws.FinishFrame> ffRef,
                                                                                                            final x10.util.Stack<x10.core.X10Throwable> stack){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.core.fun.VoidFun_0_0 body =
              ((x10.core.fun.VoidFun_0_0)(new x10.compiler.ws.RemoteFinish.$Closure$93(ffRef,
                                                                                       stack, (x10.compiler.ws.RemoteFinish.$Closure$93.__0$1x10$compiler$ws$FinishFrame$2__1$1x10$lang$Throwable$2) null)));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.lang.Place t49004 =
              ((x10.lang.Place)((ffRef).home));
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final int t49005 =
              t49004.
                id;
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
x10.compiler.ws.Worker.wsRunAsync((int)(t49005),
                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(body)));
        }
        
        
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final public x10.compiler.ws.RemoteFinish
                                                                                                           x10$compiler$ws$RemoteFinish$$x10$compiler$ws$RemoteFinish$this(
                                                                                                           ){
            
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
return x10.compiler.ws.RemoteFinish.this;
        }
        
        @x10.core.X10Generated public static class $Closure$92 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$92.class);
            
            public static final x10.rtt.RuntimeType<$Closure$92> $RTT = x10.rtt.StaticFunType.<$Closure$92> make(
            /* base class */$Closure$92.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.FinishFrame.$RTT), x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.FinishFrame.$RTT)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$92 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$92.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$92 $_obj = new $Closure$92((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$92(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply__0$1x10$compiler$ws$FinishFrame$2((x10.core.GlobalRef)a1);
            }
            
                
                public x10.core.GlobalRef<x10.compiler.ws.FinishFrame>
                  $apply__0$1x10$compiler$ws$FinishFrame$2(
                  final x10.core.GlobalRef __desugarer__var__43__){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.lang.Place t48996 =
                      ((x10.lang.Place)((__desugarer__var__43__).home));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final boolean t48997 =
                      x10.rtt.Equality.equalsequals((t48996),(x10.lang.Runtime.home()));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final boolean t48999 =
                      !(t48997);
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
if (t48999) {
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.lang.FailedDynamicCheckException t48998 =
                          new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.compiler.ws.FinishFrame]{self.home==here}");
                        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
throw t48998;
                    }
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
return __desugarer__var__43__;
                }
                
                public $Closure$92() { {
                                              
                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$93 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$93.class);
            
            public static final x10.rtt.RuntimeType<$Closure$93> $RTT = x10.rtt.StaticVoidFunType.<$Closure$93> make(
            /* base class */$Closure$93.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$93 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$93.class + " calling"); } 
                x10.core.GlobalRef ffRef = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.ffRef = ffRef;
                x10.util.Stack stack = (x10.util.Stack) $deserializer.readRef();
                $_obj.stack = stack;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$93 $_obj = new $Closure$93((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (ffRef instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.ffRef);
                } else {
                $serializer.write(this.ffRef);
                }
                if (stack instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.stack);
                } else {
                $serializer.write(this.stack);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$93(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.core.fun.Fun_0_1<x10.core.GlobalRef<x10.compiler.ws.FinishFrame>,x10.core.GlobalRef<x10.compiler.ws.FinishFrame>> t49000 =
                      ((x10.core.fun.Fun_0_1)(new x10.compiler.ws.RemoteFinish.$Closure$92()));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.core.GlobalRef<x10.compiler.ws.FinishFrame> t49001 =
                      ((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.FinishFrame.$RTT),this.
                                                                                                                                                              ffRef));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.core.GlobalRef<x10.compiler.ws.FinishFrame> t49002 =
                      ((x10.core.GlobalRef)(((x10.core.fun.Fun_0_1<x10.core.GlobalRef<x10.compiler.ws.FinishFrame>,x10.core.GlobalRef<x10.compiler.ws.FinishFrame>>)t49000).$apply(t49001,x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.FinishFrame.$RTT))));
                    
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.compiler.ws.FinishFrame ff =
                      (((x10.core.GlobalRef<x10.compiler.ws.FinishFrame>)(t49002))).$apply$G();
                    
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
ff.append__0$1x10$lang$Throwable$2(((x10.util.Stack)(this.
                                                                                                                                                                             stack)));
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
final x10.runtime.impl.java.Deque t49003 =
                      x10.lang.Runtime.wsFIFO();
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/RemoteFinish.x10"
t49003.push(((x10.core.RefI)(ff)));
                }
                
                public x10.core.GlobalRef<x10.compiler.ws.FinishFrame> ffRef;
                public x10.util.Stack<x10.core.X10Throwable> stack;
                
                public $Closure$93(final x10.core.GlobalRef<x10.compiler.ws.FinishFrame> ffRef,
                                   final x10.util.Stack<x10.core.X10Throwable> stack, __0$1x10$compiler$ws$FinishFrame$2__1$1x10$lang$Throwable$2 $dummy) { {
                                                                                                                                                                   this.ffRef = ((x10.core.GlobalRef)(ffRef));
                                                                                                                                                                   this.stack = ((x10.util.Stack)(stack));
                                                                                                                                                               }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$compiler$ws$FinishFrame$2__1$1x10$lang$Throwable$2 {}
                
            }
            
        
        public void
          x10$compiler$ws$FinishFrame$wrapResume$S(
          final x10.compiler.ws.Worker a0){
            super.wrapResume(((x10.compiler.ws.Worker)(a0)));
        }
        
        }
        