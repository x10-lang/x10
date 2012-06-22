package x10.array;


@x10.core.X10Generated abstract public class Region extends x10.core.Ref implements x10.lang.Iterable, x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Region.class);
    
    public static final x10.rtt.RuntimeType<Region> $RTT = x10.rtt.NamedType.<Region> make(
    "x10.array.Region", /* base class */Region.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterable.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Region $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + Region.class + " calling"); } 
        $_obj.rank = $deserializer.readInt();
        $_obj.rect = $deserializer.readBoolean();
        $_obj.zeroBased = $deserializer.readBoolean();
        $_obj.rail = $deserializer.readBoolean();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return null;
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.rank);
        $serializer.write(this.rect);
        $serializer.write(this.zeroBased);
        $serializer.write(this.rail);
        
    }
    
    // constructor just for allocation
    public Region(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public int rank;
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean rect;
        
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean zeroBased;
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean rail;
        
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final public int
                                                                                                rank$O(
                                                                                                ){
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47316 =
              rank;
            
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47316;
        }
        
        
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final public boolean
                                                                                                rect$O(
                                                                                                ){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47317 =
              rect;
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47317;
        }
        
        
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final public boolean
                                                                                                zeroBased$O(
                                                                                                ){
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47318 =
              zeroBased;
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47318;
        }
        
        
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final public boolean
                                                                                                rail$O(
                                                                                                ){
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47319 =
              rail;
            
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47319;
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                makeEmpty(
                                                                                                final int rank){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.EmptyRegion t47320 =
              ((x10.array.EmptyRegion)(new x10.array.EmptyRegion((java.lang.System[]) null).$init(((int)(rank)))));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47320;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                makeFull(
                                                                                                final int rank){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.FullRegion t47321 =
              ((x10.array.FullRegion)(new x10.array.FullRegion((java.lang.System[]) null).$init(((int)(rank)))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47321;
        }
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                makeUnit(
                                                                                                ){
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.FullRegion t47322 =
              ((x10.array.FullRegion)(new x10.array.FullRegion((java.lang.System[]) null).$init(((int)(0)))));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47322;
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                makeHalfspace(
                                                                                                final x10.array.Point normal,
                                                                                                final int k){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int rank =
              normal.
                rank;
            
//#line 81 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(rank)))));
            
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.PolyRow r =
              ((x10.array.PolyRow)(new x10.array.PolyRow((java.lang.System[]) null).$init(((x10.array.Point)(normal)),
                                                                                          ((int)(k)))));
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
pmb.add(((x10.array.Row)(r)));
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47323 =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47323;
        }
        
        
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeRectangularPoly__0$1x10$lang$Int$2__1$1x10$lang$Int$2(
                                                                                                 final x10.array.Array<x10.core.Int> minArg,
                                                                                                 final x10.array.Array<x10.core.Int> maxArg){
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47324 =
              ((x10.array.Array<x10.core.Int>)minArg).
                size;
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47325 =
              ((x10.array.Array<x10.core.Int>)maxArg).
                size;
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47333 =
              ((int) t47324) !=
            ((int) t47325);
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47333) {
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47326 =
                  ((x10.array.Array<x10.core.Int>)minArg).
                    size;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final java.lang.String t47327 =
                  (("min and max not equal size (") + ((x10.core.Int.$box(t47326))));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final java.lang.String t47328 =
                  ((t47327) + (" != "));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47329 =
                  ((x10.array.Array<x10.core.Int>)maxArg).
                    size;
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final java.lang.String t47330 =
                  ((t47328) + ((x10.core.Int.$box(t47329))));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final java.lang.String t47331 =
                  ((t47330) + (")"));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.IllegalArgumentException t47332 =
                  ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(t47331)));
                
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
throw t47332;
            }
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int rank =
              ((x10.array.Array<x10.core.Int>)minArg).
                size;
            
//#line 106 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.PolyMatBuilder pmb =
              ((x10.array.PolyMatBuilder)(new x10.array.PolyMatBuilder((java.lang.System[]) null).$init(((int)(rank)))));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int i47286min47534 =
              0;
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int i47286max47535 =
              ((rank) - (((int)(1))));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
int i47531 =
              i47286min47534;
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
for (;
                                                                                                        true;
                                                                                                        ) {
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47532 =
                  i47531;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47533 =
                  ((t47532) <= (((int)(i47286max47535))));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (!(t47533)) {
                    
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
break;
                }
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int i47528 =
                  i47531;
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47505 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)minArg).$apply$G((int)(i47528)));
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47506 =
                  java.lang.Integer.MIN_VALUE;
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47507 =
                  ((t47505) > (((int)(t47506))));
                
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47507) {
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47508 =
                      ((x10.core.fun.Fun_0_1)(new x10.array.Region.$Closure$76(i47528)));
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47513 =
                      ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(rank),
                                                                                               ((x10.core.fun.Fun_0_1)(t47508)))));
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47514 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)minArg).$apply$G((int)(i47528)));
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.PolyRow r47515 =
                      ((x10.array.PolyRow)(new x10.array.PolyRow((java.lang.System[]) null).$init(((x10.array.Point)(t47513)),
                                                                                                  t47514)));
                    
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
pmb.add(((x10.array.Row)(r47515)));
                }
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47516 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)maxArg).$apply$G((int)(i47528)));
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47517 =
                  java.lang.Integer.MAX_VALUE;
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47518 =
                  ((t47516) < (((int)(t47517))));
                
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47518) {
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47519 =
                      ((x10.core.fun.Fun_0_1)(new x10.array.Region.$Closure$77(i47528)));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47524 =
                      ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(rank),
                                                                                               ((x10.core.fun.Fun_0_1)(t47519)))));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47525 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)maxArg).$apply$G((int)(i47528)));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47526 =
                      (-(t47525));
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.PolyRow s47527 =
                      ((x10.array.PolyRow)(new x10.array.PolyRow((java.lang.System[]) null).$init(((x10.array.Point)(t47524)),
                                                                                                  t47526)));
                    
//#line 116 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
pmb.add(((x10.array.Row)(s47527)));
                }
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47529 =
                  i47531;
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47530 =
                  ((t47529) + (((int)(1))));
                
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
i47531 = t47530;
            }
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.PolyMat pm =
              ((x10.array.PolyMat)(pmb.toSortedPolyMat((boolean)(false))));
            
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47358 =
              ((x10.array.Region)(x10.array.PolyRegion.make(((x10.array.PolyMat)(pm)))));
            
//#line 120 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47358;
        }
        
        
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static <$S, $T>x10.array.Region
                                                                                                 makeRectangular__0$1x10$array$Region$$S$2__1$1x10$array$Region$$T$2(
                                                                                                 final x10.rtt.Type $S,
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.Array<$S> minArg,
                                                                                                 final x10.array.Array<$T> maxArg){
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47359 =
              ((x10.array.Array<$S>)minArg).
                size;
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47379 =
              ((int) t47359) ==
            ((int) 1);
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47379) {
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final $S t47360 =
                  (($S)(((x10.array.Array<$S>)minArg).$apply$G((int)(0))));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final $T t47361 =
                  (($T)(((x10.array.Array<$T>)maxArg).$apply$G((int)(0))));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.RectRegion1D t47362 =
                  ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(x10.rtt.Types.asint(t47360,$S),
                                                                                                        x10.rtt.Types.asint(t47361,$T))));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region __desugarer__var__31__47301 =
                  ((x10.array.Region)(((x10.array.Region)
                                        t47362)));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
x10.array.Region ret47302 =
                   null;
                
//#line 130 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47536 =
                  __desugarer__var__31__47301.
                    rect;
                
//#line 130 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47537 =
                  ((boolean) t47536) ==
                ((boolean) true);
                
//#line 130 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47537) {
                    
//#line 130 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47538 =
                      __desugarer__var__31__47301.
                        rank;
                    
//#line 130 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47539 =
                      ((x10.array.Array<$S>)minArg).
                        size;
                    
//#line 130 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47537 = ((int) t47538) ==
                    ((int) t47539);
                }
                
//#line 130 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47540 =
                  t47537;
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47541 =
                  !(t47540);
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47541) {
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47542 =
                      true;
                    
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47542) {
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.FailedDynamicCheckException t47543 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rect==true, self.rank==minArg.size}");
                        
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
throw t47543;
                    }
                }
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
ret47302 = ((x10.array.Region)(__desugarer__var__31__47301));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47371 =
                  ((x10.array.Region)(ret47302));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47371;
            } else {
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47373 =
                  ((x10.array.Array<$S>)minArg).
                    size;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,$S> t47374 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.Region.$Closure$78<$S, $T>($S, $T, minArg, (x10.array.Region.$Closure$78.__0$1x10$array$Region$$Closure$78$$S$2) null)));
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Array<x10.core.Int> minArray =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t47373)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t47374)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47376 =
                  ((x10.array.Array<$T>)maxArg).
                    size;
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,$T> t47377 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.Region.$Closure$79<$S, $T>($S, $T, maxArg, (x10.array.Region.$Closure$79.__0$1x10$array$Region$$Closure$79$$T$2) null)));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Array<x10.core.Int> maxArray =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t47376)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t47377)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.RectRegion t47378 =
                  ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(minArray)),
                                                                                                    ((x10.array.Array)(maxArray)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47378;
            }
        }
        
        
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeRectangular(
                                                                                                 final int min,
                                                                                                 final int max){
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.RectRegion1D t47380 =
              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(((int)(min)),
                                                                                                    ((int)(max)))));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47380;
        }
        
        
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 make(
                                                                                                 final int min,
                                                                                                 final int max){
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.RectRegion1D t47381 =
              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(((int)(min)),
                                                                                                    ((int)(max)))));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47381;
        }
        
        
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static <$T>x10.array.Region
                                                                                                 make__0$1x10$array$Region$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.Array<$T> regions){
            
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
x10.array.Region r =
              ((x10.array.Region)(((x10.array.Array<$T>)regions).$apply$G((int)(0))));
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
int i47550 =
              1;
            
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
for (;
                                                                                                        true;
                                                                                                        ) {
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47551 =
                  i47550;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47552 =
                  ((x10.array.Array<$T>)regions).
                    size;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47553 =
                  ((t47551) < (((int)(t47552))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (!(t47553)) {
                    
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
break;
                }
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47544 =
                  ((x10.array.Region)(r));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47545 =
                  i47550;
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final $T t47546 =
                  (($T)(((x10.array.Array<$T>)regions).$apply$G((int)(t47545))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47547 =
                  ((x10.array.Region)(t47544.product(((x10.array.Region)(t47546)))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
r = ((x10.array.Region)(t47547));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47548 =
                  i47550;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47549 =
                  ((t47548) + (((int)(1))));
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
i47550 = t47549;
            }
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47392 =
              ((x10.array.Region)(r));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region __desugarer__var__32__47304 =
              ((x10.array.Region)(((x10.array.Region)
                                    t47392)));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
x10.array.Region ret47305 =
               null;
            
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47554 =
              __desugarer__var__32__47304.
                rect;
            
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47555 =
              ((boolean) t47554) ==
            ((boolean) true);
            
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47555) {
                
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47556 =
                  __desugarer__var__32__47304.
                    rank;
                
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47557 =
                  ((x10.array.Array<$T>)regions).
                    size;
                
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47555 = ((int) t47556) ==
                ((int) t47557);
            }
            
//#line 161 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47558 =
              t47555;
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47559 =
              !(t47558);
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47559) {
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47560 =
                  true;
                
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47560) {
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.FailedDynamicCheckException t47561 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rect==true, self.rank==regions.size}");
                    
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
throw t47561;
                }
            }
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
ret47305 = ((x10.array.Region)(__desugarer__var__32__47304));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47401 =
              ((x10.array.Region)(ret47305));
            
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47401;
        }
        
        
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeBanded(
                                                                                                 final int size,
                                                                                                 final int upper,
                                                                                                 final int lower){
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47402 =
              ((x10.array.Region)(x10.array.PolyRegion.makeBanded((int)(size),
                                                                  (int)(upper),
                                                                  (int)(lower))));
            
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47402;
        }
        
        
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeBanded(
                                                                                                 final int size){
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47403 =
              ((x10.array.Region)(x10.array.PolyRegion.makeBanded((int)(size),
                                                                  (int)(1),
                                                                  (int)(1))));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47403;
        }
        
        
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeUpperTriangular(
                                                                                                 final int size){
            
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47404 =
              ((x10.array.Region)(x10.array.Region.makeUpperTriangular((int)(0),
                                                                       (int)(0),
                                                                       (int)(size))));
            
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47404;
        }
        
        
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeUpperTriangular(
                                                                                                 final int rowMin,
                                                                                                 final int colMin,
                                                                                                 final int size){
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47405 =
              ((x10.array.Region)(x10.array.PolyRegion.makeUpperTriangular2((int)(rowMin),
                                                                            (int)(colMin),
                                                                            (int)(size))));
            
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47405;
        }
        
        
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeLowerTriangular(
                                                                                                 final int size){
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47406 =
              ((x10.array.Region)(x10.array.Region.makeLowerTriangular((int)(0),
                                                                       (int)(0),
                                                                       (int)(size))));
            
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47406;
        }
        
        
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 makeLowerTriangular(
                                                                                                 final int rowMin,
                                                                                                 final int colMin,
                                                                                                 final int size){
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47407 =
              ((x10.array.Region)(x10.array.PolyRegion.makeLowerTriangular2((int)(rowMin),
                                                                            (int)(colMin),
                                                                            (int)(size))));
            
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47407;
        }
        
        
//#line 219 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public int
                                                                                                 size$O(
                                                                                                 );
        
        
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public boolean
                                                                                                 isConvex$O(
                                                                                                 );
        
        
//#line 229 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public boolean
                                                                                                 isEmpty$O(
                                                                                                 );
        
        
//#line 244 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public int
                                                                                                 indexOf$O(
                                                                                                 final x10.array.Point id$71);
        
        
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public int
                                                                                                 indexOf$O(
                                                                                                 final int i0){
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47408 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0))));
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47409 =
              this.indexOf$O(((x10.array.Point)(t47408)));
            
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47409;
        }
        
        
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public int
                                                                                                 indexOf$O(
                                                                                                 final int i0,
                                                                                                 final int i1){
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47410 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1))));
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47411 =
              this.indexOf$O(((x10.array.Point)(t47410)));
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47411;
        }
        
        
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public int
                                                                                                 indexOf$O(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2){
            
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47412 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2))));
            
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47413 =
              this.indexOf$O(((x10.array.Point)(t47412)));
            
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47413;
        }
        
        
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public int
                                                                                                 indexOf$O(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2,
                                                                                                 final int i3){
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47414 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2),
                                                      (int)(i3))));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47415 =
              this.indexOf$O(((x10.array.Point)(t47414)));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47415;
        }
        
        
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Region
                                                                                                 boundingBox(
                                                                                                 ){
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47416 =
              ((x10.array.Region)(this.computeBoundingBox()));
            
//#line 260 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47416;
        }
        
        
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.array.Region
                                                                                                 computeBoundingBox(
                                                                                                 );
        
        
//#line 269 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                 min(
                                                                                                 );
        
        
//#line 275 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                 max(
                                                                                                 );
        
        
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public int
                                                                                                 min$O(
                                                                                                 final int i){
            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47417 =
              this.min();
            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47418 =
              x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)t47417).$apply(x10.core.Int.$box(i),x10.rtt.Types.INT));
            
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47418;
        }
        
        
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public int
                                                                                                 max$O(
                                                                                                 final int i){
            
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47419 =
              this.max();
            
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47420 =
              x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)t47419).$apply(x10.core.Int.$box(i),x10.rtt.Types.INT));
            
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47420;
        }
        
        
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Point
                                                                                                 minPoint(
                                                                                                 ){
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47421 =
              rank;
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47422 =
              this.min();
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47423 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t47421),
                                                                                       ((x10.core.fun.Fun_0_1)(t47422)))));
            
//#line 292 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47423;
        }
        
        
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Point
                                                                                                 maxPoint(
                                                                                                 ){
            
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47424 =
              rank;
            
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47425 =
              this.max();
            
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47426 =
              ((x10.array.Point)(x10.array.Point.make__1$1x10$lang$Int$3x10$lang$Int$2((int)(t47424),
                                                                                       ((x10.core.fun.Fun_0_1)(t47425)))));
            
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47426;
        }
        
        
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.array.Region
                                                                                                 intersection(
                                                                                                 final x10.array.Region that);
        
        
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean
                                                                                                 disjoint$O(
                                                                                                 final x10.array.Region that){
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47427 =
              ((x10.array.Region)(this.intersection(((x10.array.Region)(that)))));
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47428 =
              t47427.isEmpty$O();
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47428;
        }
        
        
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.array.Region
                                                                                                 product(
                                                                                                 final x10.array.Region that);
        
        
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.array.Region
                                                                                                 translate(
                                                                                                 final x10.array.Point v);
        
        
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.array.Region
                                                                                                 projection(
                                                                                                 final int axis);
        
        
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.array.Region
                                                                                                 eliminate(
                                                                                                 final int axis);
        
        
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public x10.lang.Iterator<x10.array.Point>
                                                                                                 iterator(
                                                                                                 );
        
        
//#line 405 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static <$T>x10.array.Region
                                                                                                 $implicit_convert__0$1x10$array$Region$$T$2(
                                                                                                 final x10.rtt.Type $T,
                                                                                                 final x10.array.Array<$T> a){
            
//#line 405 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47429 =
              ((x10.array.Region)(x10.array.Region.<$T>make__0$1x10$array$Region$$T$2($T, ((x10.array.Array)(a)))));
            
//#line 405 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47429;
        }
        
        
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 $implicit_convert__0$1x10$lang$IntRange$2(
                                                                                                 final x10.array.Array<x10.lang.IntRange> a){
            
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47430 =
              ((x10.array.Array<x10.lang.IntRange>)a).
                size;
            
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47454 =
              ((int) t47430) ==
            ((int) 1);
            
//#line 415 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47454) {
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.IntRange t47431 =
                  ((x10.array.Array<x10.lang.IntRange>)a).$apply$G((int)(0));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47433 =
                  t47431.
                    min;
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.IntRange t47432 =
                  ((x10.array.Array<x10.lang.IntRange>)a).$apply$G((int)(0));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47434 =
                  t47432.
                    max;
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.RectRegion1D t47435 =
                  ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(t47433,
                                                                                                        t47434)));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region __desugarer__var__33__47307 =
                  ((x10.array.Region)(((x10.array.Region)
                                        t47435)));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
x10.array.Region ret47308 =
                   null;
                
//#line 416 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47562 =
                  __desugarer__var__33__47307.
                    rect;
                
//#line 416 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47563 =
                  ((boolean) t47562) ==
                ((boolean) true);
                
//#line 416 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47563) {
                    
//#line 416 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47564 =
                      __desugarer__var__33__47307.
                        rank;
                    
//#line 416 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47565 =
                      ((x10.array.Array<x10.lang.IntRange>)a).
                        size;
                    
//#line 416 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47563 = ((int) t47564) ==
                    ((int) t47565);
                }
                
//#line 416 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47566 =
                  t47563;
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47567 =
                  !(t47566);
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47567) {
                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47568 =
                      true;
                    
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47568) {
                        
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.FailedDynamicCheckException t47569 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rect==true, self.rank==a.size}");
                        
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
throw t47569;
                    }
                }
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
ret47308 = ((x10.array.Region)(__desugarer__var__33__47307));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47444 =
                  ((x10.array.Region)(ret47308));
                
//#line 416 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47444;
            } else {
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47447 =
                  ((x10.array.Array<x10.lang.IntRange>)a).
                    size;
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47448 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.Region.$Closure$80(a, (x10.array.Region.$Closure$80.__0$1x10$lang$IntRange$2) null)));
                
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Array<x10.core.Int> mins =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t47447)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t47448)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47451 =
                  ((x10.array.Array<x10.lang.IntRange>)a).
                    size;
                
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t47452 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.Region.$Closure$81(a, (x10.array.Region.$Closure$81.__0$1x10$lang$IntRange$2) null)));
                
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Array<x10.core.Int> maxs =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t47451)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t47452)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.RectRegion t47453 =
                  ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(mins)),
                                                                                                    ((x10.array.Array)(maxs)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47453;
            }
        }
        
        
//#line 424 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public static x10.array.Region
                                                                                                 $implicit_convert(
                                                                                                 final x10.lang.IntRange r){
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47455 =
              r.
                min;
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47456 =
              r.
                max;
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.RectRegion1D t47457 =
              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(((int)(t47455)),
                                                                                                    ((int)(t47456)))));
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region __desugarer__var__34__47310 =
              ((x10.array.Region)(((x10.array.Region)
                                    t47457)));
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
x10.array.Region ret47311 =
               null;
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47570 =
              __desugarer__var__34__47310.
                zeroBased;
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47571 =
              r.
                zeroBased;
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47572 =
              ((boolean) t47570) ==
            ((boolean) t47571);
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47572) {
                
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47573 =
                  __desugarer__var__34__47310.
                    rect;
                
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47572 = ((boolean) t47573) ==
                ((boolean) true);
            }
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47574 =
              t47572;
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47574) {
                
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47575 =
                  __desugarer__var__34__47310.
                    rank;
                
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47574 = ((int) t47575) ==
                ((int) 1);
            }
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47576 =
              t47574;
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47576) {
                
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47576 = ((__desugarer__var__34__47310) != (null));
            }
            
//#line 425 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47577 =
              t47576;
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47578 =
              !(t47577);
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47578) {
                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47579 =
                  true;
                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47579) {
                    
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.FailedDynamicCheckException t47580 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.zeroBased==r.zeroBased, self.rect==true, self.rank==1, self!=null}");
                    
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
throw t47580;
                }
            }
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
ret47311 = ((x10.array.Region)(__desugarer__var__34__47310));
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47469 =
              ((x10.array.Region)(ret47311));
            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47469;
        }
        
        
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Region
                                                                                                 $and(
                                                                                                 final x10.array.Region that){
            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47470 =
              ((x10.array.Region)(this.intersection(((x10.array.Region)(that)))));
            
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47470;
        }
        
        
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Region
                                                                                                 $times(
                                                                                                 final x10.array.Region that){
            
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47471 =
              ((x10.array.Region)(this.product(((x10.array.Region)(that)))));
            
//#line 437 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47471;
        }
        
        
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Region
                                                                                                 $plus(
                                                                                                 final x10.array.Point v){
            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47472 =
              ((x10.array.Region)(this.translate(((x10.array.Point)(v)))));
            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47472;
        }
        
        
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Region
                                                                                                 $inv_plus(
                                                                                                 final x10.array.Point v){
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47473 =
              ((x10.array.Region)(this.translate(((x10.array.Point)(v)))));
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47473;
        }
        
        
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Region
                                                                                                 $minus(
                                                                                                 final x10.array.Point v){
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47474 =
              ((x10.array.Point)(v.$minus()));
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t47475 =
              ((x10.array.Region)(this.translate(((x10.array.Point)(t47474)))));
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47475;
        }
        
        
//#line 448 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean
                                                                                                 equals(
                                                                                                 final java.lang.Object that){
            
//#line 449 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47476 =
              x10.rtt.Equality.equalsequals((this),(that));
            
//#line 449 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47476) {
                
//#line 449 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return true;
            }
            
//#line 450 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47477 =
              x10.array.Region.$RTT.isInstance(that);
            
//#line 450 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47478 =
              !(t47477);
            
//#line 450 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47478) {
                
//#line 450 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return false;
            }
            
//#line 451 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t1 =
              ((x10.array.Region)(x10.rtt.Types.<x10.array.Region> cast(that,x10.array.Region.$RTT)));
            
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47479 =
              rank;
            
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47480 =
              t1.
                rank;
            
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47481 =
              ((int) t47479) !=
            ((int) t47480);
            
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47481) {
                
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return false;
            }
            
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region __desugarer__var__35__47313 =
              ((x10.array.Region)(((x10.array.Region)
                                    t1)));
            
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
x10.array.Region ret47314 =
               null;
            
//#line 453 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47581 =
              __desugarer__var__35__47313.
                rank;
            
//#line 453 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47582 =
              x10.array.Region.this.
                rank;
            
//#line 453 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47583 =
              ((int) t47581) ==
            ((int) t47582);
            
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47584 =
              !(t47583);
            
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47584) {
                
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47585 =
                  true;
                
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47585) {
                    
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.FailedDynamicCheckException t47586 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.Region).rank}");
                    
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
throw t47586;
                }
            }
            
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
ret47314 = ((x10.array.Region)(__desugarer__var__35__47313));
            
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Region t2 =
              ((x10.array.Region)(ret47314));
            
//#line 454 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47488 =
              this.contains$O(((x10.array.Region)(t2)));
            
//#line 454 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47488) {
                
//#line 454 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47488 = t2.contains$O(((x10.array.Region)(this)));
            }
            
//#line 454 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47489 =
              t47488;
            
//#line 454 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47489;
        }
        
        
//#line 457 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public boolean
                                                                                                 contains$O(
                                                                                                 final x10.array.Region that);
        
        
//#line 460 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
abstract public boolean
                                                                                                 contains$O(
                                                                                                 final x10.array.Point p);
        
        
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean
                                                                                                 contains$O(
                                                                                                 final int i){
            
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47491 =
              ((x10.array.Point)(x10.array.Point.make((int)(i))));
            
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47492 =
              this.contains$O(((x10.array.Point)(t47491)));
            
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47492;
        }
        
        
//#line 464 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean
                                                                                                 contains$O(
                                                                                                 final int i0,
                                                                                                 final int i1){
            
//#line 464 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47494 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1))));
            
//#line 464 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47495 =
              this.contains$O(((x10.array.Point)(t47494)));
            
//#line 464 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47495;
        }
        
        
//#line 466 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean
                                                                                                 contains$O(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2){
            
//#line 466 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47497 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2))));
            
//#line 466 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47498 =
              this.contains$O(((x10.array.Point)(t47497)));
            
//#line 466 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47498;
        }
        
        
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public boolean
                                                                                                 contains$O(
                                                                                                 final int i0,
                                                                                                 final int i1,
                                                                                                 final int i2,
                                                                                                 final int i3){
            
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Point t47500 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2),
                                                      (int)(i3))));
            
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47501 =
              this.contains$O(((x10.array.Point)(t47500)));
            
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47501;
        }
        
        
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"

        // constructor for non-virtual call
        final public x10.array.Region x10$array$Region$$init$S(final int r,
                                                               final boolean t,
                                                               final boolean z) { {
                                                                                         
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"

                                                                                         
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47502 =
                                                                                           ((int) r) ==
                                                                                         ((int) 1);
                                                                                         
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47502) {
                                                                                             
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47502 = t;
                                                                                         }
                                                                                         
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
boolean t47503 =
                                                                                           t47502;
                                                                                         
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47503) {
                                                                                             
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47503 = z;
                                                                                         }
                                                                                         
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean isRail =
                                                                                           t47503;
                                                                                         
//#line 472 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
this.rank = r;
                                                                                         this.rect = t;
                                                                                         this.zeroBased = z;
                                                                                         this.rail = isRail;
                                                                                         
                                                                                     }
                                                                                     return this;
                                                                                     }
        
        // constructor
        public x10.array.Region $init(final int r,
                                      final boolean t,
                                      final boolean z){return x10$array$Region$$init$S(r,t,z);}
        
        
        
//#line 479 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"

        // constructor for non-virtual call
        final public x10.array.Region x10$array$Region$$init$S(final int r) { {
                                                                                     
//#line 479 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"

                                                                                     
//#line 480 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
this.rank = r;
                                                                                     this.rect = true;
                                                                                     this.zeroBased = true;
                                                                                     this.rail = true;
                                                                                     
                                                                                 }
                                                                                 return this;
                                                                                 }
        
        // constructor
        public x10.array.Region $init(final int r){return x10$array$Region$$init$S(r);}
        
        
        
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
public x10.array.Dist
                                                                                                 $arrow(
                                                                                                 final x10.lang.Place p){
            
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.array.Dist t47504 =
              ((x10.array.Dist)(x10.array.Dist.makeConstant(((x10.array.Region)(this)),
                                                            ((x10.lang.Place)(p)))));
            
//#line 489 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47504;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final public x10.array.Region
                                                                                                x10$array$Region$$x10$array$Region$this(
                                                                                                ){
            
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return x10.array.Region.this;
        }
        
        @x10.core.X10Generated public static class $Closure$76 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$76.class);
            
            public static final x10.rtt.RuntimeType<$Closure$76> $RTT = x10.rtt.StaticFunType.<$Closure$76> make(
            /* base class */$Closure$76.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$76 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$76.class + " calling"); } 
                $_obj.i47528 = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$76 $_obj = new $Closure$76((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.i47528);
                
            }
            
            // constructor just for allocation
            public $Closure$76(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int j47509){
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47510 =
                      ((int) this.
                               i47528) ==
                    ((int) j47509);
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
int t47511 =
                       0;
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47510) {
                        
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47511 = -1;
                    } else {
                        
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47511 = 0;
                    }
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47512 =
                      t47511;
                    
//#line 110 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47512;
                }
                
                public int i47528;
                
                public $Closure$76(final int i47528) { {
                                                              this.i47528 = i47528;
                                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$77 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$77.class);
            
            public static final x10.rtt.RuntimeType<$Closure$77> $RTT = x10.rtt.StaticFunType.<$Closure$77> make(
            /* base class */$Closure$77.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$77 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$77.class + " calling"); } 
                $_obj.i47528 = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$77 $_obj = new $Closure$77((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.i47528);
                
            }
            
            // constructor just for allocation
            public $Closure$77(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int j47520){
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final boolean t47521 =
                      ((int) this.
                               i47528) ==
                    ((int) j47520);
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
int t47522 =
                       0;
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
if (t47521) {
                        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47522 = 1;
                    } else {
                        
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
t47522 = 0;
                    }
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47523 =
                      t47522;
                    
//#line 115 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47523;
                }
                
                public int i47528;
                
                public $Closure$77(final int i47528) { {
                                                              this.i47528 = i47528;
                                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$78<$S, $T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$78.class);
            
            public static final x10.rtt.RuntimeType<$Closure$78> $RTT = x10.rtt.StaticFunType.<$Closure$78> make(
            /* base class */$Closure$78.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(0)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $S;if (i ==1)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$78 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$78.class + " calling"); } 
                $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array minArg = (x10.array.Array) $deserializer.readRef();
                $_obj.minArg = minArg;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$78 $_obj = new $Closure$78((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (minArg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.minArg);
                } else {
                $serializer.write(this.minArg);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$78(final java.lang.System[] $dummy, final x10.rtt.Type $S, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.Region.$Closure$78.$initParams(this, $S, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G(x10.core.Int.$unbox(a1));
            }
            
                private x10.rtt.Type $S;
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$78 $this, final x10.rtt.Type $S, final x10.rtt.Type $T) {
                $this.$S = $S;
                $this.$T = $T;
                }
                
                
                public $S
                  $apply$G(
                  final int i){
                    
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final $S t47372 =
                      (($S)(((x10.array.Array<$S>)this.
                                                    minArg).$apply$G((int)(i))));
                    
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47372;
                }
                
                public x10.array.Array<$S> minArg;
                
                public $Closure$78(final x10.rtt.Type $S,
                                   final x10.rtt.Type $T,
                                   final x10.array.Array<$S> minArg, __0$1x10$array$Region$$Closure$78$$S$2 $dummy) {x10.array.Region.$Closure$78.$initParams(this, $S, $T);
                                                                                                                          {
                                                                                                                             this.minArg = ((x10.array.Array)(minArg));
                                                                                                                         }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$Region$$Closure$78$$S$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$79<$S, $T> extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$79.class);
            
            public static final x10.rtt.RuntimeType<$Closure$79> $RTT = x10.rtt.StaticFunType.<$Closure$79> make(
            /* base class */$Closure$79.class, 
            /* variances */ x10.rtt.RuntimeType.INVARIANTS(2)
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.UnresolvedType.PARAM(1)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            public x10.rtt.Type<?> $getParam(int i) {if (i ==0)return $S;if (i ==1)return $T;return null;}
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$79 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$79.class + " calling"); } 
                $_obj.$S = ( x10.rtt.Type ) $deserializer.readRef();
                $_obj.$T = ( x10.rtt.Type ) $deserializer.readRef();
                x10.array.Array maxArg = (x10.array.Array) $deserializer.readRef();
                $_obj.maxArg = maxArg;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$79 $_obj = new $Closure$79((java.lang.System[]) null, (x10.rtt.Type) null, (x10.rtt.Type) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$S);
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.$T);
                if (maxArg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.maxArg);
                } else {
                $serializer.write(this.maxArg);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$79(final java.lang.System[] $dummy, final x10.rtt.Type $S, final x10.rtt.Type $T) { 
            super($dummy);
            x10.array.Region.$Closure$79.$initParams(this, $S, $T);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply$G(x10.core.Int.$unbox(a1));
            }
            
                private x10.rtt.Type $S;
                private x10.rtt.Type $T;
                // initializer of type parameters
                public static void $initParams(final $Closure$79 $this, final x10.rtt.Type $S, final x10.rtt.Type $T) {
                $this.$S = $S;
                $this.$T = $T;
                }
                
                
                public $T
                  $apply$G(
                  final int i){
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final $T t47375 =
                      (($T)(((x10.array.Array<$T>)this.
                                                    maxArg).$apply$G((int)(i))));
                    
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47375;
                }
                
                public x10.array.Array<$T> maxArg;
                
                public $Closure$79(final x10.rtt.Type $S,
                                   final x10.rtt.Type $T,
                                   final x10.array.Array<$T> maxArg, __0$1x10$array$Region$$Closure$79$$T$2 $dummy) {x10.array.Region.$Closure$79.$initParams(this, $S, $T);
                                                                                                                          {
                                                                                                                             this.maxArg = ((x10.array.Array)(maxArg));
                                                                                                                         }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$array$Region$$Closure$79$$T$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$80 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$80.class);
            
            public static final x10.rtt.RuntimeType<$Closure$80> $RTT = x10.rtt.StaticFunType.<$Closure$80> make(
            /* base class */$Closure$80.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$80 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$80.class + " calling"); } 
                x10.array.Array a = (x10.array.Array) $deserializer.readRef();
                $_obj.a = a;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$80 $_obj = new $Closure$80((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (a instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.a);
                } else {
                $serializer.write(this.a);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$80(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.IntRange t47445 =
                      ((x10.array.Array<x10.lang.IntRange>)this.
                                                             a).$apply$G((int)(i));
                    
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47446 =
                      t47445.
                        min;
                    
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47446;
                }
                
                public x10.array.Array<x10.lang.IntRange> a;
                
                public $Closure$80(final x10.array.Array<x10.lang.IntRange> a, __0$1x10$lang$IntRange$2 $dummy) { {
                                                                                                                         this.a = ((x10.array.Array)(a));
                                                                                                                     }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$IntRange$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$81 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$81.class);
            
            public static final x10.rtt.RuntimeType<$Closure$81> $RTT = x10.rtt.StaticFunType.<$Closure$81> make(
            /* base class */$Closure$81.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$81 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$81.class + " calling"); } 
                x10.array.Array a = (x10.array.Array) $deserializer.readRef();
                $_obj.a = a;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$81 $_obj = new $Closure$81((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (a instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.a);
                } else {
                $serializer.write(this.a);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$81(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final x10.lang.IntRange t47449 =
                      ((x10.array.Array<x10.lang.IntRange>)this.
                                                             a).$apply$G((int)(i));
                    
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
final int t47450 =
                      t47449.
                        max;
                    
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/Region.x10"
return t47450;
                }
                
                public x10.array.Array<x10.lang.IntRange> a;
                
                public $Closure$81(final x10.array.Array<x10.lang.IntRange> a, __0$1x10$lang$IntRange$2 $dummy) { {
                                                                                                                         this.a = ((x10.array.Array)(a));
                                                                                                                     }}
                // synthetic type for parameter mangling
                public abstract static class __0$1x10$lang$IntRange$2 {}
                
            }
            
        
        }
        
        
        