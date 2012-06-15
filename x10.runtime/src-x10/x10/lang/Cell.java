package x10.lang;

@x10.core.X10Generated final public class Cell<$T> extends x10.core.Ref implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Cell.class);
    
    public static final x10.rtt.RuntimeType<Cell> $RTT = x10.rtt.NamedType.<Cell> make(
    "x10.lang.Cell", /* base class */Cell.class, 
    /* variances */ x10.rtt.RuntimeType.INVARIANTS(1)
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $T;return null;}
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Cell $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Cell.class + " calling"); } 
        $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
        $_obj.value = $deserializer.readRef();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Cell $_obj = new Cell((java.lang.System[]) null, (x10.rtt.Type) null);
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
    public Cell(final java.lang.System[] $dummy, final x10.rtt.Type $T) { 
    super($dummy);
    x10.lang.Cell.$initParams(this, $T);
    }
    
        private x10.rtt.Type $T;
        // initializer of type parameters
        public static void $initParams(final Cell $this, final x10.rtt.Type $T) {
        $this.$T = $T;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
/**
     * The value stored in this cell.
     */
        public $T value;
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
// creation method for java code (1-phase java constructor)
        public Cell(final x10.rtt.Type $T,
                    final $T x, __0x10$lang$Cell$$T $dummy){this((java.lang.System[]) null, $T);
                                                                $init(x, (x10.lang.Cell.__0x10$lang$Cell$$T) null);}
        
        // constructor for non-virtual call
        final public x10.lang.Cell<$T> x10$lang$Cell$$init$S(final $T x, __0x10$lang$Cell$$T $dummy) { {
                                                                                                              
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"

                                                                                                              
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"

                                                                                                              
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
this.value = (($T)(x));
                                                                                                          }
                                                                                                          return this;
                                                                                                          }
        
        // constructor
        public x10.lang.Cell<$T> $init(final $T x, __0x10$lang$Cell$$T $dummy){return x10$lang$Cell$$init$S(x, $dummy);}
        
        
        
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
public java.lang.String
                                                                                             toString(
                                                                                             ){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final $T t50788 =
              (($T)(this.
                      value));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final java.lang.String t50789 =
              x10.rtt.Types.toString(((java.lang.Object)(t50788)));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final java.lang.String t50790 =
              (("Cell(") + (t50789));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final java.lang.String t50791 =
              ((t50790) + (")"));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
return t50791;
        }
        
        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
public $T
                                                                                             $apply$G(
                                                                                             ){
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final $T t50792 =
              (($T)(value));
            
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
return t50792;
        }
        
        
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
public void
                                                                                             $apply__0x10$lang$Cell$$T(
                                                                                             final $T x){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
this.value = (($T)(x));
        }
        
        
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
public void
                                                                                             $set__0x10$lang$Cell$$T(
                                                                                             final $T x){
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
this.set__0x10$lang$Cell$$T$G((($T)(x)));
        }
        
        
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
public $T
                                                                                             set__0x10$lang$Cell$$T$G(
                                                                                             final $T x){
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
this.value = (($T)(x));
            
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
return x;
        }
        
        
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
public static <$T>x10.lang.Cell<$T>
                                                                                             make__0x10$lang$Cell$$T(
                                                                                             final x10.rtt.Type $T,
                                                                                             final $T x){
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final x10.lang.Cell<$T> t50793 =
              ((x10.lang.Cell)(new x10.lang.Cell<$T>((java.lang.System[]) null, $T).$init((($T)(x)), (x10.lang.Cell.__0x10$lang$Cell$$T) null)));
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
return t50793;
        }
        
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
public static <$W>x10.lang.Cell<$W>
                                                                                             $implicit_convert__0x10$lang$Cell$$W(
                                                                                             final x10.rtt.Type $W,
                                                                                             final $W x){
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final x10.lang.Cell<$W> t50794 =
              ((x10.lang.Cell)(x10.lang.Cell.<$W>make__0x10$lang$Cell$$T($W, (($W)(x)))));
            
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
return t50794;
        }
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
final public x10.lang.Cell<$T>
                                                                                             x10$lang$Cell$$x10$lang$Cell$this(
                                                                                             ){
            
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/lang/Cell.x10"
return x10.lang.Cell.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __0x10$lang$Cell$$T {}
    
}
