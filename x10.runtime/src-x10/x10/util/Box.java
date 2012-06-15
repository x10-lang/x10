package x10.util;

@x10.core.X10Generated final public class Box<$T> extends x10.core.Ref implements x10.core.fun.Fun_0_0, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Box.class);
    
    public static final x10.rtt.RuntimeType<Box> $RTT = x10.rtt.NamedType.<Box> make(
    "x10.util.Box", /* base class */Box.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_0.$RTT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Box $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Box.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.value = $deserializer.readRef();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Box $_obj = new Box((java.lang.System[]) null, (x10.rtt.Type) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
        if (value instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.value);
        } else {
        $serializer.write(this.value);
        }
        
    }
    
    // constructor just for allocation
    public Box(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.util.Box.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final Box $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 15 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
public $T value;
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
// creation method for java code (1-phase java constructor)
        public Box(final x10.rtt.Type $T,
                   final $T x, __0x10$util$Box$$T $dummy){this((java.lang.System[]) null, $T);
                                                              $init(x, (x10.util.Box.__0x10$util$Box$$T) null);}
        
        // constructor for non-virtual call
        final public x10.util.Box<$T> x10$util$Box$$init$S(final $T x, __0x10$util$Box$$T $dummy) { {
                                                                                                           
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"

                                                                                                           
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"

                                                                                                           
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
this.value = (($T)(x));
                                                                                                       }
                                                                                                       return this;
                                                                                                       }
        
        // constructor
        public x10.util.Box<$T> $init(final $T x, __0x10$util$Box$$T $dummy){return x10$util$Box$$init$S(x, $dummy);}
        
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
public $T
                                                                                            $apply$G(
                                                                                            ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final $T t57764 =
              (($T)(value));
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return t57764;
        }
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
public int
                                                                                            hashCode(
                                                                                            ){
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final $T t57765 =
              (($T)(value));
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final int t57766 =
              x10.rtt.Types.hashCode(((java.lang.Object)(t57765)));
            
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return t57766;
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
public java.lang.String
                                                                                            toString(
                                                                                            ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final $T t57767 =
              (($T)(value));
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final java.lang.String t57768 =
              x10.rtt.Types.toString(((java.lang.Object)(t57767)));
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return t57768;
        }
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
public boolean
                                                                                            equals(
                                                                                            final java.lang.Object x){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final boolean t57769 =
              ((x) == (null));
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
if (t57769) {
                
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return false;
            }
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final boolean t57772 =
              $T.isInstance(x);
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
if (t57772) {
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final $T y =
                  (($T)(x10.rtt.Types.<$T> cast(x,$T)));
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final $T t57770 =
                  (($T)(value));
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final boolean t57771 =
                  ((java.lang.Object)(((java.lang.Object)(t57770)))).equals(y);
                
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return t57771;
            }
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final boolean t57776 =
              x10.util.Box.$RTT.isInstance(x, $T);
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
if (t57776) {
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final x10.util.Box<$T> t57773 =
                  ((x10.util.Box)(x10.rtt.Types.<x10.util.Box<$T>> cast(x,x10.rtt.ParameterizedType.make(x10.util.Box.$RTT, $T))));
                
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final $T y =
                  (($T)(((x10.util.Box<$T>)t57773).
                          value));
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final $T t57774 =
                  (($T)(value));
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final boolean t57775 =
                  ((java.lang.Object)(((java.lang.Object)(t57774)))).equals(y);
                
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return t57775;
            }
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return false;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
final public x10.util.Box<$T>
                                                                                            x10$util$Box$$x10$util$Box$this(
                                                                                            ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/util/Box.x10"
return x10.util.Box.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __0x10$util$Box$$T {}
    
}
