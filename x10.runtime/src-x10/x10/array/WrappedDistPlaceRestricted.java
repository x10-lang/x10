package x10.array;

@x10.core.X10Generated final public class WrappedDistPlaceRestricted extends x10.array.Dist implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, WrappedDistPlaceRestricted.class);
    
    public static final x10.rtt.RuntimeType<WrappedDistPlaceRestricted> $RTT = x10.rtt.NamedType.<WrappedDistPlaceRestricted> make(
    "x10.array.WrappedDistPlaceRestricted", /* base class */WrappedDistPlaceRestricted.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(WrappedDistPlaceRestricted $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + WrappedDistPlaceRestricted.class + " calling"); } 
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.array.Dist base = (x10.array.Dist) $deserializer.readRef();
        $_obj.base = base;
        x10.lang.Place filter = (x10.lang.Place) $deserializer.readRef();
        $_obj.filter = filter;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        WrappedDistPlaceRestricted $_obj = new WrappedDistPlaceRestricted((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (base instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.base);
        } else {
        $serializer.write(this.base);
        }
        if (filter instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.filter);
        } else {
        $serializer.write(this.filter);
        }
        
    }
    
    // constructor just for allocation
    public WrappedDistPlaceRestricted(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.array.Dist base;
        
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.lang.Place filter;
        
        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
// creation method for java code (1-phase java constructor)
        public WrappedDistPlaceRestricted(final x10.array.Dist d,
                                          final x10.lang.Place p){this((java.lang.System[]) null);
                                                                      $init(d,p);}
        
        // constructor for non-virtual call
        final public x10.array.WrappedDistPlaceRestricted x10$array$WrappedDistPlaceRestricted$$init$S(final x10.array.Dist d,
                                                                                                       final x10.lang.Place p) { {
                                                                                                                                        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Region t48558 =
                                                                                                                                          ((x10.array.Region)(d.$apply(((x10.lang.Place)(p)))));
                                                                                                                                        
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
super.$init(((x10.array.Region)(t48558)));
                                                                                                                                        
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"

                                                                                                                                        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
this.base = ((x10.array.Dist)(d));
                                                                                                                                        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
this.filter = ((x10.lang.Place)(p));
                                                                                                                                    }
                                                                                                                                    return this;
                                                                                                                                    }
        
        // constructor
        public x10.array.WrappedDistPlaceRestricted $init(final x10.array.Dist d,
                                                          final x10.lang.Place p){return x10$array$WrappedDistPlaceRestricted$$init$S(d,p);}
        
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.array.PlaceGroup
                                                                                                                    places(
                                                                                                                    ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48506 =
              ((x10.lang.Place)(filter));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.SparsePlaceGroup t48507 =
              ((x10.array.SparsePlaceGroup)(new x10.array.SparsePlaceGroup((java.lang.System[]) null).$init(((x10.lang.Place)(t48506)))));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48507;
        }
        
        
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public int
                                                                                                                    numPlaces$O(
                                                                                                                    ){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return 1;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.lang.Sequence<x10.array.Region>
                                                                                                                    regions(
                                                                                                                    ){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region> t48509 =
              ((x10.core.fun.Fun_0_1)(new x10.array.WrappedDistPlaceRestricted.$Closure$88(this,
                                                                                           region)));
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Array<x10.array.Region> t48510 =
              ((x10.array.Array)(new x10.array.Array<x10.array.Region>((java.lang.System[]) null, x10.array.Region.$RTT).$init(((int)(1)),
                                                                                                                               ((x10.core.fun.Fun_0_1)(t48509)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Sequence<x10.array.Region> t48511 =
              ((x10.lang.Sequence<x10.array.Region>)
                ((x10.array.Array<x10.array.Region>)t48510).sequence());
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48511;
        }
        
        
//#line 36 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.array.Region
                                                                                                                    get(
                                                                                                                    final x10.lang.Place p){
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48512 =
              ((x10.lang.Place)(filter));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final boolean t48516 =
              p.equals$O(((x10.lang.Place)(t48512)));
            
//#line 37 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
if (t48516) {
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Region t48513 =
                  ((x10.array.Region)(region));
                
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48513;
            } else {
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final int t48514 =
                  this.rank$O();
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Region t48515 =
                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(t48514))));
                
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48515;
            }
        }
        
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.array.Region
                                                                                                                    $apply(
                                                                                                                    final x10.lang.Place p){
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Region t48517 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48517;
        }
        
        
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.lang.Place
                                                                                                                    $apply(
                                                                                                                    final x10.array.Point pt){
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Dist t48518 =
              ((x10.array.Dist)(base));
            
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place bp =
              t48518.$apply(((x10.array.Point)(pt)));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48519 =
              ((x10.lang.Place)(filter));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final boolean t48523 =
              bp.equals$O(((x10.lang.Place)(t48519)));
            
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
if (t48523) {
                
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return bp;
            } else {
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final java.lang.String t48520 =
                  (("point ") + (pt));
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final java.lang.String t48521 =
                  ((t48520) + (" not contained in distribution"));
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.ArrayIndexOutOfBoundsException t48522 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t48521)));
                
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
throw t48522;
            }
        }
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.lang.Place
                                                                                                                    $apply(
                                                                                                                    final int i0){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Point t48525 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0))));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48526 =
              this.$apply(((x10.array.Point)(t48525)));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48526;
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.lang.Place
                                                                                                                    $apply(
                                                                                                                    final int i0,
                                                                                                                    final int i1){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Point t48528 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1))));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48529 =
              this.$apply(((x10.array.Point)(t48528)));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48529;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.lang.Place
                                                                                                                    $apply(
                                                                                                                    final int i0,
                                                                                                                    final int i1,
                                                                                                                    final int i2){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Point t48531 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48532 =
              this.$apply(((x10.array.Point)(t48531)));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48532;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.lang.Place
                                                                                                                    $apply(
                                                                                                                    final int i0,
                                                                                                                    final int i1,
                                                                                                                    final int i2,
                                                                                                                    final int i3){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Point t48534 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2),
                                                      (int)(i3))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48535 =
              this.$apply(((x10.array.Point)(t48534)));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48535;
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public int
                                                                                                                    offset$O(
                                                                                                                    final x10.array.Point pt){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48536 =
              ((x10.lang.Place)(filter));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final boolean t48542 =
              x10.rtt.Equality.equalsequals((x10.lang.Runtime.home()),(t48536));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
if (t48542) {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Dist t48537 =
                  ((x10.array.Dist)(base));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final int t48538 =
                  t48537.offset$O(((x10.array.Point)(pt)));
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48538;
            } else {
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final java.lang.String t48539 =
                  (("point ") + (pt));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final java.lang.String t48540 =
                  ((t48539) + (" not contained in distribution"));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.ArrayIndexOutOfBoundsException t48541 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t48540)));
                
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
throw t48541;
            }
        }
        
        
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public int
                                                                                                                    maxOffset$O(
                                                                                                                    ){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Dist t48543 =
              ((x10.array.Dist)(base));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final int t48544 =
              t48543.maxOffset$O();
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48544;
        }
        
        
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.array.Dist
                                                                                                                    restriction(
                                                                                                                    final x10.array.Region r){
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.WrappedDistRegionRestricted t48545 =
              ((x10.array.WrappedDistRegionRestricted)(new x10.array.WrappedDistRegionRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                  ((x10.array.Region)(r)))));
            
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48545;
        }
        
        
//#line 82 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public x10.array.Dist
                                                                                                                    restriction(
                                                                                                                    final x10.lang.Place p){
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final boolean t48549 =
              p.equals$O(((x10.lang.Place)(x10.lang.Runtime.home())));
            
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
if (t48549) {
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return this;
            } else {
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final int t48546 =
                  this.rank$O();
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Region t48547 =
                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(t48546))));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Dist t48548 =
                  ((x10.array.Dist)(x10.array.Dist.make(((x10.array.Region)(t48547)))));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48548;
            }
        }
        
        
//#line 90 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
public boolean
                                                                                                                    equals(
                                                                                                                    final java.lang.Object thatObj){
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final boolean t48550 =
              x10.array.WrappedDistPlaceRestricted.$RTT.isInstance(thatObj);
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final boolean t48551 =
              !(t48550);
            
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
if (t48551) {
                
//#line 91 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return false;
            }
            
//#line 92 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.WrappedDistPlaceRestricted that =
              ((x10.array.WrappedDistPlaceRestricted)(x10.rtt.Types.<x10.array.WrappedDistPlaceRestricted> cast(thatObj,x10.array.WrappedDistPlaceRestricted.$RTT)));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Dist t48552 =
              ((x10.array.Dist)(this.
                                  base));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Dist t48553 =
              ((x10.array.Dist)(that.
                                  base));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
boolean t48556 =
              t48552.equals(((java.lang.Object)(t48553)));
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
if (t48556) {
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48554 =
                  ((x10.lang.Place)(this.
                                      filter));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.lang.Place t48555 =
                  ((x10.lang.Place)(that.
                                      filter));
                
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
t48556 = t48554.equals$O(((x10.lang.Place)(t48555)));
            }
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final boolean t48557 =
              t48556;
            
//#line 93 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48557;
        }
        
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final public x10.array.WrappedDistPlaceRestricted
                                                                                                                    x10$array$WrappedDistPlaceRestricted$$x10$array$WrappedDistPlaceRestricted$this(
                                                                                                                    ){
            
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return x10.array.WrappedDistPlaceRestricted.this;
        }
        
        @x10.core.X10Generated public static class $Closure$88 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$88.class);
            
            public static final x10.rtt.RuntimeType<$Closure$88> $RTT = x10.rtt.StaticFunType.<$Closure$88> make(
            /* base class */$Closure$88.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.Region.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$88 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$88.class + " calling"); } 
                x10.array.WrappedDistPlaceRestricted out$$ = (x10.array.WrappedDistPlaceRestricted) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Region region = (x10.array.Region) $deserializer.readRef();
                $_obj.region = region;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$88 $_obj = new $Closure$88((java.lang.System[]) null);
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
                if (region instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.region);
                } else {
                $serializer.write(this.region);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$88(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.Region
                  $apply(
                  final int id$82){
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
final x10.array.Region t48508 =
                      ((x10.array.Region)(this.
                                            region));
                    
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistPlaceRestricted.x10"
return t48508;
                }
                
                public x10.array.WrappedDistPlaceRestricted out$$;
                public x10.array.Region region;
                
                public $Closure$88(final x10.array.WrappedDistPlaceRestricted out$$,
                                   final x10.array.Region region) { {
                                                                           this.out$$ = out$$;
                                                                           this.region = ((x10.array.Region)(region));
                                                                       }}
                
            }
            
        
    }
    
    