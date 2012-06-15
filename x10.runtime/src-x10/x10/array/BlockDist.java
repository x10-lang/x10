package x10.array;


@x10.core.X10Generated final public class BlockDist extends x10.array.Dist implements x10.x10rt.X10JavaSerializable
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, BlockDist.class);
    
    public static final x10.rtt.RuntimeType<BlockDist> $RTT = x10.rtt.NamedType.<BlockDist> make(
    "x10.array.BlockDist", /* base class */BlockDist.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Dist.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(BlockDist $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + BlockDist.class + " calling"); } 
        x10.array.Dist.$_deserialize_body($_obj, $deserializer);
        x10.array.PlaceGroup pg = (x10.array.PlaceGroup) $deserializer.readRef();
        $_obj.pg = pg;
        $_obj.axis = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        BlockDist $_obj = new BlockDist((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (pg instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.pg);
        } else {
        $serializer.write(this.pg);
        }
        $serializer.write(this.axis);
        
    }
    
    // constructor just for allocation
    public BlockDist(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 33 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
/**
     * The place group for this distribution
     */
        public x10.array.PlaceGroup pg;
        
//#line 38 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
/**
     * The axis along which the region is being distributed
     */
        public int axis;
        
//#line 43 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
/**
     * Cached restricted region for the current place.
     */
        public transient x10.array.Region regionForHere;
        
        
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
// creation method for java code (1-phase java constructor)
        public BlockDist(final x10.array.Region r,
                         final int axis,
                         final x10.array.PlaceGroup pg){this((java.lang.System[]) null);
                                                            $init(r,axis,pg);}
        
        // constructor for non-virtual call
        final public x10.array.BlockDist x10$array$BlockDist$$init$S(final x10.array.Region r,
                                                                     final int axis,
                                                                     final x10.array.PlaceGroup pg) { {
                                                                                                             
//#line 47 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
super.$init(((x10.array.Region)(r)));
                                                                                                             
//#line 46 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"

                                                                                                             
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
this.__fieldInitializers34858();
                                                                                                             
//#line 48 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
this.axis = axis;
                                                                                                             
//#line 49 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
this.pg = ((x10.array.PlaceGroup)(pg));
                                                                                                         }
                                                                                                         return this;
                                                                                                         }
        
        // constructor
        public x10.array.BlockDist $init(final x10.array.Region r,
                                         final int axis,
                                         final x10.array.PlaceGroup pg){return x10$array$BlockDist$$init$S(r,axis,pg);}
        
        
        
//#line 60 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
private x10.array.Region
                                                                                                   blockRegionForPlace(
                                                                                                   final x10.lang.Place place){
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35037 =
              ((x10.array.Region)(region));
            
//#line 61 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region b =
              ((x10.array.Region)(t35037.boundingBox()));
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35038 =
              axis;
            
//#line 62 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int min =
              b.min$O((int)(t35038));
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35039 =
              axis;
            
//#line 63 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int max =
              b.max$O((int)(t35039));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35040 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 64 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int P =
              t35040.numPlaces$O();
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35041 =
              ((max) - (((int)(min))));
            
//#line 65 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int numElems =
              ((t35041) + (((int)(1))));
            
//#line 66 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int blockSize =
              ((numElems) / (((int)(P))));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35042 =
              ((P) * (((int)(blockSize))));
            
//#line 67 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int leftOver =
              ((numElems) - (((int)(t35042))));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35043 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 68 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int i =
              t35043.indexOf$O(((x10.lang.Place)(place)));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35044 =
              ((blockSize) * (((int)(i))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35047 =
              ((min) + (((int)(t35044))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35045 =
              ((i) < (((int)(leftOver))));
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
int t35046 =
               0;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35045) {
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35046 = i;
            } else {
                
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35046 = leftOver;
            }
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35048 =
              t35046;
            
//#line 69 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int low =
              ((t35047) + (((int)(t35048))));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35051 =
              ((low) + (((int)(blockSize))));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35049 =
              ((i) < (((int)(leftOver))));
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
int t35050 =
               0;
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35049) {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35050 = 0;
            } else {
                
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35050 = -1;
            }
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35052 =
              t35050;
            
//#line 70 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int hi =
              ((t35051) + (((int)(t35052))));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35053 =
              ((x10.array.Region)(region));
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35095 =
              x10.array.RectRegion.$RTT.isInstance(t35053);
            
//#line 72 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35095) {
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35056 =
                  this.rank$O();
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t35057 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.BlockDist.$Closure$7(this,
                                                                             region)));
                
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Array<x10.core.Int> newMin =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t35056)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t35057)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35060 =
                  this.rank$O();
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t35061 =
                  ((x10.core.fun.Fun_0_1)(new x10.array.BlockDist.$Closure$8(this,
                                                                             region)));
                
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Array<x10.core.Int> newMax =
                  ((x10.array.Array)(new x10.array.Array<x10.core.Int>((java.lang.System[]) null, x10.rtt.Types.INT).$init(((int)(t35060)),
                                                                                                                           ((x10.core.fun.Fun_0_1)(t35061)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35062 =
                  axis;
                
//#line 76 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
((x10.array.Array<x10.core.Int>)newMin).$set__1x10$array$Array$$T$G((int)(t35062),
                                                                                                                                                                             x10.core.Int.$box(low));
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35063 =
                  axis;
                
//#line 77 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
((x10.array.Array<x10.core.Int>)newMax).$set__1x10$array$Array$$T$G((int)(t35063),
                                                                                                                                                                             x10.core.Int.$box(hi));
                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.RectRegion t35064 =
                  ((x10.array.RectRegion)(new x10.array.RectRegion((java.lang.System[]) null).$init(((x10.array.Array)(newMin)),
                                                                                                    ((x10.array.Array)(newMax)), (x10.array.RectRegion.__0$1x10$lang$Int$2__1$1x10$lang$Int$2) null)));
                
//#line 78 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35064;
            } else {
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35065 =
                  ((x10.array.Region)(region));
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35094 =
                  x10.array.RectRegion1D.$RTT.isInstance(t35065);
                
//#line 79 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35094) {
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.RectRegion1D t35066 =
                      ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null).$init(((int)(low)),
                                                                                                            ((int)(hi)))));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region __desugarer__var__6__35031 =
                      ((x10.array.Region)(((x10.array.Region)
                                            t35066)));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Region ret35032 =
                       null;
                    
//#line 80 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35221 =
                      __desugarer__var__6__35031.
                        rank;
                    
//#line 80 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35222 =
                      ((x10.array.Region)(x10.array.BlockDist.this.
                                            region));
                    
//#line 80 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35223 =
                      t35222.
                        rank;
                    
//#line 80 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35224 =
                      ((int) t35221) ==
                    ((int) t35223);
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35225 =
                      !(t35224);
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35225) {
                        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35226 =
                          true;
                        
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35226) {
                            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.FailedDynamicCheckException t35227 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.BlockDist).region.rank}");
                            
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
throw t35227;
                        }
                    }
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
ret35032 = ((x10.array.Region)(__desugarer__var__6__35031));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35074 =
                      ((x10.array.Region)(ret35032));
                    
//#line 80 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35074;
                } else {
                    
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35075 =
                      axis;
                    
//#line 83 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r1 =
                      ((x10.array.Region)(x10.array.Region.makeFull((int)(t35075))));
                    
//#line 84 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.IntRange r2 =
                      ((x10.lang.IntRange)(new x10.lang.IntRange((java.lang.System[]) null).x10$lang$IntRange$$init$S(((int)(low)), ((int)(hi)))));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35076 =
                      ((x10.array.Region)(region));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35077 =
                      t35076.
                        rank;
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35078 =
                      axis;
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35079 =
                      ((t35077) - (((int)(t35078))));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35080 =
                      ((t35079) - (((int)(1))));
                    
//#line 85 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r3 =
                      ((x10.array.Region)(x10.array.Region.makeFull((int)(t35080))));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35081 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(r2)))));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35082 =
                      ((x10.array.Region)(r1.product(((x10.array.Region)(t35081)))));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35083 =
                      ((x10.array.Region)(t35082.product(((x10.array.Region)(r3)))));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region __desugarer__var__7__35034 =
                      ((x10.array.Region)(((x10.array.Region)
                                            t35083)));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Region ret35035 =
                       null;
                    
//#line 86 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35228 =
                      __desugarer__var__7__35034.
                        rank;
                    
//#line 86 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35229 =
                      ((x10.array.Region)(x10.array.BlockDist.this.
                                            region));
                    
//#line 86 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35230 =
                      t35229.
                        rank;
                    
//#line 86 . "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35231 =
                      ((int) t35228) ==
                    ((int) t35230);
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35232 =
                      !(t35231);
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35232) {
                        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35233 =
                          true;
                        
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35233) {
                            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.FailedDynamicCheckException t35234 =
                              new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.BlockDist).region.rank}");
                            
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
throw t35234;
                        }
                    }
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
ret35035 = ((x10.array.Region)(__desugarer__var__7__35034));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35091 =
                      ((x10.array.Region)(ret35035));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35092 =
                      ((x10.array.Region)(region));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35093 =
                      ((x10.array.Region)(t35091.intersection(((x10.array.Region)(t35092)))));
                    
//#line 86 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35093;
                }
            }
        }
        
        public static x10.array.Region
          blockRegionForPlace$P(
          final x10.lang.Place place,
          final x10.array.BlockDist BlockDist){
            return BlockDist.blockRegionForPlace(((x10.lang.Place)(place)));
        }
        
        
//#line 97 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
private x10.lang.Place
                                                                                                   mapIndexToPlace(
                                                                                                   final int index){
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35096 =
              ((x10.array.Region)(region));
            
//#line 98 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region bb =
              ((x10.array.Region)(t35096.boundingBox()));
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35097 =
              axis;
            
//#line 99 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int min =
              bb.min$O((int)(t35097));
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35098 =
              axis;
            
//#line 100 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int max =
              bb.max$O((int)(t35098));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35099 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 101 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int P =
              t35099.numPlaces$O();
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35100 =
              ((max) - (((int)(min))));
            
//#line 102 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int numElems =
              ((t35100) + (((int)(1))));
            
//#line 103 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int blockSize =
              ((numElems) / (((int)(P))));
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35101 =
              ((P) * (((int)(blockSize))));
            
//#line 104 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int leftOver =
              ((numElems) - (((int)(t35101))));
            
//#line 105 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int normalizedIndex =
              ((index) - (((int)(min))));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35102 =
              ((blockSize) + (((int)(1))));
            
//#line 107 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int nominalPlace =
              ((normalizedIndex) / (((int)(t35102))));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35112 =
              ((nominalPlace) < (((int)(leftOver))));
            
//#line 108 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35112) {
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35103 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35104 =
                  t35103.$apply((int)(nominalPlace));
                
//#line 109 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35104;
            } else {
                
//#line 111 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int indexFromTop =
                  ((max) - (((int)(index))));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35109 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35105 =
                  ((x10.array.PlaceGroup)(pg));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35106 =
                  t35105.numPlaces$O();
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35107 =
                  ((t35106) - (((int)(1))));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35108 =
                  ((indexFromTop) / (((int)(blockSize))));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35110 =
                  ((t35107) - (((int)(t35108))));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35111 =
                  t35109.$apply((int)(t35110));
                
//#line 112 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35111;
            }
        }
        
        public static x10.lang.Place
          mapIndexToPlace$P(
          final int index,
          final x10.array.BlockDist BlockDist){
            return BlockDist.mapIndexToPlace((int)(index));
        }
        
        
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.array.PlaceGroup
                                                                                                    places(
                                                                                                    ){
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35113 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 117 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35113;
        }
        
        
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public int
                                                                                                    numPlaces$O(
                                                                                                    ){
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35114 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35115 =
              t35114.numPlaces$O();
            
//#line 119 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35115;
        }
        
        
//#line 121 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.lang.Sequence<x10.array.Region>
                                                                                                    regions(
                                                                                                    ){
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35116 =
              ((x10.array.PlaceGroup)(pg));
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35120 =
              t35116.numPlaces$O();
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.array.Region> t35121 =
              ((x10.core.fun.Fun_0_1)(new x10.array.BlockDist.$Closure$9(this,
                                                                         pg)));
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Array<x10.array.Region> t35122 =
              ((x10.array.Array)(new x10.array.Array<x10.array.Region>((java.lang.System[]) null, x10.array.Region.$RTT).$init(t35120,
                                                                                                                               ((x10.core.fun.Fun_0_1)(t35121)), (x10.array.Array.__1$1x10$lang$Int$3x10$array$Array$$T$2) null)));
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Sequence<x10.array.Region> t35123 =
              ((x10.lang.Sequence<x10.array.Region>)
                ((x10.array.Array<x10.array.Region>)t35122).sequence());
            
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35123;
        }
        
        
//#line 125 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.array.Region
                                                                                                    get(
                                                                                                    final x10.lang.Place p){
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35129 =
              x10.rtt.Equality.equalsequals((p),(x10.lang.Runtime.home()));
            
//#line 126 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35129) {
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35124 =
                  ((x10.array.Region)(regionForHere));
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35126 =
                  ((t35124) == (null));
                
//#line 127 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35126) {
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35125 =
                      ((x10.array.Region)(this.blockRegionForPlace(((x10.lang.Place)(x10.lang.Runtime.home())))));
                    
//#line 128 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
this.regionForHere = ((x10.array.Region)(t35125));
                }
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35127 =
                  ((x10.array.Region)(regionForHere));
                
//#line 130 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35127;
            } else {
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35128 =
                  ((x10.array.Region)(this.blockRegionForPlace(((x10.lang.Place)(p)))));
                
//#line 132 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35128;
            }
        }
        
        
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public boolean
                                                                                                    containsLocally$O(
                                                                                                    final x10.array.Point p){
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35130 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35131 =
              t35130.contains$O(((x10.array.Point)(p)));
            
//#line 136 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35131;
        }
        
        
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.array.Region
                                                                                                    $apply(
                                                                                                    final x10.lang.Place p){
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35132 =
              ((x10.array.Region)(this.get(((x10.lang.Place)(p)))));
            
//#line 140 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35132;
        }
        
        
//#line 142 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final x10.array.Point pt){
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35135 =
              true;
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35135) {
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35133 =
                  ((x10.array.Region)(region));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35134 =
                  t35133.contains$O(((x10.array.Point)(pt)));
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35135 = !(t35134);
            }
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35136 =
              t35135;
            
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35136) {
                
//#line 143 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
            }
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35137 =
              axis;
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35138 =
              pt.$apply$O((int)(t35137));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35139 =
              this.mapIndexToPlace((int)(t35138));
            
//#line 144 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35139;
        }
        
        
//#line 147 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final int i0){
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35143 =
              true;
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35143) {
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35141 =
                  ((x10.array.Region)(region));
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35142 =
                  t35141.contains$O((int)(i0));
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35143 = !(t35142);
            }
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35144 =
              t35143;
            
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35144) {
                
//#line 148 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0));
            }
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35145 =
              this.mapIndexToPlace((int)(i0));
            
//#line 149 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35145;
        }
        
        
//#line 152 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final int i0,
                                                                                                    final int i1){
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35149 =
              true;
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35149) {
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35147 =
                  ((x10.array.Region)(region));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35148 =
                  t35147.contains$O((int)(i0),
                                    (int)(i1));
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35149 = !(t35148);
            }
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35150 =
              t35149;
            
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35150) {
                
//#line 153 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                          (int)(i1));
            }
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35153 =
              axis;
            
//#line 154 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
switch (t35153) {
                
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 0:
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35151 =
                      this.mapIndexToPlace((int)(i0));
                    
//#line 155 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35151;
                
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 1:
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35152 =
                      this.mapIndexToPlace((int)(i1));
                    
//#line 156 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35152;
                
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
default:
                    
//#line 157 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return x10.lang.Runtime.home();
            }
        }
        
        
//#line 161 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2){
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35157 =
              true;
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35157) {
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35155 =
                  ((x10.array.Region)(region));
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35156 =
                  t35155.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2));
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35157 = !(t35156);
            }
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35158 =
              t35157;
            
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35158) {
                
//#line 162 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                          (int)(i1),
                                                                                                                                          (int)(i2));
            }
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35162 =
              axis;
            
//#line 163 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
switch (t35162) {
                
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 0:
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35159 =
                      this.mapIndexToPlace((int)(i0));
                    
//#line 164 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35159;
                
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 1:
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35160 =
                      this.mapIndexToPlace((int)(i1));
                    
//#line 165 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35160;
                
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 2:
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35161 =
                      this.mapIndexToPlace((int)(i2));
                    
//#line 166 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35161;
                
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
default:
                    
//#line 167 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return x10.lang.Runtime.home();
            }
        }
        
        
//#line 171 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.lang.Place
                                                                                                    $apply(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2,
                                                                                                    final int i3){
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35166 =
              true;
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35166) {
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35164 =
                  ((x10.array.Region)(region));
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35165 =
                  t35164.contains$O((int)(i0),
                                    (int)(i1),
                                    (int)(i2),
                                    (int)(i3));
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35166 = !(t35165);
            }
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35167 =
              t35166;
            
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35167) {
                
//#line 172 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                          (int)(i1),
                                                                                                                                          (int)(i2),
                                                                                                                                          (int)(i3));
            }
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35172 =
              axis;
            
//#line 173 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
switch (t35172) {
                
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 0:
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35168 =
                      this.mapIndexToPlace((int)(i0));
                    
//#line 174 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35168;
                
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 1:
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35169 =
                      this.mapIndexToPlace((int)(i1));
                    
//#line 175 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35169;
                
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 2:
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35170 =
                      this.mapIndexToPlace((int)(i2));
                    
//#line 176 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35170;
                
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
case 3:
                    
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35171 =
                      this.mapIndexToPlace((int)(i3));
                    
//#line 177 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35171;
                
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
default:
                    
//#line 178 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return x10.lang.Runtime.home();
            }
        }
        
        
//#line 182 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public int
                                                                                                    offset$O(
                                                                                                    final x10.array.Point pt){
            
//#line 183 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 184 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int offset =
              r.indexOf$O(((x10.array.Point)(pt)));
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35178 =
              ((int) offset) ==
            ((int) -1);
            
//#line 185 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35178) {
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35175 =
                  true;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35175) {
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35173 =
                      ((x10.array.Region)(region));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35174 =
                      t35173.contains$O(((x10.array.Point)(pt)));
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35175 = !(t35174);
                }
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35176 =
                  t35175;
                
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35176) {
                    
//#line 186 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError(((x10.array.Point)(pt)));
                }
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35177 =
                  true;
                
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35177) {
                    
//#line 187 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raisePlaceError(((x10.array.Point)(pt)));
                }
            }
            
//#line 189 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return offset;
        }
        
        
//#line 193 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public int
                                                                                                    offset$O(
                                                                                                    final int i0){
            
//#line 194 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 195 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0));
            
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35185 =
              ((int) offset) ==
            ((int) -1);
            
//#line 196 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35185) {
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35182 =
                  true;
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35182) {
                    
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35180 =
                      ((x10.array.Region)(region));
                    
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35181 =
                      t35180.contains$O((int)(i0));
                    
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35182 = !(t35181);
                }
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35183 =
                  t35182;
                
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35183) {
                    
//#line 197 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0));
                }
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35184 =
                  true;
                
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35184) {
                    
//#line 198 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0));
                }
            }
            
//#line 200 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return offset;
        }
        
        
//#line 203 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public int
                                                                                                    offset$O(
                                                                                                    final int i0,
                                                                                                    final int i1){
            
//#line 204 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 205 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0),
                          (int)(i1));
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35192 =
              ((int) offset) ==
            ((int) -1);
            
//#line 206 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35192) {
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35189 =
                  true;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35189) {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35187 =
                      ((x10.array.Region)(region));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35188 =
                      t35187.contains$O((int)(i0),
                                        (int)(i1));
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35189 = !(t35188);
                }
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35190 =
                  t35189;
                
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35190) {
                    
//#line 207 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                              (int)(i1));
                }
                
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35191 =
                  true;
                
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35191) {
                    
//#line 208 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                             (int)(i1));
                }
            }
            
//#line 210 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return offset;
        }
        
        
//#line 213 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public int
                                                                                                    offset$O(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2){
            
//#line 214 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 215 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0),
                          (int)(i1),
                          (int)(i2));
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35199 =
              ((int) offset) ==
            ((int) -1);
            
//#line 216 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35199) {
                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35196 =
                  true;
                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35196) {
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35194 =
                      ((x10.array.Region)(region));
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35195 =
                      t35194.contains$O((int)(i0),
                                        (int)(i1),
                                        (int)(i2));
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35196 = !(t35195);
                }
                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35197 =
                  t35196;
                
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35197) {
                    
//#line 217 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                              (int)(i1),
                                                                                                                                              (int)(i2));
                }
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35198 =
                  true;
                
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35198) {
                    
//#line 218 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                             (int)(i1),
                                                                                                                                             (int)(i2));
                }
            }
            
//#line 220 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return offset;
        }
        
        
//#line 223 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public int
                                                                                                    offset$O(
                                                                                                    final int i0,
                                                                                                    final int i1,
                                                                                                    final int i2,
                                                                                                    final int i3){
            
//#line 224 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 225 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int offset =
              r.indexOf$O((int)(i0),
                          (int)(i1),
                          (int)(i2),
                          (int)(i3));
            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35206 =
              ((int) offset) ==
            ((int) -1);
            
//#line 226 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35206) {
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35203 =
                  true;
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35203) {
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35201 =
                      ((x10.array.Region)(region));
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35202 =
                      t35201.contains$O((int)(i0),
                                        (int)(i1),
                                        (int)(i2),
                                        (int)(i3));
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35203 = !(t35202);
                }
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35204 =
                  t35203;
                
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35204) {
                    
//#line 227 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raiseBoundsError((int)(i0),
                                                                                                                                              (int)(i1),
                                                                                                                                              (int)(i2),
                                                                                                                                              (int)(i3));
                }
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35205 =
                  true;
                
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35205) {
                    
//#line 228 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
x10.array.Dist.raisePlaceError((int)(i0),
                                                                                                                                             (int)(i1),
                                                                                                                                             (int)(i2),
                                                                                                                                             (int)(i3));
                }
            }
            
//#line 230 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return offset;
        }
        
        
//#line 233 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public int
                                                                                                    maxOffset$O(
                                                                                                    ){
            
//#line 234 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region r =
              ((x10.array.Region)(this.get(((x10.lang.Place)(x10.lang.Runtime.home())))));
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35207 =
              r.size$O();
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35208 =
              ((t35207) - (((int)(1))));
            
//#line 235 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35208;
        }
        
        
//#line 238 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.array.Dist
                                                                                                    restriction(
                                                                                                    final x10.array.Region r){
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.WrappedDistRegionRestricted t35209 =
              ((x10.array.WrappedDistRegionRestricted)(new x10.array.WrappedDistRegionRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                  ((x10.array.Region)(r)))));
            
//#line 239 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35209;
        }
        
        
//#line 242 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public x10.array.Dist
                                                                                                    restriction(
                                                                                                    final x10.lang.Place p){
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.WrappedDistPlaceRestricted t35210 =
              ((x10.array.WrappedDistPlaceRestricted)(new x10.array.WrappedDistPlaceRestricted((java.lang.System[]) null).$init(((x10.array.Dist)(this)),
                                                                                                                                ((x10.lang.Place)(p)))));
            
//#line 243 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35210;
        }
        
        
//#line 246 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
public boolean
                                                                                                    equals(
                                                                                                    final java.lang.Object thatObj){
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35211 =
              x10.rtt.Equality.equalsequals((this),(thatObj));
            
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35211) {
                
//#line 247 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return true;
            }
            
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35212 =
              x10.array.BlockDist.$RTT.isInstance(thatObj);
            
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35214 =
              !(t35212);
            
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35214) {
                
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35213 =
                  super.equals(((java.lang.Object)(thatObj)));
                
//#line 248 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35213;
            }
            
//#line 249 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.BlockDist that =
              ((x10.array.BlockDist)(x10.rtt.Types.<x10.array.BlockDist> cast(thatObj,x10.array.BlockDist.$RTT)));
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35215 =
              this.
                axis;
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35216 =
              that.
                axis;
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
boolean t35219 =
              x10.rtt.Equality.equalsequals(t35215, ((int)(t35216)));
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
if (t35219) {
                
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35217 =
                  ((x10.array.Region)(this.
                                        region));
                
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35218 =
                  ((x10.array.Region)(that.
                                        region));
                
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
t35219 = t35217.equals(((java.lang.Object)(t35218)));
            }
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final boolean t35220 =
              t35219;
            
//#line 250 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35220;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final public x10.array.BlockDist
                                                                                                   x10$array$BlockDist$$x10$array$BlockDist$this(
                                                                                                   ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return x10.array.BlockDist.this;
        }
        
        
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final public void
                                                                                                   __fieldInitializers34858(
                                                                                                   ){
            
//#line 28 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
this.regionForHere = null;
        }
        
        @x10.core.X10Generated public static class $Closure$7 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$7.class);
            
            public static final x10.rtt.RuntimeType<$Closure$7> $RTT = x10.rtt.StaticFunType.<$Closure$7> make(
            /* base class */$Closure$7.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$7 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$7.class + " calling"); } 
                x10.array.BlockDist out$$ = (x10.array.BlockDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Region region = (x10.array.Region) $deserializer.readRef();
                $_obj.region = region;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$7 $_obj = new $Closure$7((java.lang.System[]) null);
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
            public $Closure$7(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35054 =
                      ((x10.array.Region)(this.
                                            region));
                    
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35055 =
                      t35054.min$O((int)(i));
                    
//#line 74 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35055;
                }
                
                public x10.array.BlockDist out$$;
                public x10.array.Region region;
                
                public $Closure$7(final x10.array.BlockDist out$$,
                                  final x10.array.Region region) { {
                                                                          this.out$$ = out$$;
                                                                          this.region = ((x10.array.Region)(region));
                                                                      }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$8 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$8.class);
            
            public static final x10.rtt.RuntimeType<$Closure$8> $RTT = x10.rtt.StaticFunType.<$Closure$8> make(
            /* base class */$Closure$8.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$8 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$8.class + " calling"); } 
                x10.array.BlockDist out$$ = (x10.array.BlockDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.Region region = (x10.array.Region) $deserializer.readRef();
                $_obj.region = region;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$8 $_obj = new $Closure$8((java.lang.System[]) null);
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
            public $Closure$8(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));
            }
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35058 =
                      ((x10.array.Region)(this.
                                            region));
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final int t35059 =
                      t35058.max$O((int)(i));
                    
//#line 75 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35059;
                }
                
                public x10.array.BlockDist out$$;
                public x10.array.Region region;
                
                public $Closure$8(final x10.array.BlockDist out$$,
                                  final x10.array.Region region) { {
                                                                          this.out$$ = out$$;
                                                                          this.region = ((x10.array.Region)(region));
                                                                      }}
                
            }
            
        @x10.core.X10Generated public static class $Closure$9 extends x10.core.Ref implements x10.core.fun.Fun_0_1, x10.x10rt.X10JavaSerializable
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$9.class);
            
            public static final x10.rtt.RuntimeType<$Closure$9> $RTT = x10.rtt.StaticFunType.<$Closure$9> make(
            /* base class */$Closure$9.class
            , /* parents */ new x10.rtt.Type[] {x10.rtt.ParameterizedType.make(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.array.Region.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException { if (x10.runtime.impl.java.Runtime.TRACE_SER) { java.lang.System.out.println("Serializer: writeObject(ObjectOutputStream) of " + this + " calling"); } oos.defaultWriteObject(); }
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$9 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                if (x10.runtime.impl.java.Runtime.TRACE_SER) { x10.runtime.impl.java.Runtime.printTraceMessage("X10JavaSerializable: $_deserialize_body() of " + $Closure$9.class + " calling"); } 
                x10.array.BlockDist out$$ = (x10.array.BlockDist) $deserializer.readRef();
                $_obj.out$$ = out$$;
                x10.array.PlaceGroup pg = (x10.array.PlaceGroup) $deserializer.readRef();
                $_obj.pg = pg;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$9 $_obj = new $Closure$9((java.lang.System[]) null);
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
                if (pg instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.pg);
                } else {
                $serializer.write(this.pg);
                }
                
            }
            
            // constructor just for allocation
            public $Closure$9(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object $apply(final java.lang.Object a1, final x10.rtt.Type t1) {
            return $apply(x10.core.Int.$unbox(a1));
            }
            
                
                public x10.array.Region
                  $apply(
                  final int i){
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.PlaceGroup t35117 =
                      ((x10.array.PlaceGroup)(this.
                                                pg));
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.lang.Place t35118 =
                      t35117.$apply((int)(i));
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
final x10.array.Region t35119 =
                      ((x10.array.Region)(this.
                                            out$$.blockRegionForPlace(((x10.lang.Place)(t35118)))));
                    
//#line 122 "/home/lshadare/x10-constraints/x10.runtime/src-x10/x10/array/BlockDist.x10"
return t35119;
                }
                
                public x10.array.BlockDist out$$;
                public x10.array.PlaceGroup pg;
                
                public $Closure$9(final x10.array.BlockDist out$$,
                                  final x10.array.PlaceGroup pg) { {
                                                                          this.out$$ = out$$;
                                                                          this.pg = ((x10.array.PlaceGroup)(pg));
                                                                      }}
                
            }
            
        
        public boolean
          x10$array$Dist$equals$S$O(
          final java.lang.Object a0){
            return super.equals(((java.lang.Object)(a0)));
        }
        
        }
        