package x10.array;

@x10.core.X10Generated final public class VarMat extends x10.array.Mat<x10.array.VarRow> implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, VarMat.class);
    
    public static final x10.rtt.RuntimeType<VarMat> $RTT = x10.rtt.NamedType.<VarMat> make(
    "x10.array.VarMat", /* base class */VarMat.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.array.Mat.$RTT, x10.array.VarRow.$RTT)}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(VarMat $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + VarMat.class + " calling"); } 
        x10.array.Mat.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        VarMat $_obj = new VarMat((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        
    }
    
    // constructor just for allocation
    public VarMat(final java.lang.System[] $dummy) { 
    super($dummy, x10.array.VarRow.$RTT);
    }
    // bridge for method public x10.array.Mat.operator()(i:x10.lang.Int):T{this(:x10.array.Mat).mat.rank==1, this(:x10.array.Mat).mat.region.rank==1, this(:x10.array.Mat).mat.region!=null, this(:x10.array.Mat).mat.rect==this(:x10.array.Mat).mat.region.rect, this(:x10.array.Mat).mat.zeroBased==this(:x10.array.Mat).mat.region.zeroBased, this(:x10.array.Mat).mat.rail==this(:x10.array.Mat).mat.region.rail}
    public x10.array.VarRow
      $apply(int a1){return super.$apply$G((a1));}
    
        
        
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
// creation method for java code (1-phase java constructor)
        public VarMat(final int cols,
                      final x10.array.Array<x10.array.VarRow> mat, __1$1x10$array$VarRow$2 $dummy){this((java.lang.System[]) null);
                                                                                                       $init(cols,mat, (x10.array.VarMat.__1$1x10$array$VarRow$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.VarMat x10$array$VarMat$$init$S(final int cols,
                                                               final x10.array.Array<x10.array.VarRow> mat, __1$1x10$array$VarRow$2 $dummy) { {
                                                                                                                                                     
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final int t48369 =
                                                                                                                                                       ((x10.array.Array<x10.array.VarRow>)mat).
                                                                                                                                                         size;
                                                                                                                                                     
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
super.$init(((int)(t48369)),
                                                                                                                                                                                                                                                       ((int)(cols)),
                                                                                                                                                                                                                                                       ((x10.array.Array)(mat)), (x10.array.Mat.__2$1x10$array$Mat$$T$2) null);
                                                                                                                                                     
//#line 16 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"

                                                                                                                                                 }
                                                                                                                                                 return this;
                                                                                                                                                 }
        
        // constructor
        public x10.array.VarMat $init(final int cols,
                                      final x10.array.Array<x10.array.VarRow> mat, __1$1x10$array$VarRow$2 $dummy){return x10$array$VarMat$$init$S(cols,mat, $dummy);}
        
        
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
// creation method for java code (1-phase java constructor)
        public VarMat(final int rows,
                      final int cols,
                      final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.VarRow> init, __2$1x10$lang$Int$3x10$array$VarRow$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                        $init(rows,cols,init, (x10.array.VarMat.__2$1x10$lang$Int$3x10$array$VarRow$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.VarMat x10$array$VarMat$$init$S(final int rows,
                                                               final int cols,
                                                               final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.VarRow> init, __2$1x10$lang$Int$3x10$array$VarRow$2 $dummy) { {
                                                                                                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final x10.array.Array<x10.array.VarRow> t48370 =
                                                                                                                                                                                        ((x10.array.Array)(new x10.array.Array<x10.array.VarRow>((java.lang.System[]) null, x10.array.VarRow.$RTT).$init(((int)(rows)),
                                                                                                                                                                                                                                                                                                         ((x10.core.fun.Fun_0_1)(init)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                                                                                      
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
super.$init(((int)(rows)),
                                                                                                                                                                                                                                                                                        ((int)(cols)),
                                                                                                                                                                                                                                                                                        ((x10.array.Array)(t48370)), (x10.array.Mat.__2$1x10$array$Mat$$T$2) null);
                                                                                                                                                                                      
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"

                                                                                                                                                                                  }
                                                                                                                                                                                  return this;
                                                                                                                                                                                  }
        
        // constructor
        public x10.array.VarMat $init(final int rows,
                                      final int cols,
                                      final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.VarRow> init, __2$1x10$lang$Int$3x10$array$VarRow$2 $dummy){return x10$array$VarMat$$init$S(rows,cols,init, $dummy);}
        
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
// creation method for java code (1-phase java constructor)
        public VarMat(final int rows,
                      final int cols,
                      final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init, __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                                                                           $init(rows,cols,init, (x10.array.VarMat.__2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.VarMat x10$array$VarMat$$init$S(final int rows,
                                                               final int cols,
                                                               final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init, __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                                                                         
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.VarRow> t48366 =
                                                                                                                                                                                                           ((x10.core.fun.Fun_0_1)(new x10.array.VarMat.$Closure$86(init,
                                                                                                                                                                                                                                                                    cols, (x10.array.VarMat.$Closure$86.__0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2) null)));
                                                                                                                                                                                                         
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
this.$init(((int)(rows)),
                                                                                                                                                                                                                                                                                                          ((int)(cols)),
                                                                                                                                                                                                                                                                                                          ((x10.core.fun.Fun_0_1)(t48366)), (x10.array.VarMat.__2$1x10$lang$Int$3x10$array$VarRow$2) null);
                                                                                                                                                                                                     }
                                                                                                                                                                                                     return this;
                                                                                                                                                                                                     }
        
        // constructor
        public x10.array.VarMat $init(final int rows,
                                      final int cols,
                                      final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init, __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy){return x10$array$VarMat$$init$S(rows,cols,init, $dummy);}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
// creation method for java code (1-phase java constructor)
        public VarMat(final int rows,
                      final int cols){this((java.lang.System[]) null);
                                          $init(rows,cols);}
        
        // constructor for non-virtual call
        final public x10.array.VarMat x10$array$VarMat$$init$S(final int rows,
                                                               final int cols) { {
                                                                                        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.VarRow> t48368 =
                                                                                          ((x10.core.fun.Fun_0_1)(new x10.array.VarMat.$Closure$87(cols)));
                                                                                        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
this.$init(((int)(rows)),
                                                                                                                                                                                         ((int)(cols)),
                                                                                                                                                                                         ((x10.core.fun.Fun_0_1)(t48368)), (x10.array.VarMat.__2$1x10$lang$Int$3x10$array$VarRow$2) null);
                                                                                    }
                                                                                    return this;
                                                                                    }
        
        // constructor
        public x10.array.VarMat $init(final int rows,
                                      final int cols){return x10$array$VarMat$$init$S(rows,cols);}
        
        
        
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final public x10.array.VarMat
                                                                                                x10$array$VarMat$$x10$array$VarMat$this(
                                                                                                ){
            
//#line 14 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
return x10.array.VarMat.this;
        }
        
        @x10.core.X10Generated public static class $Closure$85 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$85.class);
            
            public static final x10.rtt.RuntimeType<$Closure$85> $RTT = x10.rtt.StaticFunType.<$Closure$85> make(
            /* base class */$Closure$85.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$85 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$85.class + " calling"); } 
                x10.core.fun.Fun_0_2 init = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.init = init;
                $_obj.i = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$85 $_obj = new $Closure$85((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                $serializer.write(this.i);
                
            }
            
            // constructor just for allocation
            public $Closure$85(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int j){
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final int t48363 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int>)this.
                                                                                                           init).$apply(x10.core.Int.$box(this.
                                                                                                                                            i),x10.rtt.Types.INT,
                                                                                                                        x10.core.Int.$box(j),x10.rtt.Types.INT));
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
return t48363;
                }
                
                public x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init;
                public int i;
                
                public $Closure$85(final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init,
                                   final int i, __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                 this.init = ((x10.core.fun.Fun_0_2)(init));
                                                                                                                 this.i = i;
                                                                                                             }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$86 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$86.class);
            
            public static final x10.rtt.RuntimeType<$Closure$86> $RTT = x10.rtt.StaticFunType.<$Closure$86> make(
            /* base class */$Closure$86.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.VarRow.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$86 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$86.class + " calling"); } 
                x10.core.fun.Fun_0_2 init = (x10.core.fun.Fun_0_2) $deserializer.readRef();
                $_obj.init = init;
                $_obj.cols = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$86 $_obj = new $Closure$86((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (init instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.init);
                } else {
                $serializer.write(this.init);
                }
                $serializer.write(this.cols);
                
            }
            
            // constructor just for allocation
            public $Closure$86(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.VarRow
                  $apply(
                  final int i){
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t48364 =
                      ((x10.core.fun.Fun_0_1)(new x10.array.VarMat.$Closure$85(((x10.core.fun.Fun_0_2)(this.
                                                                                                         init)),
                                                                               i, (x10.array.VarMat.$Closure$85.__0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2) null)));
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final x10.array.VarRow t48365 =
                      ((x10.array.VarRow)(new x10.array.VarRow((java.lang.System[]) null).$init(this.
                                                                                                  cols,
                                                                                                ((x10.core.fun.Fun_0_1)(t48364)), (x10.array.VarRow.__1$1x10$lang$Int$3x10$lang$Int$2) null)));
                    
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
return t48365;
                }
                
                public x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init;
                public int cols;
                
                public $Closure$86(final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> init,
                                   final int cols, __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                    this.init = ((x10.core.fun.Fun_0_2)(init));
                                                                                                                    this.cols = cols;
                                                                                                                }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$87 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$87.class);
            
            public static final x10.rtt.RuntimeType<$Closure$87> $RTT = x10.rtt.StaticFunType.<$Closure$87> make(
            /* base class */$Closure$87.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.VarRow.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$87 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$87.class + " calling"); } 
                $_obj.cols = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$87 $_obj = new $Closure$87((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.cols);
                
            }
            
            // constructor just for allocation
            public $Closure$87(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.VarRow
                  $apply(
                  final int id$80){
                    
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
final x10.array.VarRow t48367 =
                      ((x10.array.VarRow)(new x10.array.VarRow((java.lang.System[]) null).$init(this.
                                                                                                  cols)));
                    
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/VarMat.x10"
return t48367;
                }
                
                public int cols;
                
                public $Closure$87(final int cols) { {
                                                            this.cols = cols;
                                                        }}
                
            }
            
        // synthetic type for parameter mangling
        public abstract static class __1$1x10$array$VarRow$2 {}
        // synthetic type for parameter mangling
        public abstract static class __2$1x10$lang$Int$3x10$array$VarRow$2 {}
        // synthetic type for parameter mangling
        public abstract static class __2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2 {}
        
        }
        