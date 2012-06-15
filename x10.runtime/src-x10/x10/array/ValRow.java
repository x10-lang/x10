package x10.array;

@x10.core.X10Generated public class ValRow extends x10.array.Row implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ValRow.class);
    
    public static final x10.rtt.RuntimeType<ValRow> $RTT = x10.rtt.NamedType.<ValRow> make(
    "x10.array.ValRow", /* base class */ValRow.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Row.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ValRow $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + ValRow.class + " calling"); } 
        x10.array.Row.$_deserialize_body($_obj, $deserializer);
        x10.array.Array row = (x10.array.Array) $deserializer.readRef();
        $_obj.row = row;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ValRow $_obj = new ValRow((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (row instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.row);
        } else {
        $serializer.write(this.row);
        }
        
    }
    
    // constructor just for allocation
    public ValRow(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
public x10.array.Array<x10.core.Int> row;
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
// creation method for java code (1-phase java constructor)
        public ValRow(final x10.array.Array<x10.core.Int> row, __0$1x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                               $init(row, (x10.array.ValRow.__0$1x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.ValRow x10$array$ValRow$$init$S(final x10.array.Array<x10.core.Int> row, __0$1x10$lang$Int$2 $dummy) { {
                                                                                                                                             
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
final int t48360 =
                                                                                                                                               ((x10.array.Array<x10.core.Int>)row).
                                                                                                                                                 size;
                                                                                                                                             
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
super.$init(((int)(t48360)));
                                                                                                                                             
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"

                                                                                                                                             
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
this.row = ((x10.array.Array)(row));
                                                                                                                                         }
                                                                                                                                         return this;
                                                                                                                                         }
        
        // constructor
        public x10.array.ValRow $init(final x10.array.Array<x10.core.Int> row, __0$1x10$lang$Int$2 $dummy){return x10$array$ValRow$$init$S(row, $dummy);}
        
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
// creation method for java code (1-phase java constructor)
        public ValRow(final int cols,
                      final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                $init(cols,init, (x10.array.ValRow.__1$1x10$lang$Int$3x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.ValRow x10$array$ValRow$$init$S(final int cols,
                                                               final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                                              
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
super.$init(((int)(cols)));
                                                                                                                                                                              
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"

                                                                                                                                                                              
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
final x10.array.Array<x10.core.Int> t48356 =
                                                                                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(cols)),
                                                                                                                                                                                                                                                                                         ((x10.core.fun.Fun_0_1)(init)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                                                                              
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
this.row = ((x10.array.Array)(t48356));
                                                                                                                                                                          }
                                                                                                                                                                          return this;
                                                                                                                                                                          }
        
        // constructor
        public x10.array.ValRow $init(final int cols,
                                      final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy){return x10$array$ValRow$$init$S(cols,init, $dummy);}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
public int
                                                                                                $apply$O(
                                                                                                final int i){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
final x10.array.Array<x10.core.Int> t48357 =
              ((x10.array.Array)(row));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
final int t48358 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t48357).$apply$G((int)(i)));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
return t48358;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
public int
                                                                                                $set$O(
                                                                                                final int i,
                                                                                                final int v){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
final x10.lang.IllegalOperationException t48359 =
              ((x10.lang.IllegalOperationException)(new x10.lang.IllegalOperationException(((java.lang.String)("ValRow.set")))));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
throw t48359;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
final public x10.array.ValRow
                                                                                                x10$array$ValRow$$x10$array$ValRow$this(
                                                                                                ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/ValRow.x10"
return x10.array.ValRow.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __0$1x10$lang$Int$2 {}
    // synthetic type for parameter mangling
    public abstract static class __1$1x10$lang$Int$3x10$lang$Int$2 {}
    
}
