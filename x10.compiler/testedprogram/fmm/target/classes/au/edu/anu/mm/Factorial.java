package au.edu.anu.mm;


public class Factorial
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Factorial.class);
    
    public static final x10.rtt.RuntimeType<Factorial> $RTT = new x10.rtt.NamedType<Factorial>(
    "au.edu.anu.mm.Factorial", /* base class */Factorial.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Factorial $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Factorial $_obj = new Factorial((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        
    }
    
    // constructor just for allocation
    public Factorial(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
public int
          X10$object_lock_id0;
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
public x10.util.concurrent.OrderedLock
                                                                                                       getOrderedLock(
                                                                                                       ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int t64574 =
              this.
                X10$object_lock_id0;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final x10.util.concurrent.OrderedLock t64575 =
              x10.util.concurrent.OrderedLock.getLock((int)(t64574));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
return t64575;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                       getStaticOrderedLock(
                                                                                                       ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int t64576 =
              au.edu.anu.mm.Factorial.X10$class_lock_id1;
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final x10.util.concurrent.OrderedLock t64577 =
              x10.util.concurrent.OrderedLock.getLock((int)(t64576));
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
return t64577;
        }
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
public static x10.array.Array<x10.core.Double>
          factorial;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final private static x10.array.Array<x10.core.Double>
                                                                                                       calcFact(
                                                                                                       ){
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final x10.array.Array<x10.core.Double> fact =
              ((x10.array.Array)(new x10.array.Array<x10.core.Double>((java.lang.System[]) null, x10.rtt.Types.DOUBLE)));
            
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.x10$lang$Object$$init$S();
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199706452264650 =
              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6457864613 =
              ((100) - (((int)(1))));
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199706452264650.$init(((int)(0)),
                                                                                                                                       t6457864613);
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__645216452664651 =
              ((x10.array.Region)(((x10.array.Region)
                                    alloc199706452264650)));
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6452764652 =
               null;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6457964614 =
              __desugarer__var__5__645216452664651.
                rank;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6458164615 =
              ((int) t6457964614) ==
            ((int) 1);
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6458164615) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6458064616 =
                  __desugarer__var__5__645216452664651.
                    zeroBased;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6458164615 = ((boolean) t6458064616) ==
                ((boolean) true);
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6458364617 =
              t6458164615;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6458364617) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6458264618 =
                  __desugarer__var__5__645216452664651.
                    rect;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6458364617 = ((boolean) t6458264618) ==
                ((boolean) true);
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6458564619 =
              t6458364617;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6458564619) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6458464620 =
                  __desugarer__var__5__645216452664651.
                    rail;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6458564619 = ((boolean) t6458464620) ==
                ((boolean) true);
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6458664621 =
              t6458564619;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6458664621) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6458664621 = ((__desugarer__var__5__645216452664651) != (null));
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6458764622 =
              t6458664621;
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6459064623 =
              !(t6458764622);
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6459064623) {
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6458964624 =
                  true;
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6458964624) {
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6458864625 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6458864625;
                }
            }
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6452764652 = ((x10.array.Region)(__desugarer__var__5__645216452664651));
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg6452064653 =
              ((x10.array.Region)(ret6452764652));
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.region = ((x10.array.Region)(myReg6452064653));
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.rank = 1;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.rect = true;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.zeroBased = true;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.rail = true;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.size = 100;
            
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199716452364654 =
              new x10.array.RectLayout((java.lang.System[]) null);
            
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max06453064655 =
              ((100) - (((int)(1))));
            
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.rank = 1;
            
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.min0 = 0;
            
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6459164626 =
              ((_max06453064655) - (((int)(0))));
            
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6459264627 =
              ((t6459164626) + (((int)(1))));
            
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.delta0 = t6459264627;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6459364628 =
              alloc199716452364654.
                delta0;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t6459464629 =
              ((t6459364628) > (((int)(0))));
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t6459564630 =
               0;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t6459464629) {
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6459564630 = alloc199716452364654.
                                                                                                                                      delta0;
            } else {
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6459564630 = 0;
            }
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6459664631 =
              t6459564630;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.size = t6459664631;
            
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.min1 = 0;
            
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.delta1 = 0;
            
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.min2 = 0;
            
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.delta2 = 0;
            
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.min3 = 0;
            
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.delta3 = 0;
            
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.min = null;
            
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716452364654.delta = null;
            
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.layout = ((x10.array.RectLayout)(alloc199716452364654));
            
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6453264656 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)fact).
                                        layout));
            
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6452464657 =
              this6453264656.
                size;
            
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6459764658 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.core.Double>allocate(x10.rtt.Types.DOUBLE, ((int)(n6452464657)), true)));
            
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
fact.raw = ((x10.core.IndexedMemoryChunk)(t6459764658));
            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret64535 =
               0;
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6459864632 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)fact).
                                               raw));
            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((double[])t6459864632.value)[0] = 1.0;
            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64535 = 1.0;
            
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
int i6060764647 =
              1;
            
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int t6460064648 =
                  i6060764647;
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final boolean t6460864649 =
                  ((t6460064648) <= (((int)(99))));
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
if (!(t6460864649)) {
                    
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
break;
                }
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int i64644 =
                  i6060764647;
                
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06455064634 =
                  i64644;
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final double t6460564635 =
                  ((double)(int)(((int)(i64644))));
                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06454264636 =
                  ((i64644) - (((int)(1))));
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6454364637 =
                   0;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6454464638: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6460364639 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)fact).
                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6460464640 =
                  ((double[])t6460364639.value)[i06454264636];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6454364637 = t6460464640;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6454464638;}
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6460664641 =
                  ret6454364637;
                
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double v6455164642 =
                  ((t6460564635) * (((double)(t6460664641))));
                
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6455264643 =
                   0;
                
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6460764633 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)fact).
                                                   raw));
                
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((double[])t6460764633.value)[i06455064634] = v6455164642;
                
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6455264643 = v6455164642;
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int t6460164645 =
                  i6060764647;
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int t6460264646 =
                  ((t6460164645) + (((int)(1))));
                
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
i6060764647 = t6460264646;
            }
            
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
return fact;
        }
        
        final public static x10.array.Array<x10.core.Double>
          calcFact$P(
          ){
            return au.edu.anu.mm.Factorial.calcFact();
        }
        
        
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
public static double
                                                                                                       getFactorial$O(
                                                                                                       final int i){
            
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final x10.array.Array<x10.core.Double> this64560 =
              ((x10.array.Array)(au.edu.anu.mm.Factorial.getInitialized$factorial()));
            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i064559 =
              i;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret64561 =
               0;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret64562: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t64609 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)this64560).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t64610 =
              ((double[])t64609.value)[i064559];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret64561 = t64610;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret64562;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t64611 =
              ret64561;
            
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
return t64611;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final public au.edu.anu.mm.Factorial
                                                                                                       au$edu$anu$mm$Factorial$$au$edu$anu$mm$Factorial$this(
                                                                                                       ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
return au.edu.anu.mm.Factorial.this;
        }
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
// creation method for java code
        public static au.edu.anu.mm.Factorial $make(){return new au.edu.anu.mm.Factorial((java.lang.System[]) null).$init();}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.Factorial au$edu$anu$mm$Factorial$$init$S() { {
                                                                                        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"

                                                                                        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"

                                                                                        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final au.edu.anu.mm.Factorial this6456864661 =
                                                                                          this;
                                                                                        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
this6456864661.X10$object_lock_id0 = -1;
                                                                                    }
                                                                                    return this;
                                                                                    }
        
        // constructor
        public au.edu.anu.mm.Factorial $init(){return au$edu$anu$mm$Factorial$$init$S();}
        
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
// creation method for java code
        public static au.edu.anu.mm.Factorial $make(final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.Factorial((java.lang.System[]) null).$init(paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.Factorial au$edu$anu$mm$Factorial$$init$S(final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                       
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"

                                                                                                                                       
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"

                                                                                                                                       
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final au.edu.anu.mm.Factorial this6457164662 =
                                                                                                                                         this;
                                                                                                                                       
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
this6457164662.X10$object_lock_id0 = -1;
                                                                                                                                       
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final int t64612 =
                                                                                                                                         paramLock.getIndex();
                                                                                                                                       
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
this.X10$object_lock_id0 = ((int)(t64612));
                                                                                                                                   }
                                                                                                                                   return this;
                                                                                                                                   }
        
        // constructor
        public au.edu.anu.mm.Factorial $init(final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$Factorial$$init$S(paramLock);}
        
        
        
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
final private void
                                                                                                       __fieldInitializers60591(
                                                                                                       ){
            
//#line 20 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Factorial.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers60591$P(
          final au.edu.anu.mm.Factorial Factorial){
            Factorial.__fieldInitializers60591();
        }
        
        public static int
          fieldId$factorial;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$factorial =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static void
          getDeserialized$factorial(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.mm.Factorial.factorial = ((x10.array.Array)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.mm.Factorial.factorial = ((x10.array.Array)(x10.rtt.Types.<x10.array.Array<x10.core.Double>> cast(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf))),new x10.rtt.ParameterizedType(x10.array.Array.$RTT, x10.rtt.Types.DOUBLE))));
            }
            au.edu.anu.mm.Factorial.initStatus$factorial.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.array.Array<x10.core.Double>
          getInitialized$factorial(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.mm.Factorial.initStatus$factorial.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                               (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.mm.Factorial.factorial = ((x10.array.Array)(au.edu.anu.mm.Factorial.calcFact()));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.mm.Factorial.factorial)),
                                                                                  (int)(au.edu.anu.mm.Factorial.fieldId$factorial));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.mm.Factorial.factorial)),
                                                                                  (int)(au.edu.anu.mm.Factorial.fieldId$factorial));
                    }
                    au.edu.anu.mm.Factorial.initStatus$factorial.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.mm.Factorial.initStatus$factorial.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.mm.Factorial.initStatus$factorial.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.mm.Factorial.factorial;
        }
        
        static {
                   au.edu.anu.mm.Factorial.fieldId$factorial = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.mm.Factorial")),
                                                                                                                   ((java.lang.String)("factorial")));
               }
    
}
