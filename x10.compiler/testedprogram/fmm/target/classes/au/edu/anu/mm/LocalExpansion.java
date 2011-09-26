package au.edu.anu.mm;


public class LocalExpansion
extends au.edu.anu.mm.Expansion
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LocalExpansion.class);
    
    public static final x10.rtt.RuntimeType<LocalExpansion> $RTT = new x10.rtt.NamedType<LocalExpansion>(
    "au.edu.anu.mm.LocalExpansion", /* base class */LocalExpansion.class
    , /* parents */ new x10.rtt.Type[] {au.edu.anu.mm.Expansion.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(LocalExpansion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        au.edu.anu.mm.Expansion.$_deserialize_body($_obj, $deserializer);
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        LocalExpansion $_obj = new LocalExpansion((java.lang.System[]) null);
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
    public LocalExpansion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public int
          X10$object_lock_id0;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public x10.util.concurrent.OrderedLock
                                                                                                            getOrderedLock(
                                                                                                            ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65153 =
              this.
                X10$object_lock_id0;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.util.concurrent.OrderedLock t65154 =
              x10.util.concurrent.OrderedLock.getLock((int)(t65153));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
return t65154;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                            getStaticOrderedLock(
                                                                                                            ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65155 =
              au.edu.anu.mm.LocalExpansion.X10$class_lock_id1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.util.concurrent.OrderedLock t65156 =
              x10.util.concurrent.OrderedLock.getLock((int)(t65155));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
return t65156;
        }
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.LocalExpansion $make(final int p){return new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null).$init(p);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.LocalExpansion au$edu$anu$mm$LocalExpansion$$init$S(final int p) { {
                                                                                                             
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
super.$init(((int)(p)));
                                                                                                             
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"

                                                                                                             
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion this6466365940 =
                                                                                                               this;
                                                                                                             
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this6466365940.X10$object_lock_id0 = -1;
                                                                                                         }
                                                                                                         return this;
                                                                                                         }
        
        // constructor
        public au.edu.anu.mm.LocalExpansion $init(final int p){return au$edu$anu$mm$LocalExpansion$$init$S(p);}
        
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.LocalExpansion $make(final int p,
                                                         final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null).$init(p,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.LocalExpansion au$edu$anu$mm$LocalExpansion$$init$S(final int p,
                                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                 
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
super.$init(((int)(p)));
                                                                                                                                                 
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"

                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion this6466665941 =
                                                                                                                                                   this;
                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this6466665941.X10$object_lock_id0 = -1;
                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65157 =
                                                                                                                                                   paramLock.getIndex();
                                                                                                                                                 
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this.X10$object_lock_id0 = ((int)(t65157));
                                                                                                                                             }
                                                                                                                                             return this;
                                                                                                                                             }
        
        // constructor
        public au.edu.anu.mm.LocalExpansion $init(final int p,
                                                  final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$LocalExpansion$$init$S(p,paramLock);}
        
        
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.LocalExpansion $make(final au.edu.anu.mm.LocalExpansion source){return new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null).$init(source);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.LocalExpansion au$edu$anu$mm$LocalExpansion$$init$S(final au.edu.anu.mm.LocalExpansion source) { {
                                                                                                                                           
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
super.$init(((au.edu.anu.mm.Expansion)(source)));
                                                                                                                                           
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"

                                                                                                                                           
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion this6466965942 =
                                                                                                                                             this;
                                                                                                                                           
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this6466965942.X10$object_lock_id0 = -1;
                                                                                                                                       }
                                                                                                                                       return this;
                                                                                                                                       }
        
        // constructor
        public au.edu.anu.mm.LocalExpansion $init(final au.edu.anu.mm.LocalExpansion source){return au$edu$anu$mm$LocalExpansion$$init$S(source);}
        
        
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
// creation method for java code
        public static au.edu.anu.mm.LocalExpansion $make(final au.edu.anu.mm.LocalExpansion source,
                                                         final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null).$init(source,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.LocalExpansion au$edu$anu$mm$LocalExpansion$$init$S(final au.edu.anu.mm.LocalExpansion source,
                                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                 
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
super.$init(((au.edu.anu.mm.Expansion)(source)));
                                                                                                                                                 
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"

                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion this6467265943 =
                                                                                                                                                   this;
                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this6467265943.X10$object_lock_id0 = -1;
                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65158 =
                                                                                                                                                   paramLock.getIndex();
                                                                                                                                                 
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this.X10$object_lock_id0 = ((int)(t65158));
                                                                                                                                             }
                                                                                                                                             return this;
                                                                                                                                             }
        
        // constructor
        public au.edu.anu.mm.LocalExpansion $init(final au.edu.anu.mm.LocalExpansion source,
                                                  final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$LocalExpansion$$init$S(source,paramLock);}
        
        
        
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public static au.edu.anu.mm.LocalExpansion
                                                                                                            getMlm(
                                                                                                            final x10x.vector.Tuple3d v,
                                                                                                            final int p){
            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion exp =
              ((au.edu.anu.mm.LocalExpansion)(new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null)));
            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.util.concurrent.OrderedLock t6515966149 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
exp.$init(((int)(p)),
                                                                                                                        t6515966149);
            
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> terms =
              ((x10.array.Array)(exp.
                                   terms));
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10x.polar.Polar3d v_pole =
              x10x.polar.Polar3d.getPolar3d(((x10x.vector.Tuple3d)(v)));
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65160 =
              v_pole.
                theta;
            
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.core.Double> pplm =
              ((x10.array.Array)(au.edu.anu.mm.AssociatedLegendrePolynomial.getPlk((double)(t65160),
                                                                                   (int)(p))));
            
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65161 =
              v_pole.
                r;
            
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double rfac0 =
              ((1.0) / (((double)(t65161))));
            
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex alloc40602 =
              new x10.lang.Complex((java.lang.System[]) null);
            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret64677 =
               0;
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6516266150 =
              ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                    region));
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6516366151 =
              t6516266150.contains$O((int)(0),
                                     (int)(0));
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6516466152 =
              !(t6516366151);
            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6516466152) {
                
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(0),
                                                                                                                                                   (int)(0));
            }
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6517366153 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                               raw));
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6468266154 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                        layout));
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6468366155 =
               0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6516565944 =
              this6468266154.
                min0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6468165945 =
              ((0) - (((int)(t6516565944))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6516665946 =
              offset6468165945;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6516765947 =
              this6468266154.
                delta1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6516865948 =
              ((t6516665946) * (((int)(t6516765947))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6516965949 =
              ((t6516865948) + (((int)(0))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6517065950 =
              this6468266154.
                min1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6517165951 =
              ((t6516965949) - (((int)(t6517065950))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6468165945 = t6517165951;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6517265952 =
              offset6468165945;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6468366155 = t6517265952;
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6517466156 =
              ret6468366155;
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6517566157 =
              ((double[])t6517366153.value)[t6517466156];
            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64677 = t6517566157;
            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t65176 =
              ret64677;
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double real64685 =
              ((rfac0) * (((double)(t65176))));
            
//#line 53 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
alloc40602.re = real64685;
            
//#line 54 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
alloc40602.im = 0.0;
            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v64690 =
              ((x10.lang.Complex)(alloc40602));
            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret64691 =
               null;
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6517766158 =
              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)terms).
                                    region));
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6517866159 =
              t6517766158.contains$O((int)(0),
                                     (int)(0));
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6517966160 =
              !(t6517866159);
            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6517966160) {
                
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(0),
                                                                                                                                                   (int)(0));
            }
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6518866161 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)terms).
                                               raw));
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6469666162 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)terms).
                                        layout));
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6469766163 =
               0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518065953 =
              this6469666162.
                min0;
            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6469565954 =
              ((0) - (((int)(t6518065953))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518165955 =
              offset6469565954;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518265956 =
              this6469666162.
                delta1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518365957 =
              ((t6518165955) * (((int)(t6518265956))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518465958 =
              ((t6518365957) + (((int)(0))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518565959 =
              this6469666162.
                min1;
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518665960 =
              ((t6518465958) - (((int)(t6518565959))));
            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6469565954 = t6518665960;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518765961 =
              offset6469565954;
            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6469766163 = t6518765961;
            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6518966164 =
              ret6469766163;
            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6518866161.value)[t6518966164] = v64690;
            
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64691 = ((x10.lang.Complex)(v64690));
            
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex phifac0 =
              new x10.lang.Complex((java.lang.System[]) null);
            
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65190 =
              v_pole.
                phi;
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double real64699 =
              java.lang.Math.cos(((double)(t65190)));
            
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65191 =
              v_pole.
                phi;
            
//#line 52 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
final double imaginary64700 =
              java.lang.Math.sin(((double)(t65191)));
            
//#line 53 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
phifac0.re = real64699;
            
//#line 54 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Complex.x10"
phifac0.im = imaginary64700;
            
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double rfac =
              ((rfac0) * (((double)(rfac0))));
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double il =
              1.0;
            
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40639max4064166166 =
              p;
            
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4063966146 =
              1;
            
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6519366147 =
                  i4063966146;
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6532066148 =
                  ((t6519366147) <= (((int)(i40639max4064166166))));
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6532066148)) {
                    
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                }
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66143 =
                  i4063966146;
                
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6519666124 =
                  il;
                
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6519766125 =
                  ((double)(int)(((int)(l66143))));
                
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6519866126 =
                  ((t6519666124) * (((double)(t6519766125))));
                
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
il = t6519866126;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double ilm66127 =
                  il;
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
x10.lang.Complex phifac66128 =
                  x10.lang.Complex.getInitialized$ONE();
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06471266129 =
                  l66143;
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6521766130 =
                  phifac66128;
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6521366131 =
                  rfac;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06470266132 =
                  l66143;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6470466133 =
                   0;
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6519966103 =
                  ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                        region));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6520066104 =
                  t6519966103.contains$O((int)(i06470266132),
                                         (int)(0));
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6520166105 =
                  !(t6520066104);
                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6520166105) {
                    
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06470266132),
                                                                                                                                                       (int)(0));
                }
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6521066106 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                                   raw));
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6470966107 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06470666108 =
                  i06470266132;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6471066109 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520265962 =
                  this6470966107.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6470865963 =
                  ((i06470666108) - (((int)(t6520265962))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520365964 =
                  offset6470865963;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520465965 =
                  this6470966107.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520565966 =
                  ((t6520365964) * (((int)(t6520465965))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520665967 =
                  ((t6520565966) + (((int)(0))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520765968 =
                  this6470966107.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520865969 =
                  ((t6520665967) - (((int)(t6520765968))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6470865963 = t6520865969;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6520965970 =
                  offset6470865963;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6471066109 = t6520965970;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6521166110 =
                  ret6471066109;
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6521266111 =
                  ((double[])t6521066106.value)[t6521166110];
                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6470466133 = t6521266111;
                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6521466134 =
                  ret6470466133;
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6521566135 =
                  ((t6521366131) * (((double)(t6521466134))));
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6521666136 =
                  ilm66127;
                
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6521866137 =
                  ((t6521566135) * (((double)(t6521666136))));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6471466138 =
                  ((x10.lang.Complex)(t6521766130.$times((double)(t6521866137))));
                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6471566139 =
                   null;
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6521966112 =
                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)terms).
                                        region));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6522066113 =
                  t6521966112.contains$O((int)(i06471266129),
                                         (int)(0));
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6522166114 =
                  !(t6522066113);
                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6522166114) {
                    
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06471266129),
                                                                                                                                                       (int)(0));
                }
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6523066115 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)terms).
                                                   raw));
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6472066116 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)terms).
                                            layout));
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06471766117 =
                  i06471266129;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6472166118 =
                   0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522265971 =
                  this6472066116.
                    min0;
                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6471965972 =
                  ((i06471766117) - (((int)(t6522265971))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522365973 =
                  offset6471965972;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522465974 =
                  this6472066116.
                    delta1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522565975 =
                  ((t6522365973) * (((int)(t6522465974))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522665976 =
                  ((t6522565975) + (((int)(0))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522765977 =
                  this6472066116.
                    min1;
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522865978 =
                  ((t6522665976) - (((int)(t6522765977))));
                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6471965972 = t6522865978;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6522965979 =
                  offset6471965972;
                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6472166118 = t6522965979;
                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6523166119 =
                  ret6472166118;
                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6523066115.value)[t6523166119] = v6471466138;
                
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6471566139 = ((x10.lang.Complex)(v6471466138));
                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
boolean m_sign66140 =
                  false;
                
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40607max4060966121 =
                  l66143;
                
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4060766043 =
                  1;
                
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6523366044 =
                      i4060766043;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6527866045 =
                      ((t6523366044) <= (((int)(i40607max4060966121))));
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6527866045)) {
                        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int m66040 =
                      i4060766043;
                    
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6523866017 =
                      ilm66127;
                    
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6523666018 =
                      ((l66143) + (((int)(1))));
                    
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6523766019 =
                      ((t6523666018) - (((int)(m66040))));
                    
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6523966020 =
                      ((double)(int)(((int)(t6523766019))));
                    
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6524066021 =
                      ((t6523866017) / (((double)(t6523966020))));
                    
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
ilm66127 = t6524066021;
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6524166022 =
                      phifac66128;
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6524266023 =
                      t6524166022.$times(((x10.lang.Complex)(phifac0)));
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
phifac66128 = t6524266023;
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6526166024 =
                      phifac66128;
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6525766025 =
                      rfac;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06472366026 =
                      l66143;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16472466027 =
                      m66040;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6472566028 =
                       0;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6524365998 =
                      ((x10.array.Region)(((x10.array.Array<x10.core.Double>)pplm).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6524465999 =
                      t6524365998.contains$O((int)(i06472366026),
                                             (int)(i16472466027));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6524566000 =
                      !(t6524465999);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6524566000) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06472366026),
                                                                                                                                                           (int)(i16472466027));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6525466001 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)pplm).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6473066002 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)pplm).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06472766003 =
                      i06472366026;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16472866004 =
                      i16472466027;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6473166005 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6524665980 =
                      this6473066002.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6472965981 =
                      ((i06472766003) - (((int)(t6524665980))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6524765982 =
                      offset6472965981;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6524865983 =
                      this6473066002.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6524965984 =
                      ((t6524765982) * (((int)(t6524865983))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6525065985 =
                      ((t6524965984) + (((int)(i16472866004))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6525165986 =
                      this6473066002.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6525265987 =
                      ((t6525065985) - (((int)(t6525165986))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6472965981 = t6525265987;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6525365988 =
                      offset6472965981;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6473166005 = t6525365988;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6525566006 =
                      ret6473166005;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6525666007 =
                      ((double[])t6525466001.value)[t6525566006];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6472566028 = t6525666007;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6525866029 =
                      ret6472566028;
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6525966030 =
                      ((t6525766025) * (((double)(t6525866029))));
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6526066031 =
                      ilm66127;
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6526266032 =
                      ((t6525966030) * (((double)(t6526066031))));
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex M_lm66033 =
                      t6526166024.$times((double)(t6526266032));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06473366034 =
                      l66143;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16473466035 =
                      m66040;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6473566036 =
                      ((x10.lang.Complex)(M_lm66033));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6473666037 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6526366008 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)terms).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6526466009 =
                      t6526366008.contains$O((int)(i06473366034),
                                             (int)(i16473466035));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6526566010 =
                      !(t6526466009);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6526566010) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06473366034),
                                                                                                                                                           (int)(i16473466035));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6527466011 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)terms).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6474166012 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)terms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06473866013 =
                      i06473366034;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16473966014 =
                      i16473466035;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6474266015 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6526665989 =
                      this6474166012.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6474065990 =
                      ((i06473866013) - (((int)(t6526665989))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6526765991 =
                      offset6474065990;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6526865992 =
                      this6474166012.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6526965993 =
                      ((t6526765991) * (((int)(t6526865992))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6527065994 =
                      ((t6526965993) + (((int)(i16473966014))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6527165995 =
                      this6474166012.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6527265996 =
                      ((t6527065994) - (((int)(t6527165995))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6474065990 = t6527265996;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6527365997 =
                      offset6474065990;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6474266015 = t6527365997;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6527566016 =
                      ret6474266015;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6527466011.value)[t6527566016] = v6473566036;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6473666037 = ((x10.lang.Complex)(v6473566036));
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6527666038 =
                      m_sign66140;
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6527766039 =
                      !(t6527666038);
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
m_sign66140 = t6527766039;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6523466041 =
                      i4060766043;
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6523566042 =
                      ((t6523466041) + (((int)(1))));
                    
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4060766043 = t6523566042;
                }
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40623min4062466122 =
                  (-(l66143));
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4062366100 =
                  i40623min4062466122;
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6528066101 =
                      i4062366100;
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6531766102 =
                      ((t6528066101) <= (((int)(-1))));
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6531766102)) {
                        
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int m66097 =
                      i4062366100;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06475466083 =
                      l66143;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16475566084 =
                      m66097;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06474466085 =
                      l66143;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16474566086 =
                      (-(m66097));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6474666087 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6528366064 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)terms).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6528466065 =
                      t6528366064.contains$O((int)(i06474466085),
                                             (int)(i16474566086));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6528566066 =
                      !(t6528466065);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6528566066) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06474466085),
                                                                                                                                                           (int)(i16474566086));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6529466067 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)terms).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6475166068 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)terms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06474866069 =
                      i06474466085;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16474966070 =
                      i16474566086;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6475266071 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6528666046 =
                      this6475166068.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6475066047 =
                      ((i06474866069) - (((int)(t6528666046))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6528766048 =
                      offset6475066047;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6528866049 =
                      this6475166068.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6528966050 =
                      ((t6528766048) * (((int)(t6528866049))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6529066051 =
                      ((t6528966050) + (((int)(i16474966070))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6529166052 =
                      this6475166068.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6529266053 =
                      ((t6529066051) - (((int)(t6529166052))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6475066047 = t6529266053;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6529366054 =
                      offset6475066047;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6475266071 = t6529366054;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6529566072 =
                      ret6475266071;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6529666073 =
                      ((x10.lang.Complex[])t6529466067.value)[t6529566072];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6474666087 = t6529666073;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6529766088 =
                      ret6474666087;
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6530266089 =
                      t6529766088.conjugate();
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6529866090 =
                      (-(m66097));
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6529966091 =
                      ((t6529866090) % (((int)(2))));
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6530066092 =
                      ((2) * (((int)(t6529966091))));
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6530166093 =
                      ((1) - (((int)(t6530066092))));
                    
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6530366094 =
                      ((double)(int)(((int)(t6530166093))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6475666095 =
                      ((x10.lang.Complex)(t6530266089.$times((double)(t6530366094))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6475766096 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6530466074 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)terms).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6530566075 =
                      t6530466074.contains$O((int)(i06475466083),
                                             (int)(i16475566084));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6530666076 =
                      !(t6530566075);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6530666076) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06475466083),
                                                                                                                                                           (int)(i16475566084));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6531566077 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)terms).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6476266078 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)terms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06475966079 =
                      i06475466083;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16476066080 =
                      i16475566084;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6476366081 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6530766055 =
                      this6476266078.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6476166056 =
                      ((i06475966079) - (((int)(t6530766055))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6530866057 =
                      offset6476166056;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6530966058 =
                      this6476266078.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6531066059 =
                      ((t6530866057) * (((int)(t6530966058))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6531166060 =
                      ((t6531066059) + (((int)(i16476066080))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6531266061 =
                      this6476266078.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6531366062 =
                      ((t6531166060) - (((int)(t6531266061))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6476166056 = t6531366062;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6531466063 =
                      offset6476166056;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6476366081 = t6531466063;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6531666082 =
                      ret6476366081;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6531566077.value)[t6531666082] = v6475666095;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6475766096 = ((x10.lang.Complex)(v6475666095));
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6528166098 =
                      i4062366100;
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6528266099 =
                      ((t6528166098) + (((int)(1))));
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4062366100 = t6528266099;
                }
                
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6531866141 =
                  rfac;
                
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6531966142 =
                  ((t6531866141) * (((double)(rfac0))));
                
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
rfac = t6531966142;
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6519466144 =
                  i4063966146;
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6519566145 =
                  ((t6519466144) + (((int)(1))));
                
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4063966146 = t6519566145;
            }
            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
return exp;
        }
        
        
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public void
                                                                                                            translateAndAddLocal(
                                                                                                            final au.edu.anu.mm.MultipoleExpansion shift,
                                                                                                            final au.edu.anu.mm.LocalExpansion source){
            
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40703max40705 =
              p;
            
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4070366293 =
              0;
            
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6532266294 =
                  i4070366293;
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6540066295 =
                  ((t6532266294) <= (((int)(i40703max40705))));
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6540066295)) {
                    
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                }
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66290 =
                  i4070366293;
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40687min4068866288 =
                  (-(l66290));
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40687max4068966289 =
                  l66290;
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4068766285 =
                  i40687min4068866288;
                
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6532666286 =
                      i4068766285;
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6539966287 =
                      ((t6532666286) <= (((int)(i40687max4068966289))));
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6539966287)) {
                        
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int m66282 =
                      i4068766285;
                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40671min4067266280 =
                      l66290;
                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40671max4067366281 =
                      p;
                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4067166277 =
                      i40671min4067266280;
                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                           true;
                                                                                                                           ) {
                        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533066278 =
                          i4067166277;
                        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6539866279 =
                          ((t6533066278) <= (((int)(i40671max4067366281))));
                        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6539866279)) {
                            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                        }
                        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int j66274 =
                          i4067166277;
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533366269 =
                          ((l66290) - (((int)(j66274))));
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40655min4065666270 =
                          ((t6533366269) + (((int)(m66282))));
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533466271 =
                          (-(l66290));
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533566272 =
                          ((t6533466271) + (((int)(j66274))));
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40655max4065766273 =
                          ((t6533566272) + (((int)(m66282))));
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4065566266 =
                          i40655min4065666270;
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                               true;
                                                                                                                               ) {
                            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533766267 =
                              i4065566266;
                            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6539766268 =
                              ((t6533766267) <= (((int)(i40655max4065766273))));
                            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6539766268)) {
                                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                            }
                            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int k66263 =
                              i4065566266;
                            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6476766242 =
                              ((x10.array.Array)(shift.
                                                   terms));
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06476566243 =
                              ((j66274) - (((int)(l66290))));
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16476666244 =
                              ((k66263) - (((int)(m66282))));
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6476866245 =
                               null;
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6534066203 =
                              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6476766242).
                                                    region));
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6534166204 =
                              t6534066203.contains$O((int)(i06476566243),
                                                     (int)(i16476666244));
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6534266205 =
                              !(t6534166204);
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6534266205) {
                                
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06476566243),
                                                                                                                                                                   (int)(i16476666244));
                            }
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6535166206 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6476766242).
                                                               raw));
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6477366207 =
                              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6476766242).
                                                        layout));
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06477066208 =
                              i06476566243;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16477166209 =
                              i16476666244;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6477466210 =
                               0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6534366167 =
                              this6477366207.
                                min0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6477266168 =
                              ((i06477066208) - (((int)(t6534366167))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6534466169 =
                              offset6477266168;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6534566170 =
                              this6477366207.
                                delta1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6534666171 =
                              ((t6534466169) * (((int)(t6534566170))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6534766172 =
                              ((t6534666171) + (((int)(i16477166209))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6534866173 =
                              this6477366207.
                                min1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6534966174 =
                              ((t6534766172) - (((int)(t6534866173))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6477266168 = t6534966174;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6535066175 =
                              offset6477266168;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6477466210 = t6535066175;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6535266211 =
                              ret6477466210;
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6535366212 =
                              ((x10.lang.Complex[])t6535166206.value)[t6535266211];
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6476866245 = t6535366212;
                            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex C_lmjk66246 =
                              ret6476866245;
                            
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6477866247 =
                              ((x10.array.Array)(source.
                                                   terms));
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06477666248 =
                              j66274;
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16477766249 =
                              k66263;
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6477966250 =
                               null;
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6535466213 =
                              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6477866247).
                                                    region));
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6535566214 =
                              t6535466213.contains$O((int)(i06477666248),
                                                     (int)(i16477766249));
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6535666215 =
                              !(t6535566214);
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6535666215) {
                                
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06477666248),
                                                                                                                                                                   (int)(i16477766249));
                            }
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6536566216 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6477866247).
                                                               raw));
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6478466217 =
                              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6477866247).
                                                        layout));
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06478166218 =
                              i06477666248;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16478266219 =
                              i16477766249;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6478566220 =
                               0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6535766176 =
                              this6478466217.
                                min0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6478366177 =
                              ((i06478166218) - (((int)(t6535766176))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6535866178 =
                              offset6478366177;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6535966179 =
                              this6478466217.
                                delta1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6536066180 =
                              ((t6535866178) * (((int)(t6535966179))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6536166181 =
                              ((t6536066180) + (((int)(i16478266219))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6536266182 =
                              this6478466217.
                                min1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6536366183 =
                              ((t6536166181) - (((int)(t6536266182))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6478366177 = t6536366183;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6536466184 =
                              offset6478366177;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6478566220 = t6536466184;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6536666221 =
                              ret6478566220;
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6536766222 =
                              ((x10.lang.Complex[])t6536566216.value)[t6536666221];
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6477966250 = t6536766222;
                            
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex O_jk66251 =
                              ret6477966250;
                            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6480166252 =
                              ((x10.array.Array)(this.
                                                   terms));
                            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06479866253 =
                              l66290;
                            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16479966254 =
                              m66282;
                            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6478966255 =
                              ((x10.array.Array)(this.
                                                   terms));
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06478766256 =
                              l66290;
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16478866257 =
                              m66282;
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6479066258 =
                               null;
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6536866223 =
                              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6478966255).
                                                    region));
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6536966224 =
                              t6536866223.contains$O((int)(i06478766256),
                                                     (int)(i16478866257));
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6537066225 =
                              !(t6536966224);
                            
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6537066225) {
                                
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06478766256),
                                                                                                                                                                   (int)(i16478866257));
                            }
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6537966226 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6478966255).
                                                               raw));
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6479566227 =
                              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6478966255).
                                                        layout));
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06479266228 =
                              i06478766256;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16479366229 =
                              i16478866257;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6479666230 =
                               0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537166185 =
                              this6479566227.
                                min0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6479466186 =
                              ((i06479266228) - (((int)(t6537166185))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537266187 =
                              offset6479466186;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537366188 =
                              this6479566227.
                                delta1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537466189 =
                              ((t6537266187) * (((int)(t6537366188))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537566190 =
                              ((t6537466189) + (((int)(i16479366229))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537666191 =
                              this6479566227.
                                min1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537766192 =
                              ((t6537566190) - (((int)(t6537666191))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6479466186 = t6537766192;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6537866193 =
                              offset6479466186;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6479666230 = t6537866193;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6538066231 =
                              ret6479666230;
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6538166232 =
                              ((x10.lang.Complex[])t6537966226.value)[t6538066231];
                            
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6479066258 = t6538166232;
                            
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6538266259 =
                              ret6479066258;
                            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6538366260 =
                              C_lmjk66246.$times(((x10.lang.Complex)(O_jk66251)));
                            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6480066261 =
                              ((x10.lang.Complex)(t6538266259.$plus(((x10.lang.Complex)(t6538366260)))));
                            
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6480266262 =
                               null;
                            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6538466233 =
                              ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6480166252).
                                                    region));
                            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6538566234 =
                              t6538466233.contains$O((int)(i06479866253),
                                                     (int)(i16479966254));
                            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6538666235 =
                              !(t6538566234);
                            
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6538666235) {
                                
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06479866253),
                                                                                                                                                                   (int)(i16479966254));
                            }
                            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6539566236 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6480166252).
                                                               raw));
                            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6480766237 =
                              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6480166252).
                                                        layout));
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06480466238 =
                              i06479866253;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16480566239 =
                              i16479966254;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6480866240 =
                               0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6538766194 =
                              this6480766237.
                                min0;
                            
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6480666195 =
                              ((i06480466238) - (((int)(t6538766194))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6538866196 =
                              offset6480666195;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6538966197 =
                              this6480766237.
                                delta1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6539066198 =
                              ((t6538866196) * (((int)(t6538966197))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6539166199 =
                              ((t6539066198) + (((int)(i16480566239))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6539266200 =
                              this6480766237.
                                min1;
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6539366201 =
                              ((t6539166199) - (((int)(t6539266200))));
                            
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6480666195 = t6539366201;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6539466202 =
                              offset6480666195;
                            
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6480866240 = t6539466202;
                            
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6539666241 =
                              ret6480866240;
                            
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6539566236.value)[t6539666241] = v6480066261;
                            
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6480266262 = ((x10.lang.Complex)(v6480066261));
                            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533866264 =
                              i4065566266;
                            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533966265 =
                              ((t6533866264) + (((int)(1))));
                            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4065566266 = t6533966265;
                        }
                        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533166275 =
                          i4067166277;
                        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6533266276 =
                          ((t6533166275) + (((int)(1))));
                        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4067166277 = t6533266276;
                    }
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6532766283 =
                      i4068766285;
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6532866284 =
                      ((t6532766283) + (((int)(1))));
                    
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4068766285 = t6532866284;
                }
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6532366291 =
                  i4070366293;
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6532466292 =
                  ((t6532366291) + (((int)(1))));
                
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4070366293 = t6532466292;
            }
        }
        
        
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public void
                                                                                                             translateAndAddLocal_1_$_x10$array$Array$_x10$lang$Complex_$_$_3_$_x10$array$Array$_x10$array$Array$_x10$lang$Double_$_$_$(
                                                                                                             final x10x.vector.Vector3d v,
                                                                                                             final x10.array.Array complexK,
                                                                                                             final au.edu.anu.mm.LocalExpansion source,
                                                                                                             final x10.array.Array wigner){
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65401 =
              v.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65402 =
              v.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65405 =
              ((t65401) * (((double)(t65402))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65403 =
              v.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65404 =
              v.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65406 =
              ((t65403) * (((double)(t65404))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65409 =
              ((t65405) + (((double)(t65406))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65407 =
              v.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65408 =
              v.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65410 =
              ((t65407) * (((double)(t65408))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65411 =
              ((t65409) + (((double)(t65410))));
            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double b =
              java.lang.Math.sqrt(((double)(t65411)));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> alloc40603 =
              ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65412 =
              p;
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65413 =
              (-(t65412));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65414 =
              p;
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.IntRange t65415 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t65413)), ((int)(t65414)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg64810 =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t65415)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.x10$lang$Object$$init$S();
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__648116481566300 =
              ((x10.array.Region)(((x10.array.Region)
                                    reg64810)));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6481666301 =
               null;
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6541666296 =
              ((__desugarer__var__0__648116481566300) != (null));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6541966297 =
              !(t6541666296);
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6541966297) {
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6541866298 =
                  true;
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6541866298) {
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6541766299 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6541766299;
                }
            }
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6481666301 = ((x10.array.Region)(__desugarer__var__0__648116481566300));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6542066302 =
              ((x10.array.Region)(ret6481666301));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.region = ((x10.array.Region)(t6542066302));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6542166303 =
              reg64810.
                rank;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.rank = t6542166303;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6542266304 =
              reg64810.
                rect;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.rect = t6542266304;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6542366305 =
              reg64810.
                zeroBased;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.zeroBased = t6542366305;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6542466306 =
              reg64810.
                rail;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.rail = t6542466306;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6542566307 =
              reg64810.size$O();
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.size = t6542566307;
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646481266475 =
              new x10.array.RectLayout((java.lang.System[]) null);
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646481266475.$init(((x10.array.Region)(reg64810)));
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.layout = ((x10.array.RectLayout)(alloc199646481266475));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6481866476 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc40603).
                                        layout));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6481366477 =
              this6481866476.
                size;
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6542666478 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6481366477)), true)));
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40603.raw = ((x10.core.IndexedMemoryChunk)(t6542666478));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> __desugarer__var__30__64819 =
              ((x10.array.Array)(((x10.array.Array<x10.lang.Complex>)
                                   alloc40603)));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
x10.array.Array<x10.lang.Complex> ret64820 =
               null;
            
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6542766479 =
              ((x10.array.Array<x10.lang.Complex>)__desugarer__var__30__64819).
                rect;
            
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
boolean t6542966480 =
              ((boolean) t6542766479) ==
            ((boolean) true);
            
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6542966480) {
                
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6542866481 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__30__64819).
                    rail;
                
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
t6542966480 = ((boolean) t6542866481) ==
                ((boolean) false);
            }
            
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
boolean t6543166482 =
              t6542966480;
            
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6543166482) {
                
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6543066483 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__30__64819).
                    rank;
                
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
t6543166482 = ((int) t6543066483) ==
                ((int) 1);
            }
            
//#line 109 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6543266484 =
              t6543166482;
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6543566485 =
              !(t6543266484);
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6543566485) {
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6543466486 =
                  true;
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6543466486) {
                    
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.FailedDynamicCheckException t6543366487 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Complex]{self.rect==true, self.rail==false, self.rank==1}");
                    
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
throw t6543366487;
                }
            }
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
ret64820 = ((x10.array.Array)(__desugarer__var__30__64819));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> temp =
              ((x10.array.Array)(ret64820));
            
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion scratch =
              new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null);
            
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.util.concurrent.OrderedLock t6543666488 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
scratch.$init(((au.edu.anu.mm.LocalExpansion)(source)),
                                                                                                                             t6543666488);
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret64823 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret64824: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t65437 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65438 =
              ((x10.array.Array)(((x10.array.Array[])t65437.value)[1]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64823 = ((x10.array.Array)(t65438));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret64824;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65441 =
              ((x10.array.Array)(ret64823));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret64831 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret64832: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t65439 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)wigner).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65440 =
              ((x10.array.Array)(((x10.array.Array[])t65439.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64831 = ((x10.array.Array)(t65440));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret64832;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65442 =
              ((x10.array.Array)(ret64831));
            
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
scratch.rotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                     ((x10.array.Array)(t65441)),
                                                                                                                                                                                                                     ((x10.array.Array)(t65442)));
            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> targetTerms =
              ((x10.array.Array)(scratch.
                                   terms));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int m_sign =
              1;
            
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40767max4076966490 =
              p;
            
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4076766472 =
              0;
            
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6544466473 =
                  i4076766472;
                
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6554966474 =
                  ((t6544466473) <= (((int)(i40767max4076966490))));
                
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6554966474)) {
                    
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                }
                
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int m66469 =
                  i4076766472;
                
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40719min4072066463 =
                  m66469;
                
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40719max4072166464 =
                  p;
                
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4071966347 =
                  i40719min4072066463;
                
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                    
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6544866348 =
                      i4071966347;
                    
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6547266349 =
                      ((t6544866348) <= (((int)(i40719max4072166464))));
                    
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6547266349)) {
                        
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66344 =
                      i4071966347;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06484866338 =
                      l66344;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06483866339 =
                      l66344;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16483966340 =
                      m66469;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6484066341 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6545166328 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6545266329 =
                      t6545166328.contains$O((int)(i06483866339),
                                             (int)(i16483966340));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6545366330 =
                      !(t6545266329);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6545366330) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06483866339),
                                                                                                                                                           (int)(i16483966340));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6546266331 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6484566332 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06484266333 =
                      i06483866339;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16484366334 =
                      i16483966340;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6484666335 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6545466308 =
                      this6484566332.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6484466309 =
                      ((i06484266333) - (((int)(t6545466308))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6545566310 =
                      offset6484466309;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6545666311 =
                      this6484566332.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6545766312 =
                      ((t6545566310) * (((int)(t6545666311))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6545866313 =
                      ((t6545766312) + (((int)(i16484366334))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6545966314 =
                      this6484566332.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6546066315 =
                      ((t6545866313) - (((int)(t6545966314))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6484466309 = t6546066315;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6546166316 =
                      offset6484466309;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6484666335 = t6546166316;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6546366336 =
                      ret6484666335;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6546466337 =
                      ((x10.lang.Complex[])t6546266331.value)[t6546366336];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6484066341 = t6546466337;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6484966342 =
                      ((x10.lang.Complex)(ret6484066341));
                    
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6485066343 =
                       null;
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6546566320 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                            region));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6546666321 =
                      t6546566320.contains$O((int)(i06484866338));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6546766322 =
                      !(t6546666321);
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6546766322) {
                        
//#line 515 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06484866338));
                    }
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6547066323 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                       raw));
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6485466324 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                layout));
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06485266325 =
                      i06484866338;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6485566326 =
                       0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6546866317 =
                      this6485466324.
                        min0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6485366318 =
                      ((i06485266325) - (((int)(t6546866317))));
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6546966319 =
                      offset6485366318;
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6485566326 = t6546966319;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6547166327 =
                      ret6485566326;
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6547066323.value)[t6547166327] = v6484966342;
                    
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6485066343 = ((x10.lang.Complex)(v6484966342));
                    
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6544966345 =
                      i4071966347;
                    
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6545066346 =
                      ((t6544966345) + (((int)(1))));
                    
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4071966347 = t6545066346;
                }
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40751min4075266465 =
                  m66469;
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40751max4075366466 =
                  p;
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4075166460 =
                  i40751min4075266465;
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6547466461 =
                      i4075166460;
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6554666462 =
                      ((t6547466461) <= (((int)(i40751max4075366466))));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6554666462)) {
                        
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66457 =
                      i4075166460;
                    
//#line 120 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
x10.lang.Complex M_lm66439 =
                      x10.lang.Complex.getInitialized$ZERO();
                    
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double F_lm66440 =
                      1.0;
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40735min4073666428 =
                      l66457;
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40735max4073766429 =
                      p;
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4073566379 =
                      i40735min4073666428;
                    
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6547866380 =
                          i4073566379;
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6550066381 =
                          ((t6547866380) <= (((int)(i40735max4073766429))));
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6550066381)) {
                            
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                        }
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int j66376 =
                          i4073566379;
                        
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6549166353 =
                          M_lm66439;
                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06485766354 =
                          j66376;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6485866355 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6485966356: {
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6548166357 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                                region));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6548266358 =
                          t6548166357.contains$O((int)(i06485766354));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6548366359 =
                          !(t6548266358);
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6548366359) {
                            
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06485766354));
                        }
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6548666360 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                           raw));
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6486266361 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                    layout));
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06486066362 =
                          i06485766354;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6486366363 =
                           0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6548466350 =
                          this6486266361.
                            min0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6486166351 =
                          ((i06486066362) - (((int)(t6548466350))));
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6548566352 =
                          offset6486166351;
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6486366363 = t6548566352;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6548766364 =
                          ret6486366363;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6548866365 =
                          ((x10.lang.Complex[])t6548666360.value)[t6548766364];
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6485866355 = t6548866365;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6485966356;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6548966366 =
                          ret6485866355;
                        
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6549066367 =
                          F_lm66440;
                        
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6549266368 =
                          t6548966366.$times((double)(t6549066367));
                        
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6549366369 =
                          t6549166353.$plus(((x10.lang.Complex)(t6549266368)));
                        
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
M_lm66439 = t6549366369;
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6549466370 =
                          F_lm66440;
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6549766371 =
                          ((t6549466370) * (((double)(b))));
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6549566372 =
                          ((j66376) - (((int)(l66457))));
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6549666373 =
                          ((t6549566372) + (((int)(1))));
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6549866374 =
                          ((double)(int)(((int)(t6549666373))));
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6549966375 =
                          ((t6549766371) / (((double)(t6549866374))));
                        
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
F_lm66440 = t6549966375;
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6547966377 =
                          i4073566379;
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6548066378 =
                          ((t6547966377) + (((int)(1))));
                        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4073566379 = t6548066378;
                    }
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06486566441 =
                      l66457;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16486666442 =
                      m66469;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6486766443 =
                      ((x10.lang.Complex)(M_lm66439));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6486866444 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6550166430 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6550266431 =
                      t6550166430.contains$O((int)(i06486566441),
                                             (int)(i16486666442));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6550366432 =
                      !(t6550266431);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6550366432) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06486566441),
                                                                                                                                                           (int)(i16486666442));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6551266433 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6487366434 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06487066435 =
                      i06486566441;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16487166436 =
                      i16486666442;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6487466437 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6550466382 =
                      this6487366434.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6487266383 =
                      ((i06487066435) - (((int)(t6550466382))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6550566384 =
                      offset6487266383;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6550666385 =
                      this6487366434.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6550766386 =
                      ((t6550566384) * (((int)(t6550666385))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6550866387 =
                      ((t6550766386) + (((int)(i16487166436))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6550966388 =
                      this6487366434.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6551066389 =
                      ((t6550866387) - (((int)(t6550966388))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6487266383 = t6551066389;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6551166390 =
                      offset6487266383;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6487466437 = t6551166390;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6551366438 =
                      ret6487466437;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6551266433.value)[t6551366438] = v6486766443;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6486866444 = ((x10.lang.Complex)(v6486766443));
                    
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6554566445 =
                      ((int) m66469) !=
                    ((int) 0);
                    
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6554566445) {
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06488666446 =
                          l66457;
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16488766447 =
                          (-(m66469));
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06487666448 =
                          l66457;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16487766449 =
                          m66469;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6487866450 =
                           null;
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6551466409 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                region));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6551566410 =
                          t6551466409.contains$O((int)(i06487666448),
                                                 (int)(i16487766449));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6551666411 =
                          !(t6551566410);
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6551666411) {
                            
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06487666448),
                                                                                                                                                               (int)(i16487766449));
                        }
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6552566412 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                           raw));
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6488366413 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06488066414 =
                          i06487666448;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16488166415 =
                          i16487766449;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6488466416 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6551766391 =
                          this6488366413.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6488266392 =
                          ((i06488066414) - (((int)(t6551766391))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6551866393 =
                          offset6488266392;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6551966394 =
                          this6488366413.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6552066395 =
                          ((t6551866393) * (((int)(t6551966394))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6552166396 =
                          ((t6552066395) + (((int)(i16488166415))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6552266397 =
                          this6488366413.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6552366398 =
                          ((t6552166396) - (((int)(t6552266397))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6488266392 = t6552366398;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6552466399 =
                          offset6488266392;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6488466416 = t6552466399;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6552666417 =
                          ret6488466416;
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6552766418 =
                          ((x10.lang.Complex[])t6552566412.value)[t6552666417];
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6487866450 = t6552766418;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6552866451 =
                          ret6487866450;
                        
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6553066452 =
                          t6552866451.conjugate();
                        
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6552966453 =
                          m_sign;
                        
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6553166454 =
                          ((double)(int)(((int)(t6552966453))));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6488866455 =
                          ((x10.lang.Complex)(t6553066452.$times((double)(t6553166454))));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6488966456 =
                           null;
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6553266419 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                region));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6553366420 =
                          t6553266419.contains$O((int)(i06488666446),
                                                 (int)(i16488766447));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6553466421 =
                          !(t6553366420);
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6553466421) {
                            
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06488666446),
                                                                                                                                                               (int)(i16488766447));
                        }
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6554366422 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                           raw));
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6489466423 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06489166424 =
                          i06488666446;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16489266425 =
                          i16488766447;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6489566426 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6553566400 =
                          this6489466423.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6489366401 =
                          ((i06489166424) - (((int)(t6553566400))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6553666402 =
                          offset6489366401;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6553766403 =
                          this6489466423.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6553866404 =
                          ((t6553666402) * (((int)(t6553766403))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6553966405 =
                          ((t6553866404) + (((int)(i16489266425))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6554066406 =
                          this6489466423.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6554166407 =
                          ((t6553966405) - (((int)(t6554066406))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6489366401 = t6554166407;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6554266408 =
                          offset6489366401;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6489566426 = t6554266408;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6554466427 =
                          ret6489566426;
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6554366422.value)[t6554466427] = v6488866455;
                        
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6488966456 = ((x10.lang.Complex)(v6488866455));
                    }
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6547566458 =
                      i4075166460;
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6547666459 =
                      ((t6547566458) + (((int)(1))));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4075166460 = t6547666459;
                }
                
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6554766467 =
                  m_sign;
                
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6554866468 =
                  (-(t6554766467));
                
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
m_sign = t6554866468;
                
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6544566470 =
                  i4076766472;
                
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6544666471 =
                  ((t6544566470) + (((int)(1))));
                
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4076766472 = t6544666471;
            }
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret64898 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret64899: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t65550 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65551 =
              ((x10.array.Array)(((x10.array.Array[])t65550.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64898 = ((x10.array.Array)(t65551));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret64899;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65554 =
              ((x10.array.Array)(ret64898));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret64906 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret64907: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t65552 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)wigner).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65553 =
              ((x10.array.Array)(((x10.array.Array[])t65552.value)[1]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64906 = ((x10.array.Array)(t65553));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret64907;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65555 =
              ((x10.array.Array)(ret64906));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
scratch.backRotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                         ((x10.array.Array)(t65554)),
                                                                                                                                                                                                                         ((x10.array.Array)(t65555)));
            
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this.unsafeAdd(((au.edu.anu.mm.Expansion)(scratch)));
        }
        
        
//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public void
                                                                                                             translateAndAddLocal(
                                                                                                             final x10x.vector.Vector3d v,
                                                                                                             final au.edu.anu.mm.LocalExpansion source){
            
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10x.polar.Polar3d polar =
              x10x.polar.Polar3d.getPolar3d(((x10x.vector.Tuple3d)(v)));
            
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65556 =
              polar.
                phi;
            
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65557 =
              p;
            
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.array.Array<x10.lang.Complex>> t65558 =
              ((x10.array.Array)(au.edu.anu.mm.Expansion.genComplexK((double)(t65556),
                                                                     (int)(t65557))));
            
//#line 187 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final double theta64913 =
              polar.
                theta;
            
//#line 187 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final int numTerms64914 =
              p;
            
//#line 187 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>> t65559 =
              ((x10.array.Array)(au.edu.anu.mm.WignerRotationMatrix.getExpandedCollection((double)(theta64913),
                                                                                          (int)(numTerms64914),
                                                                                          (int)(2))));
            
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this.translateAndAddLocal_1_$_x10$array$Array$_x10$lang$Complex_$_$_3_$_x10$array$Array$_x10$array$Array$_x10$lang$Double_$_$_$(((x10x.vector.Vector3d)(v)),
                                                                                                                                                                                                                                               ((x10.array.Array)(t65558)),
                                                                                                                                                                                                                                               ((au.edu.anu.mm.LocalExpansion)(source)),
                                                                                                                                                                                                                                               ((x10.array.Array)(t65559)));
        }
        
        
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public void
                                                                                                             transformAndAddToLocal(
                                                                                                             final au.edu.anu.mm.LocalExpansion transform,
                                                                                                             final au.edu.anu.mm.MultipoleExpansion source){
            
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40831max40833 =
              p;
            
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4083166621 =
              0;
            
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6556166622 =
                  i4083166621;
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6564266623 =
                  ((t6556166622) <= (((int)(i40831max40833))));
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6564266623)) {
                    
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                }
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int j66618 =
                  i4083166621;
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40815min4081666616 =
                  (-(j66618));
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40815max4081766617 =
                  j66618;
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4081566613 =
                  i40815min4081666616;
                
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6556566614 =
                      i4081566613;
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6564166615 =
                      ((t6556566614) <= (((int)(i40815max4081766617))));
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6564166615)) {
                        
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int k66610 =
                      i4081566613;
                    
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6491766605 =
                      ((x10.array.Array)(source.
                                           terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06491566606 =
                      j66618;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16491666607 =
                      k66610;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6491866608 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6556866592 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6491766605).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6556966593 =
                      t6556866592.contains$O((int)(i06491566606),
                                             (int)(i16491666607));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6557066594 =
                      !(t6556966593);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6557066594) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06491566606),
                                                                                                                                                           (int)(i16491666607));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6557966595 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6491766605).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6492366596 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6491766605).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06492066597 =
                      i06491566606;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16492166598 =
                      i16491666607;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6492466599 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557166491 =
                      this6492366596.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6492266492 =
                      ((i06492066597) - (((int)(t6557166491))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557266493 =
                      offset6492266492;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557366494 =
                      this6492366596.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557466495 =
                      ((t6557266493) * (((int)(t6557366494))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557566496 =
                      ((t6557466495) + (((int)(i16492166598))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557666497 =
                      this6492366596.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557766498 =
                      ((t6557566496) - (((int)(t6557666497))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6492266492 = t6557766498;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6557866499 =
                      offset6492266492;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6492466599 = t6557866499;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6558066600 =
                      ret6492466599;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6558166601 =
                      ((x10.lang.Complex[])t6557966595.value)[t6558066600];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6491866608 = t6558166601;
                    
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex O_jk66609 =
                      ret6491866608;
                    
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6558266603 =
                      p;
                    
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40799max4080166604 =
                      ((t6558266603) - (((int)(j66618))));
                    
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4079966589 =
                      0;
                    
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6558466590 =
                          i4079966589;
                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6564066591 =
                          ((t6558466590) <= (((int)(i40799max4080166604))));
                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6564066591)) {
                            
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                        }
                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66586 =
                          i4079966589;
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40783min4078466584 =
                          (-(l66586));
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40783max4078566585 =
                          l66586;
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4078366581 =
                          i40783min4078466584;
                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                                true;
                                                                                                                                ) {
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6558866582 =
                              i4078366581;
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6563966583 =
                              ((t6558866582) <= (((int)(i40783max4078566585))));
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6563966583)) {
                                
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                            }
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int m66578 =
                              i4078366581;
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a6492666556 =
                              ((k66610) + (((int)(m66578))));
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t6559166557 =
                              ((a6492666556) < (((int)(0))));
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t6559266558 =
                               0;
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t6559166557) {
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t6559266558 = (-(a6492666556));
                            } else {
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t6559266558 = a6492666556;
                            }
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t6559366559 =
                              t6559266558;
                            
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6559466560 =
                              ((j66618) + (((int)(l66586))));
                            
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6563866561 =
                              ((t6559366559) <= (((int)(t6559466560))));
                            
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6563866561) {
                                
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6492966562 =
                                  ((x10.array.Array)(transform.
                                                       terms));
                                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06492766563 =
                                  ((j66618) + (((int)(l66586))));
                                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16492866564 =
                                  ((k66610) + (((int)(m66578))));
                                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6493066565 =
                                   null;
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6559566527 =
                                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6492966562).
                                                        region));
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6559666528 =
                                  t6559566527.contains$O((int)(i06492766563),
                                                         (int)(i16492866564));
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6559766529 =
                                  !(t6559666528);
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6559766529) {
                                    
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06492766563),
                                                                                                                                                                       (int)(i16492866564));
                                }
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6560666530 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6492966562).
                                                                   raw));
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6493566531 =
                                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6492966562).
                                                            layout));
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06493266532 =
                                  i06492766563;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16493366533 =
                                  i16492866564;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6493666534 =
                                   0;
                                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6559866500 =
                                  this6493566531.
                                    min0;
                                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6493466501 =
                                  ((i06493266532) - (((int)(t6559866500))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6559966502 =
                                  offset6493466501;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6560066503 =
                                  this6493566531.
                                    delta1;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6560166504 =
                                  ((t6559966502) * (((int)(t6560066503))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6560266505 =
                                  ((t6560166504) + (((int)(i16493366533))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6560366506 =
                                  this6493566531.
                                    min1;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6560466507 =
                                  ((t6560266505) - (((int)(t6560366506))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6493466501 = t6560466507;
                                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6560566508 =
                                  offset6493466501;
                                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6493666534 = t6560566508;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6560766535 =
                                  ret6493666534;
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6560866536 =
                                  ((x10.lang.Complex[])t6560666530.value)[t6560766535];
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6493066565 = t6560866536;
                                
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex B_lmjk66566 =
                                  ret6493066565;
                                
//#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6495266567 =
                                  ((x10.array.Array)(this.
                                                       terms));
                                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06494966568 =
                                  l66586;
                                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16495066569 =
                                  m66578;
                                
//#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6494066570 =
                                  ((x10.array.Array)(this.
                                                       terms));
                                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06493866571 =
                                  l66586;
                                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16493966572 =
                                  m66578;
                                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6494166573 =
                                   null;
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6560966537 =
                                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6494066570).
                                                        region));
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6561066538 =
                                  t6560966537.contains$O((int)(i06493866571),
                                                         (int)(i16493966572));
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6561166539 =
                                  !(t6561066538);
                                
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6561166539) {
                                    
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06493866571),
                                                                                                                                                                       (int)(i16493966572));
                                }
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6562066540 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6494066570).
                                                                   raw));
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6494666541 =
                                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6494066570).
                                                            layout));
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06494366542 =
                                  i06493866571;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16494466543 =
                                  i16493966572;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6494766544 =
                                   0;
                                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561266509 =
                                  this6494666541.
                                    min0;
                                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6494566510 =
                                  ((i06494366542) - (((int)(t6561266509))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561366511 =
                                  offset6494566510;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561466512 =
                                  this6494666541.
                                    delta1;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561566513 =
                                  ((t6561366511) * (((int)(t6561466512))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561666514 =
                                  ((t6561566513) + (((int)(i16494466543))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561766515 =
                                  this6494666541.
                                    min1;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561866516 =
                                  ((t6561666514) - (((int)(t6561766515))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6494566510 = t6561866516;
                                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6561966517 =
                                  offset6494566510;
                                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6494766544 = t6561966517;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6562166545 =
                                  ret6494766544;
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6562266546 =
                                  ((x10.lang.Complex[])t6562066540.value)[t6562166545];
                                
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6494166573 = t6562266546;
                                
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6562366574 =
                                  ret6494166573;
                                
//#line 166 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6562466575 =
                                  B_lmjk66566.$times(((x10.lang.Complex)(O_jk66609)));
                                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6495166576 =
                                  ((x10.lang.Complex)(t6562366574.$plus(((x10.lang.Complex)(t6562466575)))));
                                
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6495366577 =
                                   null;
                                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6562566547 =
                                  ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6495266567).
                                                        region));
                                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6562666548 =
                                  t6562566547.contains$O((int)(i06494966568),
                                                         (int)(i16495066569));
                                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6562766549 =
                                  !(t6562666548);
                                
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6562766549) {
                                    
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06494966568),
                                                                                                                                                                       (int)(i16495066569));
                                }
                                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6563666550 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6495266567).
                                                                   raw));
                                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6495866551 =
                                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6495266567).
                                                            layout));
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06495566552 =
                                  i06494966568;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16495666553 =
                                  i16495066569;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6495966554 =
                                   0;
                                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6562866518 =
                                  this6495866551.
                                    min0;
                                
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6495766519 =
                                  ((i06495566552) - (((int)(t6562866518))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6562966520 =
                                  offset6495766519;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6563066521 =
                                  this6495866551.
                                    delta1;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6563166522 =
                                  ((t6562966520) * (((int)(t6563066521))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6563266523 =
                                  ((t6563166522) + (((int)(i16495666553))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6563366524 =
                                  this6495866551.
                                    min1;
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6563466525 =
                                  ((t6563266523) - (((int)(t6563366524))));
                                
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6495766519 = t6563466525;
                                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6563566526 =
                                  offset6495766519;
                                
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6495966554 = t6563566526;
                                
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6563766555 =
                                  ret6495966554;
                                
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6563666550.value)[t6563766555] = v6495166576;
                                
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6495366577 = ((x10.lang.Complex)(v6495166576));
                            }
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6558966579 =
                              i4078366581;
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6559066580 =
                              ((t6558966579) + (((int)(1))));
                            
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4078366581 = t6559066580;
                        }
                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6558566587 =
                          i4079966589;
                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6558666588 =
                          ((t6558566587) + (((int)(1))));
                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4079966589 = t6558666588;
                    }
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6556666611 =
                      i4081566613;
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6556766612 =
                      ((t6556666611) + (((int)(1))));
                    
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4081566613 = t6556766612;
                }
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6556266619 =
                  i4083166621;
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6556366620 =
                  ((t6556266619) + (((int)(1))));
                
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4083166621 = t6556366620;
            }
        }
        
        
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public void
                                                                                                             transformAndAddToLocal_1_$_x10$lang$Complex_$_3_$_x10$array$Array$_x10$lang$Complex_$_$_5_$_x10$array$Array$_x10$array$Array$_x10$lang$Double_$_$_$(
                                                                                                             final au.edu.anu.mm.MultipoleExpansion scratch,
                                                                                                             final x10.array.Array temp,
                                                                                                             final x10x.vector.Vector3d v,
                                                                                                             final x10.array.Array complexK,
                                                                                                             final au.edu.anu.mm.MultipoleExpansion source,
                                                                                                             final x10.array.Array wigner){
            
//#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65654 =
              ((double)(int)(((int)(1))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65643 =
              v.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65644 =
              v.
                i;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65647 =
              ((t65643) * (((double)(t65644))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65645 =
              v.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65646 =
              v.
                j;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65648 =
              ((t65645) * (((double)(t65646))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65651 =
              ((t65647) + (((double)(t65648))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65649 =
              v.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65650 =
              v.
                k;
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65652 =
              ((t65649) * (((double)(t65650))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65653 =
              ((t65651) + (((double)(t65652))));
            
//#line 94 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t65655 =
              java.lang.Math.sqrt(((double)(t65653)));
            
//#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double inv_b =
              ((t65654) / (((double)(t65655))));
            
//#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> t65656 =
              ((x10.array.Array)(source.
                                   terms));
            
//#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> t65657 =
              ((x10.array.Array)(scratch.
                                   terms));
            
//#line 187 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
x10.array.Array.<x10.lang.Complex>copy_0_$_x10$array$Array_T_$_1_$_x10$array$Array_T_$(x10.lang.Complex.$RTT, ((x10.array.Array)(t65656)),
                                                                                                                                                                                                      ((x10.array.Array)(t65657)));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret64962 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret64963: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t65658 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65659 =
              ((x10.array.Array)(((x10.array.Array[])t65658.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64962 = ((x10.array.Array)(t65659));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret64963;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65662 =
              ((x10.array.Array)(ret64962));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret64970 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret64971: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t65660 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)wigner).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65661 =
              ((x10.array.Array)(((x10.array.Array[])t65660.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64970 = ((x10.array.Array)(t65661));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret64971;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65663 =
              ((x10.array.Array)(ret64970));
            
//#line 188 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
scratch.rotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                     ((x10.array.Array)(t65662)),
                                                                                                                                                                                                                     ((x10.array.Array)(t65663)));
            
//#line 190 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> targetTerms =
              ((x10.array.Array)(scratch.
                                   terms));
            
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int m_sign =
              1;
            
//#line 193 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double b_m_pow =
              1.0;
            
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40895max4089766810 =
              p;
            
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4089566806 =
              0;
            
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6566566807 =
                  i4089566806;
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6578266808 =
                  ((t6566566807) <= (((int)(i40895max4089766810))));
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6578266808)) {
                    
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                }
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int m66803 =
                  i4089566806;
                
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40847min4084866791 =
                  m66803;
                
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40847max4084966792 =
                  p;
                
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4084766663 =
                  i40847min4084866791;
                
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                    
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6566966664 =
                      i4084766663;
                    
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6569366665 =
                      ((t6566966664) <= (((int)(i40847max4084966792))));
                    
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6569366665)) {
                        
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66660 =
                      i4084766663;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06498766654 =
                      l66660;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06497766655 =
                      l66660;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16497866656 =
                      (-(m66803));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6497966657 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6567266644 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6567366645 =
                      t6567266644.contains$O((int)(i06497766655),
                                             (int)(i16497866656));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6567466646 =
                      !(t6567366645);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6567466646) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06497766655),
                                                                                                                                                           (int)(i16497866656));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6568366647 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6498466648 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06498166649 =
                      i06497766655;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16498266650 =
                      i16497866656;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6498566651 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6567566624 =
                      this6498466648.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6498366625 =
                      ((i06498166649) - (((int)(t6567566624))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6567666626 =
                      offset6498366625;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6567766627 =
                      this6498466648.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6567866628 =
                      ((t6567666626) * (((int)(t6567766627))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6567966629 =
                      ((t6567866628) + (((int)(i16498266650))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6568066630 =
                      this6498466648.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6568166631 =
                      ((t6567966629) - (((int)(t6568066630))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6498366625 = t6568166631;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6568266632 =
                      offset6498366625;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6498566651 = t6568266632;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6568466652 =
                      ret6498566651;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6568566653 =
                      ((x10.lang.Complex[])t6568366647.value)[t6568466652];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6497966657 = t6568566653;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6498866658 =
                      ((x10.lang.Complex)(ret6497966657));
                    
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6498966659 =
                       null;
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6568666636 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                            region));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6568766637 =
                      t6568666636.contains$O((int)(i06498766654));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6568866638 =
                      !(t6568766637);
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6568866638) {
                        
//#line 515 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06498766654));
                    }
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6569166639 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                       raw));
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6499366640 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                layout));
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06499166641 =
                      i06498766654;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6499466642 =
                       0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6568966633 =
                      this6499366640.
                        min0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6499266634 =
                      ((i06499166641) - (((int)(t6568966633))));
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6569066635 =
                      offset6499266634;
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6499466642 = t6569066635;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6569266643 =
                      ret6499466642;
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6569166639.value)[t6569266643] = v6498866658;
                    
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6498966659 = ((x10.lang.Complex)(v6498866658));
                    
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6567066661 =
                      i4084766663;
                    
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6567166662 =
                      ((t6567066661) + (((int)(1))));
                    
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4084766663 = t6567166662;
                }
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6569466795 =
                  b_m_pow;
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6569566796 =
                  ((inv_b) * (((double)(t6569466795))));
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6569666797 =
                  b_m_pow;
                
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double b_lm1_pow66798 =
                  ((t6569566796) * (((double)(t6569666797))));
                
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40879min4088066793 =
                  m66803;
                
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40879max4088166794 =
                  p;
                
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4087966788 =
                  i40879min4088066793;
                
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                    
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6569866789 =
                      i4087966788;
                    
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6577766790 =
                      ((t6569866789) <= (((int)(i40879max4088166794))));
                    
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6577766790)) {
                        
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66785 =
                      i4087966788;
                    
//#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
x10.lang.Complex M_lm66756 =
                      x10.lang.Complex.getInitialized$ZERO();
                    
//#line 30 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int i6499666757 =
                      ((l66785) + (((int)(m66803))));
                    
//#line 30 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final x10.array.Array<x10.core.Double> this6499866758 =
                      ((x10.array.Array)(au.edu.anu.mm.Factorial.getInitialized$factorial()));
                    
//#line 410 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06499766759 =
                      i6499666757;
                    
//#line 409 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6499966760 =
                       0;
                    
//#line 409 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6500066761: {
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6570166762 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)this6499866758).
                                                       raw));
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6570266763 =
                      ((double[])t6570166762.value)[i06499766759];
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6499966760 = t6570266763;
//#line 413 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6500066761;}
                    
//#line 409 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6570366764 =
                      ret6499966760;
                    
//#line 200 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6570466765 =
                      b_lm1_pow66798;
                    
//#line 200 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double F_lm66766 =
                      ((t6570366764) * (((double)(t6570466765))));
                    
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40863min4086466744 =
                      m66803;
                    
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6570566745 =
                      p;
                    
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40863max4086566746 =
                      ((t6570566745) - (((int)(l66785))));
                    
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4086366695 =
                      i40863min4086466744;
                    
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                            true;
                                                                                                                            ) {
                        
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6570766696 =
                          i4086366695;
                        
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6572966697 =
                          ((t6570766696) <= (((int)(i40863max4086566746))));
                        
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6572966697)) {
                            
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                        }
                        
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int j66692 =
                          i4086366695;
                        
//#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6572066669 =
                          M_lm66756;
                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06500666670 =
                          j66692;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6500766671 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6500866672: {
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6571066673 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                                region));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6571166674 =
                          t6571066673.contains$O((int)(i06500666670));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6571266675 =
                          !(t6571166674);
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6571266675) {
                            
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06500666670));
                        }
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6571566676 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                           raw));
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6501166677 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                    layout));
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06500966678 =
                          i06500666670;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6501266679 =
                           0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6571366666 =
                          this6501166677.
                            min0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6501066667 =
                          ((i06500966678) - (((int)(t6571366666))));
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6571466668 =
                          offset6501066667;
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6501266679 = t6571466668;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6571666680 =
                          ret6501266679;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6571766681 =
                          ((x10.lang.Complex[])t6571566676.value)[t6571666680];
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6500766671 = t6571766681;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6500866672;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6571866682 =
                          ret6500766671;
                        
//#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6571966683 =
                          F_lm66766;
                        
//#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6572166684 =
                          t6571866682.$times((double)(t6571966683));
                        
//#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6572266685 =
                          t6572066669.$plus(((x10.lang.Complex)(t6572166684)));
                        
//#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
M_lm66756 = t6572266685;
                        
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6572566686 =
                          F_lm66766;
                        
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6572366687 =
                          ((j66692) + (((int)(l66785))));
                        
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6572466688 =
                          ((t6572366687) + (((int)(1))));
                        
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6572666689 =
                          ((double)(int)(((int)(t6572466688))));
                        
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6572766690 =
                          ((t6572566686) * (((double)(t6572666689))));
                        
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6572866691 =
                          ((t6572766690) * (((double)(inv_b))));
                        
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
F_lm66766 = t6572866691;
                        
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6570866693 =
                          i4086366695;
                        
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6570966694 =
                          ((t6570866693) + (((int)(1))));
                        
//#line 201 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4086366695 = t6570966694;
                    }
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06501466767 =
                      l66785;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16501566768 =
                      m66803;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6501666769 =
                      ((x10.lang.Complex)(M_lm66756));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6501766770 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6573066747 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6573166748 =
                      t6573066747.contains$O((int)(i06501466767),
                                             (int)(i16501566768));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6573266749 =
                      !(t6573166748);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6573266749) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06501466767),
                                                                                                                                                           (int)(i16501566768));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6574166750 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6502266751 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06501966752 =
                      i06501466767;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16502066753 =
                      i16501566768;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6502366754 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6573366698 =
                      this6502266751.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6502166699 =
                      ((i06501966752) - (((int)(t6573366698))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6573466700 =
                      offset6502166699;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6573566701 =
                      this6502266751.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6573666702 =
                      ((t6573466700) * (((int)(t6573566701))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6573766703 =
                      ((t6573666702) + (((int)(i16502066753))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6573866704 =
                      this6502266751.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6573966705 =
                      ((t6573766703) - (((int)(t6573866704))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6502166699 = t6573966705;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6574066706 =
                      offset6502166699;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6502366754 = t6574066706;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6574266755 =
                      ret6502366754;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6574166750.value)[t6574266755] = v6501666769;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6501766770 = ((x10.lang.Complex)(v6501666769));
                    
//#line 206 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6577466771 =
                      ((int) m66803) !=
                    ((int) 0);
                    
//#line 206 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6577466771) {
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06503566772 =
                          l66785;
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16503666773 =
                          (-(m66803));
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06502566774 =
                          l66785;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16502666775 =
                          m66803;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6502766776 =
                           null;
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6574366725 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                region));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6574466726 =
                          t6574366725.contains$O((int)(i06502566774),
                                                 (int)(i16502666775));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6574566727 =
                          !(t6574466726);
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6574566727) {
                            
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06502566774),
                                                                                                                                                               (int)(i16502666775));
                        }
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6575466728 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                           raw));
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6503266729 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06502966730 =
                          i06502566774;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16503066731 =
                          i16502666775;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6503366732 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6574666707 =
                          this6503266729.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6503166708 =
                          ((i06502966730) - (((int)(t6574666707))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6574766709 =
                          offset6503166708;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6574866710 =
                          this6503266729.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6574966711 =
                          ((t6574766709) * (((int)(t6574866710))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6575066712 =
                          ((t6574966711) + (((int)(i16503066731))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6575166713 =
                          this6503266729.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6575266714 =
                          ((t6575066712) - (((int)(t6575166713))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6503166708 = t6575266714;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6575366715 =
                          offset6503166708;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6503366732 = t6575366715;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6575566733 =
                          ret6503366732;
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6575666734 =
                          ((x10.lang.Complex[])t6575466728.value)[t6575566733];
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6502766776 = t6575666734;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6575766777 =
                          ret6502766776;
                        
//#line 206 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6575966778 =
                          t6575766777.conjugate();
                        
//#line 206 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6575866779 =
                          m_sign;
                        
//#line 206 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6576066780 =
                          ((double)(int)(((int)(t6575866779))));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6503766781 =
                          ((x10.lang.Complex)(t6575966778.$times((double)(t6576066780))));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6503866782 =
                           null;
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6576166735 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                region));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6576266736 =
                          t6576166735.contains$O((int)(i06503566772),
                                                 (int)(i16503666773));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6576366737 =
                          !(t6576266736);
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6576366737) {
                            
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06503566772),
                                                                                                                                                               (int)(i16503666773));
                        }
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6577266738 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                           raw));
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6504366739 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)targetTerms).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06504066740 =
                          i06503566772;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16504166741 =
                          i16503666773;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6504466742 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6576466716 =
                          this6504366739.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6504266717 =
                          ((i06504066740) - (((int)(t6576466716))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6576566718 =
                          offset6504266717;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6576666719 =
                          this6504366739.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6576766720 =
                          ((t6576566718) * (((int)(t6576666719))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6576866721 =
                          ((t6576766720) + (((int)(i16504166741))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6576966722 =
                          this6504366739.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6577066723 =
                          ((t6576866721) - (((int)(t6576966722))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6504266717 = t6577066723;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6577166724 =
                          offset6504266717;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6504466742 = t6577166724;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6577366743 =
                          ret6504466742;
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6577266738.value)[t6577366743] = v6503766781;
                        
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6503866782 = ((x10.lang.Complex)(v6503766781));
                    }
                    
//#line 207 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6577566783 =
                      b_lm1_pow66798;
                    
//#line 207 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6577666784 =
                      ((t6577566783) * (((double)(inv_b))));
                    
//#line 207 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
b_lm1_pow66798 = t6577666784;
                    
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6569966786 =
                      i4087966788;
                    
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6570066787 =
                      ((t6569966786) + (((int)(1))));
                    
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4087966788 = t6570066787;
                }
                
//#line 210 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6577866799 =
                  m_sign;
                
//#line 210 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6577966800 =
                  (-(t6577866799));
                
//#line 210 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
m_sign = t6577966800;
                
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6578066801 =
                  b_m_pow;
                
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6578166802 =
                  ((t6578066801) * (((double)(inv_b))));
                
//#line 211 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
b_m_pow = t6578166802;
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6566666804 =
                  i4089566806;
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6566766805 =
                  ((t6566666804) + (((int)(1))));
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4089566806 = t6566766805;
            }
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret65047 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret65048: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t65783 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65784 =
              ((x10.array.Array)(((x10.array.Array[])t65783.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret65047 = ((x10.array.Array)(t65784));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret65048;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65787 =
              ((x10.array.Array)(ret65047));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret65055 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret65056: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t65785 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)wigner).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65786 =
              ((x10.array.Array)(((x10.array.Array[])t65785.value)[1]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret65055 = ((x10.array.Array)(t65786));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret65056;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65788 =
              ((x10.array.Array)(ret65055));
            
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
scratch.backRotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                         ((x10.array.Array)(t65787)),
                                                                                                                                                                                                                         ((x10.array.Array)(t65788)));
            
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this.unsafeAdd(((au.edu.anu.mm.Expansion)(scratch)));
        }
        
        
//#line 223 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public void
                                                                                                             transformAndAddToLocal(
                                                                                                             final x10x.vector.Vector3d v,
                                                                                                             final au.edu.anu.mm.MultipoleExpansion source){
            
//#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10x.polar.Polar3d polar =
              x10x.polar.Polar3d.getPolar3d(((x10x.vector.Tuple3d)(v)));
            
//#line 225 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion scratch =
              ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
            
//#line 225 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6578966823 =
              p;
            
//#line 225 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.util.concurrent.OrderedLock t6579066824 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 225 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
scratch.$init(((int)(t6578966823)),
                                                                                                                             t6579066824);
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> alloc40604 =
              ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65791 =
              p;
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65792 =
              (-(t65791));
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65793 =
              p;
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.IntRange t65794 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t65792)), ((int)(t65793)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg65062 =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t65794)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.x10$lang$Object$$init$S();
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__650636506766815 =
              ((x10.array.Region)(((x10.array.Region)
                                    reg65062)));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6506866816 =
               null;
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6579566811 =
              ((__desugarer__var__0__650636506766815) != (null));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6579866812 =
              !(t6579566811);
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6579866812) {
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6579766813 =
                  true;
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6579766813) {
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6579666814 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6579666814;
                }
            }
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6506866816 = ((x10.array.Region)(__desugarer__var__0__650636506766815));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6579966817 =
              ((x10.array.Region)(ret6506866816));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.region = ((x10.array.Region)(t6579966817));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6580066818 =
              reg65062.
                rank;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.rank = t6580066818;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6580166819 =
              reg65062.
                rect;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.rect = t6580166819;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6580266820 =
              reg65062.
                zeroBased;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.zeroBased = t6580266820;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6580366821 =
              reg65062.
                rail;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.rail = t6580366821;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6580466822 =
              reg65062.size$O();
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.size = t6580466822;
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646506466825 =
              new x10.array.RectLayout((java.lang.System[]) null);
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646506466825.$init(((x10.array.Region)(reg65062)));
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.layout = ((x10.array.RectLayout)(alloc199646506466825));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6507066826 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc40604).
                                        layout));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6506566827 =
              this6507066826.
                size;
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6580566828 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6506566827)), true)));
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40604.raw = ((x10.core.IndexedMemoryChunk)(t6580566828));
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> __desugarer__var__31__65071 =
              ((x10.array.Array)(((x10.array.Array<x10.lang.Complex>)
                                   alloc40604)));
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
x10.array.Array<x10.lang.Complex> ret65072 =
               null;
            
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6580666829 =
              ((x10.array.Array<x10.lang.Complex>)__desugarer__var__31__65071).
                rect;
            
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
boolean t6580866830 =
              ((boolean) t6580666829) ==
            ((boolean) true);
            
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6580866830) {
                
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6580766831 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__31__65071).
                    rail;
                
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
t6580866830 = ((boolean) t6580766831) ==
                ((boolean) false);
            }
            
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
boolean t6581066832 =
              t6580866830;
            
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6581066832) {
                
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6580966833 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__31__65071).
                    rank;
                
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
t6581066832 = ((int) t6580966833) ==
                ((int) 1);
            }
            
//#line 226 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6581166834 =
              t6581066832;
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6581466835 =
              !(t6581166834);
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6581466835) {
                
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6581366836 =
                  true;
                
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6581366836) {
                    
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.FailedDynamicCheckException t6581266837 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Complex]{self.rect==true, self.rail==false, self.rank==1}");
                    
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
throw t6581266837;
                }
            }
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
ret65072 = ((x10.array.Array)(__desugarer__var__31__65071));
            
//#line 226 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> temp =
              ((x10.array.Array)(ret65072));
            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65815 =
              polar.
                phi;
            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65816 =
              p;
            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.array.Array<x10.lang.Complex>> t65817 =
              ((x10.array.Array)(au.edu.anu.mm.Expansion.genComplexK((double)(t65815),
                                                                     (int)(t65816))));
            
//#line 186 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final double theta65074 =
              polar.
                theta;
            
//#line 186 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final int numTerms65075 =
              p;
            
//#line 186 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>> t65818 =
              ((x10.array.Array)(au.edu.anu.mm.WignerRotationMatrix.getExpandedCollection((double)(theta65074),
                                                                                          (int)(numTerms65075),
                                                                                          (int)(-1))));
            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this.transformAndAddToLocal_1_$_x10$lang$Complex_$_3_$_x10$array$Array$_x10$lang$Complex_$_$_5_$_x10$array$Array$_x10$array$Array$_x10$lang$Double_$_$_$(((au.edu.anu.mm.MultipoleExpansion)(scratch)),
                                                                                                                                                                                                                                                                        ((x10.array.Array)(temp)),
                                                                                                                                                                                                                                                                        ((x10x.vector.Vector3d)(v)),
                                                                                                                                                                                                                                                                        ((x10.array.Array)(t65817)),
                                                                                                                                                                                                                                                                        ((au.edu.anu.mm.MultipoleExpansion)(source)),
                                                                                                                                                                                                                                                                        ((x10.array.Array)(t65818)));
        }
        
        
//#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public au.edu.anu.mm.LocalExpansion
                                                                                                             rotate(
                                                                                                             final double theta,
                                                                                                             final double phi){
            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion target =
              ((au.edu.anu.mm.LocalExpansion)(new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null)));
            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.util.concurrent.OrderedLock t6581966850 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
target.$init(((au.edu.anu.mm.LocalExpansion)(this)),
                                                                                                                            t6581966850);
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> alloc40605 =
              ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65820 =
              p;
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65821 =
              (-(t65820));
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65822 =
              p;
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.IntRange t65823 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t65821)), ((int)(t65822)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg65076 =
              ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t65823)))));
            
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.x10$lang$Object$$init$S();
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__650776508166842 =
              ((x10.array.Region)(((x10.array.Region)
                                    reg65076)));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6508266843 =
               null;
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6582466838 =
              ((__desugarer__var__0__650776508166842) != (null));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6582766839 =
              !(t6582466838);
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6582766839) {
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6582666840 =
                  true;
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6582666840) {
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6582566841 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6582566841;
                }
            }
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6508266843 = ((x10.array.Region)(__desugarer__var__0__650776508166842));
            
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6582866844 =
              ((x10.array.Region)(ret6508266843));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.region = ((x10.array.Region)(t6582866844));
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6582966845 =
              reg65076.
                rank;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.rank = t6582966845;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6583066846 =
              reg65076.
                rect;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.rect = t6583066846;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6583166847 =
              reg65076.
                zeroBased;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.zeroBased = t6583166847;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6583266848 =
              reg65076.
                rail;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.rail = t6583266848;
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6583366849 =
              reg65076.size$O();
            
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.size = t6583366849;
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646507866851 =
              new x10.array.RectLayout((java.lang.System[]) null);
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646507866851.$init(((x10.array.Region)(reg65076)));
            
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.layout = ((x10.array.RectLayout)(alloc199646507866851));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6508466852 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc40605).
                                        layout));
            
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6507966853 =
              this6508466852.
                size;
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6583466854 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6507966853)), true)));
            
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc40605.raw = ((x10.core.IndexedMemoryChunk)(t6583466854));
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> __desugarer__var__32__65085 =
              ((x10.array.Array)(((x10.array.Array<x10.lang.Complex>)
                                   alloc40605)));
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
x10.array.Array<x10.lang.Complex> ret65086 =
               null;
            
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6583566855 =
              ((x10.array.Array<x10.lang.Complex>)__desugarer__var__32__65085).
                rect;
            
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
boolean t6583766856 =
              ((boolean) t6583566855) ==
            ((boolean) true);
            
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6583766856) {
                
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6583666857 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__32__65085).
                    rail;
                
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
t6583766856 = ((boolean) t6583666857) ==
                ((boolean) false);
            }
            
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
boolean t6583966858 =
              t6583766856;
            
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6583966858) {
                
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6583866859 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__32__65085).
                    rank;
                
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
t6583966858 = ((int) t6583866859) ==
                ((int) 1);
            }
            
//#line 237 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6584066860 =
              t6583966858;
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6584366861 =
              !(t6584066860);
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6584366861) {
                
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6584266862 =
                  true;
                
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (t6584266862) {
                    
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.FailedDynamicCheckException t6584166863 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Complex]{self.rect==true, self.rail==false, self.rank==1}");
                    
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
throw t6584166863;
                }
            }
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
ret65086 = ((x10.array.Array)(__desugarer__var__32__65085));
            
//#line 237 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> temp =
              ((x10.array.Array)(ret65086));
            
//#line 238 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65844 =
              p;
            
//#line 238 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.array.Array<x10.lang.Complex>> this65089 =
              ((x10.array.Array)(au.edu.anu.mm.Expansion.genComplexK((double)(phi),
                                                                     (int)(t65844))));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret65090 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret65091: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t65845 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)this65089).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65846 =
              ((x10.array.Array)(((x10.array.Array[])t65845.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret65090 = ((x10.array.Array)(t65846));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret65091;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t65849 =
              ((x10.array.Array)(ret65090));
            
//#line 187 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final double theta65097 =
              theta;
            
//#line 187 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/WignerRotationMatrix.x10"
final int numTerms65098 =
              p;
            
//#line 238 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>> this65100 =
              ((x10.array.Array)(au.edu.anu.mm.WignerRotationMatrix.getExpandedCollection((double)(theta65097),
                                                                                          (int)(numTerms65098),
                                                                                          (int)(2))));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Array<x10.core.Double>> ret65101 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret65102: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Array<x10.core.Double>>> t65847 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Array<x10.core.Double>>>)this65100).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65848 =
              ((x10.array.Array)(((x10.array.Array[])t65847.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret65101 = ((x10.array.Array)(t65848));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret65102;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Array<x10.core.Double>> t65850 =
              ((x10.array.Array)(ret65101));
            
//#line 238 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
target.rotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(((x10.array.Array)(temp)),
                                                                                                                                                                                                                    ((x10.array.Array)(t65849)),
                                                                                                                                                                                                                    ((x10.array.Array)(t65850)));
            
//#line 239 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
return target;
        }
        
        
//#line 248 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public double
                                                                                                             getPotential$O(
                                                                                                             final double q,
                                                                                                             final x10x.vector.Tuple3d v){
            
//#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t65851 =
              p;
            
//#line 250 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.MultipoleExpansion transform =
              au.edu.anu.mm.MultipoleExpansion.getOlm((double)(q),
                                                      ((x10x.vector.Tuple3d)(v)),
                                                      (int)(t65851));
            
//#line 251 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
double potential =
              0.0;
            
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40927max4092966931 =
              p;
            
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4092766927 =
              0;
            
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6585366928 =
                  i4092766927;
                
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6589566929 =
                  ((t6585366928) <= (((int)(i40927max4092966931))));
                
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6589566929)) {
                    
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                }
                
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int j66924 =
                  i4092766927;
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40911min4091266922 =
                  (-(j66924));
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40911max4091366923 =
                  j66924;
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4091166919 =
                  i40911min4091266922;
                
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                    
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6585766920 =
                      i4091166919;
                    
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6589466921 =
                      ((t6585766920) <= (((int)(i40911max4091366923))));
                    
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6589466921)) {
                        
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int k66916 =
                      i4091166919;
                    
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6589166902 =
                      potential;
                    
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6511066903 =
                      ((x10.array.Array)(terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06510866904 =
                      j66924;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16510966905 =
                      k66916;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6511166906 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6586066882 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6511066903).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6586166883 =
                      t6586066882.contains$O((int)(i06510866904),
                                             (int)(i16510966905));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6586266884 =
                      !(t6586166883);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6586266884) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06510866904),
                                                                                                                                                           (int)(i16510966905));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6587166885 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6511066903).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6511666886 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6511066903).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06511366887 =
                      i06510866904;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16511466888 =
                      i16510966905;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6511766889 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6586366864 =
                      this6511666886.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6511566865 =
                      ((i06511366887) - (((int)(t6586366864))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6586466866 =
                      offset6511566865;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6586566867 =
                      this6511666886.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6586666868 =
                      ((t6586466866) * (((int)(t6586566867))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6586766869 =
                      ((t6586666868) + (((int)(i16511466888))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6586866870 =
                      this6511666886.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6586966871 =
                      ((t6586766869) - (((int)(t6586866870))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6511566865 = t6586966871;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6587066872 =
                      offset6511566865;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6511766889 = t6587066872;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6587266890 =
                      ret6511766889;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6587366891 =
                      ((x10.lang.Complex[])t6587166885.value)[t6587266890];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6511166906 = t6587366891;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6588866907 =
                      ret6511166906;
                    
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6512166908 =
                      ((x10.array.Array)(transform.
                                           terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06511966909 =
                      j66924;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16512066910 =
                      k66916;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6512266911 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6587466892 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6512166908).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6587566893 =
                      t6587466892.contains$O((int)(i06511966909),
                                             (int)(i16512066910));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6587666894 =
                      !(t6587566893);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6587666894) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06511966909),
                                                                                                                                                           (int)(i16512066910));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6588566895 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6512166908).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6512766896 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6512166908).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06512466897 =
                      i06511966909;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16512566898 =
                      i16512066910;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6512866899 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6587766873 =
                      this6512766896.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6512666874 =
                      ((i06512466897) - (((int)(t6587766873))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6587866875 =
                      offset6512666874;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6587966876 =
                      this6512766896.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6588066877 =
                      ((t6587866875) * (((int)(t6587966876))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6588166878 =
                      ((t6588066877) + (((int)(i16512566898))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6588266879 =
                      this6512766896.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6588366880 =
                      ((t6588166878) - (((int)(t6588266879))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6512666874 = t6588366880;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6588466881 =
                      offset6512666874;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6512866899 = t6588466881;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6588666900 =
                      ret6512866899;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6588766901 =
                      ((x10.lang.Complex[])t6588566895.value)[t6588666900];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6512266911 = t6588766901;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6588966912 =
                      ret6512266911;
                    
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.lang.Complex t6589066913 =
                      t6588866907.$times(((x10.lang.Complex)(t6588966912)));
                    
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6589266914 =
                      t6589066913.
                        re;
                    
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6589366915 =
                      ((t6589166902) + (((double)(t6589266914))));
                    
//#line 256 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
potential = t6589366915;
                    
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6585866917 =
                      i4091166919;
                    
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6585966918 =
                      ((t6585866917) + (((int)(1))));
                    
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4091166919 = t6585966918;
                }
                
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6585466925 =
                  i4092766927;
                
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6585566926 =
                  ((t6585466925) + (((int)(1))));
                
//#line 254 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4092766927 = t6585566926;
            }
            
//#line 259 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t65896 =
              potential;
            
//#line 259 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
return t65896;
        }
        
        
//#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
public au.edu.anu.mm.LocalExpansion
                                                                                                             getMacroscopicParent(
                                                                                                             ){
            
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final au.edu.anu.mm.LocalExpansion parentExpansion =
              ((au.edu.anu.mm.LocalExpansion)(new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null)));
            
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6589766996 =
              p;
            
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.util.concurrent.OrderedLock t6589866997 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
parentExpansion.$init(((int)(t6589766996)),
                                                                                                                                     t6589866997);
            
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40959max4096166999 =
              p;
            
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4095966993 =
              0;
            
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6590066994 =
                  i4095966993;
                
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6593966995 =
                  ((t6590066994) <= (((int)(i40959max4096166999))));
                
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6593966995)) {
                    
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                }
                
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int l66990 =
                  i4095966993;
                
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40943min4094466988 =
                  (-(l66990));
                
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int i40943max4094566989 =
                  l66990;
                
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
int i4094366985 =
                  i40943min4094466988;
                
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
for (;
                                                                                                                        true;
                                                                                                                        ) {
                    
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6590466986 =
                      i4094366985;
                    
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final boolean t6593866987 =
                      ((t6590466986) <= (((int)(i40943max4094566989))));
                    
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
if (!(t6593866987)) {
                        
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
break;
                    }
                    
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int m66982 =
                      i4094366985;
                    
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6514466969 =
                      ((x10.array.Array)(parentExpansion.
                                           terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06514166970 =
                      l66990;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16514266971 =
                      m66982;
                    
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final x10.array.Array<x10.lang.Complex> this6513266972 =
                      ((x10.array.Array)(terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06513066973 =
                      l66990;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16513166974 =
                      m66982;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6513366975 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6590766950 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6513266972).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6590866951 =
                      t6590766950.contains$O((int)(i06513066973),
                                             (int)(i16513166974));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6590966952 =
                      !(t6590866951);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6590966952) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06513066973),
                                                                                                                                                           (int)(i16513166974));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6591866953 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6513266972).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6513866954 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6513266972).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06513566955 =
                      i06513066973;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16513666956 =
                      i16513166974;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6513966957 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591066932 =
                      this6513866954.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6513766933 =
                      ((i06513566955) - (((int)(t6591066932))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591166934 =
                      offset6513766933;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591266935 =
                      this6513866954.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591366936 =
                      ((t6591166934) * (((int)(t6591266935))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591466937 =
                      ((t6591366936) + (((int)(i16513666956))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591566938 =
                      this6513866954.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591666939 =
                      ((t6591466937) - (((int)(t6591566938))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6513766933 = t6591666939;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591766940 =
                      offset6513766933;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6513966957 = t6591766940;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6591966958 =
                      ret6513966957;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6592066959 =
                      ((x10.lang.Complex[])t6591866953.value)[t6591966958];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6513366975 = t6592066959;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6592366976 =
                      ret6513366975;
                    
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6592166977 =
                      ((l66990) + (((int)(1))));
                    
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6592266978 =
                      ((double)(int)(((int)(t6592166977))));
                    
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final double t6592466979 =
                      java.lang.Math.pow(((double)(3.0)), ((double)(t6592266978)));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6514366980 =
                      ((x10.lang.Complex)(t6592366976.$over((double)(t6592466979))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6514566981 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6592566960 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6514466969).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6592666961 =
                      t6592566960.contains$O((int)(i06514166970),
                                             (int)(i16514266971));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6592766962 =
                      !(t6592666961);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6592766962) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06514166970),
                                                                                                                                                           (int)(i16514266971));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6593666963 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6514466969).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6515066964 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6514466969).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06514766965 =
                      i06514166970;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16514866966 =
                      i16514266971;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6515166967 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6592866941 =
                      this6515066964.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6514966942 =
                      ((i06514766965) - (((int)(t6592866941))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6592966943 =
                      offset6514966942;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6593066944 =
                      this6515066964.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6593166945 =
                      ((t6592966943) * (((int)(t6593066944))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6593266946 =
                      ((t6593166945) + (((int)(i16514866966))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6593366947 =
                      this6515066964.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6593466948 =
                      ((t6593266946) - (((int)(t6593366947))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6514966942 = t6593466948;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6593566949 =
                      offset6514966942;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6515166967 = t6593566949;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6593766968 =
                      ret6515166967;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6593666963.value)[t6593766968] = v6514366980;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6514566981 = ((x10.lang.Complex)(v6514366980));
                    
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6590566983 =
                      i4094366985;
                    
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6590666984 =
                      ((t6590566983) + (((int)(1))));
                    
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4094366985 = t6590666984;
                }
                
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6590166991 =
                  i4095966993;
                
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final int t6590266992 =
                  ((t6590166991) + (((int)(1))));
                
//#line 272 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
i4095966993 = t6590266992;
            }
            
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
return parentExpansion;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final public au.edu.anu.mm.LocalExpansion
                                                                                                            au$edu$anu$mm$LocalExpansion$$au$edu$anu$mm$LocalExpansion$this(
                                                                                                            ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
return au.edu.anu.mm.LocalExpansion.this;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
final private void
                                                                                                            __fieldInitializers40131(
                                                                                                            ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocalExpansion.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers40131$P(
          final au.edu.anu.mm.LocalExpansion LocalExpansion){
            LocalExpansion.__fieldInitializers40131();
        }
    
}
