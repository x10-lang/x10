package x10.util;

@x10.core.X10Generated public class WorkerLocalHandle<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.core.fun.VoidFun_0_1, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, WorkerLocalHandle.class);
    
    public static final x10.rtt.RuntimeType<WorkerLocalHandle> $RTT = x10.rtt.NamedType.<WorkerLocalHandle> make(
    "x10.util.WorkerLocalHandle", /* base class */WorkerLocalHandle.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.core.fun.VoidFun_0_1.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(WorkerLocalHandle $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + WorkerLocalHandle.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.lang.PlaceLocalHandle store = (x10.lang.PlaceLocalHandle) $deserializer.readRef();
        $_obj.store = store;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        WorkerLocalHandle $_obj = new WorkerLocalHandle((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (store instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.store);
        } else {
        $serializer.write(this.store);
        }
        
    }
    
    // constructor just for allocation
    public WorkerLocalHandle(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.util.WorkerLocalHandle.$initParams(this, $T);
    }
    // dispatcher for method abstract public (a1:Z1)=>void.operator()(a1:Z1){}:void
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    $apply__0x10$util$WorkerLocalHandle$$T(($T)a1);return null;
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final WorkerLocalHandle $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
public x10.lang.PlaceLocalHandle<x10.array.Array<$T>> store;
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
// creation method for java code (1-phase java constructor)
        public WorkerLocalHandle(final x10.rtt.Type $T,
                                 final $T t, __0x10$util$WorkerLocalHandle$$T $dummy){this((java.lang.System[]) null, $T);
                                                                                          $init(t, (x10.util.WorkerLocalHandle.__0x10$util$WorkerLocalHandle$$T) null);}
        
        // constructor for non-virtual call
        final public x10.util.WorkerLocalHandle<$T> x10$util$WorkerLocalHandle$$init$S(final $T t, __0x10$util$WorkerLocalHandle$$T $dummy) { {
                                                                                                                                                     
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"

                                                                                                                                                     
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"

                                                                                                                                                     
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t63622 =
                                                                                                                                                       ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
                                                                                                                                                     
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.core.fun.Fun_0_0<x10.array.Array<$T>> t63623 =
                                                                                                                                                       ((x10.core.fun.Fun_0_0)(new x10.util.WorkerLocalHandle.$Closure$174<$T>($T, t, (x10.util.WorkerLocalHandle.$Closure$174.__0x10$util$WorkerLocalHandle$$Closure$174$$T) null)));
                                                                                                                                                     
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<$T>> t63624 =
                                                                                                                                                       x10.lang.PlaceLocalHandle.<x10.array.Array<$T>>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, $T), ((x10.array.PlaceGroup)(t63622)),
                                                                                                                                                                                                                                              ((x10.core.fun.Fun_0_0)(t63623)));
                                                                                                                                                     
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
this.store = ((x10.lang.PlaceLocalHandle)(t63624));
                                                                                                                                                 }
                                                                                                                                                 return this;
                                                                                                                                                 }
        
        // constructor
        public x10.util.WorkerLocalHandle<$T> $init(final $T t, __0x10$util$WorkerLocalHandle$$T $dummy){return x10$util$WorkerLocalHandle$$init$S(t, $dummy);}
        
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
public $T
                                                                                                          $apply$G(
                                                                                                          ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<$T>> t63625 =
              ((x10.lang.PlaceLocalHandle)(store));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.array.Array<$T> t63626 =
              ((x10.array.Array)(((x10.lang.PlaceLocalHandle<x10.array.Array<$T>>)t63625).$apply$G()));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final int t63627 =
              x10.lang.Runtime.workerId$O();
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final $T t63628 =
              (($T)(((x10.array.Array<$T>)t63626).$apply$G((int)(t63627))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
return t63628;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
public void
                                                                                                          $apply__0x10$util$WorkerLocalHandle$$T(
                                                                                                          final $T t){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<$T>> t63629 =
              ((x10.lang.PlaceLocalHandle)(store));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.array.Array<$T> t63630 =
              ((x10.array.Array)(((x10.lang.PlaceLocalHandle<x10.array.Array<$T>>)t63629).$apply$G()));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final int t63631 =
              x10.lang.Runtime.workerId$O();
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
((x10.array.Array<$T>)t63630).$set__1x10$array$Array$$T$G((int)(t63631),
                                                                                                                                                                      (($T)(t)));
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final public x10.util.WorkerLocalHandle<$T>
                                                                                                          x10$util$WorkerLocalHandle$$x10$util$WorkerLocalHandle$this(
                                                                                                          ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
return x10.util.WorkerLocalHandle.this;
        }
        
        @x10.core.X10Generated public static class $Closure$174<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$174.class);
            
            public static final x10.rtt.RuntimeType<$Closure$174> $RTT = x10.rtt.StaticFunType.<$Closure$174> make(
            /* base class */$Closure$174.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$174 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$174.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.t = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$174 $_obj = new $Closure$174((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (t instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.t);
                } else {
                $serializer.write(this.t);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$174(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.WorkerLocalHandle.$Closure$174.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.Array
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$174 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.Array<$T>
                  $apply(
                  ){
                    
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final int t63620 =
                      x10.lang.Runtime.MAX_THREADS;
                    
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
final x10.array.Array<$T> t63621 =
                      ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(((int)(t63620)),
                                                                                                      this.
                                                                                                        t, (x10.array.Array.__1x10$array$Array$$T) null)));
                    
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalHandle.x10"
return t63621;
                }
                
                public $T t;
                
                public $Closure$174(final x10.rtt.Type $T,
                                    final $T t, __0x10$util$WorkerLocalHandle$$Closure$174$$T $dummy) {x10.util.WorkerLocalHandle.$Closure$174.$initParams(this, $T);
                                                                                                            {
                                                                                                               this.t = (($T)(t));
                                                                                                           }}
                // synthetic type for parameter mangling
                public abstract static class __0x10$util$WorkerLocalHandle$$Closure$174$$T {}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __0x10$util$WorkerLocalHandle$$T {}
        
    }
    