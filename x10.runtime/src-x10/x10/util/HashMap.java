package x10.util;


@x10.core.X10Generated public class HashMap<$K, $V> extends x10.core.Ref implements x10.util.Map, x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, HashMap.class);
    
    public static final x10.rtt.RuntimeType<HashMap> $RTT = x10.rtt.NamedType.<HashMap> make(
    "x10.util.HashMap", /* base class */HashMap.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Map.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1)), x10.io.CustomSerialization.$RTT, x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $K;if (i ==1)return $V;return null;}
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new HashMap($K, $V, $$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($K);
    oos.writeObject($V);
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $K = (x10.rtt.Type) ois.readObject();
    $V = (x10.rtt.Type) ois.readObject();
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(HashMap $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + HashMap.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        x10.rtt.Type $K = ( x10.rtt.Type ) $deserializer.readRef();
        x10.rtt.Type $V = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$K = $K;
        $_obj.$V = $V;
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        HashMap $_obj = new HashMap((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
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
        $serializer.write($K);
        $serializer.write($V);
        
    }
    
    // constructor just for allocation
    public HashMap(final java.lang.System[] $dummy, final x10.rtt.Type $K, final x10.rtt.Type $V) { 
    super($dummy);
    x10.util.HashMap.$initParams(this, $K, $V);
    }
    // dispatcher for method abstract public x10.util.Map.get(k:K):x10.util.Box[V]
    public java.lang.Object get(final java.lang.Object a1, final x10.rtt.Type t1) {
    return get__0x10$util$HashMap$$K(($K)a1);
    }
    // dispatcher for method abstract public x10.util.Map.getOrElse(k:K,orelse:V):V
    public java.lang.Object getOrElse(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
    return getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(($K)a1, ($V)a2);
    }
    // dispatcher for method abstract public x10.util.Map.getOrThrow(k:K):V
    public java.lang.Object getOrThrow(final java.lang.Object a1, final x10.rtt.Type t1) {
    return getOrThrow__0x10$util$HashMap$$K$G(($K)a1);
    }
    // dispatcher for method abstract public x10.util.Map.put(k:K,v:V):x10.util.Box[V]
    public java.lang.Object put(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
    return put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(($K)a1, ($V)a2);
    }
    // dispatcher for method abstract public x10.util.Map.containsKey(k:K):x10.lang.Boolean
    public java.lang.Object containsKey(final java.lang.Object a1, final x10.rtt.Type t1) {
    return x10.core.Boolean.$box(containsKey__0x10$util$HashMap$$K$O(($K)a1));
    }
    // dispatcher for method abstract public x10.util.Map.remove(k:K):x10.util.Box[V]
    public java.lang.Object remove(final java.lang.Object a1, final x10.rtt.Type t1) {
    return remove__0x10$util$HashMap$$K(($K)a1);
    }
    
        private x10.rtt.Type $K;
        private x10.rtt.Type $V;
        // initializer of type parameters
        public static void $initParams(final HashMap $this, final x10.rtt.Type $K, final x10.rtt.Type $V) {
        $this.$K = $K;
        $this.$V = $V;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
@x10.core.X10Generated public static class HashEntry<$Key, $Value> extends x10.core.Ref implements x10.util.Map.Entry, x10.x10rt.X10JavaSerializable
                                                                                              {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, HashEntry.class);
            
            public static final x10.rtt.RuntimeType<HashEntry> $RTT = x10.rtt.NamedType.<HashEntry> make(
            "x10.util.HashMap.HashEntry", /* base class */HashEntry.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Map.Entry.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(HashEntry $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + HashEntry.class + " calling"); } 
                $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.key = $deserializer.readRef();
                $_obj.value = $deserializer.readRef();
                $_obj.removed = $deserializer.readBoolean();
                $_obj.hash = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                HashEntry $_obj = new HashEntry((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
                if (key instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.key);
                } else {
                $serializer.write(this.key);
                }
                if (value instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.value);
                } else {
                $serializer.write(this.value);
                }
                $serializer.write(this.removed);
                $serializer.write(this.hash);
                
            }
            
            // constructor just for allocation
            public HashEntry(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
            super($dummy);
            x10.util.HashMap.HashEntry.$initParams(this, $Key, $Value);
            }
            // dispatcher for method abstract public x10.util.Map.Entry.setValue(Val):void
            public java.lang.Object setValue(final java.lang.Object a1, final x10.rtt.Type t1) {
            setValue__0x10$util$HashMap$HashEntry$$Value(($Value)a1);return null;
            }
            
                private x10.rtt.Type $Key;
                private x10.rtt.Type $Value;
                // initializer of type parameters
                public static void $initParams(final HashEntry $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                $this.$Key = $Key;
                $this.$Value = $Value;
                }
                
                
                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public $Key
                                                                                                        getKey$G(
                                                                                                        ){
                    
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $Key t58803 =
                      (($Key)(key));
                    
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58803;
                }
                
                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public $Value
                                                                                                        getValue$G(
                                                                                                        ){
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $Value t58804 =
                      (($Value)(value));
                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58804;
                }
                
                
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public void
                                                                                                        setValue__0x10$util$HashMap$HashEntry$$Value(
                                                                                                        final $Value v){
                    
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.value = (($Value)(v));
                }
                
                
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public $Key key;
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public $Value value;
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean removed;
                
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int hash;
                
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
                public HashEntry(final x10.rtt.Type $Key,
                                 final x10.rtt.Type $Value,
                                 final $Key key,
                                 final $Value value,
                                 final int h, __0x10$util$HashMap$HashEntry$$Key__1x10$util$HashMap$HashEntry$$Value $dummy){this((java.lang.System[]) null, $Key, $Value);
                                                                                                                                 $init(key,value,h, (x10.util.HashMap.HashEntry.__0x10$util$HashMap$HashEntry$$Key__1x10$util$HashMap$HashEntry$$Value) null);}
                
                // constructor for non-virtual call
                final public x10.util.HashMap.HashEntry<$Key, $Value> x10$util$HashMap$HashEntry$$init$S(final $Key key,
                                                                                                         final $Value value,
                                                                                                         final int h, __0x10$util$HashMap$HashEntry$$Key__1x10$util$HashMap$HashEntry$$Value $dummy) { {
                                                                                                                                                                                                              
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                              
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                              
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.__fieldInitializers58491();
                                                                                                                                                                                                              
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.key = (($Key)(key));
                                                                                                                                                                                                              
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.value = (($Value)(value));
                                                                                                                                                                                                              
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.hash = h;
                                                                                                                                                                                                              
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.removed = false;
                                                                                                                                                                                                          }
                                                                                                                                                                                                          return this;
                                                                                                                                                                                                          }
                
                // constructor
                public x10.util.HashMap.HashEntry<$Key, $Value> $init(final $Key key,
                                                                      final $Value value,
                                                                      final int h, __0x10$util$HashMap$HashEntry$$Key__1x10$util$HashMap$HashEntry$$Value $dummy){return x10$util$HashMap$HashEntry$$init$S(key,value,h, $dummy);}
                
                
                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public x10.util.HashMap.HashEntry<$Key, $Value>
                                                                                                        x10$util$HashMap$HashEntry$$x10$util$HashMap$HashEntry$this(
                                                                                                        ){
                    
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return x10.util.HashMap.HashEntry.this;
                }
                
                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public void
                                                                                                        __fieldInitializers58491(
                                                                                                        ){
                    
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.removed = false;
                }
            // synthetic type for parameter mangling
            public abstract static class __0x10$util$HashMap$HashEntry$$Key__1x10$util$HashMap$HashEntry$$Value {}
            
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
/** The actual table, must be of size 2**n */
        public x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> table;
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
/** Number of non-null, non-removed entries in the table. */
        public int size;
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
/** Number of non-null entries in the table. */
        public int occupation;
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
/** table.length - 1 */
        public int mask;
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int modCount;
        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean shouldRehash;
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public static int MAX_PROBES = 3;
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public static int MIN_SIZE = 4;
        
        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
        public HashMap(final x10.rtt.Type $K,
                       final x10.rtt.Type $V){this((java.lang.System[]) null, $K, $V);
                                                  $init();}
        
        // constructor for non-virtual call
        final public x10.util.HashMap<$K, $V> x10$util$HashMap$$init$S() { {
                                                                                  
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                  
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                  
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.__fieldInitializers58493();
                                                                                  
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58805 =
                                                                                    x10.util.HashMap.MIN_SIZE;
                                                                                  
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.init((int)(t58805));
                                                                              }
                                                                              return this;
                                                                              }
        
        // constructor
        public x10.util.HashMap<$K, $V> $init(){return x10$util$HashMap$$init$S();}
        
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
        public HashMap(final x10.rtt.Type $K,
                       final x10.rtt.Type $V,
                       int sz){this((java.lang.System[]) null, $K, $V);
                                   $init(sz);}
        
        // constructor for non-virtual call
        final public x10.util.HashMap<$K, $V> x10$util$HashMap$$init$S(int sz) { {
                                                                                        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.__fieldInitializers58493();
                                                                                        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
int pow2 =
                                                                                          x10.util.HashMap.MIN_SIZE;
                                                                                        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
while (true) {
                                                                                            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58806 =
                                                                                              pow2;
                                                                                            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58807 =
                                                                                              sz;
                                                                                            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58810 =
                                                                                              ((t58806) < (((int)(t58807))));
                                                                                            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t58810)) {
                                                                                                
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
break;
                                                                                            }
                                                                                            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59019 =
                                                                                              pow2;
                                                                                            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59020 =
                                                                                              ((t59019) << (((int)(1))));
                                                                                            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
pow2 = t59020;
                                                                                        }
                                                                                        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58811 =
                                                                                          pow2;
                                                                                        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.init((int)(t58811));
                                                                                    }
                                                                                    return this;
                                                                                    }
        
        // constructor
        public x10.util.HashMap<$K, $V> $init(int sz){return x10$util$HashMap$$init$S(sz);}
        
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public void
                                                                                                init(
                                                                                                final int sz){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
assert ((int) ((sz) & (((int)((-(sz))))))) ==
            ((int) sz);
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
assert ((sz) >= (((int)(x10.util.HashMap.MIN_SIZE))));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58812 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.util.HashMap.HashEntry<$K, $V>>allocate(x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, $K, $V), ((int)(sz)), true)));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.table = ((x10.core.IndexedMemoryChunk)(t58812));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58813 =
              ((sz) - (((int)(1))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.mask = t58813;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.size = 0;
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.occupation = 0;
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = false;
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public void
                                                                                                clear(
                                                                                                ){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58783 =
              ((x10.util.HashMap)(this));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58814 =
              ((x10.util.HashMap<$K, $V>)x58783).
                modCount;
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58815 =
              ((t58814) + (((int)(1))));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58783.modCount = t58815;
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58816 =
              x10.util.HashMap.MIN_SIZE;
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.init((int)(t58816));
        }
        
        
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int
                                                                                                hash__0x10$util$HashMap$$K$O(
                                                                                                final $K k){
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58817 =
              this.hashInternal__0x10$util$HashMap$$K$O((($K)(k)));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58817;
        }
        
        
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public int
                                                                                                hashInternal__0x10$util$HashMap$$K$O(
                                                                                                final $K k){
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58818 =
              x10.rtt.Types.hashCode(((java.lang.Object)(k)));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58819 =
              ((t58818) * (((int)(17))));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58819;
        }
        
        
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.Box<$V>
                                                                                                $apply__0x10$util$HashMap$$K(
                                                                                                final $K k){
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.Box<$V> t58820 =
              ((x10.util.Box<$V>)
                this.get__0x10$util$HashMap$$K((($K)(k))));
            
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58820;
        }
        
        
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.Box<$V>
                                                                                                get__0x10$util$HashMap$$K(
                                                                                                final $K k){
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> e =
              ((x10.util.HashMap.HashEntry<$K, $V>)
                this.getEntry__0x10$util$HashMap$$K((($K)(k))));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58821 =
              ((e) == (null));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t58821)) {
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58821 = ((x10.util.HashMap.HashEntry<$K, $V>)e).
                                                                                                                 removed;
            }
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58822 =
              t58821;
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58822) {
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return null;
            }
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $V t58823 =
              (($V)(((x10.util.HashMap.HashEntry<$K, $V>)e).
                      value));
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.Box<$V> t58824 =
              ((x10.util.Box)(new x10.util.Box<$V>((java.lang.System[]) null, $V).$init(t58823, (x10.util.Box.__0x10$util$Box$$T) null)));
            
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58824;
        }
        
        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public $V
                                                                                                getOrElse__0x10$util$HashMap$$K__1x10$util$HashMap$$V$G(
                                                                                                final $K k,
                                                                                                final $V orelse){
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> e =
              ((x10.util.HashMap.HashEntry<$K, $V>)
                this.getEntry__0x10$util$HashMap$$K((($K)(k))));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58825 =
              ((e) == (null));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t58825)) {
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58825 = ((x10.util.HashMap.HashEntry<$K, $V>)e).
                                                                                                                 removed;
            }
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58826 =
              t58825;
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58826) {
                
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return orelse;
            }
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $V t58827 =
              (($V)(((x10.util.HashMap.HashEntry<$K, $V>)e).
                      value));
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58827;
        }
        
        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public $V
                                                                                                 getOrThrow__0x10$util$HashMap$$K$G(
                                                                                                 final $K k){
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> e =
              ((x10.util.HashMap.HashEntry<$K, $V>)
                this.getEntry__0x10$util$HashMap$$K((($K)(k))));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58828 =
              ((e) == (null));
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t58828)) {
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58828 = ((x10.util.HashMap.HashEntry<$K, $V>)e).
                                                                                                                  removed;
            }
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58830 =
              t58828;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58830) {
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.NoSuchElementException t58829 =
                  ((x10.util.NoSuchElementException)(new x10.util.NoSuchElementException(((java.lang.String)("Not found")))));
                
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58829;
            }
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $V t58831 =
              (($V)(((x10.util.HashMap.HashEntry<$K, $V>)e).
                      value));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58831;
        }
        
        
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.HashMap.HashEntry<$K, $V>
                                                                                                 getEntry__0x10$util$HashMap$$K(
                                                                                                 final $K k){
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58832 =
              size;
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58833 =
              ((int) t58832) ==
            ((int) 0);
            
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58833) {
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return null;
            }
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int h =
              this.hash__0x10$util$HashMap$$K$O((($K)(k)));
            
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
int i =
              h;
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
while (true) {
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58834 =
                  i;
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58835 =
                  mask;
                
//#line 123 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int j =
                  ((t58834) & (((int)(t58835))));
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58836 =
                  i;
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58837 =
                  ((t58836) + (((int)(1))));
                
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
i = t58837;
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58838 =
                  ((x10.core.IndexedMemoryChunk)(table));
                
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> e =
                  ((x10.util.HashMap.HashEntry[])t58838.value)[j];
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58843 =
                  ((e) == (null));
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58843) {
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58839 =
                      i;
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58840 =
                      ((t58839) - (((int)(h))));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58841 =
                      x10.util.HashMap.MAX_PROBES;
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58842 =
                      ((t58840) > (((int)(t58841))));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58842) {
                        
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = true;
                    }
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return null;
                }
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58861 =
                  ((e) != (null));
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58861) {
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58844 =
                      ((x10.util.HashMap.HashEntry<$K, $V>)e).
                        hash;
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58846 =
                      ((int) t58844) ==
                    ((int) h);
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58846) {
                        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $K t58845 =
                          (($K)(((x10.util.HashMap.HashEntry<$K, $V>)e).
                                  key));
                        
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58846 = ((java.lang.Object)(((java.lang.Object)(k)))).equals(t58845);
                    }
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58851 =
                      t58846;
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58851) {
                        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58847 =
                          i;
                        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58848 =
                          ((t58847) - (((int)(h))));
                        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58849 =
                          x10.util.HashMap.MAX_PROBES;
                        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58850 =
                          ((t58848) > (((int)(t58849))));
                        
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58850) {
                            
//#line 135 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = true;
                        }
                        
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return e;
                    }
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58852 =
                      i;
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58854 =
                      ((t58852) - (((int)(h))));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58853 =
                      ((x10.core.IndexedMemoryChunk)(table));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58855 =
                      ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>>)(t58853))).length);
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58860 =
                      ((t58854) > (((int)(t58855))));
                    
//#line 138 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58860) {
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58856 =
                          i;
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58857 =
                          ((t58856) - (((int)(h))));
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58858 =
                          x10.util.HashMap.MAX_PROBES;
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58859 =
                          ((t58857) > (((int)(t58858))));
                        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58859) {
                            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = true;
                        }
                        
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return null;
                    }
                }
            }
        }
        
        
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.Box<$V>
                                                                                                 put__0x10$util$HashMap$$K__1x10$util$HashMap$$V(
                                                                                                 final $K k,
                                                                                                 final $V v){
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.Box<$V> t58862 =
              ((x10.util.Box<$V>)
                this.putInternal__0x10$util$HashMap$$K__1x10$util$HashMap$$V((($K)(k)),
                                                                             (($V)(v))));
            
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58862;
        }
        
        
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public x10.util.Box<$V>
                                                                                                 putInternal__0x10$util$HashMap$$K__1x10$util$HashMap$$V(
                                                                                                 final $K k,
                                                                                                 final $V v){
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58864 =
              occupation;
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58863 =
              ((x10.core.IndexedMemoryChunk)(table));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58865 =
              ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>>)(t58863))).length);
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58871 =
              ((int) t58864) ==
            ((int) t58865);
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t58871)) {
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58870 =
                  shouldRehash;
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58870) {
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58868 =
                      occupation;
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58866 =
                      ((x10.core.IndexedMemoryChunk)(table));
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58867 =
                      ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>>)(t58866))).length);
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58869 =
                      ((t58867) / (((int)(2))));
                    
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58870 = ((t58868) >= (((int)(t58869))));
                }
                
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58871 = t58870;
            }
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58872 =
              t58871;
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58872) {
                
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.rehashInternal();
            }
            
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int h =
              this.hashInternal__0x10$util$HashMap$$K$O((($K)(k)));
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
int i =
              h;
            
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
while (true) {
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58873 =
                  i;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58874 =
                  mask;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int j =
                  ((t58873) & (((int)(t58874))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58875 =
                  i;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58876 =
                  ((t58875) + (((int)(1))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
i = t58876;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58877 =
                  ((x10.core.IndexedMemoryChunk)(table));
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> e =
                  ((x10.util.HashMap.HashEntry[])t58877.value)[j];
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58898 =
                  ((e) == (null));
                
//#line 160 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58898) {
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58878 =
                      i;
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58879 =
                      ((t58878) - (((int)(h))));
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58880 =
                      x10.util.HashMap.MAX_PROBES;
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58881 =
                      ((t58879) > (((int)(t58880))));
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58881) {
                        
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = true;
                    }
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58785 =
                      ((x10.util.HashMap)(this));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58882 =
                      ((x10.util.HashMap<$K, $V>)x58785).
                        modCount;
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58883 =
                      ((t58882) + (((int)(1))));
                    
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58785.modCount = t58883;
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58884 =
                      ((x10.core.IndexedMemoryChunk)(table));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> t58885 =
                      ((x10.util.HashMap.HashEntry)(new x10.util.HashMap.HashEntry<$K, $V>((java.lang.System[]) null, $K, $V).$init((($K)(k)),
                                                                                                                                    (($V)(v)),
                                                                                                                                    ((int)(h)), (x10.util.HashMap.HashEntry.__0x10$util$HashMap$HashEntry$$Key__1x10$util$HashMap$HashEntry$$Value) null)));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
((x10.util.HashMap.HashEntry[])t58884.value)[j] = t58885;
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58787 =
                      ((x10.util.HashMap)(this));
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58886 =
                      ((x10.util.HashMap<$K, $V>)x58787).
                        size;
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58887 =
                      ((t58886) + (((int)(1))));
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58787.size = t58887;
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58789 =
                      ((x10.util.HashMap)(this));
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58888 =
                      ((x10.util.HashMap<$K, $V>)x58789).
                        occupation;
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58889 =
                      ((t58888) + (((int)(1))));
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58789.occupation = t58889;
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return null;
                } else {
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58890 =
                      ((x10.util.HashMap.HashEntry<$K, $V>)e).
                        hash;
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58892 =
                      ((int) t58890) ==
                    ((int) h);
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58892) {
                        
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $K t58891 =
                          (($K)(((x10.util.HashMap.HashEntry<$K, $V>)e).
                                  key));
                        
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58892 = ((java.lang.Object)(((java.lang.Object)(k)))).equals(t58891);
                    }
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58897 =
                      t58892;
                    
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58897) {
                        
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $V old =
                          (($V)(((x10.util.HashMap.HashEntry<$K, $V>)e).
                                  value));
                        
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
e.value = (($V)(v));
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58895 =
                          ((x10.util.HashMap.HashEntry<$K, $V>)e).
                            removed;
                        
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58895) {
                            
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
e.removed = false;
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58791 =
                              ((x10.util.HashMap)(this));
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58893 =
                              ((x10.util.HashMap<$K, $V>)x58791).
                                size;
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58894 =
                              ((t58893) + (((int)(1))));
                            
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58791.size = t58894;
                            
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return null;
                        }
                        
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.Box<$V> t58896 =
                          ((x10.util.Box)(new x10.util.Box<$V>((java.lang.System[]) null, $V).$init((($V)(old)), (x10.util.Box.__0x10$util$Box$$T) null)));
                        
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58896;
                    }
                }
            }
        }
        
        
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public void
                                                                                                 rehash(
                                                                                                 ){
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.rehashInternal();
        }
        
        
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public void
                                                                                                 rehashInternal(
                                                                                                 ){
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58793 =
              ((x10.util.HashMap)(this));
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58899 =
              ((x10.util.HashMap<$K, $V>)x58793).
                modCount;
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58900 =
              ((t58899) + (((int)(1))));
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58793.modCount = t58900;
            
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t =
              ((x10.core.IndexedMemoryChunk)(table));
            
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int oldSize =
              size;
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58901 =
              ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>>)(t))).length);
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58902 =
              ((t58901) * (((int)(2))));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58903 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.util.HashMap.HashEntry<$K, $V>>allocate(x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, $K, $V), ((int)(t58902)), true)));
            
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.table = ((x10.core.IndexedMemoryChunk)(t58903));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t58904 =
              ((x10.core.IndexedMemoryChunk)(table));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58905 =
              ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>>)(t58904))).length);
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58906 =
              ((t58905) - (((int)(1))));
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.mask = t58906;
            
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.size = 0;
            
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.occupation = 0;
            
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = false;
            
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
int i59036 =
              0;
            {
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry[] t$value59063 =
                  ((x10.util.HashMap.HashEntry[])t.value);
                
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59037 =
                      i59036;
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59038 =
                      ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>>)(t))).length);
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t59039 =
                      ((t59037) < (((int)(t59038))));
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t59039)) {
                        
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
break;
                    }
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59021 =
                      i59036;
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> t59022 =
                      ((x10.util.HashMap.HashEntry<$K, $V>)t$value59063[t59021]);
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t59023 =
                      ((t59022) != (null));
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t59023) {
                        
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59024 =
                          i59036;
                        
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> t59025 =
                          ((x10.util.HashMap.HashEntry<$K, $V>)t$value59063[t59024]);
                        
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t59026 =
                          ((x10.util.HashMap.HashEntry<$K, $V>)t59025).
                            removed;
                        
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t59023 = !(t59026);
                    }
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t59027 =
                      t59023;
                    
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t59027) {
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59028 =
                          i59036;
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> t59029 =
                          ((x10.util.HashMap.HashEntry<$K, $V>)t$value59063[t59028]);
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $K t59030 =
                          (($K)(((x10.util.HashMap.HashEntry<$K, $V>)t59029).
                                  key));
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59031 =
                          i59036;
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> t59032 =
                          ((x10.util.HashMap.HashEntry<$K, $V>)t$value59063[t59031]);
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $V t59033 =
                          (($V)(((x10.util.HashMap.HashEntry<$K, $V>)t59032).
                                  value));
                        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.putInternal__0x10$util$HashMap$$K__1x10$util$HashMap$$V((($K)(t59030)),
                                                                                                                                                                            (($V)(t59033)));
                        
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = false;
                    }
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59034 =
                      i59036;
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59035 =
                      ((t59034) + (((int)(1))));
                    
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
i59036 = t59035;
                }
            }
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
assert ((int) size) ==
            ((int) oldSize);
            
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.size = oldSize;
        }
        
        
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                 containsKey__0x10$util$HashMap$$K$O(
                                                                                                 final $K k){
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> e =
              ((x10.util.HashMap.HashEntry<$K, $V>)
                this.getEntry__0x10$util$HashMap$$K((($K)(k))));
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58927 =
              ((e) != (null));
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58927) {
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58926 =
                  ((x10.util.HashMap.HashEntry<$K, $V>)e).
                    removed;
                
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58927 = !(t58926);
            }
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58928 =
              t58927;
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58928;
        }
        
        
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.Box<$V>
                                                                                                 remove__0x10$util$HashMap$$K(
                                                                                                 final $K k){
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58795 =
              ((x10.util.HashMap)(this));
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58929 =
              ((x10.util.HashMap<$K, $V>)x58795).
                modCount;
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58930 =
              ((t58929) + (((int)(1))));
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58795.modCount = t58930;
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$K, $V> e =
              ((x10.util.HashMap.HashEntry<$K, $V>)
                this.getEntry__0x10$util$HashMap$$K((($K)(k))));
            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t58932 =
              ((e) != (null));
            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58932) {
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58931 =
                  ((x10.util.HashMap.HashEntry<$K, $V>)e).
                    removed;
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t58932 = !(t58931);
            }
            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58937 =
              t58932;
            
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58937) {
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$K, $V> x58797 =
                  ((x10.util.HashMap)(this));
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58933 =
                  ((x10.util.HashMap<$K, $V>)x58797).
                    size;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58934 =
                  ((t58933) - (((int)(1))));
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58797.size = t58934;
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
e.removed = true;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $V t58935 =
                  (($V)(((x10.util.HashMap.HashEntry<$K, $V>)e).
                          value));
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.Box<$V> t58936 =
                  ((x10.util.Box)(new x10.util.Box<$V>((java.lang.System[]) null, $V).$init(t58935, (x10.util.Box.__0x10$util$Box$$T) null)));
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58936;
            }
            
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return null;
        }
        
        
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.Set<$K>
                                                                                                 keySet(
                                                                                                 ){
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.KeySet<$K, $V> t58938 =
              ((x10.util.HashMap.KeySet)(new x10.util.HashMap.KeySet<$K, $V>((java.lang.System[]) null, $K, $V).$init(((x10.util.HashMap<$K, $V>)(this)), (x10.util.HashMap.KeySet.__0$1x10$util$HashMap$KeySet$$Key$3x10$util$HashMap$KeySet$$Value$2) null)));
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58938;
        }
        
        
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.Set<x10.util.Map.Entry<$K, $V>>
                                                                                                 entries(
                                                                                                 ){
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.EntrySet<$K, $V> t58939 =
              ((x10.util.HashMap.EntrySet)(new x10.util.HashMap.EntrySet<$K, $V>((java.lang.System[]) null, $K, $V).$init(((x10.util.HashMap<$K, $V>)(this)), (x10.util.HashMap.EntrySet.__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2) null)));
            
//#line 221 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58939;
        }
        
        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.lang.Iterator<x10.util.HashMap.HashEntry<$K, $V>>
                                                                                                 entriesIterator(
                                                                                                 ){
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.EntriesIterator<$K, $V> iterator =
              ((x10.util.HashMap.EntriesIterator)(new x10.util.HashMap.EntriesIterator<$K, $V>((java.lang.System[]) null, $K, $V).$init(((x10.util.HashMap<$K, $V>)(this)), (x10.util.HashMap.EntriesIterator.__0$1x10$util$HashMap$EntriesIterator$$Key$3x10$util$HashMap$EntriesIterator$$Value$2) null)));
            
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
((x10.util.HashMap.EntriesIterator<$K, $V>)iterator).advance();
            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return iterator;
        }
        
        
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
@x10.core.X10Generated public static class EntriesIterator<$Key, $Value> extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, EntriesIterator.class);
            
            public static final x10.rtt.RuntimeType<EntriesIterator> $RTT = x10.rtt.NamedType.<EntriesIterator> make(
            "x10.util.HashMap.EntriesIterator", /* base class */EntriesIterator.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1))), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(EntriesIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + EntriesIterator.class + " calling"); } 
                $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.HashMap map = (x10.util.HashMap) $deserializer.readRef();
                $_obj.map = map;
                $_obj.i = $deserializer.readInt();
                $_obj.originalModCount = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                EntriesIterator $_obj = new EntriesIterator((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
                if (map instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.map);
                } else {
                $serializer.write(this.map);
                }
                $serializer.write(this.i);
                $serializer.write(this.originalModCount);
                
            }
            
            // constructor just for allocation
            public EntriesIterator(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
            super($dummy);
            x10.util.HashMap.EntriesIterator.$initParams(this, $Key, $Value);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.util.HashMap.HashEntry
              next$G(){return next();}
            
                private x10.rtt.Type $Key;
                private x10.rtt.Type $Value;
                // initializer of type parameters
                public static void $initParams(final EntriesIterator $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                $this.$Key = $Key;
                $this.$Value = $Value;
                }
                
                
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.HashMap<$Key, $Value> map;
                
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int i;
                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int originalModCount;
                
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
                public EntriesIterator(final x10.rtt.Type $Key,
                                       final x10.rtt.Type $Value,
                                       final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$EntriesIterator$$Key$3x10$util$HashMap$EntriesIterator$$Value$2 $dummy){this((java.lang.System[]) null, $Key, $Value);
                                                                                                                                                                                   $init(map, (x10.util.HashMap.EntriesIterator.__0$1x10$util$HashMap$EntriesIterator$$Key$3x10$util$HashMap$EntriesIterator$$Value$2) null);}
                
                // constructor for non-virtual call
                final public x10.util.HashMap.EntriesIterator<$Key, $Value> x10$util$HashMap$EntriesIterator$$init$S(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$EntriesIterator$$Key$3x10$util$HashMap$EntriesIterator$$Value$2 $dummy) { {
                                                                                                                                                                                                                                                                      
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                                                                                      
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                                                                                      
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.__fieldInitializers58492();
                                                                                                                                                                                                                                                                      
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.map = ((x10.util.HashMap)(map));
                                                                                                                                                                                                                                                                      
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.i = 0;
                                                                                                                                                                                                                                                                      
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58940 =
                                                                                                                                                                                                                                                                        ((x10.util.HashMap<$Key, $Value>)map).
                                                                                                                                                                                                                                                                          modCount;
                                                                                                                                                                                                                                                                      
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.originalModCount = t58940;
                                                                                                                                                                                                                                                                  }
                                                                                                                                                                                                                                                                  return this;
                                                                                                                                                                                                                                                                  }
                
                // constructor
                public x10.util.HashMap.EntriesIterator<$Key, $Value> $init(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$EntriesIterator$$Key$3x10$util$HashMap$EntriesIterator$$Value$2 $dummy){return x10$util$HashMap$EntriesIterator$$init$S(map, $dummy);}
                
                
                
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public void
                                                                                                         advance(
                                                                                                         ){
                    
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
while (true) {
                        
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58943 =
                          i;
                        
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58941 =
                          ((x10.util.HashMap)(map));
                        
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$Key, $Value>> t58942 =
                          ((x10.core.IndexedMemoryChunk)(((x10.util.HashMap<$Key, $Value>)t58941).
                                                           table));
                        
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58944 =
                          ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$Key, $Value>>)(t58942))).length);
                        
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58958 =
                          ((t58943) < (((int)(t58944))));
                        
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t58958)) {
                            
//#line 237 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
break;
                        }
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t59040 =
                          ((x10.util.HashMap)(map));
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$Key, $Value>> t59041 =
                          ((x10.core.IndexedMemoryChunk)(((x10.util.HashMap<$Key, $Value>)t59040).
                                                           table));
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59042 =
                          i;
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$Key, $Value> t59043 =
                          ((x10.util.HashMap.HashEntry[])t59041.value)[t59042];
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
boolean t59044 =
                          ((t59043) != (null));
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t59044) {
                            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t59045 =
                              ((x10.util.HashMap)(map));
                            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$Key, $Value>> t59046 =
                              ((x10.core.IndexedMemoryChunk)(((x10.util.HashMap<$Key, $Value>)t59045).
                                                               table));
                            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59047 =
                              i;
                            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$Key, $Value> t59048 =
                              ((x10.util.HashMap.HashEntry[])t59046.value)[t59047];
                            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t59049 =
                              ((x10.util.HashMap.HashEntry<$Key, $Value>)t59048).
                                removed;
                            
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
t59044 = !(t59049);
                        }
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t59050 =
                          t59044;
                        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t59050) {
                            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return;
                        }
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.EntriesIterator<$Key, $Value> x59051 =
                          ((x10.util.HashMap.EntriesIterator)(this));
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59052 =
                          ((x10.util.HashMap.EntriesIterator<$Key, $Value>)x59051).
                            i;
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59053 =
                          ((t59052) + (((int)(1))));
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x59051.i = t59053;
                    }
                }
                
                
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                         hasNext$O(
                                                                                                         ){
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58961 =
                      i;
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58959 =
                      ((x10.util.HashMap)(map));
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$Key, $Value>> t58960 =
                      ((x10.core.IndexedMemoryChunk)(((x10.util.HashMap<$Key, $Value>)t58959).
                                                       table));
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58962 =
                      ((((x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$Key, $Value>>)(t58960))).length);
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58963 =
                      ((t58961) < (((int)(t58962))));
                    
//#line 245 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58963) {
                        
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return true;
                    }
                    
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return false;
                }
                
                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.HashMap.HashEntry<$Key, $Value>
                                                                                                         next(
                                                                                                         ){
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58965 =
                      originalModCount;
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58964 =
                      ((x10.util.HashMap)(map));
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58966 =
                      ((x10.util.HashMap<$Key, $Value>)t58964).
                        modCount;
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58974 =
                      ((int) t58965) !=
                    ((int) t58966);
                    
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (t58974) {
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58967 =
                          ((x10.util.HashMap)(map));
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58968 =
                          ((x10.util.HashMap<$Key, $Value>)t58967).
                            modCount;
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58969 =
                          originalModCount;
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58970 =
                          ((t58968) - (((int)(t58969))));
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final java.lang.String t58971 =
                          (("Your code has a concurrency bug! You updated the hashmap ") + ((x10.core.Int.$box(t58970))));
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final java.lang.String t58972 =
                          ((t58971) + (" times since you created the iterator."));
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.Exception t58973 =
                          ((x10.lang.Exception)(new x10.lang.Exception(t58972)));
                        
//#line 253 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58973;
                    }
                    
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int j =
                      i;
                    
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.EntriesIterator<$Key, $Value> x58801 =
                      ((x10.util.HashMap.EntriesIterator)(this));
                    
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
;
                    
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58975 =
                      ((x10.util.HashMap.EntriesIterator<$Key, $Value>)x58801).
                        i;
                    
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58976 =
                      ((t58975) + (((int)(1))));
                    
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
x58801.i = t58976;
                    
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.advance();
                    
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58977 =
                      ((x10.util.HashMap)(map));
                    
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$Key, $Value>> t58978 =
                      ((x10.core.IndexedMemoryChunk)(((x10.util.HashMap<$Key, $Value>)t58977).
                                                       table));
                    
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$Key, $Value> t58979 =
                      ((x10.util.HashMap.HashEntry[])t58978.value)[j];
                    
//#line 258 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58979;
                }
                
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public x10.util.HashMap.EntriesIterator<$Key, $Value>
                                                                                                         x10$util$HashMap$EntriesIterator$$x10$util$HashMap$EntriesIterator$this(
                                                                                                         ){
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return x10.util.HashMap.EntriesIterator.this;
                }
                
                
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public void
                                                                                                         __fieldInitializers58492(
                                                                                                         ){
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.i = 0;
                    
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.originalModCount = 0;
                }
            // synthetic type for parameter mangling
            public abstract static class __0$1x10$util$HashMap$EntriesIterator$$Key$3x10$util$HashMap$EntriesIterator$$Value$2 {}
            
        }
        
        
        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int
                                                                                                 size$O(
                                                                                                 ){
            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58980 =
              size;
            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58980;
        }
        
        
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
@x10.core.X10Generated public static class KeySet<$Key, $Value> extends x10.util.AbstractCollection<$Key> implements x10.util.Set, x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, KeySet.class);
            
            public static final x10.rtt.RuntimeType<KeySet> $RTT = x10.rtt.NamedType.<KeySet> make(
            "x10.util.HashMap.KeySet", /* base class */KeySet.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Set.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.ParameterizedType.make(x10.util.AbstractCollection.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(KeySet $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + KeySet.class + " calling"); } 
                x10.util.AbstractCollection.$_deserialize_body($_obj, $deserializer);
                $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.HashMap map = (x10.util.HashMap) $deserializer.readRef();
                $_obj.map = map;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                KeySet $_obj = new KeySet((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
                if (map instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.map);
                } else {
                $serializer.write(this.map);
                }
                
            }
            
            // constructor just for allocation
            public KeySet(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
            super($dummy, $Key);
            x10.util.HashMap.KeySet.$initParams(this, $Key, $Value);
            }
            // dispatcher for method abstract public x10.util.Container.contains(T):x10.lang.Boolean
            public java.lang.Object contains(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box(contains__0x10$util$HashMap$KeySet$$Key$O(($Key)a1));
            }
            // dispatcher for method abstract public x10.util.Collection.add(T):x10.lang.Boolean
            public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box(add__0x10$util$HashMap$KeySet$$Key$O(($Key)a1));
            }
            // dispatcher for method abstract public x10.util.Collection.remove(T):x10.lang.Boolean
            public java.lang.Object remove(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box(remove__0x10$util$HashMap$KeySet$$Key$O(($Key)a1));
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
              contains__0x10$util$AbstractContainer$$T$O($Key a1){return contains__0x10$util$HashMap$KeySet$$Key$O(a1);}
            // bridge for method abstract public x10.util.AbstractCollection.add(T):x10.lang.Boolean
            public boolean
              add__0x10$util$AbstractCollection$$T$O($Key a1){return add__0x10$util$HashMap$KeySet$$Key$O(a1);}
            // bridge for method abstract public x10.util.AbstractCollection.remove(T):x10.lang.Boolean
            public boolean
              remove__0x10$util$AbstractCollection$$T$O($Key a1){return remove__0x10$util$HashMap$KeySet$$Key$O(a1);}
            
                private x10.rtt.Type $Key;
                private x10.rtt.Type $Value;
                // initializer of type parameters
                public static void $initParams(final KeySet $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                $this.$Key = $Key;
                $this.$Value = $Value;
                }
                
                
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.HashMap<$Key, $Value> map;
                
                
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
                public KeySet(final x10.rtt.Type $Key,
                              final x10.rtt.Type $Value,
                              final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$KeySet$$Key$3x10$util$HashMap$KeySet$$Value$2 $dummy){this((java.lang.System[]) null, $Key, $Value);
                                                                                                                                                        $init(map, (x10.util.HashMap.KeySet.__0$1x10$util$HashMap$KeySet$$Key$3x10$util$HashMap$KeySet$$Value$2) null);}
                
                // constructor for non-virtual call
                final public x10.util.HashMap.KeySet<$Key, $Value> x10$util$HashMap$KeySet$$init$S(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$KeySet$$Key$3x10$util$HashMap$KeySet$$Value$2 $dummy) { {
                                                                                                                                                                                                                                  
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
super.$init();
                                                                                                                                                                                                                                  
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                                                  
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.map = ((x10.util.HashMap)(map));
                                                                                                                                                                                                                              }
                                                                                                                                                                                                                              return this;
                                                                                                                                                                                                                              }
                
                // constructor
                public x10.util.HashMap.KeySet<$Key, $Value> $init(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$KeySet$$Key$3x10$util$HashMap$KeySet$$Value$2 $dummy){return x10$util$HashMap$KeySet$$init$S(map, $dummy);}
                
                
                
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.lang.Iterator<$Key>
                                                                                                         iterator(
                                                                                                         ){
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58981 =
                      ((x10.util.HashMap)(map));
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>> t58983 =
                      ((x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>>)
                        ((x10.util.HashMap<$Key, $Value>)t58981).entriesIterator());
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.fun.Fun_0_1<x10.util.HashMap.HashEntry<$Key, $Value>,$Key> t58984 =
                      ((x10.core.fun.Fun_0_1)(new x10.util.HashMap.KeySet.$Closure$171<$Key, $Value>($Key, $Value)));
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.MapIterator<x10.util.HashMap.HashEntry<$Key, $Value>, $Key> t58985 =
                      ((x10.util.MapIterator)(new x10.util.MapIterator<x10.util.HashMap.HashEntry<$Key, $Value>, $Key>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, $Key, $Value), $Key).$init(t58983,
                                                                                                                                                                                                                                             ((x10.core.fun.Fun_0_1)(t58984)), (x10.util.MapIterator.__0$1x10$util$MapIterator$$S$2__1$1x10$util$MapIterator$$S$3x10$util$MapIterator$$T$2) null)));
                    
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58985;
                }
                
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                         contains__0x10$util$HashMap$KeySet$$Key$O(
                                                                                                         final $Key k){
                    
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58986 =
                      ((x10.util.HashMap)(map));
                    
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t58987 =
                      ((x10.util.HashMap<$Key, $Value>)t58986).containsKey__0x10$util$HashMap$$K$O((($Key)(k)));
                    
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58987;
                }
                
                
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                         add__0x10$util$HashMap$KeySet$$Key$O(
                                                                                                         final $Key k){
                    
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.UnsupportedOperationException t58988 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                    
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58988;
                }
                
                
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                         remove__0x10$util$HashMap$KeySet$$Key$O(
                                                                                                         final $Key k){
                    
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.UnsupportedOperationException t58989 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                    
//#line 278 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58989;
                }
                
                
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.HashMap.KeySet<$Key, $Value>
                                                                                                         clone(
                                                                                                         ){
                    
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.UnsupportedOperationException t58990 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                    
//#line 279 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58990;
                }
                
                
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int
                                                                                                         size$O(
                                                                                                         ){
                    
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58991 =
                      ((x10.util.HashMap)(map));
                    
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t58992 =
                      ((x10.util.HashMap<$Key, $Value>)t58991).size$O();
                    
//#line 280 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58992;
                }
                
                
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public x10.util.HashMap.KeySet<$Key, $Value>
                                                                                                         x10$util$HashMap$KeySet$$x10$util$HashMap$KeySet$this(
                                                                                                         ){
                    
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return x10.util.HashMap.KeySet.this;
                }
                
                @x10.core.X10Generated public static class $Closure$171<$Key, $Value> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$171.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$171> $RTT = x10.rtt.StaticFunType.<$Closure$171> make(
                    /* base class */$Closure$171.class, 
                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1)), x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$171 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$171.class + " calling"); } 
                        $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                        $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$171 $_obj = new $Closure$171((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
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
                    public $Closure$171(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
                    super($dummy);
                    x10.util.HashMap.KeySet.$Closure$171.$initParams(this, $Key, $Value);
                    }
                    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
                    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
                    return $apply__0$1x10$util$HashMap$KeySet$$Closure$171$$Key$3x10$util$HashMap$KeySet$$Closure$171$$Value$2$G((x10.util.HashMap.HashEntry)a1);
                    }
                    
                        private x10.rtt.Type $Key;
                        private x10.rtt.Type $Value;
                        // initializer of type parameters
                        public static void $initParams(final $Closure$171 $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                        $this.$Key = $Key;
                        $this.$Value = $Value;
                        }
                        
                        
                        public $Key
                          $apply__0$1x10$util$HashMap$KeySet$$Closure$171$$Key$3x10$util$HashMap$KeySet$$Closure$171$$Value$2$G(
                          final x10.util.HashMap.HashEntry e){
                            
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $Key t58982 =
                              (($Key)(((x10.util.HashMap.HashEntry<$Key, $Value>)e).
                                        key));
                            
//#line 270 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58982;
                        }
                        
                        public $Closure$171(final x10.rtt.Type $Key,
                                            final x10.rtt.Type $Value) {x10.util.HashMap.KeySet.$Closure$171.$initParams(this, $Key, $Value);
                                                                             {
                                                                                
                                                                            }}
                        
                    }
                    
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$HashMap$KeySet$$Key$3x10$util$HashMap$KeySet$$Value$2 {}
                
            }
            
        
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
@x10.core.X10Generated public static class EntrySet<$Key, $Value> extends x10.util.AbstractCollection<x10.util.Map.Entry<$Key, $Value>> implements x10.util.Set, x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, EntrySet.class);
            
            public static final x10.rtt.RuntimeType<EntrySet> $RTT = x10.rtt.NamedType.<EntrySet> make(
            "x10.util.HashMap.EntrySet", /* base class */EntrySet.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.Set.$RTT, x10.rtt.ParameterizedType.make(x10.util.Map.Entry.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1))), x10.rtt.ParameterizedType.make(x10.util.AbstractCollection.$RTT, x10.rtt.ParameterizedType.make(x10.util.Map.Entry.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1)))}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(EntrySet $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + EntrySet.class + " calling"); } 
                x10.util.AbstractCollection.$_deserialize_body($_obj, $deserializer);
                $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                x10.util.HashMap map = (x10.util.HashMap) $deserializer.readRef();
                $_obj.map = map;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                EntrySet $_obj = new EntrySet((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                super.$_serialize($serializer);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
                if (map instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.map);
                } else {
                $serializer.write(this.map);
                }
                
            }
            
            // constructor just for allocation
            public EntrySet(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
            super($dummy, x10.rtt.ParameterizedType.make(x10.util.Map.Entry.$RTT, $Key, $Value));
            x10.util.HashMap.EntrySet.$initParams(this, $Key, $Value);
            }
            // dispatcher for method abstract public x10.util.Container.contains(T):x10.lang.Boolean
            public java.lang.Object contains(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box(contains__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O((x10.util.Map.Entry)a1));
            }
            // dispatcher for method abstract public x10.util.Collection.add(T):x10.lang.Boolean
            public java.lang.Object add(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box(add__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O((x10.util.Map.Entry)a1));
            }
            // dispatcher for method abstract public x10.util.Collection.remove(T):x10.lang.Boolean
            public java.lang.Object remove(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Boolean.$box(remove__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O((x10.util.Map.Entry)a1));
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
              contains__0x10$util$AbstractContainer$$T$O(x10.util.Map.Entry a1){return contains__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O(a1);}
            // bridge for method abstract public x10.util.AbstractCollection.add(T):x10.lang.Boolean
            public boolean
              add__0x10$util$AbstractCollection$$T$O(x10.util.Map.Entry a1){return add__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O(a1);}
            // bridge for method abstract public x10.util.AbstractCollection.remove(T):x10.lang.Boolean
            public boolean
              remove__0x10$util$AbstractCollection$$T$O(x10.util.Map.Entry a1){return remove__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O(a1);}
            
                private x10.rtt.Type $Key;
                private x10.rtt.Type $Value;
                // initializer of type parameters
                public static void $initParams(final EntrySet $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                $this.$Key = $Key;
                $this.$Value = $Value;
                }
                
                
//#line 286 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.HashMap<$Key, $Value> map;
                
                
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
                public EntrySet(final x10.rtt.Type $Key,
                                final x10.rtt.Type $Value,
                                final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2 $dummy){this((java.lang.System[]) null, $Key, $Value);
                                                                                                                                                              $init(map, (x10.util.HashMap.EntrySet.__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2) null);}
                
                // constructor for non-virtual call
                final public x10.util.HashMap.EntrySet<$Key, $Value> x10$util$HashMap$EntrySet$$init$S(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2 $dummy) { {
                                                                                                                                                                                                                                          
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
super.$init();
                                                                                                                                                                                                                                          
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                                                          
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.map = ((x10.util.HashMap)(map));
                                                                                                                                                                                                                                      }
                                                                                                                                                                                                                                      return this;
                                                                                                                                                                                                                                      }
                
                // constructor
                public x10.util.HashMap.EntrySet<$Key, $Value> $init(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2 $dummy){return x10$util$HashMap$EntrySet$$init$S(map, $dummy);}
                
                
                
//#line 290 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.lang.Iterator<x10.util.Map.Entry<$Key, $Value>>
                                                                                                         iterator(
                                                                                                         ){
                    
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t58993 =
                      ((x10.util.HashMap)(map));
                    
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>> t58994 =
                      ((x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>>)
                        ((x10.util.HashMap<$Key, $Value>)t58993).entriesIterator());
                    
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.fun.Fun_0_1<x10.util.HashMap.HashEntry<$Key, $Value>,x10.util.Map.Entry<$Key, $Value>> t58995 =
                      ((x10.core.fun.Fun_0_1)(new x10.util.HashMap.EntrySet.$Closure$172<$Key, $Value>($Key, $Value)));
                    
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.MapIterator<x10.util.HashMap.HashEntry<$Key, $Value>, x10.util.Map.Entry<$Key, $Value>> t58996 =
                      ((x10.util.MapIterator)(new x10.util.MapIterator<x10.util.HashMap.HashEntry<$Key, $Value>, x10.util.Map.Entry<$Key, $Value>>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, $Key, $Value), x10.rtt.ParameterizedType.make(x10.util.Map.Entry.$RTT, $Key, $Value)).$init(t58994,
                                                                                                                                                                                                                                                                                                                                          ((x10.core.fun.Fun_0_1)(t58995)), (x10.util.MapIterator.__0$1x10$util$MapIterator$$S$2__1$1x10$util$MapIterator$$S$3x10$util$MapIterator$$T$2) null)));
                    
//#line 291 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t58996;
                }
                
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                         contains__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O(
                                                                                                         final x10.util.Map.Entry k){
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.UnsupportedOperationException t58997 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58997;
                }
                
                
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                         add__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O(
                                                                                                         final x10.util.Map.Entry k){
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.UnsupportedOperationException t58998 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58998;
                }
                
                
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public boolean
                                                                                                         remove__0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2$O(
                                                                                                         final x10.util.Map.Entry k){
                    
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.UnsupportedOperationException t58999 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                    
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t58999;
                }
                
                
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.util.HashMap.EntrySet<$Key, $Value>
                                                                                                         clone(
                                                                                                         ){
                    
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.UnsupportedOperationException t59000 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException()));
                    
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
throw t59000;
                }
                
                
//#line 299 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public int
                                                                                                         size$O(
                                                                                                         ){
                    
//#line 299 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap<$Key, $Value> t59001 =
                      ((x10.util.HashMap)(map));
                    
//#line 299 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int t59002 =
                      ((x10.util.HashMap<$Key, $Value>)t59001).size$O();
                    
//#line 299 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t59002;
                }
                
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public x10.util.HashMap.EntrySet<$Key, $Value>
                                                                                                         x10$util$HashMap$EntrySet$$x10$util$HashMap$EntrySet$this(
                                                                                                         ){
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return x10.util.HashMap.EntrySet.this;
                }
                
                @x10.core.X10Generated public static class $Closure$172<$Key, $Value> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$172.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$172> $RTT = x10.rtt.StaticFunType.<$Closure$172> make(
                    /* base class */$Closure$172.class, 
                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1)), x10.rtt.ParameterizedType.make(x10.util.Map.Entry.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1))), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$172 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$172.class + " calling"); } 
                        $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                        $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$172 $_obj = new $Closure$172((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
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
                    public $Closure$172(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
                    super($dummy);
                    x10.util.HashMap.EntrySet.$Closure$172.$initParams(this, $Key, $Value);
                    }
                    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
                    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
                    return $apply__0$1x10$util$HashMap$EntrySet$$Closure$172$$Key$3x10$util$HashMap$EntrySet$$Closure$172$$Value$2((x10.util.HashMap.HashEntry)a1);
                    }
                    
                        private x10.rtt.Type $Key;
                        private x10.rtt.Type $Value;
                        // initializer of type parameters
                        public static void $initParams(final $Closure$172 $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                        $this.$Key = $Key;
                        $this.$Value = $Value;
                        }
                        
                        
                        public x10.util.Map.Entry<$Key, $Value>
                          $apply__0$1x10$util$HashMap$EntrySet$$Closure$172$$Key$3x10$util$HashMap$EntrySet$$Closure$172$$Value$2(
                          final x10.util.HashMap.HashEntry e){
                            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return e;
                        }
                        
                        public $Closure$172(final x10.rtt.Type $Key,
                                            final x10.rtt.Type $Value) {x10.util.HashMap.EntrySet.$Closure$172.$initParams(this, $Key, $Value);
                                                                             {
                                                                                
                                                                            }}
                        
                    }
                    
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$HashMap$EntrySet$$Key$3x10$util$HashMap$EntrySet$$Value$2 {}
                
            }
            
        
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
@x10.core.X10Generated public static class State<$Key, $Value> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
                                                                                               {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, State.class);
            
            public static final x10.rtt.RuntimeType<State> $RTT = x10.rtt.NamedType.<State> make(
            "x10.util.HashMap.State", /* base class */State.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(State $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + State.class + " calling"); } 
                $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array content = (x10.array.Array) $deserializer.readRef();
                $_obj.content = content;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                State $_obj = new State((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
                if (content instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.content);
                } else {
                $serializer.write(this.content);
                }
                
            }
            
            // constructor just for allocation
            public State(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
            super($dummy);
            x10.util.HashMap.State.$initParams(this, $Key, $Value);
            }
            
                private x10.rtt.Type $Key;
                private x10.rtt.Type $Value;
                // initializer of type parameters
                public static void $initParams(final State $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                $this.$Key = $Key;
                $this.$Value = $Value;
                }
                
                
//#line 304 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.array.Array<x10.util.Pair<$Key, $Value>> content;
                
                
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
                public State(final x10.rtt.Type $Key,
                             final x10.rtt.Type $Value,
                             final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$State$$Key$3x10$util$HashMap$State$$Value$2 $dummy){this((java.lang.System[]) null, $Key, $Value);
                                                                                                                                                     $init(map, (x10.util.HashMap.State.__0$1x10$util$HashMap$State$$Key$3x10$util$HashMap$State$$Value$2) null);}
                
                // constructor for non-virtual call
                final public x10.util.HashMap.State<$Key, $Value> x10$util$HashMap$State$$init$S(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$State$$Key$3x10$util$HashMap$State$$Value$2 $dummy) { {
                                                                                                                                                                                                                              
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                                              
//#line 306 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"

                                                                                                                                                                                                                              
//#line 307 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final int size =
                                                                                                                                                                                                                                ((x10.util.HashMap<$Key, $Value>)map).size$O();
                                                                                                                                                                                                                              
//#line 308 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>> it =
                                                                                                                                                                                                                                ((x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>>)
                                                                                                                                                                                                                                  ((x10.util.HashMap<$Key, $Value>)map).entriesIterator());
                                                                                                                                                                                                                              
//#line 310 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.util.Pair<$Key, $Value>> t59006 =
                                                                                                                                                                                                                                ((x10.core.fun.Fun_0_1)(new x10.util.HashMap.State.$Closure$173<$Key, $Value>($Key, $Value, it, (x10.util.HashMap.State.$Closure$173.__0$1x10$util$HashMap$HashEntry$1x10$util$HashMap$State$$Closure$173$$Key$3x10$util$HashMap$State$$Closure$173$$Value$2$2) null)));
                                                                                                                                                                                                                              
//#line 309 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.array.Array<x10.util.Pair<$Key, $Value>> t59007 =
                                                                                                                                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.util.Pair<$Key, $Value>>((java.lang.System[]) null, x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, $Key, $Value)).$init(((int)(size)),
                                                                                                                                                                                                                                                                                                                                                                                                       ((x10.core.fun.Fun_0_1)(t59006)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                                                                                                                              
//#line 309 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.content = ((x10.array.Array)(t59007));
                                                                                                                                                                                                                          }
                                                                                                                                                                                                                          return this;
                                                                                                                                                                                                                          }
                
                // constructor
                public x10.util.HashMap.State<$Key, $Value> $init(final x10.util.HashMap<$Key, $Value> map, __0$1x10$util$HashMap$State$$Key$3x10$util$HashMap$State$$Value$2 $dummy){return x10$util$HashMap$State$$init$S(map, $dummy);}
                
                
                
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public x10.util.HashMap.State<$Key, $Value>
                                                                                                         x10$util$HashMap$State$$x10$util$HashMap$State$this(
                                                                                                         ){
                    
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return x10.util.HashMap.State.this;
                }
                
                @x10.core.X10Generated public static class $Closure$173<$Key, $Value> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$173.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$173> $RTT = x10.rtt.StaticFunType.<$Closure$173> make(
                    /* base class */$Closure$173.class, 
                    /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
                    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.ParameterizedType.make(x10.util.Pair.$RTT, x10.rtt.UnresolvedType.PARAM(0), x10.rtt.UnresolvedType.PARAM(1))), x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $Key;if (i ==1)return $Value;return null;}
                    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$173 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$173.class + " calling"); } 
                        $_obj.$Key = ( x10.rtt.Type ) $deserializer.readRef();
                        $_obj.$Value = ( x10.rtt.Type ) $deserializer.readRef();
                        x10.lang.Iterator it = (x10.lang.Iterator) $deserializer.readRef();
                        $_obj.it = it;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$173 $_obj = new $Closure$173((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Key);
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$Value);
                        if (it instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.it);
                        } else {
                        $serializer.write(this.it);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$173(final java.lang.System[] $dummy, final x10.rtt.Type $Key, final x10.rtt.Type $Value) { 
                    super($dummy);
                    x10.util.HashMap.State.$Closure$173.$initParams(this, $Key, $Value);
                    }
                    // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
                    public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
                    return $apply(x10.core.Int.$unbox(a1));
                    }
                    
                        private x10.rtt.Type $Key;
                        private x10.rtt.Type $Value;
                        // initializer of type parameters
                        public static void $initParams(final $Closure$173 $this, final x10.rtt.Type $Key, final x10.rtt.Type $Value) {
                        $this.$Key = $Key;
                        $this.$Value = $Value;
                        }
                        
                        
                        public x10.util.Pair<$Key, $Value>
                          $apply(
                          final int p){
                            
//#line 311 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.HashEntry<$Key, $Value> entry =
                              ((x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>>)this.
                                                                                              it).next$G();
                            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $Key t59003 =
                              (($Key)(((x10.util.HashMap.HashEntry<$Key, $Value>)entry).getKey$G()));
                            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $Value t59004 =
                              (($Value)(((x10.util.HashMap.HashEntry<$Key, $Value>)entry).getValue$G()));
                            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.Pair<$Key, $Value> t59005 =
                              new x10.util.Pair<$Key, $Value>((java.lang.System[]) null, $Key, $Value).$init((($Key)(t59003)),
                                                                                                             t59004, (x10.util.Pair.__0x10$util$Pair$$T__1x10$util$Pair$$U) null);
                            
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t59005;
                        }
                        
                        public x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>> it;
                        
                        public $Closure$173(final x10.rtt.Type $Key,
                                            final x10.rtt.Type $Value,
                                            final x10.lang.Iterator<x10.util.HashMap.HashEntry<$Key, $Value>> it, __0$1x10$util$HashMap$HashEntry$1x10$util$HashMap$State$$Closure$173$$Key$3x10$util$HashMap$State$$Closure$173$$Value$2$2 $dummy) {x10.util.HashMap.State.$Closure$173.$initParams(this, $Key, $Value);
                                                                                                                                                                                                                                                          {
                                                                                                                                                                                                                                                             this.it = ((x10.lang.Iterator)(it));
                                                                                                                                                                                                                                                         }}
                        // synthetic type for parameter mangling
                        public abstract static class __0$1x10$util$HashMap$HashEntry$1x10$util$HashMap$State$$Closure$173$$Key$3x10$util$HashMap$State$$Closure$173$$Value$2$2 {}
                        
                    }
                    
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$util$HashMap$State$$Key$3x10$util$HashMap$State$$Value$2 {}
                
            }
            
        
        
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
// creation method for java code (1-phase java constructor)
        public HashMap(final x10.rtt.Type $K,
                       final x10.rtt.Type $V,
                       final x10.io.SerialData x){this((java.lang.System[]) null, $K, $V);
                                                      $init(x);}
        
        // constructor for non-virtual call
        final public x10.util.HashMap<$K, $V> x10$util$HashMap$$init$S(final x10.io.SerialData x) {x10$util$HashMap$init_for_reflection(x);
                                                                                                       
                                                                                                       return this;
                                                                                                       }
        public void x10$util$HashMap$init_for_reflection(x10.io.SerialData x) {
             {
                
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.$init();
                
//#line 323 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final java.lang.Object t59008 =
                  ((java.lang.Object)(x.
                                        data));
                
//#line 323 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.State<$K, $V> state =
                  ((x10.util.HashMap.State)(x10.rtt.Types.<x10.util.HashMap.State<$K, $V>> cast(t59008,x10.rtt.ParameterizedType.make(x10.util.HashMap.State.$RTT, $K, $V))));
                
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.array.Array<x10.util.Pair<$K, $V>> t59059 =
                  ((x10.array.Array)(((x10.util.HashMap.State<$K, $V>)state).
                                       content));
                
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.array.Region t59060 =
                  ((x10.array.Region)(((x10.array.Array<x10.util.Pair<$K, $V>>)t59059).
                                        region));
                
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.lang.Iterator<x10.array.Point> p59061 =
                  t59060.iterator();
                
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                    
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final boolean t59062 =
                      ((x10.lang.Iterator<x10.array.Point>)p59061).hasNext$O();
                    
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
if (!(t59062)) {
                        
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
break;
                    }
                    
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.array.Point p59054 =
                      ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p59061).next$G()));
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.array.Array<x10.util.Pair<$K, $V>> t59055 =
                      ((x10.array.Array)(((x10.util.HashMap.State<$K, $V>)state).
                                           content));
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.Pair<$K, $V> pair59056 =
                      ((x10.array.Array<x10.util.Pair<$K, $V>>)t59055).$apply$G(((x10.array.Point)(p59054)));
                    
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $K t59057 =
                      (($K)(((x10.util.Pair<$K, $V>)pair59056).
                              first));
                    
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final $V t59058 =
                      (($V)(((x10.util.Pair<$K, $V>)pair59056).
                              second));
                    
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.putInternal__0x10$util$HashMap$$K__1x10$util$HashMap$$V((($K)(t59057)),
                                                                                                                                                                        (($V)(t59058)));
                }
            }}
            
        // constructor
        public x10.util.HashMap<$K, $V> $init(final x10.io.SerialData x){return x10$util$HashMap$$init$S(x);}
        
        
        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
public x10.io.SerialData
                                                                                                 serialize(
                                                                                                 ){
            
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.util.HashMap.State<$K, $V> t59016 =
              ((x10.util.HashMap.State)(new x10.util.HashMap.State<$K, $V>((java.lang.System[]) null, $K, $V).$init(((x10.util.HashMap<$K, $V>)(this)), (x10.util.HashMap.State.__0$1x10$util$HashMap$State$$Key$3x10$util$HashMap$State$$Value$2) null)));
            
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.io.SerialData t59017 =
              ((x10.io.SerialData)(new x10.io.SerialData((java.lang.System[]) null).$init(((java.lang.Object)(t59016)),
                                                                                          ((x10.io.SerialData)(null)))));
            
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return t59017;
        }
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public x10.util.HashMap<$K, $V>
                                                                                                x10$util$HashMap$$x10$util$HashMap$this(
                                                                                                ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
return x10.util.HashMap.this;
        }
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final public void
                                                                                                __fieldInitializers58493(
                                                                                                ){
            
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
final x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>> t59018 =
              (x10.core.IndexedMemoryChunk<x10.util.HashMap.HashEntry<$K, $V>>) x10.rtt.Types.zeroValue(x10.rtt.ParameterizedType.make(x10.core.IndexedMemoryChunk.$RTT, x10.rtt.ParameterizedType.make(x10.util.HashMap.HashEntry.$RTT, $K, $V)));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.table = t59018;
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.size = 0;
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.occupation = 0;
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.mask = 0;
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.modCount = 0;
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashMap.x10"
this.shouldRehash = false;
        }
        
        public static int
          getInitialized$MAX_PROBES(
          ){
            return x10.util.HashMap.MAX_PROBES;
        }
        
        public static int
          getInitialized$MIN_SIZE(
          ){
            return x10.util.HashMap.MIN_SIZE;
        }
        
        }
        