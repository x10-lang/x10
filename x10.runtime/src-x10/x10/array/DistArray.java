package x10.array;


@x10.core.X10Generated final public class DistArray<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.lang.Iterable, x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, DistArray.class);
    
    public static final x10.rtt.RuntimeType<DistArray> $RTT = x10.rtt.NamedType.<DistArray> make(
    "x10.array.DistArray", /* base class */DistArray.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.array.Point.$RTT), x10.io.CustomSerialization.$RTT, x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new DistArray($T, $$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($T);
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $T = (x10.rtt.Type) ois.readObject();
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(DistArray $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + DistArray.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        x10.rtt.Type $T = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$T = $T;
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        DistArray $_obj = new DistArray((java.lang.System[]) null, (x10.rtt.Type) null);
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
    public DistArray(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.array.DistArray.$initParams(this, $T);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $apply$G((x10.array.Point)a1);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final DistArray $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.Dist dist;
        
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public x10.array.Region
                                                                                                   region(
                                                                                                   ){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t36971 =
              ((x10.array.Dist)(dist));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t36972 =
              ((x10.array.Region)(t36971.
                                    region));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t36972;
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public int
                                                                                                   rank$O(
                                                                                                   ){
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t36973 =
              ((x10.array.Dist)(dist));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t36974 =
              t36973.rank$O();
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t36974;
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
@x10.core.X10Generated public static class LocalState<$T36928> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                                 {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LocalState.class);
            
            public static final x10.rtt.RuntimeType<LocalState> $RTT = x10.rtt.NamedType.<LocalState> make(
            "x10.array.DistArray.LocalState", /* base class */LocalState.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T36928;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(LocalState $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + LocalState.class + " calling"); } 
                $_obj.$T36928 = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.core.IndexedMemoryChunk data = (x10.core.IndexedMemoryChunk) $deserializer.readRef();
                $_obj.data = data;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                LocalState $_obj = new LocalState((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T36928);
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (data instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.data);
                } else {
                $serializer.write(this.data);
                }
                
            }
            
            // constructor just for allocation
            public LocalState(final java.lang.System[] $dummy, final x10.rtt.Type $T36928) { 
            super($dummy);
            x10.array.DistArray.LocalState.$initParams(this, $T36928);
            }
            
                private x10.rtt.Type $T36928;
                // initializer of type parameters
                public static void $initParams(final LocalState $this, final x10.rtt.Type $T36928) {
                $this.$T36928 = $T36928;
                }
                
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.Dist dist;
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.core.IndexedMemoryChunk<$T36928> data;
                
                
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
                public LocalState(final x10.rtt.Type $T36928,
                                  final x10.array.Dist d,
                                  final x10.core.IndexedMemoryChunk<$T36928> c, __1$1x10$array$DistArray$LocalState$$T36928$2 $dummy){this((java.lang.System[]) null, $T36928);
                                                                                                                                          $init(d,c, (x10.array.DistArray.LocalState.__1$1x10$array$DistArray$LocalState$$T36928$2) null);}
                
                // constructor for non-virtual call
                final public x10.array.DistArray.LocalState<$T36928> x10$array$DistArray$LocalState$$init$S(final x10.array.Dist d,
                                                                                                            final x10.core.IndexedMemoryChunk<$T36928> c, __1$1x10$array$DistArray$LocalState$$T36928$2 $dummy) { {
                                                                                                                                                                                                                         
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                                                                                                                                                                                                                         
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.dist = d;
                                                                                                                                                                                                                         this.data = c;
                                                                                                                                                                                                                         
                                                                                                                                                                                                                         
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region unused =
                                                                                                                                                                                                                           ((x10.array.Region)(d.$apply(((x10.lang.Place)(x10.lang.Runtime.home())))));
                                                                                                                                                                                                                     }
                                                                                                                                                                                                                     return this;
                                                                                                                                                                                                                     }
                
                // constructor
                public x10.array.DistArray.LocalState<$T36928> $init(final x10.array.Dist d,
                                                                     final x10.core.IndexedMemoryChunk<$T36928> c, __1$1x10$array$DistArray$LocalState$$T36928$2 $dummy){return x10$array$DistArray$LocalState$$init$S(d,c, $dummy);}
                
                
                
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public x10.array.DistArray.LocalState<$T36928>
                                                                                                           x10$array$DistArray$LocalState$$x10$array$DistArray$LocalState$this(
                                                                                                           ){
                    
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return x10.array.DistArray.LocalState.this;
                }
            // synthetic type for parameter mangling
            public abstract static class __1$1x10$array$DistArray$LocalState$$T36928$2 {}
            
        }
        
        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
/** The place-local backing storage for the DistArray */
        public x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> localHandle;
        
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
/** Can the backing storage be obtained from cachedRaw? */
        public transient boolean cachedRawValid;
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
/** Cached pointer to the backing storage */
        public transient x10.core.IndexedMemoryChunk<$T> cachedRaw;
        
        
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public x10.core.IndexedMemoryChunk<$T>
                                                                                                   raw(
                                                                                                   ){
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t36975 =
              cachedRawValid;
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t36979 =
              !(t36975);
            
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t36979) {
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> t36976 =
                  ((x10.lang.PlaceLocalHandle)(localHandle));
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$T> t36977 =
                  ((x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>>)t36976).$apply$G();
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t36978 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.DistArray.LocalState<$T>)t36977).
                                                   data));
                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.cachedRaw = ((x10.core.IndexedMemoryChunk)(t36978));
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.runtime.impl.java.Fences.storeStoreBarrier();
                
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.cachedRawValid = true;
            }
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t36980 =
              ((x10.core.IndexedMemoryChunk)(cachedRaw));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t36980;
        }
        
        
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.Array<$T>
                                                                                                    getLocalPortion(
                                                                                                    ){
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t36981 =
              ((x10.array.Dist)(dist));
            
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region regionForHere =
              ((x10.array.Region)(t36981.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t36982 =
              regionForHere.
                rect;
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t36986 =
              !(t36982);
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t36986) {
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final java.lang.String t36983 =
                  x10.rtt.Types.typeName(((x10.core.RefI)(this)));
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final java.lang.String t36984 =
                  ((t36983) + (".getLocalPortion(): local portion is not rectangular!"));
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.UnsupportedOperationException t36985 =
                  ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t36984)));
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw t36985;
            }
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t36987 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Array<$T> t36988 =
              ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(((x10.array.Region)(regionForHere)),
                                                                                              t36987, (x10.array.Array.__1$1x10$array$Array$$T$2) null)));
            
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t36988;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public static <$T>x10.array.DistArray<$T>
                                                                                                    make(
                                                                                                    final x10.rtt.Type $T,
                                                                                                    final x10.array.Dist dist){
            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t36989 =
              ((x10.array.DistArray)(new x10.array.DistArray<$T>((java.lang.System[]) null, $T).$init(((x10.array.Dist)(dist)))));
            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t36989;
        }
        
        
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
        public DistArray(final x10.rtt.Type $T,
                         final x10.array.Dist dist){this((java.lang.System[]) null, $T);
                                                        $init(dist);}
        
        // constructor for non-virtual call
        final public x10.array.DistArray<$T> x10$array$DistArray$$init$S(final x10.array.Dist dist) { {
                                                                                                             
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                                                                                                             
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.dist = dist;
                                                                                                             
                                                                                                             
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.__fieldInitializers36802();
                                                                                                             
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.fun.Fun_0_0<x10.array.DistArray.LocalState<$T>> plsInit =
                                                                                                               ((x10.core.fun.Fun_0_0)(new x10.array.DistArray.$Closure$10<$T>($T, dist)));
                                                                                                             
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t36995 =
                                                                                                               ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
                                                                                                             
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> t36996 =
                                                                                                               x10.lang.PlaceLocalHandle.<x10.array.DistArray.LocalState<$T>>makeFlat__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, $T), ((x10.array.PlaceGroup)(t36995)),
                                                                                                                                                                                                                         ((x10.core.fun.Fun_0_0)(plsInit)));
                                                                                                             
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.localHandle = ((x10.lang.PlaceLocalHandle)(t36996));
                                                                                                         }
                                                                                                         return this;
                                                                                                         }
        
        // constructor
        public x10.array.DistArray<$T> $init(final x10.array.Dist dist){return x10$array$DistArray$$init$S(dist);}
        
        
        
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
        public DistArray(final x10.rtt.Type $T,
                         final x10.io.SerialData sd){this((java.lang.System[]) null, $T);
                                                         $init(sd);}
        
        // constructor for non-virtual call
        final public x10.array.DistArray<$T> x10$array$DistArray$$init$S(final x10.io.SerialData sd) {x10$array$DistArray$init_for_reflection(sd);
                                                                                                          
                                                                                                          return this;
                                                                                                          }
        public void x10$array$DistArray$init_for_reflection(x10.io.SerialData sd) {
             {
                
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final java.lang.Object t36997 =
                  ((java.lang.Object)(sd.
                                        data));
                
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> plh =
                  ((x10.lang.PlaceLocalHandle)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.lang.PlaceLocalHandle.$RTT, x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, $T)),t36997));
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$T> t36998 =
                  ((x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>>)plh).$apply$G();
                
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist d =
                  ((x10.array.DistArray.LocalState<$T>)t36998).
                    dist;
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.dist = d;
                
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.__fieldInitializers36802();
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.localHandle = ((x10.lang.PlaceLocalHandle)(plh));
            }}
            
        // constructor
        public x10.array.DistArray<$T> $init(final x10.io.SerialData sd){return x10$array$DistArray$$init$S(sd);}
        
        
        
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.io.SerialData
                                                                                                    serialize(
                                                                                                    ){
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> t36999 =
              ((x10.lang.PlaceLocalHandle)(localHandle));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.io.SerialData t37000 =
              ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).$init(((java.lang.Object)(t36999)),
                                                                                          ((x10.io.SerialData)(null)))));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37000;
        }
        
        
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public static <$T>x10.array.DistArray<$T>
                                                                                                    make__1$1x10$array$Point$3x10$array$DistArray$$T$2(
                                                                                                    final x10.rtt.Type $T,
                                                                                                    final x10.array.Dist dist,
                                                                                                    final x10.core.fun.Fun_0_1<x10.array.Point,$T> init){
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37001 =
              ((x10.array.DistArray)(new x10.array.DistArray<$T>((java.lang.System[]) null, $T).$init(((x10.array.Dist)(dist)),
                                                                                                      ((x10.core.fun.Fun_0_1)(init)), (x10.array.DistArray.__1$1x10$array$Point$3x10$array$DistArray$$T$2) null)));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37001;
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
        public DistArray(final x10.rtt.Type $T,
                         final x10.array.Dist dist,
                         final x10.core.fun.Fun_0_1<x10.array.Point,$T> init, __1$1x10$array$Point$3x10$array$DistArray$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                                         $init(dist,init, (x10.array.DistArray.__1$1x10$array$Point$3x10$array$DistArray$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.DistArray<$T> x10$array$DistArray$$init$S(final x10.array.Dist dist,
                                                                         final x10.core.fun.Fun_0_1<x10.array.Point,$T> init, __1$1x10$array$Point$3x10$array$DistArray$$T$2 $dummy) { {
                                                                                                                                                                                              
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                                                                                                                                                                                              
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.dist = dist;
                                                                                                                                                                                              
                                                                                                                                                                                              
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.__fieldInitializers36802();
                                                                                                                                                                                              
//#line 170 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.fun.Fun_0_0<x10.array.DistArray.LocalState<$T>> plsInit =
                                                                                                                                                                                                ((x10.core.fun.Fun_0_0)(new x10.array.DistArray.$Closure$11<$T>($T, dist,
                                                                                                                                                                                                                                                                init, (x10.array.DistArray.$Closure$11.__1$1x10$array$Point$3x10$array$DistArray$$Closure$11$$T$2) null)));
                                                                                                                                                                                              
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t37013 =
                                                                                                                                                                                                ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
                                                                                                                                                                                              
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> t37014 =
                                                                                                                                                                                                x10.lang.PlaceLocalHandle.<x10.array.DistArray.LocalState<$T>>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, $T), ((x10.array.PlaceGroup)(t37013)),
                                                                                                                                                                                                                                                                                                      ((x10.core.fun.Fun_0_0)(plsInit)));
                                                                                                                                                                                              
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.localHandle = ((x10.lang.PlaceLocalHandle)(t37014));
                                                                                                                                                                                          }
                                                                                                                                                                                          return this;
                                                                                                                                                                                          }
        
        // constructor
        public x10.array.DistArray<$T> $init(final x10.array.Dist dist,
                                             final x10.core.fun.Fun_0_1<x10.array.Point,$T> init, __1$1x10$array$Point$3x10$array$DistArray$$T$2 $dummy){return x10$array$DistArray$$init$S(dist,init, $dummy);}
        
        
        
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public static <$T>x10.array.DistArray<$T>
                                                                                                    make__1x10$array$DistArray$$T(
                                                                                                    final x10.rtt.Type $T,
                                                                                                    final x10.array.Dist dist,
                                                                                                    final $T init){
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37015 =
              ((x10.array.DistArray)(new x10.array.DistArray<$T>((java.lang.System[]) null, $T).$init(((x10.array.Dist)(dist)),
                                                                                                      (($T)(init)), (x10.array.DistArray.__1x10$array$DistArray$$T) null)));
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37015;
        }
        
        
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
        public DistArray(final x10.rtt.Type $T,
                         final x10.array.Dist dist,
                         final $T init, __1x10$array$DistArray$$T $dummy){this((java.lang.System[]) null, $T);
                                                                              $init(dist,init, (x10.array.DistArray.__1x10$array$DistArray$$T) null);}
        
        // constructor for non-virtual call
        final public x10.array.DistArray<$T> x10$array$DistArray$$init$S(final x10.array.Dist dist,
                                                                         final $T init, __1x10$array$DistArray$$T $dummy) { {
                                                                                                                                   
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                                                                                                                                   
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.dist = dist;
                                                                                                                                   
                                                                                                                                   
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.__fieldInitializers36802();
                                                                                                                                   
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.fun.Fun_0_0<x10.array.DistArray.LocalState<$T>> plsInit =
                                                                                                                                     ((x10.core.fun.Fun_0_0)(new x10.array.DistArray.$Closure$12<$T>($T, dist,
                                                                                                                                                                                                     init, (x10.array.DistArray.$Closure$12.__1x10$array$DistArray$$Closure$12$$T) null)));
                                                                                                                                   
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t37026 =
                                                                                                                                     ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
                                                                                                                                   
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> t37027 =
                                                                                                                                     x10.lang.PlaceLocalHandle.<x10.array.DistArray.LocalState<$T>>makeFlat__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, $T), ((x10.array.PlaceGroup)(t37026)),
                                                                                                                                                                                                                                               ((x10.core.fun.Fun_0_0)(plsInit)));
                                                                                                                                   
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.localHandle = ((x10.lang.PlaceLocalHandle)(t37027));
                                                                                                                               }
                                                                                                                               return this;
                                                                                                                               }
        
        // constructor
        public x10.array.DistArray<$T> $init(final x10.array.Dist dist,
                                             final $T init, __1x10$array$DistArray$$T $dummy){return x10$array$DistArray$$init$S(dist,init, $dummy);}
        
        
        
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
        public DistArray(final x10.rtt.Type $T,
                         final x10.array.DistArray<$T> a,
                         final x10.array.Dist d, __0$1x10$array$DistArray$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                           $init(a,d, (x10.array.DistArray.__0$1x10$array$DistArray$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.DistArray<$T> x10$array$DistArray$$init$S(final x10.array.DistArray<$T> a,
                                                                         final x10.array.Dist d, __0$1x10$array$DistArray$$T$2 $dummy) { {
                                                                                                                                                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                                                                                                                                                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.dist = d;
                                                                                                                                                
                                                                                                                                                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.__fieldInitializers36802();
                                                                                                                                                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.fun.Fun_0_0<x10.array.DistArray.LocalState<$T>> plsInit =
                                                                                                                                                  ((x10.core.fun.Fun_0_0)(new x10.array.DistArray.$Closure$13<$T>($T, a,
                                                                                                                                                                                                                  d, (x10.array.DistArray.$Closure$13.__0$1x10$array$DistArray$$Closure$13$$T$2) null)));
                                                                                                                                                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t37032 =
                                                                                                                                                  ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
                                                                                                                                                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> t37033 =
                                                                                                                                                  x10.lang.PlaceLocalHandle.<x10.array.DistArray.LocalState<$T>>makeFlat__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, $T), ((x10.array.PlaceGroup)(t37032)),
                                                                                                                                                                                                                                                            ((x10.core.fun.Fun_0_0)(plsInit)));
                                                                                                                                                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.localHandle = ((x10.lang.PlaceLocalHandle)(t37033));
                                                                                                                                            }
                                                                                                                                            return this;
                                                                                                                                            }
        
        // constructor
        public x10.array.DistArray<$T> $init(final x10.array.DistArray<$T> a,
                                             final x10.array.Dist d, __0$1x10$array$DistArray$$T$2 $dummy){return x10$array$DistArray$$init$S(a,d, $dummy);}
        
        
        
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
        public DistArray(final x10.rtt.Type $T,
                         final x10.array.Dist d,
                         final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> pls, __1$1x10$array$DistArray$LocalState$1x10$array$DistArray$$T$2$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                                                                              $init(d,pls, (x10.array.DistArray.__1$1x10$array$DistArray$LocalState$1x10$array$DistArray$$T$2$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.DistArray<$T> x10$array$DistArray$$init$S(final x10.array.Dist d,
                                                                         final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> pls, __1$1x10$array$DistArray$LocalState$1x10$array$DistArray$$T$2$2 $dummy) { {
                                                                                                                                                                                                                                   
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                                                                                                                                                                                                                                   
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.dist = d;
                                                                                                                                                                                                                                   
                                                                                                                                                                                                                                   
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.__fieldInitializers36802();
                                                                                                                                                                                                                                   
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.localHandle = ((x10.lang.PlaceLocalHandle)(pls));
                                                                                                                                                                                                                               }
                                                                                                                                                                                                                               return this;
                                                                                                                                                                                                                               }
        
        // constructor
        public x10.array.DistArray<$T> $init(final x10.array.Dist d,
                                             final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> pls, __1$1x10$array$DistArray$LocalState$1x10$array$DistArray$$T$2$2 $dummy){return x10$array$DistArray$$init$S(d,pls, $dummy);}
        
        
        
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $apply$G(
                                                                                                    final x10.array.Point pt){
            
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37034 =
              ((x10.array.Dist)(dist));
            
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37034.offset$O(((x10.array.Point)(pt)));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37035 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37036 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t37035))).$apply$G(((int)(offset)))));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37036;
        }
        
        
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $apply$G(
                                                                                                    final int i0){
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37038 =
              ((x10.array.Dist)(dist));
            
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37038.offset$O((int)(i0));
            
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37039 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37040 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t37039))).$apply$G(((int)(offset)))));
            
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37040;
        }
        
        
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $apply$G(
                                                                                                    final int i0,
                                                                                                    final int i1){
            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37042 =
              ((x10.array.Dist)(dist));
            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37042.offset$O((int)(i0),
                              (int)(i1));
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37043 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37044 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t37043))).$apply$G(((int)(offset)))));
            
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37044;
        }
        
        
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $apply$G(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2){
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37046 =
              ((x10.array.Dist)(dist));
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37046.offset$O((int)(i0),
                              (int)(i1),
                              (int)(i2));
            
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37047 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37048 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t37047))).$apply$G(((int)(offset)))));
            
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37048;
        }
        
        
//#line 332 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $apply$G(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2,
                                                                                                    final int i3){
            
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37050 =
              ((x10.array.Dist)(dist));
            
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37050.offset$O((int)(i0),
                              (int)(i1),
                              (int)(i2),
                              (int)(i3));
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37051 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37052 =
              (($T)((((x10.core.IndexedMemoryChunk<$T>)(t37051))).$apply$G(((int)(offset)))));
            
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37052;
        }
        
        
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $set__1x10$array$DistArray$$T$G(
                                                                                                    final x10.array.Point pt,
                                                                                                    final $T v){
            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37053 =
              ((x10.array.Dist)(dist));
            
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37053.offset$O(((x10.array.Point)(pt)));
            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37054 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t37054))).$set(((int)(offset)), v);
            
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return v;
        }
        
        
//#line 371 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $set__1x10$array$DistArray$$T$G(
                                                                                                    final int i0,
                                                                                                    final $T v){
            
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37056 =
              ((x10.array.Dist)(dist));
            
//#line 372 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37056.offset$O((int)(i0));
            
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37057 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 373 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t37057))).$set(((int)(offset)), v);
            
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return v;
        }
        
        
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $set__2x10$array$DistArray$$T$G(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final $T v){
            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37059 =
              ((x10.array.Dist)(dist));
            
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37059.offset$O((int)(i0),
                              (int)(i1));
            
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37060 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t37060))).$set(((int)(offset)), v);
            
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return v;
        }
        
        
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $set__3x10$array$DistArray$$T$G(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2,
                                                                                                    final $T v){
            
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37062 =
              ((x10.array.Dist)(dist));
            
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37062.offset$O((int)(i0),
                              (int)(i1),
                              (int)(i2));
            
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37063 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t37063))).$set(((int)(offset)), v);
            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return v;
        }
        
        
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    $set__4x10$array$DistArray$$T$G(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2,
                                                                                                    final int i3,
                                                                                                    final $T v){
            
//#line 438 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37065 =
              ((x10.array.Dist)(dist));
            
//#line 438 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset =
              t37065.offset$O((int)(i0),
                              (int)(i1),
                              (int)(i2),
                              (int)(i3));
            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37066 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),this.raw()));
            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(t37066))).$set(((int)(offset)), v);
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return v;
        }
        
        
//#line 460 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.DistArray<$T>
                                                                                                    restriction(
                                                                                                    final x10.array.Dist d){
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37067 =
              ((x10.array.DistArray)(new x10.array.DistArray<$T>((java.lang.System[]) null, $T).$init(((x10.array.DistArray)(this)),
                                                                                                      ((x10.array.Dist)(d)), (x10.array.DistArray.__0$1x10$array$DistArray$$T$2) null)));
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> __desugarer__var__10__36962 =
              ((x10.array.DistArray)(((x10.array.DistArray<$T>)
                                       t37067)));
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.array.DistArray<$T> ret36963 =
               null;
            
//#line 461 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37232 =
              ((x10.array.Dist)(((x10.array.DistArray<$T>)__desugarer__var__10__36962).
                                  dist));
            
//#line 461 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t37233 =
              ((x10.array.Region)(t37232.
                                    region));
            
//#line 461 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37234 =
              t37233.
                rank;
            
//#line 461 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37235 =
              ((x10.array.Dist)(x10.array.DistArray.this.
                                  dist));
            
//#line 461 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t37236 =
              ((x10.array.Region)(t37235.
                                    region));
            
//#line 461 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37237 =
              t37236.
                rank;
            
//#line 461 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37238 =
              ((int) t37234) ==
            ((int) t37237);
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37239 =
              !(t37238);
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37239) {
                
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37240 =
                  true;
                
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37240) {
                    
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FailedDynamicCheckException t37241 =
                      new x10.lang.FailedDynamicCheckException("x10.array.DistArray[T]{self.dist.region.rank==this(:x10.array.DistArray).dist.region.rank}");
                    
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw t37241;
                }
            }
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
ret36963 = ((x10.array.DistArray)(__desugarer__var__10__36962));
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37078 =
              ((x10.array.DistArray)(ret36963));
            
//#line 461 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37078;
        }
        
        
//#line 472 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.DistArray<$T>
                                                                                                    restriction(
                                                                                                    final x10.array.Region r){
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37079 =
              ((x10.array.Dist)(dist));
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37080 =
              ((x10.array.Dist)(t37079.restriction(((x10.array.Region)(r)))));
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist __desugarer__var__11__36965 =
              ((x10.array.Dist)(((x10.array.Dist)
                                  t37080)));
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.array.Dist ret36966 =
               null;
            
//#line 473 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t37242 =
              ((x10.array.Region)(__desugarer__var__11__36965.
                                    region));
            
//#line 473 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37243 =
              t37242.
                rank;
            
//#line 473 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37244 =
              ((x10.array.Dist)(x10.array.DistArray.this.
                                  dist));
            
//#line 473 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t37245 =
              ((x10.array.Region)(t37244.
                                    region));
            
//#line 473 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37246 =
              t37245.
                rank;
            
//#line 473 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37247 =
              ((int) t37243) ==
            ((int) t37246);
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37248 =
              !(t37247);
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37248) {
                
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37249 =
                  true;
                
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37249) {
                    
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FailedDynamicCheckException t37250 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Dist{self.region.rank==this(:x10.array.DistArray).dist.region.rank}");
                    
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw t37250;
                }
            }
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
ret36966 = ((x10.array.Dist)(__desugarer__var__11__36965));
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37090 =
              ((x10.array.Dist)(ret36966));
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37091 =
              ((x10.array.DistArray)(this.restriction(((x10.array.Dist)(t37090)))));
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37091;
        }
        
        
//#line 484 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.DistArray<$T>
                                                                                                    restriction(
                                                                                                    final x10.lang.Place p){
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37092 =
              ((x10.array.Dist)(dist));
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37093 =
              ((x10.array.Dist)(t37092.restriction(((x10.lang.Place)(p)))));
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist __desugarer__var__12__36968 =
              ((x10.array.Dist)(((x10.array.Dist)
                                  t37093)));
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.array.Dist ret36969 =
               null;
            
//#line 485 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t37251 =
              ((x10.array.Region)(__desugarer__var__12__36968.
                                    region));
            
//#line 485 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37252 =
              t37251.
                rank;
            
//#line 485 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37253 =
              ((x10.array.Dist)(x10.array.DistArray.this.
                                  dist));
            
//#line 485 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t37254 =
              ((x10.array.Region)(t37253.
                                    region));
            
//#line 485 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37255 =
              t37254.
                rank;
            
//#line 485 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37256 =
              ((int) t37252) ==
            ((int) t37255);
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37257 =
              !(t37256);
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37257) {
                
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37258 =
                  true;
                
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37258) {
                    
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FailedDynamicCheckException t37259 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Dist{self.region.rank==this(:x10.array.DistArray).dist.region.rank}");
                    
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw t37259;
                }
            }
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
ret36969 = ((x10.array.Dist)(__desugarer__var__12__36968));
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37103 =
              ((x10.array.Dist)(ret36969));
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37104 =
              ((x10.array.DistArray)(this.restriction(((x10.array.Dist)(t37103)))));
            
//#line 485 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37104;
        }
        
        
//#line 496 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.DistArray<$T>
                                                                                                    $bar(
                                                                                                    final x10.array.Region r){
            
//#line 496 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37105 =
              ((x10.array.DistArray)(this.restriction(((x10.array.Region)(r)))));
            
//#line 496 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37105;
        }
        
        
//#line 506 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.DistArray<$T>
                                                                                                    $bar(
                                                                                                    final x10.lang.Place p){
            
//#line 506 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$T> t37106 =
              ((x10.array.DistArray)(this.restriction(((x10.lang.Place)(p)))));
            
//#line 506 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37106;
        }
        
        
//#line 518 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public void
                                                                                                    fill__0x10$array$DistArray$$T(
                                                                                                    final $T v){
            {
                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FinishState x10$__var0 =
                  x10.lang.Runtime.startFinish();
                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
try {try {{
                    {
                        
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37108 =
                          ((x10.array.Dist)(dist));
                        
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37109 =
                          t37108.places();
                        
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.lang.Place> where36937 =
                          t37109.iterator();
                        
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37115 =
                              ((x10.lang.Iterator<x10.lang.Place>)where36937).hasNext$O();
                            
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37115)) {
                                
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Place where37265 =
                              ((x10.lang.Place)(((x10.lang.Iterator<x10.lang.Place>)where36937).next$G()));
                            
//#line 520 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(where37265)),
                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new x10.array.DistArray.$Closure$14<$T>($T, ((x10.array.DistArray)(this)),
                                                                                                                                                                                                                    dist,
                                                                                                                                                                                                                    v, (x10.array.DistArray.$Closure$14.__0$1x10$array$DistArray$$Closure$14$$T$2__2x10$array$DistArray$$Closure$14$$T) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 519 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var0)));
                 }}
                }
            }
        
        
//#line 539 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public <$U>x10.array.DistArray<$U>
                                                                                                    map__0$1x10$array$DistArray$$T$3x10$array$DistArray$$U$2(
                                                                                                    final x10.rtt.Type $U,
                                                                                                    final x10.core.fun.Fun_0_1 op){
            
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t37132 =
              ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
            
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.fun.Fun_0_0<x10.array.DistArray.LocalState<$U>> t37133 =
              ((x10.core.fun.Fun_0_0)(new x10.array.DistArray.$Closure$15<$T, $U>($T, $U, ((x10.array.DistArray)(this)),
                                                                                  dist,
                                                                                  op, (x10.array.DistArray.$Closure$15.__0$1x10$array$DistArray$$Closure$15$$T$2__2$1x10$array$DistArray$$Closure$15$$T$3x10$array$DistArray$$Closure$15$$U$2) null)));
            
//#line 540 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$U>> plh =
              x10.lang.PlaceLocalHandle.<x10.array.DistArray.LocalState<$U>>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, $U), ((x10.array.PlaceGroup)(t37132)),
                                                                                                                    ((x10.core.fun.Fun_0_0)(t37133)));
            
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37134 =
              ((x10.array.Dist)(dist));
            
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$U> t37135 =
              ((x10.array.DistArray)(new x10.array.DistArray<$U>((java.lang.System[]) null, $U).$init(((x10.array.Dist)(t37134)),
                                                                                                      ((x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$U>>)(plh)), (x10.array.DistArray.__1$1x10$array$DistArray$LocalState$1x10$array$DistArray$$T$2$2) null)));
            
//#line 555 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37135;
        }
        
        
//#line 568 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public <$U>x10.array.DistArray<$U>
                                                                                                    map__0$1x10$array$DistArray$$U$2__1$1x10$array$DistArray$$T$3x10$array$DistArray$$U$2(
                                                                                                    final x10.rtt.Type $U,
                                                                                                    final x10.array.DistArray dst,
                                                                                                    final x10.core.fun.Fun_0_1 op){
            {
                
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FinishState x10$__var1 =
                  x10.lang.Runtime.startFinish();
                
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
try {try {{
                    {
                        
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37137 =
                          ((x10.array.Dist)(dist));
                        
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37138 =
                          t37137.places();
                        
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.lang.Place> where36943 =
                          t37138.iterator();
                        
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37145 =
                              ((x10.lang.Iterator<x10.lang.Place>)where36943).hasNext$O();
                            
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37145)) {
                                
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 570 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Place where37283 =
                              ((x10.lang.Place)(((x10.lang.Iterator<x10.lang.Place>)where36943).next$G()));
                            
//#line 571 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(where37283)),
                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new x10.array.DistArray.$Closure$16<$T, $U>($T, $U, ((x10.array.DistArray)(this)),
                                                                                                                                                                                                                        dist,
                                                                                                                                                                                                                        dst,
                                                                                                                                                                                                                        op, (x10.array.DistArray.$Closure$16.__0$1x10$array$DistArray$$Closure$16$$T$2__2$1x10$array$DistArray$$Closure$16$$U$2__3$1x10$array$DistArray$$Closure$16$$T$3x10$array$DistArray$$Closure$16$$U$2) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 569 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var1)));
                 }}
                }
            
//#line 582 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return dst;
            }
        
        
//#line 597 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public <$U>x10.array.DistArray<$U>
                                                                                                    map__0$1x10$array$DistArray$$U$2__2$1x10$array$DistArray$$T$3x10$array$DistArray$$U$2(
                                                                                                    final x10.rtt.Type $U,
                                                                                                    final x10.array.DistArray dst,
                                                                                                    final x10.array.Region filter,
                                                                                                    final x10.core.fun.Fun_0_1 op){
            {
                
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FinishState x10$__var2 =
                  x10.lang.Runtime.startFinish();
                
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
try {try {{
                    {
                        
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37147 =
                          ((x10.array.Dist)(dist));
                        
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37148 =
                          t37147.places();
                        
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.lang.Place> where36947 =
                          t37148.iterator();
                        
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37155 =
                              ((x10.lang.Iterator<x10.lang.Place>)where36947).hasNext$O();
                            
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37155)) {
                                
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 599 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Place where37295 =
                              ((x10.lang.Place)(((x10.lang.Iterator<x10.lang.Place>)where36947).next$G()));
                            
//#line 600 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(where37295)),
                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new x10.array.DistArray.$Closure$17<$T, $U>($T, $U, ((x10.array.DistArray)(this)),
                                                                                                                                                                                                                        dist,
                                                                                                                                                                                                                        filter,
                                                                                                                                                                                                                        dst,
                                                                                                                                                                                                                        op, (x10.array.DistArray.$Closure$17.__0$1x10$array$DistArray$$Closure$17$$T$2__3$1x10$array$DistArray$$Closure$17$$U$2__4$1x10$array$DistArray$$Closure$17$$T$3x10$array$DistArray$$Closure$17$$U$2) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 598 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var2)));
                 }}
                }
            
//#line 612 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return dst;
            }
        
        
//#line 625 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public <$S, $U>x10.array.DistArray<$S>
                                                                                                    map__0$1x10$array$DistArray$$U$2__1$1x10$array$DistArray$$T$3x10$array$DistArray$$U$3x10$array$DistArray$$S$2(
                                                                                                    final x10.rtt.Type $S,
                                                                                                    final x10.rtt.Type $U,
                                                                                                    final x10.array.DistArray src,
                                                                                                    final x10.core.fun.Fun_0_2 op){
            
//#line 626 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t37173 =
              ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
            
//#line 626 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.fun.Fun_0_0<x10.array.DistArray.LocalState<$S>> t37174 =
              ((x10.core.fun.Fun_0_0)(new x10.array.DistArray.$Closure$18<$T, $S, $U>($T, $S, $U, ((x10.array.DistArray)(this)),
                                                                                      dist,
                                                                                      src,
                                                                                      op, (x10.array.DistArray.$Closure$18.__0$1x10$array$DistArray$$Closure$18$$T$2__2$1x10$array$DistArray$$Closure$18$$U$2__3$1x10$array$DistArray$$Closure$18$$T$3x10$array$DistArray$$Closure$18$$U$3x10$array$DistArray$$Closure$18$$S$2) null)));
            
//#line 626 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$S>> plh =
              x10.lang.PlaceLocalHandle.<x10.array.DistArray.LocalState<$S>>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, $S), ((x10.array.PlaceGroup)(t37173)),
                                                                                                                    ((x10.core.fun.Fun_0_0)(t37174)));
            
//#line 642 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37175 =
              ((x10.array.Dist)(dist));
            
//#line 642 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray<$S> t37176 =
              ((x10.array.DistArray)(new x10.array.DistArray<$S>((java.lang.System[]) null, $S).$init(((x10.array.Dist)(t37175)),
                                                                                                      ((x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$S>>)(plh)), (x10.array.DistArray.__1$1x10$array$DistArray$LocalState$1x10$array$DistArray$$T$2$2) null)));
            
//#line 642 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37176;
        }
        
        
//#line 656 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public <$S, $U>x10.array.DistArray<$S>
                                                                                                    map__0$1x10$array$DistArray$$S$2__1$1x10$array$DistArray$$U$2__2$1x10$array$DistArray$$T$3x10$array$DistArray$$U$3x10$array$DistArray$$S$2(
                                                                                                    final x10.rtt.Type $S,
                                                                                                    final x10.rtt.Type $U,
                                                                                                    final x10.array.DistArray dst,
                                                                                                    final x10.array.DistArray src,
                                                                                                    final x10.core.fun.Fun_0_2 op){
            {
                
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FinishState x10$__var3 =
                  x10.lang.Runtime.startFinish();
                
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
try {try {{
                    {
                        
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37178 =
                          ((x10.array.Dist)(dist));
                        
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37179 =
                          t37178.places();
                        
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.lang.Place> where36953 =
                          t37179.iterator();
                        
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37187 =
                              ((x10.lang.Iterator<x10.lang.Place>)where36953).hasNext$O();
                            
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37187)) {
                                
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 658 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Place where37317 =
                              ((x10.lang.Place)(((x10.lang.Iterator<x10.lang.Place>)where36953).next$G()));
                            
//#line 659 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(where37317)),
                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new x10.array.DistArray.$Closure$19<$T, $S, $U>($T, $S, $U, ((x10.array.DistArray)(this)),
                                                                                                                                                                                                                            dist,
                                                                                                                                                                                                                            src,
                                                                                                                                                                                                                            dst,
                                                                                                                                                                                                                            op, (x10.array.DistArray.$Closure$19.$_c534dd73) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 657 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var3)));
                 }}
                }
            
//#line 671 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return dst;
            }
        
        
//#line 686 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public <$S, $U>x10.array.DistArray<$S>
                                                                                                    map__0$1x10$array$DistArray$$S$2__1$1x10$array$DistArray$$U$2__3$1x10$array$DistArray$$T$3x10$array$DistArray$$U$3x10$array$DistArray$$S$2(
                                                                                                    final x10.rtt.Type $S,
                                                                                                    final x10.rtt.Type $U,
                                                                                                    final x10.array.DistArray dst,
                                                                                                    final x10.array.DistArray src,
                                                                                                    final x10.array.Region filter,
                                                                                                    final x10.core.fun.Fun_0_2 op){
            {
                
//#line 687 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 687 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FinishState x10$__var4 =
                  x10.lang.Runtime.startFinish();
                
//#line 687 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
try {try {{
                    {
                        
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37189 =
                          ((x10.array.Dist)(dist));
                        
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37190 =
                          t37189.places();
                        
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.lang.Place> where36957 =
                          t37190.iterator();
                        
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37198 =
                              ((x10.lang.Iterator<x10.lang.Place>)where36957).hasNext$O();
                            
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37198)) {
                                
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 688 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Place where37331 =
                              ((x10.lang.Place)(((x10.lang.Iterator<x10.lang.Place>)where36957).next$G()));
                            
//#line 689 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(where37331)),
                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new x10.array.DistArray.$Closure$20<$T, $S, $U>($T, $S, $U, ((x10.array.DistArray)(this)),
                                                                                                                                                                                                                            dist,
                                                                                                                                                                                                                            filter,
                                                                                                                                                                                                                            src,
                                                                                                                                                                                                                            dst,
                                                                                                                                                                                                                            op, (x10.array.DistArray.$Closure$20.$_26b8af68) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 687 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 687 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 687 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var4)));
                 }}
                }
            
//#line 702 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return dst;
            }
        
        
//#line 717 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public $T
                                                                                                    reduce__0$1x10$array$DistArray$$T$3x10$array$DistArray$$T$3x10$array$DistArray$$T$2__1x10$array$DistArray$$T$G(
                                                                                                    final x10.core.fun.Fun_0_2 op,
                                                                                                    final $T unit){
            
//#line 717 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37199 =
              (($T)(this.<$T>reduce__0$1x10$array$DistArray$$U$3x10$array$DistArray$$T$3x10$array$DistArray$$U$2__1$1x10$array$DistArray$$U$3x10$array$DistArray$$U$3x10$array$DistArray$$U$2__2x10$array$DistArray$$U$G($T, ((x10.core.fun.Fun_0_2)(op)),
                                                                                                                                                                                                                         ((x10.core.fun.Fun_0_2)(op)),
                                                                                                                                                                                                                         (($T)(unit)))));
            
//#line 717 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37199;
        }
        
        
//#line 731 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public <$U>$U
                                                                                                    reduce__0$1x10$array$DistArray$$U$3x10$array$DistArray$$T$3x10$array$DistArray$$U$2__1$1x10$array$DistArray$$U$3x10$array$DistArray$$U$3x10$array$DistArray$$U$2__2x10$array$DistArray$$U$G(
                                                                                                    final x10.rtt.Type $U,
                                                                                                    final x10.core.fun.Fun_0_2 lop,
                                                                                                    final x10.core.fun.Fun_0_2 gop,
                                                                                                    final $U unit){
            
//#line 732 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.Anonymous$29053<$U, $T> reducer =
              ((x10.array.DistArray.Anonymous$29053)(new x10.array.DistArray.Anonymous$29053<$U, $T>((java.lang.System[]) null, $U, $T).$init(this,
                                                                                                                                              unit,
                                                                                                                                              gop, (x10.array.DistArray.Anonymous$29053.$_8fec24f7) null)));
            
//#line 737 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U result;
            {
                
//#line 737 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.FinishState x10$__var5 =
                  ((x10.lang.FinishState)(x10.lang.Runtime.<$U>startCollectingFinish__0$1x10$lang$Runtime$$T$2($U, ((x10.lang.Reducible)(reducer)))));
                
//#line 737 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
try {try {{
                    {
                        
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37201 =
                          ((x10.array.Dist)(dist));
                        
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37202 =
                          t37201.places();
                        
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.lang.Place> where36961 =
                          t37202.iterator();
                        
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37212 =
                              ((x10.lang.Iterator<x10.lang.Place>)where36961).hasNext$O();
                            
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37212)) {
                                
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 738 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Place where37346 =
                              ((x10.lang.Place)(((x10.lang.Iterator<x10.lang.Place>)where36961).next$G()));
                            
//#line 739 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(where37346)),
                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new x10.array.DistArray.$Closure$21<$T, $U>($T, $U, ((x10.array.DistArray)(this)),
                                                                                                                                                                                                                        dist,
                                                                                                                                                                                                                        unit,
                                                                                                                                                                                                                        lop, (x10.array.DistArray.$Closure$21.__0$1x10$array$DistArray$$Closure$21$$T$2__2x10$array$DistArray$$Closure$21$$U__3$1x10$array$DistArray$$Closure$21$$U$3x10$array$DistArray$$Closure$21$$T$3x10$array$DistArray$$Closure$21$$U$2) null))));
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__0__) {
                    
//#line 737 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__0__)));
                    
//#line 737 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
throw new x10.lang.Exception();
                }finally {{
                     
//#line 737 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
result = (($U)(x10.lang.Runtime.<$U>stopCollectingFinish$G($U, ((x10.lang.FinishState)(x10$__var5)))));
                 }}
                }
            
//#line 751 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return result;
            }
        
        
//#line 755 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public java.lang.String
                                                                                                    toString(
                                                                                                    ){
            
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37213 =
              ((x10.array.Dist)(dist));
            
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final java.lang.String t37214 =
              (("DistArray(") + (t37213));
            
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final java.lang.String t37215 =
              ((t37214) + (")"));
            
//#line 756 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37215;
        }
        
        
//#line 765 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                    iterator(
                                                                                                    ){
            
//#line 765 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region t37216 =
              ((x10.array.Region)(this.region()));
            
//#line 765 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> t37217 =
              t37216.iterator();
            
//#line 765 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> t37218 =
              ((x10.lang.Iterator<x10.array.Point>)
                t37217);
            
//#line 765 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37218;
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public x10.array.DistArray<$T>
                                                                                                   x10$array$DistArray$$x10$array$DistArray$this(
                                                                                                   ){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return x10.array.DistArray.this;
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final public void
                                                                                                   __fieldInitializers36802(
                                                                                                   ){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.cachedRawValid = false;
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37219 =
              (x10.core.IndexedMemoryChunk<$T>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.cachedRaw = t37219;
        }
        
        
//#line 732 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
@x10.core.X10Generated final public static class Anonymous$29053<$U, $T36929> extends x10.core.Ref implements x10.lang.Reducible, x10.x10rt.X10JavaSerializable
                                                                                                  {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Anonymous$29053.class);
            
            public static final x10.rtt.RuntimeType<Anonymous$29053> $RTT = x10.rtt.NamedType.<Anonymous$29053> make(
            "x10.array.DistArray.Anonymous$29053", /* base class */Anonymous$29053.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Reducible.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $U;if (i ==1)return $T36929;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(Anonymous$29053 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Anonymous$29053.class + " calling"); } 
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$T36929 = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.unit = $deserializer.readRef();
                x10.core.fun.Fun_0_2 gop = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.gop = gop;
                x10.array.DistArray out$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                Anonymous$29053 $_obj = new Anonymous$29053((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T36929);
                if (unit instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.unit);
                } else {
                $serializer.write(this.unit);
                }
                if (gop instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.gop);
                } else {
                $serializer.write(this.gop);
                }
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public Anonymous$29053(final java.lang.System[] $dummy, final x10.rtt.Type $U, final x10.rtt.Type $T36929) { 
            super($dummy);
            x10.array.DistArray.Anonymous$29053.$initParams(this, $U, $T36929);
            }
            // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return $apply__0x10$array$DistArray$Anonymous$29053$$U__1x10$array$DistArray$Anonymous$29053$$U$G(($U)a1, ($U)a2);
            }
            
                private x10.rtt.Type $U;
                private x10.rtt.Type $T36929;
                // initializer of type parameters
                public static void $initParams(final Anonymous$29053 $this, final x10.rtt.Type $U, final x10.rtt.Type $T36929) {
                $this.$U = $U;
                $this.$T36929 = $T36929;
                }
                
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.array.DistArray<$T36929> out$;
                
//#line 731 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public $U unit;
                
//#line 731 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public x10.core.fun.Fun_0_2<$U,$U,$U> gop;
                
                
//#line 733 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public $U
                                                                                                            zero$G(
                                                                                                            ){
                    
//#line 733 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37220 =
                      (($U)(x10.array.DistArray.Anonymous$29053.this.
                              unit));
                    
//#line 733 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37220;
                }
                
                
//#line 734 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
public $U
                                                                                                            $apply__0x10$array$DistArray$Anonymous$29053$$U__1x10$array$DistArray$Anonymous$29053$$U$G(
                                                                                                            final $U a,
                                                                                                            final $U b){
                    
//#line 734 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.fun.Fun_0_2<$U,$U,$U> t37221 =
                      x10.array.DistArray.Anonymous$29053.this.
                        gop;
                    
//#line 734 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37222 =
                      (($U)((($U)
                              ((x10.core.fun.Fun_0_2<$U,$U,$U>)t37221).$apply(a,$U,
                                                                              b,$U))));
                    
//#line 734 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37222;
                }
                
                
//#line 732 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
// creation method for java code (1-phase java constructor)
                public Anonymous$29053(final x10.rtt.Type $U,
                                       final x10.rtt.Type $T36929,
                                       final x10.array.DistArray<$T36929> out$,
                                       final $U unit,
                                       final x10.core.fun.Fun_0_2<$U,$U,$U> gop, $_8fec24f7 $dummy){this((java.lang.System[]) null, $U, $T36929);
                                                                                                        $init(out$,unit,gop, (x10.array.DistArray.Anonymous$29053.$_8fec24f7) null);}
                
                // constructor for non-virtual call
                final public x10.array.DistArray.Anonymous$29053<$U, $T36929> x10$array$DistArray$Anonymous$29053$$init$S(final x10.array.DistArray<$T36929> out$,
                                                                                                                          final $U unit,
                                                                                                                          final x10.core.fun.Fun_0_2<$U,$U,$U> gop, $_8fec24f7 $dummy) { {
                                                                                                                                                                                                
//#line 732 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"

                                                                                                                                                                                                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.out$ = out$;
                                                                                                                                                                                                
//#line 731 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.unit = (($U)(unit));
                                                                                                                                                                                                
//#line 731 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
this.gop = gop;
                                                                                                                                                                                            }
                                                                                                                                                                                            return this;
                                                                                                                                                                                            }
                
                // constructor
                public x10.array.DistArray.Anonymous$29053<$U, $T36929> $init(final x10.array.DistArray<$T36929> out$,
                                                                              final $U unit,
                                                                              final x10.core.fun.Fun_0_2<$U,$U,$U> gop, $_8fec24f7 $dummy){return x10$array$DistArray$Anonymous$29053$$init$S(out$,unit,gop, $dummy);}
                
            // synthetic type for parameter mangling for __0$1x10$array$DistArray$Anonymous$29053$$T36929$2__1x10$array$DistArray$Anonymous$29053$$U__2$1x10$array$DistArray$Anonymous$29053$$U$3x10$array$DistArray$Anonymous$29053$$U$3x10$array$DistArray$Anonymous$29053$$U$2
            public abstract static class $_8fec24f7 {}
            
        }
        
        @x10.core.X10Generated public static class $Closure$10<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$10.class);
            
            public static final x10.rtt.RuntimeType<$Closure$10> $RTT = x10.rtt.StaticFunType.<$Closure$10> make(
            /* base class */$Closure$10.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$10 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$10.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$10 $_obj = new $Closure$10((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$10(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.DistArray.$Closure$10.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.DistArray.LocalState
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$10 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.DistArray.LocalState<$T>
                  $apply(
                  ){
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t36990 =
                      this.
                        dist.places();
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t36992 =
                      t36990.contains$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
int t36993 =
                       0;
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t36992) {
                        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t36991 =
                          this.
                            dist.maxOffset$O();
                        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
t36993 = ((t36991) + (((int)(1))));
                    } else {
                        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
t36993 = 0;
                    }
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int size =
                      t36993;
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> localRaw =
                      ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(size)), true)));
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$T> t36994 =
                      ((x10.array.DistArray.LocalState)(new x10.array.DistArray.LocalState<$T>((java.lang.System[]) null, $T).$init(this.
                                                                                                                                      dist,
                                                                                                                                    ((x10.core.IndexedMemoryChunk<$T>)(localRaw)), (x10.array.DistArray.LocalState.__1$1x10$array$DistArray$LocalState$$T36928$2) null)));
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t36994;
                }
                
                public x10.array.Dist dist;
                
                public $Closure$10(final x10.rtt.Type $T,
                                   final x10.array.Dist dist) {x10.array.DistArray.$Closure$10.$initParams(this, $T);
                                                                    {
                                                                       this.dist = ((x10.array.Dist)(dist));
                                                                   }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$11<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$11.class);
            
            public static final x10.rtt.RuntimeType<$Closure$11> $RTT = x10.rtt.StaticFunType.<$Closure$11> make(
            /* base class */$Closure$11.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$11 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$11.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.core.fun.Fun_0_1 init = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.init = init;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$11 $_obj = new $Closure$11((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$11(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.DistArray.$Closure$11.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.DistArray.LocalState
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$11 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.DistArray.LocalState<$T>
                  $apply(
                  ){
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> localRaw;
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37002 =
                      this.
                        dist.places();
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37011 =
                      t37002.contains$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37011) {
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37003 =
                          this.
                            dist.maxOffset$O();
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37004 =
                          ((t37003) + (((int)(1))));
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37005 =
                          x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t37004)), false);
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
localRaw = ((x10.core.IndexedMemoryChunk)(t37005));
                        
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg =
                          ((x10.array.Region)(this.
                                                dist.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                        
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37226 =
                          reg.iterator();
                        
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37227 =
                              ((x10.lang.Iterator<x10.array.Point>)pt37226).hasNext$O();
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37227)) {
                                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37223 =
                              ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37226).next$G()));
                            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37224 =
                              this.
                                dist.offset$O(((x10.array.Point)(pt37223)));
                            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37225 =
                              (($T)((($T)
                                      ((x10.core.fun.Fun_0_1<x10.array.Point,$T>)this.
                                                                                   init).$apply(pt37223,x10.array.Point.$RTT))));
                            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(localRaw))).$set(((int)(t37224)), t37225);
                        }
                    } else {
                        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37010 =
                          x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(0)), false);
                        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
localRaw = ((x10.core.IndexedMemoryChunk)(t37010));
                    }
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$T> t37012 =
                      ((x10.array.DistArray.LocalState)(new x10.array.DistArray.LocalState<$T>((java.lang.System[]) null, $T).$init(this.
                                                                                                                                      dist,
                                                                                                                                    ((x10.core.IndexedMemoryChunk<$T>)(localRaw)), (x10.array.DistArray.LocalState.__1$1x10$array$DistArray$LocalState$$T36928$2) null)));
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37012;
                }
                
                public x10.array.Dist dist;
                public x10.core.fun.Fun_0_1<x10.array.Point,$T> init;
                
                public $Closure$11(final x10.rtt.Type $T,
                                   final x10.array.Dist dist,
                                   final x10.core.fun.Fun_0_1<x10.array.Point,$T> init, __1$1x10$array$Point$3x10$array$DistArray$$Closure$11$$T$2 $dummy) {x10.array.DistArray.$Closure$11.$initParams(this, $T);
                                                                                                                                                                 {
                                                                                                                                                                    this.dist = ((x10.array.Dist)(dist));
                                                                                                                                                                    this.init = ((x10.core.fun.Fun_0_1)(init));
                                                                                                                                                                }}
                // synthetic type for parameter mangling
                public abstract static class __1$1x10$array$Point$3x10$array$DistArray$$Closure$11$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$12<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$12.class);
            
            public static final x10.rtt.RuntimeType<$Closure$12> $RTT = x10.rtt.StaticFunType.<$Closure$12> make(
            /* base class */$Closure$12.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$12 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$12.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                $_obj.init = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$12 $_obj = new $Closure$12((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$12(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.DistArray.$Closure$12.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.DistArray.LocalState
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$12 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.DistArray.LocalState<$T>
                  $apply(
                  ){
                    
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> localRaw;
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37016 =
                      this.
                        dist.places();
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37024 =
                      t37016.contains$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37024) {
                        
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37017 =
                          this.
                            dist.maxOffset$O();
                        
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37018 =
                          ((t37017) + (((int)(1))));
                        
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37019 =
                          x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(t37018)), false);
                        
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
localRaw = ((x10.core.IndexedMemoryChunk)(t37019));
                        
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg =
                          ((x10.array.Region)(this.
                                                dist.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37230 =
                          reg.iterator();
                        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37231 =
                              ((x10.lang.Iterator<x10.array.Point>)pt37230).hasNext$O();
                            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37231)) {
                                
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37228 =
                              ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37230).next$G()));
                            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37229 =
                              this.
                                dist.offset$O(((x10.array.Point)(pt37228)));
                            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(localRaw))).$set(((int)(t37229)), this.
                                                                                                                                                                                              init);
                        }
                    } else {
                        
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37023 =
                          x10.core.IndexedMemoryChunk.<$T>allocate($T, ((int)(0)), false);
                        
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
localRaw = ((x10.core.IndexedMemoryChunk)(t37023));
                    }
                    
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$T> t37025 =
                      ((x10.array.DistArray.LocalState)(new x10.array.DistArray.LocalState<$T>((java.lang.System[]) null, $T).$init(this.
                                                                                                                                      dist,
                                                                                                                                    ((x10.core.IndexedMemoryChunk<$T>)(localRaw)), (x10.array.DistArray.LocalState.__1$1x10$array$DistArray$LocalState$$T36928$2) null)));
                    
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37025;
                }
                
                public x10.array.Dist dist;
                public $T init;
                
                public $Closure$12(final x10.rtt.Type $T,
                                   final x10.array.Dist dist,
                                   final $T init, __1x10$array$DistArray$$Closure$12$$T $dummy) {x10.array.DistArray.$Closure$12.$initParams(this, $T);
                                                                                                      {
                                                                                                         this.dist = ((x10.array.Dist)(dist));
                                                                                                         this.init = (($T)(init));
                                                                                                     }}
                // synthetic type for parameter mangling
                public abstract static class __1x10$array$DistArray$$Closure$12$$T {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$13<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$13.class);
            
            public static final x10.rtt.RuntimeType<$Closure$13> $RTT = x10.rtt.StaticFunType.<$Closure$13> make(
            /* base class */$Closure$13.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, x10.rtt.UnresolvedType.PARAM(0))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$13 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$13.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray a = (x10.array.DistArray) $deserializer.readRef();
                $_obj.a = a;
                x10.array.Dist d = (x10.array.Dist) $deserializer.readRef();
                $_obj.d = d;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$13 $_obj = new $Closure$13((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (a instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.a);
                } else {
                $serializer.write(this.a);
                }
                if (d instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.d);
                } else {
                $serializer.write(this.d);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$13(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.DistArray.$Closure$13.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.DistArray.LocalState
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$13 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.array.DistArray.LocalState<$T>
                  $apply(
                  ){
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>> t37028 =
                      ((x10.lang.PlaceLocalHandle)(((x10.array.DistArray<$T>)this.
                                                                               a).
                                                     localHandle));
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$T> t37029 =
                      ((x10.lang.PlaceLocalHandle<x10.array.DistArray.LocalState<$T>>)t37028).$apply$G();
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> t37030 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.DistArray.LocalState<$T>)t37029).
                                                       data));
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$T> t37031 =
                      ((x10.array.DistArray.LocalState)(new x10.array.DistArray.LocalState<$T>((java.lang.System[]) null, $T).$init(this.
                                                                                                                                      d,
                                                                                                                                    t37030, (x10.array.DistArray.LocalState.__1$1x10$array$DistArray$LocalState$$T36928$2) null)));
                    
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37031;
                }
                
                public x10.array.DistArray<$T> a;
                public x10.array.Dist d;
                
                public $Closure$13(final x10.rtt.Type $T,
                                   final x10.array.DistArray<$T> a,
                                   final x10.array.Dist d, __0$1x10$array$DistArray$$Closure$13$$T$2 $dummy) {x10.array.DistArray.$Closure$13.$initParams(this, $T);
                                                                                                                   {
                                                                                                                      this.a = ((x10.array.DistArray)(a));
                                                                                                                      this.d = ((x10.array.Dist)(d));
                                                                                                                  }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$DistArray$$Closure$13$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$14<$T> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$14.class);
            
            public static final x10.rtt.RuntimeType<$Closure$14> $RTT = x10.rtt.StaticVoidFunType.<$Closure$14> make(
            /* base class */$Closure$14.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$14 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$14.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                $_obj.v = $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$14 $_obj = new $Closure$14((java.lang.System[]) null, (x10.rtt.Type) null);
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
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (v instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.v);
                } else {
                $serializer.write(this.v);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$14(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.DistArray.$Closure$14.$initParams(this, $T);
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$14 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 521 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> imc37266 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                            out$$).raw()));
                    
//#line 522 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37267 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 522 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg37268 =
                      ((x10.array.Region)(t37267.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37263 =
                      reg37268.iterator();
                    
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                        
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37264 =
                          ((x10.lang.Iterator<x10.array.Point>)pt37263).hasNext$O();
                        
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37264)) {
                            
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                        }
                        
//#line 523 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37260 =
                          ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37263).next$G()));
                        
//#line 524 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37261 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 524 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37262 =
                          t37261.offset$O(((x10.array.Point)(pt37260)));
                        
//#line 524 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$T>)(imc37266))).$set(((int)(t37262)), this.
                                                                                                                                                                                          v);
                    }
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public $T v;
                
                public $Closure$14(final x10.rtt.Type $T,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final $T v, __0$1x10$array$DistArray$$Closure$14$$T$2__2x10$array$DistArray$$Closure$14$$T $dummy) {x10.array.DistArray.$Closure$14.$initParams(this, $T);
                                                                                                                                            {
                                                                                                                                               this.out$$ = out$$;
                                                                                                                                               this.dist = ((x10.array.Dist)(dist));
                                                                                                                                               this.v = (($T)(v));
                                                                                                                                           }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$DistArray$$Closure$14$$T$2__2x10$array$DistArray$$Closure$14$$T {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$15<$T, $U> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$15.class);
            
            public static final x10.rtt.RuntimeType<$Closure$15> $RTT = x10.rtt.StaticFunType.<$Closure$15> make(
            /* base class */$Closure$15.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, x10.rtt.UnresolvedType.PARAM(1))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$15 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$15.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.core.fun.Fun_0_1 op = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$15 $_obj = new $Closure$15((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$15(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.DistArray.$Closure$15.$initParams(this, $T, $U);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.DistArray.LocalState
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$15 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public x10.array.DistArray.LocalState<$U>
                  $apply(
                  ){
                    
//#line 541 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> newImc;
                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37116 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37117 =
                      t37116.places();
                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37129 =
                      t37117.contains$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                    
//#line 542 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37129) {
                        
//#line 543 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> srcImc =
                          ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                                out$$).raw()));
                        
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37118 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37119 =
                          t37118.maxOffset$O();
                        
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37120 =
                          ((t37119) + (((int)(1))));
                        
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> t37121 =
                          x10.core.IndexedMemoryChunk.<$U>allocate($U, ((int)(t37120)), false);
                        
//#line 544 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
newImc = ((x10.core.IndexedMemoryChunk)(t37121));
                        
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37122 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 545 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg =
                          ((x10.array.Region)(t37122.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                        
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37274 =
                          reg.iterator();
                        
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37275 =
                              ((x10.lang.Iterator<x10.array.Point>)pt37274).hasNext$O();
                            
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37275)) {
                                
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 546 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37269 =
                              ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37274).next$G()));
                            
//#line 547 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37270 =
                              ((x10.array.Dist)(this.
                                                  dist));
                            
//#line 547 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset37271 =
                              t37270.offset$O(((x10.array.Point)(pt37269)));
                            
//#line 548 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37272 =
                              (($T)((((x10.core.IndexedMemoryChunk<$T>)(srcImc))).$apply$G(((int)(offset37271)))));
                            
//#line 548 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37273 =
                              (($U)((($U)
                                      ((x10.core.fun.Fun_0_1<$T,$U>)this.
                                                                      op).$apply(t37272,$T))));
                            
//#line 548 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$U>)(newImc))).$set(((int)(offset37271)), t37273);
                        }
                    } else {
                        
//#line 551 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> t37128 =
                          x10.core.IndexedMemoryChunk.<$U>allocate($U, ((int)(0)), false);
                        
//#line 551 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
newImc = ((x10.core.IndexedMemoryChunk)(t37128));
                    }
                    
//#line 553 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37130 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 553 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$U> t37131 =
                      ((x10.array.DistArray.LocalState)(new x10.array.DistArray.LocalState<$U>((java.lang.System[]) null, $U).$init(((x10.array.Dist)(t37130)),
                                                                                                                                    ((x10.core.IndexedMemoryChunk<$U>)(newImc)), (x10.array.DistArray.LocalState.__1$1x10$array$DistArray$LocalState$$T36928$2) null)));
                    
//#line 553 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37131;
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public x10.core.fun.Fun_0_1<$T,$U> op;
                
                public $Closure$15(final x10.rtt.Type $T,
                                   final x10.rtt.Type $U,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final x10.core.fun.Fun_0_1<$T,$U> op, __0$1x10$array$DistArray$$Closure$15$$T$2__2$1x10$array$DistArray$$Closure$15$$T$3x10$array$DistArray$$Closure$15$$U$2 $dummy) {x10.array.DistArray.$Closure$15.$initParams(this, $T, $U);
                                                                                                                                                                                                              {
                                                                                                                                                                                                                 this.out$$ = out$$;
                                                                                                                                                                                                                 this.dist = ((x10.array.Dist)(dist));
                                                                                                                                                                                                                 this.op = ((x10.core.fun.Fun_0_1)(op));
                                                                                                                                                                                                             }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$DistArray$$Closure$15$$T$2__2$1x10$array$DistArray$$Closure$15$$T$3x10$array$DistArray$$Closure$15$$U$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$16<$T, $U> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$16.class);
            
            public static final x10.rtt.RuntimeType<$Closure$16> $RTT = x10.rtt.StaticVoidFunType.<$Closure$16> make(
            /* base class */$Closure$16.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$16 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$16.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.array.DistArray dst = (x10.array.DistArray) $deserializer.readRef();
                $_obj.dst = dst;
                x10.core.fun.Fun_0_1 op = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$16 $_obj = new $Closure$16((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (dst instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dst);
                } else {
                $serializer.write(this.dst);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$16(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.DistArray.$Closure$16.$initParams(this, $T, $U);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$16 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 572 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37284 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 572 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg37285 =
                      ((x10.array.Region)(t37284.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 573 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> srcImc37286 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                            out$$).raw()));
                    
//#line 574 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> dstImc37287 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $U),((x10.array.DistArray<$U>)this.
                                                                                                                                                                            dst).raw()));
                    
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37281 =
                      reg37285.iterator();
                    
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                        
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37282 =
                          ((x10.lang.Iterator<x10.array.Point>)pt37281).hasNext$O();
                        
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37282)) {
                            
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                        }
                        
//#line 575 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37276 =
                          ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37281).next$G()));
                        
//#line 576 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37277 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 576 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset37278 =
                          t37277.offset$O(((x10.array.Point)(pt37276)));
                        
//#line 577 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37279 =
                          (($T)((((x10.core.IndexedMemoryChunk<$T>)(srcImc37286))).$apply$G(((int)(offset37278)))));
                        
//#line 577 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37280 =
                          (($U)((($U)
                                  ((x10.core.fun.Fun_0_1<$T,$U>)this.
                                                                  op).$apply(t37279,$T))));
                        
//#line 577 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$U>)(dstImc37287))).$set(((int)(offset37278)), t37280);
                    }
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public x10.array.DistArray<$U> dst;
                public x10.core.fun.Fun_0_1<$T,$U> op;
                
                public $Closure$16(final x10.rtt.Type $T,
                                   final x10.rtt.Type $U,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final x10.array.DistArray<$U> dst,
                                   final x10.core.fun.Fun_0_1<$T,$U> op, __0$1x10$array$DistArray$$Closure$16$$T$2__2$1x10$array$DistArray$$Closure$16$$U$2__3$1x10$array$DistArray$$Closure$16$$T$3x10$array$DistArray$$Closure$16$$U$2 $dummy) {x10.array.DistArray.$Closure$16.$initParams(this, $T, $U);
                                                                                                                                                                                                                                                       {
                                                                                                                                                                                                                                                          this.out$$ = out$$;
                                                                                                                                                                                                                                                          this.dist = ((x10.array.Dist)(dist));
                                                                                                                                                                                                                                                          this.dst = ((x10.array.DistArray)(dst));
                                                                                                                                                                                                                                                          this.op = ((x10.core.fun.Fun_0_1)(op));
                                                                                                                                                                                                                                                      }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$DistArray$$Closure$16$$T$2__2$1x10$array$DistArray$$Closure$16$$U$2__3$1x10$array$DistArray$$Closure$16$$T$3x10$array$DistArray$$Closure$16$$U$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$17<$T, $U> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$17.class);
            
            public static final x10.rtt.RuntimeType<$Closure$17> $RTT = x10.rtt.StaticVoidFunType.<$Closure$17> make(
            /* base class */$Closure$17.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$17 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$17.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.array.Region filter = (x10.array.Region) $deserializer.readRef();
                $_obj.filter = filter;
                x10.array.DistArray dst = (x10.array.DistArray) $deserializer.readRef();
                $_obj.dst = dst;
                x10.core.fun.Fun_0_1 op = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$17 $_obj = new $Closure$17((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (filter instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.filter);
                } else {
                $serializer.write(this.filter);
                }
                if (dst instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dst);
                } else {
                $serializer.write(this.dst);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$17(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.DistArray.$Closure$17.$initParams(this, $T, $U);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$17 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 601 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37296 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 601 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg37297 =
                      ((x10.array.Region)(t37296.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 602 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region freg37298 =
                      ((x10.array.Region)(reg37297.$and(((x10.array.Region)(this.
                                                                              filter)))));
                    
//#line 603 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> srcImc37299 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                            out$$).raw()));
                    
//#line 604 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> dstImc37300 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $U),((x10.array.DistArray<$U>)this.
                                                                                                                                                                            dst).raw()));
                    
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37293 =
                      freg37298.iterator();
                    
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                        
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37294 =
                          ((x10.lang.Iterator<x10.array.Point>)pt37293).hasNext$O();
                        
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37294)) {
                            
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                        }
                        
//#line 605 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37288 =
                          ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37293).next$G()));
                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37289 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 606 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset37290 =
                          t37289.offset$O(((x10.array.Point)(pt37288)));
                        
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37291 =
                          (($T)((((x10.core.IndexedMemoryChunk<$T>)(srcImc37299))).$apply$G(((int)(offset37290)))));
                        
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37292 =
                          (($U)((($U)
                                  ((x10.core.fun.Fun_0_1<$T,$U>)this.
                                                                  op).$apply(t37291,$T))));
                        
//#line 607 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$U>)(dstImc37300))).$set(((int)(offset37290)), t37292);
                    }
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public x10.array.Region filter;
                public x10.array.DistArray<$U> dst;
                public x10.core.fun.Fun_0_1<$T,$U> op;
                
                public $Closure$17(final x10.rtt.Type $T,
                                   final x10.rtt.Type $U,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final x10.array.Region filter,
                                   final x10.array.DistArray<$U> dst,
                                   final x10.core.fun.Fun_0_1<$T,$U> op, __0$1x10$array$DistArray$$Closure$17$$T$2__3$1x10$array$DistArray$$Closure$17$$U$2__4$1x10$array$DistArray$$Closure$17$$T$3x10$array$DistArray$$Closure$17$$U$2 $dummy) {x10.array.DistArray.$Closure$17.$initParams(this, $T, $U);
                                                                                                                                                                                                                                                       {
                                                                                                                                                                                                                                                          this.out$$ = out$$;
                                                                                                                                                                                                                                                          this.dist = ((x10.array.Dist)(dist));
                                                                                                                                                                                                                                                          this.filter = ((x10.array.Region)(filter));
                                                                                                                                                                                                                                                          this.dst = ((x10.array.DistArray)(dst));
                                                                                                                                                                                                                                                          this.op = ((x10.core.fun.Fun_0_1)(op));
                                                                                                                                                                                                                                                      }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$DistArray$$Closure$17$$T$2__3$1x10$array$DistArray$$Closure$17$$U$2__4$1x10$array$DistArray$$Closure$17$$T$3x10$array$DistArray$$Closure$17$$U$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$18<$T, $S, $U> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$18.class);
            
            public static final x10.rtt.RuntimeType<$Closure$18> $RTT = x10.rtt.StaticFunType.<$Closure$18> make(
            /* base class */$Closure$18.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(3)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.DistArray.LocalState.$RTT, x10.rtt.UnresolvedType.PARAM(1))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $S;if (i ==2)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$18 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$18.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.array.DistArray src = (x10.array.DistArray) $deserializer.readRef();
                $_obj.src = src;
                x10.core.fun.Fun_0_2 op = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$18 $_obj = new $Closure$18((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (src instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.src);
                } else {
                $serializer.write(this.src);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$18(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.DistArray.$Closure$18.$initParams(this, $T, $S, $U);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.DistArray.LocalState
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $S;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$18 $this, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$S = $S;
                $this.$U = $U;
                }
                
                
                public x10.array.DistArray.LocalState<$S>
                  $apply(
                  ){
                    
//#line 627 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$S> newImc;
                    
//#line 628 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37156 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 628 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.PlaceGroup t37157 =
                      t37156.places();
                    
//#line 628 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37170 =
                      t37157.contains$O(((x10.lang.Place)(x10.lang.Runtime.home())));
                    
//#line 628 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (t37170) {
                        
//#line 629 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> src1Imc =
                          ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                                out$$).raw()));
                        
//#line 630 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> src2Imc =
                          ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $U),((x10.array.DistArray<$U>)this.
                                                                                                                                                                                src).raw()));
                        
//#line 631 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37158 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 631 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37159 =
                          t37158.maxOffset$O();
                        
//#line 631 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37160 =
                          ((t37159) + (((int)(1))));
                        
//#line 631 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$S> t37161 =
                          x10.core.IndexedMemoryChunk.<$S>allocate($S, ((int)(t37160)), false);
                        
//#line 631 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
newImc = ((x10.core.IndexedMemoryChunk)(t37161));
                        
//#line 632 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37162 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 632 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg =
                          ((x10.array.Region)(t37162.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                        
//#line 633 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37307 =
                          reg.iterator();
                        
//#line 633 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                            
//#line 633 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37308 =
                              ((x10.lang.Iterator<x10.array.Point>)pt37307).hasNext$O();
                            
//#line 633 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37308)) {
                                
//#line 633 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                            }
                            
//#line 633 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37301 =
                              ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37307).next$G()));
                            
//#line 634 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37302 =
                              ((x10.array.Dist)(this.
                                                  dist));
                            
//#line 634 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset37303 =
                              t37302.offset$O(((x10.array.Point)(pt37301)));
                            
//#line 635 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37304 =
                              (($T)((((x10.core.IndexedMemoryChunk<$T>)(src1Imc))).$apply$G(((int)(offset37303)))));
                            
//#line 635 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37305 =
                              (($U)((((x10.core.IndexedMemoryChunk<$U>)(src2Imc))).$apply$G(((int)(offset37303)))));
                            
//#line 635 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $S t37306 =
                              (($S)((($S)
                                      ((x10.core.fun.Fun_0_2<$T,$U,$S>)this.
                                                                         op).$apply(t37304,$T,
                                                                                    t37305,$U))));
                            
//#line 635 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$S>)(newImc))).$set(((int)(offset37303)), t37306);
                        }
                    } else {
                        
//#line 638 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$S> t37169 =
                          x10.core.IndexedMemoryChunk.<$S>allocate($S, ((int)(0)), false);
                        
//#line 638 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
newImc = ((x10.core.IndexedMemoryChunk)(t37169));
                    }
                    
//#line 640 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37171 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 640 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.DistArray.LocalState<$S> t37172 =
                      ((x10.array.DistArray.LocalState)(new x10.array.DistArray.LocalState<$S>((java.lang.System[]) null, $S).$init(((x10.array.Dist)(t37171)),
                                                                                                                                    ((x10.core.IndexedMemoryChunk<$S>)(newImc)), (x10.array.DistArray.LocalState.__1$1x10$array$DistArray$LocalState$$T36928$2) null)));
                    
//#line 640 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
return t37172;
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public x10.array.DistArray<$U> src;
                public x10.core.fun.Fun_0_2<$T,$U,$S> op;
                
                public $Closure$18(final x10.rtt.Type $T,
                                   final x10.rtt.Type $S,
                                   final x10.rtt.Type $U,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final x10.array.DistArray<$U> src,
                                   final x10.core.fun.Fun_0_2<$T,$U,$S> op, __0$1x10$array$DistArray$$Closure$18$$T$2__2$1x10$array$DistArray$$Closure$18$$U$2__3$1x10$array$DistArray$$Closure$18$$T$3x10$array$DistArray$$Closure$18$$U$3x10$array$DistArray$$Closure$18$$S$2 $dummy) {x10.array.DistArray.$Closure$18.$initParams(this, $T, $S, $U);
                                                                                                                                                                                                                                                                                              {
                                                                                                                                                                                                                                                                                                 this.out$$ = out$$;
                                                                                                                                                                                                                                                                                                 this.dist = ((x10.array.Dist)(dist));
                                                                                                                                                                                                                                                                                                 this.src = ((x10.array.DistArray)(src));
                                                                                                                                                                                                                                                                                                 this.op = ((x10.core.fun.Fun_0_2)(op));
                                                                                                                                                                                                                                                                                             }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$DistArray$$Closure$18$$T$2__2$1x10$array$DistArray$$Closure$18$$U$2__3$1x10$array$DistArray$$Closure$18$$T$3x10$array$DistArray$$Closure$18$$U$3x10$array$DistArray$$Closure$18$$S$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$19<$T, $S, $U> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$19.class);
            
            public static final x10.rtt.RuntimeType<$Closure$19> $RTT = x10.rtt.StaticVoidFunType.<$Closure$19> make(
            /* base class */$Closure$19.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(3)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $S;if (i ==2)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$19 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$19.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.array.DistArray src = (x10.array.DistArray) $deserializer.readRef();
                $_obj.src = src;
                x10.array.DistArray dst = (x10.array.DistArray) $deserializer.readRef();
                $_obj.dst = dst;
                x10.core.fun.Fun_0_2 op = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$19 $_obj = new $Closure$19((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (src instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.src);
                } else {
                $serializer.write(this.src);
                }
                if (dst instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dst);
                } else {
                $serializer.write(this.dst);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$19(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.DistArray.$Closure$19.$initParams(this, $T, $S, $U);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $S;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$19 $this, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$S = $S;
                $this.$U = $U;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37318 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 660 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg37319 =
                      ((x10.array.Region)(t37318.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 661 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> src1Imc37320 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                            out$$).raw()));
                    
//#line 662 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> src2Imc37321 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $U),((x10.array.DistArray<$U>)this.
                                                                                                                                                                            src).raw()));
                    
//#line 663 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$S> dstImc37322 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $S),((x10.array.DistArray<$S>)this.
                                                                                                                                                                            dst).raw()));
                    
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37315 =
                      reg37319.iterator();
                    
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                        
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37316 =
                          ((x10.lang.Iterator<x10.array.Point>)pt37315).hasNext$O();
                        
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37316)) {
                            
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                        }
                        
//#line 664 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37309 =
                          ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37315).next$G()));
                        
//#line 665 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37310 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 665 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset37311 =
                          t37310.offset$O(((x10.array.Point)(pt37309)));
                        
//#line 666 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37312 =
                          (($T)((((x10.core.IndexedMemoryChunk<$T>)(src1Imc37320))).$apply$G(((int)(offset37311)))));
                        
//#line 666 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37313 =
                          (($U)((((x10.core.IndexedMemoryChunk<$U>)(src2Imc37321))).$apply$G(((int)(offset37311)))));
                        
//#line 666 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $S t37314 =
                          (($S)((($S)
                                  ((x10.core.fun.Fun_0_2<$T,$U,$S>)this.
                                                                     op).$apply(t37312,$T,
                                                                                t37313,$U))));
                        
//#line 666 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$S>)(dstImc37322))).$set(((int)(offset37311)), t37314);
                    }
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public x10.array.DistArray<$U> src;
                public x10.array.DistArray<$S> dst;
                public x10.core.fun.Fun_0_2<$T,$U,$S> op;
                
                public $Closure$19(final x10.rtt.Type $T,
                                   final x10.rtt.Type $S,
                                   final x10.rtt.Type $U,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final x10.array.DistArray<$U> src,
                                   final x10.array.DistArray<$S> dst,
                                   final x10.core.fun.Fun_0_2<$T,$U,$S> op, $_c534dd73 $dummy) {x10.array.DistArray.$Closure$19.$initParams(this, $T, $S, $U);
                                                                                                     {
                                                                                                        this.out$$ = out$$;
                                                                                                        this.dist = ((x10.array.Dist)(dist));
                                                                                                        this.src = ((x10.array.DistArray)(src));
                                                                                                        this.dst = ((x10.array.DistArray)(dst));
                                                                                                        this.op = ((x10.core.fun.Fun_0_2)(op));
                                                                                                    }}
                // synthetic type for parameter mangling for __0$1x10$array$DistArray$$Closure$19$$T$2__2$1x10$array$DistArray$$Closure$19$$U$2__3$1x10$array$DistArray$$Closure$19$$S$2__4$1x10$array$DistArray$$Closure$19$$T$3x10$array$DistArray$$Closure$19$$U$3x10$array$DistArray$$Closure$19$$S$2
                public abstract static class $_c534dd73 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$20<$T, $S, $U> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$20.class);
            
            public static final x10.rtt.RuntimeType<$Closure$20> $RTT = x10.rtt.StaticVoidFunType.<$Closure$20> make(
            /* base class */$Closure$20.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(3)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $S;if (i ==2)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$20 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$20.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                x10.array.Region filter = (x10.array.Region) $deserializer.readRef();
                $_obj.filter = filter;
                x10.array.DistArray src = (x10.array.DistArray) $deserializer.readRef();
                $_obj.src = src;
                x10.array.DistArray dst = (x10.array.DistArray) $deserializer.readRef();
                $_obj.dst = dst;
                x10.core.fun.Fun_0_2 op = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.op = op;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$20 $_obj = new $Closure$20((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (filter instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.filter);
                } else {
                $serializer.write(this.filter);
                }
                if (src instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.src);
                } else {
                $serializer.write(this.src);
                }
                if (dst instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dst);
                } else {
                $serializer.write(this.dst);
                }
                if (op instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.op);
                } else {
                $serializer.write(this.op);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$20(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.DistArray.$Closure$20.$initParams(this, $T, $S, $U);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $S;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$20 $this, final x10.rtt.Type $T, final x10.rtt.Type $S, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$S = $S;
                $this.$U = $U;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 690 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37332 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 690 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg37333 =
                      ((x10.array.Region)(t37332.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 691 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region freg37334 =
                      ((x10.array.Region)(reg37333.$and(((x10.array.Region)(this.
                                                                              filter)))));
                    
//#line 692 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> src1Imc37335 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                            out$$).raw()));
                    
//#line 693 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$U> src2Imc37336 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $U),((x10.array.DistArray<$U>)this.
                                                                                                                                                                            src).raw()));
                    
//#line 694 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$S> dstImc37337 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $S),((x10.array.DistArray<$S>)this.
                                                                                                                                                                            dst).raw()));
                    
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37329 =
                      freg37334.iterator();
                    
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37330 =
                          ((x10.lang.Iterator<x10.array.Point>)pt37329).hasNext$O();
                        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37330)) {
                            
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                        }
                        
//#line 695 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37323 =
                          ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37329).next$G()));
                        
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37324 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 696 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int offset37325 =
                          t37324.offset$O(((x10.array.Point)(pt37323)));
                        
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37326 =
                          (($T)((((x10.core.IndexedMemoryChunk<$T>)(src1Imc37335))).$apply$G(((int)(offset37325)))));
                        
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37327 =
                          (($U)((((x10.core.IndexedMemoryChunk<$U>)(src2Imc37336))).$apply$G(((int)(offset37325)))));
                        
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $S t37328 =
                          (($S)((($S)
                                  ((x10.core.fun.Fun_0_2<$T,$U,$S>)this.
                                                                     op).$apply(t37326,$T,
                                                                                t37327,$U))));
                        
//#line 697 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
(((x10.core.IndexedMemoryChunk<$S>)(dstImc37337))).$set(((int)(offset37325)), t37328);
                    }
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public x10.array.Region filter;
                public x10.array.DistArray<$U> src;
                public x10.array.DistArray<$S> dst;
                public x10.core.fun.Fun_0_2<$T,$U,$S> op;
                
                public $Closure$20(final x10.rtt.Type $T,
                                   final x10.rtt.Type $S,
                                   final x10.rtt.Type $U,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final x10.array.Region filter,
                                   final x10.array.DistArray<$U> src,
                                   final x10.array.DistArray<$S> dst,
                                   final x10.core.fun.Fun_0_2<$T,$U,$S> op, $_26b8af68 $dummy) {x10.array.DistArray.$Closure$20.$initParams(this, $T, $S, $U);
                                                                                                     {
                                                                                                        this.out$$ = out$$;
                                                                                                        this.dist = ((x10.array.Dist)(dist));
                                                                                                        this.filter = ((x10.array.Region)(filter));
                                                                                                        this.src = ((x10.array.DistArray)(src));
                                                                                                        this.dst = ((x10.array.DistArray)(dst));
                                                                                                        this.op = ((x10.core.fun.Fun_0_2)(op));
                                                                                                    }}
                // synthetic type for parameter mangling for __0$1x10$array$DistArray$$Closure$20$$T$2__3$1x10$array$DistArray$$Closure$20$$U$2__4$1x10$array$DistArray$$Closure$20$$S$2__5$1x10$array$DistArray$$Closure$20$$T$3x10$array$DistArray$$Closure$20$$U$3x10$array$DistArray$$Closure$20$$S$2
                public abstract static class $_26b8af68 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$21<$T, $U> extends x10.core.Ref implements x10.core.fun.VoidFun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$21.class);
            
            public static final x10.rtt.RuntimeType<$Closure$21> $RTT = x10.rtt.StaticVoidFunType.<$Closure$21> make(
            /* base class */$Closure$21.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;if (i ==1)return $U;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$21 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$21.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$U = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.DistArray out$$ = (x10.array.DistArray) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist dist = (x10.array.Dist) $deserializer.readRef();
                $_obj.dist = dist;
                $_obj.unit = $deserializer.readRef();
                x10.core.fun.Fun_0_2 lop = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.lop = lop;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$21 $_obj = new $Closure$21((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$U);
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (dist instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.dist);
                } else {
                $serializer.write(this.dist);
                }
                if (unit instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.unit);
                } else {
                $serializer.write(this.unit);
                }
                if (lop instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.lop);
                } else {
                $serializer.write(this.lop);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$21(final java.lang.System[] $dummy, final x10.rtt.Type $T, final x10.rtt.Type $U) { 
            super($dummy);
            x10.array.DistArray.$Closure$21.$initParams(this, $T, $U);
            }
            
                private x10.rtt.Type $T;
                private x10.rtt.Type $U;
                // initializer of type parameters
                public static void $initParams(final $Closure$21 $this, final x10.rtt.Type $T, final x10.rtt.Type $U) {
                $this.$T = $T;
                $this.$U = $U;
                }
                
                
                public void
                  $apply(
                  ){
                    
//#line 740 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37347 =
                      ((x10.array.Dist)(this.
                                          dist));
                    
//#line 740 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Region reg37348 =
                      ((x10.array.Region)(t37347.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 741 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
$U localRes37349 =
                      (($U)(this.
                              unit));
                    
//#line 742 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.core.IndexedMemoryChunk<$T> imc37350 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.DistArray<$T>)this.
                                                                                                                                                                            out$$).raw()));
                    
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.lang.Iterator<x10.array.Point> pt37344 =
                      reg37348.iterator();
                    
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                        
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final boolean t37345 =
                          ((x10.lang.Iterator<x10.array.Point>)pt37344).hasNext$O();
                        
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
if (!(t37345)) {
                            
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
break;
                        }
                        
//#line 743 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Point pt37338 =
                          ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)pt37344).next$G()));
                        
//#line 744 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37339 =
                          (($U)(localRes37349));
                        
//#line 744 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final x10.array.Dist t37340 =
                          ((x10.array.Dist)(this.
                                              dist));
                        
//#line 744 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final int t37341 =
                          t37340.offset$O(((x10.array.Point)(pt37338)));
                        
//#line 744 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $T t37342 =
                          (($T)((((x10.core.IndexedMemoryChunk<$T>)(imc37350))).$apply$G(((int)(t37341)))));
                        
//#line 744 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37343 =
                          (($U)((($U)
                                  ((x10.core.fun.Fun_0_2<$U,$T,$U>)this.
                                                                     lop).$apply(t37339,$U,
                                                                                 t37342,$T))));
                        
//#line 744 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
localRes37349 = (($U)(t37343));
                    }
                    
//#line 746 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
final $U t37351 =
                      (($U)(localRes37349));
                    
//#line 746 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/DistArray.x10"
x10.lang.Runtime.<$U>makeOffer__0x10$lang$Runtime$$T($U, (($U)(t37351)));
                }
                
                public x10.array.DistArray<$T> out$$;
                public x10.array.Dist dist;
                public $U unit;
                public x10.core.fun.Fun_0_2<$U,$T,$U> lop;
                
                public $Closure$21(final x10.rtt.Type $T,
                                   final x10.rtt.Type $U,
                                   final x10.array.DistArray<$T> out$$,
                                   final x10.array.Dist dist,
                                   final $U unit,
                                   final x10.core.fun.Fun_0_2<$U,$T,$U> lop, __0$1x10$array$DistArray$$Closure$21$$T$2__2x10$array$DistArray$$Closure$21$$U__3$1x10$array$DistArray$$Closure$21$$U$3x10$array$DistArray$$Closure$21$$T$3x10$array$DistArray$$Closure$21$$U$2 $dummy) {x10.array.DistArray.$Closure$21.$initParams(this, $T, $U);
                                                                                                                                                                                                                                                                                           {
                                                                                                                                                                                                                                                                                              this.out$$ = out$$;
                                                                                                                                                                                                                                                                                              this.dist = ((x10.array.Dist)(dist));
                                                                                                                                                                                                                                                                                              this.unit = (($U)(unit));
                                                                                                                                                                                                                                                                                              this.lop = ((x10.core.fun.Fun_0_2)(lop));
                                                                                                                                                                                                                                                                                          }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$DistArray$$Closure$21$$T$2__2x10$array$DistArray$$Closure$21$$U__3$1x10$array$DistArray$$Closure$21$$U$3x10$array$DistArray$$Closure$21$$T$3x10$array$DistArray$$Closure$21$$U$2 {}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$array$Point$3x10$array$DistArray$$T$2 {}
        // synthetic type for parameter mangling
        public abstract static class __1x10$array$DistArray$$T {}
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$array$DistArray$$T$2 {}
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$array$DistArray$LocalState$1x10$array$DistArray$$T$2$2 {}
        
        }
        
        
        
        
        