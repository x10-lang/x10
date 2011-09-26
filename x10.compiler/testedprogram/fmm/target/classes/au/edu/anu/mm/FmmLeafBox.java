package au.edu.anu.mm;


public class FmmLeafBox
extends au.edu.anu.mm.FmmBox
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, FmmLeafBox.class);
    
    public static final x10.rtt.RuntimeType<FmmLeafBox> $RTT = new x10.rtt.NamedType<FmmLeafBox>(
    "au.edu.anu.mm.FmmLeafBox", /* base class */FmmLeafBox.class
    , /* parents */ new x10.rtt.Type[] {au.edu.anu.mm.FmmBox.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(FmmLeafBox $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        au.edu.anu.mm.FmmBox.$_deserialize_body($_obj, $deserializer);
        x10.util.ArrayList atoms = (x10.util.ArrayList) $deserializer.readRef();
        $_obj.atoms = atoms;
        x10.array.Array uList = (x10.array.Array) $deserializer.readRef();
        $_obj.uList = uList;
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        FmmLeafBox $_obj = new FmmLeafBox((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        if (atoms instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.atoms);
        } else {
        $serializer.write(this.atoms);
        }
        if (uList instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.uList);
        } else {
        $serializer.write(this.uList);
        }
        
    }
    
    // constructor just for allocation
    public FmmLeafBox(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public int
          X10$object_lock_id0;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public x10.util.concurrent.OrderedLock
                                                                                                        getOrderedLock(
                                                                                                        ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t58691 =
              this.
                X10$object_lock_id0;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.concurrent.OrderedLock t58692 =
              x10.util.concurrent.OrderedLock.getLock((int)(t58691));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
return t58692;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                        getStaticOrderedLock(
                                                                                                        ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t58693 =
              au.edu.anu.mm.FmmLeafBox.X10$class_lock_id1;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.concurrent.OrderedLock t58694 =
              x10.util.concurrent.OrderedLock.getLock((int)(t58693));
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
return t58694;
        }
        
        
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public x10.util.ArrayList<au.edu.anu.chem.PointCharge>
          atoms;
        
//#line 27 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
/** The U-list consists of all leaf boxes not well-separated to this box. */public x10.array.Array<x10.array.Point>
          uList;
        
        
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
// creation method for java code
        public static au.edu.anu.mm.FmmLeafBox $make(final int level,
                                                     final int x,
                                                     final int y,
                                                     final int z,
                                                     final int numTerms,
                                                     final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,java.lang.Class<?> $dummy0){return new au.edu.anu.mm.FmmLeafBox((java.lang.System[]) null).$init(level,x,y,z,numTerms,parent,(java.lang.Class<?>) null);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.FmmLeafBox au$edu$anu$mm$FmmLeafBox$$init$S(final int level,
                                                                               final int x,
                                                                               final int y,
                                                                               final int z,
                                                                               final int numTerms,
                                                                               final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,java.lang.Class<?> $dummy0) { {
                                                                                                                                                                          
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
super.$init(((int)(level)),
                                                                                                                                                                                                                                                                                    ((int)(x)),
                                                                                                                                                                                                                                                                                    ((int)(y)),
                                                                                                                                                                                                                                                                                    ((int)(z)),
                                                                                                                                                                                                                                                                                    ((int)(numTerms)),
                                                                                                                                                                                                                                                                                    ((x10.core.GlobalRef<au.edu.anu.mm.FmmBox>)(parent)),(java.lang.Class<?>) null);
                                                                                                                                                                          
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"

                                                                                                                                                                          
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final au.edu.anu.mm.FmmLeafBox this5539258779 =
                                                                                                                                                                            this;
                                                                                                                                                                          
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this5539258779.X10$object_lock_id0 = -1;
                                                                                                                                                                          
//#line 24 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> alloc412155539158778 =
                                                                                                                                                                            ((x10.util.ArrayList)(new x10.util.ArrayList<au.edu.anu.chem.PointCharge>((java.lang.System[]) null, au.edu.anu.chem.PointCharge.$RTT)));
                                                                                                                                                                          
//#line 24 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
alloc412155539158778.$init();
                                                                                                                                                                          
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this5539258779.atoms = alloc412155539158778;
                                                                                                                                                                          
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this5539258779.uList = null;
                                                                                                                                                                      }
                                                                                                                                                                      return this;
                                                                                                                                                                      }
        
        // constructor
        public au.edu.anu.mm.FmmLeafBox $init(final int level,
                                              final int x,
                                              final int y,
                                              final int z,
                                              final int numTerms,
                                              final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,java.lang.Class<?> $dummy0){return au$edu$anu$mm$FmmLeafBox$$init$S(level,x,y,z,numTerms,parent, $dummy0);}
        
        
        
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
// creation method for java code
        public static au.edu.anu.mm.FmmLeafBox $make(final int level,
                                                     final int x,
                                                     final int y,
                                                     final int z,
                                                     final int numTerms,
                                                     final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,
                                                     final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.FmmLeafBox((java.lang.System[]) null).$init(level,x,y,z,numTerms,parent,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.FmmLeafBox au$edu$anu$mm$FmmLeafBox$$init$S(final int level,
                                                                               final int x,
                                                                               final int y,
                                                                               final int z,
                                                                               final int numTerms,
                                                                               final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,
                                                                               final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                         
//#line 30 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
super.$init(((int)(level)),
                                                                                                                                                                                                                                                   ((int)(x)),
                                                                                                                                                                                                                                                   ((int)(y)),
                                                                                                                                                                                                                                                   ((int)(z)),
                                                                                                                                                                                                                                                   ((int)(numTerms)),
                                                                                                                                                                                                                                                   ((x10.core.GlobalRef<au.edu.anu.mm.FmmBox>)(parent)),(java.lang.Class<?>) null);
                                                                                                                                         
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"

                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final au.edu.anu.mm.FmmLeafBox this5539658781 =
                                                                                                                                           this;
                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this5539658781.X10$object_lock_id0 = -1;
                                                                                                                                         
//#line 24 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> alloc412155539558780 =
                                                                                                                                           ((x10.util.ArrayList)(new x10.util.ArrayList<au.edu.anu.chem.PointCharge>((java.lang.System[]) null, au.edu.anu.chem.PointCharge.$RTT)));
                                                                                                                                         
//#line 24 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
alloc412155539558780.$init();
                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this5539658781.atoms = alloc412155539558780;
                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this5539658781.uList = null;
                                                                                                                                         
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t58695 =
                                                                                                                                           paramLock.getIndex();
                                                                                                                                         
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this.X10$object_lock_id0 = ((int)(t58695));
                                                                                                                                     }
                                                                                                                                     return this;
                                                                                                                                     }
        
        // constructor
        public au.edu.anu.mm.FmmLeafBox $init(final int level,
                                              final int x,
                                              final int y,
                                              final int z,
                                              final int numTerms,
                                              final x10.core.GlobalRef<au.edu.anu.mm.FmmBox> parent,
                                              final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$FmmLeafBox$$init$S(level,x,y,z,numTerms,parent,paramLock);}
        
        
        
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public void
                                                                                                        addAtom(
                                                                                                        final au.edu.anu.chem.PointCharge atom){
            
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
try {{
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.concurrent.OrderedLock t58696 =
                  this.getOrderedLock();
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.concurrent.OrderedLock t58697 =
                  atom.getOrderedLock();
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
x10.util.concurrent.OrderedLock.acquireTwoLocks(((x10.util.concurrent.OrderedLock)(t58696)),
                                                                                                                                                              ((x10.util.concurrent.OrderedLock)(t58697)));
                
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
x10.lang.Runtime.pushAtomic();
                
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> t5869858782 =
                  ((x10.util.ArrayList)(atoms));
                
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
((x10.util.ArrayList<au.edu.anu.chem.PointCharge>)t5869858782).add_0_$$x10$util$ArrayList_T$O(((au.edu.anu.chem.PointCharge)(atom)));
            }}finally {{
                  
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
x10.lang.Runtime.popAtomic();
                  
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.concurrent.OrderedLock t58699 =
                    this.getOrderedLock();
                  
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.concurrent.OrderedLock t58700 =
                    atom.getOrderedLock();
                  
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
x10.util.concurrent.OrderedLock.releaseTwoLocks(((x10.util.concurrent.OrderedLock)(t58699)),
                                                                                                                                                                ((x10.util.concurrent.OrderedLock)(t58700)));
              }}
            }
        
        
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public x10.array.Array<x10.array.Point>
                                                                                                        getUList(
                                                                                                        ){
            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.array.Array<x10.array.Point> t58701 =
              ((x10.array.Array)(this.
                                   uList));
            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
return t58701;
        }
        
        
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public void
                                                                                                        setUList_0_$_x10$array$Point_$(
                                                                                                        final x10.array.Array uList){
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this.uList = ((x10.array.Array)(uList));
        }
        
        
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public void
                                                                                                        createUList(
                                                                                                        final int ws){
            
//#line 376 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int i58671 =
              this.
                level;
            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int levelDim =
              ((1) << (((int)(i58671))));
            
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.ArrayList<x10.array.Point> uList =
              ((x10.util.ArrayList)(new x10.util.ArrayList<x10.array.Point>((java.lang.System[]) null, x10.array.Point.$RTT)));
            
//#line 52 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
uList.$init();
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5870258839 =
              this.
                x;
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5867358840 =
              ((t5870258839) - (((int)(ws))));
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5870358841 =
              ((0) < (((int)(b5867358840))));
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5870458842 =
               0;
            
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5870358841) {
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5870458842 = b5867358840;
            } else {
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5870458842 = 0;
            }
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41249min4125058843 =
              t5870458842;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41249max4125158844 =
              this.
                x;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
int i4124958836 =
              i41249min4125058843;
            
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5870658837 =
                  i4124958836;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5874358838 =
                  ((t5870658837) <= (((int)(i41249max4125158844))));
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5874358838)) {
                    
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
break;
                }
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int x58833 =
                  i4124958836;
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5870958822 =
                  this.
                    y;
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5867558823 =
                  ((t5870958822) - (((int)(ws))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5871058824 =
                  ((0) < (((int)(b5867558823))));
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5871158825 =
                   0;
                
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5871058824) {
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5871158825 = b5867558823;
                } else {
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5871158825 = 0;
                }
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41233min4123458826 =
                  t5871158825;
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a5867658827 =
                  ((levelDim) - (((int)(1))));
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5871258828 =
                  this.
                    y;
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5867758829 =
                  ((t5871258828) + (((int)(ws))));
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5871358830 =
                  ((a5867658827) < (((int)(b5867758829))));
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5871458831 =
                   0;
                
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5871358830) {
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5871458831 = a5867658827;
                } else {
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5871458831 = b5867758829;
                }
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41233max4123558832 =
                  t5871458831;
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
int i4123358819 =
                  i41233min4123458826;
                
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5871658820 =
                      i4123358819;
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5874258821 =
                      ((t5871658820) <= (((int)(i41233max4123558832))));
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5874258821)) {
                        
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
break;
                    }
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int y58816 =
                      i4123358819;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5871958805 =
                      this.
                        z;
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5867958806 =
                      ((t5871958805) - (((int)(ws))));
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5872058807 =
                      ((0) < (((int)(b5867958806))));
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5872158808 =
                       0;
                    
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5872058807) {
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5872158808 = b5867958806;
                    } else {
                        
//#line 333 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5872158808 = 0;
                    }
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41217min4121858809 =
                      t5872158808;
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a5868058810 =
                      ((levelDim) - (((int)(1))));
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5872258811 =
                      this.
                        z;
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int b5868158812 =
                      ((t5872258811) + (((int)(ws))));
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t5872358813 =
                      ((a5868058810) < (((int)(b5868158812))));
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t5872458814 =
                       0;
                    
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t5872358813) {
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5872458814 = a5868058810;
                    } else {
                        
//#line 334 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t5872458814 = b5868158812;
                    }
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41217max4121958815 =
                      t5872458814;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
int i4121758802 =
                      i41217min4121858809;
                    
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5872658803 =
                          i4121758802;
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5874158804 =
                          ((t5872658803) <= (((int)(i41217max4121958815))));
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5874158804)) {
                            
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
break;
                        }
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int z58799 =
                          i4121758802;
                        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5872958783 =
                          this.
                            x;
                        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5873358784 =
                          ((x58833) < (((int)(t5872958783))));
                        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5873358784)) {
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5873058785 =
                              this.
                                x;
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5873258786 =
                              ((int) x58833) ==
                            ((int) t5873058785);
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5873258786) {
                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5873158787 =
                                  this.
                                    y;
                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5873258786 = ((y58816) < (((int)(t5873158787))));
                            }
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5873358784 = t5873258786;
                        }
                        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5873958788 =
                          t5873358784;
                        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5873958788)) {
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5873458789 =
                              this.
                                x;
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5873658790 =
                              ((int) x58833) ==
                            ((int) t5873458789);
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5873658790) {
                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5873558791 =
                                  this.
                                    y;
                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5873658790 = ((int) y58816) ==
                                ((int) t5873558791);
                            }
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5873858792 =
                              t5873658790;
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5873858792) {
                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5873758793 =
                                  this.
                                    z;
                                
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5873858792 = ((z58799) < (((int)(t5873758793))));
                            }
                            
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5873958788 = t5873858792;
                        }
                        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5874058794 =
                          t5873958788;
                        
//#line 56 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5874058794) {
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i05868258795 =
                              x58833;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i15868358796 =
                              y58816;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i25868458797 =
                              z58799;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc304755868558798 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc304755868558798.$init(((int)(i05868258795)),
                                                                                                                                                       ((int)(i15868358796)),
                                                                                                                                                       ((int)(i25868458797)));
                            
//#line 57 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
((x10.util.ArrayList<x10.array.Point>)uList).add_0_$$x10$util$ArrayList_T$O(((x10.array.Point)(alloc304755868558798)));
                        }
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5872758800 =
                          i4121758802;
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5872858801 =
                          ((t5872758800) + (((int)(1))));
                        
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
i4121758802 = t5872858801;
                    }
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5871758817 =
                      i4123358819;
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5871858818 =
                      ((t5871758817) + (((int)(1))));
                    
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
i4123358819 = t5871858818;
                }
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5870758834 =
                  i4124958836;
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5870858835 =
                  ((t5870758834) + (((int)(1))));
                
//#line 53 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
i4124958836 = t5870858835;
            }
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.array.Array<x10.array.Point> t58744 =
              ((x10.array.Array)(((x10.util.ArrayList<x10.array.Point>)uList).toArray()));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this.uList = ((x10.array.Array)(t58744));
        }
        
        
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
public void
                                                                                                        createUListPeriodic(
                                                                                                        final int ws){
            
//#line 376 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int i58686 =
              this.
                level;
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int levelDim =
              ((1) << (((int)(i58686))));
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.ArrayList<x10.array.Point> uList =
              ((x10.util.ArrayList)(new x10.util.ArrayList<x10.array.Point>((java.lang.System[]) null, x10.array.Point.$RTT)));
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
uList.$init();
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5874558887 =
              this.
                x;
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41297min4129858888 =
              ((t5874558887) - (((int)(ws))));
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41297max4129958889 =
              this.
                x;
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
int i4129758884 =
              i41297min4129858888;
            
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5874758885 =
                  i4129758884;
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5877658886 =
                  ((t5874758885) <= (((int)(i41297max4129958889))));
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5877658886)) {
                    
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
break;
                }
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int x58881 =
                  i4129758884;
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875058877 =
                  this.
                    y;
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41281min4128258878 =
                  ((t5875058877) - (((int)(ws))));
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875158879 =
                  this.
                    y;
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41281max4128358880 =
                  ((t5875158879) + (((int)(ws))));
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
int i4128158874 =
                  i41281min4128258878;
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875358875 =
                      i4128158874;
                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5877558876 =
                      ((t5875358875) <= (((int)(i41281max4128358880))));
                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5877558876)) {
                        
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
break;
                    }
                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int y58871 =
                      i4128158874;
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875658867 =
                      this.
                        z;
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41265min4126658868 =
                      ((t5875658867) - (((int)(ws))));
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875758869 =
                      this.
                        z;
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int i41265max4126758870 =
                      ((t5875758869) + (((int)(ws))));
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
int i4126558864 =
                      i41265min4126658868;
                    
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875958865 =
                          i4126558864;
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5877458866 =
                          ((t5875958865) <= (((int)(i41265max4126758870))));
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5877458866)) {
                            
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
break;
                        }
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int z58861 =
                          i4126558864;
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5876258845 =
                          this.
                            x;
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5876658846 =
                          ((x58881) < (((int)(t5876258845))));
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5876658846)) {
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5876358847 =
                              this.
                                x;
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5876558848 =
                              ((int) x58881) ==
                            ((int) t5876358847);
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5876558848) {
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5876458849 =
                                  this.
                                    y;
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5876558848 = ((y58871) < (((int)(t5876458849))));
                            }
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5876658846 = t5876558848;
                        }
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5877258850 =
                          t5876658846;
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (!(t5877258850)) {
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5876758851 =
                              this.
                                x;
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5876958852 =
                              ((int) x58881) ==
                            ((int) t5876758851);
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5876958852) {
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5876858853 =
                                  this.
                                    y;
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5876958852 = ((int) y58871) ==
                                ((int) t5876858853);
                            }
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
boolean t5877158854 =
                              t5876958852;
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5877158854) {
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5877058855 =
                                  this.
                                    z;
                                
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5877158854 = ((z58861) < (((int)(t5877058855))));
                            }
                            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
t5877258850 = t5877158854;
                        }
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final boolean t5877358856 =
                          t5877258850;
                        
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
if (t5877358856) {
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i05868758857 =
                              x58881;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i15868858858 =
                              y58871;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i25868958859 =
                              z58861;
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc304755869058860 =
                              ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                            
//#line 126 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc304755869058860.$init(((int)(i05868758857)),
                                                                                                                                                       ((int)(i15868858858)),
                                                                                                                                                       ((int)(i25868958859)));
                            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
((x10.util.ArrayList<x10.array.Point>)uList).add_0_$$x10$util$ArrayList_T$O(((x10.array.Point)(alloc304755869058860)));
                        }
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5876058862 =
                          i4126558864;
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5876158863 =
                          ((t5876058862) + (((int)(1))));
                        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
i4126558864 = t5876158863;
                    }
                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875458872 =
                      i4128158874;
                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5875558873 =
                      ((t5875458872) + (((int)(1))));
                    
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
i4128158874 = t5875558873;
                }
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5874858882 =
                  i4129758884;
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final int t5874958883 =
                  ((t5874858882) + (((int)(1))));
                
//#line 74 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
i4129758884 = t5874958883;
            }
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.array.Array<x10.array.Point> t58777 =
              ((x10.array.Array)(((x10.util.ArrayList<x10.array.Point>)uList).toArray()));
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this.uList = ((x10.array.Array)(t58777));
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final public au.edu.anu.mm.FmmLeafBox
                                                                                                        au$edu$anu$mm$FmmLeafBox$$au$edu$anu$mm$FmmLeafBox$this(
                                                                                                        ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
return au.edu.anu.mm.FmmLeafBox.this;
        }
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final private void
                                                                                                        __fieldInitializers41094(
                                                                                                        ){
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this.X10$object_lock_id0 = -1;
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
final x10.util.ArrayList<au.edu.anu.chem.PointCharge> alloc41215 =
              ((x10.util.ArrayList)(new x10.util.ArrayList<au.edu.anu.chem.PointCharge>((java.lang.System[]) null, au.edu.anu.chem.PointCharge.$RTT)));
            
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
alloc41215.$init();
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this.atoms = alloc41215;
            
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/FmmLeafBox.x10"
this.uList = null;
        }
        
        final public static void
          __fieldInitializers41094$P(
          final au.edu.anu.mm.FmmLeafBox FmmLeafBox){
            FmmLeafBox.__fieldInitializers41094();
        }
        
    }
    