package x10.array;


@x10.core.X10Generated final public class RectRegion extends x10.array.Region implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RectRegion.class);
    
    public static final x10.rtt.RuntimeType<RectRegion> $RTT = x10.rtt.NamedType.<RectRegion> make(
    "x10.array.RectRegion", /* base class */RectRegion.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(RectRegion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RectRegion.class + " calling"); } 
        x10.array.Region.$_deserialize_body($_obj, $deserializer);
        $_obj.size = $deserializer.readInt();
        x10.array.Array mins = (x10.array.Array) $deserializer.readRef();
        $_obj.mins = mins;
        x10.array.Array maxs = (x10.array.Array) $deserializer.readRef();
        $_obj.maxs = maxs;
        $_obj.min0 = $deserializer.readInt();
        $_obj.min1 = $deserializer.readInt();
        $_obj.min2 = $deserializer.readInt();
        $_obj.min3 = $deserializer.readInt();
        $_obj.max0 = $deserializer.readInt();
        $_obj.max1 = $deserializer.readInt();
        $_obj.max2 = $deserializer.readInt();
        $_obj.max3 = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        RectRegion $_obj = new RectRegion((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.size);
        if (mins instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.mins);
        } else {
        $serializer.write(this.mins);
        }
        if (maxs instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.maxs);
        } else {
        $serializer.write(this.maxs);
        }
        $serializer.write(this.min0);
        $serializer.write(this.min1);
        $serializer.write(this.min2);
        $serializer.write(this.min3);
        $serializer.write(this.max0);
        $serializer.write(this.max1);
        $serializer.write(this.max2);
        $serializer.write(this.max3);
        
    }
    
    // constructor just for allocation
    public RectRegion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int size;
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Array<x10.core.Int> mins;
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Array<x10.core.Int> maxs;
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int min0;
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int min1;
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int min2;
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int min3;
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int max0;
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int max1;
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int max2;
        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int max3;
        
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public transient x10.array.Region polyRep;
        
        
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
private static boolean
                                                                                                    allZeros__0$1x10$lang$Int$2$O(
                                                                                                    final x10.array.Array<x10.core.Int> a){
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46509 =
              ((x10.array.Region)(((x10.array.Array<x10.core.Int>)a).
                                    region));
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.Iterator<x10.array.Point> id46510 =
              t46509.iterator();
            
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                           true;
                                                                                                           ) {
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46511 =
                  ((x10.lang.Iterator<x10.array.Point>)id46510).hasNext$O();
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46511)) {
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                }
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Point id46505 =
                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)id46510).next$G()));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int i46506 =
                  id46505.$apply$O((int)(0));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46507 =
                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)a).$apply$G((int)(i46506)));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46508 =
                  ((int) t46507) !=
                ((int) 0);
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46508) {
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                }
            }
            
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return true;
        }
        
        public static boolean
          allZeros$P__0$1x10$lang$Int$2$O(
          final x10.array.Array<x10.core.Int> a){
            return x10.array.RectRegion.allZeros__0$1x10$lang$Int$2$O(((x10.array.Array)(a)));
        }
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
// creation method for java code (1-phase java constructor)
        public RectRegion(final x10.array.Array<x10.core.Int> minArg,
                          final x10.array.Array<x10.core.Int> maxArg, __0$1x10$lang$Int$2__1$1x10$lang$Int$2 $dummy){this((java.lang.System[]) null);
                                                                                                                         $init(minArg,maxArg, (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null);}
        
        // constructor for non-virtual call
        final public x10.array.RectRegion x10$array$RectRegion$$init$S(final x10.array.Array<x10.core.Int> minArg,
                                                                       final x10.array.Array<x10.core.Int> maxArg, __0$1x10$lang$Int$2__1$1x10$lang$Int$2 $dummy) { {
                                                                                                                                                                           
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46530 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46531 =
                                                                                                                                                                             x10.array.RectRegion.allZeros__0$1x10$lang$Int$2$O(((x10.array.Array)(minArg)));
                                                                                                                                                                           
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
super.$init(((int)(t46530)),
                                                                                                                                                                                                                                                                                 ((boolean)(true)),
                                                                                                                                                                                                                                                                                 t46531);
                                                                                                                                                                           
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"

                                                                                                                                                                           
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.__fieldInitializers44394();
                                                                                                                                                                           
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45822 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45823 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)maxArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45825 =
                                                                                                                                                                             ((int) t45822) !=
                                                                                                                                                                           ((int) t45823);
                                                                                                                                                                           
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45825) {
                                                                                                                                                                               
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.IllegalArgumentException t45824 =
                                                                                                                                                                                 ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(((java.lang.String)("size of min and max args are not equal")))));
                                                                                                                                                                               
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t45824;
                                                                                                                                                                           }
                                                                                                                                                                           
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
long s =
                                                                                                                                                                             ((long)(((int)(1))));
                                                                                                                                                                           
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int i46532 =
                                                                                                                                                                             0;
                                                                                                                                                                           
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                                                                                                                                                                                          true;
                                                                                                                                                                                                                                                                          ) {
                                                                                                                                                                               
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46533 =
                                                                                                                                                                                 i46532;
                                                                                                                                                                               
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46534 =
                                                                                                                                                                                 ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                                   size;
                                                                                                                                                                               
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46535 =
                                                                                                                                                                                 ((t46533) < (((int)(t46534))));
                                                                                                                                                                               
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46535)) {
                                                                                                                                                                                   
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                                                                                                                                                                               }
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46512 =
                                                                                                                                                                                 i46532;
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46513 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)maxArg).$apply$G((int)(t46512)));
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46514 =
                                                                                                                                                                                 ((long)(((int)(t46513))));
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46515 =
                                                                                                                                                                                 i46532;
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46516 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)minArg).$apply$G((int)(t46515)));
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46517 =
                                                                                                                                                                                 ((long)(((int)(t46516))));
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46518 =
                                                                                                                                                                                 ((t46514) - (((long)(t46517))));
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46519 =
                                                                                                                                                                                 ((long)(((int)(1))));
                                                                                                                                                                               
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
long rs46520 =
                                                                                                                                                                                 ((t46518) + (((long)(t46519))));
                                                                                                                                                                               
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46521 =
                                                                                                                                                                                 rs46520;
                                                                                                                                                                               
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46522 =
                                                                                                                                                                                 ((long)(((int)(0))));
                                                                                                                                                                               
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46523 =
                                                                                                                                                                                 ((t46521) < (((long)(t46522))));
                                                                                                                                                                               
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46523) {
                                                                                                                                                                                   
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46524 =
                                                                                                                                                                                     ((long)(((int)(0))));
                                                                                                                                                                                   
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
rs46520 = t46524;
                                                                                                                                                                               }
                                                                                                                                                                               
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46525 =
                                                                                                                                                                                 s;
                                                                                                                                                                               
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46526 =
                                                                                                                                                                                 rs46520;
                                                                                                                                                                               
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t46527 =
                                                                                                                                                                                 ((t46525) * (((long)(t46526))));
                                                                                                                                                                               
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
s = t46527;
                                                                                                                                                                               
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46528 =
                                                                                                                                                                                 i46532;
                                                                                                                                                                               
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46529 =
                                                                                                                                                                                 ((t46528) + (((int)(1))));
                                                                                                                                                                               
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
i46532 = t46529;
                                                                                                                                                                           }
                                                                                                                                                                           
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t45848 =
                                                                                                                                                                             s;
                                                                                                                                                                           
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45847 =
                                                                                                                                                                             java.lang.Integer.MAX_VALUE;
                                                                                                                                                                           
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t45849 =
                                                                                                                                                                             ((long)(((int)(t45847))));
                                                                                                                                                                           
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45852 =
                                                                                                                                                                             ((t45848) > (((long)(t45849))));
                                                                                                                                                                           
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45852) {
                                                                                                                                                                               
//#line 59 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.size = -1;
                                                                                                                                                                           } else {
                                                                                                                                                                               
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final long t45850 =
                                                                                                                                                                                 s;
                                                                                                                                                                               
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45851 =
                                                                                                                                                                                 ((int)(long)(((long)(t45850))));
                                                                                                                                                                               
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.size = t45851;
                                                                                                                                                                           }
                                                                                                                                                                           
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45853 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45857 =
                                                                                                                                                                             ((t45853) > (((int)(0))));
                                                                                                                                                                           
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45857) {
                                                                                                                                                                               
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45854 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)minArg).$apply$G((int)(0)));
                                                                                                                                                                               
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min0 = t45854;
                                                                                                                                                                               
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45855 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)maxArg).$apply$G((int)(0)));
                                                                                                                                                                               
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.max0 = t45855;
                                                                                                                                                                           } else {
                                                                                                                                                                               
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45856 =
                                                                                                                                                                                 this.max0 = 0;
                                                                                                                                                                               
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min0 = t45856;
                                                                                                                                                                           }
                                                                                                                                                                           
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45858 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45862 =
                                                                                                                                                                             ((t45858) > (((int)(1))));
                                                                                                                                                                           
//#line 71 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45862) {
                                                                                                                                                                               
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45859 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)minArg).$apply$G((int)(1)));
                                                                                                                                                                               
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min1 = t45859;
                                                                                                                                                                               
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45860 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)maxArg).$apply$G((int)(1)));
                                                                                                                                                                               
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.max1 = t45860;
                                                                                                                                                                           } else {
                                                                                                                                                                               
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45861 =
                                                                                                                                                                                 this.max1 = 0;
                                                                                                                                                                               
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min1 = t45861;
                                                                                                                                                                           }
                                                                                                                                                                           
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45863 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45867 =
                                                                                                                                                                             ((t45863) > (((int)(2))));
                                                                                                                                                                           
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45867) {
                                                                                                                                                                               
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45864 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)minArg).$apply$G((int)(2)));
                                                                                                                                                                               
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min2 = t45864;
                                                                                                                                                                               
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45865 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)maxArg).$apply$G((int)(2)));
                                                                                                                                                                               
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.max2 = t45865;
                                                                                                                                                                           } else {
                                                                                                                                                                               
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45866 =
                                                                                                                                                                                 this.max2 = 0;
                                                                                                                                                                               
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min2 = t45866;
                                                                                                                                                                           }
                                                                                                                                                                           
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45868 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45872 =
                                                                                                                                                                             ((t45868) > (((int)(3))));
                                                                                                                                                                           
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45872) {
                                                                                                                                                                               
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45869 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)minArg).$apply$G((int)(3)));
                                                                                                                                                                               
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min3 = t45869;
                                                                                                                                                                               
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45870 =
                                                                                                                                                                                 x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)maxArg).$apply$G((int)(3)));
                                                                                                                                                                               
//#line 87 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.max3 = t45870;
                                                                                                                                                                           } else {
                                                                                                                                                                               
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45871 =
                                                                                                                                                                                 this.max3 = 0;
                                                                                                                                                                               
//#line 89 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min3 = t45871;
                                                                                                                                                                           }
                                                                                                                                                                           
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45873 =
                                                                                                                                                                             ((x10.array.Array<x10.core.Int>)minArg).
                                                                                                                                                                               size;
                                                                                                                                                                           
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45874 =
                                                                                                                                                                             ((t45873) > (((int)(4))));
                                                                                                                                                                           
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45874) {
                                                                                                                                                                               
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.mins = ((x10.array.Array)(minArg));
                                                                                                                                                                               
//#line 94 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.maxs = ((x10.array.Array)(maxArg));
                                                                                                                                                                           } else {
                                                                                                                                                                               
//#line 96 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.mins = null;
                                                                                                                                                                               
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.maxs = null;
                                                                                                                                                                           }
                                                                                                                                                                       }
                                                                                                                                                                       return this;
                                                                                                                                                                       }
        
        // constructor
        public x10.array.RectRegion $init(final x10.array.Array<x10.core.Int> minArg,
                                          final x10.array.Array<x10.core.Int> maxArg, __0$1x10$lang$Int$2__1$1x10$lang$Int$2 $dummy){return x10$array$RectRegion$$init$S(minArg,maxArg, $dummy);}
        
        
        
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
// creation method for java code (1-phase java constructor)
        public RectRegion(final int min,
                          final int max){this((java.lang.System[]) null);
                                             $init(min,max);}
        
        // constructor for non-virtual call
        final public x10.array.RectRegion x10$array$RectRegion$$init$S(final int min,
                                                                       final int max) { {
                                                                                               
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46536 =
                                                                                                 ((int) min) ==
                                                                                               ((int) 0);
                                                                                               
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
super.$init(((int)(1)),
                                                                                                                                                                                                      ((boolean)(true)),
                                                                                                                                                                                                      t46536);
                                                                                               
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"

                                                                                               
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.__fieldInitializers44394();
                                                                                               
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45876 =
                                                                                                 ((max) - (((int)(min))));
                                                                                               
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45877 =
                                                                                                 ((t45876) + (((int)(1))));
                                                                                               
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.size = t45877;
                                                                                               
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min0 = min;
                                                                                               
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.max0 = max;
                                                                                               
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45878 =
                                                                                                 this.min3 = 0;
                                                                                               
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45879 =
                                                                                                 this.min2 = t45878;
                                                                                               
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min1 = t45879;
                                                                                               
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45880 =
                                                                                                 this.max3 = 0;
                                                                                               
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45881 =
                                                                                                 this.max2 = t45880;
                                                                                               
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.max1 = t45881;
                                                                                               
//#line 113 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.mins = null;
                                                                                               
//#line 114 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.maxs = null;
                                                                                           }
                                                                                           return this;
                                                                                           }
        
        // constructor
        public x10.array.RectRegion $init(final int min,
                                          final int max){return x10$array$RectRegion$$init$S(min,max);}
        
        
        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     size$O(
                                                                                                     ){
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45882 =
              size;
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45884 =
              ((t45882) < (((int)(0))));
            
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45884) {
                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.UnboundedRegionException t45883 =
                  ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(((java.lang.String)("size exceeds capacity of int")))));
                
//#line 118 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t45883;
            }
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45885 =
              size;
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45885;
        }
        
        
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     isConvex$O(
                                                                                                     ){
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return true;
        }
        
        
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     isEmpty$O(
                                                                                                     ){
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45886 =
              size;
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45887 =
              ((int) t45886) ==
            ((int) 0);
            
//#line 124 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45887;
        }
        
        
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     indexOf$O(
                                                                                                     final x10.array.Point pt){
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45888 =
              this.contains$O(((x10.array.Point)(pt)));
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45889 =
              !(t45888);
            
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45889) {
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
            }
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45890 =
              pt.$apply$O((int)(0));
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45891 =
              this.min$O((int)(0));
            
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int offset =
              ((t45890) - (((int)(t45891))));
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int i46551 =
              1;
            
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46552 =
                  i46551;
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46553 =
                  rank;
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46554 =
                  ((t46552) < (((int)(t46553))));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46554)) {
                    
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                }
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46537 =
                  i46551;
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int min_i46538 =
                  this.min$O((int)(t46537));
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46539 =
                  i46551;
                
//#line 131 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int max_i46540 =
                  this.max$O((int)(t46539));
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46541 =
                  i46551;
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int pt_i46542 =
                  pt.$apply$O((int)(t46541));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46543 =
                  ((max_i46540) - (((int)(min_i46538))));
                
//#line 133 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int delta_i46544 =
                  ((t46543) + (((int)(1))));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46545 =
                  offset;
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46546 =
                  ((t46545) * (((int)(delta_i46544))));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46547 =
                  ((t46546) + (((int)(pt_i46542))));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46548 =
                  ((t46547) - (((int)(min_i46538))));
                
//#line 134 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t46548;
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46549 =
                  i46551;
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46550 =
                  ((t46549) + (((int)(1))));
                
//#line 129 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
i46551 = t46550;
            }
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45906 =
              offset;
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45906;
        }
        
        
//#line 139 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     indexOf$O(
                                                                                                     final int i0){
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45917 =
              zeroBased;
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45917) {
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45907 =
                  rank;
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t45909 =
                  ((int) t45907) !=
                ((int) 1);
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t45909)) {
                    
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45908 =
                      this.containsInternal$O((int)(i0));
                    
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t45909 = !(t45908);
                }
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45910 =
                  t45909;
                
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45910) {
                    
//#line 141 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return i0;
            } else {
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45911 =
                  rank;
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t45913 =
                  ((int) t45911) !=
                ((int) 1);
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t45913)) {
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45912 =
                      this.containsInternal$O((int)(i0));
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t45913 = !(t45912);
                }
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45914 =
                  t45913;
                
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45914) {
                    
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45915 =
                  min0;
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45916 =
                  ((i0) - (((int)(t45915))));
                
//#line 145 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45916;
            }
        }
        
        
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     indexOf$O(
                                                                                                     final int i0,
                                                                                                     final int i1){
            
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45943 =
              zeroBased;
            
//#line 150 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45943) {
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45918 =
                  rank;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t45920 =
                  ((int) t45918) !=
                ((int) 2);
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t45920)) {
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45919 =
                      this.containsInternal$O((int)(i0),
                                              (int)(i1));
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t45920 = !(t45919);
                }
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45921 =
                  t45920;
                
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45921) {
                    
//#line 151 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int offset =
                  i0;
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45923 =
                  offset;
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45922 =
                  max1;
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45924 =
                  ((t45922) + (((int)(1))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45925 =
                  ((t45923) * (((int)(t45924))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45926 =
                  ((t45925) + (((int)(i1))));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45926;
                
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45927 =
                  offset;
                
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45927;
            } else {
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45928 =
                  rank;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t45930 =
                  ((int) t45928) !=
                ((int) 2);
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t45930)) {
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45929 =
                      this.containsInternal$O((int)(i0),
                                              (int)(i1));
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t45930 = !(t45929);
                }
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45931 =
                  t45930;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45931) {
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45932 =
                  min0;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int offset =
                  ((i0) - (((int)(t45932))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45936 =
                  offset;
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45933 =
                  max1;
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45934 =
                  min1;
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45935 =
                  ((t45933) - (((int)(t45934))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45937 =
                  ((t45935) + (((int)(1))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45938 =
                  ((t45936) * (((int)(t45937))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45939 =
                  ((t45938) + (((int)(i1))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45940 =
                  min1;
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45941 =
                  ((t45939) - (((int)(t45940))));
                
//#line 158 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45941;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45942 =
                  offset;
                
//#line 159 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45942;
            }
        }
        
        
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     indexOf$O(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2){
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45983 =
              zeroBased;
            
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45983) {
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45944 =
                  rank;
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t45946 =
                  ((int) t45944) !=
                ((int) 3);
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t45946)) {
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45945 =
                      this.containsInternal$O((int)(i0),
                                              (int)(i1),
                                              (int)(i2));
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t45946 = !(t45945);
                }
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45947 =
                  t45946;
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45947) {
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int offset =
                  i0;
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45949 =
                  offset;
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45948 =
                  max1;
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45950 =
                  ((t45948) + (((int)(1))));
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45951 =
                  ((t45949) * (((int)(t45950))));
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45952 =
                  ((t45951) + (((int)(i1))));
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45952;
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45954 =
                  offset;
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45953 =
                  max2;
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45955 =
                  ((t45953) + (((int)(1))));
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45956 =
                  ((t45954) * (((int)(t45955))));
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45957 =
                  ((t45956) + (((int)(i2))));
                
//#line 168 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45957;
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45958 =
                  offset;
                
//#line 169 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45958;
            } else {
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45959 =
                  rank;
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t45961 =
                  ((int) t45959) !=
                ((int) 3);
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t45961)) {
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45960 =
                      this.containsInternal$O((int)(i0),
                                              (int)(i1),
                                              (int)(i2));
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t45961 = !(t45960);
                }
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45962 =
                  t45961;
                
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45962) {
                    
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45963 =
                  min0;
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int offset =
                  ((i0) - (((int)(t45963))));
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45967 =
                  offset;
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45964 =
                  max1;
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45965 =
                  min1;
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45966 =
                  ((t45964) - (((int)(t45965))));
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45968 =
                  ((t45966) + (((int)(1))));
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45969 =
                  ((t45967) * (((int)(t45968))));
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45970 =
                  ((t45969) + (((int)(i1))));
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45971 =
                  min1;
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45972 =
                  ((t45970) - (((int)(t45971))));
                
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45972;
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45976 =
                  offset;
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45973 =
                  max2;
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45974 =
                  min2;
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45975 =
                  ((t45973) - (((int)(t45974))));
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45977 =
                  ((t45975) + (((int)(1))));
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45978 =
                  ((t45976) * (((int)(t45977))));
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45979 =
                  ((t45978) + (((int)(i2))));
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45980 =
                  min2;
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45981 =
                  ((t45979) - (((int)(t45980))));
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45981;
                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45982 =
                  offset;
                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t45982;
            }
        }
        
        
//#line 179 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     indexOf$O(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2,
                                                                                                     final int i3){
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46037 =
              zeroBased;
            
//#line 180 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46037) {
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45984 =
                  rank;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t45986 =
                  ((int) t45984) !=
                ((int) 4);
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t45986)) {
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45985 =
                      this.containsInternal$O((int)(i0),
                                              (int)(i1),
                                              (int)(i2),
                                              (int)(i3));
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t45986 = !(t45985);
                }
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t45987 =
                  t45986;
                
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t45987) {
                    
//#line 181 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int offset =
                  i0;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45989 =
                  offset;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45988 =
                  max1;
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45990 =
                  ((t45988) + (((int)(1))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45991 =
                  ((t45989) * (((int)(t45990))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45992 =
                  ((t45991) + (((int)(i1))));
                
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45992;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45994 =
                  offset;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45993 =
                  max2;
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45995 =
                  ((t45993) + (((int)(1))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45996 =
                  ((t45994) * (((int)(t45995))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45997 =
                  ((t45996) + (((int)(i2))));
                
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t45997;
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45999 =
                  offset;
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t45998 =
                  max3;
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46000 =
                  ((t45998) + (((int)(1))));
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46001 =
                  ((t45999) * (((int)(t46000))));
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46002 =
                  ((t46001) + (((int)(i3))));
                
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t46002;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46003 =
                  offset;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46003;
            } else {
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46004 =
                  rank;
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46006 =
                  ((int) t46004) !=
                ((int) 4);
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46006)) {
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46005 =
                      this.containsInternal$O((int)(i0),
                                              (int)(i1),
                                              (int)(i2),
                                              (int)(i3));
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46006 = !(t46005);
                }
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46007 =
                  t46006;
                
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46007) {
                    
//#line 188 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return -1;
                }
                
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46008 =
                  min0;
                
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int offset =
                  ((i0) - (((int)(t46008))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46012 =
                  offset;
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46009 =
                  max1;
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46010 =
                  min1;
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46011 =
                  ((t46009) - (((int)(t46010))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46013 =
                  ((t46011) + (((int)(1))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46014 =
                  ((t46012) * (((int)(t46013))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46015 =
                  ((t46014) + (((int)(i1))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46016 =
                  min1;
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46017 =
                  ((t46015) - (((int)(t46016))));
                
//#line 190 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t46017;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46021 =
                  offset;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46018 =
                  max2;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46019 =
                  min2;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46020 =
                  ((t46018) - (((int)(t46019))));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46022 =
                  ((t46020) + (((int)(1))));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46023 =
                  ((t46021) * (((int)(t46022))));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46024 =
                  ((t46023) + (((int)(i2))));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46025 =
                  min2;
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46026 =
                  ((t46024) - (((int)(t46025))));
                
//#line 191 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t46026;
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46030 =
                  offset;
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46027 =
                  max3;
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46028 =
                  min3;
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46029 =
                  ((t46027) - (((int)(t46028))));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46031 =
                  ((t46029) + (((int)(1))));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46032 =
                  ((t46030) * (((int)(t46031))));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46033 =
                  ((t46032) + (((int)(i3))));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46034 =
                  min3;
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46035 =
                  ((t46033) - (((int)(t46034))));
                
//#line 192 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
offset = t46035;
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46036 =
                  offset;
                
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46036;
            }
        }
        
        
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     min$O(
                                                                                                     final int i){
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46039 =
              ((i) < (((int)(0))));
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46039)) {
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46038 =
                  rank;
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46039 = ((i) >= (((int)(t46038))));
            }
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46044 =
              t46039;
            
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46044) {
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46040 =
                  (("min: ") + ((x10.core.Int.$box(i))));
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46041 =
                  ((t46040) + (" is not a valid rank for "));
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46042 =
                  ((t46041) + (this));
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t46043 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t46042)));
                
//#line 199 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46043;
            }
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
switch (i) {
                
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 0:
                    
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46045 =
                      min0;
                    
//#line 201 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46045;
                
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 1:
                    
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46046 =
                      min1;
                    
//#line 202 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46046;
                
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 2:
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46047 =
                      min2;
                    
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46047;
                
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 3:
                    
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46048 =
                      min3;
                    
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46048;
                
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
default:
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46049 =
                      ((x10.array.Array)(mins));
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46050 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t46049).$apply$G((int)(i)));
                    
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46050;
            }
        }
        
        
//#line 209 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int
                                                                                                     max$O(
                                                                                                     final int i){
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46052 =
              ((i) < (((int)(0))));
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46052)) {
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46051 =
                  rank;
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46052 = ((i) >= (((int)(t46051))));
            }
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46057 =
              t46052;
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46057) {
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46053 =
                  (("max: ") + ((x10.core.Int.$box(i))));
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46054 =
                  ((t46053) + (" is not a valid rank for "));
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46055 =
                  ((t46054) + (this));
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t46056 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t46055)));
                
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46056;
            }
            
//#line 211 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
switch (i) {
                
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 0:
                    
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46058 =
                      max0;
                    
//#line 212 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46058;
                
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 1:
                    
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46059 =
                      max1;
                    
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46059;
                
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 2:
                    
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46060 =
                      max2;
                    
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46060;
                
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 3:
                    
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46061 =
                      max3;
                    
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46061;
                
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
default:
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46062 =
                      ((x10.array.Array)(maxs));
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46063 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t46062).$apply$G((int)(i)));
                    
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46063;
            }
        }
        
        
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Region
                                                                                                     computeBoundingBox(
                                                                                                     ){
            
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return this;
        }
        
        
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                     min(
                                                                                                     ){
            
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46065 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$60(this)));
            
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46065;
        }
        
        
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                     max(
                                                                                                     ){
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46067 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$61(this)));
            
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46067;
        }
        
        
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final x10.array.Region that){
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46095 =
              x10.array.RectRegion.$RTT.isInstance(that);
            
//#line 231 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46095) {
                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46068 =
                  ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                
//#line 232 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin =
                  t46068.min();
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46069 =
                  ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax =
                  t46069.max();
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int i46567 =
                  0;
                
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46568 =
                      i46567;
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46569 =
                      rank;
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46570 =
                      ((t46568) < (((int)(t46569))));
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46570)) {
                        
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                    }
                    
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46555 =
                      i46567;
                    
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46556 =
                      this.min$O((int)(t46555));
                    
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46557 =
                      i46567;
                    
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46558 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thatMin).$apply(x10.core.Int.$box(t46557),x10.rtt.Types.INT));
                    
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46559 =
                      ((t46556) > (((int)(t46558))));
                    
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46559) {
                        
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                    }
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46560 =
                      i46567;
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46561 =
                      this.max$O((int)(t46560));
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46562 =
                      i46567;
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46563 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thatMax).$apply(x10.core.Int.$box(t46562),x10.rtt.Types.INT));
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46564 =
                      ((t46561) < (((int)(t46563))));
                    
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46564) {
                        
//#line 236 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                    }
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46565 =
                      i46567;
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46566 =
                      ((t46565) + (((int)(1))));
                    
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
i46567 = t46566;
                }
                
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return true;
            } else {
                
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46094 =
                  x10.array.RectRegion1D.$RTT.isInstance(that);
                
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46094) {
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46086 =
                      this.min$O((int)(0));
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46087 =
                      that.min$O((int)(0));
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46090 =
                      ((t46086) <= (((int)(t46087))));
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46090) {
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46088 =
                          this.max$O((int)(0));
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46089 =
                          that.max$O((int)(0));
                        
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46090 = ((t46088) >= (((int)(t46089))));
                    }
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46091 =
                      t46090;
                    
//#line 240 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46091;
                } else {
                    
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46092 =
                      ((x10.array.Region)(that.computeBoundingBox()));
                    
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46093 =
                      this.contains$O(((x10.array.Region)(t46092)));
                    
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46093;
                }
            }
        }
        
        
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final x10.array.Point p){
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46096 =
              p.
                rank;
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46097 =
              rank;
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46098 =
              ((int) t46096) !=
            ((int) t46097);
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46098) {
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
            }
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46099 =
              p.
                rank;
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46132 =
              ((t46099) - (((int)(1))));
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
switch (t46132) {
                
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
default:
                    {
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46100 =
                          p.
                            rank;
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46101 =
                          ((t46100) - (((int)(1))));
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.IntRange t46102 =
                          ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(t46101)), ((int)(4)))));
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region p45742 =
                          ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t46102)))));
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int r45743min45744 =
                          p45742.min$O((int)(0));
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int r45743max45745 =
                          p45742.max$O((int)(0));
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int r46582 =
                          r45743min45744;
                        
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46583 =
                              r46582;
                            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46584 =
                              ((t46583) <= (((int)(r45743max45745))));
                            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46584)) {
                                
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                            }
                            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int r46579 =
                              r46582;
                            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46571 =
                              p.$apply$O((int)(r46579));
                            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46572 =
                              ((x10.array.Array)(mins));
                            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46573 =
                              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t46572).$apply$G((int)(r46579)));
                            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46574 =
                              ((t46571) < (((int)(t46573))));
                            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46574)) {
                                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46575 =
                                  p.$apply$O((int)(r46579));
                                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46576 =
                                  ((x10.array.Array)(maxs));
                                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46577 =
                                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t46576).$apply$G((int)(r46579)));
                                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46574 = ((t46575) > (((int)(t46577))));
                            }
                            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46578 =
                              t46574;
                            
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46578) {
                                
//#line 252 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                            }
                            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46580 =
                              r46582;
                            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46581 =
                              ((t46580) + (((int)(1))));
                            
//#line 251 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
r46582 = t46581;
                        }
                    }
                
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 3:
                    {
                        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int tmp =
                          p.$apply$O((int)(3));
                        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46116 =
                          min3;
                        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46118 =
                          ((tmp) < (((int)(t46116))));
                        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46118)) {
                            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46117 =
                              max3;
                            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46118 = ((tmp) > (((int)(t46117))));
                        }
                        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46119 =
                          t46118;
                        
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46119) {
                            
//#line 254 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                        }
                    }
                
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 2:
                    {
                        
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int tmp =
                          p.$apply$O((int)(2));
                        
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46120 =
                          min2;
                        
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46122 =
                          ((tmp) < (((int)(t46120))));
                        
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46122)) {
                            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46121 =
                              max2;
                            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46122 = ((tmp) > (((int)(t46121))));
                        }
                        
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46123 =
                          t46122;
                        
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46123) {
                            
//#line 255 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                        }
                    }
                
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 1:
                    {
                        
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int tmp =
                          p.$apply$O((int)(1));
                        
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46124 =
                          min1;
                        
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46126 =
                          ((tmp) < (((int)(t46124))));
                        
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46126)) {
                            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46125 =
                              max1;
                            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46126 = ((tmp) > (((int)(t46125))));
                        }
                        
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46127 =
                          t46126;
                        
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46127) {
                            
//#line 256 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                        }
                    }
                
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
case 0:
                    {
                        
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int tmp =
                          p.$apply$O((int)(0));
                        
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46128 =
                          min0;
                        
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46130 =
                          ((tmp) < (((int)(t46128))));
                        
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46130)) {
                            
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46129 =
                              max0;
                            
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46130 = ((tmp) > (((int)(t46129))));
                        }
                        
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46131 =
                          t46130;
                        
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46131) {
                            
//#line 257 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                        }
                    }
            }
            
//#line 259 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return true;
        }
        
        
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final int i0){
            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46134 =
              this.containsInternal$O((int)(i0));
            
//#line 262 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46134;
        }
        
        
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final int i0,
                                                                                                     final int i1){
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46136 =
              this.containsInternal$O((int)(i0),
                                      (int)(i1));
            
//#line 263 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46136;
        }
        
        
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2){
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46138 =
              this.containsInternal$O((int)(i0),
                                      (int)(i1),
                                      (int)(i2));
            
//#line 264 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46138;
        }
        
        
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     contains$O(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2,
                                                                                                     final int i3){
            
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46140 =
              this.containsInternal$O((int)(i0),
                                      (int)(i1),
                                      (int)(i2),
                                      (int)(i3));
            
//#line 265 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46140;
        }
        
        
//#line 267 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
private boolean
                                                                                                     containsInternal$O(
                                                                                                     final int i0){
            
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46141 =
              min0;
            
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46143 =
              ((i0) >= (((int)(t46141))));
            
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46143) {
                
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46142 =
                  max0;
                
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46143 = ((i0) <= (((int)(t46142))));
            }
            
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46144 =
              t46143;
            
//#line 268 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46144;
        }
        
        public static boolean
          containsInternal$P$O(
          final int i0,
          final x10.array.RectRegion RectRegion){
            return RectRegion.containsInternal$O((int)(i0));
        }
        
        
//#line 271 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
private boolean
                                                                                                     containsInternal$O(
                                                                                                     final int i0,
                                                                                                     final int i1){
            
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46145 =
              true;
            
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46145) {
                
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46145 = zeroBased;
            }
            
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46162 =
              t46145;
            
//#line 272 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46162) {
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46147 =
                  ((int)(((int)(i0))));
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46146 =
                  max0;
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46148 =
                  ((int)(((int)(t46146))));
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46152 =
                  x10.core.Unsigned.le(t46147, ((int)(t46148)));
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46152) {
                    
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46150 =
                      ((int)(((int)(i1))));
                    
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46149 =
                      max1;
                    
//#line 274 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46151 =
                      ((int)(((int)(t46149))));
                    
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46152 = x10.core.Unsigned.le(t46150, ((int)(t46151)));
                }
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46153 =
                  t46152;
                
//#line 273 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46153;
            } else {
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46154 =
                  min0;
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46156 =
                  ((i0) >= (((int)(t46154))));
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46156) {
                    
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46155 =
                      max0;
                    
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46156 = ((i0) <= (((int)(t46155))));
                }
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46158 =
                  t46156;
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46158) {
                    
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46157 =
                      min1;
                    
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46158 = ((i1) >= (((int)(t46157))));
                }
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46160 =
                  t46158;
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46160) {
                    
//#line 277 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46159 =
                      max1;
                    
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46160 = ((i1) <= (((int)(t46159))));
                }
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46161 =
                  t46160;
                
//#line 276 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46161;
            }
        }
        
        public static boolean
          containsInternal$P$O(
          final int i0,
          final int i1,
          final x10.array.RectRegion RectRegion){
            return RectRegion.containsInternal$O((int)(i0),
                                                 (int)(i1));
        }
        
        
//#line 281 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
private boolean
                                                                                                     containsInternal$O(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2){
            
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46163 =
              true;
            
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46163) {
                
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46163 = zeroBased;
            }
            
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46188 =
              t46163;
            
//#line 282 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46188) {
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46165 =
                  ((int)(((int)(i0))));
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46164 =
                  max0;
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46166 =
                  ((int)(((int)(t46164))));
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46170 =
                  x10.core.Unsigned.le(t46165, ((int)(t46166)));
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46170) {
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46168 =
                      ((int)(((int)(i1))));
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46167 =
                      max1;
                    
//#line 284 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46169 =
                      ((int)(((int)(t46167))));
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46170 = x10.core.Unsigned.le(t46168, ((int)(t46169)));
                }
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46174 =
                  t46170;
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46174) {
                    
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46172 =
                      ((int)(((int)(i2))));
                    
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46171 =
                      max2;
                    
//#line 285 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46173 =
                      ((int)(((int)(t46171))));
                    
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46174 = x10.core.Unsigned.le(t46172, ((int)(t46173)));
                }
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46175 =
                  t46174;
                
//#line 283 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46175;
            } else {
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46176 =
                  min0;
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46178 =
                  ((i0) >= (((int)(t46176))));
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46178) {
                    
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46177 =
                      max0;
                    
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46178 = ((i0) <= (((int)(t46177))));
                }
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46180 =
                  t46178;
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46180) {
                    
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46179 =
                      min1;
                    
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46180 = ((i1) >= (((int)(t46179))));
                }
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46182 =
                  t46180;
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46182) {
                    
//#line 288 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46181 =
                      max1;
                    
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46182 = ((i1) <= (((int)(t46181))));
                }
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46184 =
                  t46182;
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46184) {
                    
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46183 =
                      min2;
                    
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46184 = ((i2) >= (((int)(t46183))));
                }
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46186 =
                  t46184;
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46186) {
                    
//#line 289 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46185 =
                      max2;
                    
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46186 = ((i2) <= (((int)(t46185))));
                }
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46187 =
                  t46186;
                
//#line 287 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46187;
            }
        }
        
        public static boolean
          containsInternal$P$O(
          final int i0,
          final int i1,
          final int i2,
          final x10.array.RectRegion RectRegion){
            return RectRegion.containsInternal$O((int)(i0),
                                                 (int)(i1),
                                                 (int)(i2));
        }
        
        
//#line 293 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
private boolean
                                                                                                     containsInternal$O(
                                                                                                     final int i0,
                                                                                                     final int i1,
                                                                                                     final int i2,
                                                                                                     final int i3){
            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46189 =
              true;
            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46189) {
                
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46189 = zeroBased;
            }
            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46222 =
              t46189;
            
//#line 294 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46222) {
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46191 =
                  ((int)(((int)(i0))));
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46190 =
                  max0;
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46192 =
                  ((int)(((int)(t46190))));
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46196 =
                  x10.core.Unsigned.le(t46191, ((int)(t46192)));
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46196) {
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46194 =
                      ((int)(((int)(i1))));
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46193 =
                      max1;
                    
//#line 296 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46195 =
                      ((int)(((int)(t46193))));
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46196 = x10.core.Unsigned.le(t46194, ((int)(t46195)));
                }
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46200 =
                  t46196;
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46200) {
                    
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46198 =
                      ((int)(((int)(i2))));
                    
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46197 =
                      max2;
                    
//#line 297 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46199 =
                      ((int)(((int)(t46197))));
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46200 = x10.core.Unsigned.le(t46198, ((int)(t46199)));
                }
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46204 =
                  t46200;
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46204) {
                    
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46202 =
                      ((int)(((int)(i3))));
                    
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46201 =
                      max3;
                    
//#line 298 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46203 =
                      ((int)(((int)(t46201))));
                    
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46204 = x10.core.Unsigned.le(t46202, ((int)(t46203)));
                }
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46205 =
                  t46204;
                
//#line 295 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46205;
            } else {
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46206 =
                  min0;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46208 =
                  ((i0) >= (((int)(t46206))));
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46208) {
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46207 =
                      max0;
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46208 = ((i0) <= (((int)(t46207))));
                }
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46210 =
                  t46208;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46210) {
                    
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46209 =
                      min1;
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46210 = ((i1) >= (((int)(t46209))));
                }
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46212 =
                  t46210;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46212) {
                    
//#line 301 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46211 =
                      max1;
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46212 = ((i1) <= (((int)(t46211))));
                }
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46214 =
                  t46212;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46214) {
                    
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46213 =
                      min2;
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46214 = ((i2) >= (((int)(t46213))));
                }
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46216 =
                  t46214;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46216) {
                    
//#line 302 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46215 =
                      max2;
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46216 = ((i2) <= (((int)(t46215))));
                }
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46218 =
                  t46216;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46218) {
                    
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46217 =
                      min3;
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46218 = ((i3) >= (((int)(t46217))));
                }
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46220 =
                  t46218;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46220) {
                    
//#line 303 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46219 =
                      max3;
                    
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46220 = ((i3) <= (((int)(t46219))));
                }
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46221 =
                  t46220;
                
//#line 300 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46221;
            }
        }
        
        public static boolean
          containsInternal$P$O(
          final int i0,
          final int i1,
          final int i2,
          final int i3,
          final x10.array.RectRegion RectRegion){
            return RectRegion.containsInternal$O((int)(i0),
                                                 (int)(i1),
                                                 (int)(i2),
                                                 (int)(i3));
        }
        
        
//#line 312 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Region
                                                                                                     toPolyRegion(
                                                                                                     ){
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46223 =
              ((x10.array.Region)(polyRep));
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46231 =
              ((t46223) == (null));
            
//#line 313 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46231) {
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46224 =
                  rank;
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46225 =
                  this.min();
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46228 =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t46224)),
                                                                                                                           t46225, (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46226 =
                  rank;
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46227 =
                  this.max();
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46229 =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t46226)),
                                                                                                                           t46227, (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46230 =
                  ((x10.array.Region)(x10.array.Region.makeRectangularPoly__0$1x10$lang$Int$2__1$1x10$lang$Int$2(((x10.array.Array)(t46228)),
                                                                                                                 ((x10.array.Array)(t46229)))));
                
//#line 314 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.polyRep = ((x10.array.Region)(t46230));
            }
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46232 =
              ((x10.array.Region)(polyRep));
            
//#line 316 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46232;
        }
        
        
//#line 320 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Region
                                                                                                     intersection(
                                                                                                     final x10.array.Region that){
            
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46299 =
              that.isEmpty$O();
            
//#line 321 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46299) {
                
//#line 322 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return that;
            } else {
                
//#line 323 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46298 =
                  x10.array.FullRegion.$RTT.isInstance(that);
                
//#line 323 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46298) {
                    
//#line 324 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return this;
                } else {
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46297 =
                      x10.array.RectRegion.$RTT.isInstance(that);
                    
//#line 325 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46297) {
                        
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46233 =
                          ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                        
//#line 326 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin =
                          t46233.min();
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46234 =
                          ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                        
//#line 327 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax =
                          t46234.max();
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46238 =
                          rank;
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46239 =
                          ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$62(this,
                                                                                       thatMin, (x10.array.RectRegion.$Closure$62.__1$1x10$lang$Int$3x10$lang$Int$2) null)));
                        
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMin =
                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t46238)),
                                                                                                                                   ((x10.core.fun.Fun_0_1)(t46239)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                        
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46243 =
                          rank;
                        
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46244 =
                          ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$63(this,
                                                                                       thatMax, (x10.array.RectRegion.$Closure$63.__1$1x10$lang$Int$3x10$lang$Int$2) null)));
                        
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMax =
                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t46243)),
                                                                                                                                   ((x10.core.fun.Fun_0_1)(t46244)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46596 =
                          ((x10.array.Array<x10.core.Int>)newMin).
                            size;
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46597 =
                          ((t46596) - (((int)(1))));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.IntRange t46598 =
                          ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(0)), ((int)(t46597)))));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region p46599 =
                          ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t46598)))));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int i45762min46600 =
                          p46599.min$O((int)(0));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int i45762max46601 =
                          p46599.max$O((int)(0));
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int i46593 =
                          i45762min46600;
                        
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46594 =
                              i46593;
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46595 =
                              ((t46594) <= (((int)(i45762max46601))));
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46595)) {
                                
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                            }
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int i46590 =
                              i46593;
                            
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46585 =
                              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)newMax).$apply$G((int)(i46590)));
                            
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46586 =
                              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)newMin).$apply$G((int)(i46590)));
                            
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46587 =
                              ((t46585) < (((int)(t46586))));
                            
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46587) {
                                
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46588 =
                                  rank;
                                
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46589 =
                                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(t46588))));
                                
//#line 331 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46589;
                            }
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46591 =
                              i46593;
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46592 =
                              ((t46591) + (((int)(1))));
                            
//#line 330 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
i46593 = t46592;
                        }
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46258 =
                          ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                            ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region __desugarer__var__25__45779 =
                          ((x10.array.Region)(((x10.array.Region)
                                                t46258)));
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
x10.array.Region ret45780 =
                           null;
                        
//#line 333 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46602 =
                          __desugarer__var__25__45779.
                            rank;
                        
//#line 333 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46603 =
                          x10.array.RectRegion.this.
                            rank;
                        
//#line 333 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46604 =
                          ((int) t46602) ==
                        ((int) t46603);
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46605 =
                          !(t46604);
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46605) {
                            
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46606 =
                              true;
                            
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46606) {
                                
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.FailedDynamicCheckException t46607 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.RectRegion).rank}");
                                
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46607;
                            }
                        }
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45780 = ((x10.array.Region)(__desugarer__var__25__45779));
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46265 =
                          ((x10.array.Region)(ret45780));
                        
//#line 333 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46265;
                    } else {
                        
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46296 =
                          x10.array.RectRegion1D.$RTT.isInstance(that);
                        
//#line 334 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46296) {
                            
//#line 335 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46266 =
                              this.min$O((int)(0));
                            
//#line 335 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46267 =
                              that.min$O((int)(0));
                            
//#line 335 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int newMin =
                              x10.lang.Math.max$O((int)(t46266),
                                                  (int)(t46267));
                            
//#line 336 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46268 =
                              this.max$O((int)(0));
                            
//#line 336 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46269 =
                              that.max$O((int)(0));
                            
//#line 336 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int newMax =
                              x10.lang.Math.min$O((int)(t46268),
                                                  (int)(t46269));
                            
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46278 =
                              ((newMax) < (((int)(newMin))));
                            
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46278) {
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46270 =
                                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(1))));
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region __desugarer__var__26__45782 =
                                  ((x10.array.Region)(((x10.array.Region)
                                                        t46270)));
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
x10.array.Region ret45783 =
                                   null;
                                
//#line 337 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46608 =
                                  __desugarer__var__26__45782.
                                    rank;
                                
//#line 337 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46609 =
                                  x10.array.RectRegion.this.
                                    rank;
                                
//#line 337 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46610 =
                                  ((int) t46608) ==
                                ((int) t46609);
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46611 =
                                  !(t46610);
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46611) {
                                    
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46612 =
                                      true;
                                    
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46612) {
                                        
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.FailedDynamicCheckException t46613 =
                                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.RectRegion).rank}");
                                        
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46613;
                                    }
                                }
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45783 = ((x10.array.Region)(__desugarer__var__26__45782));
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46277 =
                                  ((x10.array.Region)(ret45783));
                                
//#line 337 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46277;
                            }
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion1D t46279 =
                              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(((int)(newMin)),
                                                                                                                    ((int)(newMax)))));
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region __desugarer__var__27__45785 =
                              ((x10.array.Region)(((x10.array.Region)
                                                    t46279)));
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
x10.array.Region ret45786 =
                               null;
                            
//#line 338 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46614 =
                              __desugarer__var__27__45785.
                                rank;
                            
//#line 338 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46615 =
                              x10.array.RectRegion.this.
                                rank;
                            
//#line 338 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46616 =
                              ((int) t46614) ==
                            ((int) t46615);
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46617 =
                              !(t46616);
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46617) {
                                
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46618 =
                                  true;
                                
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46618) {
                                    
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.FailedDynamicCheckException t46619 =
                                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.RectRegion).rank}");
                                    
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46619;
                                }
                            }
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45786 = ((x10.array.Region)(__desugarer__var__27__45785));
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46286 =
                              ((x10.array.Region)(ret45786));
                            
//#line 338 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46286;
                        } else {
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46287 =
                              ((x10.array.Region)(this.toPolyRegion()));
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region __desugarer__var__28__45788 =
                              ((x10.array.Region)(((x10.array.Region)
                                                    t46287)));
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
x10.array.Region ret45789 =
                               null;
                            
//#line 341 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46620 =
                              __desugarer__var__28__45788.
                                rank;
                            
//#line 341 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46621 =
                              x10.array.RectRegion.this.
                                rank;
                            
//#line 341 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46622 =
                              ((int) t46620) ==
                            ((int) t46621);
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46623 =
                              !(t46622);
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46623) {
                                
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46624 =
                                  true;
                                
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46624) {
                                    
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.FailedDynamicCheckException t46625 =
                                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.RectRegion).rank}");
                                    
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46625;
                                }
                            }
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45789 = ((x10.array.Region)(__desugarer__var__28__45788));
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46294 =
                              ((x10.array.Region)(ret45789));
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46295 =
                              ((x10.array.Region)(t46294.intersection(((x10.array.Region)(that)))));
                            
//#line 341 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46295;
                        }
                    }
                }
            }
        }
        
        
//#line 347 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Region
                                                                                                     product(
                                                                                                     final x10.array.Region that){
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46360 =
              that.isEmpty$O();
            
//#line 348 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46360) {
                
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46300 =
                  rank;
                
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46301 =
                  that.
                    rank;
                
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46302 =
                  ((t46300) + (((int)(t46301))));
                
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46303 =
                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(t46302))));
                
//#line 349 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46303;
            } else {
                
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46359 =
                  x10.array.RectRegion.$RTT.isInstance(that);
                
//#line 350 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46359) {
                    
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46304 =
                      ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                    
//#line 351 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin =
                      t46304.min();
                    
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46305 =
                      ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                    
//#line 352 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax =
                      t46305.max();
                    
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46306 =
                      rank;
                    
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46307 =
                      that.
                        rank;
                    
//#line 353 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int k =
                      ((t46306) + (((int)(t46307))));
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46314 =
                      ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$64(this,
                                                                                   rank,
                                                                                   thatMin, (x10.array.RectRegion.$Closure$64.__2$1x10$lang$Int$3x10$lang$Int$2) null)));
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMin =
                      ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                               ((x10.core.fun.Fun_0_1)(t46314)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46321 =
                      ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$65(this,
                                                                                   rank,
                                                                                   thatMax, (x10.array.RectRegion.$Closure$65.__2$1x10$lang$Int$3x10$lang$Int$2) null)));
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMax =
                      ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                               ((x10.core.fun.Fun_0_1)(t46321)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                    
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46322 =
                      ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                        ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                    
//#line 356 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46322;
                } else {
                    
//#line 357 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46358 =
                      x10.array.RectRegion1D.$RTT.isInstance(that);
                    
//#line 357 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46358) {
                        
//#line 358 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int thatMin =
                          that.min$O((int)(0));
                        
//#line 359 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int thatMax =
                          that.max$O((int)(0));
                        
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46323 =
                          rank;
                        
//#line 360 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int k =
                          ((t46323) + (((int)(1))));
                        
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46328 =
                          ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$66(this,
                                                                                       rank,
                                                                                       thatMin)));
                        
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMin =
                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                                   ((x10.core.fun.Fun_0_1)(t46328)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                        
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46333 =
                          ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$67(this,
                                                                                       rank,
                                                                                       thatMax)));
                        
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMax =
                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                                   ((x10.core.fun.Fun_0_1)(t46333)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                        
//#line 363 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46334 =
                          ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                            ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                        
//#line 363 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46334;
                    } else {
                        
//#line 364 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46357 =
                          x10.array.FullRegion.$RTT.isInstance(that);
                        
//#line 364 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46357) {
                            
//#line 365 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46335 =
                              rank;
                            
//#line 365 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46336 =
                              that.
                                rank;
                            
//#line 365 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int k =
                              ((t46335) + (((int)(t46336))));
                            
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46341 =
                              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$68(this,
                                                                                           rank)));
                            
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMin =
                              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                                       ((x10.core.fun.Fun_0_1)(t46341)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46346 =
                              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$69(this,
                                                                                           rank)));
                            
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMax =
                              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                                       ((x10.core.fun.Fun_0_1)(t46346)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                            
//#line 368 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46347 =
                              ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                                ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                            
//#line 368 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46347;
                        } else {
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46348 =
                              ((x10.array.Region)(this.toPolyRegion()));
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region __desugarer__var__29__45791 =
                              ((x10.array.Region)(((x10.array.Region)
                                                    t46348)));
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
x10.array.Region ret45792 =
                               null;
                            
//#line 370 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46626 =
                              __desugarer__var__29__45791.
                                rank;
                            
//#line 370 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46627 =
                              x10.array.RectRegion.this.
                                rank;
                            
//#line 370 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46628 =
                              ((int) t46626) ==
                            ((int) t46627);
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46629 =
                              !(t46628);
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46629) {
                                
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46630 =
                                  true;
                                
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46630) {
                                    
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.FailedDynamicCheckException t46631 =
                                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.RectRegion).rank}");
                                    
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46631;
                                }
                            }
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45792 = ((x10.array.Region)(__desugarer__var__29__45791));
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46355 =
                              ((x10.array.Region)(ret45792));
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46356 =
                              ((x10.array.Region)(t46355.product(((x10.array.Region)(that)))));
                            
//#line 370 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46356;
                        }
                    }
                }
            }
        }
        
        
//#line 374 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Region
                                                                                                     translate(
                                                                                                     final x10.array.Point v){
            
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46364 =
              rank;
            
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46365 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$70(this,
                                                                           v)));
            
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMin =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t46364)),
                                                                                                                       ((x10.core.fun.Fun_0_1)(t46365)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46369 =
              rank;
            
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46370 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$71(this,
                                                                           v)));
            
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMax =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t46369)),
                                                                                                                       ((x10.core.fun.Fun_0_1)(t46370)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46371 =
              ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region __desugarer__var__30__45794 =
              ((x10.array.Region)(((x10.array.Region)
                                    t46371)));
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
x10.array.Region ret45795 =
               null;
            
//#line 377 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46632 =
              __desugarer__var__30__45794.
                rect;
            
//#line 377 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46633 =
              ((boolean) t46632) ==
            ((boolean) true);
            
//#line 377 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46633) {
                
//#line 377 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46634 =
                  __desugarer__var__30__45794.
                    rank;
                
//#line 377 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46635 =
                  x10.array.RectRegion.this.
                    rank;
                
//#line 377 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46633 = ((int) t46634) ==
                ((int) t46635);
            }
            
//#line 377 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46636 =
              t46633;
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46637 =
              !(t46636);
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46637) {
                
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46638 =
                  true;
                
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46638) {
                    
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.lang.FailedDynamicCheckException t46639 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rect==true, self.rank==this(:x10.array.RectRegion).rank}");
                    
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
throw t46639;
                }
            }
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45795 = ((x10.array.Region)(__desugarer__var__30__45794));
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region t46380 =
              ((x10.array.Region)(ret45795));
            
//#line 377 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46380;
        }
        
        
//#line 380 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Region
                                                                                                     projection(
                                                                                                     final int axis){
            
//#line 381 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46381 =
              this.min$O((int)(axis));
            
//#line 381 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46382 =
              this.max$O((int)(axis));
            
//#line 381 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46383 =
              ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(t46381,
                                                                                                t46382)));
            
//#line 381 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46383;
        }
        
        
//#line 384 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Region
                                                                                                     eliminate(
                                                                                                     final int axis){
            
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46384 =
              rank;
            
//#line 385 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int k =
              ((t46384) - (((int)(1))));
            
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46389 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$72(this,
                                                                           axis)));
            
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMin =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                       ((x10.core.fun.Fun_0_1)(t46389)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46394 =
              ((x10.core.fun.Fun_0_1)(new x10.array.RectRegion.$Closure$73(this,
                                                                           axis)));
            
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> newMax =
              ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(k)),
                                                                                                                       ((x10.core.fun.Fun_0_1)(t46394)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46395 =
              ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
            
//#line 388 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46395;
        }
        
        
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
@x10.core.X10Generated public static class RRIterator extends x10.core.Ref implements x10.lang.Iterator, x10.x10rt.X10JavaSerializable
                                                                                                   {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, RRIterator.class);
            
            public static final x10.rtt.RuntimeType<RRIterator> $RTT = x10.rtt.NamedType.<RRIterator> make(
            "x10.array.RectRegion.RRIterator", /* base class */RRIterator.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.lang.Iterator.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(RRIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + RRIterator.class + " calling"); } 
                x10.core.fun.Fun_0_1 min = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.min = min;
                x10.core.fun.Fun_0_1 max = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.max = max;
                $_obj.done = $deserializer.readBoolean();
                x10.array.Array cur = (x10.array.Array) $deserializer.readRef();
                $_obj.cur = cur;
                $_obj.myRank = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                RRIterator $_obj = new RRIterator((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                if (min instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.min);
                } else {
                $serializer.write(this.min);
                }
                if (max instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.max);
                } else {
                $serializer.write(this.max);
                }
                $serializer.write(this.done);
                if (cur instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.cur);
                } else {
                $serializer.write(this.cur);
                }
                $serializer.write(this.myRank);
                
            }
            
            // constructor just for allocation
            public RRIterator(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            public x10.array.Point
              next$G(){return next();}
            
                
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public int myRank;
                
                
//#line 393 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> min;
                
//#line 394 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> max;
                
//#line 395 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean done;
                
//#line 396 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Array<x10.core.Int> cur;
                
                
//#line 398 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
// creation method for java code (1-phase java constructor)
                public RRIterator(final x10.array.RectRegion rr){this((java.lang.System[]) null);
                                                                     $init(rr);}
                
                // constructor for non-virtual call
                final public x10.array.RectRegion.RRIterator x10$array$RectRegion$RRIterator$$init$S(final x10.array.RectRegion rr) { {
                                                                                                                                             
//#line 398 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"

                                                                                                                                             
//#line 399 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46640 =
                                                                                                                                               rr.
                                                                                                                                                 rank;
                                                                                                                                             
//#line 399 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.myRank = t46640;
                                                                                                                                             
                                                                                                                                             
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.__fieldInitializers44393();
                                                                                                                                             
//#line 400 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46397 =
                                                                                                                                               rr.min();
                                                                                                                                             
//#line 400 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.min = ((x10.core.fun.Fun_0_1)(t46397));
                                                                                                                                             
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46398 =
                                                                                                                                               rr.max();
                                                                                                                                             
//#line 401 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.max = ((x10.core.fun.Fun_0_1)(t46398));
                                                                                                                                             
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46399 =
                                                                                                                                               rr.
                                                                                                                                                 size;
                                                                                                                                             
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46400 =
                                                                                                                                               ((int) t46399) ==
                                                                                                                                             ((int) 0);
                                                                                                                                             
//#line 402 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.done = t46400;
                                                                                                                                             
//#line 403 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46401 =
                                                                                                                                               myRank;
                                                                                                                                             
//#line 403 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46402 =
                                                                                                                                               ((x10.core.fun.Fun_0_1)(min));
                                                                                                                                             
//#line 403 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46403 =
                                                                                                                                               ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t46401)),
                                                                                                                                                                                                                                                        ((x10.core.fun.Fun_0_1)(t46402)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                                                                                                                                             
//#line 403 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.cur = ((x10.array.Array)(t46403));
                                                                                                                                         }
                                                                                                                                         return this;
                                                                                                                                         }
                
                // constructor
                public x10.array.RectRegion.RRIterator $init(final x10.array.RectRegion rr){return x10$array$RectRegion$RRIterator$$init$S(rr);}
                
                
                
//#line 406 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                             hasNext$O(
                                                                                                             ){
                    
//#line 406 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46404 =
                      done;
                    
//#line 406 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46405 =
                      !(t46404);
                    
//#line 406 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46405;
                }
                
                
//#line 408 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.array.Point
                                                                                                             next(
                                                                                                             ){
                    
//#line 409 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46406 =
                      ((x10.array.Array)(cur));
                    
//#line 409 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Point ans =
                      ((x10.array.Point)(x10.array.Point.<x10.core.Int>make__0$1x10$array$Point$$T$2(x10.rtt.Types.INT, ((x10.array.Array)(t46406)))));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46408 =
                      ((x10.array.Array)(cur));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46407 =
                      myRank;
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46409 =
                      ((t46407) - (((int)(1))));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46413 =
                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t46408).$apply$G((int)(t46409)));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46411 =
                      ((x10.core.fun.Fun_0_1)(max));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46410 =
                      myRank;
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46412 =
                      ((t46410) - (((int)(1))));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46414 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)t46411).$apply(x10.core.Int.$box(t46412),x10.rtt.Types.INT));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46454 =
                      ((t46413) < (((int)(t46414))));
                    
//#line 410 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46454) {
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> x45797 =
                          ((x10.array.Array)(cur));
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46415 =
                          myRank;
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int y45798 =
                          ((t46415) - (((int)(1))));
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
;
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int ret45801 =
                           0;
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46641 =
                          x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)x45797).$apply$G((int)(y45798)));
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int r46642 =
                          ((t46641) + (((int)(1))));
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
((x10.array.Array<x10.core.Int>)x45797).$set__1x10$array$Array$$T$G((int)(y45798),
                                                                                                                                                                                       x10.core.Int.$box(r46642));
                        
//#line 411 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45801 = r46642;
                    } else {
                        
//#line 413 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46417 =
                          myRank;
                        
//#line 413 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46453 =
                          ((int) t46417) ==
                        ((int) 1);
                        
//#line 413 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46453) {
                            
//#line 414 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.done = true;
                        } else {
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46422 =
                              ((x10.array.Array)(cur));
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46418 =
                              myRank;
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46423 =
                              ((t46418) - (((int)(1))));
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46420 =
                              ((x10.core.fun.Fun_0_1)(min));
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46419 =
                              myRank;
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46421 =
                              ((t46419) - (((int)(1))));
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46424 =
                              x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)t46420).$apply(x10.core.Int.$box(t46421),x10.rtt.Types.INT));
                            
//#line 417 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
((x10.array.Array<x10.core.Int>)t46422).$set__1x10$array$Array$$T$G((int)(t46423),
                                                                                                                                                                                           x10.core.Int.$box(t46424));
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> x45803 =
                              ((x10.array.Array)(cur));
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46425 =
                              myRank;
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int y45804 =
                              ((t46425) - (((int)(2))));
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
;
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int ret45807 =
                               0;
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46656 =
                              x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)x45803).$apply$G((int)(y45804)));
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int r46657 =
                              ((t46656) + (((int)(1))));
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
((x10.array.Array<x10.core.Int>)x45803).$set__1x10$array$Array$$T$G((int)(y45804),
                                                                                                                                                                                           x10.core.Int.$box(r46657));
                            
//#line 418 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret45807 = r46657;
                            
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46427 =
                              myRank;
                            
//#line 419 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int carryRank =
                              ((t46427) - (((int)(2))));
                            
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
while (true) {
                                
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46428 =
                                  carryRank;
                                
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46435 =
                                  ((t46428) > (((int)(0))));
                                
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46435) {
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46429 =
                                      ((x10.array.Array)(cur));
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46430 =
                                      carryRank;
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46433 =
                                      x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t46429).$apply$G((int)(t46430)));
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46431 =
                                      ((x10.core.fun.Fun_0_1)(max));
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46432 =
                                      carryRank;
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46434 =
                                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)t46431).$apply(x10.core.Int.$box(t46432),x10.rtt.Types.INT));
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46435 = ((t46433) > (((int)(t46434))));
                                }
                                
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46445 =
                                  t46435;
                                
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46445)) {
                                    
//#line 420 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                                }
                                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46645 =
                                  ((x10.array.Array)(cur));
                                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46646 =
                                  carryRank;
                                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46647 =
                                  ((x10.core.fun.Fun_0_1)(min));
                                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46648 =
                                  carryRank;
                                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46649 =
                                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)t46647).$apply(x10.core.Int.$box(t46648),x10.rtt.Types.INT));
                                
//#line 421 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
((x10.array.Array<x10.core.Int>)t46645).$set__1x10$array$Array$$T$G((int)(t46646),
                                                                                                                                                                                               x10.core.Int.$box(t46649));
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> x46650 =
                                  ((x10.array.Array)(cur));
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46651 =
                                  carryRank;
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int y46652 =
                                  ((t46651) - (((int)(1))));
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
;
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int ret46653 =
                                   0;
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46643 =
                                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)x46650).$apply$G((int)(y46652)));
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int r46644 =
                                  ((t46643) + (((int)(1))));
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
((x10.array.Array<x10.core.Int>)x46650).$set__1x10$array$Array$$T$G((int)(y46652),
                                                                                                                                                                                               x10.core.Int.$box(r46644));
                                
//#line 422 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
ret46653 = r46644;
                                
//#line 423 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46654 =
                                  carryRank;
                                
//#line 423 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46655 =
                                  ((t46654) - (((int)(1))));
                                
//#line 423 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
carryRank = t46655;
                            }
                            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46446 =
                              carryRank;
                            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46451 =
                              ((int) t46446) ==
                            ((int) 0);
                            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46451) {
                                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Array<x10.core.Int> t46447 =
                                  ((x10.array.Array)(cur));
                                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46449 =
                                  x10.core.Int.$unbox(((x10.array.Array<x10.core.Int>)t46447).$apply$G((int)(0)));
                                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t46448 =
                                  ((x10.core.fun.Fun_0_1)(max));
                                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46450 =
                                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)t46448).$apply(x10.core.Int.$box(0),x10.rtt.Types.INT));
                                
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46451 = ((t46449) > (((int)(t46450))));
                            }
                            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46452 =
                              t46451;
                            
//#line 425 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46452) {
                                
//#line 426 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.done = true;
                            }
                        }
                    }
                    
//#line 430 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return ans;
                }
                
                
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final public x10.array.RectRegion.RRIterator
                                                                                                             x10$array$RectRegion$RRIterator$$x10$array$RectRegion$RRIterator$this(
                                                                                                             ){
                    
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return x10.array.RectRegion.RRIterator.this;
                }
                
                
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final public void
                                                                                                             __fieldInitializers44393(
                                                                                                             ){
                    
//#line 392 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.done = false;
                }
            
        }
        
        
        
//#line 433 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                     iterator(
                                                                                                     ){
            
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion.RRIterator t46455 =
              ((x10.array.RectRegion.RRIterator)(new x10.array.RectRegion.RRIterator((java.lang.System[]) null).$init(((x10.array.RectRegion)(this)))));
            
//#line 434 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46455;
        }
        
        
//#line 438 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public boolean
                                                                                                     equals(
                                                                                                     final java.lang.Object thatObj){
            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46456 =
              x10.rtt.Equality.equalsequals((this),(thatObj));
            
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46456) {
                
//#line 439 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return true;
            }
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46457 =
              x10.array.Region.$RTT.isInstance(thatObj);
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46458 =
              !(t46457);
            
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46458) {
                
//#line 440 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
            }
            
//#line 441 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.Region that =
              x10.rtt.Types.<x10.array.Region> cast(thatObj,x10.array.Region.$RTT);
            
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46459 =
              x10.array.RectRegion.$RTT.isInstance(that);
            
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46461 =
              !(t46459);
            
//#line 444 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46461) {
                
//#line 445 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46460 =
                  super.equals(((java.lang.Object)(that)));
                
//#line 445 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46460;
            }
            
//#line 448 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46462 =
              this.
                rank;
            
//#line 448 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46463 =
              that.
                rank;
            
//#line 448 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46464 =
              ((int) t46462) !=
            ((int) t46463);
            
//#line 448 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46464) {
                
//#line 449 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
            }
            
//#line 452 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thisMin =
              this.min();
            
//#line 453 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thisMax =
              this.max();
            
//#line 454 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46465 =
              ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
            
//#line 454 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin =
              t46465.min();
            
//#line 455 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.array.RectRegion t46466 =
              ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
            
//#line 455 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax =
              t46466.max();
            
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int i46670 =
              0;
            
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46671 =
                  i46670;
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46672 =
                  rank;
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46673 =
                  ((t46671) < (((int)(t46672))));
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46673)) {
                    
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                }
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46658 =
                  i46670;
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46659 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thisMin).$apply(x10.core.Int.$box(t46658),x10.rtt.Types.INT));
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46660 =
                  i46670;
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46661 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thatMin).$apply(x10.core.Int.$box(t46660),x10.rtt.Types.INT));
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
boolean t46662 =
                  ((int) t46659) !=
                ((int) t46661);
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46662)) {
                    
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46663 =
                      i46670;
                    
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46664 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thisMax).$apply(x10.core.Int.$box(t46663),x10.rtt.Types.INT));
                    
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46665 =
                      i46670;
                    
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46666 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thatMax).$apply(x10.core.Int.$box(t46665),x10.rtt.Types.INT));
                    
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46662 = ((int) t46664) !=
                    ((int) t46666);
                }
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46667 =
                  t46662;
                
//#line 459 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46667) {
                    
//#line 460 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return false;
                }
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46668 =
                  i46670;
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46669 =
                  ((t46668) + (((int)(1))));
                
//#line 458 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
i46670 = t46669;
            }
            
//#line 462 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return true;
        }
        
        
//#line 465 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
public java.lang.String
                                                                                                     toString(
                                                                                                     ){
            
//#line 466 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thisMin =
              this.min();
            
//#line 467 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thisMax =
              this.max();
            
//#line 468 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
java.lang.String s =
              "[";
            
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int i46689 =
              0;
            
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46690 =
                  i46689;
                
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46691 =
                  rank;
                
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46692 =
                  ((t46690) < (((int)(t46691))));
                
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (!(t46692)) {
                    
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
break;
                }
                
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46674 =
                  i46689;
                
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46675 =
                  ((t46674) > (((int)(0))));
                
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46675) {
                    
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46676 =
                      s;
                    
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46677 =
                      ((t46676) + (","));
                    
//#line 470 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
s = t46677;
                }
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46678 =
                  s;
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46679 =
                  i46689;
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46680 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thisMin).$apply(x10.core.Int.$box(t46679),x10.rtt.Types.INT));
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46681 =
                  (("") + ((x10.core.Int.$box(t46680))));
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46682 =
                  ((t46681) + (".."));
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46683 =
                  i46689;
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46684 =
                  x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)thisMax).$apply(x10.core.Int.$box(t46683),x10.rtt.Types.INT));
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46685 =
                  ((t46682) + ((x10.core.Int.$box(t46684))));
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46686 =
                  ((t46678) + (t46685));
                
//#line 471 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
s = t46686;
                
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46687 =
                  i46689;
                
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46688 =
                  ((t46687) + (((int)(1))));
                
//#line 469 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
i46689 = t46688;
            }
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46502 =
              s;
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46503 =
              ((t46502) + ("]"));
            
//#line 473 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
s = t46503;
            
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final java.lang.String t46504 =
              s;
            
//#line 474 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46504;
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final public x10.array.RectRegion
                                                                                                    x10$array$RectRegion$$x10$array$RectRegion$this(
                                                                                                    ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return x10.array.RectRegion.this;
        }
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final public void
                                                                                                    __fieldInitializers44394(
                                                                                                    ){
            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
this.polyRep = null;
        }
        
        @x10.core.X10Generated public static class $Closure$60 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$60.class);
            
            public static final x10.rtt.RuntimeType<$Closure$60> $RTT = x10.rtt.StaticFunType.<$Closure$60> make(
            /* base class */$Closure$60.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$60 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$60.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$60 $_obj = new $Closure$60((java.lang.System[]) null);
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
                
            }
            
            // constructor just for allocation
            public $Closure$60(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46064 =
                      this.
                        out$$.min$O((int)(i));
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46064;
                }
                
                public x10.array.RectRegion out$$;
                
                public $Closure$60(final x10.array.RectRegion out$$) { {
                                                                              this.out$$ = out$$;
                                                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$61 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$61.class);
            
            public static final x10.rtt.RuntimeType<$Closure$61> $RTT = x10.rtt.StaticFunType.<$Closure$61> make(
            /* base class */$Closure$61.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$61 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$61.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$61 $_obj = new $Closure$61((java.lang.System[]) null);
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
                
            }
            
            // constructor just for allocation
            public $Closure$61(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46066 =
                      this.
                        out$$.max$O((int)(i));
                    
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46066;
                }
                
                public x10.array.RectRegion out$$;
                
                public $Closure$61(final x10.array.RectRegion out$$) { {
                                                                              this.out$$ = out$$;
                                                                          }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$62 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$62.class);
            
            public static final x10.rtt.RuntimeType<$Closure$62> $RTT = x10.rtt.StaticFunType.<$Closure$62> make(
            /* base class */$Closure$62.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$62 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$62.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.fun.Fun_0_1 thatMin = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.thatMin = thatMin;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$62 $_obj = new $Closure$62((java.lang.System[]) null);
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
                if (thatMin instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.thatMin);
                } else {
                $serializer.write(this.thatMin);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$62(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46235 =
                      this.
                        out$$.min$O((int)(i));
                    
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46236 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                              thatMin).$apply(x10.core.Int.$box(i),x10.rtt.Types.INT));
                    
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46237 =
                      x10.lang.Math.max$O((int)(t46235),
                                          (int)(t46236));
                    
//#line 328 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46237;
                }
                
                public x10.array.RectRegion out$$;
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin;
                
                public $Closure$62(final x10.array.RectRegion out$$,
                                   final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                     this.thatMin = ((x10.core.fun.Fun_0_1)(thatMin));
                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __1$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$63 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$63.class);
            
            public static final x10.rtt.RuntimeType<$Closure$63> $RTT = x10.rtt.StaticFunType.<$Closure$63> make(
            /* base class */$Closure$63.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$63 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$63.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.core.fun.Fun_0_1 thatMax = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.thatMax = thatMax;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$63 $_obj = new $Closure$63((java.lang.System[]) null);
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
                if (thatMax instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.thatMax);
                } else {
                $serializer.write(this.thatMax);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$63(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46240 =
                      this.
                        out$$.max$O((int)(i));
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46241 =
                      x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                              thatMax).$apply(x10.core.Int.$box(i),x10.rtt.Types.INT));
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46242 =
                      x10.lang.Math.min$O((int)(t46240),
                                          (int)(t46241));
                    
//#line 329 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46242;
                }
                
                public x10.array.RectRegion out$$;
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax;
                
                public $Closure$63(final x10.array.RectRegion out$$,
                                   final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax, __1$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                     this.thatMax = ((x10.core.fun.Fun_0_1)(thatMax));
                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __1$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$64 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$64.class);
            
            public static final x10.rtt.RuntimeType<$Closure$64> $RTT = x10.rtt.StaticFunType.<$Closure$64> make(
            /* base class */$Closure$64.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$64 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$64.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                x10.core.fun.Fun_0_1 thatMin = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.thatMin = thatMin;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$64 $_obj = new $Closure$64((java.lang.System[]) null);
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
                $serializer.write(this.rank);
                if (thatMin instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.thatMin);
                } else {
                $serializer.write(this.thatMin);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$64(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46308 =
                      this.
                        rank;
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46311 =
                      ((i) < (((int)(t46308))));
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46312 =
                       0;
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46311) {
                        
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46312 = this.
                                                                                                                              out$$.min$O((int)(i));
                    } else {
                        
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46309 =
                          this.
                            rank;
                        
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46310 =
                          ((i) - (((int)(t46309))));
                        
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46312 = x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                                                                                                                                    thatMin).$apply(x10.core.Int.$box(t46310),x10.rtt.Types.INT));
                    }
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46313 =
                      t46312;
                    
//#line 354 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46313;
                }
                
                public x10.array.RectRegion out$$;
                public int rank;
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin;
                
                public $Closure$64(final x10.array.RectRegion out$$,
                                   final int rank,
                                   final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin, __2$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                     this.rank = rank;
                                                                                                                                                     this.thatMin = ((x10.core.fun.Fun_0_1)(thatMin));
                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __2$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$65 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$65.class);
            
            public static final x10.rtt.RuntimeType<$Closure$65> $RTT = x10.rtt.StaticFunType.<$Closure$65> make(
            /* base class */$Closure$65.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$65 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$65.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                x10.core.fun.Fun_0_1 thatMax = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.thatMax = thatMax;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$65 $_obj = new $Closure$65((java.lang.System[]) null);
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
                $serializer.write(this.rank);
                if (thatMax instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.thatMax);
                } else {
                $serializer.write(this.thatMax);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$65(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46315 =
                      this.
                        rank;
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46318 =
                      ((i) < (((int)(t46315))));
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46319 =
                       0;
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46318) {
                        
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46319 = this.
                                                                                                                              out$$.max$O((int)(i));
                    } else {
                        
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46316 =
                          this.
                            rank;
                        
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46317 =
                          ((i) - (((int)(t46316))));
                        
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46319 = x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                                                                                                                                    thatMax).$apply(x10.core.Int.$box(t46317),x10.rtt.Types.INT));
                    }
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46320 =
                      t46319;
                    
//#line 355 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46320;
                }
                
                public x10.array.RectRegion out$$;
                public int rank;
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax;
                
                public $Closure$65(final x10.array.RectRegion out$$,
                                   final int rank,
                                   final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax, __2$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                     this.rank = rank;
                                                                                                                                                     this.thatMax = ((x10.core.fun.Fun_0_1)(thatMax));
                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __2$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$66 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$66.class);
            
            public static final x10.rtt.RuntimeType<$Closure$66> $RTT = x10.rtt.StaticFunType.<$Closure$66> make(
            /* base class */$Closure$66.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$66 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$66.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                $_obj.thatMin = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$66 $_obj = new $Closure$66((java.lang.System[]) null);
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
                $serializer.write(this.rank);
                $serializer.write(this.thatMin);
                
            }
            
            // constructor just for allocation
            public $Closure$66(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46324 =
                      this.
                        rank;
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46325 =
                      ((i) < (((int)(t46324))));
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46326 =
                       0;
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46325) {
                        
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46326 = this.
                                                                                                                              out$$.min$O((int)(i));
                    } else {
                        
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46326 = this.
                                                                                                                              thatMin;
                    }
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46327 =
                      t46326;
                    
//#line 361 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46327;
                }
                
                public x10.array.RectRegion out$$;
                public int rank;
                public int thatMin;
                
                public $Closure$66(final x10.array.RectRegion out$$,
                                   final int rank,
                                   final int thatMin) { {
                                                               this.out$$ = out$$;
                                                               this.rank = rank;
                                                               this.thatMin = thatMin;
                                                           }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$67 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$67.class);
            
            public static final x10.rtt.RuntimeType<$Closure$67> $RTT = x10.rtt.StaticFunType.<$Closure$67> make(
            /* base class */$Closure$67.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$67 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$67.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                $_obj.thatMax = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$67 $_obj = new $Closure$67((java.lang.System[]) null);
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
                $serializer.write(this.rank);
                $serializer.write(this.thatMax);
                
            }
            
            // constructor just for allocation
            public $Closure$67(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46329 =
                      this.
                        rank;
                    
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46330 =
                      ((i) < (((int)(t46329))));
                    
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46331 =
                       0;
                    
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46330) {
                        
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46331 = this.
                                                                                                                              out$$.max$O((int)(i));
                    } else {
                        
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46331 = this.
                                                                                                                              thatMax;
                    }
                    
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46332 =
                      t46331;
                    
//#line 362 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46332;
                }
                
                public x10.array.RectRegion out$$;
                public int rank;
                public int thatMax;
                
                public $Closure$67(final x10.array.RectRegion out$$,
                                   final int rank,
                                   final int thatMax) { {
                                                               this.out$$ = out$$;
                                                               this.rank = rank;
                                                               this.thatMax = thatMax;
                                                           }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$68 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$68.class);
            
            public static final x10.rtt.RuntimeType<$Closure$68> $RTT = x10.rtt.StaticFunType.<$Closure$68> make(
            /* base class */$Closure$68.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$68 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$68.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$68 $_obj = new $Closure$68((java.lang.System[]) null);
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
                $serializer.write(this.rank);
                
            }
            
            // constructor just for allocation
            public $Closure$68(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46337 =
                      this.
                        rank;
                    
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46338 =
                      ((i) < (((int)(t46337))));
                    
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46339 =
                       0;
                    
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46338) {
                        
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46339 = this.
                                                                                                                              out$$.min$O((int)(i));
                    } else {
                        
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46339 = java.lang.Integer.MIN_VALUE;
                    }
                    
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46340 =
                      t46339;
                    
//#line 366 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46340;
                }
                
                public x10.array.RectRegion out$$;
                public int rank;
                
                public $Closure$68(final x10.array.RectRegion out$$,
                                   final int rank) { {
                                                            this.out$$ = out$$;
                                                            this.rank = rank;
                                                        }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$69 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$69.class);
            
            public static final x10.rtt.RuntimeType<$Closure$69> $RTT = x10.rtt.StaticFunType.<$Closure$69> make(
            /* base class */$Closure$69.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$69 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$69.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$69 $_obj = new $Closure$69((java.lang.System[]) null);
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
                $serializer.write(this.rank);
                
            }
            
            // constructor just for allocation
            public $Closure$69(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46342 =
                      this.
                        rank;
                    
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46343 =
                      ((i) < (((int)(t46342))));
                    
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46344 =
                       0;
                    
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46343) {
                        
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46344 = this.
                                                                                                                              out$$.max$O((int)(i));
                    } else {
                        
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46344 = java.lang.Integer.MAX_VALUE;
                    }
                    
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46345 =
                      t46344;
                    
//#line 367 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46345;
                }
                
                public x10.array.RectRegion out$$;
                public int rank;
                
                public $Closure$69(final x10.array.RectRegion out$$,
                                   final int rank) { {
                                                            this.out$$ = out$$;
                                                            this.rank = rank;
                                                        }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$70 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$70.class);
            
            public static final x10.rtt.RuntimeType<$Closure$70> $RTT = x10.rtt.StaticFunType.<$Closure$70> make(
            /* base class */$Closure$70.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$70 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$70.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point v = (x10.array.Point) $deserializer.readRef();
                $_obj.v = v;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$70 $_obj = new $Closure$70((java.lang.System[]) null);
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
                if (v instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.v);
                } else {
                $serializer.write(this.v);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$70(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46361 =
                      this.
                        out$$.min$O((int)(i));
                    
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46362 =
                      this.
                        v.$apply$O((int)(i));
                    
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46363 =
                      ((t46361) + (((int)(t46362))));
                    
//#line 375 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46363;
                }
                
                public x10.array.RectRegion out$$;
                public x10.array.Point v;
                
                public $Closure$70(final x10.array.RectRegion out$$,
                                   final x10.array.Point v) { {
                                                                     this.out$$ = out$$;
                                                                     this.v = ((x10.array.Point)(v));
                                                                 }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$71 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$71.class);
            
            public static final x10.rtt.RuntimeType<$Closure$71> $RTT = x10.rtt.StaticFunType.<$Closure$71> make(
            /* base class */$Closure$71.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$71 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$71.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Point v = (x10.array.Point) $deserializer.readRef();
                $_obj.v = v;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$71 $_obj = new $Closure$71((java.lang.System[]) null);
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
                if (v instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.v);
                } else {
                $serializer.write(this.v);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$71(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46366 =
                      this.
                        out$$.max$O((int)(i));
                    
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46367 =
                      this.
                        v.$apply$O((int)(i));
                    
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46368 =
                      ((t46366) + (((int)(t46367))));
                    
//#line 376 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46368;
                }
                
                public x10.array.RectRegion out$$;
                public x10.array.Point v;
                
                public $Closure$71(final x10.array.RectRegion out$$,
                                   final x10.array.Point v) { {
                                                                     this.out$$ = out$$;
                                                                     this.v = ((x10.array.Point)(v));
                                                                 }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$72 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$72.class);
            
            public static final x10.rtt.RuntimeType<$Closure$72> $RTT = x10.rtt.StaticFunType.<$Closure$72> make(
            /* base class */$Closure$72.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$72 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$72.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.axis = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$72 $_obj = new $Closure$72((java.lang.System[]) null);
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
                $serializer.write(this.axis);
                
            }
            
            // constructor just for allocation
            public $Closure$72(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46386 =
                      ((i) < (((int)(this.
                                       axis))));
                    
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46387 =
                       0;
                    
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46386) {
                        
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46387 = this.
                                                                                                                              out$$.min$O((int)(i));
                    } else {
                        
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46385 =
                          ((i) + (((int)(1))));
                        
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46387 = this.
                                                                                                                              out$$.min$O((int)(t46385));
                    }
                    
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46388 =
                      t46387;
                    
//#line 386 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46388;
                }
                
                public x10.array.RectRegion out$$;
                public int axis;
                
                public $Closure$72(final x10.array.RectRegion out$$,
                                   final int axis) { {
                                                            this.out$$ = out$$;
                                                            this.axis = axis;
                                                        }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$73 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$73.class);
            
            public static final x10.rtt.RuntimeType<$Closure$73> $RTT = x10.rtt.StaticFunType.<$Closure$73> make(
            /* base class */$Closure$73.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$73 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$73.class + " calling"); } 
                x10.array.RectRegion out$$ = (x10.array.RectRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.axis = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$73 $_obj = new $Closure$73((java.lang.System[]) null);
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
                $serializer.write(this.axis);
                
            }
            
            // constructor just for allocation
            public $Closure$73(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final boolean t46391 =
                      ((i) < (((int)(this.
                                       axis))));
                    
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
int t46392 =
                       0;
                    
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
if (t46391) {
                        
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46392 = this.
                                                                                                                              out$$.max$O((int)(i));
                    } else {
                        
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46390 =
                          ((i) + (((int)(1))));
                        
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
t46392 = this.
                                                                                                                              out$$.max$O((int)(t46390));
                    }
                    
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
final int t46393 =
                      t46392;
                    
//#line 387 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/RectRegion.x10"
return t46393;
                }
                
                public x10.array.RectRegion out$$;
                public int axis;
                
                public $Closure$73(final x10.array.RectRegion out$$,
                                   final int axis) { {
                                                            this.out$$ = out$$;
                                                            this.axis = axis;
                                                        }}
                
            }
            
        
        public boolean
          x10$array$Region$equals$S$O(
          final java.lang.Object a0){
            return super.equals(((java.lang.Object)(a0)));
        }
        // synthetic type for parameter mangling
        public abstract static class __0$1x10$lang$Int$2__1$1x10$lang$Int$2 {}
        
        }
        