package x10.util;

@x10.core.X10Generated public class ArrayList<$T> extends x10.util.AbstractCollection<$T> implements x10.util.List, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ArrayList.class);
    
    public static final x10.rtt.RuntimeType<ArrayList> $RTT = x10.rtt.NamedType.<ArrayList> make(
    "x10.util.ArrayList", /* base class */ArrayList.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.List.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.util.AbstractCollection.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ArrayList $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ArrayList.class + " calling"); } 
        x10.util.AbstractCollection.$_deserialize_body($_obj, $deserializer);
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        x10.util.GrowableIndexedMemoryChunk a = (x10.util.GrowableIndexedMemoryChunk) $deserializer.readRef();
        $_obj.a = a;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ArrayList $_obj = new ArrayList((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (a instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.a);
        } else {
        $serializer.write(this.a);
        }
        
    }
    
    // constructor just for allocation
    public ArrayList(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy, $T);
    x10.util.ArrayList.$initParams(this, $T);
    }
    // dispatcher for method abstract public x10.util.Container.contains(T):x10.lang.Boolean
    public java.lang.Object contains(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(contains__0x10$util$ArrayList$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.add(T):x10.lang.Boolean
    public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(add__0x10$util$ArrayList$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.Collection.remove(T):x10.lang.Boolean
    public java.lang.Object remove(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(remove__0x10$util$ArrayList$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.List.addBefore(i:x10.lang.Int,T):void
    public java.lang.Object addBefore(final int a1, final java.lang.Object a2, final x10.rtt.Type t2) {
    addBefore__1x10$util$ArrayList$$T(a1, ($T)a2);return null;
    }
    // dispatcher for method abstract public x10.lang.Settable.operator()=(i:I,v:V):V
    public java.lang.Object $set(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
    return $set__1x10$util$ArrayList$$T$G(x10.core.Int.$unbox(a1), ($T)a2);
    }
    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1){}:U
    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
    return $apply$G(x10.core.Int.$unbox(a1));
    }
    // dispatcher for method abstract public x10.util.List.indexOf(T):x10.lang.Int
    public java.lang.Object indexOf(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Int.$box(indexOf__0x10$util$ArrayList$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.List.indexOf(x10.lang.Int,T):x10.lang.Int
    public java.lang.Object indexOf(final int a1, final java.lang.Object a2, final x10.rtt.Type t2) {
    return x10.core.Int.$box(indexOf__1x10$util$ArrayList$$T$O(a1, ($T)a2));
    }
    // dispatcher for method abstract public x10.util.List.lastIndexOf(T):x10.lang.Int
    public java.lang.Object lastIndexOf(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Int.$box(lastIndexOf__0x10$util$ArrayList$$T$O(($T)a1));
    }
    // dispatcher for method abstract public x10.util.List.lastIndexOf(x10.lang.Int,T):x10.lang.Int
    public java.lang.Object lastIndexOf(final int a1, final java.lang.Object a2, final x10.rtt.Type t2) {
    return x10.core.Int.$box(lastIndexOf__1x10$util$ArrayList$$T$O(a1, ($T)a2));
    }
    // dispatcher for method abstract public x10.util.List.sort(cmp:(a1:T, a2:T)=>x10.lang.Int):void
    public java.lang.Object sort(final x10.core.fun.Fun_0_2 a1, final x10.rtt.Type t1) {
    sort__0$1x10$util$ArrayList$$T$3x10$util$ArrayList$$T$3x10$lang$Int$2((x10.core.fun.Fun_0_2)a1);return null;
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
      contains__0x10$util$AbstractContainer$$T$O($T a1){return contains__0x10$util$ArrayList$$T$O(a1);}
    // bridge for method abstract public x10.util.AbstractCollection.add(T):x10.lang.Boolean
    public boolean
      add__0x10$util$AbstractCollection$$T$O($T a1){return add__0x10$util$ArrayList$$T$O(a1);}
    // bridge for method abstract public x10.util.AbstractCollection.remove(T):x10.lang.Boolean
    public boolean
      remove__0x10$util$AbstractCollection$$T$O($T a1){return remove__0x10$util$ArrayList$$T$O(a1);}
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final ArrayList $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.util.GrowableIndexedMemoryChunk<$T> a;
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public static <$T>x10.util.ArrayList<$T>
                                                                                                  make__0$1x10$util$ArrayList$$T$2(
                                                                                                  final x10.rtt.Type $T,
                                                                                                  final x10.util.Container<$T> c){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$T> a =
              ((x10.util.ArrayList)(new x10.util.ArrayList<$T>((java.lang.System[]) null, $T).$init()));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.AbstractCollection<$T>)a).addAll__0$1x10$util$AbstractCollection$$T$2$O(((x10.util.Container)(c)));
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return a;
        }
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public boolean
                                                                                                  contains__0x10$util$ArrayList$$T$O(
                                                                                                  final $T v){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57459 =
              0;
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57460 =
                  i57459;
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57461 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57462 =
                  ((x10.util.GrowableIndexedMemoryChunk<$T>)t57461).length$O();
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57463 =
                  ((t57460) < (((int)(t57462))));
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57463)) {
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57448 =
                  ((v) == (null));
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
boolean t57449 =
                   false;
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57448) {
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57450 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57451 =
                      i57459;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57452 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57450).$apply$G((int)(t57451))));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57449 = ((t57452) == (null));
                } else {
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57453 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57454 =
                      i57459;
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57455 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57453).$apply$G((int)(t57454))));
                    
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57449 = ((java.lang.Object)(((java.lang.Object)(v)))).equals(t57455);
                }
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57456 =
                  t57449;
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57456) {
                    
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return true;
                }
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57457 =
                  i57459;
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57458 =
                  ((t57457) + (((int)(1))));
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57459 = t57458;
            }
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return false;
        }
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                  clear(
                                                                                                  ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57241 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57241).clear();
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.util.ArrayList<$T>
                                                                                                  clone(
                                                                                                  ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$T> a =
              ((x10.util.ArrayList)(new x10.util.ArrayList<$T>((java.lang.System[]) null, $T).$init()));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.AbstractCollection<$T>)a).addAll__0$1x10$util$AbstractCollection$$T$2$O(((x10.util.Container)(this)));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return a;
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public boolean
                                                                                                  add__0x10$util$ArrayList$$T$O(
                                                                                                  final $T v){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57242 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57242).add__0x10$util$GrowableIndexedMemoryChunk$$T((($T)(v)));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return true;
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public boolean
                                                                                                  remove__0x10$util$ArrayList$$T$O(
                                                                                                  final $T v){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57476 =
              0;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57477 =
                  i57476;
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57478 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57479 =
                  ((x10.util.GrowableIndexedMemoryChunk<$T>)t57478).length$O();
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57480 =
                  ((t57477) < (((int)(t57479))));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57480)) {
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57464 =
                  ((v) == (null));
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
boolean t57465 =
                   false;
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57464) {
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57466 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57467 =
                      i57476;
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57468 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57466).$apply$G((int)(t57467))));
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57465 = ((t57468) == (null));
                } else {
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57469 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57470 =
                      i57476;
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57471 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57469).$apply$G((int)(t57470))));
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57465 = ((java.lang.Object)(((java.lang.Object)(v)))).equals(t57471);
                }
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57472 =
                  t57465;
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57472) {
                    
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57473 =
                      i57476;
                    
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.removeAt$G((int)(t57473));
                    
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return true;
                }
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57474 =
                  i57476;
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57475 =
                  ((t57474) + (((int)(1))));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57476 = t57475;
            }
            
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return false;
        }
        
        
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                  addBefore__1x10$util$ArrayList$$T(
                                                                                                  final int i,
                                                                                                  final $T v){
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57260 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57260).add__0x10$util$GrowableIndexedMemoryChunk$$T((($T)(v)));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57489 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57490 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t57489).length$O();
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int j57491 =
              ((t57490) - (((int)(1))));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57492 =
                  j57491;
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57493 =
                  ((t57492) > (((int)(i))));
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57493)) {
                    
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57481 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57482 =
                  j57491;
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57483 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57484 =
                  j57491;
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57485 =
                  ((t57484) - (((int)(1))));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57486 =
                  (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57483).$apply$G((int)(t57485))));
                
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57481).$set__1x10$util$GrowableIndexedMemoryChunk$$T((int)(t57482),
                                                                                                                                                                                                        (($T)(t57486)));
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57487 =
                  j57491;
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57488 =
                  ((t57487) - (((int)(1))));
                
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
j57491 = t57488;
            }
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57274 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57274).$set__1x10$util$GrowableIndexedMemoryChunk$$T((int)(i),
                                                                                                                                                                                                    (($T)(v)));
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                  $set__1x10$util$ArrayList$$T$G(
                                                                                                  final int i,
                                                                                                  final $T v){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57275 =
              (($T)(this.set__0x10$util$ArrayList$$T$G((($T)(v)),
                                                       (int)(i))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57275;
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                  set__0x10$util$ArrayList$$T$G(
                                                                                                  final $T v,
                                                                                                  final int i){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57276 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57276).$set__1x10$util$GrowableIndexedMemoryChunk$$T((int)(i),
                                                                                                                                                                                                    (($T)(v)));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return v;
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                  removeAt$G(
                                                                                                  final int i){
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57277 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T v =
              (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57277).$apply$G((int)(i))));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int j57502 =
              ((i) + (((int)(1))));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                         true;
                                                                                                         ) {
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57503 =
                  j57502;
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57504 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57505 =
                  ((x10.util.GrowableIndexedMemoryChunk<$T>)t57504).length$O();
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57506 =
                  ((t57503) < (((int)(t57505))));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57506)) {
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57494 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57495 =
                  j57502;
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57496 =
                  ((t57495) - (((int)(1))));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57497 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57498 =
                  j57502;
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57499 =
                  (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57497).$apply$G((int)(t57498))));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57494).$set__1x10$util$GrowableIndexedMemoryChunk$$T((int)(t57496),
                                                                                                                                                                                                        (($T)(t57499)));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57500 =
                  j57502;
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57501 =
                  ((t57500) + (((int)(1))));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
j57502 = t57501;
            }
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57291 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)t57291).removeLast();
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return v;
        }
        
        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                  $apply$G(
                                                                                                  final int i){
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57292 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57293 =
              (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57292).$apply$G((int)(i))));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57293;
        }
        
        
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                  get$G(
                                                                                                  final int i){
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57294 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57295 =
              (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57294).$apply$G((int)(i))));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57295;
        }
        
        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int
                                                                                                  size$O(
                                                                                                  ){
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57296 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57297 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t57296).length$O();
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57297;
        }
        
        
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public boolean
                                                                                                  isEmpty$O(
                                                                                                  ){
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57298 =
              this.size$O();
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57299 =
              ((int) t57298) ==
            ((int) 0);
            
//#line 88 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57299;
        }
        
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.array.Array<$T>
                                                                                                  toArray(
                                                                                                  ){
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57300 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.array.Array<$T> t57301 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   ((x10.util.GrowableIndexedMemoryChunk<$T>)t57300).toArray())));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57301;
        }
        
        
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.core.IndexedMemoryChunk<$T>
                                                                                                  toIndexedMemoryChunk(
                                                                                                  ){
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57302 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.core.IndexedMemoryChunk<$T> t57303 =
              ((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.util.GrowableIndexedMemoryChunk<$T>)t57302).toIndexedMemoryChunk()));
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57303;
        }
        
        
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
// creation method for java code (1-phase java constructor)
        public ArrayList(final x10.rtt.Type $T){this((java.lang.System[]) null, $T);
                                                    $init();}
        
        // constructor for non-virtual call
        final public x10.util.ArrayList<$T> x10$util$ArrayList$$init$S() { {
                                                                                  
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
super.$init();
                                                                                  
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"

                                                                                  
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57304 =
                                                                                    ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<$T>((java.lang.System[]) null, $T).$init()));
                                                                                  
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.a = ((x10.util.GrowableIndexedMemoryChunk)(t57304));
                                                                              }
                                                                              return this;
                                                                              }
        
        // constructor
        public x10.util.ArrayList<$T> $init(){return x10$util$ArrayList$$init$S();}
        
        
        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
// creation method for java code (1-phase java constructor)
        public ArrayList(final x10.rtt.Type $T,
                         final int size){this((java.lang.System[]) null, $T);
                                             $init(size);}
        
        // constructor for non-virtual call
        final public x10.util.ArrayList<$T> x10$util$ArrayList$$init$S(final int size) { {
                                                                                                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
super.$init();
                                                                                                
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"

                                                                                                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57305 =
                                                                                                  ((x10.util.GrowableIndexedMemoryChunk)(new x10.util.GrowableIndexedMemoryChunk<$T>((java.lang.System[]) null, $T).$init(((int)(size)))));
                                                                                                
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.a = ((x10.util.GrowableIndexedMemoryChunk)(t57305));
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10.util.ArrayList<$T> $init(final int size){return x10$util$ArrayList$$init$S(size);}
        
        
        
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                   removeFirst$G(
                                                                                                   ){
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57306 =
              (($T)(this.removeAt$G((int)(0))));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57306;
        }
        
        
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                   removeLast$G(
                                                                                                   ){
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57307 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57308 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t57307).length$O();
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57309 =
              ((t57308) - (((int)(1))));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57310 =
              (($T)(this.removeAt$G((int)(t57309))));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57310;
        }
        
        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                   getFirst$G(
                                                                                                   ){
            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57311 =
              (($T)(this.get$G((int)(0))));
            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57311;
        }
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $T
                                                                                                   getLast$G(
                                                                                                   ){
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57312 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57313 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t57312).length$O();
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57314 =
              ((t57313) - (((int)(1))));
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57315 =
              (($T)(this.get$G((int)(t57314))));
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57315;
        }
        
        
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.util.List<x10.core.Int>
                                                                                                   indices(
                                                                                                   ){
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<x10.core.Int> l =
              ((x10.util.ArrayList)(new x10.util.ArrayList<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init()));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57510 =
              0;
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                          true;
                                                                                                          ) {
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57511 =
                  i57510;
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57512 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57513 =
                  ((x10.util.GrowableIndexedMemoryChunk<$T>)t57512).length$O();
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57514 =
                  ((t57511) < (((int)(t57513))));
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57514)) {
                    
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57507 =
                  i57510;
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.ArrayList<x10.core.Int>)l).add__0x10$util$ArrayList$$T$O(x10.core.Int.$box(t57507));
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57508 =
                  i57510;
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57509 =
                  ((t57508) + (((int)(1))));
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57510 = t57509;
            }
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return l;
        }
        
        
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.util.List<$T>
                                                                                                   subList(
                                                                                                   final int begin,
                                                                                                   final int end){
            
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$T> l =
              ((x10.util.ArrayList)(new x10.util.ArrayList<$T>((java.lang.System[]) null, $T).$init()));
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57520 =
              begin;
            
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                          true;
                                                                                                          ) {
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57521 =
                  i57520;
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57522 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57523 =
                  ((x10.util.GrowableIndexedMemoryChunk<$T>)t57522).length$O();
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
boolean t57524 =
                  ((t57521) < (((int)(t57523))));
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57524) {
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57525 =
                      i57520;
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57524 = ((t57525) < (((int)(end))));
                }
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57526 =
                  t57524;
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57526)) {
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57515 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57516 =
                  i57520;
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57517 =
                  (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57515).$apply$G((int)(t57516))));
                
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.ArrayList<$T>)l).add__0x10$util$ArrayList$$T$O((($T)(t57517)));
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57518 =
                  i57520;
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57519 =
                  ((t57518) + (((int)(1))));
                
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57520 = t57519;
            }
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return l;
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int
                                                                                                   indexOf__0x10$util$ArrayList$$T$O(
                                                                                                   final $T v){
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57336 =
              this.indexOf__1x10$util$ArrayList$$T$O((int)(0),
                                                     (($T)(v)));
            
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57336;
        }
        
        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int
                                                                                                   indexOf__1x10$util$ArrayList$$T$O(
                                                                                                   final int index,
                                                                                                   final $T v){
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57539 =
              index;
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                          true;
                                                                                                          ) {
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57540 =
                  i57539;
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57541 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57542 =
                  ((x10.util.GrowableIndexedMemoryChunk<$T>)t57541).length$O();
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57543 =
                  ((t57540) < (((int)(t57542))));
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57543)) {
                    
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57527 =
                  ((v) == (null));
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
boolean t57528 =
                   false;
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57527) {
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57529 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57530 =
                      i57539;
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57531 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57529).$apply$G((int)(t57530))));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57528 = ((t57531) == (null));
                } else {
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57532 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57533 =
                      i57539;
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57534 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57532).$apply$G((int)(t57533))));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57528 = ((java.lang.Object)(((java.lang.Object)(v)))).equals(t57534);
                }
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57535 =
                  t57528;
                
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57535) {
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57536 =
                      i57539;
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57536;
                }
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57537 =
                  i57539;
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57538 =
                  ((t57537) + (((int)(1))));
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57539 = t57538;
            }
            
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return -1;
        }
        
        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int
                                                                                                   lastIndexOf__0x10$util$ArrayList$$T$O(
                                                                                                   final $T v){
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57354 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57355 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t57354).length$O();
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57356 =
              ((t57355) - (((int)(1))));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57357 =
              this.lastIndexOf__1x10$util$ArrayList$$T$O((int)(t57356),
                                                         (($T)(v)));
            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57357;
        }
        
        
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int
                                                                                                   lastIndexOf__1x10$util$ArrayList$$T$O(
                                                                                                   final int index,
                                                                                                   final $T v){
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57556 =
              index;
            
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                          true;
                                                                                                          ) {
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57557 =
                  i57556;
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57558 =
                  ((t57557) >= (((int)(0))));
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57558)) {
                    
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57544 =
                  ((v) == (null));
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
boolean t57545 =
                   false;
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57544) {
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57546 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57547 =
                      i57556;
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57548 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57546).$apply$G((int)(t57547))));
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57545 = ((t57548) == (null));
                } else {
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57549 =
                      ((x10.util.GrowableIndexedMemoryChunk)(a));
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57550 =
                      i57556;
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57551 =
                      (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)t57549).$apply$G((int)(t57550))));
                    
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
t57545 = ((java.lang.Object)(((java.lang.Object)(v)))).equals(t57551);
                }
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57552 =
                  t57545;
                
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57552) {
                    
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57553 =
                      i57556;
                    
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57553;
                }
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57554 =
                  i57556;
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57555 =
                  ((t57554) - (((int)(1))));
                
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57556 = t57555;
            }
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return -1;
        }
        
        
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.array.Array<$T>
                                                                                                   moveSectionToArray(
                                                                                                   final int i,
                                                                                                   final int j){
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57373 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.array.Array<$T> t57374 =
              ((x10.array.Array)(((x10.array.Array<$T>)
                                   ((x10.util.GrowableIndexedMemoryChunk<$T>)t57373).moveSectionToArray((int)(i),
                                                                                                        (int)(j)))));
            
//#line 146 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57374;
        }
        
        
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
@x10.core.X10Generated public static class It<$S> extends x10.core.Ref implements x10.util.ListIterator, x10.x10rt.X10JavaSerializable
                                                                                                 {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, It.class);
            
            public static final x10.rtt.RuntimeType<It> $RTT = x10.rtt.NamedType.<It> make(
            "x10.util.ArrayList.It", /* base class */It.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.ListIterator.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $S;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(It $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + It.class + " calling"); } 
                $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.i = $deserializer.readInt();
                x10.util.ArrayList al = (x10.util.ArrayList) $deserializer.readRef();
                $_obj.al = al;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                It $_obj = new It((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
                $serializer.write(this.i);
                if (al instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.al);
                } else {
                $serializer.write(this.al);
                }
                
            }
            
            // constructor just for allocation
            public It(final java.lang.System[] $dummy, final x10.rtt.Type $S) { 
            super($dummy);
            x10.util.ArrayList.It.$initParams(this, $S);
            }
            // dispatcher for method abstract public x10.util.ListIterator.set(T):void
            public java.lang.Object set(final java.lang.Object a1, final x10.rtt.Type t1) {
            set__0x10$util$ArrayList$It$$S(($S)a1);return null;
            }
            // dispatcher for method abstract public x10.util.ListIterator.add(T):void
            public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
            add__0x10$util$ArrayList$It$$S(($S)a1);return null;
            }
            
                private x10.rtt.Type $S;
                // initializer of type parameters
                public static void $initParams(final It $this, final x10.rtt.Type $S) {
                $this.$S = $S;
                }
                
                
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int i;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.util.ArrayList<$S> al;
                
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
// creation method for java code (1-phase java constructor)
                public It(final x10.rtt.Type $S,
                          final x10.util.ArrayList<$S> al, __0$1x10$util$ArrayList$It$$S$2 $dummy){this((java.lang.System[]) null, $S);
                                                                                                       $init(al, (x10.util.ArrayList.It.__0$1x10$util$ArrayList$It$$S$2) null);}
                
                // constructor for non-virtual call
                final public x10.util.ArrayList.It<$S> x10$util$ArrayList$It$$init$S(final x10.util.ArrayList<$S> al, __0$1x10$util$ArrayList$It$$S$2 $dummy) { {
                                                                                                                                                                       
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.$init(((x10.util.ArrayList<$S>)(al)),
                                                                                                                                                                                                                                                                           ((int)(-1)), (x10.util.ArrayList.It.__0$1x10$util$ArrayList$It$$S$2) null);
                                                                                                                                                                   }
                                                                                                                                                                   return this;
                                                                                                                                                                   }
                
                // constructor
                public x10.util.ArrayList.It<$S> $init(final x10.util.ArrayList<$S> al, __0$1x10$util$ArrayList$It$$S$2 $dummy){return x10$util$ArrayList$It$$init$S(al, $dummy);}
                
                
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
// creation method for java code (1-phase java constructor)
                public It(final x10.rtt.Type $S,
                          final x10.util.ArrayList<$S> al,
                          final int i, __0$1x10$util$ArrayList$It$$S$2 $dummy){this((java.lang.System[]) null, $S);
                                                                                   $init(al,i, (x10.util.ArrayList.It.__0$1x10$util$ArrayList$It$$S$2) null);}
                
                // constructor for non-virtual call
                final public x10.util.ArrayList.It<$S> x10$util$ArrayList$It$$init$S(final x10.util.ArrayList<$S> al,
                                                                                     final int i, __0$1x10$util$ArrayList$It$$S$2 $dummy) { {
                                                                                                                                                   
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"

                                                                                                                                                   
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"

                                                                                                                                                   
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.__fieldInitializers56906();
                                                                                                                                                   
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.al = ((x10.util.ArrayList)(al));
                                                                                                                                                   
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.i = i;
                                                                                                                                               }
                                                                                                                                               return this;
                                                                                                                                               }
                
                // constructor
                public x10.util.ArrayList.It<$S> $init(final x10.util.ArrayList<$S> al,
                                                       final int i, __0$1x10$util$ArrayList$It$$S$2 $dummy){return x10$util$ArrayList$It$$init$S(al,i, $dummy);}
                
                
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public boolean
                                                                                                           hasNext$O(
                                                                                                           ){
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57375 =
                      i;
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57377 =
                      ((t57375) + (((int)(1))));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$S> t57376 =
                      ((x10.util.ArrayList)(al));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57378 =
                      ((x10.util.ArrayList<$S>)t57376).size$O();
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57379 =
                      ((t57377) < (((int)(t57378))));
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57379;
                }
                
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int
                                                                                                           nextIndex$O(
                                                                                                           ){
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList.It<$S> x57217 =
                      ((x10.util.ArrayList.It)(this));
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
;
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57380 =
                      ((x10.util.ArrayList.It<$S>)x57217).
                        i;
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57381 =
                      ((t57380) + (((int)(1))));
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57382 =
                      x57217.i = t57381;
                    
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57382;
                }
                
                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $S
                                                                                                           next$G(
                                                                                                           ){
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$S> t57383 =
                      ((x10.util.ArrayList)(al));
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$S> t57386 =
                      ((x10.util.GrowableIndexedMemoryChunk)(((x10.util.ArrayList<$S>)t57383).
                                                               a));
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList.It<$S> x57219 =
                      ((x10.util.ArrayList.It)(this));
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
;
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57384 =
                      ((x10.util.ArrayList.It<$S>)x57219).
                        i;
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57385 =
                      ((t57384) + (((int)(1))));
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57387 =
                      x57219.i = t57385;
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $S t57388 =
                      (($S)(((x10.util.GrowableIndexedMemoryChunk<$S>)t57386).$apply$G((int)(t57387))));
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57388;
                }
                
                
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public boolean
                                                                                                           hasPrevious$O(
                                                                                                           ){
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57389 =
                      i;
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57390 =
                      ((t57389) - (((int)(1))));
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57391 =
                      ((t57390) >= (((int)(0))));
                    
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57391;
                }
                
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public int
                                                                                                           previousIndex$O(
                                                                                                           ){
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList.It<$S> x57221 =
                      ((x10.util.ArrayList.It)(this));
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
;
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57392 =
                      ((x10.util.ArrayList.It<$S>)x57221).
                        i;
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57393 =
                      ((t57392) - (((int)(1))));
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57394 =
                      x57221.i = t57393;
                    
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57394;
                }
                
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public $S
                                                                                                           previous$G(
                                                                                                           ){
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$S> t57395 =
                      ((x10.util.ArrayList)(al));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$S> t57398 =
                      ((x10.util.GrowableIndexedMemoryChunk)(((x10.util.ArrayList<$S>)t57395).
                                                               a));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList.It<$S> x57223 =
                      ((x10.util.ArrayList.It)(this));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
;
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57396 =
                      ((x10.util.ArrayList.It<$S>)x57223).
                        i;
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57397 =
                      ((t57396) - (((int)(1))));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57399 =
                      x57223.i = t57397;
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $S t57400 =
                      (($S)(((x10.util.GrowableIndexedMemoryChunk<$S>)t57398).$apply$G((int)(t57399))));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57400;
                }
                
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                           remove(
                                                                                                           ){
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$S> t57401 =
                      ((x10.util.ArrayList)(al));
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57402 =
                      i;
                    
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.ArrayList<$S>)t57401).removeAt$G((int)(t57402));
                }
                
                
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                           set__0x10$util$ArrayList$It$$S(
                                                                                                           final $S v){
                    
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$S> t57403 =
                      ((x10.util.ArrayList)(al));
                    
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57404 =
                      i;
                    
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.ArrayList<$S>)t57403).set__0x10$util$ArrayList$$T$G((($S)(v)),
                                                                                                                                                                            (int)(t57404));
                }
                
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                           add__0x10$util$ArrayList$It$$S(
                                                                                                           final $S v){
                    
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList<$S> t57405 =
                      ((x10.util.ArrayList)(al));
                    
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57406 =
                      i;
                    
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.ArrayList<$S>)t57405).addBefore__1x10$util$ArrayList$$T((int)(t57406),
                                                                                                                                                                                (($S)(v)));
                }
                
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final public x10.util.ArrayList.It<$S>
                                                                                                           x10$util$ArrayList$It$$x10$util$ArrayList$It$this(
                                                                                                           ){
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return x10.util.ArrayList.It.this;
                }
                
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final public void
                                                                                                           __fieldInitializers56906(
                                                                                                           ){
                    
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.i = 0;
                }
            // synthetic type for parameter mangling
            public abstract static class __0$1x10$util$ArrayList$It$$S$2 {}
            
        }
        
        
        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.util.ListIterator<$T>
                                                                                                   iterator(
                                                                                                   ){
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList.It<$T> t57407 =
              ((x10.util.ArrayList.It)(new x10.util.ArrayList.It<$T>((java.lang.System[]) null, $T).$init(((x10.util.ArrayList<$T>)(this)), (x10.util.ArrayList.It.__0$1x10$util$ArrayList$It$$S$2) null)));
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57407;
        }
        
        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public x10.util.ListIterator<$T>
                                                                                                   iteratorFrom(
                                                                                                   final int i){
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.ArrayList.It<$T> t57408 =
              ((x10.util.ArrayList.It)(new x10.util.ArrayList.It<$T>((java.lang.System[]) null, $T).$init(((x10.util.ArrayList<$T>)(this)),
                                                                                                          ((int)(i)), (x10.util.ArrayList.It.__0$1x10$util$ArrayList$It$$S$2) null)));
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57408;
        }
        
        
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                   reverse(
                                                                                                   ){
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57409 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int length =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t57409).length$O();
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57566 =
              0;
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                          true;
                                                                                                          ) {
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57567 =
                  i57566;
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57568 =
                  ((length) / (((int)(2))));
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57569 =
                  ((t57567) < (((int)(t57568))));
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57569)) {
                    
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57559 =
                  ((x10.util.GrowableIndexedMemoryChunk)(a));
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57560 =
                  i57566;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57561 =
                  ((length) - (((int)(1))));
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57562 =
                  i57566;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57563 =
                  ((t57561) - (((int)(t57562))));
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.exch__0$1x10$util$ArrayList$$T$2(((x10.util.GrowableIndexedMemoryChunk)(t57559)),
                                                                                                                                               (int)(t57560),
                                                                                                                                               (int)(t57563));
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57564 =
                  i57566;
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57565 =
                  ((t57564) + (((int)(1))));
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57566 = t57565;
            }
        }
        
        
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                   sort(
                                                                                                   ){
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.core.fun.Fun_0_2<$T,$T,x10.core.Int> t57423 =
              ((x10.core.fun.Fun_0_2)(new x10.util.ArrayList.$Closure$162<$T>($T)));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
this.sort__0$1x10$util$ArrayList$$T$3x10$util$ArrayList$$T$3x10$lang$Int$2(((x10.core.fun.Fun_0_2)(t57423)));
        }
        
        
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public void
                                                                                                   sort__0$1x10$util$ArrayList$$T$3x10$util$ArrayList$$T$3x10$lang$Int$2(
                                                                                                   final x10.core.fun.Fun_0_2 cmp){
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57424 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.core.IndexedMemoryChunk<$T> t57427 =
              ((x10.core.IndexedMemoryChunk)(((x10.core.IndexedMemoryChunk)x10.rtt.Types.asStruct(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, $T),((x10.util.GrowableIndexedMemoryChunk<$T>)t57424).imc()))));
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.GrowableIndexedMemoryChunk<$T> t57425 =
              ((x10.util.GrowableIndexedMemoryChunk)(a));
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57426 =
              ((x10.util.GrowableIndexedMemoryChunk<$T>)t57425).length$O();
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57428 =
              ((t57426) - (((int)(1))));
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
x10.util.ArrayUtils.<$T>qsort__0$1x10$util$ArrayUtils$$T$2__3$1x10$util$ArrayUtils$$T$3x10$util$ArrayUtils$$T$3x10$lang$Int$2($T, ((x10.core.IndexedMemoryChunk)(t57427)),
                                                                                                                                                                                                                                   (int)(0),
                                                                                                                                                                                                                                   (int)(t57428),
                                                                                                                                                                                                                                   ((x10.core.fun.Fun_0_2)(cmp)));
        }
        
        
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
private void
                                                                                                   exch__0$1x10$util$ArrayList$$T$2(
                                                                                                   final x10.util.GrowableIndexedMemoryChunk a,
                                                                                                   final int i,
                                                                                                   final int j){
            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T temp =
              (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)a).$apply$G((int)(i))));
            
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57429 =
              (($T)(((x10.util.GrowableIndexedMemoryChunk<$T>)a).$apply$G((int)(j))));
            
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)a).$set__1x10$util$GrowableIndexedMemoryChunk$$T((int)(i),
                                                                                                                                                                                                (($T)(t57429)));
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
((x10.util.GrowableIndexedMemoryChunk<$T>)a).$set__1x10$util$GrowableIndexedMemoryChunk$$T((int)(j),
                                                                                                                                                                                                (($T)(temp)));
        }
        
        public static <$T>void
          exch$P__0$1x10$util$ArrayList$$T$2__3$1x10$util$ArrayList$$T$2(
          final x10.rtt.Type $T,
          final x10.util.GrowableIndexedMemoryChunk<$T> a,
          final int i,
          final int j,
          final x10.util.ArrayList<$T> ArrayList){
            ((x10.util.ArrayList<$T>)ArrayList).exch__0$1x10$util$ArrayList$$T$2(((x10.util.GrowableIndexedMemoryChunk)(a)),
                                                                                 (int)(i),
                                                                                 (int)(j));
        }
        
        
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
public java.lang.String
                                                                                                   toString(
                                                                                                   ){
            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final x10.util.StringBuilder sb =
              ((x10.util.StringBuilder)(new x10.util.StringBuilder((java.lang.System[]) null).$init()));
            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
sb.add(((java.lang.String)("[")));
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57430 =
              this.size$O();
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int sz =
              x10.lang.Math.min$O((int)(t57430),
                                  (int)(10));
            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
int i57577 =
              0;
            
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
for (;
                                                                                                          true;
                                                                                                          ) {
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57578 =
                  i57577;
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57579 =
                  ((t57578) < (((int)(sz))));
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (!(t57579)) {
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
break;
                }
                
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57570 =
                  i57577;
                
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57571 =
                  ((t57570) > (((int)(0))));
                
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57571) {
                    
//#line 241 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
sb.add(((java.lang.String)(",")));
                }
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57572 =
                  i57577;
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final $T t57573 =
                  (($T)(this.$apply$G((int)(t57572))));
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final java.lang.String t57574 =
                  (("") + (t57573));
                
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
sb.add(((java.lang.String)(t57574)));
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57575 =
                  i57577;
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57576 =
                  ((t57575) + (((int)(1))));
                
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
i57577 = t57576;
            }
            
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57441 =
              this.size$O();
            
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final boolean t57446 =
              ((sz) < (((int)(t57441))));
            
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
if (t57446) {
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57442 =
                  this.size$O();
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57443 =
                  ((t57442) - (((int)(sz))));
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final java.lang.String t57444 =
                  (("...(omitted ") + ((x10.core.Int.$box(t57443))));
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final java.lang.String t57445 =
                  ((t57444) + (" elements)"));
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
sb.add(((java.lang.String)(t57445)));
            }
            
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
sb.add(((java.lang.String)("]")));
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final java.lang.String t57447 =
              sb.toString();
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57447;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final public x10.util.ArrayList<$T>
                                                                                                  x10$util$ArrayList$$x10$util$ArrayList$this(
                                                                                                  ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return x10.util.ArrayList.this;
        }
        
        @x10.core.X10Generated public static class $Closure$162<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_2, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$162.class);
            
            public static final x10.rtt.RuntimeType<$Closure$162> $RTT = x10.rtt.StaticFunType.<$Closure$162> make(
            /* base class */$Closure$162.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_2.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(0), x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$162 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$162.class + " calling"); } 
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$162 $_obj = new $Closure$162((java.lang.System[]) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                
            }
            
            // constructor just for allocation
            public $Closure$162(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
            super($dummy);
            x10.util.ArrayList.$Closure$162.$initParams(this, $T);
            }
            // dispatcher for method abstract public (a1:Z1, a2:Z2)=>U.operator()(a1:Z1,a2:Z2):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Int.$box($apply__0x10$util$ArrayList$$Closure$162$$T__1x10$util$ArrayList$$Closure$162$$T$O(($T)a1, ($T)a2));
            }
            
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$162 $this, final x10.rtt.Type $T) {
                $this.$T = $T;
                }
                
                
                public int
                  $apply__0x10$util$ArrayList$$Closure$162$$T__1x10$util$ArrayList$$Closure$162$$T$O(
                  final $T x,
                  final $T y){
                    
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final java.lang.Comparable<$T> t57421 =
                      x10.rtt.Types.<java.lang.Comparable<$T>> castConversion(x,x10.rtt.ParameterizedType.make(x10.rtt.Types.COMPARABLE, $T));
                    
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
final int t57422 =
                      ((java.lang.Comparable<$T>)(t57421)).compareTo(y);
                    
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/ArrayList.x10"
return t57422;
                }
                
                public $Closure$162(final x10.rtt.Type $T) {x10.util.ArrayList.$Closure$162.$initParams(this, $T);
                                                                 {
                                                                    
                                                                }}
                
            }
            
        
    }
    