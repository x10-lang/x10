package au.edu.anu.chem.mm;


public class TestElectrostatic
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, TestElectrostatic.class);
    
    public static final x10.rtt.RuntimeType<TestElectrostatic> $RTT = new x10.rtt.NamedType<TestElectrostatic>(
    "au.edu.anu.chem.mm.TestElectrostatic", /* base class */TestElectrostatic.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(TestElectrostatic $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        $_obj.SIZE = $deserializer.readDouble();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        TestElectrostatic $_obj = new TestElectrostatic((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        $serializer.write(this.SIZE);
        
    }
    
    // constructor just for allocation
    public TestElectrostatic(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public int
          X10$object_lock_id0;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                    getOrderedLock(
                                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t35545 =
              this.
                X10$object_lock_id0;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.concurrent.OrderedLock t35546 =
              x10.util.concurrent.OrderedLock.getLock((int)(t35545));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return t35546;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                    getStaticOrderedLock(
                                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t35547 =
              au.edu.anu.chem.mm.TestElectrostatic.X10$class_lock_id1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.concurrent.OrderedLock t35548 =
              x10.util.concurrent.OrderedLock.getLock((int)(t35547));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return t35548;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final public static long
          RANDOM_SEED =
          10101110L;
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public static x10.util.Random
          R;
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final public static double
          NOISE =
          0.25;
        
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public double
          SIZE;
        
        
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public double
                                                                                                                    sizeOfCentralCluster$O(
                                                                                                                    ){
            
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return 80.0;
        }
        
        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public void
                                                                                                                    logTime(
                                                                                                                    final java.lang.String desc,
                                                                                                                    final int timerIndex,
                                                                                                                    final au.edu.anu.util.Timer timer,
                                                                                                                    final boolean printNewline){
            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
if (printNewline) {
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.io.Printer t35554 =
                  ((x10.io.Printer)(x10.io.Console.OUT));
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final java.lang.String t35555 =
                  ((desc) + (": %g seconds\n"));
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.array.Array<x10.core.Long> this33265 =
                  ((x10.array.Array)(timer.
                                       total));
                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i033264 =
                  timerIndex;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret33266 =
                   0;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret33267: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t35550 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)this33265).
                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t35551 =
                  ((long[])t35550.value)[i033264];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret33266 = t35551;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret33267;}
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t35552 =
                  ret33266;
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35553 =
                  ((double)(long)(((long)(t35552))));
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35556 =
                  ((t35553) / (((double)(1.0E9))));
                
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
t35554.printf(((java.lang.String)(t35555)),
                                                                                                                                        x10.core.Double.$box(t35556));
            } else {
                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.io.Printer t35561 =
                  ((x10.io.Printer)(x10.io.Console.OUT));
                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final java.lang.String t35562 =
                  ((desc) + (": %g seconds"));
                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.array.Array<x10.core.Long> this33274 =
                  ((x10.array.Array)(timer.
                                       total));
                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i033273 =
                  timerIndex;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
long ret33275 =
                   0;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret33276: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Long> t35557 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Long>)this33274).
                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t35558 =
                  ((long[])t35557.value)[i033273];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret33275 = t35558;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret33276;}
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final long t35559 =
                  ret33275;
                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35560 =
                  ((double)(long)(((long)(t35559))));
                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35563 =
                  ((t35560) / (((double)(1.0E9))));
                
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
t35561.printf(((java.lang.String)(t35562)),
                                                                                                                                        x10.core.Double.$box(t35563));
            }
        }
        
        
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public void
                                                                                                                    logTime(
                                                                                                                    final java.lang.String desc,
                                                                                                                    final int timerIndex,
                                                                                                                    final au.edu.anu.util.Timer timer){
            
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this.logTime(((java.lang.String)(desc)),
                                                                                                                                   (int)(timerIndex),
                                                                                                                                   ((au.edu.anu.util.Timer)(timer)),
                                                                                                                                   (boolean)(true));
        }
        
        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>
                                                                                                                    generateAtoms(
                                                                                                                    final int numAtoms){
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist34321 =
              ((x10.array.Dist)(x10.array.Dist.makeUnique()));
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> t35564 =
              ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.TestElectrostatic.$Closure$10()));
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> init34322 =
              ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.array.Point,x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>)(x10.core.fun.Fun_0_1)
                                        t35564)));
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> alloc3102334323 =
              ((x10.array.DistArray)(new x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.util.ArrayList.$RTT, au.edu.anu.chem.mm.MMAtom.$RTT))));
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102334323.$init(((x10.array.Dist)(dist34321)),
                                                                                                                                      ((x10.core.fun.Fun_0_1<x10.array.Point,x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>)(init34322)),(java.lang.Class<?>) null);
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> tempAtoms =
              ((x10.array.DistArray)(alloc3102334323));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35565 =
              ((double)(int)(((int)(numAtoms))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35566 =
              java.lang.Math.cbrt(((double)(t35565)));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35567 =
              java.lang.Math.ceil(((double)(t35566)));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int gridSize =
              ((int)(double)(((double)(t35567))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35570 =
              ((80.0) / (((double)(2.0))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35569 =
              this.sizeOfCentralCluster$O();
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35571 =
              ((t35569) / (((double)(2.0))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double clusterStart =
              ((t35570) - (((double)(t35571))));
            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
int gridPoint =
              0;
            {
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.lang.FinishState x10$__var1 =
                  x10.lang.Runtime.startFinish();
                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
try {try {{
                    {
                        
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
int i =
                          0;
                        
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
for (;
                                                                                                                                       true;
                                                                                                                                       ) {
                            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t35573 =
                              i;
                            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final boolean t35621 =
                              ((t35573) < (((int)(numAtoms))));
                            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
if (!(t35621)) {
                                
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
break;
                            }
                            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3557635642 =
                              gridPoint;
                            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3557735643 =
                              ((gridSize) * (((int)(gridSize))));
                            
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int gridX35644 =
                              ((t3557635642) / (((int)(t3557735643))));
                            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3557935645 =
                              gridPoint;
                            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3557835646 =
                              ((gridX35644) * (((int)(gridSize))));
                            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3558035647 =
                              ((t3557835646) * (((int)(gridSize))));
                            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3558135648 =
                              ((t3557935645) - (((int)(t3558035647))));
                            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int gridY35649 =
                              ((t3558135648) / (((int)(gridSize))));
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3558335650 =
                              gridPoint;
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3558235651 =
                              ((gridX35644) * (((int)(gridSize))));
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3558435652 =
                              ((t3558235651) * (((int)(gridSize))));
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3558535653 =
                              ((t3558335650) - (((int)(t3558435652))));
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3558635654 =
                              ((gridY35649) * (((int)(gridSize))));
                            
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int gridZ35655 =
                              ((t3558535653) - (((int)(t3558635654))));
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3558735656 =
                              ((double)(int)(((int)(gridX35644))));
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3558835657 =
                              ((t3558735656) + (((double)(0.5))));
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3558935658 =
                              au.edu.anu.chem.mm.TestElectrostatic.randomNoise$O();
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559235659 =
                              ((t3558835657) + (((double)(t3558935658))));
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559035660 =
                              this.sizeOfCentralCluster$O();
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559135661 =
                              ((double)(int)(((int)(gridSize))));
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559335662 =
                              ((t3559035660) / (((double)(t3559135661))));
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559435663 =
                              ((t3559235659) * (((double)(t3559335662))));
                            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double x35664 =
                              ((clusterStart) + (((double)(t3559435663))));
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559535665 =
                              ((double)(int)(((int)(gridY35649))));
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559635666 =
                              ((t3559535665) + (((double)(0.5))));
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559735667 =
                              au.edu.anu.chem.mm.TestElectrostatic.randomNoise$O();
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560035668 =
                              ((t3559635666) + (((double)(t3559735667))));
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559835669 =
                              this.sizeOfCentralCluster$O();
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3559935670 =
                              ((double)(int)(((int)(gridSize))));
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560135671 =
                              ((t3559835669) / (((double)(t3559935670))));
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560235672 =
                              ((t3560035668) * (((double)(t3560135671))));
                            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double y35673 =
                              ((clusterStart) + (((double)(t3560235672))));
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560335674 =
                              ((double)(int)(((int)(gridZ35655))));
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560435675 =
                              ((t3560335674) + (((double)(0.5))));
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560535676 =
                              au.edu.anu.chem.mm.TestElectrostatic.randomNoise$O();
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560835677 =
                              ((t3560435675) + (((double)(t3560535676))));
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560635678 =
                              this.sizeOfCentralCluster$O();
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560735679 =
                              ((double)(int)(((int)(gridSize))));
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3560935680 =
                              ((t3560635678) / (((double)(t3560735679))));
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3561035681 =
                              ((t3560835677) * (((double)(t3560935680))));
                            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double z35682 =
                              ((clusterStart) + (((double)(t3561035681))));
                            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3561135683 =
                              i;
                            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3561235684 =
                              ((t3561135683) % (((int)(2))));
                            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final boolean t3561335685 =
                              ((int) t3561235684) ==
                            ((int) 0);
                            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
int t3561435686 =
                               0;
                            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
if (t3561335685) {
                                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
t3561435686 = 1;
                            } else {
                                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
t3561435686 = -1;
                            }
                            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int charge35687 =
                              t3561435686;
                            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int p35688 =
                              this.getPlaceId$O((double)(x35664),
                                                (double)(y35673),
                                                (double)(z35682));
                            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
x10.lang.Runtime.runAsync(((x10.core.fun.VoidFun_0_0)(new au.edu.anu.chem.mm.TestElectrostatic.$Closure$12(p35688,
                                                                                                                                                                                                                                                 x35664,
                                                                                                                                                                                                                                                 y35673,
                                                                                                                                                                                                                                                 z35682,
                                                                                                                                                                                                                                                 charge35687,
                                                                                                                                                                                                                                                 tempAtoms,(java.lang.Class<?>) null))));
                            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3561935693 =
                              gridPoint;
                            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3562035694 =
                              ((t3561935693) + (((int)(1))));
                            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
gridPoint = t3562035694;
                            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3557435695 =
                              i;
                            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t3557535696 =
                              ((t3557435695) + (((int)(1))));
                            
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
i = t3557535696;
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__3__) {
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__3__)));
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
throw new x10.lang.RuntimeException();
                }finally {{
                     
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var1)));
                 }}
                }
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist34326 =
              ((x10.array.Dist)(x10.array.Dist.makeUnique()));
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.mm.MMAtom>> t35625 =
              ((x10.core.fun.Fun_0_1)(new au.edu.anu.chem.mm.TestElectrostatic.$Closure$13(tempAtoms,(java.lang.Class<?>) null)));
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.mm.MMAtom>> init34327 =
              ((x10.core.fun.Fun_0_1)(((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)(x10.core.fun.Fun_0_1)
                                        t35625)));
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> alloc3102334328 =
              ((x10.array.DistArray)(new x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.mm.MMAtom.$RTT))));
            
//#line 140 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102334328.$init(((x10.array.Dist)(dist34326)),
                                                                                                                                      ((x10.core.fun.Fun_0_1<x10.array.Point,x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)(init34327)),(java.lang.Class<?>) null);
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms =
              ((x10.array.DistArray)(alloc3102334328));
            
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return atoms;
            }
        
        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public int
                                                                                                                    getPlaceId$O(
                                                                                                                    final double x,
                                                                                                                    final double y,
                                                                                                                    final double z){
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35628 =
              ((x) / (((double)(80.0))));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t35627 =
              x10.lang.Place.getInitialized$MAX_PLACES();
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35629 =
              ((double)(int)(((int)(t35627))));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35630 =
              ((t35628) * (((double)(t35629))));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t35631 =
              ((int)(double)(((double)(t35630))));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return t35631;
        }
        
        
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
public static double
                                                                                                                    randomNoise$O(
                                                                                                                    ){
            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.Random t35632 =
              ((x10.util.Random)(au.edu.anu.chem.mm.TestElectrostatic.getInitialized$R()));
            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35633 =
              t35632.nextDouble$O();
            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35634 =
              ((t35633) - (((double)(0.5))));
            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t35636 =
              ((t35634) * (((double)(0.25))));
            
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return t35636;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final public au.edu.anu.chem.mm.TestElectrostatic
                                                                                                                    au$edu$anu$chem$mm$TestElectrostatic$$au$edu$anu$chem$mm$TestElectrostatic$this(
                                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return au.edu.anu.chem.mm.TestElectrostatic.this;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.TestElectrostatic $make(){return new au.edu.anu.chem.mm.TestElectrostatic((java.lang.System[]) null).$init();}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.TestElectrostatic au$edu$anu$chem$mm$TestElectrostatic$$init$S() { {
                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"

                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"

                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final au.edu.anu.chem.mm.TestElectrostatic this3553935697 =
                                                                                                                    this;
                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this3553935697.X10$object_lock_id0 = -1;
                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this3553935697.SIZE = 80.0;
                                                                                                              }
                                                                                                              return this;
                                                                                                              }
        
        // constructor
        public au.edu.anu.chem.mm.TestElectrostatic $init(){return au$edu$anu$chem$mm$TestElectrostatic$$init$S();}
        
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
// creation method for java code
        public static au.edu.anu.chem.mm.TestElectrostatic $make(final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.chem.mm.TestElectrostatic((java.lang.System[]) null).$init(paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.chem.mm.TestElectrostatic au$edu$anu$chem$mm$TestElectrostatic$$init$S(final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"

                                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"

                                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final au.edu.anu.chem.mm.TestElectrostatic this3554235698 =
                                                                                                                                                                   this;
                                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this3554235698.X10$object_lock_id0 = -1;
                                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this3554235698.SIZE = 80.0;
                                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int t35637 =
                                                                                                                                                                   paramLock.getIndex();
                                                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this.X10$object_lock_id0 = ((int)(t35637));
                                                                                                                                                             }
                                                                                                                                                             return this;
                                                                                                                                                             }
        
        // constructor
        public au.edu.anu.chem.mm.TestElectrostatic $init(final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$chem$mm$TestElectrostatic$$init$S(paramLock);}
        
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final private void
                                                                                                                    __fieldInitializers23648(
                                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this.X10$object_lock_id0 = -1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
this.SIZE = 80.0;
        }
        
        final public static void
          __fieldInitializers23648$P(
          final au.edu.anu.chem.mm.TestElectrostatic TestElectrostatic){
            TestElectrostatic.__fieldInitializers23648();
        }
        
        public static int
          fieldId$R;
        final public static x10.core.concurrent.AtomicInteger
          initStatus$R =
          new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
        
        public static long
          getInitialized$RANDOM_SEED(
          ){
            return au.edu.anu.chem.mm.TestElectrostatic.RANDOM_SEED;
        }
        
        public static void
          getDeserialized$R(
          byte[] buf){
            if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                  ((boolean) true)) {
                au.edu.anu.chem.mm.TestElectrostatic.R = ((x10.util.Random)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
            } else {
                au.edu.anu.chem.mm.TestElectrostatic.R = ((x10.util.Random)(x10.rtt.Types.<x10.util.Random> cast(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf))),x10.util.Random.$RTT)));
            }
            au.edu.anu.chem.mm.TestElectrostatic.initStatus$R.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
            x10.runtime.impl.java.InitDispatcher.lockInitialized();
            x10.runtime.impl.java.InitDispatcher.notifyInitialized();
        }
        
        public static x10.util.Random
          getInitialized$R(
          ){
            if (((int) x10.lang.Runtime.hereInt$O()) ==
                ((int) 0)) {
                if (au.edu.anu.chem.mm.TestElectrostatic.initStatus$R.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                    (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                    au.edu.anu.chem.mm.TestElectrostatic.R = ((x10.util.Random)(new x10.util.Random((java.lang.System[]) null).$init(((long)(10101110L)))));
                    if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                          ((boolean) true)) {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.mm.TestElectrostatic.R)),
                                                                                  (int)(au.edu.anu.chem.mm.TestElectrostatic.fieldId$R));
                    } else {
                        x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.chem.mm.TestElectrostatic.R)),
                                                                                  (int)(au.edu.anu.chem.mm.TestElectrostatic.fieldId$R));
                    }
                    au.edu.anu.chem.mm.TestElectrostatic.initStatus$R.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                }
            }
            if (((int) au.edu.anu.chem.mm.TestElectrostatic.initStatus$R.get()) !=
                ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                while (((int) au.edu.anu.chem.mm.TestElectrostatic.initStatus$R.get()) !=
                       ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                }
                x10.runtime.impl.java.InitDispatcher.unlockInitialized();
            }
            return au.edu.anu.chem.mm.TestElectrostatic.R;
        }
        
        public static double
          getInitialized$NOISE(
          ){
            return au.edu.anu.chem.mm.TestElectrostatic.NOISE;
        }
        
        static {
                   au.edu.anu.chem.mm.TestElectrostatic.fieldId$R = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.chem.mm.TestElectrostatic")),
                                                                                                                        ((java.lang.String)("R")));
               }
        
        public static class $Closure$10
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$10.class);
            
            public static final x10.rtt.RuntimeType<$Closure$10> $RTT = new x10.rtt.StaticFunType<$Closure$10>(
            /* base class */$Closure$10.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, new x10.rtt.ParameterizedType(x10.util.ArrayList.$RTT, au.edu.anu.chem.mm.MMAtom.$RTT)), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$10 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$10 $_obj = new $Closure$10((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                
            }
            
            // constructor just for allocation
            public $Closure$10(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply((x10.array.Point)a1);}
            
                
                public x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>
                  $apply(
                  final x10.array.Point id$18){
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom> alloc23968 =
                      ((x10.util.ArrayList)(new x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>((java.lang.System[]) null, au.edu.anu.chem.mm.MMAtom.$RTT)));
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
alloc23968.$init();
                    
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return alloc23968;
                }
                
                // creation method for java code
                public static au.edu.anu.chem.mm.TestElectrostatic.$Closure$10 $make(){return new $Closure$10();}
                public $Closure$10() { {
                                              
                                          }}
                
            }
            
        public static class $Closure$11
        extends x10.core.Ref
          implements x10.core.fun.VoidFun_0_0,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$11.class);
            
            public static final x10.rtt.RuntimeType<$Closure$11> $RTT = new x10.rtt.StaticVoidFunType<$Closure$11>(
            /* base class */$Closure$11.class
            , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$11 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $_obj.x35664 = $deserializer.readDouble();
                $_obj.y35673 = $deserializer.readDouble();
                $_obj.z35682 = $deserializer.readDouble();
                $_obj.charge35687 = $deserializer.readInt();
                x10.array.DistArray tempAtoms = (x10.array.DistArray) $deserializer.readRef();
                $_obj.tempAtoms = tempAtoms;
                $_obj.p35688 = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$11 $_obj = new $Closure$11((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.x35664);
                $serializer.write(this.y35673);
                $serializer.write(this.z35682);
                $serializer.write(this.charge35687);
                if (tempAtoms instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.tempAtoms);
                } else {
                $serializer.write(this.tempAtoms);
                }
                $serializer.write(this.p35688);
                
            }
            
            // constructor just for allocation
            public $Closure$11(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            
                
                public void
                  $apply(
                  ){
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final au.edu.anu.chem.mm.MMAtom atom35691 =
                      ((au.edu.anu.chem.mm.MMAtom)(new au.edu.anu.chem.mm.MMAtom((java.lang.System[]) null)));
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10x.vector.Point3d alloc2396935639 =
                      new x10x.vector.Point3d((java.lang.System[]) null);
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.concurrent.OrderedLock t3561535638 =
                      x10.util.concurrent.OrderedLock.createNewLock();
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
alloc2396935639.$init(this.
                                                                                                                                                      x35664,
                                                                                                                                                    this.
                                                                                                                                                      y35673,
                                                                                                                                                    this.
                                                                                                                                                      z35682,
                                                                                                                                                    t3561535638);
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final double t3561635640 =
                      ((double)(int)(((int)(this.
                                              charge35687))));
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.concurrent.OrderedLock t3561735641 =
                      x10.util.concurrent.OrderedLock.createNewLock();
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
atom35691.$init(alloc2396935639,
                                                                                                                                              t3561635640,
                                                                                                                                              t3561735641);
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
try {{
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
x10.lang.Runtime.enterAtomic();
                        {
                            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom> t3561835692 =
                              ((x10.util.ArrayList)(((x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                                           tempAtoms).$apply$G((int)(this.
                                                                                                                                                       p35688))));
                            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
((x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>)t3561835692).add_0_$$x10$util$ArrayList_T$O(((au.edu.anu.chem.mm.MMAtom)(atom35691)));
                        }
                    }}finally {{
                          
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
x10.lang.Runtime.exitAtomic();
                      }}
                    }
                
                public double
                  x35664;
                public double
                  y35673;
                public double
                  z35682;
                public int
                  charge35687;
                public x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>
                  tempAtoms;
                public int
                  p35688;
                
                // creation method for java code
                public static au.edu.anu.chem.mm.TestElectrostatic.$Closure$11 $make(final double x35664,
                                                                                     final double y35673,
                                                                                     final double z35682,
                                                                                     final int charge35687,
                                                                                     final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> tempAtoms,
                                                                                     final int p35688,java.lang.Class<?> $dummy0){return new $Closure$11(x35664,y35673,z35682,charge35687,tempAtoms,p35688,(java.lang.Class<?>) null);}
                public $Closure$11(final double x35664,
                                   final double y35673,
                                   final double z35682,
                                   final int charge35687,
                                   final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> tempAtoms,
                                   final int p35688,java.lang.Class<?> $dummy0) { {
                                                                                         this.x35664 = x35664;
                                                                                         this.y35673 = y35673;
                                                                                         this.z35682 = z35682;
                                                                                         this.charge35687 = charge35687;
                                                                                         this.tempAtoms = ((x10.array.DistArray)(tempAtoms));
                                                                                         this.p35688 = p35688;
                                                                                     }}
                
                }
                
            public static class $Closure$12
            extends x10.core.Ref
              implements x10.core.fun.VoidFun_0_0,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$12.class);
                
                public static final x10.rtt.RuntimeType<$Closure$12> $RTT = new x10.rtt.StaticVoidFunType<$Closure$12>(
                /* base class */$Closure$12.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$12 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $_obj.p35688 = $deserializer.readInt();
                    $_obj.x35664 = $deserializer.readDouble();
                    $_obj.y35673 = $deserializer.readDouble();
                    $_obj.z35682 = $deserializer.readDouble();
                    $_obj.charge35687 = $deserializer.readInt();
                    x10.array.DistArray tempAtoms = (x10.array.DistArray) $deserializer.readRef();
                    $_obj.tempAtoms = tempAtoms;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$12 $_obj = new $Closure$12((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    $serializer.write(this.p35688);
                    $serializer.write(this.x35664);
                    $serializer.write(this.y35673);
                    $serializer.write(this.z35682);
                    $serializer.write(this.charge35687);
                    if (tempAtoms instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.tempAtoms);
                    } else {
                    $serializer.write(this.tempAtoms);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$12(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final int id3432435689 =
                          this.
                            p35688;
                        
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
final x10.lang.Place alloc323593432535690 =
                          new x10.lang.Place((java.lang.System[]) null);
                        
//#line 127 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Place.x10"
alloc323593432535690.$init(((int)(id3432435689)));
                        
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(alloc323593432535690)),
                                                                                                                                                         ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.chem.mm.TestElectrostatic.$Closure$11(((double)(this.
                                                                                                                                                                                                                                                      x35664)),
                                                                                                                                                                                                                                          ((double)(this.
                                                                                                                                                                                                                                                      y35673)),
                                                                                                                                                                                                                                          ((double)(this.
                                                                                                                                                                                                                                                      z35682)),
                                                                                                                                                                                                                                          ((int)(this.
                                                                                                                                                                                                                                                   charge35687)),
                                                                                                                                                                                                                                          ((x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>)(this.
                                                                                                                                                                                                                                                                                                                  tempAtoms)),
                                                                                                                                                                                                                                          ((int)(this.
                                                                                                                                                                                                                                                   p35688)),(java.lang.Class<?>) null))));
                    }
                    
                    public int
                      p35688;
                    public double
                      x35664;
                    public double
                      y35673;
                    public double
                      z35682;
                    public int
                      charge35687;
                    public x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>
                      tempAtoms;
                    
                    // creation method for java code
                    public static au.edu.anu.chem.mm.TestElectrostatic.$Closure$12 $make(final int p35688,
                                                                                         final double x35664,
                                                                                         final double y35673,
                                                                                         final double z35682,
                                                                                         final int charge35687,
                                                                                         final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> tempAtoms,java.lang.Class<?> $dummy0){return new $Closure$12(p35688,x35664,y35673,z35682,charge35687,tempAtoms,(java.lang.Class<?>) null);}
                    public $Closure$12(final int p35688,
                                       final double x35664,
                                       final double y35673,
                                       final double z35682,
                                       final int charge35687,
                                       final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> tempAtoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                                               this.p35688 = p35688;
                                                                                                                                                               this.x35664 = x35664;
                                                                                                                                                               this.y35673 = y35673;
                                                                                                                                                               this.z35682 = z35682;
                                                                                                                                                               this.charge35687 = charge35687;
                                                                                                                                                               this.tempAtoms = ((x10.array.DistArray)(tempAtoms));
                                                                                                                                                           }}
                    
                }
                
            public static class $Closure$13
            extends x10.core.Ref
              implements x10.core.fun.Fun_0_1,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$13.class);
                
                public static final x10.rtt.RuntimeType<$Closure$13> $RTT = new x10.rtt.StaticFunType<$Closure$13>(
                /* base class */$Closure$13.class
                , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.array.Point.$RTT, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.mm.MMAtom.$RTT)), x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$13 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    x10.array.DistArray tempAtoms = (x10.array.DistArray) $deserializer.readRef();
                    $_obj.tempAtoms = tempAtoms;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$13 $_obj = new $Closure$13((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    if (tempAtoms instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.tempAtoms);
                    } else {
                    $serializer.write(this.tempAtoms);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$13(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
                public java.lang.Object
                  $apply(final java.lang.Object a1,final x10.rtt.Type t1){return $apply((x10.array.Point)a1);}
                
                    
                    public x10.array.Array<au.edu.anu.chem.mm.MMAtom>
                      $apply(
                      final x10.array.Point id19){
                        
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final int p =
                          id19.$apply$O((int)(0));
                        
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom> t35622 =
                          ((x10.util.ArrayList)(((x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                                       tempAtoms).$apply$G((int)(p))));
                        
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom> t35623 =
                          ((x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>)
                            t35622);
                        
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> t35624 =
                          ((x10.array.Array)(((x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>)t35623).toArray()));
                        
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/anu-chem/src/au/edu/anu/chem/mm/TestElectrostatic.x10"
return t35624;
                    }
                    
                    public x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>>
                      tempAtoms;
                    
                    // creation method for java code
                    public static au.edu.anu.chem.mm.TestElectrostatic.$Closure$13 $make(final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> tempAtoms,java.lang.Class<?> $dummy0){return new $Closure$13(tempAtoms,(java.lang.Class<?>) null);}
                    public $Closure$13(final x10.array.DistArray<x10.util.ArrayList<au.edu.anu.chem.mm.MMAtom>> tempAtoms,java.lang.Class<?> $dummy0) { {
                                                                                                                                                               this.tempAtoms = ((x10.array.DistArray)(tempAtoms));
                                                                                                                                                           }}
                    
                }
                
            
            }
            