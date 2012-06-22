package x10.array;


@x10.core.X10Generated public class PolyMatBuilder extends x10.array.MatBuilder implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PolyMatBuilder.class);
    
    public static final x10.rtt.RuntimeType<PolyMatBuilder> $RTT = x10.rtt.NamedType.<PolyMatBuilder> make(
    "x10.array.PolyMatBuilder", /* base class */PolyMatBuilder.class
    , /* parents */ new x10.rtt.Type[] {x10.array.MatBuilder.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PolyMatBuilder $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + PolyMatBuilder.class + " calling"); } 
        x10.array.MatBuilder.$_deserialize_body($_obj, $deserializer);
        $_obj.rank = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PolyMatBuilder $_obj = new PolyMatBuilder((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.rank);
        
    }
    
    // constructor just for allocation
    public PolyMatBuilder(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
public int rank;
        
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
// creation method for java code (1-phase java constructor)
        public PolyMatBuilder(final int rank){this((java.lang.System[]) null);
                                                  $init(rank);}
        
        // constructor for non-virtual call
        final public x10.array.PolyMatBuilder x10$array$PolyMatBuilder$$init$S(final int rank) { {
                                                                                                        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41454 =
                                                                                                          ((rank) + (((int)(1))));
                                                                                                        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
super.$init(t41454);
                                                                                                        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
this.rank = rank;
                                                                                                        
                                                                                                    }
                                                                                                    return this;
                                                                                                    }
        
        // constructor
        public x10.array.PolyMatBuilder $init(final int rank){return x10$array$PolyMatBuilder$$init$S(rank);}
        
        
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
public x10.array.PolyMat
                                                                                                        toSortedPolyMat(
                                                                                                        final boolean isSimplified){
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t41408 =
              ((x10.util.ArrayList)(mat));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.core.fun.Fun_0_2<x10.array.Row,x10.array.Row,x10.core.Int> t41409 =
              ((x10.core.fun.Fun_0_2)(new x10.array.PolyMatBuilder.$Closure$53()));
            
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
((x10.util.ArrayList<x10.array.Row>)t41408).sort__0$1x10$util$ArrayList$$T$3x10$util$ArrayList$$T$3x10$lang$Int$2(((x10.core.fun.Fun_0_2)(t41409)));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t41410 =
              ((x10.util.ArrayList)(mat));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41415 =
              ((x10.util.ArrayList<x10.array.Row>)t41410).size$O();
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41411 =
              rank;
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41416 =
              ((t41411) + (((int)(1))));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.core.fun.Fun_0_2<x10.core.Int,x10.core.Int,x10.core.Int> t41417 =
              ((x10.core.fun.Fun_0_2)(new x10.array.PolyMatBuilder.$Closure$54(this,
                                                                               mat, (x10.array.PolyMatBuilder.$Closure$54.__1$1x10$array$Row$2) null)));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.array.PolyMat result =
              ((x10.array.PolyMat)(new x10.array.PolyMat((java.lang.System[]) null).$init(t41415,
                                                                                          t41416,
                                                                                          ((x10.core.fun.Fun_0_2)(t41417)),
                                                                                          ((boolean)(isSimplified)), (x10.array.PolyMat.__2$1x10$lang$Int$3x10$lang$Int$3x10$lang$Int$2) null)));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.array.PolyMat __desugarer__var__21__41403 =
              ((x10.array.PolyMat)(((x10.array.PolyMat)
                                     result)));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
x10.array.PolyMat ret41404 =
               null;
            
//#line 44 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41455 =
              __desugarer__var__21__41403.
                rank;
            
//#line 44 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41456 =
              x10.array.PolyMatBuilder.this.
                rank;
            
//#line 44 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final boolean t41457 =
              ((int) t41455) ==
            ((int) t41456);
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final boolean t41458 =
              !(t41457);
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
if (t41458) {
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final boolean t41459 =
                  true;
                
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
if (t41459) {
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.lang.FailedDynamicCheckException t41460 =
                      new x10.lang.FailedDynamicCheckException("x10.array.PolyMat{self.rank==this(:x10.array.PolyMatBuilder).rank}");
                    
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
throw t41460;
                }
            }
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
ret41404 = ((x10.array.PolyMat)(__desugarer__var__21__41403));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.array.PolyMat t41424 =
              ((x10.array.PolyMat)(ret41404));
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
return t41424;
        }
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
/**
     * a simple mechanism of somewhat dubious utility to allow
     * semi-symbolic specification of halfspaces. For example
     * X0-Y1 >= n is specified as add(X(0)-Y(1), GE, n)
     *
     * XXX coefficients must be -1,0,+1; can allow larger coefficients
     * by increasing # bits per coeff
     */
        final public static int ZERO = 178956970;
        
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final public static int GE = 0;
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final public static int LE = 1;
        
        
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final public static int
                                                                                                        X$O(
                                                                                                        final int axis){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41425 =
              ((2) * (((int)(axis))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41426 =
              ((1) << (((int)(t41425))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
return t41426;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
public void
                                                                                                        add(
                                                                                                        int coeff,
                                                                                                        final int op,
                                                                                                        final int k){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41427 =
              coeff;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41428 =
              x10.array.PolyMatBuilder.ZERO;
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41429 =
              ((t41427) + (((int)(t41428))));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
coeff = t41429;
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41430 =
              rank;
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41431 =
              ((t41430) + (((int)(1))));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.array.Array<x10.core.Int> as_ =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(t41431)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
int i41473 =
              0;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41474 =
                  i41473;
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41475 =
                  rank;
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final boolean t41476 =
                  ((t41474) < (((int)(t41475))));
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
if (!(t41476)) {
                    
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
break;
                }
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41461 =
                  coeff;
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41462 =
                  ((t41461) & (((int)(3))));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int a41463 =
                  ((t41462) - (((int)(2))));
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41464 =
                  i41473;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41465 =
                  x10.array.PolyMatBuilder.LE;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final boolean t41466 =
                  ((int) op) ==
                ((int) t41465);
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
int t41467 =
                   0;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
if (t41466) {
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
t41467 = a41463;
                } else {
                    
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
t41467 = (-(a41463));
                }
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41468 =
                  t41467;
                
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
((x10.array.Array<x10.core.Int>)as_).$set__1x10$array$Array$$T$G((int)(t41464),
                                                                                                                                                                               x10.core.Int.$box(t41468));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41469 =
                  coeff;
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41470 =
                  ((t41469) >> (((int)(2))));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
coeff = t41470;
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41471 =
                  i41473;
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41472 =
                  ((t41471) + (((int)(1))));
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
i41473 = t41472;
            }
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41450 =
              rank;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41447 =
              x10.array.PolyMatBuilder.LE;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final boolean t41448 =
              ((int) op) ==
            ((int) t41447);
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
int t41449 =
               0;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
if (t41448) {
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
t41449 = (-(k));
            } else {
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
t41449 = k;
            }
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41451 =
              t41449;
            
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
((x10.array.Array<x10.core.Int>)as_).$set__1x10$array$Array$$T$G((int)(t41450),
                                                                                                                                                                           x10.core.Int.$box(t41451));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t41453 =
              ((x10.core.fun.Fun_0_1)(new x10.array.PolyMatBuilder.$Closure$55(as_, (x10.array.PolyMatBuilder.$Closure$55.__0$1x10$lang$Int$2) null)));
            
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
this.add__0$1x10$lang$Int$3x10$lang$Int$2(((x10.core.fun.Fun_0_1)(t41453)));
        }
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final public x10.array.PolyMatBuilder
                                                                                                        x10$array$PolyMatBuilder$$x10$array$PolyMatBuilder$this(
                                                                                                        ){
            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
return x10.array.PolyMatBuilder.this;
        }
        
        public static int
          getInitialized$ZERO(
          ){
            return x10.array.PolyMatBuilder.ZERO;
        }
        
        public static int
          getInitialized$GE(
          ){
            return x10.array.PolyMatBuilder.GE;
        }
        
        public static int
          getInitialized$LE(
          ){
            return x10.array.PolyMatBuilder.LE;
        }
        
        @x10.core.X10Generated public static class $Closure$53 extends x10.core.Ref implements x10.core.fun.Fun_0_2, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$53.class);
            
            public static final x10.rtt.RuntimeType<$Closure$53> $RTT = x10.rtt.StaticFunType.<$Closure$53> make(
            /* base class */$Closure$53.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_2.$RTT, x10.array.Row.$RTT, x10.array.Row.$RTT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$53 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$53.class + " calling"); } 
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$53 $_obj = new $Closure$53((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$53(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1, a2:Z2)=>U.operator()(a1:Z1,a2:Z2):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Int.$box($apply$O((x10.array.Row)a1, (x10.array.Row)a2));
            }
            
                
                public int
                  $apply$O(
                  final x10.array.Row x,
                  final x10.array.Row y){
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41407 =
                      x10.array.PolyRow.compare$O(((x10.array.Row)(x)),
                                                  ((x10.array.Row)(y)));
                    
//#line 42 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
return t41407;
                }
                
                public $Closure$53() { {
                                              
                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$54 extends x10.core.Ref implements x10.core.fun.Fun_0_2, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$54.class);
            
            public static final x10.rtt.RuntimeType<$Closure$54> $RTT = x10.rtt.StaticFunType.<$Closure$54> make(
            /* base class */$Closure$54.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_2.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$54 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$54.class + " calling"); } 
                x10.array.PolyMatBuilder out$$ = (x10.array.PolyMatBuilder) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.util.ArrayList mat = (x10.util.ArrayList) $deserializer.readRef();
                $_obj.mat = mat;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$54 $_obj = new $Closure$54((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                if (mat instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.mat);
                } else {
                $serializer.write(this.mat);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$54(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1, a2:Z2)=>U.operator()(a1:Z1,a2:Z2):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1, final java.lang.Object a2, final x10.rtt.Type t2) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1), x10.core.Int.$unbox(a2)));
            }
            
                
                public int
                  $apply$O(
                  final int i,
                  final int j){
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.util.ArrayList<x10.array.Row> t41412 =
                      ((x10.util.ArrayList)(this.
                                              mat));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final x10.array.Row t41413 =
                      ((x10.array.Row)(((x10.util.ArrayList<x10.array.Row>)t41412).$apply$G((int)(i))));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41414 =
                      t41413.$apply$O((int)(j));
                    
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
return t41414;
                }
                
                public x10.array.PolyMatBuilder out$$;
                public x10.util.ArrayList<x10.array.Row> mat;
                
                public $Closure$54(final x10.array.PolyMatBuilder out$$,
                                   final x10.util.ArrayList<x10.array.Row> mat, __1$1x10$array$Row$2 $dummy) { {
                                                                                                                      this.out$$ = out$$;
                                                                                                                      this.mat = ((x10.util.ArrayList)(mat));
                                                                                                                  }}
                // synthetic type for parameter mangling
                public abstract static class __1$1x10$array$Row$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$55 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$55.class);
            
            public static final x10.rtt.RuntimeType<$Closure$55> $RTT = x10.rtt.StaticFunType.<$Closure$55> make(
            /* base class */$Closure$55.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$55 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$55.class + " calling"); } 
                x10.array.Array as_ = (x10.array.Array) $deserializer.readRef();
                $_obj.as_ = as_;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$55 $_obj = new $Closure$55((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (as_ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.as_);
                } else {
                $serializer.write(this.as_);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$55(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
final int t41452 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)this.
                                                                            as_).$apply$G((int)(i)));
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/PolyMatBuilder.x10"
return t41452;
                }
                
                public x10.array.Array<x10.core.Int> as_;
                
                public $Closure$55(final x10.array.Array<x10.core.Int> as_, __0$1x10$lang$Int$2 $dummy) { {
                                                                                                                 this.as_ = ((x10.array.Array)(as_));
                                                                                                             }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$Int$2 {}
                
            }
            
        
        }
        
        