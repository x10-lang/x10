package x10.array;

public class TriangularRegion
extends x10.array.Region implements x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, TriangularRegion.class);
    
    public static final x10.rtt.RuntimeType<TriangularRegion> $RTT = new x10.rtt.NamedType<TriangularRegion>(
    "x10.array.TriangularRegion", /* base class */TriangularRegion.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(TriangularRegion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10.array.Region.$_deserialize_body($_obj, $deserializer);
        $_obj.dim = $deserializer.readInt();
        $_obj.rowMin = $deserializer.readInt();
        $_obj.colMin = $deserializer.readInt();
        $_obj.lower = $deserializer.readBoolean();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        TriangularRegion $_obj = new TriangularRegion((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.dim);
        $serializer.write(this.rowMin);
        $serializer.write(this.colMin);
        $serializer.write(this.lower);
        
    }
    
    // constructor just for allocation
    public TriangularRegion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
        
//#line 7 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
          dim;
        
//#line 8 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
          rowMin;
        
//#line 9 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
          colMin;
        
//#line 10 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public boolean
          lower;
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
// creation method for java code
        public static x10.array.TriangularRegion $make(final int rowMin,
                                                       final int colMin,
                                                       final int size,
                                                       final boolean lower){return new x10.array.TriangularRegion((java.lang.System[]) null).$init(rowMin,colMin,size,lower);}
        
        // constructor for non-virtual call
        final public x10.array.TriangularRegion x10$array$TriangularRegion$$init$S(final int rowMin,
                                                                                   final int colMin,
                                                                                   final int size,
                                                                                   final boolean lower) { {
                                                                                                                 
//#line 17 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region this73190 =
                                                                                                                   ((x10.array.Region)(this));
                                                                                                                 
//#line 469 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73190.x10$lang$Object$$init$S();
                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73190.rank = 2;
                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73190.rect = false;
                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73190.zeroBased = true;
                                                                                                                 
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73190.rail = false;
                                                                                                                 
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"

                                                                                                                 
//#line 18 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.dim = size;
                                                                                                                 
//#line 19 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.rowMin = rowMin;
                                                                                                                 
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.colMin = colMin;
                                                                                                                 
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.lower = lower;
                                                                                                             }
                                                                                                             return this;
                                                                                                             }
        
        // constructor
        public x10.array.TriangularRegion $init(final int rowMin,
                                                final int colMin,
                                                final int size,
                                                final boolean lower){return x10$array$TriangularRegion$$init$S(rowMin,colMin,size,lower);}
        
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public boolean
                                                                                                             isConvex$O(
                                                                                                             ){
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return true;
        }
        
        
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public boolean
                                                                                                             isEmpty$O(
                                                                                                             ){
            
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return false;
        }
        
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
                                                                                                             indexOf$O(
                                                                                                             final x10.array.Point pt){
            
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73223 =
              pt.
                rank;
            
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73224 =
              ((int) t73223) !=
            ((int) 2);
            
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73224) {
                
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return -1;
            }
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73225 =
              pt.$apply$O((int)(0));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73226 =
              pt.$apply$O((int)(0));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73227 =
              ((t73225) * (((int)(t73226))));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73228 =
              ((t73227) / (((int)(2))));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73229 =
              pt.$apply$O((int)(1));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73230 =
              ((t73228) + (((int)(t73229))));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73230;
        }
        
        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                             min(
                                                                                                             ){
            
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t73239 =
              ((x10.core.fun.Fun_0_1)(new x10.array.TriangularRegion.$Closure$77(this,
                                                                                 rowMin,
                                                                                 colMin)));
            
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73239;
        }
        
        
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                             max(
                                                                                                             ){
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t73252 =
              ((x10.core.fun.Fun_0_1)(new x10.array.TriangularRegion.$Closure$78(this,
                                                                                 rowMin,
                                                                                 dim,
                                                                                 colMin)));
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73252;
        }
        
        
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
                                                                                                             size$O(
                                                                                                             ){
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73254 =
              dim;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73253 =
              dim;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73255 =
              ((t73253) + (((int)(1))));
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73256 =
              ((t73254) * (((int)(t73255))));
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73257 =
              ((t73256) / (((int)(2))));
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73257;
        }
        
        
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public boolean
                                                                                                             contains$O(
                                                                                                             final x10.array.Point p){
            
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73258 =
              p.
                rank;
            
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73282 =
              ((int) t73258) ==
            ((int) 2);
            
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73282) {
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73259 =
                  p.$apply$O((int)(0));
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73260 =
                  rowMin;
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
boolean t73265 =
                  ((t73259) >= (((int)(t73260))));
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73265) {
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73263 =
                      p.$apply$O((int)(0));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73261 =
                      rowMin;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73262 =
                      dim;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73264 =
                      ((t73261) + (((int)(t73262))));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
t73265 = ((t73263) <= (((int)(t73264))));
                }
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73281 =
                  t73265;
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73281) {
                    
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73280 =
                      lower;
                    
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73280) {
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73266 =
                          p.$apply$O((int)(1));
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73267 =
                          colMin;
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
boolean t73270 =
                          ((t73266) >= (((int)(t73267))));
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73270) {
                            
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73268 =
                              p.$apply$O((int)(1));
                            
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73269 =
                              p.$apply$O((int)(0));
                            
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
t73270 = ((t73268) <= (((int)(t73269))));
                        }
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73271 =
                          t73270;
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73271) {
                            
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return true;
                        }
                    } else {
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73274 =
                          p.$apply$O((int)(1));
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73272 =
                          colMin;
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73273 =
                          dim;
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73275 =
                          ((t73272) + (((int)(t73273))));
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
boolean t73278 =
                          ((t73274) <= (((int)(t73275))));
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73278) {
                            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73276 =
                              p.$apply$O((int)(1));
                            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73277 =
                              p.$apply$O((int)(0));
                            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
t73278 = ((t73276) >= (((int)(t73277))));
                        }
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73279 =
                          t73278;
                        
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73279) {
                            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return true;
                        }
                    }
                }
                
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return false;
            }
            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73283 =
              (("contains(") + (p));
            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73284 =
              ((t73283) + (")"));
            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.UnsupportedOperationException t73285 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t73284)));
            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73285;
        }
        
        
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public boolean
                                                                                                             contains$O(
                                                                                                             final x10.array.Region r){
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.UnsupportedOperationException t73286 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("contains(Region)")))));
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73286;
        }
        
        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                             complement(
                                                                                                             ){
            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.UnsupportedOperationException t73287 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("complement()")))));
            
//#line 79 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73287;
        }
        
        
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                             intersection(
                                                                                                             final x10.array.Region t){
            
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.UnsupportedOperationException t73288 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("intersection()")))));
            
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73288;
        }
        
        
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                             product(
                                                                                                             final x10.array.Region r){
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.UnsupportedOperationException t73289 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("product()")))));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73289;
        }
        
        
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                             translate(
                                                                                                             final x10.array.Point v){
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion alloc71935 =
              ((x10.array.TriangularRegion)(new x10.array.TriangularRegion((java.lang.System[]) null)));
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73290 =
              rowMin;
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73291 =
              v.$apply$O((int)(0));
            
//#line 16 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int rowMin73192 =
              ((t73290) + (((int)(t73291))));
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73292 =
              colMin;
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73293 =
              v.$apply$O((int)(1));
            
//#line 16 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int colMin73193 =
              ((t73292) + (((int)(t73293))));
            
//#line 16 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int size73194 =
              dim;
            
//#line 16 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean lower73195 =
              lower;
            
//#line 469 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
alloc71935.x10$lang$Object$$init$S();
            
//#line 472 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
alloc71935.rank = 2;
            
//#line 472 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
alloc71935.rect = false;
            
//#line 472 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
alloc71935.zeroBased = true;
            
//#line 472 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
alloc71935.rail = false;
            
//#line 18 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
alloc71935.dim = size73194;
            
//#line 19 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
alloc71935.rowMin = rowMin73192;
            
//#line 20 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
alloc71935.colMin = colMin73193;
            
//#line 21 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
alloc71935.lower = lower73195;
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region __desugarer__var__42__73202 =
              ((x10.array.Region)(((x10.array.Region)
                                    alloc71935)));
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
x10.array.Region ret73203 =
               null;
            
//#line 93 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t7329473432 =
              __desugarer__var__42__73202.
                rank;
            
//#line 93 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t7329573433 =
              x10.array.TriangularRegion.this.
                rank;
            
//#line 93 . "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7329673434 =
              ((int) t7329473432) ==
            ((int) t7329573433);
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7329973435 =
              !(t7329673434);
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t7329973435) {
                
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7329873436 =
                  true;
                
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t7329873436) {
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.FailedDynamicCheckException t7329773437 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.TriangularRegion).rank}");
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t7329773437;
                }
            }
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
ret73203 = ((x10.array.Region)(__desugarer__var__42__73202));
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73300 =
              ((x10.array.Region)(ret73203));
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73300;
        }
        
        
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                             projection(
                                                                                                             final int axis){
            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
switch (axis) {
                
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
case 0:
                    
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73303 =
                      rowMin;
                    
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73301 =
                      rowMin;
                    
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73302 =
                      dim;
                    
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73304 =
                      ((t73301) + (((int)(t73302))));
                    
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73305 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73303)), ((int)(t73304)))));
                    
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73306 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73305)))));
                    
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73306;
                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
case 1:
                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73309 =
                      colMin;
                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73307 =
                      colMin;
                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73308 =
                      dim;
                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73310 =
                      ((t73307) + (((int)(t73308))));
                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73311 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73309)), ((int)(t73310)))));
                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73312 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73311)))));
                    
//#line 101 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73312;
                
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
default:
                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73313 =
                      (("projection(") + ((x10.core.Int.$box(axis))));
                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73314 =
                      ((t73313) + (")"));
                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.UnsupportedOperationException t73315 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t73314)));
                    
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73315;
            }
        }
        
        
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                              eliminate(
                                                                                                              final int axis){
            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
switch (axis) {
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
case 0:
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73318 =
                      colMin;
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73316 =
                      colMin;
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73317 =
                      dim;
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73319 =
                      ((t73316) + (((int)(t73317))));
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73320 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73318)), ((int)(t73319)))));
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73321 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73320)))));
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73321;
                
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
case 1:
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73324 =
                      rowMin;
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73322 =
                      rowMin;
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73323 =
                      dim;
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73325 =
                      ((t73322) + (((int)(t73323))));
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73326 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73324)), ((int)(t73325)))));
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73327 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73326)))));
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73327;
                
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
default:
                    
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73328 =
                      (("projection(") + ((x10.core.Int.$box(axis))));
                    
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73329 =
                      ((t73328) + (")"));
                    
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.UnsupportedOperationException t73330 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t73329)));
                    
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73330;
            }
        }
        
        
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                              boundingBox(
                                                                                                              ){
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73333 =
              rowMin;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73331 =
              rowMin;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73332 =
              dim;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73334 =
              ((t73331) + (((int)(t73332))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73339 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73333)), ((int)(t73334)))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73337 =
              colMin;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73335 =
              colMin;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73336 =
              dim;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73338 =
              ((t73335) + (((int)(t73336))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73340 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73337)), ((int)(t73338)))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73341 =
              ((x10.array.Region)(t73339.$times(((x10.lang.IntRange)(t73340)))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region __desugarer__var__43__73205 =
              ((x10.array.Region)(((x10.array.Region)
                                    t73341)));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
x10.array.Region ret73206 =
               null;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t7334273438 =
              __desugarer__var__43__73205.
                rank;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t7334373439 =
              x10.array.TriangularRegion.this.
                rank;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7334473440 =
              ((int) t7334273438) ==
            ((int) t7334373439);
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7334773441 =
              !(t7334473440);
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t7334773441) {
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7334673442 =
                  true;
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t7334673442) {
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.FailedDynamicCheckException t7334573443 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.TriangularRegion).rank}");
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t7334573443;
                }
            }
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
ret73206 = ((x10.array.Region)(__desugarer__var__43__73205));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73348 =
              ((x10.array.Region)(ret73206));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73348;
        }
        
        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.Region
                                                                                                              computeBoundingBox(
                                                                                                              ){
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73351 =
              rowMin;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73349 =
              rowMin;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73350 =
              dim;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73352 =
              ((t73349) + (((int)(t73350))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73357 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73351)), ((int)(t73352)))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73355 =
              colMin;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73353 =
              colMin;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73354 =
              dim;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73356 =
              ((t73353) + (((int)(t73354))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.IntRange t73358 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73355)), ((int)(t73356)))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73359 =
              ((x10.array.Region)(t73357.$times(((x10.lang.IntRange)(t73358)))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region __desugarer__var__44__73208 =
              ((x10.array.Region)(((x10.array.Region)
                                    t73359)));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
x10.array.Region ret73209 =
               null;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t7336073444 =
              __desugarer__var__44__73208.
                rank;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t7336173445 =
              x10.array.TriangularRegion.this.
                rank;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7336273446 =
              ((int) t7336073444) ==
            ((int) t7336173445);
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7336573447 =
              !(t7336273446);
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t7336573447) {
                
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t7336473448 =
                  true;
                
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t7336473448) {
                    
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.FailedDynamicCheckException t7336373449 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:x10.array.TriangularRegion).rank}");
                    
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t7336373449;
                }
            }
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
ret73209 = ((x10.array.Region)(__desugarer__var__44__73208));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Region t73366 =
              ((x10.array.Region)(ret73209));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73366;
        }
        
        
//#line 126 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                              iterator(
                                                                                                              ){
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion.TriangularRegionIterator alloc71936 =
              ((x10.array.TriangularRegion.TriangularRegionIterator)(new x10.array.TriangularRegion.TriangularRegionIterator((java.lang.System[]) null)));
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
alloc71936.$init(this,
                                                                                                                                 ((x10.array.TriangularRegion)(this)));
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.Iterator<x10.array.Point> t73367 =
              x10.rtt.Types.<x10.lang.Iterator<x10.array.Point>> cast(alloc71936,new x10.rtt.ParameterizedType(x10.lang.Iterator.$RTT, x10.array.Point.$RTT));
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73367;
        }
        
        
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public static class TriangularRegionIterator
                                                                                                            extends x10.core.Ref
                                                                                                              implements x10.lang.Iterator,
                                                                                                                          x10.x10rt.X10JavaSerializable 
                                                                                                            {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, TriangularRegionIterator.class);
            
            public static final x10.rtt.RuntimeType<TriangularRegionIterator> $RTT = new x10.rtt.NamedType<TriangularRegionIterator>(
            "x10.array.TriangularRegion.TriangularRegionIterator", /* base class */TriangularRegionIterator.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.lang.Iterator.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(TriangularRegionIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $_obj.dim = $deserializer.readInt();
                $_obj.lower = $deserializer.readBoolean();
                $_obj.i = $deserializer.readInt();
                $_obj.j = $deserializer.readInt();
                x10.array.TriangularRegion out$ = (x10.array.TriangularRegion) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                TriangularRegionIterator $_obj = new TriangularRegionIterator((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.dim);
                $serializer.write(this.lower);
                $serializer.write(this.i);
                $serializer.write(this.j);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public TriangularRegionIterator(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            final public x10.array.Point
              next$G(){return next();}
            
                
//#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public x10.array.TriangularRegion
                  out$;
                
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
                  dim;
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public boolean
                  lower;
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
                  i;
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public int
                  j;
                
                
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
// creation method for java code
                public static x10.array.TriangularRegion.TriangularRegionIterator $make(final x10.array.TriangularRegion out$,
                                                                                        final x10.array.TriangularRegion r){return new x10.array.TriangularRegion.TriangularRegionIterator((java.lang.System[]) null).$init(out$,r);}
                
                // constructor for non-virtual call
                final public x10.array.TriangularRegion.TriangularRegionIterator x10$array$TriangularRegion$TriangularRegionIterator$$init$S(final x10.array.TriangularRegion out$,
                                                                                                                                             final x10.array.TriangularRegion r) { {
                                                                                                                                                                                          
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"

                                                                                                                                                                                          
//#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.out$ = out$;
                                                                                                                                                                                          
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"

                                                                                                                                                                                          
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion.TriangularRegionIterator this7321173450 =
                                                                                                                                                                                            this;
                                                                                                                                                                                          
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this7321173450.i = 0;
                                                                                                                                                                                          
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this7321173450.j = 0;
                                                                                                                                                                                          
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73368 =
                                                                                                                                                                                            r.
                                                                                                                                                                                              dim;
                                                                                                                                                                                          
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.dim = t73368;
                                                                                                                                                                                          
//#line 138 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73369 =
                                                                                                                                                                                            r.
                                                                                                                                                                                              lower;
                                                                                                                                                                                          
//#line 138 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.lower = t73369;
                                                                                                                                                                                          
//#line 139 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73370 =
                                                                                                                                                                                            r.
                                                                                                                                                                                              rowMin;
                                                                                                                                                                                          
//#line 139 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.i = t73370;
                                                                                                                                                                                          
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73373 =
                                                                                                                                                                                            r.
                                                                                                                                                                                              lower;
                                                                                                                                                                                          
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
int t73374 =
                                                                                                                                                                                             0;
                                                                                                                                                                                          
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73373) {
                                                                                                                                                                                              
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
t73374 = r.
                                                                                                                                                                                                                                                                                                             colMin;
                                                                                                                                                                                          } else {
                                                                                                                                                                                              
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73371 =
                                                                                                                                                                                                r.
                                                                                                                                                                                                  colMin;
                                                                                                                                                                                              
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73372 =
                                                                                                                                                                                                r.
                                                                                                                                                                                                  dim;
                                                                                                                                                                                              
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
t73374 = ((t73371) + (((int)(t73372))));
                                                                                                                                                                                          }
                                                                                                                                                                                          
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73375 =
                                                                                                                                                                                            t73374;
                                                                                                                                                                                          
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.j = t73375;
                                                                                                                                                                                      }
                                                                                                                                                                                      return this;
                                                                                                                                                                                      }
                
                // constructor
                public x10.array.TriangularRegion.TriangularRegionIterator $init(final x10.array.TriangularRegion out$,
                                                                                 final x10.array.TriangularRegion r){return x10$array$TriangularRegion$TriangularRegionIterator$$init$S(out$,r);}
                
                
                
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final public boolean
                                                                                                                      hasNext$O(
                                                                                                                      ){
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73377 =
                      i;
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion t73376 =
                      this.
                        out$;
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73378 =
                      t73376.
                        rowMin;
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73379 =
                      ((t73377) - (((int)(t73378))));
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73380 =
                      dim;
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
boolean t73386 =
                      ((t73379) <= (((int)(t73380))));
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73386) {
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73382 =
                          j;
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion t73381 =
                          this.
                            out$;
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73383 =
                          t73381.
                            colMin;
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73384 =
                          ((t73382) - (((int)(t73383))));
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73385 =
                          dim;
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
t73386 = ((t73384) <= (((int)(t73385))));
                    }
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73387 =
                      t73386;
                    
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73387) {
                        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return true;
                    } else {
                        
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return false;
                    }
                }
                
                
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final public x10.array.Point
                                                                                                                      next(
                                                                                                                      ){
                    
//#line 149 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73388 =
                      i;
                    
//#line 149 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73389 =
                      j;
                    
//#line 132 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Array<x10.core.Int> a73214 =
                      ((x10.array.Array)(x10.core.ArrayFactory.<x10.core.Int> makeArrayFromJavaArray(x10.rtt.Types.INT, new int[] {t73388, t73389})));
                    
//#line 149 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.Point nextPoint =
                      ((x10.array.Point)(x10.array.Point.<x10.core.Int>make_0_$_x10$array$Point_T_$(x10.rtt.Types.INT, ((x10.array.Array)(a73214)))));
                    
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73413 =
                      lower;
                    
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73413) {
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73393 =
                          j;
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion t73390 =
                          this.
                            out$;
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73391 =
                          t73390.
                            colMin;
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73392 =
                          dim;
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73394 =
                          ((t73391) + (((int)(t73392))));
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73403 =
                          ((t73393) < (((int)(t73394))));
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73403) {
                            
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion.TriangularRegionIterator x73215 =
                              ((x10.array.TriangularRegion.TriangularRegionIterator)(this));
                            
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73395 =
                              x73215.
                                j;
                            
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73396 =
                              ((t73395) + (((int)(1))));
                            
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
x73215.j = t73396;
                        } else {
                            
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion.TriangularRegionIterator x73217 =
                              ((x10.array.TriangularRegion.TriangularRegionIterator)(this));
                            
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73397 =
                              x73217.
                                i;
                            
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73398 =
                              ((t73397) + (((int)(1))));
                            
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
x73217.i = t73398;
                            
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion t73399 =
                              this.
                                out$;
                            
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73400 =
                              t73399.
                                colMin;
                            
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73401 =
                              i;
                            
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73402 =
                              ((t73400) + (((int)(t73401))));
                            
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.j = t73402;
                        }
                    } else {
                        
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73404 =
                          j;
                        
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73405 =
                          i;
                        
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73412 =
                          ((t73404) < (((int)(t73405))));
                        
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73412) {
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion.TriangularRegionIterator x73219 =
                              ((x10.array.TriangularRegion.TriangularRegionIterator)(this));
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73406 =
                              x73219.
                                j;
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73407 =
                              ((t73406) + (((int)(1))));
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
x73219.j = t73407;
                        } else {
                            
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion.TriangularRegionIterator x73221 =
                              ((x10.array.TriangularRegion.TriangularRegionIterator)(this));
                            
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73408 =
                              x73221.
                                i;
                            
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73409 =
                              ((t73408) + (((int)(1))));
                            
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
x73221.i = t73409;
                            
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion t73410 =
                              this.
                                out$;
                            
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73411 =
                              t73410.
                                colMin;
                            
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.j = t73411;
                        }
                    }
                    
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return nextPoint;
                }
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final public x10.array.TriangularRegion.TriangularRegionIterator
                                                                                                                      x10$array$TriangularRegion$TriangularRegionIterator$$x10$array$TriangularRegion$TriangularRegionIterator$this(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return x10.array.TriangularRegion.TriangularRegionIterator.this;
                }
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final public x10.array.TriangularRegion
                                                                                                                      x10$array$TriangularRegion$TriangularRegionIterator$$x10$array$TriangularRegion$this(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.array.TriangularRegion t73414 =
                      this.
                        out$;
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73414;
                }
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final private void
                                                                                                                      __fieldInitializers71180(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.i = 0;
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
this.j = 0;
                }
                
                final public static void
                  __fieldInitializers71180$P(
                  final x10.array.TriangularRegion.TriangularRegionIterator TriangularRegionIterator){
                    TriangularRegionIterator.__fieldInitializers71180();
                }
            
        }
        
        
        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
public java.lang.String
                                                                                                              toString(
                                                                                                              ){
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73415 =
              colMin;
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73416 =
              (("triangular region ") + ((x10.core.Int.$box(t73415))));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73419 =
              ((t73416) + (".."));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73417 =
              colMin;
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73418 =
              dim;
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73420 =
              ((t73417) + (((int)(t73418))));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73421 =
              ((t73419) + ((x10.core.Int.$box(t73420))));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73422 =
              ((t73421) + (","));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73423 =
              rowMin;
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73424 =
              ((t73422) + ((x10.core.Int.$box(t73423))));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73427 =
              ((t73424) + (".."));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73425 =
              rowMin;
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73426 =
              dim;
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73428 =
              ((t73425) + (((int)(t73426))));
            
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String triangleString =
              ((t73427) + ((x10.core.Int.$box(t73428))));
            
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73431 =
              lower;
            
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73431) {
                
//#line 170 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73429 =
                  (("lower ") + (triangleString));
                
//#line 170 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73429;
            } else {
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73430 =
                  (("upper ") + (triangleString));
                
//#line 172 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73430;
            }
        }
        
        
//#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final public x10.array.TriangularRegion
                                                                                                            x10$array$TriangularRegion$$x10$array$TriangularRegion$this(
                                                                                                            ){
            
//#line 3 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return x10.array.TriangularRegion.this;
        }
        
        public static class $Closure$77
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$77.class);
            
            public static final x10.rtt.RuntimeType<$Closure$77> $RTT = new x10.rtt.StaticFunType<$Closure$77>(
            /* base class */$Closure$77.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$77 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.array.TriangularRegion out$$ = (x10.array.TriangularRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rowMin = $deserializer.readInt();
                $_obj.colMin = $deserializer.readInt();
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
            
                if (out$$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$$);
                } else {
                $serializer.write(this.out$$);
                }
                $serializer.write(this.rowMin);
                $serializer.write(this.colMin);
                
            }
            
            // constructor just for allocation
            public $Closure$77(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));}
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73238 =
                      ((int) i) ==
                    ((int) 0);
                    
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73238) {
                        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73231 =
                          this.
                            rowMin;
                        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73231;
                    } else {
                        
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73237 =
                          ((int) i) ==
                        ((int) 1);
                        
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73237) {
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73232 =
                              this.
                                colMin;
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73232;
                        } else {
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73233 =
                              (("min: ") + ((x10.core.Int.$box(i))));
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73234 =
                              ((t73233) + (" is not a valid rank for "));
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73235 =
                              ((t73234) + (this.
                                             out$$));
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t73236 =
                              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t73235)));
                            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73236;
                        }
                    }
                }
                
                public x10.array.TriangularRegion
                  out$$;
                public int
                  rowMin;
                public int
                  colMin;
                
                // creation method for java code
                public static x10.array.TriangularRegion.$Closure$77 $make(final x10.array.TriangularRegion out$$,
                                                                           final int rowMin,
                                                                           final int colMin){return new $Closure$77(out$$,rowMin,colMin);}
                public $Closure$77(final x10.array.TriangularRegion out$$,
                                   final int rowMin,
                                   final int colMin) { {
                                                              this.out$$ = out$$;
                                                              this.rowMin = rowMin;
                                                              this.colMin = colMin;
                                                          }}
                
            }
            
        public static class $Closure$78
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$78.class);
            
            public static final x10.rtt.RuntimeType<$Closure$78> $RTT = new x10.rtt.StaticFunType<$Closure$78>(
            /* base class */$Closure$78.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$78 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                x10.array.TriangularRegion out$$ = (x10.array.TriangularRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.rowMin = $deserializer.readInt();
                $_obj.dim = $deserializer.readInt();
                $_obj.colMin = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$78 $_obj = new $Closure$78((java.lang.System[]) null);
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
                $serializer.write(this.rowMin);
                $serializer.write(this.dim);
                $serializer.write(this.colMin);
                
            }
            
            // constructor just for allocation
            public $Closure$78(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));}
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73251 =
                      ((int) i) ==
                    ((int) 0);
                    
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73251) {
                        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73240 =
                          this.
                            rowMin;
                        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73241 =
                          this.
                            dim;
                        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73242 =
                          ((t73240) + (((int)(t73241))));
                        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73242;
                    } else {
                        
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final boolean t73250 =
                          ((int) i) ==
                        ((int) 1);
                        
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
if (t73250) {
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73243 =
                              this.
                                colMin;
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73244 =
                              this.
                                dim;
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final int t73245 =
                              ((t73243) + (((int)(t73244))));
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
return t73245;
                        } else {
                            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73246 =
                              (("max: ") + ((x10.core.Int.$box(i))));
                            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73247 =
                              ((t73246) + (" is not a valid rank for "));
                            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final java.lang.String t73248 =
                              ((t73247) + (this.
                                             out$$));
                            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t73249 =
                              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t73248)));
                            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/x10.runtime/src/x10/array/TriangularRegion.x10"
throw t73249;
                        }
                    }
                }
                
                public x10.array.TriangularRegion
                  out$$;
                public int
                  rowMin;
                public int
                  dim;
                public int
                  colMin;
                
                // creation method for java code
                public static x10.array.TriangularRegion.$Closure$78 $make(final x10.array.TriangularRegion out$$,
                                                                           final int rowMin,
                                                                           final int dim,
                                                                           final int colMin){return new $Closure$78(out$$,rowMin,dim,colMin);}
                public $Closure$78(final x10.array.TriangularRegion out$$,
                                   final int rowMin,
                                   final int dim,
                                   final int colMin) { {
                                                              this.out$$ = out$$;
                                                              this.rowMin = rowMin;
                                                              this.dim = dim;
                                                              this.colMin = colMin;
                                                          }}
                
            }
            
        
        }
        