package au.edu.anu.mm;


public class MultipoleExpansion
extends au.edu.anu.mm.Expansion
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, MultipoleExpansion.class);
    
    public static final x10.rtt.RuntimeType<MultipoleExpansion> $RTT = new x10.rtt.NamedType<MultipoleExpansion>(
    "au.edu.anu.mm.MultipoleExpansion", /* base class */MultipoleExpansion.class
    , /* parents */ new x10.rtt.Type[] {au.edu.anu.mm.Expansion.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(MultipoleExpansion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        au.edu.anu.mm.Expansion.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        MultipoleExpansion $_obj = new MultipoleExpansion((java.lang.System[]) null);
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
    public MultipoleExpansion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public int
          X10$object_lock_id0;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                getOrderedLock(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t60735 =
              this.
                X10$object_lock_id0;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t60736 =
              x10.util.concurrent.OrderedLock.getLock((int)(t60735));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
return t60736;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                getStaticOrderedLock(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t60737 =
              au.edu.anu.mm.MultipoleExpansion.X10$class_lock_id1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t60738 =
              x10.util.concurrent.OrderedLock.getLock((int)(t60737));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
return t60738;
        }
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.MultipoleExpansion $make(final int p){return new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null).$init(p);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.MultipoleExpansion au$edu$anu$mm$MultipoleExpansion$$init$S(final int p) { {
                                                                                                                     
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
super.$init(((int)(p)));
                                                                                                                     
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"

                                                                                                                     
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion this5897461373 =
                                                                                                                       this;
                                                                                                                     
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this5897461373.X10$object_lock_id0 = -1;
                                                                                                                 }
                                                                                                                 return this;
                                                                                                                 }
        
        // constructor
        public au.edu.anu.mm.MultipoleExpansion $init(final int p){return au$edu$anu$mm$MultipoleExpansion$$init$S(p);}
        
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.MultipoleExpansion $make(final int p,
                                                             final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null).$init(p,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.MultipoleExpansion au$edu$anu$mm$MultipoleExpansion$$init$S(final int p,
                                                                                               final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                         
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
super.$init(((int)(p)));
                                                                                                                                                         
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"

                                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion this5897761374 =
                                                                                                                                                           this;
                                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this5897761374.X10$object_lock_id0 = -1;
                                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t60739 =
                                                                                                                                                           paramLock.getIndex();
                                                                                                                                                         
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this.X10$object_lock_id0 = ((int)(t60739));
                                                                                                                                                     }
                                                                                                                                                     return this;
                                                                                                                                                     }
        
        // constructor
        public au.edu.anu.mm.MultipoleExpansion $init(final int p,
                                                      final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$MultipoleExpansion$$init$S(p,paramLock);}
        
        
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.MultipoleExpansion $make(final au.edu.anu.mm.MultipoleExpansion source){return new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null).$init(source);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.MultipoleExpansion au$edu$anu$mm$MultipoleExpansion$$init$S(final au.edu.anu.mm.MultipoleExpansion source) { {
                                                                                                                                                       
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
super.$init(((au.edu.anu.mm.Expansion)(source)));
                                                                                                                                                       
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"

                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion this5898061375 =
                                                                                                                                                         this;
                                                                                                                                                       
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this5898061375.X10$object_lock_id0 = -1;
                                                                                                                                                   }
                                                                                                                                                   return this;
                                                                                                                                                   }
        
        // constructor
        public au.edu.anu.mm.MultipoleExpansion $init(final au.edu.anu.mm.MultipoleExpansion source){return au$edu$anu$mm$MultipoleExpansion$$init$S(source);}
        
        
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.MultipoleExpansion $make(final au.edu.anu.mm.MultipoleExpansion source,
                                                             final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null).$init(source,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.MultipoleExpansion au$edu$anu$mm$MultipoleExpansion$$init$S(final au.edu.anu.mm.MultipoleExpansion source,
                                                                                               final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                         
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
super.$init(((au.edu.anu.mm.Expansion)(source)));
                                                                                                                                                         
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"

                                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion this5898361376 =
                                                                                                                                                           this;
                                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this5898361376.X10$object_lock_id0 = -1;
                                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t60740 =
                                                                                                                                                           paramLock.getIndex();
                                                                                                                                                         
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this.X10$object_lock_id0 = ((int)(t60740));
                                                                                                                                                     }
                                                                                                                                                     return this;
                                                                                                                                                     }
        
        // constructor
        public au.edu.anu.mm.MultipoleExpansion $init(final au.edu.anu.mm.MultipoleExpansion source,
                                                      final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$MultipoleExpansion$$init$S(source,paramLock);}
        
        
        
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public static au.edu.anu.mm.MultipoleExpansion
                                                                                                                getOlm(
                                                                                                                final double q,
                                                                                                                final x10x.vector.Tuple3d v,
                                                                                                                final int p){
            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion exp =
              ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t6074161585 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
exp.$init(((int)(p)),
                                                                                                                            t6074161585);
            
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10x.polar.Polar3d v_pole =
              x10x.polar.Polar3d.getPolar3d(((x10x.vector.Tuple3d)(v)));
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60742 =
              v_pole.
                theta;
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.core.Double> pplm =
              ((x10.array.Array)(au.edu.anu.mm.AssociatedLegendrePolynomial.getPlk((double)(t60742),
                                                                                   (int)(p))));
            
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this60322 =
              ((x10.array.Array)(exp.
                                   terms));
            
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex alloc39393 =
              new x10.lang.Complex((java.lang.System[]) null);
            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret59405 =
               0;
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6074361586 =
              ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                    region));
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6074461587 =
              t6074361586.contains$O((int)(0),
                                     (int)(0));
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6074561588 =
              !(t6074461587);
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6074561588) {
                
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(0),
                                                                                                                                                   (int)(0));
            }
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6075461589 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                               raw));
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this5941061590 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                        layout));
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret5941161591 =
               0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6074661377 =
              this5941061590.
                min0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset5940961378 =
              ((0) - (((int)(t6074661377))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6074761379 =
              offset5940961378;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6074861380 =
              this5941061590.
                delta1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6074961381 =
              ((t6074761379) * (((int)(t6074861380))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6075061382 =
              ((t6074961381) + (((int)(0))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6075161383 =
              this5941061590.
                min1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6075261384 =
              ((t6075061382) - (((int)(t6075161383))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset5940961378 = t6075261384;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6075361385 =
              offset5940961378;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret5941161591 = t6075361385;
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6075561592 =
              ret5941161591;
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6075661593 =
              ((double[])t6075461589.value)[t6075561592];
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret59405 = t6075661593;
            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t60757 =
              ret59405;
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double real60316 =
              ((q) * (((double)(t60757))));
            
//#line 53 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
alloc39393.re = real60316;
            
//#line 54 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
alloc39393.im = 0.0;
            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v60321 =
              ((x10.lang.Complex)(alloc39393));
            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret60323 =
               null;
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6075861594 =
              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this60322).
                                    region));
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6075961595 =
              t6075861594.contains$O((int)(0),
                                     (int)(0));
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6076061596 =
              !(t6075961595);
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6076061596) {
                
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(0),
                                                                                                                                                   (int)(0));
            }
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6076961597 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this60322).
                                               raw));
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6032861598 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this60322).
                                        layout));
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6032961599 =
               0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076161386 =
              this6032861598.
                min0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6032761387 =
              ((0) - (((int)(t6076161386))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076261388 =
              offset6032761387;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076361389 =
              this6032861598.
                delta1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076461390 =
              ((t6076261388) * (((int)(t6076361389))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076561391 =
              ((t6076461390) + (((int)(0))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076661392 =
              this6032861598.
                min1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076761393 =
              ((t6076561391) - (((int)(t6076661392))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6032761387 = t6076761393;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6076861394 =
              offset6032761387;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6032961599 = t6076861394;
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6077061600 =
              ret6032961599;
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6076961597.value)[t6077061600] = v60321;
            
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60323 = ((x10.lang.Complex)(v60321));
            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex phifac0 =
              new x10.lang.Complex((java.lang.System[]) null);
            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60771 =
              v_pole.
                phi;
            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60772 =
              (-(t60771));
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double real60331 =
              java.lang.Math.cos(((double)(t60772)));
            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60773 =
              v_pole.
                phi;
            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60774 =
              (-(t60773));
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double imaginary60332 =
              java.lang.Math.sin(((double)(t60774)));
            
//#line 53 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
phifac0.re = real60331;
            
//#line 54 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
phifac0.im = imaginary60332;
            
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double rfac =
              v_pole.
                r;
            
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double il =
              1.0;
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39430max3943261602 =
              p;
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3943061582 =
              1;
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6077661583 =
                  i3943061582;
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6090361584 =
                  ((t6077661583) <= (((int)(i39430max3943261602))));
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6090361584)) {
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                }
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int l61579 =
                  i3943061582;
                
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6077961558 =
                  il;
                
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6078061559 =
                  ((double)(int)(((int)(l61579))));
                
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6078161560 =
                  ((t6077961558) * (((double)(t6078061559))));
                
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
il = t6078161560;
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double ilm61561 =
                  il;
                
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.lang.Complex phifac61562 =
                  x10.lang.Complex.getInitialized$ONE();
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6034761563 =
                  ((x10.array.Array)(exp.
                                       terms));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06034461564 =
                  l61579;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6078261565 =
                  phifac61562;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6078361566 =
                  ilm61561;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6080161567 =
                  t6078261565.$over((double)(t6078361566));
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6078461568 =
                  rfac;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6079961569 =
                  ((q) * (((double)(t6078461568))));
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06033461570 =
                  l61579;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6033661571 =
                   0;
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6078561537 =
                  ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                        region));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6078661538 =
                  t6078561537.contains$O((int)(i06033461570),
                                         (int)(0));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6078761539 =
                  !(t6078661538);
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6078761539) {
                    
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06033461570),
                                                                                                                                                       (int)(0));
                }
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6079661540 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                                   raw));
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6034161541 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06033861542 =
                  i06033461570;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6034261543 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6078861395 =
                  this6034161541.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6034061396 =
                  ((i06033861542) - (((int)(t6078861395))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6078961397 =
                  offset6034061396;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6079061398 =
                  this6034161541.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6079161399 =
                  ((t6078961397) * (((int)(t6079061398))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6079261400 =
                  ((t6079161399) + (((int)(0))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6079361401 =
                  this6034161541.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6079461402 =
                  ((t6079261400) - (((int)(t6079361401))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6034061396 = t6079461402;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6079561403 =
                  offset6034061396;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6034261543 = t6079561403;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6079761544 =
                  ret6034261543;
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6079861545 =
                  ((double[])t6079661540.value)[t6079761544];
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6033661571 = t6079861545;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6080061572 =
                  ret6033661571;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6080261573 =
                  ((t6079961569) * (((double)(t6080061572))));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6034661574 =
                  ((x10.lang.Complex)(t6080161567.$times((double)(t6080261573))));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6034861575 =
                   null;
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6080361546 =
                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6034761563).
                                        region));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6080461547 =
                  t6080361546.contains$O((int)(i06034461564),
                                         (int)(0));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6080561548 =
                  !(t6080461547);
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6080561548) {
                    
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06034461564),
                                                                                                                                                       (int)(0));
                }
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6081461549 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6034761563).
                                                   raw));
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6035361550 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6034761563).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06035061551 =
                  i06034461564;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6035461552 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6080661404 =
                  this6035361550.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6035261405 =
                  ((i06035061551) - (((int)(t6080661404))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6080761406 =
                  offset6035261405;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6080861407 =
                  this6035361550.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6080961408 =
                  ((t6080761406) * (((int)(t6080861407))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6081061409 =
                  ((t6080961408) + (((int)(0))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6081161410 =
                  this6035361550.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6081261411 =
                  ((t6081061409) - (((int)(t6081161410))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6035261405 = t6081261411;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6081361412 =
                  offset6035261405;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6035461552 = t6081361412;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6081561553 =
                  ret6035461552;
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6081461549.value)[t6081561553] = v6034661574;
                
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6034861575 = ((x10.lang.Complex)(v6034661574));
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39398max3940061555 =
                  l61579;
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3939861475 =
                  1;
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                           true;
                                                                                                                           ) {
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6081761476 =
                      i3939861475;
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6086061477 =
                      ((t6081761476) <= (((int)(i39398max3940061555))));
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6086061477)) {
                        
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                    }
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int m61472 =
                      i3939861475;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6082161450 =
                      ilm61561;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6082061451 =
                      ((l61579) + (((int)(m61472))));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6082261452 =
                      ((double)(int)(((int)(t6082061451))));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6082361453 =
                      ((t6082161450) * (((double)(t6082261452))));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
ilm61561 = t6082361453;
                    
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6082461454 =
                      phifac61562;
                    
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6082561455 =
                      t6082461454.$times(((x10.lang.Complex)(phifac0)));
                    
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
phifac61562 = t6082561455;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6082661456 =
                      phifac61562;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6082761457 =
                      ilm61561;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6084561458 =
                      t6082661456.$over((double)(t6082761457));
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6082861459 =
                      rfac;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6084361460 =
                      ((q) * (((double)(t6082861459))));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06035661461 =
                      l61579;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16035761462 =
                      m61472;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6035861463 =
                       0;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6082961431 =
                      ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6083061432 =
                      t6082961431.contains$O((int)(i06035661461),
                                             (int)(i16035761462));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6083161433 =
                      !(t6083061432);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6083161433) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06035661461),
                                                                                                                                                           (int)(i16035761462));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6084061434 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6036361435 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06036061436 =
                      i06035661461;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16036161437 =
                      i16035761462;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6036461438 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083261413 =
                      this6036361435.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6036261414 =
                      ((i06036061436) - (((int)(t6083261413))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083361415 =
                      offset6036261414;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083461416 =
                      this6036361435.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083561417 =
                      ((t6083361415) * (((int)(t6083461416))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083661418 =
                      ((t6083561417) + (((int)(i16036161437))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083761419 =
                      this6036361435.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083861420 =
                      ((t6083661418) - (((int)(t6083761419))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6036261414 = t6083861420;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6083961421 =
                      offset6036261414;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6036461438 = t6083961421;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6084161439 =
                      ret6036461438;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6084261440 =
                      ((double[])t6084061434.value)[t6084161439];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6035861463 = t6084261440;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6084461464 =
                      ret6035861463;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6084661465 =
                      ((t6084361460) * (((double)(t6084461464))));
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex O_lm61466 =
                      t6084561458.$times((double)(t6084661465));
                    
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6036961467 =
                      ((x10.array.Array)(exp.
                                           terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06036661468 =
                      l61579;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16036761469 =
                      m61472;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6036861470 =
                      ((x10.lang.Complex)(O_lm61466));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6037061471 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6084761441 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6036961467).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6084861442 =
                      t6084761441.contains$O((int)(i06036661468),
                                             (int)(i16036761469));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6084961443 =
                      !(t6084861442);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6084961443) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06036661468),
                                                                                                                                                           (int)(i16036761469));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6085861444 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6036961467).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6037561445 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6036961467).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06037261446 =
                      i06036661468;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16037361447 =
                      i16036761469;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6037661448 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085061422 =
                      this6037561445.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6037461423 =
                      ((i06037261446) - (((int)(t6085061422))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085161424 =
                      offset6037461423;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085261425 =
                      this6037561445.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085361426 =
                      ((t6085161424) * (((int)(t6085261425))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085461427 =
                      ((t6085361426) + (((int)(i16037361447))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085561428 =
                      this6037561445.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085661429 =
                      ((t6085461427) - (((int)(t6085561428))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6037461423 = t6085661429;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085761430 =
                      offset6037461423;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6037661448 = t6085761430;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6085961449 =
                      ret6037661448;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6085861444.value)[t6085961449] = v6036861470;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6037061471 = ((x10.lang.Complex)(v6036861470));
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6081861473 =
                      i3939861475;
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6081961474 =
                      ((t6081861473) + (((int)(1))));
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3939861475 = t6081961474;
                }
                
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39414min3941561556 =
                  (-(l61579));
                
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3941461534 =
                  i39414min3941561556;
                
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                           true;
                                                                                                                           ) {
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6086261535 =
                      i3941461534;
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6089961536 =
                      ((t6086261535) <= (((int)(-1))));
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6089961536)) {
                        
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                    }
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int m61531 =
                      i3941461534;
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6039261515 =
                      ((x10.array.Array)(exp.
                                           terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06038961516 =
                      l61579;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16039061517 =
                      m61531;
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6038061518 =
                      ((x10.array.Array)(exp.
                                           terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06037861519 =
                      l61579;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16037961520 =
                      (-(m61531));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6038161521 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6086561496 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6038061518).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6086661497 =
                      t6086561496.contains$O((int)(i06037861519),
                                             (int)(i16037961520));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6086761498 =
                      !(t6086661497);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6086761498) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06037861519),
                                                                                                                                                           (int)(i16037961520));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6087661499 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6038061518).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6038661500 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6038061518).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06038361501 =
                      i06037861519;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16038461502 =
                      i16037961520;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6038761503 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6086861478 =
                      this6038661500.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6038561479 =
                      ((i06038361501) - (((int)(t6086861478))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6086961480 =
                      offset6038561479;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6087061481 =
                      this6038661500.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6087161482 =
                      ((t6086961480) * (((int)(t6087061481))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6087261483 =
                      ((t6087161482) + (((int)(i16038461502))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6087361484 =
                      this6038661500.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6087461485 =
                      ((t6087261483) - (((int)(t6087361484))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6038561479 = t6087461485;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6087561486 =
                      offset6038561479;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6038761503 = t6087561486;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6087761504 =
                      ret6038761503;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6087861505 =
                      ((x10.lang.Complex[])t6087661499.value)[t6087761504];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6038161521 = t6087861505;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6087961522 =
                      ret6038161521;
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6088461523 =
                      t6087961522.conjugate();
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6088061524 =
                      (-(m61531));
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6088161525 =
                      ((t6088061524) % (((int)(2))));
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6088261526 =
                      ((2) * (((int)(t6088161525))));
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6088361527 =
                      ((1) - (((int)(t6088261526))));
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6088561528 =
                      ((double)(int)(((int)(t6088361527))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6039161529 =
                      ((x10.lang.Complex)(t6088461523.$times((double)(t6088561528))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6039361530 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6088661506 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6039261515).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6088761507 =
                      t6088661506.contains$O((int)(i06038961516),
                                             (int)(i16039061517));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6088861508 =
                      !(t6088761507);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6088861508) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06038961516),
                                                                                                                                                           (int)(i16039061517));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6089761509 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6039261515).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6039861510 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6039261515).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06039561511 =
                      i06038961516;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16039661512 =
                      i16039061517;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6039961513 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6088961487 =
                      this6039861510.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6039761488 =
                      ((i06039561511) - (((int)(t6088961487))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089061489 =
                      offset6039761488;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089161490 =
                      this6039861510.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089261491 =
                      ((t6089061489) * (((int)(t6089161490))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089361492 =
                      ((t6089261491) + (((int)(i16039661512))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089461493 =
                      this6039861510.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089561494 =
                      ((t6089361492) - (((int)(t6089461493))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6039761488 = t6089561494;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089661495 =
                      offset6039761488;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6039961513 = t6089661495;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6089861514 =
                      ret6039961513;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6089761509.value)[t6089861514] = v6039161529;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6039361530 = ((x10.lang.Complex)(v6039161529));
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6086361532 =
                      i3941461534;
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6086461533 =
                      ((t6086361532) + (((int)(1))));
                    
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3941461534 = t6086461533;
                }
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6090061576 =
                  rfac;
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6090161577 =
                  v_pole.
                    r;
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6090261578 =
                  ((t6090061576) * (((double)(t6090161577))));
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
rfac = t6090261578;
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6077761580 =
                  i3943061582;
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6077861581 =
                  ((t6077761580) + (((int)(1))));
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3943061582 = t6077861581;
            }
            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
return exp;
        }
        
        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public static au.edu.anu.mm.MultipoleExpansion
                                                                                                                getOlm(
                                                                                                                final x10x.vector.Tuple3d v,
                                                                                                                final int p){
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion exp =
              ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t6090461812 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
exp.$init(((int)(p)),
                                                                                                                            t6090461812);
            
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10x.polar.Polar3d v_pole =
              x10x.polar.Polar3d.getPolar3d(((x10x.vector.Tuple3d)(v)));
            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60905 =
              v_pole.
                theta;
            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.core.Double> pplm =
              ((x10.array.Array)(au.edu.anu.mm.AssociatedLegendrePolynomial.getPlk((double)(t60905),
                                                                                   (int)(p))));
            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this60417 =
              ((x10.array.Array)(exp.
                                   terms));
            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex alloc39394 =
              new x10.lang.Complex((java.lang.System[]) null);
            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret60403 =
               0;
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6090661813 =
              ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                    region));
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6090761814 =
              t6090661813.contains$O((int)(0),
                                     (int)(0));
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6090861815 =
              !(t6090761814);
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6090861815) {
                
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(0),
                                                                                                                                                   (int)(0));
            }
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6091761816 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                               raw));
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6040861817 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                        layout));
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6040961818 =
               0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6090961603 =
              this6040861817.
                min0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6040761604 =
              ((0) - (((int)(t6090961603))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091061605 =
              offset6040761604;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091161606 =
              this6040861817.
                delta1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091261607 =
              ((t6091061605) * (((int)(t6091161606))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091361608 =
              ((t6091261607) + (((int)(0))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091461609 =
              this6040861817.
                min1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091561610 =
              ((t6091361608) - (((int)(t6091461609))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6040761604 = t6091561610;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091661611 =
              offset6040761604;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6040961818 = t6091661611;
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6091861819 =
              ret6040961818;
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6091961820 =
              ((double[])t6091761816.value)[t6091861819];
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60403 = t6091961820;
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double real60411 =
              ret60403;
            
//#line 53 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
alloc39394.re = real60411;
            
//#line 54 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
alloc39394.im = 0.0;
            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v60416 =
              ((x10.lang.Complex)(alloc39394));
            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret60418 =
               null;
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6092061821 =
              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this60417).
                                    region));
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6092161822 =
              t6092061821.contains$O((int)(0),
                                     (int)(0));
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6092261823 =
              !(t6092161822);
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6092261823) {
                
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(0),
                                                                                                                                                   (int)(0));
            }
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6093161824 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this60417).
                                               raw));
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6042361825 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this60417).
                                        layout));
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6042461826 =
               0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6092361612 =
              this6042361825.
                min0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6042261613 =
              ((0) - (((int)(t6092361612))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6092461614 =
              offset6042261613;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6092561615 =
              this6042361825.
                delta1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6092661616 =
              ((t6092461614) * (((int)(t6092561615))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6092761617 =
              ((t6092661616) + (((int)(0))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6092861618 =
              this6042361825.
                min1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6092961619 =
              ((t6092761617) - (((int)(t6092861618))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6042261613 = t6092961619;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6093061620 =
              offset6042261613;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6042461826 = t6093061620;
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6093261827 =
              ret6042461826;
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6093161824.value)[t6093261827] = v60416;
            
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60418 = ((x10.lang.Complex)(v60416));
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex phifac0 =
              new x10.lang.Complex((java.lang.System[]) null);
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60933 =
              v_pole.
                phi;
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60934 =
              (-(t60933));
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double real60426 =
              java.lang.Math.cos(((double)(t60934)));
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60935 =
              v_pole.
                phi;
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t60936 =
              (-(t60935));
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double imaginary60427 =
              java.lang.Math.sin(((double)(t60936)));
            
//#line 53 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
phifac0.re = real60426;
            
//#line 54 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
phifac0.im = imaginary60427;
            
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double rfac =
              v_pole.
                r;
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double il =
              1.0;
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39478max3948061829 =
              p;
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3947861809 =
              1;
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6093861810 =
                  i3947861809;
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6106561811 =
                  ((t6093861810) <= (((int)(i39478max3948061829))));
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6106561811)) {
                    
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                }
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int l61806 =
                  i3947861809;
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6094161785 =
                  il;
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6094261786 =
                  ((double)(int)(((int)(l61806))));
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6094361787 =
                  ((t6094161785) * (((double)(t6094261786))));
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
il = t6094361787;
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double ilm61788 =
                  il;
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.lang.Complex phifac61789 =
                  x10.lang.Complex.getInitialized$ONE();
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6044261790 =
                  ((x10.array.Array)(exp.
                                       terms));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06043961791 =
                  l61806;
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6094461792 =
                  phifac61789;
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6094561793 =
                  ilm61788;
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6096261794 =
                  t6094461792.$over((double)(t6094561793));
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6096061795 =
                  rfac;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06042961796 =
                  l61806;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6043161797 =
                   0;
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6094661764 =
                  ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                        region));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6094761765 =
                  t6094661764.contains$O((int)(i06042961796),
                                         (int)(0));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6094861766 =
                  !(t6094761765);
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6094861766) {
                    
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06042961796),
                                                                                                                                                       (int)(0));
                }
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6095761767 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                                   raw));
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6043661768 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06043361769 =
                  i06042961796;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6043761770 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6094961621 =
                  this6043661768.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6043561622 =
                  ((i06043361769) - (((int)(t6094961621))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095061623 =
                  offset6043561622;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095161624 =
                  this6043661768.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095261625 =
                  ((t6095061623) * (((int)(t6095161624))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095361626 =
                  ((t6095261625) + (((int)(0))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095461627 =
                  this6043661768.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095561628 =
                  ((t6095361626) - (((int)(t6095461627))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6043561622 = t6095561628;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095661629 =
                  offset6043561622;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6043761770 = t6095661629;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6095861771 =
                  ret6043761770;
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6095961772 =
                  ((double[])t6095761767.value)[t6095861771];
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6043161797 = t6095961772;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6096161798 =
                  ret6043161797;
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6096361799 =
                  ((t6096061795) * (((double)(t6096161798))));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6044161800 =
                  ((x10.lang.Complex)(t6096261794.$times((double)(t6096361799))));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6044361801 =
                   null;
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6096461773 =
                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6044261790).
                                        region));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6096561774 =
                  t6096461773.contains$O((int)(i06043961791),
                                         (int)(0));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6096661775 =
                  !(t6096561774);
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6096661775) {
                    
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06043961791),
                                                                                                                                                       (int)(0));
                }
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6097561776 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6044261790).
                                                   raw));
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6044861777 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6044261790).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06044561778 =
                  i06043961791;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6044961779 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6096761630 =
                  this6044861777.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6044761631 =
                  ((i06044561778) - (((int)(t6096761630))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6096861632 =
                  offset6044761631;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6096961633 =
                  this6044861777.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6097061634 =
                  ((t6096861632) * (((int)(t6096961633))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6097161635 =
                  ((t6097061634) + (((int)(0))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6097261636 =
                  this6044861777.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6097361637 =
                  ((t6097161635) - (((int)(t6097261636))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6044761631 = t6097361637;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6097461638 =
                  offset6044761631;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6044961779 = t6097461638;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6097661780 =
                  ret6044961779;
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6097561776.value)[t6097661780] = v6044161800;
                
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6044361801 = ((x10.lang.Complex)(v6044161800));
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
boolean m_sign61802 =
                  false;
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39446max3944861782 =
                  l61806;
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3944661702 =
                  1;
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                           true;
                                                                                                                           ) {
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6097861703 =
                      i3944661702;
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6102261704 =
                      ((t6097861703) <= (((int)(i39446max3944861782))));
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6102261704)) {
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                    }
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int m61699 =
                      i3944661702;
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6098261676 =
                      ilm61788;
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6098161677 =
                      ((l61806) + (((int)(m61699))));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6098361678 =
                      ((double)(int)(((int)(t6098161677))));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6098461679 =
                      ((t6098261676) * (((double)(t6098361678))));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
ilm61788 = t6098461679;
                    
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6098561680 =
                      phifac61789;
                    
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6098661681 =
                      t6098561680.$times(((x10.lang.Complex)(phifac0)));
                    
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
phifac61789 = t6098661681;
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6098761682 =
                      phifac61789;
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6098861683 =
                      ilm61788;
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6100561684 =
                      t6098761682.$over((double)(t6098861683));
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6100361685 =
                      rfac;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06045161686 =
                      l61806;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16045261687 =
                      m61699;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6045361688 =
                       0;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6098961657 =
                      ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6099061658 =
                      t6098961657.contains$O((int)(i06045161686),
                                             (int)(i16045261687));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6099161659 =
                      !(t6099061658);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6099161659) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06045161686),
                                                                                                                                                           (int)(i16045261687));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6100061660 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6045861661 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06045561662 =
                      i06045161686;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16045661663 =
                      i16045261687;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6045961664 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099261639 =
                      this6045861661.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6045761640 =
                      ((i06045561662) - (((int)(t6099261639))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099361641 =
                      offset6045761640;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099461642 =
                      this6045861661.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099561643 =
                      ((t6099361641) * (((int)(t6099461642))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099661644 =
                      ((t6099561643) + (((int)(i16045661663))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099761645 =
                      this6045861661.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099861646 =
                      ((t6099661644) - (((int)(t6099761645))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6045761640 = t6099861646;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6099961647 =
                      offset6045761640;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6045961664 = t6099961647;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6100161665 =
                      ret6045961664;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6100261666 =
                      ((double[])t6100061660.value)[t6100161665];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6045361688 = t6100261666;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6100461689 =
                      ret6045361688;
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6100661690 =
                      ((t6100361685) * (((double)(t6100461689))));
                    
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex O_lm61691 =
                      t6100561684.$times((double)(t6100661690));
                    
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6046461692 =
                      ((x10.array.Array)(exp.
                                           terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06046161693 =
                      l61806;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16046261694 =
                      m61699;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6046361695 =
                      ((x10.lang.Complex)(O_lm61691));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6046561696 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6100761667 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6046461692).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6100861668 =
                      t6100761667.contains$O((int)(i06046161693),
                                             (int)(i16046261694));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6100961669 =
                      !(t6100861668);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6100961669) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06046161693),
                                                                                                                                                           (int)(i16046261694));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6101861670 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6046461692).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6047061671 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6046461692).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06046761672 =
                      i06046161693;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16046861673 =
                      i16046261694;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6047161674 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101061648 =
                      this6047061671.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6046961649 =
                      ((i06046761672) - (((int)(t6101061648))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101161650 =
                      offset6046961649;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101261651 =
                      this6047061671.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101361652 =
                      ((t6101161650) * (((int)(t6101261651))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101461653 =
                      ((t6101361652) + (((int)(i16046861673))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101561654 =
                      this6047061671.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101661655 =
                      ((t6101461653) - (((int)(t6101561654))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6046961649 = t6101661655;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101761656 =
                      offset6046961649;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6047161674 = t6101761656;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6101961675 =
                      ret6047161674;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6101861670.value)[t6101961675] = v6046361695;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6046561696 = ((x10.lang.Complex)(v6046361695));
                    
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6102061697 =
                      m_sign61802;
                    
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6102161698 =
                      !(t6102061697);
                    
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
m_sign61802 = t6102161698;
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6097961700 =
                      i3944661702;
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6098061701 =
                      ((t6097961700) + (((int)(1))));
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3944661702 = t6098061701;
                }
                
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39462min3946361783 =
                  (-(l61806));
                
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3946261761 =
                  i39462min3946361783;
                
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                           true;
                                                                                                                           ) {
                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6102461762 =
                      i3946261761;
                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6106161763 =
                      ((t6102461762) <= (((int)(-1))));
                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6106161763)) {
                        
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                    }
                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int m61758 =
                      i3946261761;
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6048761742 =
                      ((x10.array.Array)(exp.
                                           terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06048461743 =
                      l61806;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16048561744 =
                      m61758;
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6047561745 =
                      ((x10.array.Array)(exp.
                                           terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06047361746 =
                      l61806;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16047461747 =
                      (-(m61758));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6047661748 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6102761723 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6047561745).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6102861724 =
                      t6102761723.contains$O((int)(i06047361746),
                                             (int)(i16047461747));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6102961725 =
                      !(t6102861724);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6102961725) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06047361746),
                                                                                                                                                           (int)(i16047461747));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6103861726 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6047561745).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6048161727 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6047561745).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06047861728 =
                      i06047361746;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16047961729 =
                      i16047461747;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6048261730 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103061705 =
                      this6048161727.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6048061706 =
                      ((i06047861728) - (((int)(t6103061705))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103161707 =
                      offset6048061706;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103261708 =
                      this6048161727.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103361709 =
                      ((t6103161707) * (((int)(t6103261708))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103461710 =
                      ((t6103361709) + (((int)(i16047961729))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103561711 =
                      this6048161727.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103661712 =
                      ((t6103461710) - (((int)(t6103561711))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6048061706 = t6103661712;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103761713 =
                      offset6048061706;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6048261730 = t6103761713;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6103961731 =
                      ret6048261730;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6104061732 =
                      ((x10.lang.Complex[])t6103861726.value)[t6103961731];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6047661748 = t6104061732;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6104161749 =
                      ret6047661748;
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6104661750 =
                      t6104161749.conjugate();
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6104261751 =
                      (-(m61758));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6104361752 =
                      ((t6104261751) % (((int)(2))));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6104461753 =
                      ((2) * (((int)(t6104361752))));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6104561754 =
                      ((1) - (((int)(t6104461753))));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6104761755 =
                      ((double)(int)(((int)(t6104561754))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6048661756 =
                      ((x10.lang.Complex)(t6104661750.$times((double)(t6104761755))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6048861757 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6104861733 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6048761742).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6104961734 =
                      t6104861733.contains$O((int)(i06048461743),
                                             (int)(i16048561744));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6105061735 =
                      !(t6104961734);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6105061735) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06048461743),
                                                                                                                                                           (int)(i16048561744));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6105961736 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6048761742).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6049361737 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6048761742).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06049061738 =
                      i06048461743;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16049161739 =
                      i16048561744;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6049461740 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105161714 =
                      this6049361737.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6049261715 =
                      ((i06049061738) - (((int)(t6105161714))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105261716 =
                      offset6049261715;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105361717 =
                      this6049361737.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105461718 =
                      ((t6105261716) * (((int)(t6105361717))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105561719 =
                      ((t6105461718) + (((int)(i16049161739))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105661720 =
                      this6049361737.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105761721 =
                      ((t6105561719) - (((int)(t6105661720))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6049261715 = t6105761721;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6105861722 =
                      offset6049261715;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6049461740 = t6105861722;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6106061741 =
                      ret6049461740;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6105961736.value)[t6106061741] = v6048661756;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6048861757 = ((x10.lang.Complex)(v6048661756));
                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6102561759 =
                      i3946261761;
                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6102661760 =
                      ((t6102561759) + (((int)(1))));
                    
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3946261761 = t6102661760;
                }
                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6106261803 =
                  rfac;
                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6106361804 =
                  v_pole.
                    r;
                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6106461805 =
                  ((t6106261803) * (((double)(t6106361804))));
                
//#line 100 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
rfac = t6106461805;
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6093961807 =
                  i3947861809;
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6094061808 =
                  ((t6093961807) + (((int)(1))));
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3947861809 = t6094061808;
            }
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
return exp;
        }
        
        
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public void
                                                                                                                 translateAndAddMultipole(
                                                                                                                 final au.edu.anu.mm.MultipoleExpansion shift,
                                                                                                                 final au.edu.anu.mm.MultipoleExpansion source){
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
try {{
                
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t61066 =
                  this.getOrderedLock();
                
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t61067 =
                  shift.getOrderedLock();
                
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t61068 =
                  source.getOrderedLock();
                
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.util.concurrent.OrderedLock.acquireThreeLocks(((x10.util.concurrent.OrderedLock)(t61066)),
                                                                                                                                                                         ((x10.util.concurrent.OrderedLock)(t61067)),
                                                                                                                                                                         ((x10.util.concurrent.OrderedLock)(t61068)));
                
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.lang.Runtime.pushAtomic();
                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39542max3954461963 =
                  p;
                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3954261959 =
                  0;
                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6107061960 =
                      i3954261959;
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6115061961 =
                      ((t6107061960) <= (((int)(i39542max3954461963))));
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6115061961)) {
                        
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                    }
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int j61956 =
                      i3954261959;
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39526min3952761954 =
                      (-(j61956));
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39526max3952861955 =
                      j61956;
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3952661951 =
                      i39526min3952761954;
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6107461952 =
                          i3952661951;
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6114961953 =
                          ((t6107461952) <= (((int)(i39526max3952861955))));
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6114961953)) {
                            
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                        }
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int k61948 =
                          i3952661951;
                        
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6049861943 =
                          ((x10.array.Array)(source.
                                               terms));
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06049661944 =
                          j61956;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16049761945 =
                          k61948;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6049961946 =
                           null;
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6107761931 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6049861943).
                                                region));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6107861932 =
                          t6107761931.contains$O((int)(i06049661944),
                                                 (int)(i16049761945));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6107961933 =
                          !(t6107861932);
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6107961933) {
                            
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06049661944),
                                                                                                                                                               (int)(i16049761945));
                        }
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6108861934 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6049861943).
                                                           raw));
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6050461935 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6049861943).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06050161936 =
                          i06049661944;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16050261937 =
                          i16049761945;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6050561938 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108061830 =
                          this6050461935.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6050361831 =
                          ((i06050161936) - (((int)(t6108061830))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108161832 =
                          offset6050361831;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108261833 =
                          this6050461935.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108361834 =
                          ((t6108161832) * (((int)(t6108261833))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108461835 =
                          ((t6108361834) + (((int)(i16050261937))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108561836 =
                          this6050461935.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108661837 =
                          ((t6108461835) - (((int)(t6108561836))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6050361831 = t6108661837;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108761838 =
                          offset6050361831;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6050561938 = t6108761838;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6108961939 =
                          ret6050561938;
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6109061940 =
                          ((x10.lang.Complex[])t6108861934.value)[t6108961939];
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6049961946 = t6109061940;
                        
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex O_jk61947 =
                          ret6049961946;
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39510min3951161941 =
                          j61956;
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39510max3951261942 =
                          p;
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3951061928 =
                          i39510min3951161941;
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                                    true;
                                                                                                                                    ) {
                            
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6109261929 =
                              i3951061928;
                            
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6114861930 =
                              ((t6109261929) <= (((int)(i39510max3951261942))));
                            
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6114861930)) {
                                
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                            }
                            
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int l61925 =
                              i3951061928;
                            
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39494min3949561923 =
                              (-(l61925));
                            
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39494max3949661924 =
                              l61925;
                            
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3949461920 =
                              i39494min3949561923;
                            
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                                        true;
                                                                                                                                        ) {
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6109661921 =
                                  i3949461920;
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6114761922 =
                                  ((t6109661921) <= (((int)(i39494max3949661924))));
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6114761922)) {
                                    
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                                }
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int m61917 =
                                  i3949461920;
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a6050761895 =
                                  ((m61917) - (((int)(k61948))));
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t6109961896 =
                                  ((a6050761895) < (((int)(0))));
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t6110061897 =
                                   0;
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t6109961896) {
                                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t6110061897 = (-(a6050761895));
                                } else {
                                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t6110061897 = a6050761895;
                                }
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t6110161898 =
                                  t6110061897;
                                
//#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6110261899 =
                                  ((l61925) - (((int)(j61956))));
                                
//#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6114661900 =
                                  ((t6110161898) <= (((int)(t6110261899))));
                                
//#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6114661900) {
                                    
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6051061901 =
                                      ((x10.array.Array)(shift.
                                                           terms));
                                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06050861902 =
                                      ((l61925) - (((int)(j61956))));
                                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16050961903 =
                                      ((m61917) - (((int)(k61948))));
                                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6051161904 =
                                       null;
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6110361866 =
                                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6051061901).
                                                            region));
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6110461867 =
                                      t6110361866.contains$O((int)(i06050861902),
                                                             (int)(i16050961903));
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6110561868 =
                                      !(t6110461867);
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6110561868) {
                                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06050861902),
                                                                                                                                                                           (int)(i16050961903));
                                    }
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6111461869 =
                                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6051061901).
                                                                       raw));
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6051661870 =
                                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6051061901).
                                                                layout));
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06051361871 =
                                      i06050861902;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16051461872 =
                                      i16050961903;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6051761873 =
                                       0;
                                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6110661839 =
                                      this6051661870.
                                        min0;
                                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6051561840 =
                                      ((i06051361871) - (((int)(t6110661839))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6110761841 =
                                      offset6051561840;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6110861842 =
                                      this6051661870.
                                        delta1;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6110961843 =
                                      ((t6110761841) * (((int)(t6110861842))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6111061844 =
                                      ((t6110961843) + (((int)(i16051461872))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6111161845 =
                                      this6051661870.
                                        min1;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6111261846 =
                                      ((t6111061844) - (((int)(t6111161845))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6051561840 = t6111261846;
                                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6111361847 =
                                      offset6051561840;
                                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6051761873 = t6111361847;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6111561874 =
                                      ret6051761873;
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6111661875 =
                                      ((x10.lang.Complex[])t6111461869.value)[t6111561874];
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6051161904 = t6111661875;
                                    
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex A_lmjk61905 =
                                      ret6051161904;
                                    
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6053361906 =
                                      ((x10.array.Array)(this.
                                                           terms));
                                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06053061907 =
                                      l61925;
                                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16053161908 =
                                      m61917;
                                    
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6052161909 =
                                      ((x10.array.Array)(this.
                                                           terms));
                                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06051961910 =
                                      l61925;
                                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16052061911 =
                                      m61917;
                                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6052261912 =
                                       null;
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6111761876 =
                                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6052161909).
                                                            region));
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6111861877 =
                                      t6111761876.contains$O((int)(i06051961910),
                                                             (int)(i16052061911));
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6111961878 =
                                      !(t6111861877);
                                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6111961878) {
                                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06051961910),
                                                                                                                                                                           (int)(i16052061911));
                                    }
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6112861879 =
                                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6052161909).
                                                                       raw));
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6052761880 =
                                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6052161909).
                                                                layout));
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06052461881 =
                                      i06051961910;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16052561882 =
                                      i16052061911;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6052861883 =
                                       0;
                                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112061848 =
                                      this6052761880.
                                        min0;
                                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6052661849 =
                                      ((i06052461881) - (((int)(t6112061848))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112161850 =
                                      offset6052661849;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112261851 =
                                      this6052761880.
                                        delta1;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112361852 =
                                      ((t6112161850) * (((int)(t6112261851))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112461853 =
                                      ((t6112361852) + (((int)(i16052561882))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112561854 =
                                      this6052761880.
                                        min1;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112661855 =
                                      ((t6112461853) - (((int)(t6112561854))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6052661849 = t6112661855;
                                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112761856 =
                                      offset6052661849;
                                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6052861883 = t6112761856;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6112961884 =
                                      ret6052861883;
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6113061885 =
                                      ((x10.lang.Complex[])t6112861879.value)[t6112961884];
                                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6052261912 = t6113061885;
                                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6113161913 =
                                      ret6052261912;
                                    
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6113261914 =
                                      A_lmjk61905.$times(((x10.lang.Complex)(O_jk61947)));
                                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6053261915 =
                                      ((x10.lang.Complex)(t6113161913.$plus(((x10.lang.Complex)(t6113261914)))));
                                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6053461916 =
                                       null;
                                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6113361886 =
                                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6053361906).
                                                            region));
                                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6113461887 =
                                      t6113361886.contains$O((int)(i06053061907),
                                                             (int)(i16053161908));
                                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6113561888 =
                                      !(t6113461887);
                                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6113561888) {
                                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06053061907),
                                                                                                                                                                           (int)(i16053161908));
                                    }
                                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6114461889 =
                                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6053361906).
                                                                       raw));
                                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6053961890 =
                                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6053361906).
                                                                layout));
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06053661891 =
                                      i06053061907;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16053761892 =
                                      i16053161908;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6054061893 =
                                       0;
                                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6113661857 =
                                      this6053961890.
                                        min0;
                                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6053861858 =
                                      ((i06053661891) - (((int)(t6113661857))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6113761859 =
                                      offset6053861858;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6113861860 =
                                      this6053961890.
                                        delta1;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6113961861 =
                                      ((t6113761859) * (((int)(t6113861860))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6114061862 =
                                      ((t6113961861) + (((int)(i16053761892))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6114161863 =
                                      this6053961890.
                                        min1;
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6114261864 =
                                      ((t6114061862) - (((int)(t6114161863))));
                                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6053861858 = t6114261864;
                                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6114361865 =
                                      offset6053861858;
                                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6054061893 = t6114361865;
                                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6114561894 =
                                      ret6054061893;
                                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6114461889.value)[t6114561894] = v6053261915;
                                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6053461916 = ((x10.lang.Complex)(v6053261915));
                                }
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6109761918 =
                                  i3949461920;
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6109861919 =
                                  ((t6109761918) + (((int)(1))));
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3949461920 = t6109861919;
                            }
                            
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6109361926 =
                              i3951061928;
                            
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6109461927 =
                              ((t6109361926) + (((int)(1))));
                            
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3951061928 = t6109461927;
                        }
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6107561949 =
                          i3952661951;
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6107661950 =
                          ((t6107561949) + (((int)(1))));
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3952661951 = t6107661950;
                    }
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6107161957 =
                      i3954261959;
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6107261958 =
                      ((t6107161957) + (((int)(1))));
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3954261959 = t6107261958;
                }
            }}finally {{
                  
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.lang.Runtime.popAtomic();
                  
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t61151 =
                    this.getOrderedLock();
                  
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t61152 =
                    shift.getOrderedLock();
                  
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t61153 =
                    source.getOrderedLock();
                  
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.util.concurrent.OrderedLock.releaseThreeLocks(((x10.util.concurrent.OrderedLock)(t61151)),
                                                                                                                                                                           ((x10.util.concurrent.OrderedLock)(t61152)),
                                                                                                                                                                           ((x10.util.concurrent.OrderedLock)(t61153)));
              }}
            }
        
        
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public void
                                                                                                                 translateAndAddMultipole_1_$_x10$array$Array$_x10$lang$Complex_$_$_3_$_x10$array$Array$_x10$array$Array$_x10$lang$Double_$_$_$(
                                                                                                                 final x10x.vector.Vector3d v,
                                                                                                                 final x10.array.Array complexK,
                                                                                                                 final au.edu.anu.mm.MultipoleExpansion source,
                                                                                                                 final x10.array.Array wigner){
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61154 =
              v.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61155 =
              v.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61158 =
              ((t61154) * (((double)(t61155))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61156 =
              v.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61157 =
              v.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61159 =
              ((t61156) * (((double)(t61157))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61162 =
              ((t61158) + (((double)(t61159))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61160 =
              v.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61161 =
              v.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61163 =
              ((t61160) * (((double)(t61161))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t61164 =
              ((t61162) + (((double)(t61163))));
            
//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double b =
              java.lang.Math.sqrt(((double)(t61164)));
            
//#line 147 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t61165 =
              ((double)(int)(((int)(1))));
            
//#line 147 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double invB =
              ((t61165) / (((double)(b))));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> alloc39395 =
              ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61166 =
              p;
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61167 =
              (-(t61166));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61168 =
              p;
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.IntRange t61169 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t61167)), ((int)(t61168)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg60542 =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t61169)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.x10$lang$Object$$init$S();
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__605436054761968 =
              ((x10.array.Region)(((x10.array.Region)
                                    reg60542)));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6054861969 =
               null;
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6117061964 =
              ((__desugarer__var__0__605436054761968) != (null));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6117361965 =
              !(t6117061964);
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6117361965) {
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6117261966 =
                  true;
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6117261966) {
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6117161967 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6117161967;
                }
            }
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6054861969 = ((x10.array.Region)(__desugarer__var__0__605436054761968));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6117461970 =
              ((x10.array.Region)(ret6054861969));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.region = ((x10.array.Region)(t6117461970));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6117561971 =
              reg60542.
                rank;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.rank = t6117561971;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6117661972 =
              reg60542.
                rect;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.rect = t6117661972;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6117761973 =
              reg60542.
                zeroBased;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.zeroBased = t6117761973;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6117861974 =
              reg60542.
                rail;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.rail = t6117861974;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6117961975 =
              reg60542.size$O();
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.size = t6117961975;
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646054462132 =
              new x10.array.RectLayout((java.lang.System[]) null);
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646054462132.$init(((x10.array.Region)(reg60542)));
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.layout = ((x10.array.RectLayout)(alloc199646054462132));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6055062133 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc39395).
                                        layout));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6054562134 =
              this6055062133.
                size;
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6118062135 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6054562134)), true)));
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39395.raw = ((x10.core.IndexedMemoryChunk)(t6118062135));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> __desugarer__var__27__60551 =
              ((x10.array.Array)(((x10.array.Array<x10.lang.Complex>)
                                   alloc39395)));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.array.Array<x10.lang.Complex> ret60552 =
               null;
            
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6118162136 =
              ((x10.array.Array<x10.lang.Complex>)__desugarer__var__27__60551).
                rect;
            
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
boolean t6118362137 =
              ((boolean) t6118162136) ==
            ((boolean) true);
            
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6118362137) {
                
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6118262138 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__27__60551).
                    rail;
                
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
t6118362137 = ((boolean) t6118262138) ==
                ((boolean) false);
            }
            
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
boolean t6118562139 =
              t6118362137;
            
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6118562139) {
                
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6118462140 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__27__60551).
                    rank;
                
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
t6118562139 = ((int) t6118462140) ==
                ((int) 1);
            }
            
//#line 148 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6118662141 =
              t6118562139;
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6118962142 =
              !(t6118662141);
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6118962142) {
                
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6118862143 =
                  true;
                
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6118862143) {
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.FailedDynamicCheckException t6118762144 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Complex]{self.rect==true, self.rail==false, self.rank==1}");
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
throw t6118762144;
                }
            }
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
ret60552 = ((x10.array.Array)(__desugarer__var__27__60551));
            
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> temp =
              ((x10.array.Array)(ret60552));
            
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion scratch =
              ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
            
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t6119062145 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
scratch.$init(((au.edu.anu.mm.MultipoleExpansion)(source)),
                                                                                                                                 t6119062145);
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret60555 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret60556: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t61191 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t61192 =
              ((x10.array.Array)(((x10.array.Array[])t61191.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60555 = ((x10.array.Array)(t61192));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret60556;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t61195 =
              ((x10.array.Array)(ret60555));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret60563 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret60564: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t61193 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)wigner).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t61194 =
              ((x10.array.Array)(((x10.array.Array[])t61193.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60563 = ((x10.array.Array)(t61194));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret60564;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t61196 =
              ((x10.array.Array)(ret60563));
            
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
scratch.rotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                         ((x10.array.Array)(t61195)),
                                                                                                                                                                                                                         ((x10.array.Array)(t61196)));
            
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> targetTerms =
              ((x10.array.Array)(scratch.
                                   terms));
            
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int m_sign =
              1;
            
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39606max3960862147 =
              p;
            
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3960662129 =
              0;
            
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6119862130 =
                  i3960662129;
                
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6129462131 =
                  ((t6119862130) <= (((int)(i39606max3960862147))));
                
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6129462131)) {
                    
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                }
                
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int m62126 =
                  i3960662129;
                
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39558min3955962119 =
                  m62126;
                
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39558max3956062120 =
                  p;
                
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3955862015 =
                  i39558min3955962119;
                
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6120262016 =
                      i3955862015;
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6122662017 =
                      ((t6120262016) <= (((int)(i39558max3956062120))));
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6122662017)) {
                        
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                    }
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int l62012 =
                      i3955862015;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06058062006 =
                      l62012;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06057062007 =
                      l62012;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16057162008 =
                      m62126;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6057262009 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6120561996 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6120661997 =
                      t6120561996.contains$O((int)(i06057062007),
                                             (int)(i16057162008));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6120761998 =
                      !(t6120661997);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6120761998) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06057062007),
                                                                                                                                                           (int)(i16057162008));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6121661999 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6057762000 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06057462001 =
                      i06057062007;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16057562002 =
                      i16057162008;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6057862003 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6120861976 =
                      this6057762000.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6057661977 =
                      ((i06057462001) - (((int)(t6120861976))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6120961978 =
                      offset6057661977;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6121061979 =
                      this6057762000.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6121161980 =
                      ((t6120961978) * (((int)(t6121061979))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6121261981 =
                      ((t6121161980) + (((int)(i16057562002))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6121361982 =
                      this6057762000.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6121461983 =
                      ((t6121261981) - (((int)(t6121361982))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6057661977 = t6121461983;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6121561984 =
                      offset6057661977;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6057862003 = t6121561984;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6121762004 =
                      ret6057862003;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6121862005 =
                      ((x10.lang.Complex[])t6121661999.value)[t6121762004];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6057262009 = t6121862005;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6058162010 =
                      ((x10.lang.Complex)(ret6057262009));
                    
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6058262011 =
                       null;
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6121961988 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                            region));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6122061989 =
                      t6121961988.contains$O((int)(i06058062006));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6122161990 =
                      !(t6122061989);
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6122161990) {
                        
//#line 515 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06058062006));
                    }
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6122461991 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                       raw));
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6058661992 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                layout));
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06058461993 =
                      i06058062006;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6058761994 =
                       0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6122261985 =
                      this6058661992.
                        min0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6058561986 =
                      ((i06058461993) - (((int)(t6122261985))));
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6122361987 =
                      offset6058561986;
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6058761994 = t6122361987;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6122561995 =
                      ret6058761994;
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6122461991.value)[t6122561995] = v6058162010;
                    
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6058262011 = ((x10.lang.Complex)(v6058162010));
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6120362013 =
                      i3955862015;
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6120462014 =
                      ((t6120362013) + (((int)(1))));
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3955862015 = t6120462014;
                }
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double b_lm_pow62123 =
                  1.0;
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39590min3959162121 =
                  m62126;
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39590max3959262122 =
                  p;
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3959062116 =
                  i39590min3959162121;
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6122862117 =
                      i3959062116;
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6129162118 =
                      ((t6122862117) <= (((int)(i39590max3959262122))));
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6129162118)) {
                        
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                    }
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int l62113 =
                      i3959062116;
                    
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.lang.Complex O_lm62087 =
                      x10.lang.Complex.getInitialized$ZERO();
                    
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6123362088 =
                      b_lm_pow62123;
                    
//#line 30 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int i6062262089 =
                      ((l62113) - (((int)(m62126))));
                    
//#line 30 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final x10.array.Array<x10.core.Double> this6062462090 =
                      ((x10.array.Array)(au.edu.anu.mm.Factorial.getInitialized$factorial()));
                    
//#line 410 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06062362091 =
                      i6062262089;
                    
//#line 409 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6062562092 =
                       0;
                    
//#line 409 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6062662093: {
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6123162094 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)this6062462090).
                                                       raw));
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6123262095 =
                      ((double[])t6123162094.value)[i06062362091];
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6062562092 = t6123262095;
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6062662093;}
                    
//#line 409 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6123462096 =
                      ret6062562092;
                    
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
double F_lm62097 =
                      ((t6123362088) / (((double)(t6123462096))));
                    
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39574min3957562076 =
                      m62126;
                    
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int i39574max3957662077 =
                      l62113;
                    
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
int i3957462046 =
                      i39574min3957562076;
                    
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6123662047 =
                          i3957462046;
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6125762048 =
                          ((t6123662047) <= (((int)(i39574max3957662077))));
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6125762048)) {
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                        }
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int j62043 =
                          i3957462046;
                        
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6124962021 =
                          O_lm62087;
                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06063262022 =
                          j62043;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6063362023 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6063462024: {
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6123962025 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                                region));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6124062026 =
                          t6123962025.contains$O((int)(i06063262022));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6124162027 =
                          !(t6124062026);
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6124162027) {
                            
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06063262022));
                        }
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6124462028 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                           raw));
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6063762029 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                    layout));
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06063562030 =
                          i06063262022;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6063862031 =
                           0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6124262018 =
                          this6063762029.
                            min0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6063662019 =
                          ((i06063562030) - (((int)(t6124262018))));
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6124362020 =
                          offset6063662019;
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6063862031 = t6124362020;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6124562032 =
                          ret6063862031;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6124662033 =
                          ((x10.lang.Complex[])t6124462028.value)[t6124562032];
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6063362023 = t6124662033;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6063462024;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6124762034 =
                          ret6063362023;
                        
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6124862035 =
                          F_lm62097;
                        
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6125062036 =
                          t6124762034.$times((double)(t6124862035));
                        
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6125162037 =
                          t6124962021.$plus(((x10.lang.Complex)(t6125062036)));
                        
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
O_lm62087 = t6125162037;
                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6125362038 =
                          F_lm62097;
                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6125262039 =
                          ((l62113) - (((int)(j62043))));
                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6125462040 =
                          ((double)(int)(((int)(t6125262039))));
                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6125562041 =
                          ((t6125362038) * (((double)(t6125462040))));
                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6125662042 =
                          ((t6125562041) * (((double)(invB))));
                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
F_lm62097 = t6125662042;
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6123762044 =
                          i3957462046;
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6123862045 =
                          ((t6123762044) + (((int)(1))));
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3957462046 = t6123862045;
                    }
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06064062098 =
                      l62113;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16064162099 =
                      m62126;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6064262100 =
                      ((x10.lang.Complex)(O_lm62087));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6064362101 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6125862078 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6125962079 =
                      t6125862078.contains$O((int)(i06064062098),
                                             (int)(i16064162099));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6126062080 =
                      !(t6125962079);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6126062080) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06064062098),
                                                                                                                                                           (int)(i16064162099));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6126962081 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6064862082 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06064562083 =
                      i06064062098;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16064662084 =
                      i16064162099;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6064962085 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126162049 =
                      this6064862082.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6064762050 =
                      ((i06064562083) - (((int)(t6126162049))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126262051 =
                      offset6064762050;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126362052 =
                      this6064862082.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126462053 =
                      ((t6126262051) * (((int)(t6126362052))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126562054 =
                      ((t6126462053) + (((int)(i16064662084))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126662055 =
                      this6064862082.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126762056 =
                      ((t6126562054) - (((int)(t6126662055))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6064762050 = t6126762056;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6126862057 =
                      offset6064762050;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6064962085 = t6126862057;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6127062086 =
                      ret6064962085;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6126962081.value)[t6127062086] = v6064262100;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6064362101 = ((x10.lang.Complex)(v6064262100));
                    
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6128862102 =
                      ((int) m62126) !=
                    ((int) 0);
                    
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6128862102) {
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06065162103 =
                          l62113;
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16065262104 =
                          (-(m62126));
                        
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6127162105 =
                          O_lm62087;
                        
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Complex t6127362106 =
                          t6127162105.conjugate();
                        
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6127262107 =
                          m_sign;
                        
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6127462108 =
                          ((double)(int)(((int)(t6127262107))));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6065362109 =
                          ((x10.lang.Complex)(t6127362106.$times((double)(t6127462108))));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6065462110 =
                           null;
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6127562067 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                region));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6127662068 =
                          t6127562067.contains$O((int)(i06065162103),
                                                 (int)(i16065262104));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6127762069 =
                          !(t6127662068);
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6127762069) {
                            
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06065162103),
                                                                                                                                                               (int)(i16065262104));
                        }
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6128662070 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                           raw));
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6065962071 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06065662072 =
                          i06065162103;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16065762073 =
                          i16065262104;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6066062074 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6127862058 =
                          this6065962071.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6065862059 =
                          ((i06065662072) - (((int)(t6127862058))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6127962060 =
                          offset6065862059;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6128062061 =
                          this6065962071.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6128162062 =
                          ((t6127962060) * (((int)(t6128062061))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6128262063 =
                          ((t6128162062) + (((int)(i16065762073))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6128362064 =
                          this6065962071.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6128462065 =
                          ((t6128262063) - (((int)(t6128362064))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6065862059 = t6128462065;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6128562066 =
                          offset6065862059;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6066062074 = t6128562066;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6128762075 =
                          ret6066062074;
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6128662070.value)[t6128762075] = v6065362109;
                        
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6065462110 = ((x10.lang.Complex)(v6065362109));
                    }
                    
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6128962111 =
                      b_lm_pow62123;
                    
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6129062112 =
                      ((t6128962111) * (((double)(b))));
                    
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
b_lm_pow62123 = t6129062112;
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6122962114 =
                      i3959062116;
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6123062115 =
                      ((t6122962114) + (((int)(1))));
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3959062116 = t6123062115;
                }
                
//#line 171 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6129262124 =
                  m_sign;
                
//#line 171 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6129362125 =
                  (-(t6129262124));
                
//#line 171 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
m_sign = t6129362125;
                
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6119962127 =
                  i3960662129;
                
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6120062128 =
                  ((t6119962127) + (((int)(1))));
                
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
i3960662129 = t6120062128;
            }
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret60663 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret60664: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t61295 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t61296 =
              ((x10.array.Array)(((x10.array.Array[])t61295.value)[1]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60663 = ((x10.array.Array)(t61296));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret60664;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t61299 =
              ((x10.array.Array)(ret60663));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret60671 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret60672: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t61297 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)wigner).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t61298 =
              ((x10.array.Array)(((x10.array.Array[])t61297.value)[1]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60671 = ((x10.array.Array)(t61298));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret60672;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t61300 =
              ((x10.array.Array)(ret60671));
            
//#line 174 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
scratch.backRotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                             ((x10.array.Array)(t61299)),
                                                                                                                                                                                                                             ((x10.array.Array)(t61300)));
            
//#line 175 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this.unsafeAdd(((au.edu.anu.mm.Expansion)(scratch)));
        }
        
        
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public void
                                                                                                                 translateAndAddMultipole(
                                                                                                                 final x10x.vector.Vector3d v,
                                                                                                                 final au.edu.anu.mm.MultipoleExpansion source){
            
//#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10x.polar.Polar3d polar =
              x10x.polar.Polar3d.getPolar3d(((x10x.vector.Tuple3d)(v)));
            
//#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t61301 =
              polar.
                phi;
            
//#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61302 =
              p;
            
//#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.array.Array<x10.lang.Complex>> t61303 =
              ((x10.array.Array)(au.edu.anu.mm.Expansion.genComplexK((double)(t61301),
                                                                     (int)(t61302))));
            
//#line 185 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final double theta60678 =
              polar.
                theta;
            
//#line 185 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final int numTerms60679 =
              p;
            
//#line 185 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>> t61304 =
              ((x10.array.Array)(au.edu.anu.mm.WignerRotationMatrix.getExpandedCollection((double)(theta60678),
                                                                                          (int)(numTerms60679),
                                                                                          (int)(0))));
            
//#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this.translateAndAddMultipole_1_$_x10$array$Array$_x10$lang$Complex_$_$_3_$_x10$array$Array$_x10$array$Array$_x10$lang$Double_$_$_$(((x10x.vector.Vector3d)(v)),
                                                                                                                                                                                                                                                       ((x10.array.Array)(t61303)),
                                                                                                                                                                                                                                                       ((au.edu.anu.mm.MultipoleExpansion)(source)),
                                                                                                                                                                                                                                                       ((x10.array.Array)(t61304)));
        }
        
        
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public au.edu.anu.mm.MultipoleExpansion
                                                                                                                 rotate(
                                                                                                                 final double theta,
                                                                                                                 final double phi){
            
//#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion target =
              ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
            
//#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t6130562160 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
target.$init(((au.edu.anu.mm.MultipoleExpansion)(this)),
                                                                                                                                t6130562160);
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> alloc39396 =
              ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61306 =
              p;
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61307 =
              (-(t61306));
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61308 =
              p;
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.IntRange t61309 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t61307)), ((int)(t61308)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg60680 =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t61309)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.x10$lang$Object$$init$S();
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__606816068562152 =
              ((x10.array.Region)(((x10.array.Region)
                                    reg60680)));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6068662153 =
               null;
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6131062148 =
              ((__desugarer__var__0__606816068562152) != (null));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6131362149 =
              !(t6131062148);
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6131362149) {
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6131262150 =
                  true;
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6131262150) {
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6131162151 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6131162151;
                }
            }
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6068662153 = ((x10.array.Region)(__desugarer__var__0__606816068562152));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6131462154 =
              ((x10.array.Region)(ret6068662153));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.region = ((x10.array.Region)(t6131462154));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6131562155 =
              reg60680.
                rank;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.rank = t6131562155;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6131662156 =
              reg60680.
                rect;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.rect = t6131662156;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6131762157 =
              reg60680.
                zeroBased;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.zeroBased = t6131762157;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6131862158 =
              reg60680.
                rail;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.rail = t6131862158;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6131962159 =
              reg60680.size$O();
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.size = t6131962159;
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646068262161 =
              new x10.array.RectLayout((java.lang.System[]) null);
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646068262161.$init(((x10.array.Region)(reg60680)));
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.layout = ((x10.array.RectLayout)(alloc199646068262161));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6068862162 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc39396).
                                        layout));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6068362163 =
              this6068862162.
                size;
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6132062164 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6068362163)), true)));
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39396.raw = ((x10.core.IndexedMemoryChunk)(t6132062164));
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> __desugarer__var__28__60689 =
              ((x10.array.Array)(((x10.array.Array<x10.lang.Complex>)
                                   alloc39396)));
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
x10.array.Array<x10.lang.Complex> ret60690 =
               null;
            
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6132162165 =
              ((x10.array.Array<x10.lang.Complex>)__desugarer__var__28__60689).
                rect;
            
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
boolean t6132362166 =
              ((boolean) t6132162165) ==
            ((boolean) true);
            
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6132362166) {
                
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6132262167 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__28__60689).
                    rail;
                
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
t6132362166 = ((boolean) t6132262167) ==
                ((boolean) false);
            }
            
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
boolean t6132562168 =
              t6132362166;
            
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6132562168) {
                
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6132462169 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__28__60689).
                    rank;
                
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
t6132562168 = ((int) t6132462169) ==
                ((int) 1);
            }
            
//#line 197 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6132662170 =
              t6132562168;
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6132962171 =
              !(t6132662170);
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6132962171) {
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6132862172 =
                  true;
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (t6132862172) {
                    
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.FailedDynamicCheckException t6132762173 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Complex]{self.rect==true, self.rail==false, self.rank==1}");
                    
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
throw t6132762173;
                }
            }
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
ret60690 = ((x10.array.Array)(__desugarer__var__28__60689));
            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> temp =
              ((x10.array.Array)(ret60690));
            
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t61330 =
              p;
            
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.array.Array<x10.lang.Complex>> this60693 =
              ((x10.array.Array)(au.edu.anu.mm.Expansion.genComplexK((double)(phi),
                                                                     (int)(t61330))));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret60694 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret60695: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t61331 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)this60693).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t61332 =
              ((x10.array.Array)(((x10.array.Array[])t61331.value)[1]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60694 = ((x10.array.Array)(t61332));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret60695;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t61335 =
              ((x10.array.Array)(ret60694));
            
//#line 185 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final double theta60701 =
              theta;
            
//#line 185 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final int numTerms60702 =
              p;
            
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>> this60704 =
              ((x10.array.Array)(au.edu.anu.mm.WignerRotationMatrix.getExpandedCollection((double)(theta60701),
                                                                                          (int)(numTerms60702),
                                                                                          (int)(0))));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret60705 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret60706: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t61333 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)this60704).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t61334 =
              ((x10.array.Array)(((x10.array.Array[])t61333.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret60705 = ((x10.array.Array)(t61334));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret60706;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t61336 =
              ((x10.array.Array)(ret60705));
            
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
target.rotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                        ((x10.array.Array)(t61335)),
                                                                                                                                                                                                                        ((x10.array.Array)(t61336)));
            
//#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
return target;
        }
        
        
//#line 210 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
public au.edu.anu.mm.MultipoleExpansion
                                                                                                                 getMacroscopicParent(
                                                                                                                 ){
            
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion parentExpansion =
              ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
            
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int t6133762226 =
              p;
            
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.util.concurrent.OrderedLock t6133862227 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
parentExpansion.$init(((int)(t6133762226)),
                                                                                                                                         t6133862227);
            
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> t6134062228 =
              ((x10.array.Array)(terms));
            
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Region t6134162229 =
              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)t6134062228).
                                    region));
            
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.lang.Iterator<x10.array.Point> id3962262230 =
              t6134162229.iterator();
            
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final boolean t6137262231 =
                  ((x10.lang.Iterator<x10.array.Point>)id3962262230).hasNext$O();
                
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
if (!(t6137262231)) {
                    
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
break;
                }
                
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Point id228262211 =
                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)id3962262230).next$G()));
                
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int l62212 =
                  id228262211.$apply$O((int)(0));
                
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final int m62213 =
                  id228262211.$apply$O((int)(1));
                
//#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6072662214 =
                  ((x10.array.Array)(parentExpansion.
                                       terms));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06072362215 =
                  l62212;
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16072462216 =
                  m62213;
                
//#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6071462217 =
                  ((x10.array.Array)(terms));
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06071262218 =
                  l62212;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16071362219 =
                  m62213;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6071562220 =
                   null;
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6134262192 =
                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6071462217).
                                        region));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6134362193 =
                  t6134262192.contains$O((int)(i06071262218),
                                         (int)(i16071362219));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6134462194 =
                  !(t6134362193);
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6134462194) {
                    
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06071262218),
                                                                                                                                                       (int)(i16071362219));
                }
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6135362195 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6071462217).
                                                   raw));
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6072062196 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6071462217).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06071762197 =
                  i06071262218;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16071862198 =
                  i16071362219;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6072162199 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6134562174 =
                  this6072062196.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6071962175 =
                  ((i06071762197) - (((int)(t6134562174))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6134662176 =
                  offset6071962175;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6134762177 =
                  this6072062196.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6134862178 =
                  ((t6134662176) * (((int)(t6134762177))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6134962179 =
                  ((t6134862178) + (((int)(i16071862198))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6135062180 =
                  this6072062196.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6135162181 =
                  ((t6134962179) - (((int)(t6135062180))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6071962175 = t6135162181;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6135262182 =
                  offset6071962175;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6072162199 = t6135262182;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6135462200 =
                  ret6072162199;
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6135562201 =
                  ((x10.lang.Complex[])t6135362195.value)[t6135462200];
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6071562220 = t6135562201;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6135762221 =
                  ret6071562220;
                
//#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6135662222 =
                  ((double)(int)(((int)(l62212))));
                
//#line 213 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final double t6135862223 =
                  java.lang.Math.pow(((double)(3.0)), ((double)(t6135662222)));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6072562224 =
                  ((x10.lang.Complex)(t6135762221.$times((double)(t6135862223))));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6072762225 =
                   null;
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6135962202 =
                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6072662214).
                                        region));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6136062203 =
                  t6135962202.contains$O((int)(i06072362215),
                                         (int)(i16072462216));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6136162204 =
                  !(t6136062203);
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6136162204) {
                    
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06072362215),
                                                                                                                                                       (int)(i16072462216));
                }
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6137062205 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6072662214).
                                                   raw));
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6073262206 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6072662214).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06072962207 =
                  i06072362215;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16073062208 =
                  i16072462216;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6073362209 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136262183 =
                  this6073262206.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6073162184 =
                  ((i06072962207) - (((int)(t6136262183))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136362185 =
                  offset6073162184;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136462186 =
                  this6073262206.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136562187 =
                  ((t6136362185) * (((int)(t6136462186))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136662188 =
                  ((t6136562187) + (((int)(i16073062208))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136762189 =
                  this6073262206.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136862190 =
                  ((t6136662188) - (((int)(t6136762189))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6073162184 = t6136862190;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6136962191 =
                  offset6073162184;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6073362209 = t6136962191;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6137162210 =
                  ret6073362209;
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6137062205.value)[t6137162210] = v6072562224;
                
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6072762225 = ((x10.lang.Complex)(v6072562224));
            }
            
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
return parentExpansion;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final public au.edu.anu.mm.MultipoleExpansion
                                                                                                                au$edu$anu$mm$MultipoleExpansion$$au$edu$anu$mm$MultipoleExpansion$this(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
return au.edu.anu.mm.MultipoleExpansion.this;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
final private void
                                                                                                                __fieldInitializers38995(
                                                                                                                ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/MultipoleExpansion.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers38995$P(
          final au.edu.anu.mm.MultipoleExpansion MultipoleExpansion){
            MultipoleExpansion.__fieldInitializers38995();
        }
        
    }
    