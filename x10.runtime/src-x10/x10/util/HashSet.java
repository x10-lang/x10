package x10.util;


@x10.core.X10Generated public class HashSet<$T> extends x10.util.MapSet<$T> implements x10.io.CustomSerialization
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, HashSet.class);
    
    public static final x10.rtt.RuntimeType<HashSet> $RTT = x10.rtt.NamedType.<HashSet> make(
    "x10.util.HashSet", /* base class */HashSet.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.io.CustomSerialization.$RTT, x10.rtt.ParameterizedType.make(x10.util.MapSet.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    // custom serializer
    private transient x10.io.SerialData $$serialdata;
    private Object writeReplace() { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " calling"); } $$serialdata = serialize(); if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: serialize() of " + this + " returned " + $$serialdata); } return this; }
    private Object readResolve() { return new HashSet($T, $$serialdata); }
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
    oos.writeObject($T);
    oos.writeObject($$serialdata); }
    private void readObject(java.io.ObjectInputStream ois) throws java.io.IOException, java.lang.ClassNotFoundException {
    $T = (x10.rtt.Type) ois.readObject();
    $$serialdata = (x10.io.SerialData) ois.readObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(HashSet $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("X10JavaSerializable: $_deserialize_body() of " + HashSet.class + " calling"); } 
        x10.io.SerialData $$serialdata = (x10.io.SerialData) $deserializer.readRef();
        x10.rtt.Type $T = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.$T = $T;
        $_obj.$init($$serialdata);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        HashSet $_obj = new HashSet((java.lang.System[]) null, (x10.rtt.Type) null);
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
    public HashSet(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy, $T);
    x10.util.HashSet.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final HashSet $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
// creation method for java code (1-phase java constructor)
        public HashSet(final x10.rtt.Type $T){this((java.lang.System[]) null, $T);
                                                  $init();}
        
        // constructor for non-virtual call
        final public x10.util.HashSet<$T> x10$util$HashSet$$init$S() { {
                                                                              
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.util.HashMap<$T, x10.core.Boolean> t59072 =
                                                                                ((x10.util.HashMap)(new x10.util.HashMap<$T, x10.core.Boolean>((java.lang.System[]) null, $T, x10.rtt.Types.BOOLEAN).$init()));
                                                                              
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
super.$init(((x10.util.Map<$T, x10.core.Boolean>)(t59072)), (x10.util.MapSet.__0$1x10$util$MapSet$$T$3x10$lang$Boolean$2) null);
                                                                              
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"

                                                                          }
                                                                          return this;
                                                                          }
        
        // constructor
        public x10.util.HashSet<$T> $init(){return x10$util$HashSet$$init$S();}
        
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
// creation method for java code (1-phase java constructor)
        public HashSet(final x10.rtt.Type $T,
                       final int sz){this((java.lang.System[]) null, $T);
                                         $init(sz);}
        
        // constructor for non-virtual call
        final public x10.util.HashSet<$T> x10$util$HashSet$$init$S(final int sz) { {
                                                                                          
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.util.HashMap<$T, x10.core.Boolean> t59073 =
                                                                                            ((x10.util.HashMap)(new x10.util.HashMap<$T, x10.core.Boolean>((java.lang.System[]) null, $T, x10.rtt.Types.BOOLEAN).$init(((int)(sz)))));
                                                                                          
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
super.$init(((x10.util.Map<$T, x10.core.Boolean>)(t59073)), (x10.util.MapSet.__0$1x10$util$MapSet$$T$3x10$lang$Boolean$2) null);
                                                                                          
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"

                                                                                      }
                                                                                      return this;
                                                                                      }
        
        // constructor
        public x10.util.HashSet<$T> $init(final int sz){return x10$util$HashSet$$init$S(sz);}
        
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
public x10.io.SerialData
                                                                                                serialize(
                                                                                                ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.util.Map<$T, x10.core.Boolean> t59066 =
              ((x10.util.Map)(map));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.util.HashMap<$T, x10.core.Boolean> t59067 =
              ((x10.util.HashMap)(x10.rtt.Types.<x10.util.HashMap<$T, x10.core.Boolean>> cast(t59066,x10.rtt.ParameterizedType.make(x10.util.HashMap.$RTT, $T, x10.rtt.Types.BOOLEAN))));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.io.SerialData t59068 =
              ((x10.util.HashMap<$T, x10.core.Boolean>)t59067).serialize();
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
return t59068;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
// creation method for java code (1-phase java constructor)
        public HashSet(final x10.rtt.Type $T,
                       final x10.io.SerialData a){this((java.lang.System[]) null, $T);
                                                      $init(a);}
        
        // constructor for non-virtual call
        final public x10.util.HashSet<$T> x10$util$HashSet$$init$S(final x10.io.SerialData a) {x10$util$HashSet$init_for_reflection(a);
                                                                                                   
                                                                                                   return this;
                                                                                                   }
        public void x10$util$HashSet$init_for_reflection(x10.io.SerialData a) {
             {
                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.util.HashMap<$T, x10.core.Boolean> t59074 =
                  ((x10.util.HashMap)(new x10.util.HashMap<$T, x10.core.Boolean>((java.lang.System[]) null, $T, x10.rtt.Types.BOOLEAN).$init(((x10.io.SerialData)(a)))));
                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
super.$init(((x10.util.Map<$T, x10.core.Boolean>)(t59074)), (x10.util.MapSet.__0$1x10$util$MapSet$$T$3x10$lang$Boolean$2) null);
                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"

            }}
            
        // constructor
        public x10.util.HashSet<$T> $init(final x10.io.SerialData a){return x10$util$HashSet$$init$S(a);}
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
public x10.util.HashSet<$T>
                                                                                                clone(
                                                                                                ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.io.SerialData t59070 =
              this.serialize();
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final x10.util.HashSet<$T> t59071 =
              ((x10.util.HashSet)(new x10.util.HashSet<$T>((java.lang.System[]) null, $T).$init(t59070)));
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
return t59071;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
final public x10.util.HashSet<$T>
                                                                                                x10$util$HashSet$$x10$util$HashSet$this(
                                                                                                ){
            
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/HashSet.x10"
return x10.util.HashSet.this;
        }
    
}
