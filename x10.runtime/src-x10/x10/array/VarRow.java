package x10.array;

@x10.core.X10Generated final public class VarRow extends x10.array.Row implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, VarRow.class);
    
    public static final x10.rtt.RuntimeType<VarRow> $RTT = x10.rtt.NamedType.<VarRow> make(
    "x10.array.VarRow", /* base class */VarRow.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Row.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(VarRow $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + VarRow.class + " calling"); } 
        x10.array.Row.$_deserialize_body($_obj, $deserializer);
        x10.array.Array row = (x10.array.Array) $deserializer.readRef();
        $_obj.row = row;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        VarRow $_obj = new VarRow((java.lang.System[]) null);
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
    public VarRow(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
public x10.array.Array<x10.core.Int> row;
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
// creation method for java code (1-phase java constructor)
        public VarRow(final int cols,
                      final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                $init(cols,init, (x10.array.VarRow.__1$1x10$lang$Int$3x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.VarRow x10$array$VarRow$$init$S(final int cols,
                                                               final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                                              
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
super.$init(((int)(cols)));
                                                                                                                                                                              
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"

                                                                                                                                                                              
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.array.Array<x10.core.Int> t48375 =
                                                                                                                                                                                ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(cols)),
                                                                                                                                                                                                                                                                                         ((x10.core.fun.Fun_0_1)(init)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                                                                              
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
this.row = ((x10.array.Array)(t48375));
                                                                                                                                                                          }
                                                                                                                                                                          return this;
                                                                                                                                                                          }
        
        // constructor
        public x10.array.VarRow $init(final int cols,
                                      final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> init, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy){return x10$array$VarRow$$init$S(cols,init, $dummy);}
        
        
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
// creation method for java code (1-phase java constructor)
        public VarRow(final int cols){this((java.lang.System[]) null);
                                          $init(cols);}
        
        // constructor for non-virtual call
        final public x10.array.VarRow x10$array$VarRow$$init$S(final int cols) { {
                                                                                        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
super.$init(((int)(cols)));
                                                                                        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"

                                                                                        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.array.Array<x10.core.Int> t48376 =
                                                                                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(cols)))));
                                                                                        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
this.row = ((x10.array.Array)(t48376));
                                                                                    }
                                                                                    return this;
                                                                                    }
        
        // constructor
        public x10.array.VarRow $init(final int cols){return x10$array$VarRow$$init$S(cols);}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
public x10.array.Array<x10.core.Int>
                                                                                                row(
                                                                                                ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.array.Array<x10.core.Int> t48377 =
              ((x10.array.Array)(row));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.array.Array<x10.core.Int> __desugarer__var__41__48372 =
              ((x10.array.Array)(((x10.array.Array<x10.core.Int>)
                                   t48377)));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
x10.array.Array<x10.core.Int> ret48373 =
               null;
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final int t48394 =
              ((x10.array.Array<x10.core.Int>)__desugarer__var__41__48372).
                rank;
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
boolean t48395 =
              ((int) t48394) ==
            ((int) 1);
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
if (t48395) {
                
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final boolean t48396 =
                  ((x10.array.Array<x10.core.Int>)__desugarer__var__41__48372).
                    zeroBased;
                
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
t48395 = ((boolean) t48396) ==
                ((boolean) true);
            }
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
boolean t48397 =
              t48395;
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
if (t48397) {
                
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final boolean t48398 =
                  ((x10.array.Array<x10.core.Int>)__desugarer__var__41__48372).
                    rect;
                
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
t48397 = ((boolean) t48398) ==
                ((boolean) true);
            }
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
boolean t48399 =
              t48397;
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
if (t48399) {
                
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final boolean t48400 =
                  ((x10.array.Array<x10.core.Int>)__desugarer__var__41__48372).
                    rail;
                
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
t48399 = ((boolean) t48400) ==
                ((boolean) true);
            }
            
//#line 28 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final boolean t48401 =
              t48399;
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final boolean t48402 =
              !(t48401);
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
if (t48402) {
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final boolean t48403 =
                  true;
                
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
if (t48403) {
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.lang.FailedDynamicCheckException t48404 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Int]{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true}");
                    
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
throw t48404;
                }
            }
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
ret48373 = ((x10.array.Array)(__desugarer__var__41__48372));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.array.Array<x10.core.Int> t48389 =
              ((x10.array.Array)(ret48373));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
return t48389;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
public int
                                                                                                $apply$O(
                                                                                                final int i){
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.array.Array<x10.core.Int> t48390 =
              ((x10.array.Array)(this.row()));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final int t48391 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t48390).$apply$G((int)(i)));
            
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
return t48391;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
public int
                                                                                                $set$O(
                                                                                                final int i,
                                                                                                final int v){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final x10.array.Array<x10.core.Int> t48392 =
              ((x10.array.Array)(this.row()));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final int t48393 =
              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t48392).$set__1x10$array$Array$$T$G((int)(i),
                                                                                                      x10.core.Int.$box(v)));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
return t48393;
        }
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
final public x10.array.VarRow
                                                                                                x10$array$VarRow$$x10$array$VarRow$this(
                                                                                                ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarRow.x10"
return x10.array.VarRow.this;
        }
    // synthetic type for parameter mangling
    public abstract static class __1$1x10$lang$Int$3x10$lang$Int$2 {}
    
}
