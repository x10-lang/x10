package au.edu.anu.mm;


public class LocallyEssentialTree
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, LocallyEssentialTree.class);
    
    public static final x10.rtt.RuntimeType<LocallyEssentialTree> $RTT = new x10.rtt.NamedType<LocallyEssentialTree>(
    "au.edu.anu.mm.LocallyEssentialTree", /* base class */LocallyEssentialTree.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(LocallyEssentialTree $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10.array.Array combinedUList = (x10.array.Array) $deserializer.readRef();
        $_obj.combinedUList = combinedUList;
        x10.array.Array combinedVList = (x10.array.Array) $deserializer.readRef();
        $_obj.combinedVList = combinedVList;
        x10.array.Array uListMin = (x10.array.Array) $deserializer.readRef();
        $_obj.uListMin = uListMin;
        x10.array.Array uListMax = (x10.array.Array) $deserializer.readRef();
        $_obj.uListMax = uListMax;
        x10.array.Array vListMin = (x10.array.Array) $deserializer.readRef();
        $_obj.vListMin = vListMin;
        x10.array.Array vListMax = (x10.array.Array) $deserializer.readRef();
        $_obj.vListMax = vListMax;
        x10.array.Array multipoleCopies = (x10.array.Array) $deserializer.readRef();
        $_obj.multipoleCopies = multipoleCopies;
        x10.array.DistArray cachedAtoms = (x10.array.DistArray) $deserializer.readRef();
        $_obj.cachedAtoms = cachedAtoms;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        LocallyEssentialTree $_obj = new LocallyEssentialTree((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (combinedUList instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.combinedUList);
        } else {
        $serializer.write(this.combinedUList);
        }
        if (combinedVList instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.combinedVList);
        } else {
        $serializer.write(this.combinedVList);
        }
        if (uListMin instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.uListMin);
        } else {
        $serializer.write(this.uListMin);
        }
        if (uListMax instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.uListMax);
        } else {
        $serializer.write(this.uListMax);
        }
        if (vListMin instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.vListMin);
        } else {
        $serializer.write(this.vListMin);
        }
        if (vListMax instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.vListMax);
        } else {
        $serializer.write(this.vListMax);
        }
        if (multipoleCopies instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.multipoleCopies);
        } else {
        $serializer.write(this.multipoleCopies);
        }
        if (cachedAtoms instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.cachedAtoms);
        } else {
        $serializer.write(this.cachedAtoms);
        }
        
    }
    
    // constructor just for allocation
    public LocallyEssentialTree(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public int
          X10$object_lock_id0;
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                  getOrderedLock(
                                                                                                                  ){
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t67593 =
              this.
                X10$object_lock_id0;
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.util.concurrent.OrderedLock t67594 =
              x10.util.concurrent.OrderedLock.getLock((int)(t67593));
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
return t67594;
        }
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                  getStaticOrderedLock(
                                                                                                                  ){
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t67595 =
              au.edu.anu.mm.LocallyEssentialTree.X10$class_lock_id1;
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.util.concurrent.OrderedLock t67596 =
              x10.util.concurrent.OrderedLock.getLock((int)(t67595));
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
return t67596;
        }
        
        
//#line 22 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public x10.array.Array<x10.array.Point>
          combinedUList;
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public x10.array.Array<x10.array.Array<x10.array.Point>>
          combinedVList;
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public x10.array.Array<x10.core.Int>
          uListMin;
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public x10.array.Array<x10.core.Int>
          uListMax;
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public x10.array.Array<x10.array.Array<x10.core.Int>>
          vListMin;
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
public x10.array.Array<x10.array.Array<x10.core.Int>>
          vListMax;
        
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
/**
     * A cache of multipole copies for the combined V-list of all
     * boxes at this place.  Used to overlap fetching of the multipole
     * expansions with other computation.
     * The Array has one element for each level; each element
     * holds the portion of the combined V-list for that level.
     */public x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>
          multipoleCopies;
        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
/**
     * A cache of PointCharge for the combined U-list of all
     * boxes at this place.  Used to store fetched atoms from 
     * non-well-separated boxes for use in direct evaluations 
     * with all atoms at a given place.
     */public x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>
          cachedAtoms;
        
        
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
// creation method for java code
        public static au.edu.anu.mm.LocallyEssentialTree $make(final x10.array.Array<x10.array.Point> combinedUList,
                                                               final x10.array.Array<x10.array.Array<x10.array.Point>> combinedVList,
                                                               final x10.array.Array<x10.core.Int> uListMin,
                                                               final x10.array.Array<x10.core.Int> uListMax,
                                                               final x10.array.Array<x10.array.Array<x10.core.Int>> vListMin,
                                                               final x10.array.Array<x10.array.Array<x10.core.Int>> vListMax,java.lang.Class<?> $dummy0){return new au.edu.anu.mm.LocallyEssentialTree((java.lang.System[]) null).$init(combinedUList,combinedVList,uListMin,uListMax,vListMin,vListMax,(java.lang.Class<?>) null);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.LocallyEssentialTree au$edu$anu$mm$LocallyEssentialTree$$init$S(final x10.array.Array<x10.array.Point> combinedUList,
                                                                                                   final x10.array.Array<x10.array.Array<x10.array.Point>> combinedVList,
                                                                                                   final x10.array.Array<x10.core.Int> uListMin,
                                                                                                   final x10.array.Array<x10.core.Int> uListMax,
                                                                                                   final x10.array.Array<x10.array.Array<x10.core.Int>> vListMin,
                                                                                                   final x10.array.Array<x10.array.Array<x10.core.Int>> vListMax,java.lang.Class<?> $dummy0) { {
                                                                                                                                                                                                      
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"

                                                                                                                                                                                                      
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"

                                                                                                                                                                                                      
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final au.edu.anu.mm.LocallyEssentialTree this6706067898 =
                                                                                                                                                                                                        this;
                                                                                                                                                                                                      
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this6706067898.X10$object_lock_id0 = -1;
                                                                                                                                                                                                      
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.combinedUList = ((x10.array.Array)(combinedUList));
                                                                                                                                                                                                      
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.combinedVList = ((x10.array.Array)(combinedVList));
                                                                                                                                                                                                      
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.uListMin = ((x10.array.Array)(uListMin));
                                                                                                                                                                                                      
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.uListMax = ((x10.array.Array)(uListMax));
                                                                                                                                                                                                      
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.vListMin = ((x10.array.Array)(vListMin));
                                                                                                                                                                                                      
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.vListMax = ((x10.array.Array)(vListMax));
                                                                                                                                                                                                      
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>> multipoleCopies =
                                                                                                                                                                                                        ((x10.array.Array)(new x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.DistArray.$RTT, au.edu.anu.mm.MultipoleExpansion.$RTT))));
                                                                                                                                                                                                      
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size67063 =
                                                                                                                                                                                                        ((x10.array.Array<x10.array.Array<x10.array.Point>>)combinedVList).
                                                                                                                                                                                                          size;
                                                                                                                                                                                                      
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.x10$lang$Object$$init$S();
                                                                                                                                                                                                      
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199706706667899 =
                                                                                                                                                                                                        ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                                                                                                                                                                                                      
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6759767780 =
                                                                                                                                                                                                        ((size67063) - (((int)(1))));
                                                                                                                                                                                                      
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199706706667899.$init(((int)(0)),
                                                                                                                                                                                                                                                                                                                                 t6759767780);
                                                                                                                                                                                                      
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__670656707067900 =
                                                                                                                                                                                                        ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                                                                                              alloc199706706667899)));
                                                                                                                                                                                                      
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6707167901 =
                                                                                                                                                                                                         null;
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6759867781 =
                                                                                                                                                                                                        __desugarer__var__5__670656707067900.
                                                                                                                                                                                                          rank;
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6760067782 =
                                                                                                                                                                                                        ((int) t6759867781) ==
                                                                                                                                                                                                      ((int) 1);
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6760067782) {
                                                                                                                                                                                                          
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6759967783 =
                                                                                                                                                                                                            __desugarer__var__5__670656707067900.
                                                                                                                                                                                                              zeroBased;
                                                                                                                                                                                                          
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6760067782 = ((boolean) t6759967783) ==
                                                                                                                                                                                                          ((boolean) true);
                                                                                                                                                                                                      }
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6760267784 =
                                                                                                                                                                                                        t6760067782;
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6760267784) {
                                                                                                                                                                                                          
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6760167785 =
                                                                                                                                                                                                            __desugarer__var__5__670656707067900.
                                                                                                                                                                                                              rect;
                                                                                                                                                                                                          
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6760267784 = ((boolean) t6760167785) ==
                                                                                                                                                                                                          ((boolean) true);
                                                                                                                                                                                                      }
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6760467786 =
                                                                                                                                                                                                        t6760267784;
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6760467786) {
                                                                                                                                                                                                          
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6760367787 =
                                                                                                                                                                                                            __desugarer__var__5__670656707067900.
                                                                                                                                                                                                              rail;
                                                                                                                                                                                                          
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6760467786 = ((boolean) t6760367787) ==
                                                                                                                                                                                                          ((boolean) true);
                                                                                                                                                                                                      }
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6760567788 =
                                                                                                                                                                                                        t6760467786;
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6760567788) {
                                                                                                                                                                                                          
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6760567788 = ((__desugarer__var__5__670656707067900) != (null));
                                                                                                                                                                                                      }
                                                                                                                                                                                                      
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6760667789 =
                                                                                                                                                                                                        t6760567788;
                                                                                                                                                                                                      
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6760967790 =
                                                                                                                                                                                                        !(t6760667789);
                                                                                                                                                                                                      
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6760967790) {
                                                                                                                                                                                                          
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6760867791 =
                                                                                                                                                                                                            true;
                                                                                                                                                                                                          
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6760867791) {
                                                                                                                                                                                                              
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6760767792 =
                                                                                                                                                                                                                new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                                                                                                                                                                                              
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6760767792;
                                                                                                                                                                                                          }
                                                                                                                                                                                                      }
                                                                                                                                                                                                      
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6707167901 = ((x10.array.Region)(__desugarer__var__5__670656707067900));
                                                                                                                                                                                                      
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg6706467902 =
                                                                                                                                                                                                        ((x10.array.Region)(ret6707167901));
                                                                                                                                                                                                      
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.region = ((x10.array.Region)(myReg6706467902));
                                                                                                                                                                                                      
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.rank = 1;
                                                                                                                                                                                                      
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.rect = true;
                                                                                                                                                                                                      
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.zeroBased = true;
                                                                                                                                                                                                      
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.rail = true;
                                                                                                                                                                                                      
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.size = size67063;
                                                                                                                                                                                                      
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199716706767903 =
                                                                                                                                                                                                        new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                                                                                                                      
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max06707467904 =
                                                                                                                                                                                                        ((size67063) - (((int)(1))));
                                                                                                                                                                                                      
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.rank = 1;
                                                                                                                                                                                                      
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.min0 = 0;
                                                                                                                                                                                                      
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6761067793 =
                                                                                                                                                                                                        ((_max06707467904) - (((int)(0))));
                                                                                                                                                                                                      
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6761167794 =
                                                                                                                                                                                                        ((t6761067793) + (((int)(1))));
                                                                                                                                                                                                      
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.delta0 = t6761167794;
                                                                                                                                                                                                      
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6761267795 =
                                                                                                                                                                                                        alloc199716706767903.
                                                                                                                                                                                                          delta0;
                                                                                                                                                                                                      
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t6761367796 =
                                                                                                                                                                                                        ((t6761267795) > (((int)(0))));
                                                                                                                                                                                                      
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t6761467797 =
                                                                                                                                                                                                         0;
                                                                                                                                                                                                      
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t6761367796) {
                                                                                                                                                                                                          
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6761467797 = alloc199716706767903.
                                                                                                                                                                                                                                                                                                                                delta0;
                                                                                                                                                                                                      } else {
                                                                                                                                                                                                          
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6761467797 = 0;
                                                                                                                                                                                                      }
                                                                                                                                                                                                      
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6761567798 =
                                                                                                                                                                                                        t6761467797;
                                                                                                                                                                                                      
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.size = t6761567798;
                                                                                                                                                                                                      
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.min1 = 0;
                                                                                                                                                                                                      
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.delta1 = 0;
                                                                                                                                                                                                      
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.min2 = 0;
                                                                                                                                                                                                      
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.delta2 = 0;
                                                                                                                                                                                                      
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.min3 = 0;
                                                                                                                                                                                                      
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.delta3 = 0;
                                                                                                                                                                                                      
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.min = null;
                                                                                                                                                                                                      
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716706767903.delta = null;
                                                                                                                                                                                                      
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.layout = ((x10.array.RectLayout)(alloc199716706767903));
                                                                                                                                                                                                      
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6707667905 =
                                                                                                                                                                                                        ((x10.array.RectLayout)(((x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>)multipoleCopies).
                                                                                                                                                                                                                                  layout));
                                                                                                                                                                                                      
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6706867906 =
                                                                                                                                                                                                        this6707667905.
                                                                                                                                                                                                          size;
                                                                                                                                                                                                      
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>> t6761667907 =
                                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>allocate(new x10.rtt.ParameterizedType(x10.array.DistArray.$RTT, au.edu.anu.mm.MultipoleExpansion.$RTT), ((int)(n6706867906)), true)));
                                                                                                                                                                                                      
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.raw = ((x10.core.IndexedMemoryChunk)(t6761667907));
                                                                                                                                                                                                      
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6761767909 =
                                                                                                                                                                                                        ((x10.array.Array<x10.array.Array<x10.array.Point>>)combinedVList).
                                                                                                                                                                                                          size;
                                                                                                                                                                                                      
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int i50107max5010967910 =
                                                                                                                                                                                                        ((t6761767909) - (((int)(1))));
                                                                                                                                                                                                      
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
int i5010767894 =
                                                                                                                                                                                                        0;
                                                                                                                                                                                                      
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
for (;
                                                                                                                                                                                                                                                                                                                   true;
                                                                                                                                                                                                                                                                                                                   ) {
                                                                                                                                                                                                          
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6761967895 =
                                                                                                                                                                                                            i5010767894;
                                                                                                                                                                                                          
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final boolean t6766367896 =
                                                                                                                                                                                                            ((t6761967895) <= (((int)(i50107max5010967910))));
                                                                                                                                                                                                          
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
if (!(t6766367896)) {
                                                                                                                                                                                                              
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
break;
                                                                                                                                                                                                          }
                                                                                                                                                                                                          
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int i67891 =
                                                                                                                                                                                                            i5010767894;
                                                                                                                                                                                                          
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06707767806 =
                                                                                                                                                                                                            i67891;
                                                                                                                                                                                                          
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Point> ret6707867807 =
                                                                                                                                                                                                             null;
                                                                                                                                                                                                          
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6707967808: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Point>> t6762267809 =
                                                                                                                                                                                                            ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Point>>)combinedVList).
                                                                                                                                                                                                                                             raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Point> t6762367810 =
                                                                                                                                                                                                            ((x10.array.Array)(((x10.array.Array[])t6762267809.value)[i06707767806]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6707867807 = ((x10.array.Array)(t6762367810));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6707967808;}
                                                                                                                                                                                                          
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Point> t6762467811 =
                                                                                                                                                                                                            ((x10.array.Array)(ret6707867807));
                                                                                                                                                                                                          
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final boolean t6766267812 =
                                                                                                                                                                                                            ((t6762467811) != (null));
                                                                                                                                                                                                          
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
if (t6766267812) {
                                                                                                                                                                                                              
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06708567813 =
                                                                                                                                                                                                                i67891;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6708667814 =
                                                                                                                                                                                                                 null;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6708767815: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6762567816 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMin).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6762667817 =
                                                                                                                                                                                                                ((x10.array.Array)(((x10.array.Array[])t6762567816.value)[i06708567813]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6708667814 = ((x10.array.Array)(t6762667817));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6708767815;}
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6709467818 =
                                                                                                                                                                                                                ((x10.array.Array)(ret6708667814));
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6709567819 =
                                                                                                                                                                                                                 0;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6709667820: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6762767821 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6709467818).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6762867822 =
                                                                                                                                                                                                                ((int[])t6762767821.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6709567819 = t6762867822;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6709667820;}
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6763367823 =
                                                                                                                                                                                                                ret6709567819;
                                                                                                                                                                                                              
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06710267824 =
                                                                                                                                                                                                                i67891;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6710367825 =
                                                                                                                                                                                                                 null;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6710467826: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6762967827 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMax).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6763067828 =
                                                                                                                                                                                                                ((x10.array.Array)(((x10.array.Array[])t6762967827.value)[i06710267824]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6710367825 = ((x10.array.Array)(t6763067828));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6710467826;}
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6711167829 =
                                                                                                                                                                                                                ((x10.array.Array)(ret6710367825));
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6711267830 =
                                                                                                                                                                                                                 0;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6711367831: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6763167832 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6711167829).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6763267833 =
                                                                                                                                                                                                                ((int[])t6763167832.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6711267830 = t6763267833;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6711367831;}
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6763467834 =
                                                                                                                                                                                                                ret6711267830;
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t6764567835 =
                                                                                                                                                                                                                ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t6763367823)), ((int)(t6763467834)))));
                                                                                                                                                                                                              
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06711967836 =
                                                                                                                                                                                                                i67891;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6712067837 =
                                                                                                                                                                                                                 null;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6712167838: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6763567839 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMin).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6763667840 =
                                                                                                                                                                                                                ((x10.array.Array)(((x10.array.Array[])t6763567839.value)[i06711967836]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6712067837 = ((x10.array.Array)(t6763667840));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6712167838;}
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6712867841 =
                                                                                                                                                                                                                ((x10.array.Array)(ret6712067837));
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6712967842 =
                                                                                                                                                                                                                 0;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6713067843: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6763767844 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6712867841).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6763867845 =
                                                                                                                                                                                                                ((int[])t6763767844.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6712967842 = t6763867845;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6713067843;}
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6764367846 =
                                                                                                                                                                                                                ret6712967842;
                                                                                                                                                                                                              
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06713667847 =
                                                                                                                                                                                                                i67891;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6713767848 =
                                                                                                                                                                                                                 null;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6713867849: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6763967850 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMax).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6764067851 =
                                                                                                                                                                                                                ((x10.array.Array)(((x10.array.Array[])t6763967850.value)[i06713667847]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6713767848 = ((x10.array.Array)(t6764067851));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6713867849;}
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6714567852 =
                                                                                                                                                                                                                ((x10.array.Array)(ret6713767848));
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6714667853 =
                                                                                                                                                                                                                 0;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6714767854: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6764167855 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6714567852).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6764267856 =
                                                                                                                                                                                                                ((int[])t6764167855.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6714667853 = t6764267856;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6714767854;}
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6764467857 =
                                                                                                                                                                                                                ret6714667853;
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t6764667858 =
                                                                                                                                                                                                                ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t6764367846)), ((int)(t6764467857)))));
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t6765867859 =
                                                                                                                                                                                                                ((x10.array.Region)(t6764567835.$times(((x10.lang.IntRange)(t6764667858)))));
                                                                                                                                                                                                              
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06715367860 =
                                                                                                                                                                                                                i67891;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6715467861 =
                                                                                                                                                                                                                 null;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6715567862: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6764767863 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMin).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6764867864 =
                                                                                                                                                                                                                ((x10.array.Array)(((x10.array.Array[])t6764767863.value)[i06715367860]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6715467861 = ((x10.array.Array)(t6764867864));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6715567862;}
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6716267865 =
                                                                                                                                                                                                                ((x10.array.Array)(ret6715467861));
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6716367866 =
                                                                                                                                                                                                                 0;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6716467867: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6764967868 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6716267865).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6765067869 =
                                                                                                                                                                                                                ((int[])t6764967868.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6716367866 = t6765067869;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6716467867;}
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6765567870 =
                                                                                                                                                                                                                ret6716367866;
                                                                                                                                                                                                              
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06717067871 =
                                                                                                                                                                                                                i67891;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6717167872 =
                                                                                                                                                                                                                 null;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6717267873: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6765167874 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMax).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6765267875 =
                                                                                                                                                                                                                ((x10.array.Array)(((x10.array.Array[])t6765167874.value)[i06717067871]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6717167872 = ((x10.array.Array)(t6765267875));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6717267873;}
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6717967876 =
                                                                                                                                                                                                                ((x10.array.Array)(ret6717167872));
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6718067877 =
                                                                                                                                                                                                                 0;
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6718167878: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6765367879 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6717967876).
                                                                                                                                                                                                                                                 raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6765467880 =
                                                                                                                                                                                                                ((int[])t6765367879.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6718067877 = t6765467880;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6718167878;}
                                                                                                                                                                                                              
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6765667881 =
                                                                                                                                                                                                                ret6718067877;
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t6765767882 =
                                                                                                                                                                                                                ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t6765567870)), ((int)(t6765667881)))));
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t6765967883 =
                                                                                                                                                                                                                ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t6765767882)))));
                                                                                                                                                                                                              
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region multipoleCopiesLevelRegion67884 =
                                                                                                                                                                                                                ((x10.array.Region)(t6765867859.$times(((x10.array.Region)(t6765967883)))));
                                                                                                                                                                                                              
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06732567885 =
                                                                                                                                                                                                                i67891;
                                                                                                                                                                                                              
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.PeriodicDist alloc5010267886 =
                                                                                                                                                                                                                ((x10.array.PeriodicDist)(new x10.array.PeriodicDist((java.lang.System[]) null)));
                                                                                                                                                                                                              
//#line 64 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region r6718767801 =
                                                                                                                                                                                                                ((x10.array.Region)(multipoleCopiesLevelRegion67884));
                                                                                                                                                                                                              
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.ConstantDist alloc289196718867802 =
                                                                                                                                                                                                                ((x10.array.ConstantDist)(new x10.array.ConstantDist((java.lang.System[]) null)));
                                                                                                                                                                                                              
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.array.Region r6731867803 =
                                                                                                                                                                                                                ((x10.array.Region)(r6718767801));
                                                                                                                                                                                                              
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.lang.Place p6731967804 =
                                                                                                                                                                                                                ((x10.lang.Place)(x10.lang.Runtime.home()));
                                                                                                                                                                                                              
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region region6732167799 =
                                                                                                                                                                                                                ((x10.array.Region)(r6731867803));
                                                                                                                                                                                                              
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196718867802.x10$lang$Object$$init$S();
                                                                                                                                                                                                              
//#line 669 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196718867802.region = region6732167799;
                                                                                                                                                                                                              
//#line 29 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
alloc289196718867802.onePlace = p6731967804;
                                                                                                                                                                                                              
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Dist t6766067805 =
                                                                                                                                                                                                                ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                                                                    alloc289196718867802)));
                                                                                                                                                                                                              
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
alloc5010267886.$init(((x10.array.Dist)(t6766067805)));
                                                                                                                                                                                                              
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist6732367887 =
                                                                                                                                                                                                                ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                                                                    alloc5010267886)));
                                                                                                                                                                                                              
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion> alloc310216732467888 =
                                                                                                                                                                                                                ((x10.array.DistArray)(new x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>((java.lang.System[]) null, au.edu.anu.mm.MultipoleExpansion.$RTT)));
                                                                                                                                                                                                              
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc310216732467888.$init(((x10.array.Dist)(dist6732367887)));
                                                                                                                                                                                                              
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion> v6732667889 =
                                                                                                                                                                                                                ((x10.array.DistArray)(alloc310216732467888));
                                                                                                                                                                                                              
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion> ret6732767890 =
                                                                                                                                                                                                                 null;
                                                                                                                                                                                                              
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>> t6766167800 =
                                                                                                                                                                                                                ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>)multipoleCopies).
                                                                                                                                                                                                                                                 raw));
                                                                                                                                                                                                              
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.array.DistArray[])t6766167800.value)[i06732567885] = v6732667889;
                                                                                                                                                                                                              
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6732767890 = ((x10.array.DistArray)(v6732667889));
                                                                                                                                                                                                          }
                                                                                                                                                                                                          
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6762067892 =
                                                                                                                                                                                                            i5010767894;
                                                                                                                                                                                                          
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6762167893 =
                                                                                                                                                                                                            ((t6762067892) + (((int)(1))));
                                                                                                                                                                                                          
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
i5010767894 = t6762167893;
                                                                                                                                                                                                      }
                                                                                                                                                                                                      
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.multipoleCopies = ((x10.array.Array)(multipoleCopies));
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67335 =
                                                                                                                                                                                                         0;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67336: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67664 =
                                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMin).
                                                                                                                                                                                                                                         raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67665 =
                                                                                                                                                                                                        ((int[])t67664.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67335 = t67665;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67336;}
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67668 =
                                                                                                                                                                                                        ret67335;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67343 =
                                                                                                                                                                                                         0;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67344: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67666 =
                                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMax).
                                                                                                                                                                                                                                         raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67667 =
                                                                                                                                                                                                        ((int[])t67666.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67343 = t67667;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67344;}
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67669 =
                                                                                                                                                                                                        ret67343;
                                                                                                                                                                                                      
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t67676 =
                                                                                                                                                                                                        ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t67668)), ((int)(t67669)))));
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67351 =
                                                                                                                                                                                                         0;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67352: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67670 =
                                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMin).
                                                                                                                                                                                                                                         raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67671 =
                                                                                                                                                                                                        ((int[])t67670.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67351 = t67671;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67352;}
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67674 =
                                                                                                                                                                                                        ret67351;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67359 =
                                                                                                                                                                                                         0;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67360: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67672 =
                                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMax).
                                                                                                                                                                                                                                         raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67673 =
                                                                                                                                                                                                        ((int[])t67672.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67359 = t67673;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67360;}
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67675 =
                                                                                                                                                                                                        ret67359;
                                                                                                                                                                                                      
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t67677 =
                                                                                                                                                                                                        ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t67674)), ((int)(t67675)))));
                                                                                                                                                                                                      
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t67685 =
                                                                                                                                                                                                        ((x10.array.Region)(t67676.$times(((x10.lang.IntRange)(t67677)))));
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67367 =
                                                                                                                                                                                                         0;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67368: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67678 =
                                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMin).
                                                                                                                                                                                                                                         raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67679 =
                                                                                                                                                                                                        ((int[])t67678.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67367 = t67679;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67368;}
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67682 =
                                                                                                                                                                                                        ret67367;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67375 =
                                                                                                                                                                                                         0;
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67376: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67680 =
                                                                                                                                                                                                        ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMax).
                                                                                                                                                                                                                                         raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67681 =
                                                                                                                                                                                                        ((int[])t67680.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67375 = t67681;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67376;}
                                                                                                                                                                                                      
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67683 =
                                                                                                                                                                                                        ret67375;
                                                                                                                                                                                                      
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t67684 =
                                                                                                                                                                                                        ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t67682)), ((int)(t67683)))));
                                                                                                                                                                                                      
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t67686 =
                                                                                                                                                                                                        ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t67684)))));
                                                                                                                                                                                                      
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region cachedAtomsRegion =
                                                                                                                                                                                                        ((x10.array.Region)(t67685.$times(((x10.array.Region)(t67686)))));
                                                                                                                                                                                                      
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.PeriodicDist alloc50103 =
                                                                                                                                                                                                        ((x10.array.PeriodicDist)(new x10.array.PeriodicDist((java.lang.System[]) null)));
                                                                                                                                                                                                      
//#line 64 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region r6738267911 =
                                                                                                                                                                                                        ((x10.array.Region)(cachedAtomsRegion));
                                                                                                                                                                                                      
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.ConstantDist alloc289196738367912 =
                                                                                                                                                                                                        ((x10.array.ConstantDist)(new x10.array.ConstantDist((java.lang.System[]) null)));
                                                                                                                                                                                                      
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.array.Region r6738467913 =
                                                                                                                                                                                                        ((x10.array.Region)(r6738267911));
                                                                                                                                                                                                      
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.lang.Place p6738567914 =
                                                                                                                                                                                                        ((x10.lang.Place)(x10.lang.Runtime.home()));
                                                                                                                                                                                                      
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region region6738767897 =
                                                                                                                                                                                                        ((x10.array.Region)(r6738467913));
                                                                                                                                                                                                      
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196738367912.x10$lang$Object$$init$S();
                                                                                                                                                                                                      
//#line 669 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196738367912.region = region6738767897;
                                                                                                                                                                                                      
//#line 29 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
alloc289196738367912.onePlace = p6738567914;
                                                                                                                                                                                                      
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Dist t6768767915 =
                                                                                                                                                                                                        ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                                                            alloc289196738367912)));
                                                                                                                                                                                                      
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
alloc50103.$init(((x10.array.Dist)(t6768767915)));
                                                                                                                                                                                                      
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist67389 =
                                                                                                                                                                                                        ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                                                            alloc50103)));
                                                                                                                                                                                                      
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> alloc3102167390 =
                                                                                                                                                                                                        ((x10.array.DistArray)(new x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))));
                                                                                                                                                                                                      
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102167390.$init(((x10.array.Dist)(dist67389)));
                                                                                                                                                                                                      
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.cachedAtoms = ((x10.array.DistArray)(alloc3102167390));
                                                                                                                                                                                                  }
                                                                                                                                                                                                  return this;
                                                                                                                                                                                                  }
        
        // constructor
        public au.edu.anu.mm.LocallyEssentialTree $init(final x10.array.Array<x10.array.Point> combinedUList,
                                                        final x10.array.Array<x10.array.Array<x10.array.Point>> combinedVList,
                                                        final x10.array.Array<x10.core.Int> uListMin,
                                                        final x10.array.Array<x10.core.Int> uListMax,
                                                        final x10.array.Array<x10.array.Array<x10.core.Int>> vListMin,
                                                        final x10.array.Array<x10.array.Array<x10.core.Int>> vListMax,java.lang.Class<?> $dummy0){return au$edu$anu$mm$LocallyEssentialTree$$init$S(combinedUList,combinedVList,uListMin,uListMax,vListMin,vListMax, $dummy0);}
        
        
        
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
// creation method for java code
        public static au.edu.anu.mm.LocallyEssentialTree $make(final x10.array.Array<x10.array.Point> combinedUList,
                                                               final x10.array.Array<x10.array.Array<x10.array.Point>> combinedVList,
                                                               final x10.array.Array<x10.core.Int> uListMin,
                                                               final x10.array.Array<x10.core.Int> uListMax,
                                                               final x10.array.Array<x10.array.Array<x10.core.Int>> vListMin,
                                                               final x10.array.Array<x10.array.Array<x10.core.Int>> vListMax,
                                                               final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.LocallyEssentialTree((java.lang.System[]) null).$init(combinedUList,combinedVList,uListMin,uListMax,vListMin,vListMax,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.LocallyEssentialTree au$edu$anu$mm$LocallyEssentialTree$$init$S(final x10.array.Array<x10.array.Point> combinedUList,
                                                                                                   final x10.array.Array<x10.array.Array<x10.array.Point>> combinedVList,
                                                                                                   final x10.array.Array<x10.core.Int> uListMin,
                                                                                                   final x10.array.Array<x10.core.Int> uListMax,
                                                                                                   final x10.array.Array<x10.array.Array<x10.core.Int>> vListMin,
                                                                                                   final x10.array.Array<x10.array.Array<x10.core.Int>> vListMax,
                                                                                                   final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                             
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"

                                                                                                                                                             
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"

                                                                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final au.edu.anu.mm.LocallyEssentialTree this6739168034 =
                                                                                                                                                               this;
                                                                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this6739168034.X10$object_lock_id0 = -1;
                                                                                                                                                             
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.combinedUList = ((x10.array.Array)(combinedUList));
                                                                                                                                                             
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.combinedVList = ((x10.array.Array)(combinedVList));
                                                                                                                                                             
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.uListMin = ((x10.array.Array)(uListMin));
                                                                                                                                                             
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.uListMax = ((x10.array.Array)(uListMax));
                                                                                                                                                             
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.vListMin = ((x10.array.Array)(vListMin));
                                                                                                                                                             
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.vListMax = ((x10.array.Array)(vListMax));
                                                                                                                                                             
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>> multipoleCopies =
                                                                                                                                                               ((x10.array.Array)(new x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.DistArray.$RTT, au.edu.anu.mm.MultipoleExpansion.$RTT))));
                                                                                                                                                             
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int size67394 =
                                                                                                                                                               ((x10.array.Array<x10.array.Array<x10.array.Point>>)combinedVList).
                                                                                                                                                                 size;
                                                                                                                                                             
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.x10$lang$Object$$init$S();
                                                                                                                                                             
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199706739768035 =
                                                                                                                                                               ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
                                                                                                                                                             
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6768867916 =
                                                                                                                                                               ((size67394) - (((int)(1))));
                                                                                                                                                             
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199706739768035.$init(((int)(0)),
                                                                                                                                                                                                                                                                                        t6768867916);
                                                                                                                                                             
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__673966740168036 =
                                                                                                                                                               ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                                                     alloc199706739768035)));
                                                                                                                                                             
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6740268037 =
                                                                                                                                                                null;
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6768967917 =
                                                                                                                                                               __desugarer__var__5__673966740168036.
                                                                                                                                                                 rank;
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6769167918 =
                                                                                                                                                               ((int) t6768967917) ==
                                                                                                                                                             ((int) 1);
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6769167918) {
                                                                                                                                                                 
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6769067919 =
                                                                                                                                                                   __desugarer__var__5__673966740168036.
                                                                                                                                                                     zeroBased;
                                                                                                                                                                 
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6769167918 = ((boolean) t6769067919) ==
                                                                                                                                                                 ((boolean) true);
                                                                                                                                                             }
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6769367920 =
                                                                                                                                                               t6769167918;
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6769367920) {
                                                                                                                                                                 
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6769267921 =
                                                                                                                                                                   __desugarer__var__5__673966740168036.
                                                                                                                                                                     rect;
                                                                                                                                                                 
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6769367920 = ((boolean) t6769267921) ==
                                                                                                                                                                 ((boolean) true);
                                                                                                                                                             }
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6769567922 =
                                                                                                                                                               t6769367920;
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6769567922) {
                                                                                                                                                                 
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6769467923 =
                                                                                                                                                                   __desugarer__var__5__673966740168036.
                                                                                                                                                                     rail;
                                                                                                                                                                 
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6769567922 = ((boolean) t6769467923) ==
                                                                                                                                                                 ((boolean) true);
                                                                                                                                                             }
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6769667924 =
                                                                                                                                                               t6769567922;
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6769667924) {
                                                                                                                                                                 
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6769667924 = ((__desugarer__var__5__673966740168036) != (null));
                                                                                                                                                             }
                                                                                                                                                             
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6769767925 =
                                                                                                                                                               t6769667924;
                                                                                                                                                             
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6770067926 =
                                                                                                                                                               !(t6769767925);
                                                                                                                                                             
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6770067926) {
                                                                                                                                                                 
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6769967927 =
                                                                                                                                                                   true;
                                                                                                                                                                 
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6769967927) {
                                                                                                                                                                     
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6769867928 =
                                                                                                                                                                       new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                                                                                                                                                                     
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6769867928;
                                                                                                                                                                 }
                                                                                                                                                             }
                                                                                                                                                             
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6740268037 = ((x10.array.Region)(__desugarer__var__5__673966740168036));
                                                                                                                                                             
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg6739568038 =
                                                                                                                                                               ((x10.array.Region)(ret6740268037));
                                                                                                                                                             
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.region = ((x10.array.Region)(myReg6739568038));
                                                                                                                                                             
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.rank = 1;
                                                                                                                                                             
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.rect = true;
                                                                                                                                                             
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.zeroBased = true;
                                                                                                                                                             
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.rail = true;
                                                                                                                                                             
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.size = size67394;
                                                                                                                                                             
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199716739868039 =
                                                                                                                                                               new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                                                                             
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max06740568040 =
                                                                                                                                                               ((size67394) - (((int)(1))));
                                                                                                                                                             
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.rank = 1;
                                                                                                                                                             
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.min0 = 0;
                                                                                                                                                             
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6770167929 =
                                                                                                                                                               ((_max06740568040) - (((int)(0))));
                                                                                                                                                             
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6770267930 =
                                                                                                                                                               ((t6770167929) + (((int)(1))));
                                                                                                                                                             
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.delta0 = t6770267930;
                                                                                                                                                             
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6770367931 =
                                                                                                                                                               alloc199716739868039.
                                                                                                                                                                 delta0;
                                                                                                                                                             
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t6770467932 =
                                                                                                                                                               ((t6770367931) > (((int)(0))));
                                                                                                                                                             
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t6770567933 =
                                                                                                                                                                0;
                                                                                                                                                             
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t6770467932) {
                                                                                                                                                                 
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6770567933 = alloc199716739868039.
                                                                                                                                                                                                                                                                                       delta0;
                                                                                                                                                             } else {
                                                                                                                                                                 
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6770567933 = 0;
                                                                                                                                                             }
                                                                                                                                                             
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6770667934 =
                                                                                                                                                               t6770567933;
                                                                                                                                                             
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.size = t6770667934;
                                                                                                                                                             
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.min1 = 0;
                                                                                                                                                             
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.delta1 = 0;
                                                                                                                                                             
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.min2 = 0;
                                                                                                                                                             
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.delta2 = 0;
                                                                                                                                                             
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.min3 = 0;
                                                                                                                                                             
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.delta3 = 0;
                                                                                                                                                             
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.min = null;
                                                                                                                                                             
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716739868039.delta = null;
                                                                                                                                                             
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.layout = ((x10.array.RectLayout)(alloc199716739868039));
                                                                                                                                                             
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6740768041 =
                                                                                                                                                               ((x10.array.RectLayout)(((x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>)multipoleCopies).
                                                                                                                                                                                         layout));
                                                                                                                                                             
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6739968042 =
                                                                                                                                                               this6740768041.
                                                                                                                                                                 size;
                                                                                                                                                             
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>> t6770768043 =
                                                                                                                                                               ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>allocate(new x10.rtt.ParameterizedType(x10.array.DistArray.$RTT, au.edu.anu.mm.MultipoleExpansion.$RTT), ((int)(n6739968042)), true)));
                                                                                                                                                             
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
multipoleCopies.raw = ((x10.core.IndexedMemoryChunk)(t6770768043));
                                                                                                                                                             
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6770868045 =
                                                                                                                                                               ((x10.array.Array<x10.array.Array<x10.array.Point>>)combinedVList).
                                                                                                                                                                 size;
                                                                                                                                                             
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int i50123max5012568046 =
                                                                                                                                                               ((t6770868045) - (((int)(1))));
                                                                                                                                                             
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
int i5012368030 =
                                                                                                                                                               0;
                                                                                                                                                             
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
for (;
                                                                                                                                                                                                                                                                          true;
                                                                                                                                                                                                                                                                          ) {
                                                                                                                                                                 
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6771068031 =
                                                                                                                                                                   i5012368030;
                                                                                                                                                                 
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final boolean t6775468032 =
                                                                                                                                                                   ((t6771068031) <= (((int)(i50123max5012568046))));
                                                                                                                                                                 
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
if (!(t6775468032)) {
                                                                                                                                                                     
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
break;
                                                                                                                                                                 }
                                                                                                                                                                 
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int i68027 =
                                                                                                                                                                   i5012368030;
                                                                                                                                                                 
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06740867942 =
                                                                                                                                                                   i68027;
                                                                                                                                                                 
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.array.Point> ret6740967943 =
                                                                                                                                                                    null;
                                                                                                                                                                 
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6741067944: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.array.Point>> t6771367945 =
                                                                                                                                                                   ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.array.Point>>)combinedVList).
                                                                                                                                                                                                    raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Point> t6771467946 =
                                                                                                                                                                   ((x10.array.Array)(((x10.array.Array[])t6771367945.value)[i06740867942]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6740967943 = ((x10.array.Array)(t6771467946));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6741067944;}
                                                                                                                                                                 
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.array.Point> t6771567947 =
                                                                                                                                                                   ((x10.array.Array)(ret6740967943));
                                                                                                                                                                 
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final boolean t6775367948 =
                                                                                                                                                                   ((t6771567947) != (null));
                                                                                                                                                                 
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
if (t6775367948) {
                                                                                                                                                                     
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06741667949 =
                                                                                                                                                                       i68027;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6741767950 =
                                                                                                                                                                        null;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6741867951: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6771667952 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMin).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6771767953 =
                                                                                                                                                                       ((x10.array.Array)(((x10.array.Array[])t6771667952.value)[i06741667949]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6741767950 = ((x10.array.Array)(t6771767953));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6741867951;}
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6742567954 =
                                                                                                                                                                       ((x10.array.Array)(ret6741767950));
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6742667955 =
                                                                                                                                                                        0;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6742767956: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6771867957 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6742567954).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6771967958 =
                                                                                                                                                                       ((int[])t6771867957.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6742667955 = t6771967958;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6742767956;}
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6772467959 =
                                                                                                                                                                       ret6742667955;
                                                                                                                                                                     
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06743367960 =
                                                                                                                                                                       i68027;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6743467961 =
                                                                                                                                                                        null;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6743567962: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6772067963 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMax).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6772167964 =
                                                                                                                                                                       ((x10.array.Array)(((x10.array.Array[])t6772067963.value)[i06743367960]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6743467961 = ((x10.array.Array)(t6772167964));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6743567962;}
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6744267965 =
                                                                                                                                                                       ((x10.array.Array)(ret6743467961));
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6744367966 =
                                                                                                                                                                        0;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6744467967: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6772267968 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6744267965).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6772367969 =
                                                                                                                                                                       ((int[])t6772267968.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6744367966 = t6772367969;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6744467967;}
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6772567970 =
                                                                                                                                                                       ret6744367966;
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t6773667971 =
                                                                                                                                                                       ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t6772467959)), ((int)(t6772567970)))));
                                                                                                                                                                     
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06745067972 =
                                                                                                                                                                       i68027;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6745167973 =
                                                                                                                                                                        null;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6745267974: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6772667975 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMin).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6772767976 =
                                                                                                                                                                       ((x10.array.Array)(((x10.array.Array[])t6772667975.value)[i06745067972]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6745167973 = ((x10.array.Array)(t6772767976));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6745267974;}
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6745967977 =
                                                                                                                                                                       ((x10.array.Array)(ret6745167973));
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6746067978 =
                                                                                                                                                                        0;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6746167979: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6772867980 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6745967977).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6772967981 =
                                                                                                                                                                       ((int[])t6772867980.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6746067978 = t6772967981;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6746167979;}
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6773467982 =
                                                                                                                                                                       ret6746067978;
                                                                                                                                                                     
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06746767983 =
                                                                                                                                                                       i68027;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6746867984 =
                                                                                                                                                                        null;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6746967985: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6773067986 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMax).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6773167987 =
                                                                                                                                                                       ((x10.array.Array)(((x10.array.Array[])t6773067986.value)[i06746767983]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6746867984 = ((x10.array.Array)(t6773167987));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6746967985;}
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6747667988 =
                                                                                                                                                                       ((x10.array.Array)(ret6746867984));
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6747767989 =
                                                                                                                                                                        0;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6747867990: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6773267991 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6747667988).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6773367992 =
                                                                                                                                                                       ((int[])t6773267991.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6747767989 = t6773367992;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6747867990;}
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6773567993 =
                                                                                                                                                                       ret6747767989;
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t6773767994 =
                                                                                                                                                                       ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t6773467982)), ((int)(t6773567993)))));
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t6774967995 =
                                                                                                                                                                       ((x10.array.Region)(t6773667971.$times(((x10.lang.IntRange)(t6773767994)))));
                                                                                                                                                                     
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06748467996 =
                                                                                                                                                                       i68027;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6748567997 =
                                                                                                                                                                        null;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6748667998: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6773867999 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMin).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6773968000 =
                                                                                                                                                                       ((x10.array.Array)(((x10.array.Array[])t6773867999.value)[i06748467996]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6748567997 = ((x10.array.Array)(t6773968000));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6748667998;}
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6749368001 =
                                                                                                                                                                       ((x10.array.Array)(ret6748567997));
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6749468002 =
                                                                                                                                                                        0;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6749568003: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6774068004 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6749368001).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6774168005 =
                                                                                                                                                                       ((int[])t6774068004.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6749468002 = t6774168005;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6749568003;}
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6774668006 =
                                                                                                                                                                       ret6749468002;
                                                                                                                                                                     
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06750168007 =
                                                                                                                                                                       i68027;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Int> ret6750268008 =
                                                                                                                                                                        null;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6750368009: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Int>> t6774268010 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Int>>)vListMax).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Int> t6774368011 =
                                                                                                                                                                       ((x10.array.Array)(((x10.array.Array[])t6774268010.value)[i06750168007]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6750268008 = ((x10.array.Array)(t6774368011));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6750368009;}
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Array<x10.core.Int> this6751068012 =
                                                                                                                                                                       ((x10.array.Array)(ret6750268008));
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret6751168013 =
                                                                                                                                                                        0;
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6751268014: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t6774468015 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)this6751068012).
                                                                                                                                                                                                        raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6774568016 =
                                                                                                                                                                       ((int[])t6774468015.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6751168013 = t6774568016;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6751268014;}
                                                                                                                                                                     
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6774768017 =
                                                                                                                                                                       ret6751168013;
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t6774868018 =
                                                                                                                                                                       ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t6774668006)), ((int)(t6774768017)))));
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t6775068019 =
                                                                                                                                                                       ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t6774868018)))));
                                                                                                                                                                     
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region multipoleCopiesLevelRegion68020 =
                                                                                                                                                                       ((x10.array.Region)(t6774967995.$times(((x10.array.Region)(t6775068019)))));
                                                                                                                                                                     
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06752768021 =
                                                                                                                                                                       i68027;
                                                                                                                                                                     
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.PeriodicDist alloc5010468022 =
                                                                                                                                                                       ((x10.array.PeriodicDist)(new x10.array.PeriodicDist((java.lang.System[]) null)));
                                                                                                                                                                     
//#line 64 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region r6751867937 =
                                                                                                                                                                       ((x10.array.Region)(multipoleCopiesLevelRegion68020));
                                                                                                                                                                     
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.ConstantDist alloc289196751967938 =
                                                                                                                                                                       ((x10.array.ConstantDist)(new x10.array.ConstantDist((java.lang.System[]) null)));
                                                                                                                                                                     
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.array.Region r6752067939 =
                                                                                                                                                                       ((x10.array.Region)(r6751867937));
                                                                                                                                                                     
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.lang.Place p6752167940 =
                                                                                                                                                                       ((x10.lang.Place)(x10.lang.Runtime.home()));
                                                                                                                                                                     
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region region6752367935 =
                                                                                                                                                                       ((x10.array.Region)(r6752067939));
                                                                                                                                                                     
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196751967938.x10$lang$Object$$init$S();
                                                                                                                                                                     
//#line 669 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196751967938.region = region6752367935;
                                                                                                                                                                     
//#line 29 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
alloc289196751967938.onePlace = p6752167940;
                                                                                                                                                                     
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Dist t6775167941 =
                                                                                                                                                                       ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                           alloc289196751967938)));
                                                                                                                                                                     
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
alloc5010468022.$init(((x10.array.Dist)(t6775167941)));
                                                                                                                                                                     
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist6752568023 =
                                                                                                                                                                       ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                           alloc5010468022)));
                                                                                                                                                                     
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion> alloc310216752668024 =
                                                                                                                                                                       ((x10.array.DistArray)(new x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>((java.lang.System[]) null, au.edu.anu.mm.MultipoleExpansion.$RTT)));
                                                                                                                                                                     
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc310216752668024.$init(((x10.array.Dist)(dist6752568023)));
                                                                                                                                                                     
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion> v6752868025 =
                                                                                                                                                                       ((x10.array.DistArray)(alloc310216752668024));
                                                                                                                                                                     
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion> ret6752968026 =
                                                                                                                                                                        null;
                                                                                                                                                                     
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>> t6775267936 =
                                                                                                                                                                       ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.DistArray<au.edu.anu.mm.MultipoleExpansion>>)multipoleCopies).
                                                                                                                                                                                                        raw));
                                                                                                                                                                     
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.array.DistArray[])t6775267936.value)[i06752768021] = v6752868025;
                                                                                                                                                                     
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6752968026 = ((x10.array.DistArray)(v6752868025));
                                                                                                                                                                 }
                                                                                                                                                                 
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6771168028 =
                                                                                                                                                                   i5012368030;
                                                                                                                                                                 
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t6771268029 =
                                                                                                                                                                   ((t6771168028) + (((int)(1))));
                                                                                                                                                                 
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
i5012368030 = t6771268029;
                                                                                                                                                             }
                                                                                                                                                             
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.multipoleCopies = ((x10.array.Array)(multipoleCopies));
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67537 =
                                                                                                                                                                0;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67538: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67755 =
                                                                                                                                                               ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMin).
                                                                                                                                                                                                raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67756 =
                                                                                                                                                               ((int[])t67755.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67537 = t67756;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67538;}
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67759 =
                                                                                                                                                               ret67537;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67545 =
                                                                                                                                                                0;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67546: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67757 =
                                                                                                                                                               ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMax).
                                                                                                                                                                                                raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67758 =
                                                                                                                                                               ((int[])t67757.value)[0];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67545 = t67758;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67546;}
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67760 =
                                                                                                                                                               ret67545;
                                                                                                                                                             
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t67767 =
                                                                                                                                                               ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t67759)), ((int)(t67760)))));
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67553 =
                                                                                                                                                                0;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67554: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67761 =
                                                                                                                                                               ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMin).
                                                                                                                                                                                                raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67762 =
                                                                                                                                                               ((int[])t67761.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67553 = t67762;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67554;}
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67765 =
                                                                                                                                                               ret67553;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67561 =
                                                                                                                                                                0;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67562: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67763 =
                                                                                                                                                               ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMax).
                                                                                                                                                                                                raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67764 =
                                                                                                                                                               ((int[])t67763.value)[1];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67561 = t67764;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67562;}
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67766 =
                                                                                                                                                               ret67561;
                                                                                                                                                             
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t67768 =
                                                                                                                                                               ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t67765)), ((int)(t67766)))));
                                                                                                                                                             
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t67776 =
                                                                                                                                                               ((x10.array.Region)(t67767.$times(((x10.lang.IntRange)(t67768)))));
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67569 =
                                                                                                                                                                0;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67570: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67769 =
                                                                                                                                                               ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMin).
                                                                                                                                                                                                raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67770 =
                                                                                                                                                               ((int[])t67769.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67569 = t67770;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67570;}
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67773 =
                                                                                                                                                               ret67569;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
int ret67577 =
                                                                                                                                                                0;
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret67578: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Int> t67771 =
                                                                                                                                                               ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Int>)uListMax).
                                                                                                                                                                                                raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67772 =
                                                                                                                                                               ((int[])t67771.value)[2];
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret67577 = t67772;
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret67578;}
                                                                                                                                                             
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t67774 =
                                                                                                                                                               ret67577;
                                                                                                                                                             
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.lang.IntRange t67775 =
                                                                                                                                                               ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t67773)), ((int)(t67774)))));
                                                                                                                                                             
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region t67777 =
                                                                                                                                                               ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t67775)))));
                                                                                                                                                             
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.Region cachedAtomsRegion =
                                                                                                                                                               ((x10.array.Region)(t67776.$times(((x10.array.Region)(t67777)))));
                                                                                                                                                             
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final x10.array.PeriodicDist alloc50105 =
                                                                                                                                                               ((x10.array.PeriodicDist)(new x10.array.PeriodicDist((java.lang.System[]) null)));
                                                                                                                                                             
//#line 64 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region r6758468047 =
                                                                                                                                                               ((x10.array.Region)(cachedAtomsRegion));
                                                                                                                                                             
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.ConstantDist alloc289196758568048 =
                                                                                                                                                               ((x10.array.ConstantDist)(new x10.array.ConstantDist((java.lang.System[]) null)));
                                                                                                                                                             
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.array.Region r6758668049 =
                                                                                                                                                               ((x10.array.Region)(r6758468047));
                                                                                                                                                             
//#line 27 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
final x10.lang.Place p6758768050 =
                                                                                                                                                               ((x10.lang.Place)(x10.lang.Runtime.home()));
                                                                                                                                                             
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Region region6758968033 =
                                                                                                                                                               ((x10.array.Region)(r6758668049));
                                                                                                                                                             
//#line 668 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196758568048.x10$lang$Object$$init$S();
                                                                                                                                                             
//#line 669 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
alloc289196758568048.region = region6758968033;
                                                                                                                                                             
//#line 29 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/ConstantDist.x10"
alloc289196758568048.onePlace = p6758768050;
                                                                                                                                                             
//#line 65 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Dist.x10"
final x10.array.Dist t6777868051 =
                                                                                                                                                               ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                   alloc289196758568048)));
                                                                                                                                                             
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
alloc50105.$init(((x10.array.Dist)(t6777868051)));
                                                                                                                                                             
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.Dist dist67591 =
                                                                                                                                                               ((x10.array.Dist)(((x10.array.Dist)
                                                                                                                                                                                   alloc50105)));
                                                                                                                                                             
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
final x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>> alloc3102167592 =
                                                                                                                                                               ((x10.array.DistArray)(new x10.array.DistArray<x10.array.Array<au.edu.anu.chem.PointCharge>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, au.edu.anu.chem.PointCharge.$RTT))));
                                                                                                                                                             
//#line 109 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/DistArray.x10"
alloc3102167592.$init(((x10.array.Dist)(dist67591)));
                                                                                                                                                             
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.cachedAtoms = ((x10.array.DistArray)(alloc3102167592));
                                                                                                                                                             
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final int t67779 =
                                                                                                                                                               paramLock.getIndex();
                                                                                                                                                             
//#line 46 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.X10$object_lock_id0 = ((int)(t67779));
                                                                                                                                                         }
                                                                                                                                                         return this;
                                                                                                                                                         }
        
        // constructor
        public au.edu.anu.mm.LocallyEssentialTree $init(final x10.array.Array<x10.array.Point> combinedUList,
                                                        final x10.array.Array<x10.array.Array<x10.array.Point>> combinedVList,
                                                        final x10.array.Array<x10.core.Int> uListMin,
                                                        final x10.array.Array<x10.core.Int> uListMax,
                                                        final x10.array.Array<x10.array.Array<x10.core.Int>> vListMin,
                                                        final x10.array.Array<x10.array.Array<x10.core.Int>> vListMax,
                                                        final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$LocallyEssentialTree$$init$S(combinedUList,combinedVList,uListMin,uListMax,vListMin,vListMax,paramLock);}
        
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final public au.edu.anu.mm.LocallyEssentialTree
                                                                                                                  au$edu$anu$mm$LocallyEssentialTree$$au$edu$anu$mm$LocallyEssentialTree$this(
                                                                                                                  ){
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
return au.edu.anu.mm.LocallyEssentialTree.this;
        }
        
        
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
final private void
                                                                                                                  __fieldInitializers50053(
                                                                                                                  ){
            
//#line 21 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/LocallyEssentialTree.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers50053$P(
          final au.edu.anu.mm.LocallyEssentialTree LocallyEssentialTree){
            LocallyEssentialTree.__fieldInitializers50053();
        }
    
}
