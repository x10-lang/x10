package x10.array;

@x10.core.X10Generated final public class FullRegion extends x10.array.Region implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FullRegion.class);
    
    public static final x10.rtt.RuntimeType<FullRegion> $RTT = x10.rtt.NamedType.<FullRegion> make(
    "x10.array.FullRegion", /* base class */FullRegion.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FullRegion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + FullRegion.class + " calling"); } 
        x10.array.Region.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FullRegion $_obj = new FullRegion((java.lang.System[]) null);
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
    public FullRegion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
// creation method for java code (1-phase java constructor)
        public FullRegion(final int rank){this((java.lang.System[]) null);
                                              $init(rank);}
        
        // constructor for non-virtual call
        final public x10.array.FullRegion x10$array$FullRegion$$init$S(final int rank) { {
                                                                                                
//#line 20 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
super.$init(((int)(rank)),
                                                                                                                                                                                                      ((boolean)(true)),
                                                                                                                                                                                                      ((boolean)(false)));
                                                                                                
//#line 19 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"

                                                                                                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37843 =
                                                                                                  ((rank) < (((int)(0))));
                                                                                                
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37843) {
                                                                                                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37840 =
                                                                                                      (("Rank is negative (") + ((x10.core.Int.$box(rank))));
                                                                                                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37841 =
                                                                                                      ((t37840) + (")"));
                                                                                                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.lang.IllegalArgumentException t37842 =
                                                                                                      ((x10.lang.IllegalArgumentException)(new x10.lang.IllegalArgumentException(t37841)));
                                                                                                    
//#line 21 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
throw t37842;
                                                                                                }
                                                                                            }
                                                                                            return this;
                                                                                            }
        
        // constructor
        public x10.array.FullRegion $init(final int rank){return x10$array$FullRegion$$init$S(rank);}
        
        
        
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public boolean
                                                                                                    isConvex$O(
                                                                                                    ){
            
//#line 24 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return true;
        }
        
        
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public boolean
                                                                                                    isEmpty$O(
                                                                                                    ){
            
//#line 25 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return false;
        }
        
        
//#line 26 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public int
                                                                                                    size$O(
                                                                                                    ){
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.UnboundedRegionException t37844 =
              ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(((java.lang.String)("size not supported")))));
            
//#line 27 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
throw t37844;
        }
        
        
//#line 29 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public int
                                                                                                    indexOf$O(
                                                                                                    final x10.array.Point id$46){
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.UnboundedRegionException t37845 =
              ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(((java.lang.String)("indexOf not supported")))));
            
//#line 30 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
throw t37845;
        }
        
        
//#line 32 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                    min(
                                                                                                    ){
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t37854 =
              ((x10.core.fun.Fun_0_1)(new x10.array.FullRegion.$Closure$22(this,
                                                                           rank)));
            
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37854;
        }
        
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                    max(
                                                                                                    ){
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t37863 =
              ((x10.core.fun.Fun_0_1)(new x10.array.FullRegion.$Closure$23(this,
                                                                           rank)));
            
//#line 39 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37863;
        }
        
        
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.array.Region
                                                                                                    intersection(
                                                                                                    final x10.array.Region that){
            
//#line 44 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return that;
        }
        
        
//#line 45 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.array.Region
                                                                                                    product(
                                                                                                    final x10.array.Region that){
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37900 =
              that.isEmpty$O();
            
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37900) {
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37864 =
                  rank;
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37865 =
                  that.
                    rank;
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37866 =
                  ((t37864) + (((int)(t37865))));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.Region t37867 =
                  ((x10.array.Region)(x10.array.Region.makeEmpty((int)(t37866))));
                
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37867;
            } else {
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37899 =
                  x10.array.FullRegion.$RTT.isInstance(that);
                
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37899) {
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37868 =
                      rank;
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37869 =
                      that.
                        rank;
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37870 =
                      ((t37868) + (((int)(t37869))));
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.FullRegion t37871 =
                      ((x10.array.FullRegion)(new x10.array.FullRegion((java.lang.System[]) null).$init(t37870)));
                    
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37871;
                } else {
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37898 =
                      x10.array.RectRegion.$RTT.isInstance(that);
                    
//#line 50 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37898) {
                        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.RectRegion t37872 =
                          ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                        
//#line 51 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin =
                          t37872.min();
                        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.RectRegion t37873 =
                          ((x10.array.RectRegion)(x10.rtt.Types.<x10.array.RectRegion> cast(that,x10.array.RectRegion.$RTT)));
                        
//#line 52 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax =
                          t37873.max();
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37874 =
                          rank;
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37875 =
                          that.
                            rank;
                        
//#line 53 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int newRank =
                          ((t37874) + (((int)(t37875))));
                        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t37882 =
                          ((x10.core.fun.Fun_0_1)(new x10.array.FullRegion.$Closure$24(this,
                                                                                       rank,
                                                                                       thatMin, (x10.array.FullRegion.$Closure$24.__2$1x10$lang$Int$3x10$lang$Int$2) null)));
                        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.Array<x10.core.Int> newMin =
                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(newRank)),
                                                                                                                                   ((x10.core.fun.Fun_0_1)(t37882)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t37889 =
                          ((x10.core.fun.Fun_0_1)(new x10.array.FullRegion.$Closure$25(this,
                                                                                       rank,
                                                                                       thatMax, (x10.array.FullRegion.$Closure$25.__2$1x10$lang$Int$3x10$lang$Int$2) null)));
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.Array<x10.core.Int> newMax =
                          ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(newRank)),
                                                                                                                                   ((x10.core.fun.Fun_0_1)(t37889)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.RectRegion t37890 =
                          ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                            ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                        
//#line 56 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37890;
                    } else {
                        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37897 =
                          x10.array.RectRegion1D.$RTT.isInstance(that);
                        
//#line 57 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37897) {
                            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.RectRegion1D t37891 =
                              ((x10.array.RectRegion1D)(x10.rtt.Types.<x10.array.RectRegion1D> cast(that,x10.array.RectRegion1D.$RTT)));
                            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.RectRegion t37892 =
                              ((x10.array.RectRegion)(t37891.toRectRegion()));
                            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.Region t37893 =
                              ((x10.array.Region)(this.product(((x10.array.Region)(t37892)))));
                            
//#line 58 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37893;
                        } else {
                            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37894 =
                              x10.rtt.Types.typeName(that);
                            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37895 =
                              (("haven\'t implemented FullRegion product with ") + (t37894));
                            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.lang.UnsupportedOperationException t37896 =
                              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t37895)));
                            
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
throw t37896;
                        }
                    }
                }
            }
        }
        
        
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.array.Region
                                                                                                    projection(
                                                                                                    final int axis){
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.FullRegion t37901 =
              ((x10.array.FullRegion)(new x10.array.FullRegion((java.lang.System[]) null).$init(((int)(1)))));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37901;
        }
        
        
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.array.Region
                                                                                                    translate(
                                                                                                    final x10.array.Point p){
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return this;
        }
        
        
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.array.FullRegion
                                                                                                    eliminate(
                                                                                                    final int i){
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37902 =
              rank;
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37903 =
              ((t37902) - (((int)(1))));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.FullRegion t37904 =
              ((x10.array.FullRegion)(new x10.array.FullRegion((java.lang.System[]) null).$init(t37903)));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37904;
        }
        
        
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.array.Region
                                                                                                    computeBoundingBox(
                                                                                                    ){
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return this;
        }
        
        
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public boolean
                                                                                                    contains$O(
                                                                                                    final x10.array.Region that){
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return true;
        }
        
        
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public boolean
                                                                                                    contains$O(
                                                                                                    final x10.array.Point p){
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return true;
        }
        
        
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public java.lang.String
                                                                                                    toString(
                                                                                                    ){
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37905 =
              rank;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37906 =
              (("full(") + ((x10.core.Int.$box(t37905))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37907 =
              ((t37906) + (")"));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37907;
        }
        
        
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                    iterator(
                                                                                                    ){
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.array.UnboundedRegionException t37908 =
              ((x10.array.UnboundedRegionException)(new x10.array.UnboundedRegionException(((java.lang.String)("iterator not supported")))));
            
//#line 73 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
throw t37908;
        }
        
        
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final public x10.array.FullRegion
                                                                                                    x10$array$FullRegion$$x10$array$FullRegion$this(
                                                                                                    ){
            
//#line 17 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return x10.array.FullRegion.this;
        }
        
        @x10.core.X10Generated public static class $Closure$22 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$22.class);
            
            public static final x10.rtt.RuntimeType<$Closure$22> $RTT = x10.rtt.StaticFunType.<$Closure$22> make(
            /* base class */$Closure$22.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$22 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$22.class + " calling"); } 
                x10.array.FullRegion out$$ = (x10.array.FullRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$22 $_obj = new $Closure$22((java.lang.System[]) null);
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
            public $Closure$22(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
boolean t37847 =
                      ((i) < (((int)(0))));
                    
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (!(t37847)) {
                        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37846 =
                          this.
                            rank;
                        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
t37847 = ((i) >= (((int)(t37846))));
                    }
                    
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37852 =
                      t37847;
                    
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37852) {
                        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37848 =
                          (("min: ") + ((x10.core.Int.$box(i))));
                        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37849 =
                          ((t37848) + (" is not a valid rank for "));
                        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37850 =
                          ((t37849) + (this.
                                         out$$));
                        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t37851 =
                          ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t37850)));
                        
//#line 34 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
throw t37851;
                    }
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37853 =
                      java.lang.Integer.MIN_VALUE;
                    
//#line 35 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37853;
                }
                
                public x10.array.FullRegion out$$;
                public int rank;
                
                public $Closure$22(final x10.array.FullRegion out$$,
                                   final int rank) { {
                                                            this.out$$ = out$$;
                                                            this.rank = rank;
                                                        }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$23 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$23.class);
            
            public static final x10.rtt.RuntimeType<$Closure$23> $RTT = x10.rtt.StaticFunType.<$Closure$23> make(
            /* base class */$Closure$23.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$23 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$23.class + " calling"); } 
                x10.array.FullRegion out$$ = (x10.array.FullRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$23 $_obj = new $Closure$23((java.lang.System[]) null);
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
            public $Closure$23(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
boolean t37856 =
                      ((i) < (((int)(0))));
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (!(t37856)) {
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37855 =
                          this.
                            rank;
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
t37856 = ((i) >= (((int)(t37855))));
                    }
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37861 =
                      t37856;
                    
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37861) {
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37857 =
                          (("max: ") + ((x10.core.Int.$box(i))));
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37858 =
                          ((t37857) + (" is not a valid rank for "));
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final java.lang.String t37859 =
                          ((t37858) + (this.
                                         out$$));
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t37860 =
                          ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t37859)));
                        
//#line 40 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
throw t37860;
                    }
                    
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37862 =
                      java.lang.Integer.MAX_VALUE;
                    
//#line 41 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37862;
                }
                
                public x10.array.FullRegion out$$;
                public int rank;
                
                public $Closure$23(final x10.array.FullRegion out$$,
                                   final int rank) { {
                                                            this.out$$ = out$$;
                                                            this.rank = rank;
                                                        }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$24 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$24.class);
            
            public static final x10.rtt.RuntimeType<$Closure$24> $RTT = x10.rtt.StaticFunType.<$Closure$24> make(
            /* base class */$Closure$24.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$24 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$24.class + " calling"); } 
                x10.array.FullRegion out$$ = (x10.array.FullRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                x10.core.fun.Fun_0_1 thatMin = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.thatMin = thatMin;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$24 $_obj = new $Closure$24((java.lang.System[]) null);
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
            public $Closure$24(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37876 =
                      this.
                        rank;
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37879 =
                      ((i) < (((int)(t37876))));
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
int t37880 =
                       0;
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37879) {
                        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
t37880 = java.lang.Integer.MIN_VALUE;
                    } else {
                        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37877 =
                          this.
                            rank;
                        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37878 =
                          ((i) - (((int)(t37877))));
                        
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
t37880 = x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                                                                                                                                   thatMin).$apply(x10.core.Int.$box(t37878),x10.rtt.Types.INT));
                    }
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37881 =
                      t37880;
                    
//#line 54 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37881;
                }
                
                public x10.array.FullRegion out$$;
                public int rank;
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin;
                
                public $Closure$24(final x10.array.FullRegion out$$,
                                   final int rank,
                                   final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMin, __2$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                     this.rank = rank;
                                                                                                                                                     this.thatMin = ((x10.core.fun.Fun_0_1)(thatMin));
                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __2$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        @x10.core.X10Generated public static class $Closure$25 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$25.class);
            
            public static final x10.rtt.RuntimeType<$Closure$25> $RTT = x10.rtt.StaticFunType.<$Closure$25> make(
            /* base class */$Closure$25.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$25 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$25.class + " calling"); } 
                x10.array.FullRegion out$$ = (x10.array.FullRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rank = $deserializer.readInt();
                x10.core.fun.Fun_0_1 thatMax = (x10.core.fun.Fun_0_1) $deserializer.readRef();
                $_obj.thatMax = thatMax;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$25 $_obj = new $Closure$25((java.lang.System[]) null);
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
            public $Closure$25(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37883 =
                      this.
                        rank;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final boolean t37886 =
                      ((i) < (((int)(t37883))));
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
int t37887 =
                       0;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
if (t37886) {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
t37887 = java.lang.Integer.MAX_VALUE;
                    } else {
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37884 =
                          this.
                            rank;
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37885 =
                          ((i) - (((int)(t37884))));
                        
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
t37887 = x10.core.Int.$unbox(((x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>)this.
                                                                                                                                                                                                   thatMax).$apply(x10.core.Int.$box(t37885),x10.rtt.Types.INT));
                    }
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
final int t37888 =
                      t37887;
                    
//#line 55 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/FullRegion.x10"
return t37888;
                }
                
                public x10.array.FullRegion out$$;
                public int rank;
                public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax;
                
                public $Closure$25(final x10.array.FullRegion out$$,
                                   final int rank,
                                   final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> thatMax, __2$1x10$lang$Int$3x10$lang$Int$2 $dummy) { {
                                                                                                                                                     this.out$$ = out$$;
                                                                                                                                                     this.rank = rank;
                                                                                                                                                     this.thatMax = ((x10.core.fun.Fun_0_1)(thatMax));
                                                                                                                                                 }}
                // synthetic type for parameter mangling
                public abstract static class __2$1x10$lang$Int$3x10$lang$Int$2 {}
                
            }
            
        
        }
        