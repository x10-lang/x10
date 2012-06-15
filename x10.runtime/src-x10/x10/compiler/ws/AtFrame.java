package x10.compiler.ws;


@x10.core.X10Generated final public class AtFrame extends x10.compiler.ws.Frame implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, AtFrame.class);
    
    public static final x10.rtt.RuntimeType<AtFrame> $RTT = x10.rtt.NamedType.<AtFrame> make(
    "x10.compiler.ws.AtFrame", /* base class */AtFrame.class
    , /* parents */ new x10.rtt.Type[] {x10.compiler.ws.Frame.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(AtFrame $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + AtFrame.class + " calling"); } 
        x10.compiler.ws.Frame.$_deserialize_body($_obj, $deserializer);
        x10.core.GlobalRef upRef = (x10.core.GlobalRef) $deserializer.readRef();
        $_obj.upRef = upRef;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        AtFrame $_obj = new AtFrame((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (upRef instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.upRef);
        } else {
        $serializer.write(this.upRef);
        }
        
    }
    
    // constructor just for allocation
    public AtFrame(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 7 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
public x10.core.GlobalRef<x10.compiler.ws.Frame> upRef;
        
        
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
// creation method for java code (1-phase java constructor)
        public AtFrame(final x10.compiler.ws.Frame up,
                       final x10.compiler.ws.FinishFrame ff){this((java.lang.System[]) null);
                                                                 $init(up,ff);}
        
        // constructor for non-virtual call
        final public x10.compiler.ws.AtFrame x10$compiler$ws$AtFrame$$init$S(final x10.compiler.ws.Frame up,
                                                                             final x10.compiler.ws.FinishFrame ff) { {
                                                                                                                            
//#line 10 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
super.$init(((x10.compiler.ws.Frame)(ff)));
                                                                                                                            
//#line 9 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"

                                                                                                                            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.core.GlobalRef<x10.compiler.ws.Frame> t48824 =
                                                                                                                              ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.compiler.ws.Frame>(x10.compiler.ws.Frame.$RTT, ((x10.compiler.ws.Frame)(up)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                                                                                                                            
//#line 11 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
this.upRef = ((x10.core.GlobalRef)(t48824));
                                                                                                                        }
                                                                                                                        return this;
                                                                                                                        }
        
        // constructor
        public x10.compiler.ws.AtFrame $init(final x10.compiler.ws.Frame up,
                                             final x10.compiler.ws.FinishFrame ff){return x10$compiler$ws$AtFrame$$init$S(up,ff);}
        
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
public void
                                                                                                       wrapResume(
                                                                                                       final x10.compiler.ws.Worker worker){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.core.GlobalRef<x10.compiler.ws.Frame> t48825 =
              ((x10.core.GlobalRef)(upRef));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.core.X10Throwable t48826 =
              ((x10.core.X10Throwable)(worker.
                                         throwable));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
x10.compiler.ws.AtFrame.update__0$1x10$compiler$ws$Frame$2(((x10.core.GlobalRef)(t48825)),
                                                                                                                                                                    ((x10.core.X10Throwable)(t48826)));
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
worker.throwable = null;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
public static void
                                                                                                       update__0$1x10$compiler$ws$Frame$2(
                                                                                                       final x10.core.GlobalRef<x10.compiler.ws.Frame> upRef,
                                                                                                       final x10.core.X10Throwable throwable){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.core.fun.VoidFun_0_0 body =
              ((x10.core.fun.VoidFun_0_0)(new x10.compiler.ws.AtFrame.$Closure$91(upRef,
                                                                                  throwable, (x10.compiler.ws.AtFrame.$Closure$91.__0$1x10$compiler$ws$Frame$2) null)));
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.lang.Place t48839 =
              ((x10.lang.Place)((upRef).home));
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final int t48840 =
              t48839.
                id;
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
x10.compiler.ws.Worker.wsRunAsync((int)(t48840),
                                                                                                                                           ((x10.core.fun.VoidFun_0_0)(body)));
        }
        
        
//#line 6 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final public x10.compiler.ws.AtFrame
                                                                                                      x10$compiler$ws$AtFrame$$x10$compiler$ws$AtFrame$this(
                                                                                                      ){
            
//#line 6 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
return x10.compiler.ws.AtFrame.this;
        }
        
        @x10.core.X10Generated public static class $Closure$90 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$90.class);
            
            public static final x10.rtt.RuntimeType<$Closure$90> $RTT = x10.rtt.StaticFunType.<$Closure$90> make(
            /* base class */$Closure$90.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.Frame.$RTT), x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.Frame.$RTT)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$90 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$90.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$90 $_obj = new $Closure$90((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$90(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply__0$1x10$compiler$ws$Frame$2((x10.core.GlobalRef)a1);
            }
            
                
                public x10.core.GlobalRef<x10.compiler.ws.Frame>
                  $apply__0$1x10$compiler$ws$Frame$2(
                  final x10.core.GlobalRef __desugarer__var__42__){
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.lang.Place t48827 =
                      ((x10.lang.Place)((__desugarer__var__42__).home));
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final boolean t48828 =
                      x10.rtt.Equality.equalsequals((t48827),(x10.lang.Runtime.home()));
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final boolean t48830 =
                      !(t48828);
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
if (t48830) {
                        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.lang.FailedDynamicCheckException t48829 =
                          new x10.lang.FailedDynamicCheckException("x10.lang.GlobalRef[x10.compiler.ws.Frame]{self.home==here}");
                        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
throw t48829;
                    }
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
return __desugarer__var__42__;
                }
                
                public $Closure$90() { {
                                              
                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$91 extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_GENERAL_ASYNC, $Closure$91.class);
            
            public static final x10.rtt.RuntimeType<$Closure$91> $RTT = x10.rtt.StaticVoidFunType.<$Closure$91> make(
            /* base class */$Closure$91.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$91 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$91.class + " calling"); } 
                x10.core.GlobalRef upRef = (x10.core.GlobalRef) $deserializer.readRef();
                $_obj.upRef = upRef;
                x10.core.X10Throwable throwable = (x10.core.X10Throwable) $deserializer.readRef();
                $_obj.throwable = throwable;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$91 $_obj = new $Closure$91((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (upRef instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.upRef);
                } else {
                $serializer.write(this.upRef);
                }
                if (throwable instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.throwable);
                } else {
                $serializer.write(this.throwable);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$91(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.core.fun.Fun_0_1<x10.core.GlobalRef<x10.compiler.ws.Frame>,x10.core.GlobalRef<x10.compiler.ws.Frame>> t48831 =
                      ((x10.core.fun.Fun_0_1)(new x10.compiler.ws.AtFrame.$Closure$90()));
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.core.GlobalRef<x10.compiler.ws.Frame> t48832 =
                      ((x10.core.GlobalRef)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.Frame.$RTT),this.
                                                                                                                                                        upRef));
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.core.GlobalRef<x10.compiler.ws.Frame> t48833 =
                      ((x10.core.GlobalRef)(((x10.core.fun.Fun_0_1<x10.core.GlobalRef<x10.compiler.ws.Frame>,x10.core.GlobalRef<x10.compiler.ws.Frame>>)t48831).$apply(t48832,x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.compiler.ws.Frame.$RTT))));
                    
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
x10.compiler.ws.Frame up =
                      (((x10.core.GlobalRef<x10.compiler.ws.Frame>)(t48833))).$apply$G();
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final boolean t48836 =
                      ((null) != (this.
                                    throwable));
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
if (t48836) {
                        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.compiler.ws.Frame t48834 =
                          up;
                        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.compiler.ws.ThrowFrame t48835 =
                          ((x10.compiler.ws.ThrowFrame)(new x10.compiler.ws.ThrowFrame((java.lang.System[]) null).$init(t48834,
                                                                                                                        this.
                                                                                                                          throwable)));
                        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
up = ((x10.compiler.ws.Frame)(t48835));
                    }
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.runtime.impl.java.Deque t48837 =
                      x10.lang.Runtime.wsFIFO();
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
final x10.compiler.ws.Frame t48838 =
                      up;
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/compiler/ws/AtFrame.x10"
t48837.push(((x10.core.RefI)(t48838)));
                }
                
                public x10.core.GlobalRef<x10.compiler.ws.Frame> upRef;
                public x10.core.X10Throwable throwable;
                
                public $Closure$91(final x10.core.GlobalRef<x10.compiler.ws.Frame> upRef,
                                   final x10.core.X10Throwable throwable, __0$1x10$compiler$ws$Frame$2 $dummy) { {
                                                                                                                        this.upRef = ((x10.core.GlobalRef)(upRef));
                                                                                                                        this.throwable = ((x10.core.X10Throwable)(throwable));
                                                                                                                    }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$compiler$ws$Frame$2 {}
                
            }
            
        
        }
        