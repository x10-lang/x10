package x10.util;

@x10.core.X10Generated public class MapIterator<$S, $T> extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MapIterator.class);
    
    public static final x10.rtt.RuntimeType<MapIterator> $RTT = x10.rtt.NamedType.<MapIterator> make(
    "x10.util.MapIterator", /* base class */MapIterator.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.UnresolvedType.PARAM(1)), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $S;if (i ==1)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MapIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + MapIterator.class + " calling"); } 
        $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.lang.Iterator i = (x10.lang.Iterator) $deserializer.readRef();
        $_obj.i = i;
        x10.core.fun.Fun_0_1 f = (x10.core.fun.Fun_0_1) $deserializer.readRef();
        $_obj.f = f;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        MapIterator $_obj = new MapIterator((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (i instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.i);
        } else {
        $serializer.write(this.i);
        }
        if (f instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.f);
        } else {
        $serializer.write(this.f);
        }
        
    }
    
    // constructor just for allocation
    public MapIterator(final java.lang.System[] $dummy, final x10.rtt.Type $S, final x10.rtt.Type $T) { 
    super($dummy);
    x10.util.MapIterator.$initParams(this, $S, $T);
    }
    
        private x10.rtt.Type $S;
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final MapIterator $this, final x10.rtt.Type $S, final x10.rtt.Type $T) {
        $this.$S = $S;
        $this.$T = $T;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
public x10.lang.Iterator<$S> i;
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
public x10.core.fun.Fun_0_1<$S,$T> f;
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
// creation method for java code (1-phase java constructor)
        public MapIterator(final x10.rtt.Type $S,
                           final x10.rtt.Type $T,
                           final x10.lang.Iterator<$S> i,
                           final x10.core.fun.Fun_0_1<$S,$T> f, __0$1x10$util$MapIterator$$S$2__1$1x10$util$MapIterator$$S$3x10$util$MapIterator$$T$2 $dummy){this((java.lang.System[]) null, $S, $T);
                                                                                                                                                                  $init(i,f, (x10.util.MapIterator.__0$1x10$util$MapIterator$$S$2__1$1x10$util$MapIterator$$S$3x10$util$MapIterator$$T$2) null);}
        
        // constructor for non-virtual call
        final public x10.util.MapIterator<$S, $T> x10$util$MapIterator$$init$S(final x10.lang.Iterator<$S> i,
                                                                               final x10.core.fun.Fun_0_1<$S,$T> f, __0$1x10$util$MapIterator$$S$2__1$1x10$util$MapIterator$$S$3x10$util$MapIterator$$T$2 $dummy) { {
                                                                                                                                                                                                                           
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"

                                                                                                                                                                                                                           
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"

                                                                                                                                                                                                                           
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
this.i = ((x10.lang.Iterator)(i));
                                                                                                                                                                                                                           
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
this.f = ((x10.core.fun.Fun_0_1)(f));
                                                                                                                                                                                                                       }
                                                                                                                                                                                                                       return this;
                                                                                                                                                                                                                       }
        
        // constructor
        public x10.util.MapIterator<$S, $T> $init(final x10.lang.Iterator<$S> i,
                                                  final x10.core.fun.Fun_0_1<$S,$T> f, __0$1x10$util$MapIterator$$S$2__1$1x10$util$MapIterator$$S$3x10$util$MapIterator$$T$2 $dummy){return x10$util$MapIterator$$init$S(i,f, $dummy);}
        
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
public boolean
                                                                                                    hasNext$O(
                                                                                                    ){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
final x10.lang.Iterator<$S> t59081 =
              ((x10.lang.Iterator)(i));
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
final boolean t59082 =
              ((x10.lang.Iterator<$S>)t59081).hasNext$O();
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
return t59082;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
public $T
                                                                                                    next$G(
                                                                                                    ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
final x10.core.fun.Fun_0_1<$S,$T> t59084 =
              ((x10.core.fun.Fun_0_1)(f));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
final x10.lang.Iterator<$S> t59083 =
              ((x10.lang.Iterator)(i));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
final $S t59085 =
              (($S)(((x10.lang.Iterator<$S>)t59083).next$G()));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
final $T t59086 =
              (($T)((($T)
                      ((x10.core.fun.Fun_0_1<$S,$T>)t59084).$apply(t59085,$S))));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
return t59086;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
final public x10.util.MapIterator<$S, $T>
                                                                                                    x10$util$MapIterator$$x10$util$MapIterator$this(
                                                                                                    ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapIterator.x10"
return x10.util.MapIterator.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __0$1x10$util$MapIterator$$S$2__1$1x10$util$MapIterator$$S$3x10$util$MapIterator$$T$2 {}
    
}
