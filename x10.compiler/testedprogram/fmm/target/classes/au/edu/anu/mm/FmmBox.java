package au.edu.anu.mm;


public class FmmBox
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FmmBox.class);
    
    public static final x10.rtt.RuntimeType<FmmBox> $RTT = new x10.rtt.NamedType<FmmBox>(
    "au.edu.anu.mm.FmmBox", /* base class */FmmBox.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FmmBox $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10.core.GlobalRef parent = (x10.core.GlobalRef) $deserializer.readRef();
        $_obj.parent = parent;
        $_obj.level = $deserializer.readInt();
        $_obj.x = $deserializer.readInt();
        $_obj.y = $deserializer.readInt();
        $_obj.z = $deserializer.readInt();
        x10.array.Array vList = (x10.array.Array) $deserializer.readRef();
        $_obj.vList = vList;
        au.edu.anu.mm.MultipoleExpansion multipoleExp = (au.edu.anu.mm.MultipoleExpansion) $deserializer.readRef();
        $_obj.multipoleExp = multipoleExp;
        au.edu.anu.mm.LocalExpansion localExp = (au.edu.anu.mm.LocalExpansion) $deserializer.readRef();
        $_obj.localExp = localExp;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FmmBox $_obj = new FmmBox((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (parent instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.parent);
        } else {
        $serializer.write(this.parent);
        }
        $serializer.write(this.level);
        $serializer.write(this.x);
        $serializer.write(this.y);
        $serializer.write(this.z);
        if (vList instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.vList);
        } else {
        $serializer.write(this.vList);
        }
        if (multipoleExp instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.multipoleExp);
        } else {
        $serializer.write(this.multipoleExp);
        }
        if (localExp instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.localExp);
        } else {
        $serializer.write(this.localExp);
        }
        
    }
    
    // constructor just for allocation
    public FmmBox(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public int
          X10$object_lock_id0;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public x10.util.concurrent.OrderedLock
                                                                                                    getOrderedLock(
                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54699 =
              this.
                X10$object_lock_id0;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.concurrent.OrderedLock t54700 =
              x10.util.concurrent.OrderedLock.getLock((int)(t54699));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return t54700;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                    getStaticOrderedLock(
                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54701 =
              au.edu.anu.mm.FmmBox.X10$class_lock_id1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.concurrent.OrderedLock t54702 =
              x10.util.concurrent.OrderedLock.getLock((int)(t54701));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return t54702;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public x10.core.GlobalRef<au.edu.anu.mm.FmmBox>
          parent;
        
//#line 26 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public int
          level;
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public int
          x;
        
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public int
          y;
        
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public int
          z;
        
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
/** 
     * The V-list consists of the children of those boxes 
     * not well-separated from this box's parent.
     */public x10.array.Array<x10.array.Point>
          vList;
        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
/** The multipole expansion of the charges within this box. */public au.edu.anu.mm.MultipoleExpansion
          multipoleExp;
        
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
/** The Taylor expansion of the potential within this box due to particles in well separated boxes. */public au.edu.anu.mm.LocalExpansion
          localExp;
        
        
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
// creation method for java code
        public static au.edu.anu.mm.FmmBox $make(final int level,
                                                 final int x,
                                                 final int y,
                                                 final int z,
                                                 final int numTerms,
                                                 final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,java.lang.Class<?> $dummy0){return new au.edu.anu.mm.FmmBox((java.lang.System[]) null).$init(level,x,y,z,numTerms,parent,(java.lang.Class<?>) null);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.FmmBox au$edu$anu$mm$FmmBox$$init$S(final int level,
                                                                       final int x,
                                                                       final int y,
                                                                       final int z,
                                                                       final int numTerms,
                                                                       final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,java.lang.Class<?> $dummy0) { {
                                                                                                                                                                  
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"

                                                                                                                                                                  
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"

                                                                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final au.edu.anu.mm.FmmBox this5466154889 =
                                                                                                                                                                    this;
                                                                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this5466154889.X10$object_lock_id0 = -1;
                                                                                                                                                                  
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this5466154889.vList = null;
                                                                                                                                                                  
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.level = level;
                                                                                                                                                                  
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.x = x;
                                                                                                                                                                  
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.y = y;
                                                                                                                                                                  
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.z = z;
                                                                                                                                                                  
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.parent = ((x10.core.GlobalRef)(parent));
                                                                                                                                                                  
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final au.edu.anu.mm.MultipoleExpansion alloc41947 =
                                                                                                                                                                    ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
                                                                                                                                                                  
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.concurrent.OrderedLock t5470354890 =
                                                                                                                                                                    x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                                                  
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
alloc41947.$init(((int)(numTerms)),
                                                                                                                                                                                                                                                                             t5470354890);
                                                                                                                                                                  
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.multipoleExp = ((au.edu.anu.mm.MultipoleExpansion)(alloc41947));
                                                                                                                                                                  
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final au.edu.anu.mm.LocalExpansion alloc41948 =
                                                                                                                                                                    ((au.edu.anu.mm.LocalExpansion)(new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null)));
                                                                                                                                                                  
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.concurrent.OrderedLock t5470454891 =
                                                                                                                                                                    x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                                                  
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
alloc41948.$init(((int)(numTerms)),
                                                                                                                                                                                                                                                                             t5470454891);
                                                                                                                                                                  
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.localExp = ((au.edu.anu.mm.LocalExpansion)(alloc41948));
                                                                                                                                                              }
                                                                                                                                                              return this;
                                                                                                                                                              }
        
        // constructor
        public au.edu.anu.mm.FmmBox $init(final int level,
                                          final int x,
                                          final int y,
                                          final int z,
                                          final int numTerms,
                                          final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,java.lang.Class<?> $dummy0){return au$edu$anu$mm$FmmBox$$init$S(level,x,y,z,numTerms,parent, $dummy0);}
        
        
        
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
// creation method for java code
        public static au.edu.anu.mm.FmmBox $make(final int level,
                                                 final int x,
                                                 final int y,
                                                 final int z,
                                                 final int numTerms,
                                                 final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,
                                                 final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.FmmBox((java.lang.System[]) null).$init(level,x,y,z,numTerms,parent,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.FmmBox au$edu$anu$mm$FmmBox$$init$S(final int level,
                                                                       final int x,
                                                                       final int y,
                                                                       final int z,
                                                                       final int numTerms,
                                                                       final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,
                                                                       final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                 
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"

                                                                                                                                 
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"

                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final au.edu.anu.mm.FmmBox this5466454892 =
                                                                                                                                   this;
                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this5466454892.X10$object_lock_id0 = -1;
                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this5466454892.vList = null;
                                                                                                                                 
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.level = level;
                                                                                                                                 
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.x = x;
                                                                                                                                 
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.y = y;
                                                                                                                                 
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.z = z;
                                                                                                                                 
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.parent = ((x10.core.GlobalRef)(parent));
                                                                                                                                 
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final au.edu.anu.mm.MultipoleExpansion alloc41949 =
                                                                                                                                   ((au.edu.anu.mm.MultipoleExpansion)(new au.edu.anu.mm.MultipoleExpansion((java.lang.System[]) null)));
                                                                                                                                 
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.concurrent.OrderedLock t5470554893 =
                                                                                                                                   x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                 
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
alloc41949.$init(((int)(numTerms)),
                                                                                                                                                                                                                                            t5470554893);
                                                                                                                                 
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.multipoleExp = ((au.edu.anu.mm.MultipoleExpansion)(alloc41949));
                                                                                                                                 
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final au.edu.anu.mm.LocalExpansion alloc41950 =
                                                                                                                                   ((au.edu.anu.mm.LocalExpansion)(new au.edu.anu.mm.LocalExpansion((java.lang.System[]) null)));
                                                                                                                                 
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.concurrent.OrderedLock t5470654894 =
                                                                                                                                   x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                 
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
alloc41950.$init(((int)(numTerms)),
                                                                                                                                                                                                                                            t5470654894);
                                                                                                                                 
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.localExp = ((au.edu.anu.mm.LocalExpansion)(alloc41950));
                                                                                                                                 
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54707 =
                                                                                                                                   paramLock.getIndex();
                                                                                                                                 
//#line 47 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.X10$object_lock_id0 = ((int)(t54707));
                                                                                                                             }
                                                                                                                             return this;
                                                                                                                             }
        
        // constructor
        public au.edu.anu.mm.FmmBox $init(final int level,
                                          final int x,
                                          final int y,
                                          final int z,
                                          final int numTerms,
                                          final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,
                                          final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$FmmBox$$init$S(level,x,y,z,numTerms,parent,paramLock);}
        
        
        
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public x10x.vector.Point3d
                                                                                                    getCentre(
                                                                                                    final double size){
            
//#line 376 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int i54667 =
              level;
            
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int dim =
              ((1) << (((int)(i54667))));
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t54708 =
              ((double)(int)(((int)(dim))));
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double sideLength =
              ((size) / (((double)(t54708))));
            
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double offset =
              ((0.5) * (((double)(size))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10x.vector.Point3d alloc41951 =
              new x10x.vector.Point3d((java.lang.System[]) null);
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5470954895 =
              x;
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471054896 =
              ((double)(int)(((int)(t5470954895))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471154897 =
              ((t5471054896) + (((double)(0.5))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471254898 =
              ((t5471154897) * (((double)(sideLength))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5472154899 =
              ((t5471254898) - (((double)(offset))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5471354900 =
              y;
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471454901 =
              ((double)(int)(((int)(t5471354900))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471554902 =
              ((t5471454901) + (((double)(0.5))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471654903 =
              ((t5471554902) * (((double)(sideLength))));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5472254904 =
              ((t5471654903) - (((double)(offset))));
            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5471754905 =
              z;
            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471854906 =
              ((double)(int)(((int)(t5471754905))));
            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5471954907 =
              ((t5471854906) + (((double)(0.5))));
            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5472054908 =
              ((t5471954907) * (((double)(sideLength))));
            
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final double t5472354909 =
              ((t5472054908) - (((double)(offset))));
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.concurrent.OrderedLock t5472454910 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
alloc41951.$init(t5472154899,
                                                                                                                       t5472254904,
                                                                                                                       t5472354909,
                                                                                                                       t5472454910);
            
//#line 61 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return alloc41951;
        }
        
        
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public boolean
                                                                                                    wellSeparated$O(
                                                                                                    final int ws,
                                                                                                    final int x2,
                                                                                                    final int y2,
                                                                                                    final int z2){
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54725 =
              x;
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a54668 =
              ((t54725) - (((int)(x2))));
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54726 =
              ((a54668) < (((int)(0))));
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t54727 =
               0;
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54726) {
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54727 = (-(a54668));
            } else {
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54727 = a54668;
            }
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t54728 =
              t54727;
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
boolean t54733 =
              ((t54728) > (((int)(ws))));
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t54733)) {
                
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54729 =
                  y;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a54669 =
                  ((t54729) - (((int)(y2))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54730 =
                  ((a54669) < (((int)(0))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t54731 =
                   0;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54730) {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54731 = (-(a54669));
                } else {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54731 = a54669;
                }
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t54732 =
                  t54731;
                
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54733 = ((t54732) > (((int)(ws))));
            }
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
boolean t54738 =
              t54733;
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t54738)) {
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54734 =
                  z;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a54670 =
                  ((t54734) - (((int)(z2))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54735 =
                  ((a54670) < (((int)(0))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t54736 =
                   0;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54735) {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54736 = (-(a54670));
                } else {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54736 = a54670;
                }
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t54737 =
                  t54736;
                
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54738 = ((t54737) > (((int)(ws))));
            }
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54739 =
              t54738;
            
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return t54739;
        }
        
        
//#line 82 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public boolean
                                                                                                    wellSeparated$O(
                                                                                                    final int ws,
                                                                                                    final au.edu.anu.mm.FmmBox box2){
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54740 =
              x;
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54741 =
              box2.
                x;
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a54671 =
              ((t54740) - (((int)(t54741))));
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54742 =
              ((a54671) < (((int)(0))));
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t54743 =
               0;
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54742) {
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54743 = (-(a54671));
            } else {
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54743 = a54671;
            }
            
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t54744 =
              t54743;
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
boolean t54750 =
              ((t54744) > (((int)(ws))));
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t54750)) {
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54745 =
                  y;
                
//#line 84 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54746 =
                  box2.
                    y;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a54672 =
                  ((t54745) - (((int)(t54746))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54747 =
                  ((a54672) < (((int)(0))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t54748 =
                   0;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54747) {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54748 = (-(a54672));
                } else {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54748 = a54672;
                }
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t54749 =
                  t54748;
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54750 = ((t54749) > (((int)(ws))));
            }
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
boolean t54756 =
              t54750;
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t54756)) {
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54751 =
                  z;
                
//#line 85 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54752 =
                  box2.
                    z;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a54673 =
                  ((t54751) - (((int)(t54752))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t54753 =
                  ((a54673) < (((int)(0))));
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t54754 =
                   0;
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t54753) {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54754 = (-(a54673));
                } else {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t54754 = a54673;
                }
                
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t54755 =
                  t54754;
                
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54756 = ((t54755) > (((int)(ws))));
            }
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54757 =
              t54756;
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return t54757;
        }
        
        
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final public x10.array.Point
                                                                                                    getTranslationIndex(
                                                                                                    final x10.array.Point boxIndex2){
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54758 =
              boxIndex2.$apply$O((int)(0));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54759 =
              x;
            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i054674 =
              ((t54758) - (((int)(t54759))));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54760 =
              boxIndex2.$apply$O((int)(1));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54761 =
              y;
            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i154675 =
              ((t54760) - (((int)(t54761))));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54762 =
              boxIndex2.$apply$O((int)(2));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54763 =
              z;
            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i254676 =
              ((t54762) - (((int)(t54763))));
            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc3047554677 =
              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc3047554677.$init(((int)(i054674)),
                                                                                                                                  ((int)(i154675)),
                                                                                                                                  ((int)(i254676)));
            
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return alloc3047554677;
        }
        
        
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public x10.array.Array<x10.array.Point>
                                                                                                    getVList(
                                                                                                    ){
            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.array.Array<x10.array.Point> t54764 =
              ((x10.array.Array)(this.
                                   vList));
            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return t54764;
        }
        
        
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public void
                                                                                                    setVList_0_$_x10$array$Point_$(
                                                                                                    final x10.array.Array vList){
            
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.vList = ((x10.array.Array)(vList));
        }
        
        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public void
                                                                                                     createVList(
                                                                                                     final int ws){
            
//#line 376 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int i54678 =
              this.
                level;
            
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int levelDim =
              ((1) << (((int)(i54678))));
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54765 =
              this.
                x;
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54766 =
              ((t54765) % (((int)(2))));
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54767 =
              ((int) t54766) ==
            ((int) 1);
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int t54768 =
               0;
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t54767) {
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54768 = -1;
            } else {
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54768 = 0;
            }
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int xOffset =
              t54768;
            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54769 =
              this.
                y;
            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54770 =
              ((t54769) % (((int)(2))));
            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54771 =
              ((int) t54770) ==
            ((int) 1);
            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int t54772 =
               0;
            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t54771) {
                
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54772 = -1;
            } else {
                
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54772 = 0;
            }
            
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int yOffset =
              t54772;
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54773 =
              this.
                z;
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54774 =
              ((t54773) % (((int)(2))));
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54775 =
              ((int) t54774) ==
            ((int) 1);
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int t54776 =
               0;
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t54775) {
                
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54776 = -1;
            } else {
                
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54776 = 0;
            }
            
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int zOffset =
              t54776;
            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.ArrayList<x10.array.Point> vList =
              ((x10.util.ArrayList)(new x10.util.ArrayList<x10.array.Point>((java.lang.System[]) null, x10.array.Point.$RTT)));
            
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
vList.$init();
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5477754966 =
              this.
                x;
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5477854967 =
              ((2) * (((int)(ws))));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5477954968 =
              ((t5477754966) - (((int)(t5477854967))));
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5468054969 =
              ((t5477954968) + (((int)(xOffset))));
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5478054970 =
              ((0) < (((int)(b5468054969))));
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5478154971 =
               0;
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5478054970) {
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5478154971 = b5468054969;
            } else {
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5478154971 = 0;
            }
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i41985min4198654972 =
              t5478154971;
            
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a5468154973 =
              ((levelDim) - (((int)(1))));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5478254974 =
              this.
                x;
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5478354975 =
              ((2) * (((int)(ws))));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5478454976 =
              ((t5478254974) + (((int)(t5478354975))));
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5478554977 =
              ((t5478454976) + (((int)(1))));
            
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5468254978 =
              ((t5478554977) + (((int)(xOffset))));
            
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5478654979 =
              ((a5468154973) < (((int)(b5468254978))));
            
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5478754980 =
               0;
            
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5478654979) {
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5478754980 = a5468154973;
            } else {
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5478754980 = b5468254978;
            }
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i41985max4198754981 =
              t5478754980;
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int i4198554963 =
              i41985min4198654972;
            
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5478954964 =
                  i4198554963;
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5482554965 =
                  ((t5478954964) <= (((int)(i41985max4198754981))));
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t5482554965)) {
                    
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
break;
                }
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int x54960 =
                  i4198554963;
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479254944 =
                  this.
                    y;
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479354945 =
                  ((2) * (((int)(ws))));
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479454946 =
                  ((t5479254944) - (((int)(t5479354945))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5468454947 =
                  ((t5479454946) + (((int)(yOffset))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5479554948 =
                  ((0) < (((int)(b5468454947))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5479654949 =
                   0;
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5479554948) {
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5479654949 = b5468454947;
                } else {
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5479654949 = 0;
                }
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i41969min4197054950 =
                  t5479654949;
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a5468554951 =
                  ((levelDim) - (((int)(1))));
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479754952 =
                  this.
                    y;
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479854953 =
                  ((2) * (((int)(ws))));
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479954954 =
                  ((t5479754952) + (((int)(t5479854953))));
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5480054955 =
                  ((t5479954954) + (((int)(1))));
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5468654956 =
                  ((t5480054955) + (((int)(yOffset))));
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5480154957 =
                  ((a5468554951) < (((int)(b5468654956))));
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5480254958 =
                   0;
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5480154957) {
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5480254958 = a5468554951;
                } else {
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5480254958 = b5468654956;
                }
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i41969max4197154959 =
                  t5480254958;
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int i4196954941 =
                  i41969min4197054950;
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5480454942 =
                      i4196954941;
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5482454943 =
                      ((t5480454942) <= (((int)(i41969max4197154959))));
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t5482454943)) {
                        
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
break;
                    }
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int y54938 =
                      i4196954941;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5480754922 =
                      this.
                        z;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5480854923 =
                      ((2) * (((int)(ws))));
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5480954924 =
                      ((t5480754922) - (((int)(t5480854923))));
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5468854925 =
                      ((t5480954924) + (((int)(zOffset))));
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5481054926 =
                      ((0) < (((int)(b5468854925))));
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5481154927 =
                       0;
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5481054926) {
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5481154927 = b5468854925;
                    } else {
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5481154927 = 0;
                    }
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i41953min4195454928 =
                      t5481154927;
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a5468954929 =
                      ((levelDim) - (((int)(1))));
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5481254930 =
                      this.
                        z;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5481354931 =
                      ((2) * (((int)(ws))));
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5481454932 =
                      ((t5481254930) + (((int)(t5481354931))));
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5481554933 =
                      ((t5481454932) + (((int)(1))));
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5469054934 =
                      ((t5481554933) + (((int)(zOffset))));
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5481654935 =
                      ((a5468954929) < (((int)(b5469054934))));
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5481754936 =
                       0;
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5481654935) {
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5481754936 = a5468954929;
                    } else {
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5481754936 = b5469054934;
                    }
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i41953max4195554937 =
                      t5481754936;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int i4195354919 =
                      i41953min4195454928;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5481954920 =
                          i4195354919;
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5482354921 =
                          ((t5481954920) <= (((int)(i41953max4195554937))));
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t5482354921)) {
                            
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
break;
                        }
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int z54916 =
                          i4195354919;
                        
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5482254911 =
                          this.wellSeparated$O((int)(ws),
                                               (int)(x54960),
                                               (int)(y54938),
                                               (int)(z54916));
                        
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t5482254911) {
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i05469154912 =
                              x54960;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i15469254913 =
                              y54938;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i25469354914 =
                              z54916;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc304755469454915 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc304755469454915.$init(((int)(i05469154912)),
                                                                                                                                                       ((int)(i15469254913)),
                                                                                                                                                       ((int)(i25469354914)));
                            
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
((x10.util.ArrayList<x10.array.Point>)vList).add_0_$$x10$util$ArrayList_T$O(((x10.array.Point)(alloc304755469454915)));
                        }
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5482054917 =
                          i4195354919;
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5482154918 =
                          ((t5482054917) + (((int)(1))));
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
i4195354919 = t5482154918;
                    }
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5480554939 =
                      i4196954941;
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5480654940 =
                      ((t5480554939) + (((int)(1))));
                    
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
i4196954941 = t5480654940;
                }
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479054961 =
                  i4198554963;
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5479154962 =
                  ((t5479054961) + (((int)(1))));
                
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
i4198554963 = t5479154962;
            }
            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.array.Array<x10.array.Point> t54826 =
              ((x10.array.Array)(((x10.util.ArrayList<x10.array.Point>)vList).toArray()));
            
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.vList = ((x10.array.Array)(t54826));
        }
        
        
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public void
                                                                                                     createVListPeriodic(
                                                                                                     final int ws){
            
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54827 =
              this.
                x;
            
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54828 =
              ((t54827) % (((int)(2))));
            
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54829 =
              ((int) t54828) ==
            ((int) 1);
            
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int t54830 =
               0;
            
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t54829) {
                
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54830 = -1;
            } else {
                
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54830 = 0;
            }
            
//#line 128 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int xOffset =
              t54830;
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54831 =
              this.
                y;
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54832 =
              ((t54831) % (((int)(2))));
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54833 =
              ((int) t54832) ==
            ((int) 1);
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int t54834 =
               0;
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t54833) {
                
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54834 = -1;
            } else {
                
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54834 = 0;
            }
            
//#line 129 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int yOffset =
              t54834;
            
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54835 =
              this.
                z;
            
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54836 =
              ((t54835) % (((int)(2))));
            
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t54837 =
              ((int) t54836) ==
            ((int) 1);
            
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int t54838 =
               0;
            
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t54837) {
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54838 = -1;
            } else {
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
t54838 = 0;
            }
            
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int zOffset =
              t54838;
            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.util.ArrayList<x10.array.Point> vList =
              ((x10.util.ArrayList)(new x10.util.ArrayList<x10.array.Point>((java.lang.System[]) null, x10.array.Point.$RTT)));
            
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
vList.$init();
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5483955023 =
              this.
                x;
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484055024 =
              ((2) * (((int)(ws))));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484155025 =
              ((t5483955023) - (((int)(t5484055024))));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i42033min4203455026 =
              ((t5484155025) + (((int)(xOffset))));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484255027 =
              this.
                x;
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484355028 =
              ((2) * (((int)(ws))));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484455029 =
              ((t5484255027) + (((int)(t5484355028))));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484555030 =
              ((t5484455029) + (((int)(1))));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i42033max4203555031 =
              ((t5484555030) + (((int)(xOffset))));
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int i4203355020 =
              i42033min4203455026;
            
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
for (;
                                                                                                            true;
                                                                                                            ) {
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484755021 =
                  i4203355020;
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5487555022 =
                  ((t5484755021) <= (((int)(i42033max4203555031))));
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t5487555022)) {
                    
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
break;
                }
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int x55017 =
                  i4203355020;
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485055008 =
                  this.
                    y;
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485155009 =
                  ((2) * (((int)(ws))));
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485255010 =
                  ((t5485055008) - (((int)(t5485155009))));
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i42017min4201855011 =
                  ((t5485255010) + (((int)(yOffset))));
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485355012 =
                  this.
                    y;
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485455013 =
                  ((2) * (((int)(ws))));
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485555014 =
                  ((t5485355012) + (((int)(t5485455013))));
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485655015 =
                  ((t5485555014) + (((int)(1))));
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i42017max4201955016 =
                  ((t5485655015) + (((int)(yOffset))));
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int i4201755005 =
                  i42017min4201855011;
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
for (;
                                                                                                                true;
                                                                                                                ) {
                    
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485855006 =
                      i4201755005;
                    
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5487455007 =
                      ((t5485855006) <= (((int)(i42017max4201955016))));
                    
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t5487455007)) {
                        
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
break;
                    }
                    
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int y55002 =
                      i4201755005;
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486154993 =
                      this.
                        z;
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486254994 =
                      ((2) * (((int)(ws))));
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486354995 =
                      ((t5486154993) - (((int)(t5486254994))));
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i42001min4200254996 =
                      ((t5486354995) + (((int)(zOffset))));
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486454997 =
                      this.
                        z;
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486554998 =
                      ((2) * (((int)(ws))));
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486654999 =
                      ((t5486454997) + (((int)(t5486554998))));
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486755000 =
                      ((t5486654999) + (((int)(1))));
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int i42001max4200355001 =
                      ((t5486755000) + (((int)(zOffset))));
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
int i4200154990 =
                      i42001min4200254996;
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
for (;
                                                                                                                    true;
                                                                                                                    ) {
                        
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486954991 =
                          i4200154990;
                        
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5487354992 =
                          ((t5486954991) <= (((int)(i42001max4200355001))));
                        
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (!(t5487354992)) {
                            
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
break;
                        }
                        
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int z54987 =
                          i4200154990;
                        
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final boolean t5487254982 =
                          this.wellSeparated$O((int)(ws),
                                               (int)(x55017),
                                               (int)(y55002),
                                               (int)(z54987));
                        
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
if (t5487254982) {
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i05469554983 =
                              x55017;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i15469654984 =
                              y55002;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i25469754985 =
                              z54987;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc304755469854986 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc304755469854986.$init(((int)(i05469554983)),
                                                                                                                                                       ((int)(i15469654984)),
                                                                                                                                                       ((int)(i25469754985)));
                            
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
((x10.util.ArrayList<x10.array.Point>)vList).add_0_$$x10$util$ArrayList_T$O(((x10.array.Point)(alloc304755469854986)));
                        }
                        
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5487054988 =
                          i4200154990;
                        
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5487154989 =
                          ((t5487054988) + (((int)(1))));
                        
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
i4200154990 = t5487154989;
                    }
                    
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5485955003 =
                      i4201755005;
                    
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5486055004 =
                      ((t5485955003) + (((int)(1))));
                    
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
i4201755005 = t5486055004;
                }
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484855018 =
                  i4203355020;
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t5484955019 =
                  ((t5484855018) + (((int)(1))));
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
i4203355020 = t5484955019;
            }
            
//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final x10.array.Array<x10.array.Point> t54876 =
              ((x10.array.Array)(((x10.util.ArrayList<x10.array.Point>)vList).toArray()));
            
//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.vList = ((x10.array.Array)(t54876));
        }
        
        
//#line 144 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
public java.lang.String
                                                                                                     toString(
                                                                                                     ){
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54877 =
              level;
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54878 =
              (("FmmBox level ") + ((x10.core.Int.$box(t54877))));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54879 =
              ((t54878) + (" ("));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54880 =
              x;
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54881 =
              ((t54879) + ((x10.core.Int.$box(t54880))));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54882 =
              ((t54881) + (","));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54883 =
              y;
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54884 =
              ((t54882) + ((x10.core.Int.$box(t54883))));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54885 =
              ((t54884) + (","));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final int t54886 =
              z;
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54887 =
              ((t54885) + ((x10.core.Int.$box(t54886))));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final java.lang.String t54888 =
              ((t54887) + (")"));
            
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return t54888;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final public au.edu.anu.mm.FmmBox
                                                                                                    au$edu$anu$mm$FmmBox$$au$edu$anu$mm$FmmBox$this(
                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
return au.edu.anu.mm.FmmBox.this;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
final private void
                                                                                                    __fieldInitializers41314(
                                                                                                    ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.X10$object_lock_id0 = -1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmBox.x10"
this.vList = null;
        }
        
        final public static void
          __fieldInitializers41314$P(
          final au.edu.anu.mm.FmmBox FmmBox){
            FmmBox.__fieldInitializers41314();
        }
    
}
