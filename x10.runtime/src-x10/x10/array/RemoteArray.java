package x10.array;


@x10.core.X10Generated final public class RemoteArray<$T> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RemoteArray.class);
    
    public static final x10.rtt.RuntimeType<RemoteArray> $RTT = x10.rtt.NamedType.<RemoteArray> make(
    "x10.array.RemoteArray", /* base class */RemoteArray.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RemoteArray $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RemoteArray.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.core.RemoteIndexedMemoryChunk rawData = (x10.core.RemoteIndexedMemoryChunk) $deserializer.readRef();
        $_obj.rawData = rawData;
        x10.array.Region region = (x10.array.Region) $deserializer.readRef();
        $_obj.region = region;
        $_obj.size = $deserializer.readInt();
        x10.core.GlobalRef array = (x10.core.GlobalRef) $deserializer.readRef();
        $_obj.array = array;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        RemoteArray $_obj = new RemoteArray((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (rawData instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.rawData);
        } else {
        $serializer.write(this.rawData);
        }
        if (region instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.region);
        } else {
        $serializer.write(this.region);
        }
        $serializer.write(this.size);
        if (array instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.array);
        } else {
        $serializer.write(this.array);
        }
        
    }
    
    // constructor just for allocation
    public RemoteArray(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.array.RemoteArray.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final RemoteArray $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public x10.array.Region region;
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public int size;
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public x10.core.GlobalRef<x10.array.Array<$T>> array;
        
        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
/**
     * Caches a remote reference to the backing storage for the remote array
     * to enable DMA operations to be initiated remotely.  
     */
        public x10.core.RemoteIndexedMemoryChunk<$T> rawData;
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final public int
                                                                                                     rank$O(
                                                                                                     ){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Region t47597 =
              ((x10.array.Region)(region));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final int t47598 =
              t47597.
                rank;
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47598;
        }
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final public x10.lang.Place
                                                                                                     home(
                                                                                                     ){
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47599 =
              ((x10.core.GlobalRef)(array));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.lang.Place t47600 =
              ((x10.lang.Place)((t47599).home));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47600;
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
// creation method for java code (1-phase java constructor)
        public RemoteArray(final x10.rtt.Type $T,
                           final x10.array.Array<$T> a, __0$1x10$array$RemoteArray$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                    $init(a, (x10.array.RemoteArray.__0$1x10$array$RemoteArray$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.RemoteArray<$T> x10$array$RemoteArray$$init$S(final x10.array.Array<$T> a, __0$1x10$array$RemoteArray$$T$2 $dummy) { {
                                                                                                                                                           
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"

                                                                                                                                                           
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Region t47646 =
                                                                                                                                                             ((x10.array.Region)(((x10.array.Array<$T>)a).
                                                                                                                                                                                   region));
                                                                                                                                                           
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final int t47647 =
                                                                                                                                                             ((x10.array.Array<$T>)a).
                                                                                                                                                               size;
                                                                                                                                                           
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47648 =
                                                                                                                                                             ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.array.Array<$T>>(x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, $T), ((x10.array.Array)(a)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                                                                                                                                                           
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
this.region = t47646;
                                                                                                                                                           this.size = t47647;
                                                                                                                                                           this.array = t47648;
                                                                                                                                                           
                                                                                                                                                           
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.IndexedMemoryChunk<$T> t47604 =
                                                                                                                                                             ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.array.Array<$T>)a).raw()))));
                                                                                                                                                           
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.RemoteIndexedMemoryChunk<$T> t47605 =
                                                                                                                                                             x10.core.RemoteIndexedMemoryChunk.<$T>wrap(t47604);
                                                                                                                                                           
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
this.rawData = ((x10.core.RemoteIndexedMemoryChunk)(t47605));
                                                                                                                                                       }
                                                                                                                                                       return this;
                                                                                                                                                       }
        
        // constructor
        public x10.array.RemoteArray<$T> $init(final x10.array.Array<$T> a, __0$1x10$array$RemoteArray$$T$2 $dummy){return x10$array$RemoteArray$$init$S(a, $dummy);}
        
        
        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
// creation method for java code (1-phase java constructor)
        public RemoteArray(final x10.rtt.Type $T,
                           final x10.array.Region reg,
                           final x10.core.RemoteIndexedMemoryChunk<$T> raw, __1$1x10$array$RemoteArray$$T$2 $dummy){this((java.lang.System[]) null, $T);
                                                                                                                        $init(reg,raw, (x10.array.RemoteArray.__1$1x10$array$RemoteArray$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.RemoteArray<$T> x10$array$RemoteArray$$init$S(final x10.array.Region reg,
                                                                             final x10.core.RemoteIndexedMemoryChunk<$T> raw, __1$1x10$array$RemoteArray$$T$2 $dummy) { {
                                                                                                                                                                               
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"

                                                                                                                                                                               
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> arr;
                                                                                                                                                                               
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.lang.Place t47606 =
                                                                                                                                                                                 ((x10.lang.Place)(((((x10.core.RemoteIndexedMemoryChunk<$T>)(raw))).home)));
                                                                                                                                                                               
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final boolean t47608 =
                                                                                                                                                                                 t47606.isCUDA$O();
                                                                                                                                                                               
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
if (t47608) {
                                                                                                                                                                                   
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47607 =
                                                                                                                                                                                     ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.array.Array<$T>>(x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, $T), ((x10.array.Array)(null)), (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                                                                                                                                                                                   
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
arr = ((x10.core.GlobalRef)(t47607));
                                                                                                                                                                               } else {
                                                                                                                                                                                   
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.lang.Place t47591 =
                                                                                                                                                                                     ((x10.lang.Place)(((((x10.core.RemoteIndexedMemoryChunk<$T>)(raw))).home)));
                                                                                                                                                                                   
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47592 =
                                                                                                                                                                                     ((x10.core.GlobalRef)(x10.lang.Runtime.<x10.core.GlobalRef<x10.array.Array<$T>>>evalAt__1$1x10$lang$Runtime$$T$2$G(x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, $T)), ((x10.lang.Place)(t47591)),
                                                                                                                                                                                                                                                                                                        ((x10.core.fun.Fun_0_0)(new x10.array.RemoteArray.$Closure$82<$T>($T, raw,
                                                                                                                                                                                                                                                                                                                                                                          reg, (x10.array.RemoteArray.$Closure$82.__0$1x10$array$RemoteArray$$Closure$82$$T$2) null))))));
                                                                                                                                                                                   
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
arr = ((x10.core.GlobalRef)(t47592));
                                                                                                                                                                               }
                                                                                                                                                                               
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final int t47649 =
                                                                                                                                                                                 reg.size$O();
                                                                                                                                                                               
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
this.region = reg;
                                                                                                                                                                               this.size = t47649;
                                                                                                                                                                               this.array = arr;
                                                                                                                                                                               
                                                                                                                                                                               
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
this.rawData = ((x10.core.RemoteIndexedMemoryChunk)(raw));
                                                                                                                                                                           }
                                                                                                                                                                           return this;
                                                                                                                                                                           }
        
        // constructor
        public x10.array.RemoteArray<$T> $init(final x10.array.Region reg,
                                               final x10.core.RemoteIndexedMemoryChunk<$T> raw, __1$1x10$array$RemoteArray$$T$2 $dummy){return x10$array$RemoteArray$$init$S(reg,raw, $dummy);}
        
        
        
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public $T
                                                                                                      $apply$G(
                                                                                                      final int i){
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47613 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   this.$apply())));
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final $T t47614 =
              (($T)(((x10.array.Array<$T>)t47613).$apply$G((int)(i))));
            
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47614;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public $T
                                                                                                      $apply$G(
                                                                                                      final x10.array.Point p){
            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47617 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   this.$apply())));
            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final $T t47618 =
              (($T)(((x10.array.Array<$T>)t47617).$apply$G(((x10.array.Point)(p)))));
            
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47618;
        }
        
        
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public $T
                                                                                                      $set__1x10$array$RemoteArray$$T$G(
                                                                                                      final int i,
                                                                                                      final $T v){
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47622 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   this.$apply())));
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final $T t47623 =
              (($T)(((x10.array.Array<$T>)t47622).$set__1x10$array$Array$$T$G((int)(i),
                                                                              (($T)(v)))));
            
//#line 137 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47623;
        }
        
        
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public $T
                                                                                                      $set__1x10$array$RemoteArray$$T$G(
                                                                                                      final x10.array.Point p,
                                                                                                      final $T v){
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47625 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   this.$apply())));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final $T t47626 =
              (($T)(((x10.array.Array<$T>)t47625).$set__1x10$array$Array$$T$G(((x10.array.Point)(p)),
                                                                              (($T)(v)))));
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47626;
        }
        
        
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public x10.array.Array<$T>
                                                                                                      $apply(
                                                                                                      ){
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47629 =
              ((x10.core.GlobalRef)(this.
                                      array));
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47630 =
              (((x10.core.GlobalRef<x10.array.Array<$T>>)(t47629))).$apply$G();
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> __desugarer__var__36__47594 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   t47630)));
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
x10.array.Array<$T> ret47595 =
               null;
            
//#line 159 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final int t47650 =
              ((x10.array.Array<$T>)__desugarer__var__36__47594).
                rank;
            
//#line 159 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Region t47651 =
              ((x10.array.Region)(x10.array.RemoteArray.this.
                                    region));
            
//#line 159 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final int t47652 =
              t47651.
                rank;
            
//#line 159 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final boolean t47653 =
              ((int) t47650) ==
            ((int) t47652);
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final boolean t47654 =
              !(t47653);
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
if (t47654) {
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final boolean t47655 =
                  true;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
if (t47655) {
                    
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.lang.FailedDynamicCheckException t47656 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Array[T]{self.rank==this(:x10.array.RemoteArray).region.rank}");
                    
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
throw t47656;
                }
            }
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
ret47595 = ((x10.array.Array)(__desugarer__var__36__47594));
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47638 =
              ((x10.array.Array)(ret47595));
            
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47638;
        }
        
        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public boolean
                                                                                                      equals(
                                                                                                      final java.lang.Object other){
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final boolean t47639 =
              x10.array.RemoteArray.$RTT.isInstance(other, $T);
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final boolean t47640 =
              !(t47639);
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
if (t47640) {
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return false;
            }
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.RemoteArray<$T> oRA =
              ((x10.array.RemoteArray)(x10.rtt.Types.<x10.array.RemoteArray<$T>> cast(other,x10.rtt.ParameterizedType.make(x10.array.RemoteArray.$RTT, $T))));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47641 =
              ((x10.core.GlobalRef)(((x10.array.RemoteArray<$T>)oRA).
                                      array));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47642 =
              ((x10.core.GlobalRef)(array));
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final boolean t47643 =
              (((x10.core.GlobalRef<x10.array.Array<$T>>)(t47641))).equals(t47642);
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47643;
        }
        
        
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
public int
                                                                                                      hashCode(
                                                                                                      ){
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47644 =
              ((x10.core.GlobalRef)(array));
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final int t47645 =
              (((x10.core.GlobalRef<x10.array.Array<$T>>)(t47644))).hashCode();
            
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47645;
        }
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final public x10.array.RemoteArray<$T>
                                                                                                     x10$array$RemoteArray$$x10$array$RemoteArray$this(
                                                                                                     ){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return x10.array.RemoteArray.this;
        }
        
        @x10.core.X10Generated public static class $Closure$82<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$82.class);
            
            public static final x10.rtt.RuntimeType<$Closure$82> $RTT = x10.rtt.StaticFunType.<$Closure$82> make(
            /* base class */$Closure$82.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.core.GlobalRef.$RTT, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.rtt.UnresolvedType.PARAM(0)))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$82 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$82.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.core.RemoteIndexedMemoryChunk raw = (x10.core.RemoteIndexedMemoryChunk) $deserializer.readRef();
                $_obj.raw = raw;
                x10.array.Region reg = (x10.array.Region) $deserializer.readRef();
                $_obj.reg = reg;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$82 $_obj = new $Closure$82((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (raw instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.raw);
                } else {
                $serializer.write(this.raw);
                }
                if (reg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.reg);
                } else {
                $serializer.write(this.reg);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$82(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.RemoteArray.$Closure$82.$initParams(this, $T);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.core.GlobalRef
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$82 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public x10.core.GlobalRef<x10.array.Array<$T>>
                  $apply(
                  ){
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.IndexedMemoryChunk<$T> t47587 =
                      ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),(((x10.core.RemoteIndexedMemoryChunk<$T>)(this.
                                                                                                                                                                                            raw))).$apply$G()));
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47588 =
                      ((x10.array.Array)(new x10.array.Array<$T>((java.lang.System[]) null, $T).$init(((x10.array.Region)(this.
                                                                                                                            reg)),
                                                                                                      t47587, (x10.array.Array.__1$1x10$array$Array$$T$2) null)));
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.array.Array<$T> t47589 =
                      ((x10.array.Array<$T>)
                        t47588);
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
final x10.core.GlobalRef<x10.array.Array<$T>> t47590 =
                      ((x10.core.GlobalRef)(new x10.core.GlobalRef<x10.array.Array<$T>>(x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, $T), t47589, (x10.core.GlobalRef.__0x10$lang$GlobalRef$$T) null)));
                    
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RemoteArray.x10"
return t47590;
                }
                
                public x10.core.RemoteIndexedMemoryChunk<$T> raw;
                public x10.array.Region reg;
                
                public $Closure$82(final x10.rtt.Type $T,
                                   final x10.core.RemoteIndexedMemoryChunk<$T> raw,
                                   final x10.array.Region reg, __0$1x10$array$RemoteArray$$Closure$82$$T$2 $dummy) {x10.array.RemoteArray.$Closure$82.$initParams(this, $T);
                                                                                                                         {
                                                                                                                            this.raw = ((x10.core.RemoteIndexedMemoryChunk)(raw));
                                                                                                                            this.reg = ((x10.array.Region)(reg));
                                                                                                                        }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$RemoteArray$$Closure$82$$T$2 {}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$array$RemoteArray$$T$2 {}
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$array$RemoteArray$$T$2 {}
        
    }
    