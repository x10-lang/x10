package x10.util;

@x10.core.X10Generated public class Stack<$T> extends x10.util.ArrayList<$T> implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Stack.class);
    
    public static final x10.rtt.RuntimeType<Stack> $RTT = x10.rtt.NamedType.<Stack> make(
    "x10.util.Stack", /* base class */Stack.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.util.ArrayList.$RTT, x10.rtt.UnresolvedType.PARAM(0))}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Stack $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Stack.class + " calling"); } 
        x10.util.ArrayList.$_deserialize_body($_obj, $deserializer);
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Stack $_obj = new Stack((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        
    }
    
    // constructor just for allocation
    public Stack(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy, $T);
    x10.util.Stack.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final Stack $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
// creation method for java code (1-phase java constructor)
        public Stack(final x10.rtt.Type $T){this((java.lang.System[]) null, $T);
                                                $init();}
        
        // constructor for non-virtual call
        final public x10.util.Stack<$T> x10$util$Stack$$init$S() { {
                                                                          
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
super.$init();
                                                                          
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"

                                                                      }
                                                                      return this;
                                                                      }
        
        // constructor
        public x10.util.Stack<$T> $init(){return x10$util$Stack$$init$S();}
        
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
public boolean
                                                                                              push__0x10$util$Stack$$T$O(
                                                                                              final $T v){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final boolean t63148 =
              this.add__0x10$util$ArrayList$$T$O((($T)(v)));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return t63148;
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
public $T
                                                                                              pop$G(
                                                                                              ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final $T t63149 =
              (($T)(this.removeLast$G()));
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return t63149;
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
public x10.array.Array<$T>
                                                                                              pop(
                                                                                              final int k){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final int n =
              this.size$O();
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final boolean t63150 =
              ((n) < (((int)(k))));
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
if (t63150) {
                
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return null;
            }
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final int t63151 =
              ((n) - (((int)(k))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final int t63152 =
              ((n) - (((int)(1))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final x10.array.Array<$T> t63153 =
              ((x10.array.Array)(this.moveSectionToArray((int)(t63151),
                                                         (int)(t63152))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return t63153;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
public $T
                                                                                              peek$G(
                                                                                              ){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final $T t63154 =
              (($T)(this.getLast$G()));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return t63154;
        }
        
        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
public int
                                                                                              search__0x10$util$Stack$$T$O(
                                                                                              final $T v){
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final int i =
              this.lastIndexOf__0x10$util$ArrayList$$T$O((($T)(v)));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final boolean t63157 =
              ((i) >= (((int)(0))));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
if (t63157) {
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final int t63155 =
                  this.size$O();
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final int t63156 =
                  ((t63155) - (((int)(i))));
                
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return t63156;
            } else {
                
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return -1;
            }
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
final public x10.util.Stack<$T>
                                                                                              x10$util$Stack$$x10$util$Stack$this(
                                                                                              ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Stack.x10"
return x10.util.Stack.this;
        }
    
}
