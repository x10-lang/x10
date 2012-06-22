package x10.util;

@x10.core.X10Generated abstract public class MapSet<$T> extends x10.util.AbstractCollection<$T> implements x10.util.Set, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MapSet.class);
    
    public static final x10.rtt.RuntimeType<MapSet> $RTT = x10.rtt.NamedType.<MapSet> make(
    "x10.util.MapSet", /* base class */MapSet.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Set.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.util.AbstractCollection.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MapSet $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + MapSet.class + " calling"); } 
        x10.util.AbstractCollection.$_deserialize_body($_obj, $deserializer);
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.util.Map map = (x10.util.Map) $deserializer.readRef();
        $_obj.map = map;
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
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (map instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.map);
        } else {
        $serializer.write(this.map);
        }
        
    }
    
    // constructor just for allocation
    public MapSet(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy, $T);
    x10.util.MapSet.$initParams(this, $T);
    }
    // dispatcher for method abstract public x10.util.Container.contains(T):x10.lang.Boolean
    public java.lang.Object contains(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(contains__0x10$util$MapSet$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.add(T):x10.lang.Boolean
    public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(add__0x10$util$MapSet$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.remove(T):x10.lang.Boolean
    public java.lang.Object remove(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(remove__0x10$util$MapSet$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.addAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object addAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(addAll__0$1x10$util$AbstractCollection$$T$2$O((x10.util.Container)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.retainAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object retainAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(retainAll__0$1x10$util$AbstractCollection$$T$2$O((x10.util.Container)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.removeAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object removeAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(removeAll__0$1x10$util$AbstractCollection$$T$2$O((x10.util.Container)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.addAllWhere(x10.util.Container[T],(a1:T)=>x10.lang.Boolean):x10.lang.Boolean
    public java.lang.Object addAllWhere(final x10.util.Container a1, final x10.rtt.Type t1, final x10.core.fun.Fun_0_1 a2, final x10.rtt.Type t2) {
    return x10.core.Boolean.$box(addAllWhere__0$1x10$util$AbstractCollection$$T$2__1$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O((x10.util.Container)a1, (x10.core.fun.Fun_0_1)a2));
    }
    // dispatcher for method abstract public x10.util.Collection.removeAllWhere((a1:T)=>x10.lang.Boolean):x10.lang.Boolean
    public java.lang.Object removeAllWhere(final x10.core.fun.Fun_0_1 a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(removeAllWhere__0$1x10$util$AbstractCollection$$T$3x10$lang$Boolean$2$O((x10.core.fun.Fun_0_1)a1));
    }
    // dispatcher for method abstract public x10.util.Container.containsAll(x10.util.Container[T]):x10.lang.Boolean
    public java.lang.Object containsAll(final x10.util.Container a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(containsAll__0$1x10$util$AbstractContainer$$T$2$O((x10.util.Container)a1));
    }
    // bridge for method abstract public x10.util.AbstractContainer.contains(y:T):x10.lang.Boolean
    public boolean
      contains__0x10$util$AbstractContainer$$T$O($T a1){return contains__0x10$util$MapSet$$T$O(a1);}
    // bridge for method abstract public x10.util.AbstractCollection.add(T):x10.lang.Boolean
    public boolean
      add__0x10$util$AbstractCollection$$T$O($T a1){return add__0x10$util$MapSet$$T$O(a1);}
    // bridge for method abstract public x10.util.AbstractCollection.remove(T):x10.lang.Boolean
    public boolean
      remove__0x10$util$AbstractCollection$$T$O($T a1){return remove__0x10$util$MapSet$$T$O(a1);}
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final MapSet $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
public x10.util.Map<$T, x10.core.Boolean> map;
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"

        // constructor for non-virtual call
        final public x10.util.MapSet<$T> x10$util$MapSet$$init$S(final x10.util.Map<$T, x10.core.Boolean> map, __0$1x10$util$MapSet$$T$3x10$lang$Boolean$2 $dummy) { {
                                                                                                                                                                            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
super.$init();
                                                                                                                                                                            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"

                                                                                                                                                                            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
this.map = ((x10.util.Map)(map));
                                                                                                                                                                        }
                                                                                                                                                                        return this;
                                                                                                                                                                        }
        
        // constructor
        public x10.util.MapSet<$T> $init(final x10.util.Map<$T, x10.core.Boolean> map, __0$1x10$util$MapSet$$T$3x10$lang$Boolean$2 $dummy){return x10$util$MapSet$$init$S(map, $dummy);}
        
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
public int
                                                                                               size$O(
                                                                                               ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Map<$T, x10.core.Boolean> t59087 =
              ((x10.util.Map)(map));
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Set<$T> t59088 =
              ((x10.util.Set<$T>)
                ((x10.util.Map<$T, x10.core.Boolean>)t59087).keySet());
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final int t59089 =
              ((x10.util.Container<$T>)t59088).size$O();
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
return t59089;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
public boolean
                                                                                               contains__0x10$util$MapSet$$T$O(
                                                                                               final $T v){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Map<$T, x10.core.Boolean> t59090 =
              ((x10.util.Map)(map));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final boolean t59091 =
              x10.core.Boolean.$unbox(((x10.util.Map<$T, x10.core.Boolean>)t59090).containsKey((($T)(v)),$T));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
return t59091;
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
public boolean
                                                                                               add__0x10$util$MapSet$$T$O(
                                                                                               final $T v){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Map<$T, x10.core.Boolean> t59092 =
              ((x10.util.Map)(map));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Box<x10.core.Boolean> t59093 =
              ((x10.util.Box<x10.core.Boolean>)
                ((x10.util.Map<$T, x10.core.Boolean>)t59092).put((($T)(v)),$T,
                                                                 x10.core.Boolean.$box(true),x10.rtt.Types.BOOLEAN));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final boolean t59094 =
              ((t59093) == (null));
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
return t59094;
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
public boolean
                                                                                               remove__0x10$util$MapSet$$T$O(
                                                                                               final $T v){
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Map<$T, x10.core.Boolean> t59095 =
              ((x10.util.Map)(map));
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Box<x10.core.Boolean> t59096 =
              ((x10.util.Box<x10.core.Boolean>)
                ((x10.util.Map<$T, x10.core.Boolean>)t59095).remove((($T)(v)),$T));
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final boolean t59097 =
              ((t59096) != (null));
            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
return t59097;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
public void
                                                                                               clear(
                                                                                               ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Map<$T, x10.core.Boolean> t59098 =
              ((x10.util.Map)(map));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
((x10.util.Map<$T, x10.core.Boolean>)t59098).clear();
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
public x10.lang.Iterator<$T>
                                                                                               iterator(
                                                                                               ){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Map<$T, x10.core.Boolean> t59099 =
              ((x10.util.Map)(map));
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.util.Set<$T> t59100 =
              ((x10.util.Set<$T>)
                ((x10.util.Map<$T, x10.core.Boolean>)t59099).keySet());
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final x10.lang.Iterator<$T> t59101 =
              ((x10.lang.Iterator<$T>)
                ((x10.lang.Iterable<$T>)t59100).iterator());
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
return t59101;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
final public x10.util.MapSet<$T>
                                                                                               x10$util$MapSet$$x10$util$MapSet$this(
                                                                                               ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/MapSet.x10"
return x10.util.MapSet.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __0$1x10$util$MapSet$$T$3x10$lang$Boolean$2 {}
    
}
