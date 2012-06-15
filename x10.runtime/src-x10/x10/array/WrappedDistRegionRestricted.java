package x10.array;

@x10.core.X10Generated final public class WrappedDistRegionRestricted extends x10.array.Dist implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, WrappedDistRegionRestricted.class);
    
    public static final x10.rtt.RuntimeType<WrappedDistRegionRestricted> $RTT = x10.rtt.NamedType.<WrappedDistRegionRestricted> make(
    "x10.array.WrappedDistRegionRestricted", /* base class */WrappedDistRegionRestricted.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(WrappedDistRegionRestricted $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + WrappedDistRegionRestricted.class + " calling"); } 
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.array.Dist base = (x10.array.Dist) $deserializer.readRef();
        $_obj.base = base;
        x10.array.Region filter = (x10.array.Region) $deserializer.readRef();
        $_obj.filter = filter;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        WrappedDistRegionRestricted $_obj = new WrappedDistRegionRestricted((java.lang.System[]) null);
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
    public WrappedDistRegionRestricted(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 18 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.array.Dist base;
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.array.Region filter;
        
        
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
// creation method for java code (1-phase java constructor)
        public WrappedDistRegionRestricted(final x10.array.Dist d,
                                           final x10.array.Region r){this((java.lang.System[]) null);
                                                                         $init(d,r);}
        
        // constructor for non-virtual call
        final public x10.array.WrappedDistRegionRestricted x10$array$WrappedDistRegionRestricted$$init$S(final x10.array.Dist d,
                                                                                                         final x10.array.Region r) { {
                                                                                                                                            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48720 =
                                                                                                                                              ((x10.array.Region)(d.
                                                                                                                                                                    region));
                                                                                                                                            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48721 =
                                                                                                                                              ((x10.array.Region)(t48720.intersection(((x10.array.Region)(r)))));
                                                                                                                                            
//#line 22 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
super.$init(((x10.array.Region)(t48721)));
                                                                                                                                            
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"

                                                                                                                                            
//#line 23 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
this.base = ((x10.array.Dist)(d));
                                                                                                                                            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
this.filter = ((x10.array.Region)(r));
                                                                                                                                        }
                                                                                                                                        return this;
                                                                                                                                        }
        
        // constructor
        public x10.array.WrappedDistRegionRestricted $init(final x10.array.Dist d,
                                                           final x10.array.Region r){return x10$array$WrappedDistRegionRestricted$$init$S(d,r);}
        
        
        
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.array.PlaceGroup
                                                                                                                     places(
                                                                                                                     ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48661 =
              ((x10.array.Dist)(base));
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.PlaceGroup t48662 =
              t48661.places();
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48662;
        }
        
        
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public int
                                                                                                                     numPlaces$O(
                                                                                                                     ){
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48663 =
              ((x10.array.Dist)(base));
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final int t48664 =
              t48663.numPlaces$O();
            
//#line 31 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48664;
        }
        
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.lang.Sequence<x10.array.Region>
                                                                                                                     regions(
                                                                                                                     ){
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final int t48670 =
              x10.lang.Place.getInitialized$MAX_PLACES();
            
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region> t48671 =
              ((x10.core.fun.Fun_0_1)(new x10.array.WrappedDistRegionRestricted.$Closure$89(this,
                                                                                            base,
                                                                                            filter)));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Array<x10.array.Region> t48672 =
              ((x10.array.Array)(new x10.array.Array<x10.array.Region>((java.lang.System[]) null, x10.array.Region.$RTT).$init(((int)(t48670)),
                                                                                                                               ((x10.core.fun.Fun_0_1)(t48671)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.Sequence<x10.array.Region> t48673 =
              ((x10.lang.Sequence<x10.array.Region>)
                ((x10.array.Array<x10.array.Region>)t48672).sequence());
            
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48673;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.array.Region
                                                                                                                     get(
                                                                                                                     final x10.lang.Place p){
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48674 =
              ((x10.array.Dist)(base));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48675 =
              ((x10.array.Region)(t48674.get(((x10.lang.Place)(p)))));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48676 =
              ((x10.array.Region)(filter));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48677 =
              ((x10.array.Region)(t48675.intersection(((x10.array.Region)(t48676)))));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48677;
        }
        
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.array.Region
                                                                                                                     $apply(
                                                                                                                     final x10.lang.Place p){
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48678 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48678;
        }
        
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.lang.Place
                                                                                                                     $apply(
                                                                                                                     final x10.array.Point pt){
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48679 =
              ((x10.array.Region)(filter));
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final boolean t48685 =
              t48679.contains$O(((x10.array.Point)(pt)));
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
if (t48685) {
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48680 =
                  ((x10.array.Dist)(base));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.Place t48681 =
                  t48680.$apply(((x10.array.Point)(pt)));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48681;
            } else {
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final java.lang.String t48682 =
                  (("point ") + (pt));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final java.lang.String t48683 =
                  ((t48682) + (" not contained in distribution"));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.ArrayIndexOutOfBoundsException t48684 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t48683)));
                
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
throw t48684;
            }
        }
        
        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.lang.Place
                                                                                                                     $apply(
                                                                                                                     final int i0){
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Point t48687 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0))));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.Place t48688 =
              this.$apply(((x10.array.Point)(t48687)));
            
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48688;
        }
        
        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.lang.Place
                                                                                                                     $apply(
                                                                                                                     final int i0,
                                                                                                                     final int i1){
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Point t48690 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1))));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.Place t48691 =
              this.$apply(((x10.array.Point)(t48690)));
            
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48691;
        }
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.lang.Place
                                                                                                                     $apply(
                                                                                                                     final int i0,
                                                                                                                     final int i1,
                                                                                                                     final int i2){
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Point t48693 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2))));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.Place t48694 =
              this.$apply(((x10.array.Point)(t48693)));
            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48694;
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.lang.Place
                                                                                                                     $apply(
                                                                                                                     final int i0,
                                                                                                                     final int i1,
                                                                                                                     final int i2,
                                                                                                                     final int i3){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Point t48696 =
              ((x10.array.Point)(x10.array.Point.make((int)(i0),
                                                      (int)(i1),
                                                      (int)(i2),
                                                      (int)(i3))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.Place t48697 =
              this.$apply(((x10.array.Point)(t48696)));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48697;
        }
        
        
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public int
                                                                                                                     offset$O(
                                                                                                                     final x10.array.Point pt){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48698 =
              ((x10.array.Region)(filter));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final boolean t48704 =
              t48698.contains$O(((x10.array.Point)(pt)));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
if (t48704) {
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48699 =
                  ((x10.array.Dist)(base));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final int t48700 =
                  t48699.offset$O(((x10.array.Point)(pt)));
                
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48700;
            } else {
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final java.lang.String t48701 =
                  (("point ") + (pt));
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final java.lang.String t48702 =
                  ((t48701) + (" not contained in distribution"));
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.ArrayIndexOutOfBoundsException t48703 =
                  ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t48702)));
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
throw t48703;
            }
        }
        
        
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public int
                                                                                                                     maxOffset$O(
                                                                                                                     ){
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48705 =
              ((x10.array.Dist)(base));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final int t48706 =
              t48705.maxOffset$O();
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48706;
        }
        
        
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.array.Dist
                                                                                                                     restriction(
                                                                                                                     final x10.array.Region r){
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48708 =
              ((x10.array.Dist)(base));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48707 =
              ((x10.array.Region)(filter));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48709 =
              ((x10.array.Region)(t48707.intersection(((x10.array.Region)(r)))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.WrappedDistRegionRestricted t48710 =
              ((x10.array.WrappedDistRegionRestricted)(new x10.array.WrappedDistRegionRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(t48708)),
                                                                                                                                  ((x10.array.Region)(t48709)))));
            
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48710;
        }
        
        
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public x10.array.Dist
                                                                                                                     restriction(
                                                                                                                     final x10.lang.Place p){
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.WrappedDistPlaceRestricted t48711 =
              ((x10.array.WrappedDistPlaceRestricted)(new x10.array.WrappedDistPlaceRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                ((x10.lang.Place)(p)))));
            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48711;
        }
        
        
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
public boolean
                                                                                                                     equals(
                                                                                                                     final java.lang.Object thatObj){
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final boolean t48712 =
              x10.array.WrappedDistRegionRestricted.$RTT.isInstance(thatObj);
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final boolean t48713 =
              !(t48712);
            
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
if (t48713) {
                
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return false;
            }
            
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.WrappedDistRegionRestricted that =
              ((x10.array.WrappedDistRegionRestricted)(x10.rtt.Types.<x10.array.WrappedDistRegionRestricted> cast(thatObj,x10.array.WrappedDistRegionRestricted.$RTT)));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48714 =
              ((x10.array.Dist)(this.
                                  base));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48715 =
              ((x10.array.Dist)(that.
                                  base));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
boolean t48718 =
              t48714.equals(((java.lang.Object)(t48715)));
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
if (t48718) {
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48716 =
                  ((x10.array.Region)(this.
                                        filter));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48717 =
                  ((x10.array.Region)(that.
                                        filter));
                
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
t48718 = t48716.equals(((java.lang.Object)(t48717)));
            }
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final boolean t48719 =
              t48718;
            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48719;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final public x10.array.WrappedDistRegionRestricted
                                                                                                                     x10$array$WrappedDistRegionRestricted$$x10$array$WrappedDistRegionRestricted$this(
                                                                                                                     ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return x10.array.WrappedDistRegionRestricted.this;
        }
        
        @x10.core.X10Generated public static class $Closure$89 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$89.class);
            
            public static final x10.rtt.RuntimeType<$Closure$89> $RTT = x10.rtt.StaticFunType.<$Closure$89> make(
            /* base class */$Closure$89.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.Region.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$89 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$89.class + " calling"); } 
                x10.array.WrappedDistRegionRestricted out$$ = (x10.array.WrappedDistRegionRestricted) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Dist base = (x10.array.Dist) $deserializer.readRef();
                $_obj.base = base;
                x10.array.Region filter = (x10.array.Region) $deserializer.readRef();
                $_obj.filter = filter;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$89 $_obj = new $Closure$89((java.lang.System[]) null);
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
            public $Closure$89(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.Region
                  $apply(
                  final int i){
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Dist t48665 =
                      ((x10.array.Dist)(this.
                                          base));
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.lang.Place t48666 =
                      ((x10.lang.Place)(new x10.lang.Place((java.lang.System[]) null).$init(((int)(i)))));
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48667 =
                      ((x10.array.Region)(t48665.get(((x10.lang.Place)(t48666)))));
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48668 =
                      ((x10.array.Region)(this.
                                            filter));
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
final x10.array.Region t48669 =
                      ((x10.array.Region)(t48667.intersection(((x10.array.Region)(t48668)))));
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/WrappedDistRegionRestricted.x10"
return t48669;
                }
                
                public x10.array.WrappedDistRegionRestricted out$$;
                public x10.array.Dist base;
                public x10.array.Region filter;
                
                public $Closure$89(final x10.array.WrappedDistRegionRestricted out$$,
                                   final x10.array.Dist base,
                                   final x10.array.Region filter) { {
                                                                           this.out$$ = out$$;
                                                                           this.base = ((x10.array.Dist)(base));
                                                                           this.filter = ((x10.array.Region)(filter));
                                                                       }}
                
            }
            
        
    }
    
    