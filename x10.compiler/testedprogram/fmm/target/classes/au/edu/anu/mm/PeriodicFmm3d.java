package au.edu.anu.mm;


public class PeriodicFmm3d
extends au.edu.anu.mm.Fmm3d
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, PeriodicFmm3d.class);
    
    public static final x10.rtt.RuntimeType<PeriodicFmm3d> $RTT = new x10.rtt.NamedType<PeriodicFmm3d>(
    "au.edu.anu.mm.PeriodicFmm3d", /* base class */PeriodicFmm3d.class
    , /* parents */ new x10.rtt.Type[] {au.edu.anu.mm.Fmm3d.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(PeriodicFmm3d $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        au.edu.anu.mm.Fmm3d.$_deserialize_body($_obj, $deserializer);
        $_obj.numShells = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        PeriodicFmm3d $_obj = new PeriodicFmm3d((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.numShells);
        
    }
    
    // constructor just for allocation
    public PeriodicFmm3d(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public int
          X10$object_lock_id0;
        
        
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public x10.util.concurrent.OrderedLock
                                                                                                           getOrderedLock(
                                                                                                           ){
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42168 =
              this.
                X10$object_lock_id0;
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t42169 =
              x10.util.concurrent.OrderedLock.getLock((int)(t42168));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return t42169;
        }
        
        
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                           getStaticOrderedLock(
                                                                                                           ){
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42170 =
              au.edu.anu.mm.PeriodicFmm3d.X10$class_lock_id1;
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t42171 =
              x10.util.concurrent.OrderedLock.getLock((int)(t42170));
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return t42171;
        }
        
        
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
/** The number of concentric shells of copies of the unit cell. */public int
          numShells;
        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public static int
          TIMER_INDEX_MACROSCOPIC =
          8;
        
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public static x10.array.Region
          threeCube;
        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public static x10.array.Region
          nineCube;
        
        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
// creation method for java code
        public static au.edu.anu.mm.PeriodicFmm3d $make(final double density,
                                                        final int numTerms,
                                                        final x10x.vector.Point3d topLeftFront,
                                                        final double size,
                                                        final int numAtoms,
                                                        final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                        final int numShells,java.lang.Class<?> $dummy0){return new au.edu.anu.mm.PeriodicFmm3d((java.lang.System[]) null).$init(density,numTerms,topLeftFront,size,numAtoms,atoms,numShells,(java.lang.Class<?>) null);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.PeriodicFmm3d au$edu$anu$mm$PeriodicFmm3d$$init$S(final double density,
                                                                                     final int numTerms,
                                                                                     final x10x.vector.Point3d topLeftFront,
                                                                                     final double size,
                                                                                     final int numAtoms,
                                                                                     final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                                     final int numShells,java.lang.Class<?> $dummy0) { {
                                                                                                                                              
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
super.$init(((double)(density)),
                                                                                                                                                                                                                                                           ((int)(numTerms)),
                                                                                                                                                                                                                                                           ((int)(1)),
                                                                                                                                                                                                                                                           ((x10x.vector.Point3d)(topLeftFront)),
                                                                                                                                                                                                                                                           ((double)(size)),
                                                                                                                                                                                                                                                           ((int)(numAtoms)),
                                                                                                                                                                                                                                                           ((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)(atoms)),
                                                                                                                                                                                                                                                           ((int)(0)),
                                                                                                                                                                                                                                                           ((boolean)(true)),(java.lang.Class<?>[]) null);
                                                                                                                                              
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"

                                                                                                                                              
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.PeriodicFmm3d this3892842681 =
                                                                                                                                                this;
                                                                                                                                              
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this3892842681.X10$object_lock_id0 = -1;
                                                                                                                                              
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.numShells = numShells;
                                                                                                                                          }
                                                                                                                                          return this;
                                                                                                                                          }
        
        // constructor
        public au.edu.anu.mm.PeriodicFmm3d $init(final double density,
                                                 final int numTerms,
                                                 final x10x.vector.Point3d topLeftFront,
                                                 final double size,
                                                 final int numAtoms,
                                                 final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                 final int numShells,java.lang.Class<?> $dummy0){return au$edu$anu$mm$PeriodicFmm3d$$init$S(density,numTerms,topLeftFront,size,numAtoms,atoms,numShells, $dummy0);}
        
        
        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
// creation method for java code
        public static au.edu.anu.mm.PeriodicFmm3d $make(final double density,
                                                        final int numTerms,
                                                        final x10x.vector.Point3d topLeftFront,
                                                        final double size,
                                                        final int numAtoms,
                                                        final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                        final int numShells,
                                                        final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.PeriodicFmm3d((java.lang.System[]) null).$init(density,numTerms,topLeftFront,size,numAtoms,atoms,numShells,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.PeriodicFmm3d au$edu$anu$mm$PeriodicFmm3d$$init$S(final double density,
                                                                                     final int numTerms,
                                                                                     final x10x.vector.Point3d topLeftFront,
                                                                                     final double size,
                                                                                     final int numAtoms,
                                                                                     final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                                     final int numShells,
                                                                                     final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                               
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
super.$init(((double)(density)),
                                                                                                                                                                                                                                                            ((int)(numTerms)),
                                                                                                                                                                                                                                                            ((int)(1)),
                                                                                                                                                                                                                                                            ((x10x.vector.Point3d)(topLeftFront)),
                                                                                                                                                                                                                                                            ((double)(size)),
                                                                                                                                                                                                                                                            ((int)(numAtoms)),
                                                                                                                                                                                                                                                            ((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)(atoms)),
                                                                                                                                                                                                                                                            ((int)(0)),
                                                                                                                                                                                                                                                            ((boolean)(true)),(java.lang.Class<?>[]) null);
                                                                                                                                               
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"

                                                                                                                                               
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.PeriodicFmm3d this3893142682 =
                                                                                                                                                 this;
                                                                                                                                               
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this3893142682.X10$object_lock_id0 = -1;
                                                                                                                                               
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.numShells = numShells;
                                                                                                                                               
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42172 =
                                                                                                                                                 paramLock.getIndex();
                                                                                                                                               
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.X10$object_lock_id0 = ((int)(t42172));
                                                                                                                                           }
                                                                                                                                           return this;
                                                                                                                                           }
        
        // constructor
        public au.edu.anu.mm.PeriodicFmm3d $init(final double density,
                                                 final int numTerms,
                                                 final x10x.vector.Point3d topLeftFront,
                                                 final double size,
                                                 final int numAtoms,
                                                 final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                 final int numShells,
                                                 final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$PeriodicFmm3d$$init$S(density,numTerms,topLeftFront,size,numAtoms,atoms,numShells,paramLock);}
        
        
        
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public double
                                                                                                           calculateEnergy$O(
                                                                                                           ){
            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42173 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42173.start((int)(0));
            {
                
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.FinishState x10$__var2 =
                  x10.lang.Runtime.startFinish();
                
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
try {try {{
                    {
                        
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$14(this))));
                        
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.multipoleLowestLevel();
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.combineMultipoles();
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.combineMacroscopicExpansions();
                        
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.transformToLocal();
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__4__) {
                    
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__4__)));
                    
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
throw new x10.lang.RuntimeException();
                }finally {{
                     
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var2)));
                 }}
                }
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42175 =
              this.getDirectEnergy$O();
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42176 =
              this.getFarFieldEnergy$O();
            
//#line 80 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double totalEnergy =
              ((t42175) + (((double)(t42176))));
            
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42177 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42177.stop((int)(0));
            
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return totalEnergy;
            }
        
        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public void
                                                                                                           combineMacroscopicExpansions(
                                                                                                           ){
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42179 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42179.start((int)(8));
            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int numShells =
              this.
                numShells;
            
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int numTerms =
              this.
                numTerms;
            
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double size =
              this.
                size;
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Array<x10.array.DistArray<au.edu.anu.mm.FmmBox>> boxes =
              ((x10.array.Array)(this.
                                   boxes));
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.DistArray<au.edu.anu.mm.FmmBox> ret38935 =
               null;
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret38936: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.DistArray<au.edu.anu.mm.FmmBox>> t42181 =
              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.DistArray<au.edu.anu.mm.FmmBox>>)boxes).
                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.DistArray<au.edu.anu.mm.FmmBox> t42182 =
              ((x10.array.DistArray)(((x10.array.DistArray[])t42181.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret38935 = ((x10.array.DistArray)(t42182));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret38936;}
            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.DistArray<au.edu.anu.mm.FmmBox> t42183 =
              ((x10.array.DistArray)(ret38935));
            
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Dist t42184 =
              ((x10.array.Dist)(((x10.array.DistArray<au.edu.anu.mm.FmmBox>)t42183).
                                  dist));
            
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Place t42314 =
              t42184.$apply((int)(0),
                            (int)(0),
                            (int)(0));
            
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t42314)),
                                                                                                                                    ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$15(numShells,
                                                                                                                                                                                                            boxes,
                                                                                                                                                                                                            numTerms,
                                                                                                                                                                                                            size,(java.lang.Class<?>) null))));
            
//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42315 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42315.stop((int)(8));
        }
        
        
//#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public void
                                                                                                            assignAtomsToBoxes(
                                                                                                            ){
            
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42317 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42317.start((int)(7));
            
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d offset =
              ((x10x.vector.Vector3d)(this.
                                        offset));
            
//#line 152 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes =
              ((x10.array.DistArray)(this.
                                       lowestLevelBoxes));
            
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms =
              ((x10.array.DistArray)(this.
                                       atoms));
            
//#line 154 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int lowestLevelDim =
              this.
                lowestLevelDim;
            
//#line 155 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double size =
              this.
                size;
            
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d dipole;
            {
                
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.FinishState x10$__var4 =
                  ((x10.lang.FinishState)(x10.lang.Runtime.<x10x.vector.Vector3d>startCollectingFinish_0_$_x10$lang$Runtime_T_$(x10x.vector.Vector3d.$RTT, ((x10.lang.Reducible)((new java.io.Serializable() { au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer eval() {
                                                                                                                                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer alloc24679 =
                                                                                                                                      new au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer((java.lang.System[]) null);
                                                                                                                                    {
                                                                                                                                        
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t42319 =
                                                                                                                                          x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                        
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24679.$init(t42319);
                                                                                                                                    }
                                                                                                                                    return alloc24679;
                                                                                                                                } }.eval()))))));
                
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
try {try {{
                    {
                        {
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.ensureNotInAtomic();
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Dist __lowerer__var__6__ =
                              ((x10.array.Dist)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)atoms).
                                                  dist));
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.lang.Place> __lowerer__var__7__43271 =
                                                                                                                                     ((x10.lang.Iterable<x10.lang.Place>)__lowerer__var__6__.places()).iterator();
                                                                                                                                   ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__7__43271).hasNext$O();
                                                                                                                                   ) {
                                
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Place __lowerer__var__7__ =
                                  ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__7__43271).next$G();
                                
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(__lowerer__var__7__)),
                                                                                                                                                            ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$19(__lowerer__var__6__,
                                                                                                                                                                                                                                    atoms,
                                                                                                                                                                                                                                    offset,
                                                                                                                                                                                                                                    lowestLevelDim,
                                                                                                                                                                                                                                    size,
                                                                                                                                                                                                                                    lowestLevelBoxes,(java.lang.Class<?>) null))));
                            }
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__8__) {
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__8__)));
                    
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
throw new x10.lang.RuntimeException();
                }finally {{
                     
//#line 156 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
dipole = x10.lang.Runtime.<x10x.vector.Vector3d>stopCollectingFinish$G(x10x.vector.Vector3d.$RTT, ((x10.lang.FinishState)(x10$__var4)));
                 }}
                }
            
//#line 177 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.cancelDipole(((x10x.vector.Vector3d)(dipole)));
            {
                
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.ensureNotInAtomic();
                
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.FinishState x10$__var5 =
                  x10.lang.Runtime.startFinish();
                
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
try {try {{
                    {
                        {
                            
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.ensureNotInAtomic();
                            
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Dist __lowerer__var__9__ =
                              ((x10.array.Dist)(((x10.array.DistArray<au.edu.anu.mm.FmmBox>)lowestLevelBoxes).
                                                  dist));
                            
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.lang.Place> __lowerer__var__10__43283 =
                                                                                                                                     ((x10.lang.Iterable<x10.lang.Place>)__lowerer__var__9__.places()).iterator();
                                                                                                                                   ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__10__43283).hasNext$O();
                                                                                                                                   ) {
                                
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Place __lowerer__var__10__ =
                                  ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__10__43283).next$G();
                                
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(__lowerer__var__10__)),
                                                                                                                                                            ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$21(__lowerer__var__9__,
                                                                                                                                                                                                                                    lowestLevelBoxes,(java.lang.Class<?>) null))));
                            }
                        }
                    }
                }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__11__) {
                    
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__11__)));
                    
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
throw new x10.lang.RuntimeException();
                }finally {{
                     
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var5)));
                 }}
                }
            
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42371 =
              ((au.edu.anu.util.Timer)(timer));
            
//#line 189 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42371.stop((int)(7));
            }
            
            
//#line 192 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public void
                                                                                                                addAtomToLowestLevelBoxAsync(
                                                                                                                final x10.array.Point boxIndex,
                                                                                                                final x10x.vector.Point3d offsetCentre,
                                                                                                                final double charge){
                
//#line 193 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double size =
                  this.
                    size;
                
//#line 194 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int numTerms =
                  this.
                    numTerms;
                
//#line 195 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes =
                  ((x10.array.DistArray)(this.
                                           lowestLevelBoxes));
                
//#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$23(lowestLevelBoxes,
                                                                                                                                                                                                                    boxIndex,
                                                                                                                                                                                                                    offsetCentre,
                                                                                                                                                                                                                    charge,
                                                                                                                                                                                                                    size,
                                                                                                                                                                                                                    numTerms,(java.lang.Class<?>) null))));
            }
            
            
//#line 212 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public x10x.vector.Vector3d
                                                                                                                cancelDipole(
                                                                                                                final x10x.vector.Vector3d dipole){
                
//#line 214 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10x.vector.Vector3d newDipole =
                  dipole;
                {
                    
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.ensureNotInAtomic();
                    
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.FinishState x10$__var6 =
                      x10.lang.Runtime.startFinish();
                    
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
try {try {{
                        {
                            
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d alloc24681 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4238842933 =
                              size;
                            
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4238942934 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24681.$init(((double)(t4238842933)),
                                                                                                                                               ((double)(0.0)),
                                                                                                                                               ((double)(0.0)),
                                                                                                                                               t4238942934);
                            
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this42052 =
                              alloc24681;
                            
//#line 31 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d that42051 =
                              ((x10x.vector.Vector3d)(offset));
                            
//#line 27 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d b42053 =
                              ((x10x.vector.Vector3d)(that42051));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc2425242054 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4239042935 =
                              this42052.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4239142936 =
                              b42053.
                                i;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4239642937 =
                              ((t4239042935) + (((double)(t4239142936))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4239242938 =
                              this42052.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4239342939 =
                              b42053.
                                j;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4239742940 =
                              ((t4239242938) + (((double)(t4239342939))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4239442941 =
                              this42052.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4239542942 =
                              b42053.
                                k;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4239842943 =
                              ((t4239442941) + (((double)(t4239542942))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t4239942944 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425242054.$init(t4239642937,
                                                                                                                                                  t4239742940,
                                                                                                                                                  t4239842943,
                                                                                                                                                  t4239942944);
                            
//#line 216 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d p1 =
                              alloc2425242054;
                            
//#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42400 =
                              dipole.
                                i;
                            
//#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42401 =
                              (-(t42400));
                            
//#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42402 =
                              size;
                            
//#line 217 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double q1 =
                              ((t42401) / (((double)(t42402))));
                            
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42403 =
                              lowestLevelDim;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i042055 =
                              ((t42403) - (((int)(1))));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc3047542058 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc3047542058.$init(((int)(i042055)),
                                                                                                                                                  ((int)(0)),
                                                                                                                                                  ((int)(0)));
                            
//#line 218 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.addAtomToLowestLevelBoxAsync(((x10.array.Point)(alloc3047542058)),
                                                                                                                                                                ((x10x.vector.Point3d)(p1)),
                                                                                                                                                                (double)(q1));
                            
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d alloc24682 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4240442945 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24682.$init(((x10x.vector.Tuple3d)(p1)),
                                                                                                                                               t4240442945);
                            
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d this42060 =
                              alloc24682;
                            
//#line 72 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double that42059 =
                              q1;
                            
//#line 81 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double c42061 =
                              that42059;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550942062 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4240542946 =
                              this42060.
                                i;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4240842947 =
                              ((t4240542946) * (((double)(c42061))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4240642948 =
                              this42060.
                                j;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4240942949 =
                              ((t4240642948) * (((double)(c42061))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4240742950 =
                              this42060.
                                k;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4241042951 =
                              ((t4240742950) * (((double)(c42061))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4241142952 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550942062.$init(t4240842947,
                                                                                                                                                   t4240942949,
                                                                                                                                                   t4241042951,
                                                                                                                                                   t4241142952);
                            
//#line 33 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d that42063 =
                              ((x10x.vector.Vector3d)(alloc2550942062));
                            
//#line 37 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b42064 =
                              ((x10x.vector.Vector3d)(that42063));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550642065 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4241242953 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4241342954 =
                              t4241242953.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4241442955 =
                              b42064.
                                i;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4242142956 =
                              ((t4241342954) + (((double)(t4241442955))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4241542957 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4241642958 =
                              t4241542957.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4241742959 =
                              b42064.
                                j;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4242242960 =
                              ((t4241642958) + (((double)(t4241742959))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4241842961 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4241942962 =
                              t4241842961.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4242042963 =
                              b42064.
                                k;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4242342964 =
                              ((t4241942962) + (((double)(t4242042963))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4242442965 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550642065.$init(t4242142956,
                                                                                                                                                   t4242242960,
                                                                                                                                                   t4242342964,
                                                                                                                                                   t4242442965);
                            
//#line 219 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
newDipole = alloc2550642065;
                            
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d alloc24683 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4242542966 =
                              size;
                            
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4242642967 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24683.$init(((double)(0.0)),
                                                                                                                                               ((double)(t4242542966)),
                                                                                                                                               ((double)(0.0)),
                                                                                                                                               t4242642967);
                            
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this42067 =
                              alloc24683;
                            
//#line 31 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d that42066 =
                              ((x10x.vector.Vector3d)(offset));
                            
//#line 27 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d b42068 =
                              ((x10x.vector.Vector3d)(that42066));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc2425242069 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4242742968 =
                              this42067.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4242842969 =
                              b42068.
                                i;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4243342970 =
                              ((t4242742968) + (((double)(t4242842969))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4242942971 =
                              this42067.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4243042972 =
                              b42068.
                                j;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4243442973 =
                              ((t4242942971) + (((double)(t4243042972))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4243142974 =
                              this42067.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4243242975 =
                              b42068.
                                k;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4243542976 =
                              ((t4243142974) + (((double)(t4243242975))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t4243642977 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425242069.$init(t4243342970,
                                                                                                                                                  t4243442973,
                                                                                                                                                  t4243542976,
                                                                                                                                                  t4243642977);
                            
//#line 221 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d p2 =
                              alloc2425242069;
                            
//#line 222 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42437 =
                              dipole.
                                j;
                            
//#line 222 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42438 =
                              (-(t42437));
                            
//#line 222 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42439 =
                              size;
                            
//#line 222 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double q2 =
                              ((t42438) / (((double)(t42439))));
                            
//#line 223 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42440 =
                              lowestLevelDim;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i142071 =
                              ((t42440) - (((int)(1))));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc3047542073 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc3047542073.$init(((int)(0)),
                                                                                                                                                  ((int)(i142071)),
                                                                                                                                                  ((int)(0)));
                            
//#line 223 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.addAtomToLowestLevelBoxAsync(((x10.array.Point)(alloc3047542073)),
                                                                                                                                                                ((x10x.vector.Point3d)(p2)),
                                                                                                                                                                (double)(q2));
                            
//#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d alloc24684 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4244142978 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24684.$init(((x10x.vector.Tuple3d)(p2)),
                                                                                                                                               t4244142978);
                            
//#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d this42075 =
                              alloc24684;
                            
//#line 72 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double that42074 =
                              q2;
                            
//#line 81 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double c42076 =
                              that42074;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550942077 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4244242979 =
                              this42075.
                                i;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4244542980 =
                              ((t4244242979) * (((double)(c42076))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4244342981 =
                              this42075.
                                j;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4244642982 =
                              ((t4244342981) * (((double)(c42076))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4244442983 =
                              this42075.
                                k;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4244742984 =
                              ((t4244442983) * (((double)(c42076))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4244842985 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550942077.$init(t4244542980,
                                                                                                                                                   t4244642982,
                                                                                                                                                   t4244742984,
                                                                                                                                                   t4244842985);
                            
//#line 33 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d that42078 =
                              ((x10x.vector.Vector3d)(alloc2550942077));
                            
//#line 37 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b42079 =
                              ((x10x.vector.Vector3d)(that42078));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550642080 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4244942986 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245042987 =
                              t4244942986.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245142988 =
                              b42079.
                                i;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245842989 =
                              ((t4245042987) + (((double)(t4245142988))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4245242990 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245342991 =
                              t4245242990.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245442992 =
                              b42079.
                                j;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245942993 =
                              ((t4245342991) + (((double)(t4245442992))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4245542994 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245642995 =
                              t4245542994.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4245742996 =
                              b42079.
                                k;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4246042997 =
                              ((t4245642995) + (((double)(t4245742996))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4246142998 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550642080.$init(t4245842989,
                                                                                                                                                   t4245942993,
                                                                                                                                                   t4246042997,
                                                                                                                                                   t4246142998);
                            
//#line 224 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
newDipole = alloc2550642080;
                            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d alloc24685 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4246242999 =
                              size;
                            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4246343000 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24685.$init(((double)(0.0)),
                                                                                                                                               ((double)(0.0)),
                                                                                                                                               ((double)(t4246242999)),
                                                                                                                                               t4246343000);
                            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this42082 =
                              alloc24685;
                            
//#line 31 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d that42081 =
                              ((x10x.vector.Vector3d)(offset));
                            
//#line 27 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d b42083 =
                              ((x10x.vector.Vector3d)(that42081));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc2425242084 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4246443001 =
                              this42082.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4246543002 =
                              b42083.
                                i;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4247043003 =
                              ((t4246443001) + (((double)(t4246543002))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4246643004 =
                              this42082.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4246743005 =
                              b42083.
                                j;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4247143006 =
                              ((t4246643004) + (((double)(t4246743005))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4246843007 =
                              this42082.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4246943008 =
                              b42083.
                                k;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4247243009 =
                              ((t4246843007) + (((double)(t4246943008))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t4247343010 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425242084.$init(t4247043003,
                                                                                                                                                  t4247143006,
                                                                                                                                                  t4247243009,
                                                                                                                                                  t4247343010);
                            
//#line 227 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d p3 =
                              alloc2425242084;
                            
//#line 228 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42474 =
                              dipole.
                                k;
                            
//#line 228 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42475 =
                              (-(t42474));
                            
//#line 228 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42476 =
                              size;
                            
//#line 228 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double q3 =
                              ((t42475) / (((double)(t42476))));
                            
//#line 229 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42477 =
                              lowestLevelDim;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i242087 =
                              ((t42477) - (((int)(1))));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc3047542088 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc3047542088.$init(((int)(0)),
                                                                                                                                                  ((int)(0)),
                                                                                                                                                  ((int)(i242087)));
                            
//#line 229 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.addAtomToLowestLevelBoxAsync(((x10.array.Point)(alloc3047542088)),
                                                                                                                                                                ((x10x.vector.Point3d)(p3)),
                                                                                                                                                                (double)(q3));
                            
//#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d alloc24686 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4247843011 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24686.$init(((x10x.vector.Tuple3d)(p3)),
                                                                                                                                               t4247843011);
                            
//#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d this42090 =
                              alloc24686;
                            
//#line 72 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double that42089 =
                              q3;
                            
//#line 81 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double c42091 =
                              that42089;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550942092 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4247943012 =
                              this42090.
                                i;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4248243013 =
                              ((t4247943012) * (((double)(c42091))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4248043014 =
                              this42090.
                                j;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4248343015 =
                              ((t4248043014) * (((double)(c42091))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4248143016 =
                              this42090.
                                k;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4248443017 =
                              ((t4248143016) * (((double)(c42091))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4248543018 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550942092.$init(t4248243013,
                                                                                                                                                   t4248343015,
                                                                                                                                                   t4248443017,
                                                                                                                                                   t4248543018);
                            
//#line 33 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d that42093 =
                              ((x10x.vector.Vector3d)(alloc2550942092));
                            
//#line 37 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b42094 =
                              ((x10x.vector.Vector3d)(that42093));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550642095 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4248643019 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4248743020 =
                              t4248643019.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4248843021 =
                              b42094.
                                i;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4249543022 =
                              ((t4248743020) + (((double)(t4248843021))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4248943023 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4249043024 =
                              t4248943023.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4249143025 =
                              b42094.
                                j;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4249643026 =
                              ((t4249043024) + (((double)(t4249143025))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4249243027 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4249343028 =
                              t4249243027.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4249443029 =
                              b42094.
                                k;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4249743030 =
                              ((t4249343028) + (((double)(t4249443029))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4249843031 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550642095.$init(t4249543022,
                                                                                                                                                   t4249643026,
                                                                                                                                                   t4249743030,
                                                                                                                                                   t4249843031);
                            
//#line 230 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
newDipole = alloc2550642095;
                            
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d alloc24687 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4249943032 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24687.$init(((double)(0.0)),
                                                                                                                                               ((double)(0.0)),
                                                                                                                                               ((double)(0.0)),
                                                                                                                                               t4249943032);
                            
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this42097 =
                              alloc24687;
                            
//#line 31 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d that42096 =
                              ((x10x.vector.Vector3d)(offset));
                            
//#line 27 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d b42098 =
                              ((x10x.vector.Vector3d)(that42096));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc2425242099 =
                              new x10x.vector.Point3d((java.lang.System[]) null);
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4250043033 =
                              this42097.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4250143034 =
                              b42098.
                                i;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4250643035 =
                              ((t4250043033) + (((double)(t4250143034))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4250243036 =
                              this42097.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4250343037 =
                              b42098.
                                j;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4250743038 =
                              ((t4250243036) + (((double)(t4250343037))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4250443039 =
                              this42097.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4250543040 =
                              b42098.
                                k;
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4250843041 =
                              ((t4250443039) + (((double)(t4250543040))));
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t4250943042 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425242099.$init(t4250643035,
                                                                                                                                                  t4250743038,
                                                                                                                                                  t4250843041,
                                                                                                                                                  t4250943042);
                            
//#line 233 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d p0 =
                              alloc2425242099;
                            
//#line 234 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42510 =
                              ((q1) + (((double)(q2))));
                            
//#line 234 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42511 =
                              ((t42510) + (((double)(q3))));
                            
//#line 234 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double q0 =
                              (-(t42511));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc3047542103 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc3047542103.$init(((int)(0)),
                                                                                                                                                  ((int)(0)),
                                                                                                                                                  ((int)(0)));
                            
//#line 235 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.addAtomToLowestLevelBoxAsync(((x10.array.Point)(alloc3047542103)),
                                                                                                                                                                ((x10x.vector.Point3d)(p0)),
                                                                                                                                                                (double)(q0));
                            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d alloc24688 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4251243043 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24688.$init(((x10x.vector.Tuple3d)(p0)),
                                                                                                                                               t4251243043);
                            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d this42105 =
                              alloc24688;
                            
//#line 72 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double that42104 =
                              q0;
                            
//#line 81 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double c42106 =
                              that42104;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550942107 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4251343044 =
                              this42105.
                                i;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4251643045 =
                              ((t4251343044) * (((double)(c42106))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4251443046 =
                              this42105.
                                j;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4251743047 =
                              ((t4251443046) * (((double)(c42106))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4251543048 =
                              this42105.
                                k;
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4251843049 =
                              ((t4251543048) * (((double)(c42106))));
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4251943050 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550942107.$init(t4251643045,
                                                                                                                                                   t4251743047,
                                                                                                                                                   t4251843049,
                                                                                                                                                   t4251943050);
                            
//#line 33 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d that42108 =
                              ((x10x.vector.Vector3d)(alloc2550942107));
                            
//#line 37 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b42109 =
                              ((x10x.vector.Vector3d)(that42108));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550642110 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4252043051 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4252143052 =
                              t4252043051.
                                i;
                            
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4252243053 =
                              b42109.
                                i;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4252943054 =
                              ((t4252143052) + (((double)(t4252243053))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4252343055 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4252443056 =
                              t4252343055.
                                j;
                            
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4252543057 =
                              b42109.
                                j;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4253043058 =
                              ((t4252443056) + (((double)(t4252543057))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4252643059 =
                              newDipole;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4252743060 =
                              t4252643059.
                                k;
                            
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4252843061 =
                              b42109.
                                k;
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4253143062 =
                              ((t4252743060) + (((double)(t4252843061))));
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4253243063 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550642110.$init(t4252943054,
                                                                                                                                                   t4253043058,
                                                                                                                                                   t4253143062,
                                                                                                                                                   t4253243063);
                            
//#line 236 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
newDipole = alloc2550642110;
                        }
                    }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__12__) {
                        
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__12__)));
                        
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
throw new x10.lang.RuntimeException();
                    }finally {{
                         
//#line 215 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var6)));
                     }}
                    }
                
//#line 246 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d t42533 =
                  newDipole;
                
//#line 246 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return t42533;
                }
            
            
//#line 255 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public double
                                                                                                                getDirectEnergy$O(
                                                                                                                ){
                
//#line 257 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42534 =
                  ((au.edu.anu.util.Timer)(timer));
                
//#line 257 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42534.start((int)(2));
                
//#line 259 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree> locallyEssentialTrees =
                  ((x10.array.DistArray)(this.
                                           locallyEssentialTrees));
                
//#line 260 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes =
                  ((x10.array.DistArray)(this.
                                           lowestLevelBoxes));
                
//#line 261 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int lowestLevelDim =
                  this.
                    lowestLevelDim;
                
//#line 262 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double size =
                  this.
                    size;
                
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double directEnergy;
                {
                    
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.FinishState x10$__var7 =
                      ((x10.lang.FinishState)(x10.lang.Runtime.<x10.core.Double>startCollectingFinish_0_$_x10$lang$Runtime_T_$(x10.rtt.Types.DOUBLE, ((x10.lang.Reducible)((new java.io.Serializable() { au.edu.anu.mm.Fmm3d.SumReducer eval() {
                                                                                                                                   
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.Fmm3d.SumReducer alloc24689 =
                                                                                                                                     new au.edu.anu.mm.Fmm3d.SumReducer((java.lang.System[]) null);
                                                                                                                                   {
                                                                                                                                       
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t42536 =
                                                                                                                                         x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                       
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24689.$init(t42536);
                                                                                                                                   }
                                                                                                                                   return alloc24689;
                                                                                                                               } }.eval()))))));
                    
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
try {try {{
                        {
                            {
                                
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.ensureNotInAtomic();
                                
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Dist __lowerer__var__13__ =
                                  ((x10.array.Dist)(((x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree>)locallyEssentialTrees).
                                                      dist));
                                
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.lang.Place> __lowerer__var__14__43302 =
                                                                                                                                         ((x10.lang.Iterable<x10.lang.Place>)__lowerer__var__13__.places()).iterator();
                                                                                                                                       ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__14__43302).hasNext$O();
                                                                                                                                       ) {
                                    
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Place __lowerer__var__14__ =
                                      ((x10.lang.Iterator<x10.lang.Place>)__lowerer__var__14__43302).next$G();
                                    
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(__lowerer__var__14__)),
                                                                                                                                                                ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$25(__lowerer__var__13__,
                                                                                                                                                                                                                                        locallyEssentialTrees,
                                                                                                                                                                                                                                        lowestLevelBoxes,
                                                                                                                                                                                                                                        lowestLevelDim,
                                                                                                                                                                                                                                        size,(java.lang.Class<?>) null))));
                                }
                            }
                        }
                    }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__15__) {
                        
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__15__)));
                        
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
throw new x10.lang.RuntimeException();
                    }finally {{
                         
//#line 263 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
directEnergy = x10.core.Double.$unbox(x10.lang.Runtime.<x10.core.Double>stopCollectingFinish$G(x10.rtt.Types.DOUBLE, ((x10.lang.FinishState)(x10$__var7))));
                     }}
                    }
                
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.util.Timer t42643 =
                  ((au.edu.anu.util.Timer)(timer));
                
//#line 310 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42643.stop((int)(2));
                
//#line 312 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return directEnergy;
                }
            
            
//#line 319 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
private static x10x.vector.Vector3d
                                                                                                                getTranslation(
                                                                                                                final int lowestLevelDim,
                                                                                                                final double size,
                                                                                                                final int x,
                                                                                                                final int y,
                                                                                                                final int z){
                
//#line 320 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
double translationX =
                  0.0;
                
//#line 321 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42647 =
                  ((x) >= (((int)(lowestLevelDim))));
                
//#line 321 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42647) {
                    
//#line 322 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationX = size;
                } else {
                    
//#line 323 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42646 =
                      ((x) < (((int)(0))));
                    
//#line 323 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42646) {
                        
//#line 324 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42645 =
                          (-(size));
                        
//#line 324 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationX = t42645;
                    }
                }
                
//#line 327 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
double translationY =
                  ((double)(int)(((int)(0))));
                
//#line 328 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42650 =
                  ((y) >= (((int)(lowestLevelDim))));
                
//#line 328 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42650) {
                    
//#line 329 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationY = size;
                } else {
                    
//#line 330 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42649 =
                      ((y) < (((int)(0))));
                    
//#line 330 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42649) {
                        
//#line 331 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42648 =
                          (-(size));
                        
//#line 331 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationY = t42648;
                    }
                }
                
//#line 334 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
double translationZ =
                  ((double)(int)(((int)(0))));
                
//#line 335 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42653 =
                  ((z) >= (((int)(lowestLevelDim))));
                
//#line 335 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42653) {
                    
//#line 336 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationZ = size;
                } else {
                    
//#line 337 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42652 =
                      ((z) < (((int)(0))));
                    
//#line 337 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42652) {
                        
//#line 338 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42651 =
                          (-(size));
                        
//#line 338 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationZ = t42651;
                    }
                }
                
//#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d alloc24690 =
                  new x10x.vector.Vector3d((java.lang.System[]) null);
                
//#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4265443234 =
                  translationX;
                
//#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4265543235 =
                  translationY;
                
//#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4265643236 =
                  translationZ;
                
//#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4265743237 =
                  x10.util.concurrent.OrderedLock.createNewLock();
                
//#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24690.$init(t4265443234,
                                                                                                                                   t4265543235,
                                                                                                                                   t4265643236,
                                                                                                                                   t4265743237);
                
//#line 340 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return alloc24690;
            }
            
            public static x10x.vector.Vector3d
              getTranslation$P(
              final int lowestLevelDim,
              final double size,
              final int x,
              final int y,
              final int z){
                return au.edu.anu.mm.PeriodicFmm3d.getTranslation((int)(lowestLevelDim),
                                                                  (double)(size),
                                                                  (int)(x),
                                                                  (int)(y),
                                                                  (int)(z));
            }
            
            
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public static class VectorSumReducer
                                                                                                              extends x10.core.Struct
                                                                                                                implements x10.lang.Reducible,
                                                                                                                           x10.util.concurrent.Atomic,
                                                                                                                            x10.x10rt.X10JavaSerializable 
                                                                                                              {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, VectorSumReducer.class);
                
                public static final x10.rtt.RuntimeType<VectorSumReducer> $RTT = new x10.rtt.NamedType<VectorSumReducer>(
                "au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer", /* base class */VectorSumReducer.class
                , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.lang.Reducible.$RTT, x10x.vector.Vector3d.$RTT), x10.rtt.Types.STRUCT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body(VectorSumReducer $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    VectorSumReducer $_obj = new VectorSumReducer((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    
                }
                
                // zero value constructor
                public VectorSumReducer(final java.lang.System $dummy) { }
                // constructor just for allocation
                public VectorSumReducer(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                // dispatcher for method abstract public x10.lang.Reducible.operator()(T,T):T
                public java.lang.Object
                  $apply(final java.lang.Object a1,final x10.rtt.Type t1,
                final java.lang.Object a2,final x10.rtt.Type t2){return $apply((x10x.vector.Vector3d)a1,
                (x10x.vector.Vector3d)a2);}
                // bridge for method abstract public x10.lang.Reducible.zero():T
                final public x10x.vector.Vector3d
                  zero$G(){return zero();}
                
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public int
                      X10$object_lock_id0;
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                        getOrderedLock(
                                                                                                                        ){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42658 =
                          this.
                            X10$object_lock_id0;
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t42659 =
                          x10.util.concurrent.OrderedLock.getLock((int)(t42658));
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return t42659;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public static int
                      X10$class_lock_id1 =
                      x10.util.concurrent.OrderedLock.createNewLockID();
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                        getStaticOrderedLock(
                                                                                                                        ){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42660 =
                          au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer.X10$class_lock_id1;
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t42661 =
                          x10.util.concurrent.OrderedLock.getLock((int)(t42660));
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return t42661;
                    }
                    
                    
//#line 344 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public x10x.vector.Vector3d
                                                                                                                        zero(
                                                                                                                        ){
                        
//#line 344 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d t42662 =
                          ((x10x.vector.Vector3d)(x10x.vector.Vector3d.getInitialized$NULL()));
                        
//#line 344 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return t42662;
                    }
                    
                    
//#line 345 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public x10x.vector.Vector3d
                                                                                                                        $apply(
                                                                                                                        final x10x.vector.Vector3d a,
                                                                                                                        final x10x.vector.Vector3d b){
                        
//#line 33 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d that42155 =
                          ((x10x.vector.Vector3d)(b));
                        
//#line 37 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b42156 =
                          ((x10x.vector.Vector3d)(that42155));
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc2550642157 =
                          new x10x.vector.Vector3d((java.lang.System[]) null);
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4266343238 =
                          a.
                            i;
                        
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4266443239 =
                          b42156.
                            i;
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4266943240 =
                          ((t4266343238) + (((double)(t4266443239))));
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4266543241 =
                          a.
                            j;
                        
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4266643242 =
                          b42156.
                            j;
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4267043243 =
                          ((t4266543241) + (((double)(t4266643242))));
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4266743244 =
                          a.
                            k;
                        
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4266843245 =
                          b42156.
                            k;
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4267143246 =
                          ((t4266743244) + (((double)(t4266843245))));
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4267243247 =
                          x10.util.concurrent.OrderedLock.createNewLock();
                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc2550642157.$init(t4266943240,
                                                                                                                                               t4267043243,
                                                                                                                                               t4267143246,
                                                                                                                                               t4267243247);
                        
//#line 345 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return alloc2550642157;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public java.lang.String
                                                                                                                        typeName$O(
                                                                                                                        ){try {return x10.rtt.Types.typeName(this);}catch (java.lang.Throwable $exc$) { throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$); } }
                    
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public java.lang.String
                                                                                                                        toString(
                                                                                                                        ){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return "struct au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer";
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public int
                                                                                                                        hashCode(
                                                                                                                        ){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int result =
                          1;
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42673 =
                          result;
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return t42673;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public boolean
                                                                                                                        equals(
                                                                                                                        java.lang.Object other){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final java.lang.Object t42674 =
                          other;
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42675 =
                          au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer.$RTT.instanceOf(t42674);
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42676 =
                          !(t42675);
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42676) {
                            
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return false;
                        }
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return true;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public boolean
                                                                                                                        equals(
                                                                                                                        au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer other){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return true;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public boolean
                                                                                                                        _struct_equals$O(
                                                                                                                        java.lang.Object other){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final java.lang.Object t42677 =
                          other;
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42678 =
                          au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer.$RTT.instanceOf(t42677);
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42679 =
                          !(t42678);
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42679) {
                            
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return false;
                        }
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return true;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public boolean
                                                                                                                        _struct_equals$O(
                                                                                                                        au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer other){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return true;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer
                                                                                                                        au$edu$anu$mm$PeriodicFmm3d$VectorSumReducer$$au$edu$anu$mm$PeriodicFmm3d$VectorSumReducer$this(
                                                                                                                        ){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer.this;
                    }
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
// creation method for java code
                    public static au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer $make(){return new au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer((java.lang.System[]) null).$init();}
                    
                    // constructor for non-virtual call
                    final public au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer au$edu$anu$mm$PeriodicFmm3d$VectorSumReducer$$init$S() { {
                                                                                                                                              
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"

                                                                                                                                              
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer this4216243248 =
                                                                                                                                                this;
                                                                                                                                              
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this4216243248.X10$object_lock_id0 = -1;
                                                                                                                                          }
                                                                                                                                          return this;
                                                                                                                                          }
                    
                    // constructor
                    public au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer $init(){return au$edu$anu$mm$PeriodicFmm3d$VectorSumReducer$$init$S();}
                    
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
// creation method for java code
                    public static au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer $make(final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer((java.lang.System[]) null).$init(paramLock);}
                    
                    // constructor for non-virtual call
                    final public au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer au$edu$anu$mm$PeriodicFmm3d$VectorSumReducer$$init$S(final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                                             
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"

                                                                                                                                                                                             
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer this4216543249 =
                                                                                                                                                                                               this;
                                                                                                                                                                                             
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this4216543249.X10$object_lock_id0 = -1;
                                                                                                                                                                                             
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42680 =
                                                                                                                                                                                               paramLock.getIndex();
                                                                                                                                                                                             
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.X10$object_lock_id0 = ((int)(t42680));
                                                                                                                                                                                         }
                                                                                                                                                                                         return this;
                                                                                                                                                                                         }
                    
                    // constructor
                    public au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer $init(final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$PeriodicFmm3d$VectorSumReducer$$init$S(paramLock);}
                    
                    
                    
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final private void
                                                                                                                        __fieldInitializers24259(
                                                                                                                        ){
                        
//#line 343 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.X10$object_lock_id0 = -1;
                    }
                    
                    final public static void
                      __fieldInitializers24259$P(
                      final au.edu.anu.mm.PeriodicFmm3d.VectorSumReducer VectorSumReducer){
                        VectorSumReducer.__fieldInitializers24259();
                    }
                
            }
            
            
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final public au.edu.anu.mm.PeriodicFmm3d
                                                                                                               au$edu$anu$mm$PeriodicFmm3d$$au$edu$anu$mm$PeriodicFmm3d$this(
                                                                                                               ){
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
return au.edu.anu.mm.PeriodicFmm3d.this;
            }
            
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final private void
                                                                                                               __fieldInitializers24260(
                                                                                                               ){
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.X10$object_lock_id0 = -1;
            }
            
            final public static void
              __fieldInitializers24260$P(
              final au.edu.anu.mm.PeriodicFmm3d PeriodicFmm3d){
                PeriodicFmm3d.__fieldInitializers24260();
            }
            
            public static int
              fieldId$nineCube;
            final public static x10.core.concurrent.AtomicInteger
              initStatus$nineCube =
              new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
            public static int
              fieldId$threeCube;
            final public static x10.core.concurrent.AtomicInteger
              initStatus$threeCube =
              new x10.core.concurrent.AtomicInteger(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED);
            
            public static int
              getInitialized$TIMER_INDEX_MACROSCOPIC(
              ){
                return au.edu.anu.mm.PeriodicFmm3d.TIMER_INDEX_MACROSCOPIC;
            }
            
            public static void
              getDeserialized$threeCube(
              byte[] buf){
                if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                      ((boolean) true)) {
                    au.edu.anu.mm.PeriodicFmm3d.threeCube = ((x10.array.Region)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
                } else {
                    au.edu.anu.mm.PeriodicFmm3d.threeCube = ((x10.array.Region)(x10.rtt.Types.<x10.array.Region> cast(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf))),x10.array.Region.$RTT)));
                }
                au.edu.anu.mm.PeriodicFmm3d.initStatus$threeCube.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            }
            
            public static x10.array.Region
              getInitialized$threeCube(
              ){
                if (((int) x10.lang.Runtime.hereInt$O()) ==
                    ((int) 0)) {
                    if (au.edu.anu.mm.PeriodicFmm3d.initStatus$threeCube.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                       (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        au.edu.anu.mm.PeriodicFmm3d.threeCube = ((x10.array.Region)(x10.lang.IntRange.$make(((int)(-1)), ((int)(1))).$times(((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(-1)), ((int)(1)))))).$times(((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(-1)), ((int)(1)))))))))));
                        if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.mm.PeriodicFmm3d.threeCube)),
                                                                                      (int)(au.edu.anu.mm.PeriodicFmm3d.fieldId$threeCube));
                        } else {
                            x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.mm.PeriodicFmm3d.threeCube)),
                                                                                      (int)(au.edu.anu.mm.PeriodicFmm3d.fieldId$threeCube));
                        }
                        au.edu.anu.mm.PeriodicFmm3d.initStatus$threeCube.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    }
                }
                if (((int) au.edu.anu.mm.PeriodicFmm3d.initStatus$threeCube.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) au.edu.anu.mm.PeriodicFmm3d.initStatus$threeCube.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
                return au.edu.anu.mm.PeriodicFmm3d.threeCube;
            }
            
            public static void
              getDeserialized$nineCube(
              byte[] buf){
                if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                      ((boolean) true)) {
                    au.edu.anu.mm.PeriodicFmm3d.nineCube = ((x10.array.Region)(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf)))));
                } else {
                    au.edu.anu.mm.PeriodicFmm3d.nineCube = ((x10.array.Region)(x10.rtt.Types.<x10.array.Region> cast(x10.runtime.impl.java.InitDispatcher.deserializeField(((byte[])(buf))),x10.array.Region.$RTT)));
                }
                au.edu.anu.mm.PeriodicFmm3d.initStatus$nineCube.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                x10.runtime.impl.java.InitDispatcher.lockInitialized();
                x10.runtime.impl.java.InitDispatcher.notifyInitialized();
            }
            
            public static x10.array.Region
              getInitialized$nineCube(
              ){
                if (((int) x10.lang.Runtime.hereInt$O()) ==
                    ((int) 0)) {
                    if (au.edu.anu.mm.PeriodicFmm3d.initStatus$nineCube.compareAndSet((int)(x10.runtime.impl.java.InitDispatcher.UNINITIALIZED),
                                                                                      (int)(x10.runtime.impl.java.InitDispatcher.INITIALIZING))) {
                        au.edu.anu.mm.PeriodicFmm3d.nineCube = ((x10.array.Region)(x10.lang.IntRange.$make(((int)(-4)), ((int)(4))).$times(((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(-4)), ((int)(4)))))).$times(((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(-4)), ((int)(4)))))))))));
                        if (((boolean) x10.x10rt.X10JavaSerializable.CUSTOM_JAVA_SERIALIZATION) ==
                              ((boolean) true)) {
                            x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.mm.PeriodicFmm3d.nineCube)),
                                                                                      (int)(au.edu.anu.mm.PeriodicFmm3d.fieldId$nineCube));
                        } else {
                            x10.runtime.impl.java.InitDispatcher.broadcastStaticField(((java.lang.Object)(au.edu.anu.mm.PeriodicFmm3d.nineCube)),
                                                                                      (int)(au.edu.anu.mm.PeriodicFmm3d.fieldId$nineCube));
                        }
                        au.edu.anu.mm.PeriodicFmm3d.initStatus$nineCube.set((int)(x10.runtime.impl.java.InitDispatcher.INITIALIZED));
                        x10.runtime.impl.java.InitDispatcher.lockInitialized();
                        x10.runtime.impl.java.InitDispatcher.notifyInitialized();
                    }
                }
                if (((int) au.edu.anu.mm.PeriodicFmm3d.initStatus$nineCube.get()) !=
                    ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                    x10.runtime.impl.java.InitDispatcher.lockInitialized();
                    while (((int) au.edu.anu.mm.PeriodicFmm3d.initStatus$nineCube.get()) !=
                           ((int) x10.runtime.impl.java.InitDispatcher.INITIALIZED)) {
                        x10.runtime.impl.java.InitDispatcher.awaitInitialized();
                    }
                    x10.runtime.impl.java.InitDispatcher.unlockInitialized();
                }
                return au.edu.anu.mm.PeriodicFmm3d.nineCube;
            }
            
            static {
                       au.edu.anu.mm.PeriodicFmm3d.fieldId$threeCube = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.mm.PeriodicFmm3d")),
                                                                                                                           ((java.lang.String)("threeCube")));
                       au.edu.anu.mm.PeriodicFmm3d.fieldId$nineCube = x10.runtime.impl.java.InitDispatcher.addInitializer(((java.lang.String)("au.edu.anu.mm.PeriodicFmm3d")),
                                                                                                                          ((java.lang.String)("nineCube")));
                   }
            
            public static class $Closure$14
            extends x10.core.Ref
              implements x10.core.fun.VoidFun_0_0,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$14.class);
                
                public static final x10.rtt.RuntimeType<$Closure$14> $RTT = new x10.rtt.StaticVoidFunType<$Closure$14>(
                /* base class */$Closure$14.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$14 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    au.edu.anu.mm.PeriodicFmm3d out$$ = (au.edu.anu.mm.PeriodicFmm3d) $deserializer.readRef();
                    $_obj.out$$ = out$$;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$14 $_obj = new $Closure$14((java.lang.System[]) null);
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
                public $Closure$14(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
this.
                                                                                                                           out$$.prefetchRemoteAtoms();
                    }
                    
                    public au.edu.anu.mm.PeriodicFmm3d
                      out$$;
                    
                    // creation method for java code
                    public static au.edu.anu.mm.PeriodicFmm3d.$Closure$14 $make(final au.edu.anu.mm.PeriodicFmm3d out$$){return new $Closure$14(out$$);}
                    public $Closure$14(final au.edu.anu.mm.PeriodicFmm3d out$$) { {
                                                                                         this.out$$ = out$$;
                                                                                     }}
                    
                }
                
            public static class $Closure$15
            extends x10.core.Ref
              implements x10.core.fun.VoidFun_0_0,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$15.class);
                
                public static final x10.rtt.RuntimeType<$Closure$15> $RTT = new x10.rtt.StaticVoidFunType<$Closure$15>(
                /* base class */$Closure$15.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$15 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $_obj.numShells = $deserializer.readInt();
                    x10.array.Array boxes = (x10.array.Array) $deserializer.readRef();
                    $_obj.boxes = boxes;
                    $_obj.numTerms = $deserializer.readInt();
                    $_obj.size = $deserializer.readDouble();
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$15 $_obj = new $Closure$15((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    $serializer.write(this.numShells);
                    if (boxes instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.boxes);
                    } else {
                    $serializer.write(this.boxes);
                    }
                    $serializer.write(this.numTerms);
                    $serializer.write(this.size);
                    
                }
                
                // constructor just for allocation
                public $Closure$15(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Array<au.edu.anu.mm.MultipoleExpansion> macroMultipoles =
                          ((x10.array.Array)(new x10.array.Array<au.edu.anu.mm.MultipoleExpansion>((java.lang.System[]) null, au.edu.anu.mm.MultipoleExpansion.$RTT)));
                        
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size38942 =
                          ((this.
                              numShells) + (((int)(1))));
                        
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.x10$lang$Object$$init$S();
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703894542822 =
                          ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t4218542683 =
                          ((size38942) - (((int)(1))));
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703894542822.$init(((int)(0)),
                                                                                                                                                   t4218542683);
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__389443894942823 =
                          ((x10.array.Region)(((x10.array.Region)
                                                alloc199703894542822)));
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3895042824 =
                           null;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t4218642684 =
                          __desugarer__var__5__389443894942823.
                            rank;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4218842685 =
                          ((int) t4218642684) ==
                        ((int) 1);
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4218842685) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4218742686 =
                              __desugarer__var__5__389443894942823.
                                zeroBased;
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4218842685 = ((boolean) t4218742686) ==
                            ((boolean) true);
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4219042687 =
                          t4218842685;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4219042687) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4218942688 =
                              __desugarer__var__5__389443894942823.
                                rect;
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4219042687 = ((boolean) t4218942688) ==
                            ((boolean) true);
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4219242689 =
                          t4219042687;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4219242689) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4219142690 =
                              __desugarer__var__5__389443894942823.
                                rail;
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4219242689 = ((boolean) t4219142690) ==
                            ((boolean) true);
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4219342691 =
                          t4219242689;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4219342691) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4219342691 = ((__desugarer__var__5__389443894942823) != (null));
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4219442692 =
                          t4219342691;
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4219742693 =
                          !(t4219442692);
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4219742693) {
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4219642694 =
                              true;
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4219642694) {
                                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t4219542695 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t4219542695;
                            }
                        }
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3895042824 = ((x10.array.Region)(__desugarer__var__5__389443894942823));
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3894342825 =
                          ((x10.array.Region)(ret3895042824));
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.region = ((x10.array.Region)(myReg3894342825));
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.rank = 1;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.rect = true;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.zeroBased = true;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.rail = true;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.size = size38942;
                        
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713894642826 =
                          new x10.array.RectLayout((java.lang.System[]) null);
                        
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03895342827 =
                          ((size38942) - (((int)(1))));
                        
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.rank = 1;
                        
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.min0 = 0;
                        
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4219842696 =
                          ((_max03895342827) - (((int)(0))));
                        
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4219942697 =
                          ((t4219842696) + (((int)(1))));
                        
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.delta0 = t4219942697;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4220042698 =
                          alloc199713894642826.
                            delta0;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t4220142699 =
                          ((t4220042698) > (((int)(0))));
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t4220242700 =
                           0;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t4220142699) {
                            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t4220242700 = alloc199713894642826.
                                                                                                                                                  delta0;
                        } else {
                            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t4220242700 = 0;
                        }
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4220342701 =
                          t4220242700;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.size = t4220342701;
                        
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.min1 = 0;
                        
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.delta1 = 0;
                        
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.min2 = 0;
                        
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.delta2 = 0;
                        
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.min3 = 0;
                        
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.delta3 = 0;
                        
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.min = null;
                        
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713894642826.delta = null;
                        
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.layout = ((x10.array.RectLayout)(alloc199713894642826));
                        
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3895542828 =
                          ((x10.array.RectLayout)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                    layout));
                        
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3894742829 =
                          this3895542828.
                            size;
                        
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t4220442830 =
                          ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<au.edu.anu.mm.MultipoleExpansion>allocate(au.edu.anu.mm.MultipoleExpansion.$RTT, ((int)(n3894742829)), true)));
                        
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroMultipoles.raw = ((x10.core.IndexedMemoryChunk)(t4220442830));
                        
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Array<au.edu.anu.mm.LocalExpansion> macroLocalTranslations =
                          ((x10.array.Array)(new x10.array.Array<au.edu.anu.mm.LocalExpansion>((java.lang.System[]) null, au.edu.anu.mm.LocalExpansion.$RTT)));
                        
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size38956 =
                          ((this.
                              numShells) + (((int)(1))));
                        
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.x10$lang$Object$$init$S();
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199703895942831 =
                          ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t4220542702 =
                          ((size38956) - (((int)(1))));
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199703895942831.$init(((int)(0)),
                                                                                                                                                   t4220542702);
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__389583896342832 =
                          ((x10.array.Region)(((x10.array.Region)
                                                alloc199703895942831)));
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret3896442833 =
                           null;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t4220642703 =
                          __desugarer__var__5__389583896342832.
                            rank;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4220842704 =
                          ((int) t4220642703) ==
                        ((int) 1);
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4220842704) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4220742705 =
                              __desugarer__var__5__389583896342832.
                                zeroBased;
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4220842704 = ((boolean) t4220742705) ==
                            ((boolean) true);
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4221042706 =
                          t4220842704;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4221042706) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4220942707 =
                              __desugarer__var__5__389583896342832.
                                rect;
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4221042706 = ((boolean) t4220942707) ==
                            ((boolean) true);
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4221242708 =
                          t4221042706;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4221242708) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4221142709 =
                              __desugarer__var__5__389583896342832.
                                rail;
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4221242708 = ((boolean) t4221142709) ==
                            ((boolean) true);
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t4221342710 =
                          t4221242708;
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4221342710) {
                            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t4221342710 = ((__desugarer__var__5__389583896342832) != (null));
                        }
                        
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4221442711 =
                          t4221342710;
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4221742712 =
                          !(t4221442711);
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4221742712) {
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t4221642713 =
                              true;
                            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t4221642713) {
                                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t4221542714 =
                                  new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t4221542714;
                            }
                        }
                        
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret3896442833 = ((x10.array.Region)(__desugarer__var__5__389583896342832));
                        
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg3895742834 =
                          ((x10.array.Region)(ret3896442833));
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.region = ((x10.array.Region)(myReg3895742834));
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.rank = 1;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.rect = true;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.zeroBased = true;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.rail = true;
                        
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.size = size38956;
                        
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199713896042835 =
                          new x10.array.RectLayout((java.lang.System[]) null);
                        
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max03896742836 =
                          ((size38956) - (((int)(1))));
                        
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.rank = 1;
                        
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.min0 = 0;
                        
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4221842715 =
                          ((_max03896742836) - (((int)(0))));
                        
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4221942716 =
                          ((t4221842715) + (((int)(1))));
                        
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.delta0 = t4221942716;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4222042717 =
                          alloc199713896042835.
                            delta0;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t4222142718 =
                          ((t4222042717) > (((int)(0))));
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t4222242719 =
                           0;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t4222142718) {
                            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t4222242719 = alloc199713896042835.
                                                                                                                                                  delta0;
                        } else {
                            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t4222242719 = 0;
                        }
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t4222342720 =
                          t4222242719;
                        
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.size = t4222342720;
                        
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.min1 = 0;
                        
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.delta1 = 0;
                        
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.min2 = 0;
                        
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.delta2 = 0;
                        
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.min3 = 0;
                        
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.delta3 = 0;
                        
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.min = null;
                        
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199713896042835.delta = null;
                        
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.layout = ((x10.array.RectLayout)(alloc199713896042835));
                        
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this3896942837 =
                          ((x10.array.RectLayout)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                    layout));
                        
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n3896142838 =
                          this3896942837.
                            size;
                        
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t4222442839 =
                          ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<au.edu.anu.mm.LocalExpansion>allocate(au.edu.anu.mm.LocalExpansion.$RTT, ((int)(n3896142838)), true)));
                        
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
macroLocalTranslations.raw = ((x10.core.IndexedMemoryChunk)(t4222442839));
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.DistArray<au.edu.anu.mm.FmmBox> ret38971 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret38972: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.DistArray<au.edu.anu.mm.FmmBox>> t42225 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.DistArray<au.edu.anu.mm.FmmBox>>)this.
                                                                                                                        boxes).
                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.DistArray<au.edu.anu.mm.FmmBox> t42226 =
                          ((x10.array.DistArray)(((x10.array.DistArray[])t42225.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret38971 = ((x10.array.DistArray)(t42226));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret38972;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.DistArray<au.edu.anu.mm.FmmBox> t42227 =
                          ((x10.array.DistArray)(ret38971));
                        
//#line 99 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmBox topLevelBox =
                          ((x10.array.DistArray<au.edu.anu.mm.FmmBox>)t42227).$apply$G((int)(0),
                                                                                       (int)(0),
                                                                                       (int)(0));
                        
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion v38979 =
                          ((au.edu.anu.mm.MultipoleExpansion)(topLevelBox.
                                                                multipoleExp));
                        
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret38980 =
                           null;
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t4222842721 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                           raw));
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((au.edu.anu.mm.MultipoleExpansion[])t4222842721.value)[0] = v38979;
                        
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret38980 = ((au.edu.anu.mm.MultipoleExpansion)(v38979));
                        
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
au.edu.anu.mm.MultipoleExpansion macroTranslation =
                          new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null);
                        
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4222942840 =
                          x10.util.concurrent.OrderedLock.createNewLock();
                        
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
macroTranslation.$init(((int)(this.
                                                                                                                                                          numTerms)),
                                                                                                                                                 t4222942840);
                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Region t4223142841 =
                          ((x10.array.Region)(au.edu.anu.mm.PeriodicFmm3d.getInitialized$threeCube()));
                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.array.Point> id2469242842 =
                          t4223142841.iterator();
                        
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                               true;
                                                                                                                               ) {
                            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4224042843 =
                              ((x10.lang.Iterator<x10.array.Point>)id2469242842).hasNext$O();
                            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4224042843)) {
                                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                            }
                            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point id41042729 =
                              ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)id2469242842).next$G()));
                            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i42730 =
                              id41042729.$apply$O((int)(0));
                            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int j42731 =
                              id41042729.$apply$O((int)(1));
                            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int k42732 =
                              id41042729.$apply$O((int)(2));
                            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d translationVector42733 =
                              ((x10x.vector.Vector3d)(new x10x.vector.Vector3d((java.lang.System[]) null)));
                            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4223242722 =
                              ((double)(int)(((int)(i42730))));
                            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4223542723 =
                              ((t4223242722) * (((double)(this.
                                                            size))));
                            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4223342724 =
                              ((double)(int)(((int)(j42731))));
                            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4223642725 =
                              ((t4223342724) * (((double)(this.
                                                            size))));
                            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4223442726 =
                              ((double)(int)(((int)(k42732))));
                            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4223742727 =
                              ((t4223442726) * (((double)(this.
                                                            size))));
                            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4223842728 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationVector42733.$init(t4223542723,
                                                                                                                                                           t4223642725,
                                                                                                                                                           t4223742727,
                                                                                                                                                           t4223842728);
                            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion translation42734 =
                              au.edu.anu.mm.MultipoleExpansion.getOlm(((x10x.vector.Tuple3d)(translationVector42733)),
                                                                      (int)(this.
                                                                              numTerms));
                            
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion t4223942735 =
                              macroTranslation;
                            
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t4223942735.unsafeAdd(((au.edu.anu.mm.Expansion)(translation42734)));
                        }
                        
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion alloc24676 =
                          ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
                        
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4224142844 =
                          x10.util.concurrent.OrderedLock.createNewLock();
                        
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24676.$init(((int)(this.
                                                                                                                                                    numTerms)),
                                                                                                                                           t4224142844);
                        
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion v40105 =
                          ((au.edu.anu.mm.MultipoleExpansion)(alloc24676));
                        
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret40106 =
                           null;
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t4224242736 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                           raw));
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((au.edu.anu.mm.MultipoleExpansion[])t4224242736.value)[1] = v40105;
                        
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret40106 = ((au.edu.anu.mm.MultipoleExpansion)(v40105));
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret40114 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret40115: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t42243 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t42244 =
                          ((au.edu.anu.mm.MultipoleExpansion)(((au.edu.anu.mm.MultipoleExpansion[])t42243.value)[1]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret40114 = t42244;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret40115;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t42247 =
                          ret40114;
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion t42248 =
                          macroTranslation;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret40122 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret40123: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t42245 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t42246 =
                          ((au.edu.anu.mm.MultipoleExpansion)(((au.edu.anu.mm.MultipoleExpansion[])t42245.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret40122 = t42246;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret40123;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t42249 =
                          ret40122;
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42247.translateAndAddMultipole(((au.edu.anu.mm.MultipoleExpansion)(t42248)),
                                                                                                                                                          ((au.edu.anu.mm.MultipoleExpansion)(t42249)));
                        
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.LocalExpansion alloc24677 =
                          ((au.edu.anu.mm.LocalExpansion)(new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null)));
                        
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4225042845 =
                          x10.util.concurrent.OrderedLock.createNewLock();
                        
//#line 117 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc24677.$init(((int)(this.
                                                                                                                                                    numTerms)),
                                                                                                                                           t4225042845);
                        
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion v40975 =
                          ((au.edu.anu.mm.LocalExpansion)(alloc24677));
                        
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.LocalExpansion ret40976 =
                           null;
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t4225142737 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                           raw));
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((au.edu.anu.mm.LocalExpansion[])t4225142737.value)[0] = v40975;
                        
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret40976 = ((au.edu.anu.mm.LocalExpansion)(v40975));
                        
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Region t4225342846 =
                          ((x10.array.Region)(au.edu.anu.mm.PeriodicFmm3d.getInitialized$nineCube()));
                        
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.array.Point> id2471242847 =
                          t4225342846.iterator();
                        
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                               true;
                                                                                                                               ) {
                            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4227642848 =
                              ((x10.lang.Iterator<x10.array.Point>)id2471242847).hasNext$O();
                            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4227642848)) {
                                
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                            }
                            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point id41142745 =
                              ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)id2471242847).next$G()));
                            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i42746 =
                              id41142745.$apply$O((int)(0));
                            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int j42747 =
                              id41142745.$apply$O((int)(1));
                            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int k42748 =
                              id41142745.$apply$O((int)(2));
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a4098342749 =
                              i42746;
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t4225442750 =
                              ((a4098342749) < (((int)(0))));
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t4225542751 =
                               0;
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t4225442750) {
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t4225542751 = (-(a4098342749));
                            } else {
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t4225542751 = a4098342749;
                            }
                            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t4225642752 =
                              t4225542751;
                            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
boolean t4226042753 =
                              ((t4225642752) > (((int)(1))));
                            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4226042753)) {
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a4098442754 =
                                  j42747;
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t4225742755 =
                                  ((a4098442754) < (((int)(0))));
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t4225842756 =
                                   0;
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t4225742755) {
                                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t4225842756 = (-(a4098442754));
                                } else {
                                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t4225842756 = a4098442754;
                                }
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t4225942757 =
                                  t4225842756;
                                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t4226042753 = ((t4225942757) > (((int)(1))));
                            }
                            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
boolean t4226442758 =
                              t4226042753;
                            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4226442758)) {
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a4098542759 =
                                  k42748;
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t4226142760 =
                                  ((a4098542759) < (((int)(0))));
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t4226242761 =
                                   0;
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t4226142760) {
                                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t4226242761 = (-(a4098542759));
                                } else {
                                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t4226242761 = a4098542759;
                                }
                                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t4226342762 =
                                  t4226242761;
                                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t4226442758 = ((t4226342762) > (((int)(1))));
                            }
                            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4227542763 =
                              t4226442758;
                            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t4227542763) {
                                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d translationVector42764 =
                                  ((x10x.vector.Vector3d)(new x10x.vector.Vector3d((java.lang.System[]) null)));
                                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4226542738 =
                                  ((double)(int)(((int)(i42746))));
                                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4226842739 =
                                  ((t4226542738) * (((double)(this.
                                                                size))));
                                
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4226642740 =
                                  ((double)(int)(((int)(j42747))));
                                
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4226942741 =
                                  ((t4226642740) * (((double)(this.
                                                                size))));
                                
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4226742742 =
                                  ((double)(int)(((int)(k42748))));
                                
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4227042743 =
                                  ((t4226742742) * (((double)(this.
                                                                size))));
                                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4227142744 =
                                  x10.util.concurrent.OrderedLock.createNewLock();
                                
//#line 121 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
translationVector42764.$init(t4226842739,
                                                                                                                                                               t4226942741,
                                                                                                                                                               t4227042743,
                                                                                                                                                               t4227142744);
                                
//#line 124 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.LocalExpansion transform42765 =
                                  au.edu.anu.mm.LocalExpansion.getMlm(((x10x.vector.Tuple3d)(translationVector42764)),
                                                                      (int)(this.
                                                                              numTerms));
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.LocalExpansion ret4098742766 =
                                   null;
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4098842767: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t4227242768 =
                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion t4227342769 =
                                  ((au.edu.anu.mm.LocalExpansion)(((au.edu.anu.mm.LocalExpansion[])t4227242768.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4098742766 = t4227342769;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4098842767;}
                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion t4227442770 =
                                  ret4098742766;
                                
//#line 125 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t4227442770.unsafeAdd(((au.edu.anu.mm.Expansion)(transform42765)));
                            }
                        }
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.LocalExpansion ret40995 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret40996: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t42277 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion t42278 =
                          ((au.edu.anu.mm.LocalExpansion)(((au.edu.anu.mm.LocalExpansion[])t42277.value)[0]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret40995 = t42278;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret40996;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion t42279 =
                          ret40995;
                        
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion v41003 =
                          ((au.edu.anu.mm.LocalExpansion)(t42279.getMacroscopicParent()));
                        
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.LocalExpansion ret41004 =
                           null;
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t4228042771 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                           raw));
                        
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((au.edu.anu.mm.LocalExpansion[])t4228042771.value)[1] = v41003;
                        
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret41004 = ((au.edu.anu.mm.LocalExpansion)(v41003));
                        
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int shell42849 =
                          2;
                        
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                               true;
                                                                                                                               ) {
                            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4228242850 =
                              shell42849;
                            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4230242851 =
                              ((t4228242850) <= (((int)(this.
                                                          numShells))));
                            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4230242851)) {
                                
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                            }
                            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion t4228542775 =
                              macroTranslation;
                            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion t4228642776 =
                              t4228542775.getMacroscopicParent();
                            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
macroTranslation = t4228642776;
                            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04101142777 =
                              shell42849;
                            
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion alloc2467842778 =
                              ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
                            
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4228742774 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc2467842778.$init(((int)(this.
                                                                                                                                                             numTerms)),
                                                                                                                                                    t4228742774);
                            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion v4101242779 =
                              ((au.edu.anu.mm.MultipoleExpansion)(alloc2467842778));
                            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret4101342780 =
                               null;
                            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t4228842772 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                               raw));
                            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((au.edu.anu.mm.MultipoleExpansion[])t4228842772.value)[i04101142777] = v4101242779;
                            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4101342780 = ((au.edu.anu.mm.MultipoleExpansion)(v4101242779));
                            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04102042781 =
                              shell42849;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret4102142782 =
                               null;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4102242783: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t4228942784 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t4229042785 =
                              ((au.edu.anu.mm.MultipoleExpansion)(((au.edu.anu.mm.MultipoleExpansion[])t4228942784.value)[i04102042781]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4102142782 = t4229042785;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4102242783;}
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t4229442786 =
                              ret4102142782;
                            
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion t4229542787 =
                              macroTranslation;
                            
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4229142788 =
                              shell42849;
                            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04102842789 =
                              ((t4229142788) - (((int)(1))));
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret4102942790 =
                               null;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4103042791: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t4229242792 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t4229342793 =
                              ((au.edu.anu.mm.MultipoleExpansion)(((au.edu.anu.mm.MultipoleExpansion[])t4229242792.value)[i04102842789]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4102942790 = t4229342793;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4103042791;}
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t4229642794 =
                              ret4102942790;
                            
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t4229442786.translateAndAddMultipole(((au.edu.anu.mm.MultipoleExpansion)(t4229542787)),
                                                                                                                                                                   ((au.edu.anu.mm.MultipoleExpansion)(t4229642794)));
                            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04104442795 =
                              shell42849;
                            
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4229742796 =
                              shell42849;
                            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04103642797 =
                              ((t4229742796) - (((int)(1))));
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.LocalExpansion ret4103742798 =
                               null;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4103842799: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t4229842800 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion t4229942801 =
                              ((au.edu.anu.mm.LocalExpansion)(((au.edu.anu.mm.LocalExpansion[])t4229842800.value)[i04103642797]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4103742798 = t4229942801;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4103842799;}
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion t4230042802 =
                              ret4103742798;
                            
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion v4104542803 =
                              ((au.edu.anu.mm.LocalExpansion)(t4230042802.getMacroscopicParent()));
                            
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.LocalExpansion ret4104642804 =
                               null;
                            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t4230142773 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                               raw));
                            
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((au.edu.anu.mm.LocalExpansion[])t4230142773.value)[i04104442795] = v4104542803;
                            
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4104642804 = ((au.edu.anu.mm.LocalExpansion)(v4104542803));
                            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4228342805 =
                              shell42849;
                            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4228442806 =
                              ((t4228342805) + (((int)(1))));
                            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
shell42849 = t4228442806;
                        }
                        
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int shell42852 =
                          0;
                        
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                               true;
                                                                                                                               ) {
                            
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4230442853 =
                              shell42852;
                            
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4231342854 =
                              ((t4230442853) <= (((int)(this.
                                                          numShells))));
                            
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4231342854)) {
                                
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                            }
                            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04105342807 =
                              shell42852;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.LocalExpansion ret4105442808 =
                               null;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4105542809: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.LocalExpansion> t4230742810 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.LocalExpansion>)macroLocalTranslations).
                                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.LocalExpansion t4230842811 =
                              ((au.edu.anu.mm.LocalExpansion)(((au.edu.anu.mm.LocalExpansion[])t4230742810.value)[i04105342807]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4105442808 = t4230842811;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4105542809;}
                            
//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.LocalExpansion localExpansion42812 =
                              ret4105442808;
                            
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.LocalExpansion t4231142813 =
                              ((au.edu.anu.mm.LocalExpansion)(topLevelBox.
                                                                localExp));
                            
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04106142814 =
                              shell42852;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.mm.MultipoleExpansion ret4106242815 =
                               null;
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4106342816: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.mm.MultipoleExpansion> t4230942817 =
                              ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.mm.MultipoleExpansion>)macroMultipoles).
                                                               raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t4231042818 =
                              ((au.edu.anu.mm.MultipoleExpansion)(((au.edu.anu.mm.MultipoleExpansion[])t4230942817.value)[i04106142814]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4106242815 = t4231042818;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4106342816;}
                            
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.mm.MultipoleExpansion t4231242819 =
                              ret4106242815;
                            
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t4231142813.transformAndAddToLocal(((au.edu.anu.mm.LocalExpansion)(localExpansion42812)),
                                                                                                                                                                 ((au.edu.anu.mm.MultipoleExpansion)(t4231242819)));
                            
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4230542820 =
                              shell42852;
                            
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4230642821 =
                              ((t4230542820) + (((int)(1))));
                            
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
shell42852 = t4230642821;
                        }
                    }
                    
                    public int
                      numShells;
                    public x10.array.Array<x10.array.DistArray<au.edu.anu.mm.FmmBox>>
                      boxes;
                    public int
                      numTerms;
                    public double
                      size;
                    
                    // creation method for java code
                    public static au.edu.anu.mm.PeriodicFmm3d.$Closure$15 $make(final int numShells,
                                                                                final x10.array.Array<x10.array.DistArray<au.edu.anu.mm.FmmBox>> boxes,
                                                                                final int numTerms,
                                                                                final double size,java.lang.Class<?> $dummy0){return new $Closure$15(numShells,boxes,numTerms,size,(java.lang.Class<?>) null);}
                    public $Closure$15(final int numShells,
                                       final x10.array.Array<x10.array.DistArray<au.edu.anu.mm.FmmBox>> boxes,
                                       final int numTerms,
                                       final double size,java.lang.Class<?> $dummy0) { {
                                                                                              this.numShells = numShells;
                                                                                              this.boxes = ((x10.array.Array)(boxes));
                                                                                              this.numTerms = numTerms;
                                                                                              this.size = size;
                                                                                          }}
                    
                }
                
            public static class $Closure$16
            extends x10.core.Ref
              implements x10.core.fun.VoidFun_0_0,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$16.class);
                
                public static final x10.rtt.RuntimeType<$Closure$16> $RTT = new x10.rtt.StaticVoidFunType<$Closure$16>(
                /* base class */$Closure$16.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$16 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    x10x.vector.Point3d offsetCentre42898 = (x10x.vector.Point3d) $deserializer.readRef();
                    $_obj.offsetCentre42898 = offsetCentre42898;
                    $_obj.charge42893 = $deserializer.readDouble();
                    x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                    $_obj.lowestLevelBoxes = lowestLevelBoxes;
                    x10.array.Point boxIndex42907 = (x10.array.Point) $deserializer.readRef();
                    $_obj.boxIndex42907 = boxIndex42907;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$16 $_obj = new $Closure$16((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    if (offsetCentre42898 instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.offsetCentre42898);
                    } else {
                    $serializer.write(this.offsetCentre42898);
                    }
                    $serializer.write(this.charge42893);
                    if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                    } else {
                    $serializer.write(this.lowestLevelBoxes);
                    }
                    if (boxIndex42907 instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.boxIndex42907);
                    } else {
                    $serializer.write(this.boxIndex42907);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$16(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.chem.PointCharge remoteAtom42913 =
                          new au.edu.anu.chem.PointCharge((java.lang.System[]) null);
                        
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4236242855 =
                          x10.util.concurrent.OrderedLock.createNewLock();
                        
//#line 168 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
remoteAtom42913.$init(this.
                                                                                                                                                  offsetCentre42898,
                                                                                                                                                ((double)(this.
                                                                                                                                                            charge42893)),
                                                                                                                                                t4236242855);
                        
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmBox t4236342914 =
                          ((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                        lowestLevelBoxes).$apply$G(((x10.array.Point)(this.
                                                                                                                        boxIndex42907)));
                        
//#line 169 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmLeafBox leafBox42915 =
                          x10.rtt.Types.<au.edu.anu.mm.FmmLeafBox> cast(t4236342914,au.edu.anu.mm.FmmLeafBox.$RTT);
                        
//#line 170 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
leafBox42915.addAtom(((au.edu.anu.chem.PointCharge)(remoteAtom42913)));
                    }
                    
                    public x10x.vector.Point3d
                      offsetCentre42898;
                    public double
                      charge42893;
                    public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                      lowestLevelBoxes;
                    public x10.array.Point
                      boxIndex42907;
                    
                    // creation method for java code
                    public static au.edu.anu.mm.PeriodicFmm3d.$Closure$16 $make(final x10x.vector.Point3d offsetCentre42898,
                                                                                final double charge42893,
                                                                                final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                                                                final x10.array.Point boxIndex42907,java.lang.Class<?> $dummy0){return new $Closure$16(offsetCentre42898,charge42893,lowestLevelBoxes,boxIndex42907,(java.lang.Class<?>) null);}
                    public $Closure$16(final x10x.vector.Point3d offsetCentre42898,
                                       final double charge42893,
                                       final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                       final x10.array.Point boxIndex42907,java.lang.Class<?> $dummy0) { {
                                                                                                                this.offsetCentre42898 = ((x10x.vector.Point3d)(offsetCentre42898));
                                                                                                                this.charge42893 = charge42893;
                                                                                                                this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                                this.boxIndex42907 = ((x10.array.Point)(boxIndex42907));
                                                                                                            }}
                    
                }
                
            public static class $Closure$17
            extends x10.core.Ref
              implements x10.core.fun.VoidFun_0_0,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$17.class);
                
                public static final x10.rtt.RuntimeType<$Closure$17> $RTT = new x10.rtt.StaticVoidFunType<$Closure$17>(
                /* base class */$Closure$17.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$17 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                    $_obj.lowestLevelBoxes = lowestLevelBoxes;
                    x10.array.Point boxIndex42907 = (x10.array.Point) $deserializer.readRef();
                    $_obj.boxIndex42907 = boxIndex42907;
                    x10x.vector.Point3d offsetCentre42898 = (x10x.vector.Point3d) $deserializer.readRef();
                    $_obj.offsetCentre42898 = offsetCentre42898;
                    $_obj.charge42893 = $deserializer.readDouble();
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$17 $_obj = new $Closure$17((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                    } else {
                    $serializer.write(this.lowestLevelBoxes);
                    }
                    if (boxIndex42907 instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.boxIndex42907);
                    } else {
                    $serializer.write(this.boxIndex42907);
                    }
                    if (offsetCentre42898 instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.offsetCentre42898);
                    } else {
                    $serializer.write(this.offsetCentre42898);
                    }
                    $serializer.write(this.charge42893);
                    
                }
                
                // constructor just for allocation
                public $Closure$17(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Dist t4235842908 =
                          ((x10.array.Dist)(((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                                          lowestLevelBoxes).
                                              dist));
                        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4235942909 =
                          this.
                            boxIndex42907.$apply$O((int)(0));
                        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4236042910 =
                          this.
                            boxIndex42907.$apply$O((int)(1));
                        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4236142911 =
                          this.
                            boxIndex42907.$apply$O((int)(2));
                        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Place t4236442912 =
                          t4235842908.$apply((int)(t4235942909),
                                             (int)(t4236042910),
                                             (int)(t4236142911));
                        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t4236442912)),
                                                                                                                                                 ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$16(((x10x.vector.Point3d)(this.
                                                                                                                                                                                                                                                  offsetCentre42898)),
                                                                                                                                                                                                                         this.
                                                                                                                                                                                                                           charge42893,
                                                                                                                                                                                                                         this.
                                                                                                                                                                                                                           lowestLevelBoxes,
                                                                                                                                                                                                                         ((x10.array.Point)(this.
                                                                                                                                                                                                                                              boxIndex42907)),(java.lang.Class<?>) null))));
                    }
                    
                    public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                      lowestLevelBoxes;
                    public x10.array.Point
                      boxIndex42907;
                    public x10x.vector.Point3d
                      offsetCentre42898;
                    public double
                      charge42893;
                    
                    // creation method for java code
                    public static au.edu.anu.mm.PeriodicFmm3d.$Closure$17 $make(final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                                                                final x10.array.Point boxIndex42907,
                                                                                final x10x.vector.Point3d offsetCentre42898,
                                                                                final double charge42893,java.lang.Class<?> $dummy0){return new $Closure$17(lowestLevelBoxes,boxIndex42907,offsetCentre42898,charge42893,(java.lang.Class<?>) null);}
                    public $Closure$17(final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                       final x10.array.Point boxIndex42907,
                                       final x10x.vector.Point3d offsetCentre42898,
                                       final double charge42893,java.lang.Class<?> $dummy0) { {
                                                                                                     this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                     this.boxIndex42907 = ((x10.array.Point)(boxIndex42907));
                                                                                                     this.offsetCentre42898 = ((x10x.vector.Point3d)(offsetCentre42898));
                                                                                                     this.charge42893 = charge42893;
                                                                                                 }}
                    
                }
                
            public static class $Closure$18
            extends x10.core.Ref
              implements x10.core.fun.VoidFun_0_0,
                          x10.x10rt.X10JavaSerializable 
            {
                private static final long serialVersionUID = 1L;
                private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$18.class);
                
                public static final x10.rtt.RuntimeType<$Closure$18> $RTT = new x10.rtt.StaticVoidFunType<$Closure$18>(
                /* base class */$Closure$18.class
                , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                );
                public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                
                
                public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$18 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                    $_obj.atoms = atoms;
                    x10.array.Point p1 = (x10.array.Point) $deserializer.readRef();
                    $_obj.p1 = p1;
                    x10x.vector.Vector3d offset = (x10x.vector.Vector3d) $deserializer.readRef();
                    $_obj.offset = offset;
                    $_obj.lowestLevelDim = $deserializer.readInt();
                    $_obj.size = $deserializer.readDouble();
                    x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                    $_obj.lowestLevelBoxes = lowestLevelBoxes;
                    return $_obj;
                    
                }
                
                public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                
                    $Closure$18 $_obj = new $Closure$18((java.lang.System[]) null);
                    $deserializer.record_reference($_obj);
                    return $_deserialize_body($_obj, $deserializer);
                    
                }
                
                public short $_get_serialization_id() {
                
                     return $_serialization_id;
                    
                }
                
                public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                
                    if (atoms instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.atoms);
                    } else {
                    $serializer.write(this.atoms);
                    }
                    if (p1 instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.p1);
                    } else {
                    $serializer.write(this.p1);
                    }
                    if (offset instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.offset);
                    } else {
                    $serializer.write(this.offset);
                    }
                    $serializer.write(this.lowestLevelDim);
                    $serializer.write(this.size);
                    if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                    $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                    } else {
                    $serializer.write(this.lowestLevelBoxes);
                    }
                    
                }
                
                // constructor just for allocation
                public $Closure$18(final java.lang.System[] $dummy) { 
                super($dummy);
                }
                
                    
                    public void
                      $apply(
                      ){
                        
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10x.vector.Vector3d myDipole =
                          x10x.vector.Vector3d.getInitialized$NULL();
                        
//#line 159 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Array<au.edu.anu.chem.mm.MMAtom> localAtoms =
                          ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>)this.
                                                                                                                 atoms).$apply$G(((x10.array.Point)(this.
                                                                                                                                                      p1)))));
                        {
                            
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.ensureNotInAtomic();
                            
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.FinishState x10$__var3 =
                              x10.lang.Runtime.startFinish();
                            
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
try {try {{
                                {
                                    
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42320 =
                                      ((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)localAtoms).
                                        size;
                                    
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i24732max24734 =
                                      ((t42320) - (((int)(1))));
                                    
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int i2473242919 =
                                      0;
                                    
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                                           true;
                                                                                                                                           ) {
                                        
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4232242920 =
                                          i2473242919;
                                        
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4236542921 =
                                          ((t4232242920) <= (((int)(i24732max24734))));
                                        
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4236542921)) {
                                            
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                                        }
                                        
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i42916 =
                                          i2473242919;
                                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04106942887 =
                                          i42916;
                                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.mm.MMAtom ret4107042888 =
                                           null;
                                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4107142889: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.mm.MMAtom> t4232542890 =
                                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.mm.MMAtom>)localAtoms).
                                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.mm.MMAtom t4232642891 =
                                          ((au.edu.anu.chem.mm.MMAtom)(((au.edu.anu.chem.mm.MMAtom[])t4232542890.value)[i04106942887]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4107042888 = t4232642891;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4107142889;}
                                        
//#line 161 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.chem.mm.MMAtom atom42892 =
                                          ret4107042888;
                                        
//#line 162 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double charge42893 =
                                          atom42892.
                                            charge;
                                        
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this4107842894 =
                                          ((x10x.vector.Point3d)(atom42892.
                                                                   centre));
                                        
//#line 31 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d that4107742895 =
                                          ((x10x.vector.Vector3d)(this.
                                                                    offset));
                                        
//#line 27 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d b4107942896 =
                                          ((x10x.vector.Vector3d)(that4107742895));
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc242524108042897 =
                                          new x10x.vector.Point3d((java.lang.System[]) null);
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4232742856 =
                                          this4107842894.
                                            i;
                                        
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4232842857 =
                                          b4107942896.
                                            i;
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4233342858 =
                                          ((t4232742856) + (((double)(t4232842857))));
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4232942859 =
                                          this4107842894.
                                            j;
                                        
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4233042860 =
                                          b4107942896.
                                            j;
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4233442861 =
                                          ((t4232942859) + (((double)(t4233042860))));
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4233142862 =
                                          this4107842894.
                                            k;
                                        
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4233242863 =
                                          b4107942896.
                                            k;
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4233542864 =
                                          ((t4233142862) + (((double)(t4233242863))));
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t4233642865 =
                                          x10.util.concurrent.OrderedLock.createNewLock();
                                        
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc242524108042897.$init(t4233342858,
                                                                                                                                                                   t4233442861,
                                                                                                                                                                   t4233542864,
                                                                                                                                                                   t4233642865);
                                        
//#line 163 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d offsetCentre42898 =
                                          alloc242524108042897;
                                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d alloc2468042899 =
                                          new x10x.vector.Vector3d((java.lang.System[]) null);
                                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4233742866 =
                                          x10.util.concurrent.OrderedLock.createNewLock();
                                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
alloc2468042899.$init(((x10x.vector.Tuple3d)(offsetCentre42898)),
                                                                                                                                                                t4233742866);
                                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d this4108242900 =
                                          alloc2468042899;
                                        
//#line 72 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double that4108142901 =
                                          charge42893;
                                        
//#line 81 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double c4108342902 =
                                          that4108142901;
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc255094108442903 =
                                          new x10x.vector.Vector3d((java.lang.System[]) null);
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4233842867 =
                                          this4108242900.
                                            i;
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4234142868 =
                                          ((t4233842867) * (((double)(c4108342902))));
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4233942869 =
                                          this4108242900.
                                            j;
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4234242870 =
                                          ((t4233942869) * (((double)(c4108342902))));
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4234042871 =
                                          this4108242900.
                                            k;
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4234342872 =
                                          ((t4234042871) * (((double)(c4108342902))));
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4234442873 =
                                          x10.util.concurrent.OrderedLock.createNewLock();
                                        
//#line 82 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc255094108442903.$init(t4234142868,
                                                                                                                                                                    t4234242870,
                                                                                                                                                                    t4234342872,
                                                                                                                                                                    t4234442873);
                                        
//#line 33 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d that4108542904 =
                                          ((x10x.vector.Vector3d)(alloc255094108442903));
                                        
//#line 37 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d b4108642905 =
                                          ((x10x.vector.Vector3d)(that4108542904));
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d alloc255064108742906 =
                                          new x10x.vector.Vector3d((java.lang.System[]) null);
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4234542874 =
                                          myDipole;
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4234642875 =
                                          t4234542874.
                                            i;
                                        
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4234742876 =
                                          b4108642905.
                                            i;
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4235442877 =
                                          ((t4234642875) + (((double)(t4234742876))));
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4234842878 =
                                          myDipole;
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4234942879 =
                                          t4234842878.
                                            j;
                                        
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4235042880 =
                                          b4108642905.
                                            j;
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4235542881 =
                                          ((t4234942879) + (((double)(t4235042880))));
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10x.vector.Vector3d t4235142882 =
                                          myDipole;
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4235242883 =
                                          t4235142882.
                                            k;
                                        
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4235342884 =
                                          b4108642905.
                                            k;
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4235642885 =
                                          ((t4235242883) + (((double)(t4235342884))));
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final x10.util.concurrent.OrderedLock t4235742886 =
                                          x10.util.concurrent.OrderedLock.createNewLock();
                                        
//#line 38 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
alloc255064108742906.$init(t4235442877,
                                                                                                                                                                    t4235542881,
                                                                                                                                                                    t4235642885,
                                                                                                                                                                    t4235742886);
                                        
//#line 164 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
myDipole = alloc255064108742906;
                                        
//#line 165 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point boxIndex42907 =
                                          ((x10.array.Point)(au.edu.anu.mm.Fmm3d.getLowestLevelBoxIndex(((x10x.vector.Point3d)(offsetCentre42898)),
                                                                                                        (int)(this.
                                                                                                                lowestLevelDim),
                                                                                                        (double)(this.
                                                                                                                   size))));
                                        
//#line 167 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$17(this.
                                                                                                                                                                                                                                              lowestLevelBoxes,
                                                                                                                                                                                                                                            boxIndex42907,
                                                                                                                                                                                                                                            offsetCentre42898,
                                                                                                                                                                                                                                            charge42893,(java.lang.Class<?>) null))));
                                        
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4232342917 =
                                          i2473242919;
                                        
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4232442918 =
                                          ((t4232342917) + (((int)(1))));
                                        
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
i2473242919 = t4232442918;
                                    }
                                }
                            }}catch (x10.core.Throwable $exc$) {throw $exc$;}catch (java.lang.Throwable $exc$) {throw x10.core.ThrowableUtilities.convertJavaThrowable($exc$);}}catch (x10.core.X10Throwable __lowerer__var__5__) {
                                
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.pushException(((x10.core.X10Throwable)(__lowerer__var__5__)));
                                
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
throw new x10.lang.RuntimeException();
                            }finally {{
                                 
//#line 160 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.stopFinish(((x10.lang.FinishState)(x10$__var3)));
                             }}
                            }
                        
//#line 173 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d t42366 =
                          myDipole;
                        
//#line 173 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.<x10x.vector.Vector3d>makeOffer_0_$$x10$lang$Runtime_T(x10x.vector.Vector3d.$RTT, ((x10x.vector.Vector3d)(t42366)));
                        }
                    
                    public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>
                      atoms;
                    public x10.array.Point
                      p1;
                    public x10x.vector.Vector3d
                      offset;
                    public int
                      lowestLevelDim;
                    public double
                      size;
                    public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                      lowestLevelBoxes;
                    
                    // creation method for java code
                    public static au.edu.anu.mm.PeriodicFmm3d.$Closure$18 $make(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                                final x10.array.Point p1,
                                                                                final x10x.vector.Vector3d offset,
                                                                                final int lowestLevelDim,
                                                                                final double size,
                                                                                final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,java.lang.Class<?> $dummy0){return new $Closure$18(atoms,p1,offset,lowestLevelDim,size,lowestLevelBoxes,(java.lang.Class<?>) null);}
                    public $Closure$18(final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                       final x10.array.Point p1,
                                       final x10x.vector.Vector3d offset,
                                       final int lowestLevelDim,
                                       final double size,
                                       final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,java.lang.Class<?> $dummy0) { {
                                                                                                                                             this.atoms = ((x10.array.DistArray)(atoms));
                                                                                                                                             this.p1 = ((x10.array.Point)(p1));
                                                                                                                                             this.offset = ((x10x.vector.Vector3d)(offset));
                                                                                                                                             this.lowestLevelDim = lowestLevelDim;
                                                                                                                                             this.size = size;
                                                                                                                                             this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                                                         }}
                    
                    }
                    
                public static class $Closure$19
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$19.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$19> $RTT = new x10.rtt.StaticVoidFunType<$Closure$19>(
                    /* base class */$Closure$19.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$19 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10.array.Dist __lowerer__var__6__ = (x10.array.Dist) $deserializer.readRef();
                        $_obj.__lowerer__var__6__ = __lowerer__var__6__;
                        x10.array.DistArray atoms = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.atoms = atoms;
                        x10x.vector.Vector3d offset = (x10x.vector.Vector3d) $deserializer.readRef();
                        $_obj.offset = offset;
                        $_obj.lowestLevelDim = $deserializer.readInt();
                        $_obj.size = $deserializer.readDouble();
                        x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.lowestLevelBoxes = lowestLevelBoxes;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$19 $_obj = new $Closure$19((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (__lowerer__var__6__ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.__lowerer__var__6__);
                        } else {
                        $serializer.write(this.__lowerer__var__6__);
                        }
                        if (atoms instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.atoms);
                        } else {
                        $serializer.write(this.atoms);
                        }
                        if (offset instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.offset);
                        } else {
                        $serializer.write(this.offset);
                        }
                        $serializer.write(this.lowestLevelDim);
                        $serializer.write(this.size);
                        if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                        } else {
                        $serializer.write(this.lowestLevelBoxes);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$19(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.array.Point> p143269 =
                                                                                                                                     this.
                                                                                                                                       __lowerer__var__6__.restriction(((x10.lang.Place)(x10.lang.Runtime.home()))).
                                                                                                                                       region.iterator();
                                                                                                                                   ((x10.lang.Iterator<x10.array.Point>)p143269).hasNext$O();
                                                                                                                                   ) {
                                
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point p1 =
                                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p143269).next$G()));
                                
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(x10.lang.Runtime.home())),
                                                                                                                                                            ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$18(this.
                                                                                                                                                                                                                                      atoms,
                                                                                                                                                                                                                                    p1,
                                                                                                                                                                                                                                    this.
                                                                                                                                                                                                                                      offset,
                                                                                                                                                                                                                                    this.
                                                                                                                                                                                                                                      lowestLevelDim,
                                                                                                                                                                                                                                    this.
                                                                                                                                                                                                                                      size,
                                                                                                                                                                                                                                    this.
                                                                                                                                                                                                                                      lowestLevelBoxes,(java.lang.Class<?>) null))));
                            }
                        }
                        
                        public x10.array.Dist
                          __lowerer__var__6__;
                        public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>>
                          atoms;
                        public x10x.vector.Vector3d
                          offset;
                        public int
                          lowestLevelDim;
                        public double
                          size;
                        public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                          lowestLevelBoxes;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.PeriodicFmm3d.$Closure$19 $make(final x10.array.Dist __lowerer__var__6__,
                                                                                    final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                                                                    final x10x.vector.Vector3d offset,
                                                                                    final int lowestLevelDim,
                                                                                    final double size,
                                                                                    final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,java.lang.Class<?> $dummy0){return new $Closure$19(__lowerer__var__6__,atoms,offset,lowestLevelDim,size,lowestLevelBoxes,(java.lang.Class<?>) null);}
                        public $Closure$19(final x10.array.Dist __lowerer__var__6__,
                                           final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.mm.MMAtom>> atoms,
                                           final x10x.vector.Vector3d offset,
                                           final int lowestLevelDim,
                                           final double size,
                                           final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,java.lang.Class<?> $dummy0) { {
                                                                                                                                                 this.__lowerer__var__6__ = ((x10.array.Dist)(__lowerer__var__6__));
                                                                                                                                                 this.atoms = ((x10.array.DistArray)(atoms));
                                                                                                                                                 this.offset = ((x10x.vector.Vector3d)(offset));
                                                                                                                                                 this.lowestLevelDim = lowestLevelDim;
                                                                                                                                                 this.size = size;
                                                                                                                                                 this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                                                             }}
                        
                    }
                    
                public static class $Closure$20
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$20.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$20> $RTT = new x10.rtt.StaticVoidFunType<$Closure$20>(
                    /* base class */$Closure$20.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$20 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.lowestLevelBoxes = lowestLevelBoxes;
                        x10.array.Point boxIndex = (x10.array.Point) $deserializer.readRef();
                        $_obj.boxIndex = boxIndex;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$20 $_obj = new $Closure$20((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                        } else {
                        $serializer.write(this.lowestLevelBoxes);
                        }
                        if (boxIndex instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.boxIndex);
                        } else {
                        $serializer.write(this.boxIndex);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$20(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmBox t42367 =
                              ((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                            lowestLevelBoxes).$apply$G(((x10.array.Point)(this.
                                                                                                                            boxIndex)));
                            
//#line 184 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmLeafBox box =
                              x10.rtt.Types.<au.edu.anu.mm.FmmLeafBox> cast(t42367,au.edu.anu.mm.FmmLeafBox.$RTT);
                            
//#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> t42368 =
                              ((x10.util.ArrayList)(box.
                                                      atoms));
                            
//#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t42369 =
                              ((x10.util.ArrayList<au.edu.anu.chem.PointCharge>)t42368).size$O();
                            
//#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t42370 =
                              ((int) t42369) ==
                            ((int) 0);
                            
//#line 185 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t42370) {
                                
//#line 186 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                                                                                                                                lowestLevelBoxes).$set_1_$$x10$array$DistArray_T$G(((x10.array.Point)(this.
                                                                                                                                                                                                                                                        boxIndex)),
                                                                                                                                                                                                                                   ((au.edu.anu.mm.FmmBox)(null)));
                            }
                        }
                        
                        public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                          lowestLevelBoxes;
                        public x10.array.Point
                          boxIndex;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.PeriodicFmm3d.$Closure$20 $make(final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                                                                    final x10.array.Point boxIndex,java.lang.Class<?> $dummy0){return new $Closure$20(lowestLevelBoxes,boxIndex,(java.lang.Class<?>) null);}
                        public $Closure$20(final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                           final x10.array.Point boxIndex,java.lang.Class<?> $dummy0) { {
                                                                                                               this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                               this.boxIndex = ((x10.array.Point)(boxIndex));
                                                                                                           }}
                        
                    }
                    
                public static class $Closure$21
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$21.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$21> $RTT = new x10.rtt.StaticVoidFunType<$Closure$21>(
                    /* base class */$Closure$21.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$21 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10.array.Dist __lowerer__var__9__ = (x10.array.Dist) $deserializer.readRef();
                        $_obj.__lowerer__var__9__ = __lowerer__var__9__;
                        x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.lowestLevelBoxes = lowestLevelBoxes;
                        return $_obj;
                        
                    }
                    
                    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        $Closure$21 $_obj = new $Closure$21((java.lang.System[]) null);
                        $deserializer.record_reference($_obj);
                        return $_deserialize_body($_obj, $deserializer);
                        
                    }
                    
                    public short $_get_serialization_id() {
                    
                         return $_serialization_id;
                        
                    }
                    
                    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
                    
                        if (__lowerer__var__9__ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.__lowerer__var__9__);
                        } else {
                        $serializer.write(this.__lowerer__var__9__);
                        }
                        if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                        } else {
                        $serializer.write(this.lowestLevelBoxes);
                        }
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$21(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.array.Point> boxIndex43281 =
                                                                                                                                     this.
                                                                                                                                       __lowerer__var__9__.restriction(((x10.lang.Place)(x10.lang.Runtime.home()))).
                                                                                                                                       region.iterator();
                                                                                                                                   ((x10.lang.Iterator<x10.array.Point>)boxIndex43281).hasNext$O();
                                                                                                                                   ) {
                                
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point boxIndex =
                                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)boxIndex43281).next$G()));
                                
//#line 183 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(x10.lang.Runtime.home())),
                                                                                                                                                            ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$20(this.
                                                                                                                                                                                                                                      lowestLevelBoxes,
                                                                                                                                                                                                                                    boxIndex,(java.lang.Class<?>) null))));
                            }
                        }
                        
                        public x10.array.Dist
                          __lowerer__var__9__;
                        public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                          lowestLevelBoxes;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.PeriodicFmm3d.$Closure$21 $make(final x10.array.Dist __lowerer__var__9__,
                                                                                    final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,java.lang.Class<?> $dummy0){return new $Closure$21(__lowerer__var__9__,lowestLevelBoxes,(java.lang.Class<?>) null);}
                        public $Closure$21(final x10.array.Dist __lowerer__var__9__,
                                           final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,java.lang.Class<?> $dummy0) { {
                                                                                                                                                 this.__lowerer__var__9__ = ((x10.array.Dist)(__lowerer__var__9__));
                                                                                                                                                 this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                                                             }}
                        
                    }
                    
                public static class $Closure$22
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$22.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$22> $RTT = new x10.rtt.StaticVoidFunType<$Closure$22>(
                    /* base class */$Closure$22.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$22 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10x.vector.Point3d offsetCentre = (x10x.vector.Point3d) $deserializer.readRef();
                        $_obj.offsetCentre = offsetCentre;
                        $_obj.charge = $deserializer.readDouble();
                        x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.lowestLevelBoxes = lowestLevelBoxes;
                        x10.array.Point boxIndex = (x10.array.Point) $deserializer.readRef();
                        $_obj.boxIndex = boxIndex;
                        $_obj.size = $deserializer.readDouble();
                        $_obj.numTerms = $deserializer.readInt();
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
                    
                        if (offsetCentre instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.offsetCentre);
                        } else {
                        $serializer.write(this.offsetCentre);
                        }
                        $serializer.write(this.charge);
                        if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                        } else {
                        $serializer.write(this.lowestLevelBoxes);
                        }
                        if (boxIndex instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.boxIndex);
                        } else {
                        $serializer.write(this.boxIndex);
                        }
                        $serializer.write(this.size);
                        $serializer.write(this.numTerms);
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$22(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.chem.PointCharge remoteAtom =
                              new au.edu.anu.chem.PointCharge((java.lang.System[]) null);
                            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.concurrent.OrderedLock t4237442922 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 197 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
remoteAtom.$init(this.
                                                                                                                                                 offsetCentre,
                                                                                                                                               this.
                                                                                                                                                 charge,
                                                                                                                                               t4237442922);
                            
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmBox t42375 =
                              ((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                            lowestLevelBoxes).$apply$G(((x10.array.Point)(this.
                                                                                                                            boxIndex)));
                            
//#line 198 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmLeafBox leafBox =
                              x10.rtt.Types.<au.edu.anu.mm.FmmLeafBox> cast(t42375,au.edu.anu.mm.FmmLeafBox.$RTT);
                            
//#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this42050 =
                              leafBox.getCentre((double)(this.
                                                           size));
                            
//#line 49 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b42048 =
                              ((x10x.vector.Point3d)(this.
                                                       offsetCentre));
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d alloc2425442049 =
                              new x10x.vector.Vector3d((java.lang.System[]) null);
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4237642923 =
                              this42050.
                                i;
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4237742924 =
                              b42048.
                                i;
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4238242925 =
                              ((t4237642923) - (((double)(t4237742924))));
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4237842926 =
                              this42050.
                                j;
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4237942927 =
                              b42048.
                                j;
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4238342928 =
                              ((t4237842926) - (((double)(t4237942927))));
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4238042929 =
                              this42050.
                                k;
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4238142930 =
                              b42048.
                                k;
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4238442931 =
                              ((t4238042929) - (((double)(t4238142930))));
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t4238542932 =
                              x10.util.concurrent.OrderedLock.createNewLock();
                            
//#line 50 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc2425442049.$init(t4238242925,
                                                                                                                                                 t4238342928,
                                                                                                                                                 t4238442931,
                                                                                                                                                 t4238542932);
                            
//#line 199 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d boxLocation =
                              alloc2425442049;
                            
//#line 200 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion atomExpansion =
                              au.edu.anu.mm.MultipoleExpansion.getOlm((double)(this.
                                                                                 charge),
                                                                      ((x10x.vector.Tuple3d)(boxLocation)),
                                                                      (int)(this.
                                                                              numTerms));
                            
//#line 202 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
leafBox.addAtom(((au.edu.anu.chem.PointCharge)(remoteAtom)));
                            
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.MultipoleExpansion t42386 =
                              ((au.edu.anu.mm.MultipoleExpansion)(leafBox.
                                                                    multipoleExp));
                            
//#line 203 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
t42386.add(((au.edu.anu.mm.Expansion)(atomExpansion)));
                        }
                        
                        public x10x.vector.Point3d
                          offsetCentre;
                        public double
                          charge;
                        public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                          lowestLevelBoxes;
                        public x10.array.Point
                          boxIndex;
                        public double
                          size;
                        public int
                          numTerms;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.PeriodicFmm3d.$Closure$22 $make(final x10x.vector.Point3d offsetCentre,
                                                                                    final double charge,
                                                                                    final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                                                                    final x10.array.Point boxIndex,
                                                                                    final double size,
                                                                                    final int numTerms,java.lang.Class<?> $dummy0){return new $Closure$22(offsetCentre,charge,lowestLevelBoxes,boxIndex,size,numTerms,(java.lang.Class<?>) null);}
                        public $Closure$22(final x10x.vector.Point3d offsetCentre,
                                           final double charge,
                                           final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                           final x10.array.Point boxIndex,
                                           final double size,
                                           final int numTerms,java.lang.Class<?> $dummy0) { {
                                                                                                   this.offsetCentre = ((x10x.vector.Point3d)(offsetCentre));
                                                                                                   this.charge = charge;
                                                                                                   this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                   this.boxIndex = ((x10.array.Point)(boxIndex));
                                                                                                   this.size = size;
                                                                                                   this.numTerms = numTerms;
                                                                                               }}
                        
                    }
                    
                public static class $Closure$23
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$23.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$23> $RTT = new x10.rtt.StaticVoidFunType<$Closure$23>(
                    /* base class */$Closure$23.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$23 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.lowestLevelBoxes = lowestLevelBoxes;
                        x10.array.Point boxIndex = (x10.array.Point) $deserializer.readRef();
                        $_obj.boxIndex = boxIndex;
                        x10x.vector.Point3d offsetCentre = (x10x.vector.Point3d) $deserializer.readRef();
                        $_obj.offsetCentre = offsetCentre;
                        $_obj.charge = $deserializer.readDouble();
                        $_obj.size = $deserializer.readDouble();
                        $_obj.numTerms = $deserializer.readInt();
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
                    
                        if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                        } else {
                        $serializer.write(this.lowestLevelBoxes);
                        }
                        if (boxIndex instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.boxIndex);
                        } else {
                        $serializer.write(this.boxIndex);
                        }
                        if (offsetCentre instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.offsetCentre);
                        } else {
                        $serializer.write(this.offsetCentre);
                        }
                        $serializer.write(this.charge);
                        $serializer.write(this.size);
                        $serializer.write(this.numTerms);
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$23(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Dist t42373 =
                              ((x10.array.Dist)(((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                                              lowestLevelBoxes).
                                                  dist));
                            
//#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Place t42387 =
                              t42373.$apply(((x10.array.Point)(this.
                                                                 boxIndex)));
                            
//#line 196 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAt(((x10.lang.Place)(t42387)),
                                                                                                                                                     ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$22(((x10x.vector.Point3d)(this.
                                                                                                                                                                                                                                                      offsetCentre)),
                                                                                                                                                                                                                             ((double)(this.
                                                                                                                                                                                                                                         charge)),
                                                                                                                                                                                                                             this.
                                                                                                                                                                                                                               lowestLevelBoxes,
                                                                                                                                                                                                                             ((x10.array.Point)(this.
                                                                                                                                                                                                                                                  boxIndex)),
                                                                                                                                                                                                                             this.
                                                                                                                                                                                                                               size,
                                                                                                                                                                                                                             this.
                                                                                                                                                                                                                               numTerms,(java.lang.Class<?>) null))));
                        }
                        
                        public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                          lowestLevelBoxes;
                        public x10.array.Point
                          boxIndex;
                        public x10x.vector.Point3d
                          offsetCentre;
                        public double
                          charge;
                        public double
                          size;
                        public int
                          numTerms;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.PeriodicFmm3d.$Closure$23 $make(final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                                                                    final x10.array.Point boxIndex,
                                                                                    final x10x.vector.Point3d offsetCentre,
                                                                                    final double charge,
                                                                                    final double size,
                                                                                    final int numTerms,java.lang.Class<?> $dummy0){return new $Closure$23(lowestLevelBoxes,boxIndex,offsetCentre,charge,size,numTerms,(java.lang.Class<?>) null);}
                        public $Closure$23(final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                           final x10.array.Point boxIndex,
                                           final x10x.vector.Point3d offsetCentre,
                                           final double charge,
                                           final double size,
                                           final int numTerms,java.lang.Class<?> $dummy0) { {
                                                                                                   this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                   this.boxIndex = ((x10.array.Point)(boxIndex));
                                                                                                   this.offsetCentre = ((x10x.vector.Point3d)(offsetCentre));
                                                                                                   this.charge = charge;
                                                                                                   this.size = size;
                                                                                                   this.numTerms = numTerms;
                                                                                               }}
                        
                    }
                    
                public static class $Closure$24
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$24.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$24> $RTT = new x10.rtt.StaticVoidFunType<$Closure$24>(
                    /* base class */$Closure$24.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$24 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10.array.DistArray locallyEssentialTrees = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.locallyEssentialTrees = locallyEssentialTrees;
                        x10.array.Point p1 = (x10.array.Point) $deserializer.readRef();
                        $_obj.p1 = p1;
                        x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.lowestLevelBoxes = lowestLevelBoxes;
                        $_obj.lowestLevelDim = $deserializer.readInt();
                        $_obj.size = $deserializer.readDouble();
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
                    
                        if (locallyEssentialTrees instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.locallyEssentialTrees);
                        } else {
                        $serializer.write(this.locallyEssentialTrees);
                        }
                        if (p1 instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.p1);
                        } else {
                        $serializer.write(this.p1);
                        }
                        if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                        } else {
                        $serializer.write(this.lowestLevelBoxes);
                        }
                        $serializer.write(this.lowestLevelDim);
                        $serializer.write(this.size);
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$24(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 265 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.LocallyEssentialTree myLET =
                              ((x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree>)this.
                                                                                          locallyEssentialTrees).$apply$G(((x10.array.Point)(this.
                                                                                                                                               p1)));
                            
//#line 266 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> cachedAtoms =
                              ((x10.array.DistArray)(myLET.
                                                       cachedAtoms));
                            
//#line 267 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
double thisPlaceEnergy =
                              0.0;
                            
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Dist t4253843230 =
                              ((x10.array.Dist)(((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                                              lowestLevelBoxes).
                                                  dist));
                            
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Region t4253943231 =
                              ((x10.array.Region)(t4253843230.$apply(((x10.lang.Place)(x10.lang.Runtime.home())))));
                            
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.array.Point> id2482843232 =
                              t4253943231.iterator();
                            
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                                   true;
                                                                                                                                   ) {
                                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4264143233 =
                                  ((x10.lang.Iterator<x10.array.Point>)id2482843232).hasNext$O();
                                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4264143233)) {
                                    
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                                }
                                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point id41243222 =
                                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)id2482843232).next$G()));
                                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int x143223 =
                                  id41243222.$apply$O((int)(0));
                                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int y143224 =
                                  id41243222.$apply$O((int)(1));
                                
//#line 268 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int z143225 =
                                  id41243222.$apply$O((int)(2));
                                
//#line 269 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmBox t4254043226 =
                                  ((x10.array.DistArray<au.edu.anu.mm.FmmBox>)this.
                                                                                lowestLevelBoxes).$apply$G((int)(x143223),
                                                                                                           (int)(y143224),
                                                                                                           (int)(z143225));
                                
//#line 269 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.mm.FmmLeafBox box143227 =
                                  x10.rtt.Types.<au.edu.anu.mm.FmmLeafBox> cast(t4254043226,au.edu.anu.mm.FmmLeafBox.$RTT);
                                
//#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4264043228 =
                                  ((box143227) != (null));
                                
//#line 270 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t4264043228) {
                                    
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> t4254143216 =
                                      ((x10.util.ArrayList)(box143227.
                                                              atoms));
                                    
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4254243217 =
                                      ((x10.util.ArrayList<au.edu.anu.chem.PointCharge>)t4254143216).size$O();
                                    
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i24764max2476643218 =
                                      ((t4254243217) - (((int)(1))));
                                    
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int i2476443105 =
                                      0;
                                    
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                                           true;
                                                                                                                                           ) {
                                        
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4254443106 =
                                          i2476443105;
                                        
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4257243107 =
                                          ((t4254443106) <= (((int)(i24764max2476643218))));
                                        
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4257243107)) {
                                            
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                                        }
                                        
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int atomIndex143102 =
                                          i2476443105;
                                        
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> t4254743100 =
                                          ((x10.util.ArrayList)(box143227.
                                                                  atoms));
                                        
//#line 273 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.chem.PointCharge atom143101 =
                                          ((au.edu.anu.chem.PointCharge)(((x10.util.ArrayList<au.edu.anu.chem.PointCharge>)t4254743100).$apply$G((int)(atomIndex143102))));
                                        
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i24748max2475043099 =
                                          ((atomIndex143102) - (((int)(1))));
                                        
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int i2474843095 =
                                          0;
                                        
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                                               true;
                                                                                                                                               ) {
                                            
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4254943096 =
                                              i2474843095;
                                            
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4257143097 =
                                              ((t4254943096) <= (((int)(i24748max2475043099))));
                                            
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4257143097)) {
                                                
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                                            }
                                            
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int sameBoxAtomIndex43092 =
                                              i2474843095;
                                            
//#line 275 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> t4255243078 =
                                              ((x10.util.ArrayList)(box143227.
                                                                      atoms));
                                            
//#line 275 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.chem.PointCharge sameBoxAtom43079 =
                                              ((au.edu.anu.chem.PointCharge)(((x10.util.ArrayList<au.edu.anu.chem.PointCharge>)t4255243078).$apply$G((int)(sameBoxAtomIndex43092))));
                                            
//#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4255343080 =
                                              atom143101.
                                                charge;
                                            
//#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4255443081 =
                                              sameBoxAtom43079.
                                                charge;
                                            
//#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4256743082 =
                                              ((t4255343080) * (((double)(t4255443081))));
                                            
//#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this4211243083 =
                                              ((x10x.vector.Point3d)(atom143101.
                                                                       centre));
                                            
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b4211143084 =
                                              ((x10x.vector.Point3d)(sameBoxAtom43079.
                                                                       centre));
                                            
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b4211343085 =
                                              ((x10x.vector.Point3d)(b4211143084));
                                            
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
double ret4211743086 =
                                               0;
                                            
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4255543064 =
                                              this4211243083.
                                                i;
                                            
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4255643065 =
                                              b4211343085.
                                                i;
                                            
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di4211443066 =
                                              ((t4255543064) - (((double)(t4255643065))));
                                            
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4255743067 =
                                              this4211243083.
                                                j;
                                            
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4255843068 =
                                              b4211343085.
                                                j;
                                            
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj4211543069 =
                                              ((t4255743067) - (((double)(t4255843068))));
                                            
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4255943070 =
                                              this4211243083.
                                                k;
                                            
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256043071 =
                                              b4211343085.
                                                k;
                                            
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk4211643072 =
                                              ((t4255943070) - (((double)(t4256043071))));
                                            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256143073 =
                                              ((di4211443066) * (((double)(di4211443066))));
                                            
//#line 62 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256243074 =
                                              ((dj4211543069) * (((double)(dj4211543069))));
                                            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256343075 =
                                              ((t4256143073) + (((double)(t4256243074))));
                                            
//#line 63 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256443076 =
                                              ((dk4211643072) * (((double)(dk4211643072))));
                                            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256543077 =
                                              ((t4256343075) + (((double)(t4256443076))));
                                            
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
ret4211743086 = t4256543077;
                                            
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256643087 =
                                              ret4211743086;
                                            
//#line 67 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4256843088 =
                                              java.lang.Math.sqrt(((double)(t4256643087)));
                                            
//#line 276 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double pairEnergy43089 =
                                              ((t4256743082) / (((double)(t4256843088))));
                                            
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4256943090 =
                                              thisPlaceEnergy;
                                            
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4257043091 =
                                              ((t4256943090) + (((double)(pairEnergy43089))));
                                            
//#line 277 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
thisPlaceEnergy = t4257043091;
                                            
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4255043093 =
                                              i2474843095;
                                            
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4255143094 =
                                              ((t4255043093) + (((int)(1))));
                                            
//#line 274 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
i2474843095 = t4255143094;
                                        }
                                        
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4254543103 =
                                          i2476443105;
                                        
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4254643104 =
                                          ((t4254543103) + (((int)(1))));
                                        
//#line 271 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
i2476443105 = t4254643104;
                                    }
                                    
//#line 282 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Array<x10.array.Point> uList43229 =
                                      ((x10.array.Array)(box143227.getUList()));
                                    
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4257343220 =
                                      ((x10.array.Array<x10.array.Point>)uList43229).
                                        size;
                                    
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i24812max2481443221 =
                                      ((t4257343220) - (((int)(1))));
                                    
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int i2481243212 =
                                      0;
                                    
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                                           true;
                                                                                                                                           ) {
                                        
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4257543213 =
                                          i2481243212;
                                        
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4263943214 =
                                          ((t4257543213) <= (((int)(i24812max2481443221))));
                                        
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4263943214)) {
                                            
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                                        }
                                        
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int p43209 =
                                          i2481243212;
                                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04211943197 =
                                          p43209;
                                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Point ret4212043198 =
                                           null;
                                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4212143199: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Point> t4257843200 =
                                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Point>)uList43229).
                                                                           raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Point t4257943201 =
                                          ((x10.array.Point)(((x10.array.Point[])t4257843200.value)[i04211943197]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4212043198 = ((x10.array.Point)(t4257943201));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4212143199;}
                                        
//#line 284 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point boxIndex243202 =
                                          ((x10.array.Point)(ret4212043198));
                                        
//#line 286 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int x243203 =
                                          boxIndex243202.$apply$O((int)(0));
                                        
//#line 287 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int y243204 =
                                          boxIndex243202.$apply$O((int)(1));
                                        
//#line 288 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int z243205 =
                                          boxIndex243202.$apply$O((int)(2));
                                        
//#line 289 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Array<au.edu.anu.chem.PointCharge> boxAtoms43206 =
                                          ((x10.array.Array)(((x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>)cachedAtoms).$apply$G((int)(x243203),
                                                                                                                                                       (int)(y243204),
                                                                                                                                                       (int)(z243205))));
                                        
//#line 290 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4263843207 =
                                          ((boxAtoms43206) != (null));
                                        
//#line 290 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t4263843207) {
                                            
//#line 291 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Vector3d translation43208 =
                                              au.edu.anu.mm.PeriodicFmm3d.getTranslation((int)(this.
                                                                                                 lowestLevelDim),
                                                                                         (double)(this.
                                                                                                    size),
                                                                                         (int)(x243203),
                                                                                         (int)(y243204),
                                                                                         (int)(z243205));
                                            
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4258043195 =
                                              ((x10.array.Array<au.edu.anu.chem.PointCharge>)boxAtoms43206).
                                                size;
                                            
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i24796max2479843196 =
                                              ((t4258043195) - (((int)(1))));
                                            
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int i2479643191 =
                                              0;
                                            
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                                                   true;
                                                                                                                                                   ) {
                                                
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4258243192 =
                                                  i2479643191;
                                                
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4263743193 =
                                                  ((t4258243192) <= (((int)(i24796max2479843196))));
                                                
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4263743193)) {
                                                    
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                                                }
                                                
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int otherBoxAtomIndex43188 =
                                                  i2479643191;
                                                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i04212743177 =
                                                  otherBoxAtomIndex43188;
                                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
au.edu.anu.chem.PointCharge ret4212843178 =
                                                   null;
                                                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret4212943179: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<au.edu.anu.chem.PointCharge> t4258543180 =
                                                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<au.edu.anu.chem.PointCharge>)boxAtoms43206).
                                                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final au.edu.anu.chem.PointCharge t4258643181 =
                                                  ((au.edu.anu.chem.PointCharge)(((au.edu.anu.chem.PointCharge[])t4258543180.value)[i04212743177]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret4212843178 = t4258643181;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret4212943179;}
                                                
//#line 293 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.chem.PointCharge atom243182 =
                                                  ret4212843178;
                                                
//#line 294 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this4213643183 =
                                                  ((x10x.vector.Point3d)(atom243182.
                                                                           centre));
                                                
//#line 31 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d that4213543184 =
                                                  ((x10x.vector.Vector3d)(translation43208));
                                                
//#line 27 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Vector3d b4213743185 =
                                                  ((x10x.vector.Vector3d)(that4213543184));
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d alloc242524213843186 =
                                                  new x10x.vector.Point3d((java.lang.System[]) null);
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4258743163 =
                                                  this4213643183.
                                                    i;
                                                
//#line 24 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4258843164 =
                                                  b4213743185.
                                                    i;
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4259343165 =
                                                  ((t4258743163) + (((double)(t4258843164))));
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4258943166 =
                                                  this4213643183.
                                                    j;
                                                
//#line 25 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4259043167 =
                                                  b4213743185.
                                                    j;
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4259443168 =
                                                  ((t4258943166) + (((double)(t4259043167))));
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4259143169 =
                                                  this4213643183.
                                                    k;
                                                
//#line 26 ... "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Vector3d.x10"
final double t4259243170 =
                                                  b4213743185.
                                                    k;
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4259543171 =
                                                  ((t4259143169) + (((double)(t4259243170))));
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10.util.concurrent.OrderedLock t4259643172 =
                                                  x10.util.concurrent.OrderedLock.createNewLock();
                                                
//#line 28 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
alloc242524213843186.$init(t4259343165,
                                                                                                                                                                           t4259443168,
                                                                                                                                                                           t4259543171,
                                                                                                                                                                           t4259643172);
                                                
//#line 294 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d translatedCentre43187 =
                                                  alloc242524213843186;
                                                
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> t4259743174 =
                                                  ((x10.util.ArrayList)(box143227.
                                                                          atoms));
                                                
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4259843175 =
                                                  ((x10.util.ArrayList<au.edu.anu.chem.PointCharge>)t4259743174).size$O();
                                                
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int i24780max2478243176 =
                                                  ((t4259843175) - (((int)(1))));
                                                
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
int i2478043160 =
                                                  0;
                                                
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (;
                                                                                                                                                       true;
                                                                                                                                                       ) {
                                                    
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4260043161 =
                                                      i2478043160;
                                                    
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4263643162 =
                                                      ((t4260043161) <= (((int)(i24780max2478243176))));
                                                    
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (!(t4263643162)) {
                                                        
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
break;
                                                    }
                                                    
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int atomIndex143157 =
                                                      i2478043160;
                                                    
//#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> t4260343136 =
                                                      ((x10.util.ArrayList)(box143227.
                                                                              atoms));
                                                    
//#line 296 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final au.edu.anu.chem.PointCharge atom143137 =
                                                      ((au.edu.anu.chem.PointCharge)(((x10.util.ArrayList<au.edu.anu.chem.PointCharge>)t4260343136).$apply$G((int)(atomIndex143157))));
                                                    
//#line 297 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this4214043138 =
                                                      ((x10x.vector.Point3d)(atom143137.
                                                                               centre));
                                                    
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b4213943139 =
                                                      ((x10x.vector.Point3d)(translatedCentre43187));
                                                    
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b4214143140 =
                                                      ((x10x.vector.Point3d)(b4213943139));
                                                    
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
double ret4214543141 =
                                                       0;
                                                    
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4260443122 =
                                                      this4214043138.
                                                        i;
                                                    
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4260543123 =
                                                      b4214143140.
                                                        i;
                                                    
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di4214243124 =
                                                      ((t4260443122) - (((double)(t4260543123))));
                                                    
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4260643125 =
                                                      this4214043138.
                                                        j;
                                                    
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4260743126 =
                                                      b4214143140.
                                                        j;
                                                    
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj4214343127 =
                                                      ((t4260643125) - (((double)(t4260743126))));
                                                    
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4260843128 =
                                                      this4214043138.
                                                        k;
                                                    
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4260943129 =
                                                      b4214143140.
                                                        k;
                                                    
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk4214443130 =
                                                      ((t4260843128) - (((double)(t4260943129))));
                                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261043131 =
                                                      ((di4214243124) * (((double)(di4214243124))));
                                                    
//#line 62 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261143132 =
                                                      ((dj4214343127) * (((double)(dj4214343127))));
                                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261243133 =
                                                      ((t4261043131) + (((double)(t4261143132))));
                                                    
//#line 63 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261343134 =
                                                      ((dk4214443130) * (((double)(dk4214443130))));
                                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261443135 =
                                                      ((t4261243133) + (((double)(t4261343134))));
                                                    
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
ret4214543141 = t4261443135;
                                                    
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261543142 =
                                                      ret4214543141;
                                                    
//#line 297 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double distance43143 =
                                                      java.lang.Math.sqrt(((double)(t4261543142)));
                                                    
//#line 298 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final boolean t4263543144 =
                                                      ((double) distance43143) !=
                                                    ((double) 0.0);
                                                    
//#line 298 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
if (t4263543144) {
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4263243145 =
                                                          thisPlaceEnergy;
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4261643146 =
                                                          atom143137.
                                                            charge;
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4261743147 =
                                                          atom243182.
                                                            charge;
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4263043148 =
                                                          ((t4261643146) * (((double)(t4261743147))));
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10x.vector.Point3d this4214843149 =
                                                          ((x10x.vector.Point3d)(atom143137.
                                                                                   centre));
                                                        
//#line 66 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b4214743150 =
                                                          ((x10x.vector.Point3d)(translatedCentre43187));
                                                        
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final x10x.vector.Point3d b4214943151 =
                                                          ((x10x.vector.Point3d)(b4214743150));
                                                        
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
double ret4215343152 =
                                                           0;
                                                        
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261843108 =
                                                          this4214843149.
                                                            i;
                                                        
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4261943109 =
                                                          b4214943151.
                                                            i;
                                                        
//#line 58 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double di4215043110 =
                                                          ((t4261843108) - (((double)(t4261943109))));
                                                        
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262043111 =
                                                          this4214843149.
                                                            j;
                                                        
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262143112 =
                                                          b4214943151.
                                                            j;
                                                        
//#line 59 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dj4215143113 =
                                                          ((t4262043111) - (((double)(t4262143112))));
                                                        
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262243114 =
                                                          this4214843149.
                                                            k;
                                                        
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262343115 =
                                                          b4214943151.
                                                            k;
                                                        
//#line 60 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double dk4215243116 =
                                                          ((t4262243114) - (((double)(t4262343115))));
                                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262443117 =
                                                          ((di4215043110) * (((double)(di4215043110))));
                                                        
//#line 62 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262543118 =
                                                          ((dj4215143113) * (((double)(dj4215143113))));
                                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262643119 =
                                                          ((t4262443117) + (((double)(t4262543118))));
                                                        
//#line 63 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262743120 =
                                                          ((dk4215243116) * (((double)(dk4215243116))));
                                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262843121 =
                                                          ((t4262643119) + (((double)(t4262743120))));
                                                        
//#line 61 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
ret4215343152 = t4262843121;
                                                        
//#line 57 .. "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4262943153 =
                                                          ret4215343152;
                                                        
//#line 67 . "/home/zhangsa/x10/x10programs/anuchem/x10x.lib/xla/src/x10x/vector/Point3d.x10"
final double t4263143154 =
                                                          java.lang.Math.sqrt(((double)(t4262943153)));
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4263343155 =
                                                          ((t4263043148) / (((double)(t4263143154))));
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t4263443156 =
                                                          ((t4263243145) + (((double)(t4263343155))));
                                                        
//#line 299 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
thisPlaceEnergy = t4263443156;
                                                    }
                                                    
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4260143158 =
                                                      i2478043160;
                                                    
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4260243159 =
                                                      ((t4260143158) + (((int)(1))));
                                                    
//#line 295 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
i2478043160 = t4260243159;
                                                }
                                                
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4258343189 =
                                                  i2479643191;
                                                
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4258443190 =
                                                  ((t4258343189) + (((int)(1))));
                                                
//#line 292 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
i2479643191 = t4258443190;
                                            }
                                        }
                                        
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4257643210 =
                                          i2481243212;
                                        
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final int t4257743211 =
                                          ((t4257643210) + (((int)(1))));
                                        
//#line 283 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
i2481243212 = t4257743211;
                                    }
                                }
                            }
                            
//#line 307 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final double t42642 =
                              thisPlaceEnergy;
                            
//#line 307 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.<x10.core.Double>makeOffer_0_$$x10$lang$Runtime_T(x10.rtt.Types.DOUBLE, x10.core.Double.$box(t42642));
                        }
                        
                        public x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree>
                          locallyEssentialTrees;
                        public x10.array.Point
                          p1;
                        public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                          lowestLevelBoxes;
                        public int
                          lowestLevelDim;
                        public double
                          size;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.PeriodicFmm3d.$Closure$24 $make(final x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree> locallyEssentialTrees,
                                                                                    final x10.array.Point p1,
                                                                                    final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                                                                    final int lowestLevelDim,
                                                                                    final double size,java.lang.Class<?> $dummy0){return new $Closure$24(locallyEssentialTrees,p1,lowestLevelBoxes,lowestLevelDim,size,(java.lang.Class<?>) null);}
                        public $Closure$24(final x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree> locallyEssentialTrees,
                                           final x10.array.Point p1,
                                           final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                           final int lowestLevelDim,
                                           final double size,java.lang.Class<?> $dummy0) { {
                                                                                                  this.locallyEssentialTrees = ((x10.array.DistArray)(locallyEssentialTrees));
                                                                                                  this.p1 = ((x10.array.Point)(p1));
                                                                                                  this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                  this.lowestLevelDim = lowestLevelDim;
                                                                                                  this.size = size;
                                                                                              }}
                        
                    }
                    
                public static class $Closure$25
                extends x10.core.Ref
                  implements x10.core.fun.VoidFun_0_0,
                              x10.x10rt.X10JavaSerializable 
                {
                    private static final long serialVersionUID = 1L;
                    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_SIMPLE_ASYNC, $Closure$25.class);
                    
                    public static final x10.rtt.RuntimeType<$Closure$25> $RTT = new x10.rtt.StaticVoidFunType<$Closure$25>(
                    /* base class */$Closure$25.class
                    , /* parents */ new x10.rtt.Type[] {x10.core.fun.VoidFun_0_0.$RTT, x10.rtt.Types.OBJECT}
                    );
                    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
                    
                    
                    public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$25 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
                    
                        x10.array.Dist __lowerer__var__13__ = (x10.array.Dist) $deserializer.readRef();
                        $_obj.__lowerer__var__13__ = __lowerer__var__13__;
                        x10.array.DistArray locallyEssentialTrees = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.locallyEssentialTrees = locallyEssentialTrees;
                        x10.array.DistArray lowestLevelBoxes = (x10.array.DistArray) $deserializer.readRef();
                        $_obj.lowestLevelBoxes = lowestLevelBoxes;
                        $_obj.lowestLevelDim = $deserializer.readInt();
                        $_obj.size = $deserializer.readDouble();
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
                    
                        if (__lowerer__var__13__ instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.__lowerer__var__13__);
                        } else {
                        $serializer.write(this.__lowerer__var__13__);
                        }
                        if (locallyEssentialTrees instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.locallyEssentialTrees);
                        } else {
                        $serializer.write(this.locallyEssentialTrees);
                        }
                        if (lowestLevelBoxes instanceof x10.x10rt.X10JavaSerializable) {
                        $serializer.write( (x10.x10rt.X10JavaSerializable) this.lowestLevelBoxes);
                        } else {
                        $serializer.write(this.lowestLevelBoxes);
                        }
                        $serializer.write(this.lowestLevelDim);
                        $serializer.write(this.size);
                        
                    }
                    
                    // constructor just for allocation
                    public $Closure$25(final java.lang.System[] $dummy) { 
                    super($dummy);
                    }
                    
                        
                        public void
                          $apply(
                          ){
                            
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
for (
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.lang.Iterator<x10.array.Point> p143300 =
                                                                                                                                     this.
                                                                                                                                       __lowerer__var__13__.restriction(((x10.lang.Place)(x10.lang.Runtime.home()))).
                                                                                                                                       region.iterator();
                                                                                                                                   ((x10.lang.Iterator<x10.array.Point>)p143300).hasNext$O();
                                                                                                                                   ) {
                                
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
final x10.array.Point p1 =
                                  ((x10.array.Point)(((x10.lang.Iterator<x10.array.Point>)p143300).next$G()));
                                
//#line 264 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/PeriodicFmm3d.x10"
x10.lang.Runtime.runAsync(((x10.lang.Place)(x10.lang.Runtime.home())),
                                                                                                                                                            ((x10.core.fun.VoidFun_0_0)(new au.edu.anu.mm.PeriodicFmm3d.$Closure$24(this.
                                                                                                                                                                                                                                      locallyEssentialTrees,
                                                                                                                                                                                                                                    p1,
                                                                                                                                                                                                                                    this.
                                                                                                                                                                                                                                      lowestLevelBoxes,
                                                                                                                                                                                                                                    this.
                                                                                                                                                                                                                                      lowestLevelDim,
                                                                                                                                                                                                                                    this.
                                                                                                                                                                                                                                      size,(java.lang.Class<?>) null))));
                            }
                        }
                        
                        public x10.array.Dist
                          __lowerer__var__13__;
                        public x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree>
                          locallyEssentialTrees;
                        public x10.array.DistArray<au.edu.anu.mm.FmmBox>
                          lowestLevelBoxes;
                        public int
                          lowestLevelDim;
                        public double
                          size;
                        
                        // creation method for java code
                        public static au.edu.anu.mm.PeriodicFmm3d.$Closure$25 $make(final x10.array.Dist __lowerer__var__13__,
                                                                                    final x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree> locallyEssentialTrees,
                                                                                    final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                                                                    final int lowestLevelDim,
                                                                                    final double size,java.lang.Class<?> $dummy0){return new $Closure$25(__lowerer__var__13__,locallyEssentialTrees,lowestLevelBoxes,lowestLevelDim,size,(java.lang.Class<?>) null);}
                        public $Closure$25(final x10.array.Dist __lowerer__var__13__,
                                           final x10.array.DistArray<au.edu.anu.mm.LocallyEssentialTree> locallyEssentialTrees,
                                           final x10.array.DistArray<au.edu.anu.mm.FmmBox> lowestLevelBoxes,
                                           final int lowestLevelDim,
                                           final double size,java.lang.Class<?> $dummy0) { {
                                                                                                  this.__lowerer__var__13__ = ((x10.array.Dist)(__lowerer__var__13__));
                                                                                                  this.locallyEssentialTrees = ((x10.array.DistArray)(locallyEssentialTrees));
                                                                                                  this.lowestLevelBoxes = ((x10.array.DistArray)(lowestLevelBoxes));
                                                                                                  this.lowestLevelDim = lowestLevelDim;
                                                                                                  this.size = size;
                                                                                              }}
                        
                    }
                    
                
                }
                