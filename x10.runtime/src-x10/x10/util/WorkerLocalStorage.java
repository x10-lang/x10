package x10.util;

@x10.core.X10Generated public class WorkerLocalStorage<$Key, $Value> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, WorkerLocalStorage.class);
    
    public static final x10.rtt.RuntimeType<WorkerLocalStorage> $RTT = x10.rtt.NamedType.<WorkerLocalStorage> make(
    "x10.util.WorkerLocalStorage", /* base class */WorkerLocalStorage.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(WorkerLocalStorage $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + WorkerLocalStorage.class + " calling"); } 
        $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
        x10.lang.PlaceLocalHandle store = (x10.lang.PlaceLocalHandle) $deserializer.readRef();
        $_obj.store = store;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        WorkerLocalStorage $_obj = new WorkerLocalStorage((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
        if (store instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.store);
        } else {
        $serializer.write(this.store);
        }
        
    }
    
    // constructor just for allocation
    public WorkerLocalStorage(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
    super($dummy);
    x10.util.WorkerLocalStorage.$initParams(this, $Key, $Value);
    }
    
        private x10.rtt.Type $Key;
        private x10.rtt.Type $Value;
        // initializer of type parameters
        public static void $initParams(final WorkerLocalStorage $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
        $this.$Key = $Key;
        $this.$Value = $Value;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
public x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>> store;
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
public x10.util.Box<$Value>
                                                                                                           get__0x10$util$WorkerLocalStorage$$Key(
                                                                                                           final $Key key){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final int id =
              x10.lang.Runtime.workerId$O();
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>> t63633 =
              ((x10.lang.PlaceLocalHandle)(store));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.array.Array<x10.util.HashMap<$Key, $Value>> localStore =
              ((x10.array.Array)(((x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>>)t63633).$apply$G()));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63634 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final boolean t63635 =
              ((null) == (t63634));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
if (t63635) {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return null;
            }
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63636 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.Box<$Value> t63637 =
              ((x10.util.Box<$Value>)
                ((x10.util.HashMap<$Key, $Value>)t63636).get__0x10$util$HashMap$$K((($Key)(key))));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return t63637;
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
public $Value
                                                                                                           getOrElse__0x10$util$WorkerLocalStorage$$Key__1x10$util$WorkerLocalStorage$$Value$G(
                                                                                                           final $Key key,
                                                                                                           final $Value value){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final int id =
              x10.lang.Runtime.workerId$O();
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>> t63638 =
              ((x10.lang.PlaceLocalHandle)(store));
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.array.Array<x10.util.HashMap<$Key, $Value>> localStore =
              ((x10.array.Array)(((x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>>)t63638).$apply$G()));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63639 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final boolean t63640 =
              ((null) == (t63639));
            
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
if (t63640) {
                
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return value;
            }
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63641 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final $Value t63642 =
              (($Value)(((x10.util.HashMap<$Key, $Value>)t63641).getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G((($Key)(key)),
                                                                                                                         (($Value)(value)))));
            
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return t63642;
        }
        
        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
public $Value
                                                                                                           getOrThrow__0x10$util$WorkerLocalStorage$$Key$G(
                                                                                                           final $Key key){
            
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final int id =
              x10.lang.Runtime.workerId$O();
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>> t63643 =
              ((x10.lang.PlaceLocalHandle)(store));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.array.Array<x10.util.HashMap<$Key, $Value>> localStore =
              ((x10.array.Array)(((x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>>)t63643).$apply$G()));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63644 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final boolean t63646 =
              ((null) == (t63644));
            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
if (t63646) {
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.NoSuchElementException t63645 =
                  ((x10.util.NoSuchElementException)(new x10.util.NoSuchElementException(((java.lang.String)("Not found")))));
                
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
throw t63645;
            }
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63647 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final $Value t63648 =
              (($Value)(((x10.util.HashMap<$Key, $Value>)t63647).getOrThrow__0x10$util$HashMap$$K$G((($Key)(key)))));
            
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return t63648;
        }
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
public x10.util.Box<$Value>
                                                                                                           put__0x10$util$WorkerLocalStorage$$Key__1x10$util$WorkerLocalStorage$$Value(
                                                                                                           final $Key key,
                                                                                                           final $Value value){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final int id =
              x10.lang.Runtime.workerId$O();
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>> t63649 =
              ((x10.lang.PlaceLocalHandle)(store));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.array.Array<x10.util.HashMap<$Key, $Value>> localStore =
              ((x10.array.Array)(((x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>>)t63649).$apply$G()));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63650 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final boolean t63652 =
              ((null) == (t63650));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
if (t63652) {
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63651 =
                  ((x10.util.HashMap)(new x10.util.HashMap<$Key, $Value>((java.lang.System[]) null, $Key, $Value).$init()));
                
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$set__1x10$array$Array$$T$G((int)(id),
                                                                                                                                                                                                           ((x10.util.HashMap)(t63651)));
            }
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63653 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.Box<$Value> t63654 =
              ((x10.util.Box<$Value>)
                ((x10.util.HashMap<$Key, $Value>)t63653).put__0x10$util$HashMap$$K__1x10$util$HashMap$$V((($Key)(key)),
                                                                                                         (($Value)(value))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return t63654;
        }
        
        
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
public x10.util.Box<$Value>
                                                                                                           remove__0x10$util$WorkerLocalStorage$$Key(
                                                                                                           final $Key key){
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final int id =
              x10.lang.Runtime.workerId$O();
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>> t63655 =
              ((x10.lang.PlaceLocalHandle)(store));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.array.Array<x10.util.HashMap<$Key, $Value>> localStore =
              ((x10.array.Array)(((x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>>)t63655).$apply$G()));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63656 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final boolean t63657 =
              ((null) == (t63656));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
if (t63657) {
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return null;
            }
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63658 =
              ((x10.array.Array<x10.util.HashMap<$Key, $Value>>)localStore).$apply$G((int)(id));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.Box<$Value> t63659 =
              ((x10.util.Box<$Value>)
                ((x10.util.HashMap<$Key, $Value>)t63658).remove__0x10$util$HashMap$$K((($Key)(key))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return t63659;
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final public x10.util.WorkerLocalStorage<$Key, $Value>
                                                                                                           x10$util$WorkerLocalStorage$$x10$util$WorkerLocalStorage$this(
                                                                                                           ){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return x10.util.WorkerLocalStorage.this;
        }
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
// creation method for java code (1-phase java constructor)
        public WorkerLocalStorage(final x10.rtt.Type $Key,
                                  final x10.rtt.Type $Value){this((java.lang.System[]) null, $Key, $Value);
                                                                 $init();}
        
        // constructor for non-virtual call
        final public x10.util.WorkerLocalStorage<$Key, $Value> x10$util$WorkerLocalStorage$$init$S() { {
                                                                                                              
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"

                                                                                                              
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"

                                                                                                              
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
this.__fieldInitializers63632();
                                                                                                          }
                                                                                                          return this;
                                                                                                          }
        
        // constructor
        public x10.util.WorkerLocalStorage<$Key, $Value> $init(){return x10$util$WorkerLocalStorage$$init$S();}
        
        
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final public void
                                                                                                           __fieldInitializers63632(
                                                                                                           ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.array.PlaceGroup.WorldPlaceGroup t63663 =
              ((x10.array.PlaceGroup.WorldPlaceGroup)(x10.array.PlaceGroup.getInitialized$WORLD()));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.core.fun.Fun_0_0<x10.array.Array<x10.util.HashMap<$Key, $Value>>> t63664 =
              ((x10.core.fun.Fun_0_0)(new x10.util.WorkerLocalStorage.$Closure$175<$Key, $Value>($Key, $Value)));
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.lang.PlaceLocalHandle<x10.array.Array<x10.util.HashMap<$Key, $Value>>> t63665 =
              x10.lang.PlaceLocalHandle.<x10.array.Array<x10.util.HashMap<$Key, $Value>>>make__1$1x10$lang$PlaceLocalHandle$$T$2(x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.rtt.ParameterizedType.make(x10.util.HashMap.$RTT, $Key, $Value)), ((x10.array.PlaceGroup)(t63663)),
                                                                                                                                 ((x10.core.fun.Fun_0_0)(t63664)));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
this.store = t63665;
        }
        
        @x10.core.X10Generated public static class $Closure$175<$Key, $Value> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$175.class);
            
            public static final x10.rtt.RuntimeType<$Closure$175> $RTT = x10.rtt.StaticFunType.<$Closure$175> make(
            /* base class */$Closure$175.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.ParameterizedType.make(x10.array.Array.$RTT, x10.rtt.ParameterizedType.make(x10.util.HashMap.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1)))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$175 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$175.class + " calling"); } 
                $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$175 $_obj = new $Closure$175((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
                
            }
            
            // constructor just for allocation
            public $Closure$175(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
            super($dummy);
            x10.util.WorkerLocalStorage.$Closure$175.$initParams(this, $Key, $Value);
            }
            // bridge for method abstract public ()=>U.operator()():U
            public x10.array.Array
              $apply$G(){return $apply();}
            
                private x10.rtt.Type $Key;
                private x10.rtt.Type $Value;
                // initializer of type parameters
                public static void $initParams(final $Closure$175 $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                $this.$Key = $Key;
                $this.$Value = $Value;
                }
                
                
                public x10.array.Array<x10.util.HashMap<$Key, $Value>>
                  $apply(
                  ){
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final int t63660 =
                      x10.lang.Runtime.MAX_THREADS;
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.util.HashMap<$Key, $Value> t63661 =
                      ((x10.util.HashMap<$Key, $Value>)
                        (null));
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
final x10.array.Array<x10.util.HashMap<$Key, $Value>> t63662 =
                      ((x10.array.Array)(new x10.array.Array<x10.util.HashMap<$Key, $Value>>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.util.HashMap.$RTT, $Key, $Value)).$init(((int)(t63660)),
                                                                                                                                                                                                   t63661, (x10.array.Array.__1x10$array$Array$$T) null)));
                    
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/WorkerLocalStorage.x10"
return t63662;
                }
                
                public $Closure$175(final x10.rtt.Type $Key,
                                    final x10.rtt.Type $Value) {x10.util.WorkerLocalStorage.$Closure$175.$initParams(this, $Key, $Value);
                                                                     {
                                                                        
                                                                    }}
                
            }
            
        
    }
    