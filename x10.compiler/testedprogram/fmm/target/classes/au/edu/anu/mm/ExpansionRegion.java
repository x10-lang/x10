package au.edu.anu.mm;

public class ExpansionRegion
extends x10.array.Region
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ExpansionRegion.class);
    
    public static final x10.rtt.RuntimeType<ExpansionRegion> $RTT = new x10.rtt.NamedType<ExpansionRegion>(
    "au.edu.anu.mm.ExpansionRegion", /* base class */ExpansionRegion.class
    , /* parents */ new x10.rtt.Type[] {x10.array.Region.$RTT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(ExpansionRegion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10.array.Region.$_deserialize_body($_obj, $deserializer);
        $_obj.p = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        ExpansionRegion $_obj = new ExpansionRegion((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        super.$_serialize($serializer);
        $serializer.write(this.p);
        
    }
    
    // constructor just for allocation
    public ExpansionRegion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
          X10$object_lock_id0;
        
        
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.util.concurrent.OrderedLock
                                                                                                             getOrderedLock(
                                                                                                             ){
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73031 =
              this.
                X10$object_lock_id0;
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.util.concurrent.OrderedLock t73032 =
              x10.util.concurrent.OrderedLock.getLock((int)(t73031));
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73032;
        }
        
        
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                             getStaticOrderedLock(
                                                                                                             ){
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73033 =
              au.edu.anu.mm.ExpansionRegion.X10$class_lock_id1;
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.util.concurrent.OrderedLock t73034 =
              x10.util.concurrent.OrderedLock.getLock((int)(t73033));
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73034;
        }
        
        
        
//#line 16 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
          p;
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
// creation method for java code
        public static au.edu.anu.mm.ExpansionRegion $make(final int p){return new au.edu.anu.mm.ExpansionRegion((java.lang.System[]) null).$init(p);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.ExpansionRegion au$edu$anu$mm$ExpansionRegion$$init$S(final int p) { {
                                                                                                               
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region this72997 =
                                                                                                                 ((x10.array.Region)(this));
                                                                                                               
//#line 469 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this72997.x10$lang$Object$$init$S();
                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this72997.rank = 2;
                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this72997.rect = false;
                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this72997.zeroBased = true;
                                                                                                               
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this72997.rail = false;
                                                                                                               
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"

                                                                                                               
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion this7299973169 =
                                                                                                                 this;
                                                                                                               
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7299973169.X10$object_lock_id0 = -1;
                                                                                                               
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.p = p;
                                                                                                           }
                                                                                                           return this;
                                                                                                           }
        
        // constructor
        public au.edu.anu.mm.ExpansionRegion $init(final int p){return au$edu$anu$mm$ExpansionRegion$$init$S(p);}
        
        
        
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
// creation method for java code
        public static au.edu.anu.mm.ExpansionRegion $make(final int p,
                                                          final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.ExpansionRegion((java.lang.System[]) null).$init(p,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.ExpansionRegion au$edu$anu$mm$ExpansionRegion$$init$S(final int p,
                                                                                         final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                   
//#line 24 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region this73006 =
                                                                                                                                                     ((x10.array.Region)(this));
                                                                                                                                                   
//#line 469 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73006.x10$lang$Object$$init$S();
                                                                                                                                                   
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73006.rank = 2;
                                                                                                                                                   
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73006.rect = false;
                                                                                                                                                   
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73006.zeroBased = true;
                                                                                                                                                   
//#line 472 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Region.x10"
this73006.rail = false;
                                                                                                                                                   
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"

                                                                                                                                                   
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion this7300873170 =
                                                                                                                                                     this;
                                                                                                                                                   
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7300873170.X10$object_lock_id0 = -1;
                                                                                                                                                   
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.p = p;
                                                                                                                                                   
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73035 =
                                                                                                                                                     paramLock.getIndex();
                                                                                                                                                   
//#line 23 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.X10$object_lock_id0 = ((int)(t73035));
                                                                                                                                               }
                                                                                                                                               return this;
                                                                                                                                               }
        
        // constructor
        public au.edu.anu.mm.ExpansionRegion $init(final int p,
                                                   final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$ExpansionRegion$$init$S(p,paramLock);}
        
        
        
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public boolean
                                                                                                             isConvex$O(
                                                                                                             ){
            
//#line 29 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return true;
        }
        
        
//#line 32 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public boolean
                                                                                                             isEmpty$O(
                                                                                                             ){
            
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return false;
        }
        
        
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                             min(
                                                                                                             ){
            
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t73044 =
              ((x10.core.fun.Fun_0_1)(new au.edu.anu.mm.ExpansionRegion.$Closure$75(this,
                                                                                    p)));
            
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73044;
        }
        
        
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int>
                                                                                                             max(
                                                                                                             ){
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.core.fun.Fun_0_1<x10.core.Int,x10.core.Int> t73053 =
              ((x10.core.fun.Fun_0_1)(new au.edu.anu.mm.ExpansionRegion.$Closure$76(this,
                                                                                    p)));
            
//#line 42 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73053;
        }
        
        
//#line 54 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
                                                                                                             size$O(
                                                                                                             ){
            
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73054 =
              p;
            
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73056 =
              ((t73054) + (((int)(1))));
            
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73055 =
              p;
            
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73057 =
              ((t73055) + (((int)(1))));
            
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73058 =
              ((t73056) * (((int)(t73057))));
            
//#line 55 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73058;
        }
        
        
//#line 58 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public boolean
                                                                                                             contains$O(
                                                                                                             final x10.array.Point p){
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73059 =
              p.
                rank;
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73070 =
              ((int) t73059) ==
            ((int) 2);
            
//#line 59 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73070) {
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73060 =
                  p.$apply$O((int)(0));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
boolean t73063 =
                  ((t73060) >= (((int)(0))));
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73063) {
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73061 =
                      p.$apply$O((int)(0));
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73062 =
                      this.
                        p;
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
t73063 = ((t73061) <= (((int)(t73062))));
                }
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
boolean t73068 =
                  t73063;
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73068) {
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int a73011 =
                      p.$apply$O((int)(1));
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final boolean t73064 =
                      ((a73011) < (((int)(0))));
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
int t73065 =
                       0;
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
if (t73064) {
                        
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t73065 = (-(a73011));
                    } else {
                        
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
t73065 = a73011;
                    }
                    
//#line 22 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/lang/Math.x10"
final int t73066 =
                      t73065;
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73067 =
                      p.$apply$O((int)(0));
                    
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
t73068 = ((t73066) <= (((int)(t73067))));
                }
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73069 =
                  t73068;
                
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73069;
            }
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73071 =
              (("contains(") + (p));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73072 =
              ((t73071) + (")"));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73073 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t73072)));
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73073;
        }
        
        
//#line 65 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public boolean
                                                                                                             contains$O(
                                                                                                             final x10.array.Region r){
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
boolean t73077 =
              au.edu.anu.mm.ExpansionRegion.$RTT.instanceOf(r);
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73077) {
                
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion t73074 =
                  ((au.edu.anu.mm.ExpansionRegion)(x10.rtt.Types.<au.edu.anu.mm.ExpansionRegion> cast(r,au.edu.anu.mm.ExpansionRegion.$RTT)));
                
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73075 =
                  t73074.
                    p;
                
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73076 =
                  this.
                    p;
                
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
t73077 = ((int) t73075) ==
                ((int) t73076);
            }
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73078 =
              t73077;
            
//#line 66 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73078) {
                
//#line 67 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return true;
            }
            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73079 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("contains(Region)")))));
            
//#line 68 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73079;
        }
        
        
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                             complement(
                                                                                                             ){
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73080 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("complement()")))));
            
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73080;
        }
        
        
//#line 76 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                             intersection(
                                                                                                             final x10.array.Region t){
            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73081 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("intersection()")))));
            
//#line 78 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73081;
        }
        
        
//#line 81 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                             product(
                                                                                                             final x10.array.Region that){
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73082 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("product()")))));
            
//#line 83 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73082;
        }
        
        
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                             translate(
                                                                                                             final x10.array.Point v){
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73083 =
              ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(((java.lang.String)("translate()")))));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73083;
        }
        
        
//#line 91 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                             projection(
                                                                                                             final int axis){
            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
switch (axis) {
                
//#line 93 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
case 0:
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73084 =
                      p;
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73085 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(0)), ((int)(t73084)))));
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73086 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73085)))));
                    
//#line 94 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73086;
                
//#line 95 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
case 1:
                    
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73087 =
                      p;
                    
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73088 =
                      (-(t73087));
                    
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73089 =
                      p;
                    
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73090 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73088)), ((int)(t73089)))));
                    
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73091 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73090)))));
                    
//#line 96 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73091;
                
//#line 97 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
default:
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73092 =
                      (("projection(") + ((x10.core.Int.$box(axis))));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73093 =
                      ((t73092) + (")"));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73094 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t73093)));
                    
//#line 98 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73094;
            }
        }
        
        
//#line 102 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                              eliminate(
                                                                                                              final int axis){
            
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
switch (axis) {
                
//#line 104 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
case 0:
                    
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73095 =
                      p;
                    
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73096 =
                      (-(t73095));
                    
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73097 =
                      p;
                    
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73098 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73096)), ((int)(t73097)))));
                    
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73099 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73098)))));
                    
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73099;
                
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
case 1:
                    
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73100 =
                      p;
                    
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73101 =
                      ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(0)), ((int)(t73100)))));
                    
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73102 =
                      ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t73101)))));
                    
//#line 107 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73102;
                
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
default:
                    
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73103 =
                      (("projection(") + ((x10.core.Int.$box(axis))));
                    
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73104 =
                      ((t73103) + (")"));
                    
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.UnsupportedOperationException t73105 =
                      ((x10.lang.UnsupportedOperationException)(new x10.lang.UnsupportedOperationException(t73104)));
                    
//#line 109 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73105;
            }
        }
        
        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
                                                                                                              indexOf$O(
                                                                                                              final x10.array.Point pt){
            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73106 =
              pt.
                rank;
            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73107 =
              ((int) t73106) !=
            ((int) 2);
            
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73107) {
                
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return -1;
            }
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73108 =
              pt.$apply$O((int)(0));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73109 =
              pt.$apply$O((int)(0));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73110 =
              ((t73108) * (((int)(t73109))));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73111 =
              pt.$apply$O((int)(1));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73112 =
              ((t73110) + (((int)(t73111))));
            
//#line 115 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73112;
        }
        
        
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                              boundingBox(
                                                                                                              ){
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73113 =
              p;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73117 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(0)), ((int)(t73113)))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73114 =
              p;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73115 =
              (-(t73114));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73116 =
              p;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73118 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73115)), ((int)(t73116)))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73119 =
              ((x10.array.Region)(t73117.$times(((x10.lang.IntRange)(t73118)))));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region __desugarer__var__38__73012 =
              ((x10.array.Region)(((x10.array.Region)
                                    t73119)));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
x10.array.Region ret73013 =
               null;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t7312073171 =
              __desugarer__var__38__73012.
                rank;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t7312173172 =
              au.edu.anu.mm.ExpansionRegion.this.
                rank;
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t7312273173 =
              ((int) t7312073171) ==
            ((int) t7312173172);
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t7312573174 =
              !(t7312273173);
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t7312573174) {
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t7312473175 =
                  true;
                
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t7312473175) {
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.FailedDynamicCheckException t7312373176 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:au.edu.anu.mm.ExpansionRegion).rank}");
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t7312373176;
                }
            }
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
ret73013 = ((x10.array.Region)(__desugarer__var__38__73012));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73126 =
              ((x10.array.Region)(ret73013));
            
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73126;
        }
        
        
//#line 122 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.array.Region
                                                                                                              computeBoundingBox(
                                                                                                              ){
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73127 =
              p;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73131 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(0)), ((int)(t73127)))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73128 =
              p;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73129 =
              (-(t73128));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73130 =
              p;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.IntRange t73132 =
              ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t73129)), ((int)(t73130)))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73133 =
              ((x10.array.Region)(t73131.$times(((x10.lang.IntRange)(t73132)))));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region __desugarer__var__39__73015 =
              ((x10.array.Region)(((x10.array.Region)
                                    t73133)));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
x10.array.Region ret73016 =
               null;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t7313473177 =
              __desugarer__var__39__73015.
                rank;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t7313573178 =
              au.edu.anu.mm.ExpansionRegion.this.
                rank;
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t7313673179 =
              ((int) t7313473177) ==
            ((int) t7313573178);
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t7313973180 =
              !(t7313673179);
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t7313973180) {
                
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t7313873181 =
                  true;
                
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t7313873181) {
                    
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.FailedDynamicCheckException t7313773182 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==this(:au.edu.anu.mm.ExpansionRegion).rank}");
                    
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t7313773182;
                }
            }
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
ret73016 = ((x10.array.Region)(__desugarer__var__39__73015));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Region t73140 =
              ((x10.array.Region)(ret73016));
            
//#line 123 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73140;
        }
        
        
//#line 126 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.lang.Iterator<x10.array.Point>
                                                                                                              iterator(
                                                                                                              ){
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator alloc62729 =
              ((au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator)(new au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator((java.lang.System[]) null)));
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.util.concurrent.OrderedLock t7314173183 =
              x10.util.concurrent.OrderedLock.createNewLock();
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
alloc62729.$init(this,
                                                                                                                                 ((au.edu.anu.mm.ExpansionRegion)(this)),
                                                                                                                                 t7314173183);
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.Iterator<x10.array.Point> t73142 =
              x10.rtt.Types.<x10.lang.Iterator<x10.array.Point>> cast(alloc62729,new x10.rtt.ParameterizedType(x10.lang.Iterator.$RTT, x10.array.Point.$RTT));
            
//#line 127 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73142;
        }
        
        
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public static class ExpansionRegionIterator
                                                                                                            extends x10.core.Ref
                                                                                                              implements x10.lang.Iterator,
                                                                                                                         x10.util.concurrent.Atomic,
                                                                                                                          x10.x10rt.X10JavaSerializable 
                                                                                                            {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ExpansionRegionIterator.class);
            
            public static final x10.rtt.RuntimeType<ExpansionRegionIterator> $RTT = new x10.rtt.NamedType<ExpansionRegionIterator>(
            "au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator", /* base class */ExpansionRegionIterator.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.lang.Iterator.$RTT, x10.array.Point.$RTT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body(ExpansionRegionIterator $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $_obj.p = $deserializer.readInt();
                $_obj.l = $deserializer.readInt();
                $_obj.m = $deserializer.readInt();
                au.edu.anu.mm.ExpansionRegion out$ = (au.edu.anu.mm.ExpansionRegion) $deserializer.readRef();
                $_obj.out$ = out$;
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                ExpansionRegionIterator $_obj = new ExpansionRegionIterator((java.lang.System[]) null);
                $deserializer.record_reference($_obj);
                return $_deserialize_body($_obj, $deserializer);
                
            }
            
            public short $_get_serialization_id() {
            
                 return $_serialization_id;
                
            }
            
            public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
            
                $serializer.write(this.p);
                $serializer.write(this.l);
                $serializer.write(this.m);
                if (out$ instanceof x10.x10rt.X10JavaSerializable) {
                $serializer.write( (x10.x10rt.X10JavaSerializable) this.out$);
                } else {
                $serializer.write(this.out$);
                }
                
            }
            
            // constructor just for allocation
            public ExpansionRegionIterator(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // bridge for method abstract public x10.lang.Iterator.next():T
            final public x10.array.Point
              next$G(){return next();}
            
                
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public au.edu.anu.mm.ExpansionRegion
                  out$;
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
                  X10$object_lock_id0;
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public x10.util.concurrent.OrderedLock
                                                                                                                      getOrderedLock(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73143 =
                      this.
                        X10$object_lock_id0;
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.util.concurrent.OrderedLock t73144 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t73143));
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73144;
                }
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public static int
                  X10$class_lock_id1 =
                  x10.util.concurrent.OrderedLock.createNewLockID();
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                                      getStaticOrderedLock(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73145 =
                      au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator.X10$class_lock_id1;
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.util.concurrent.OrderedLock t73146 =
                      x10.util.concurrent.OrderedLock.getLock((int)(t73145));
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73146;
                }
                
                
//#line 131 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
                  p;
                
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
                  l;
                
//#line 133 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public int
                  m;
                
                
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
// creation method for java code
                public static au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator $make(final au.edu.anu.mm.ExpansionRegion out$,
                                                                                          final au.edu.anu.mm.ExpansionRegion r){return new au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator((java.lang.System[]) null).$init(out$,r);}
                
                // constructor for non-virtual call
                final public au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator au$edu$anu$mm$ExpansionRegion$ExpansionRegionIterator$$init$S(final au.edu.anu.mm.ExpansionRegion out$,
                                                                                                                                                 final au.edu.anu.mm.ExpansionRegion r) { {
                                                                                                                                                                                                 
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"

                                                                                                                                                                                                 
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.out$ = out$;
                                                                                                                                                                                                 
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"

                                                                                                                                                                                                 
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator this7301873184 =
                                                                                                                                                                                                   this;
                                                                                                                                                                                                 
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7301873184.X10$object_lock_id0 = -1;
                                                                                                                                                                                                 
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7301873184.l = 0;
                                                                                                                                                                                                 
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7301873184.m = 0;
                                                                                                                                                                                                 
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73147 =
                                                                                                                                                                                                   r.
                                                                                                                                                                                                     p;
                                                                                                                                                                                                 
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.p = t73147;
                                                                                                                                                                                                 
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.l = 0;
                                                                                                                                                                                                 
//#line 138 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.m = 0;
                                                                                                                                                                                             }
                                                                                                                                                                                             return this;
                                                                                                                                                                                             }
                
                // constructor
                public au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator $init(final au.edu.anu.mm.ExpansionRegion out$,
                                                                                   final au.edu.anu.mm.ExpansionRegion r){return au$edu$anu$mm$ExpansionRegion$ExpansionRegionIterator$$init$S(out$,r);}
                
                
                
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
// creation method for java code
                public static au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator $make(final au.edu.anu.mm.ExpansionRegion out$,
                                                                                          final au.edu.anu.mm.ExpansionRegion r,
                                                                                          final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator((java.lang.System[]) null).$init(out$,r,paramLock);}
                
                // constructor for non-virtual call
                final public au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator au$edu$anu$mm$ExpansionRegion$ExpansionRegionIterator$$init$S(final au.edu.anu.mm.ExpansionRegion out$,
                                                                                                                                                 final au.edu.anu.mm.ExpansionRegion r,
                                                                                                                                                 final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                                                                                           
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"

                                                                                                                                                                                                           
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.out$ = out$;
                                                                                                                                                                                                           
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"

                                                                                                                                                                                                           
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator this7302173185 =
                                                                                                                                                                                                             this;
                                                                                                                                                                                                           
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7302173185.X10$object_lock_id0 = -1;
                                                                                                                                                                                                           
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7302173185.l = 0;
                                                                                                                                                                                                           
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this7302173185.m = 0;
                                                                                                                                                                                                           
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73148 =
                                                                                                                                                                                                             r.
                                                                                                                                                                                                               p;
                                                                                                                                                                                                           
//#line 136 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.p = t73148;
                                                                                                                                                                                                           
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.l = 0;
                                                                                                                                                                                                           
//#line 138 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.m = 0;
                                                                                                                                                                                                           
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73149 =
                                                                                                                                                                                                             paramLock.getIndex();
                                                                                                                                                                                                           
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.X10$object_lock_id0 = ((int)(t73149));
                                                                                                                                                                                                       }
                                                                                                                                                                                                       return this;
                                                                                                                                                                                                       }
                
                // constructor
                public au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator $init(final au.edu.anu.mm.ExpansionRegion out$,
                                                                                   final au.edu.anu.mm.ExpansionRegion r,
                                                                                   final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$ExpansionRegion$ExpansionRegionIterator$$init$S(out$,r,paramLock);}
                
                
                
//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final public boolean
                                                                                                                      hasNext$O(
                                                                                                                      ){
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73150 =
                      l;
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73151 =
                      p;
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
boolean t73154 =
                      ((t73150) <= (((int)(t73151))));
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73154) {
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73152 =
                          m;
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73153 =
                          l;
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
t73154 = ((t73152) <= (((int)(t73153))));
                    }
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73155 =
                      t73154;
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73155) {
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return true;
                    } else {
                        
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return false;
                    }
                }
                
                
//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final public x10.array.Point
                                                                                                                      next(
                                                                                                                      ){
                    
//#line 125 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i073024 =
                      l;
                    
//#line 125 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final int i173025 =
                      m;
                    
//#line 125 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
final x10.array.Point alloc3047473026 =
                      ((x10.array.Point)(new x10.array.Point((java.lang.System[]) null)));
                    
//#line 125 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Point.x10"
alloc3047473026.$init(((int)(i073024)),
                                                                                                                                          ((int)(i173025)));
                    
//#line 147 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.array.Point nextPoint =
                      ((x10.array.Point)(alloc3047473026));
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73156 =
                      m;
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73157 =
                      l;
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73164 =
                      ((t73156) < (((int)(t73157))));
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73164) {
                        
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator x73027 =
                          ((au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator)(this));
                        
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73158 =
                          x73027.
                            m;
                        
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73159 =
                          ((t73158) + (((int)(1))));
                        
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
x73027.m = t73159;
                    } else {
                        
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator x73029 =
                          ((au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator)(this));
                        
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73160 =
                          x73029.
                            l;
                        
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73161 =
                          ((t73160) + (((int)(1))));
                        
//#line 150 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
x73029.l = t73161;
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73162 =
                          l;
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73163 =
                          (-(t73162));
                        
//#line 151 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.m = t73163;
                    }
                    
//#line 153 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return nextPoint;
                }
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final public au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator
                                                                                                                      au$edu$anu$mm$ExpansionRegion$ExpansionRegionIterator$$au$edu$anu$mm$ExpansionRegion$ExpansionRegionIterator$this(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator.this;
                }
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final public au.edu.anu.mm.ExpansionRegion
                                                                                                                      au$edu$anu$mm$ExpansionRegion$ExpansionRegionIterator$$au$edu$anu$mm$ExpansionRegion$this(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final au.edu.anu.mm.ExpansionRegion t73165 =
                      this.
                        out$;
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73165;
                }
                
                
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final private void
                                                                                                                      __fieldInitializers62289(
                                                                                                                      ){
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.X10$object_lock_id0 = -1;
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.l = 0;
                    
//#line 130 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.m = 0;
                }
                
                final public static void
                  __fieldInitializers62289$P(
                  final au.edu.anu.mm.ExpansionRegion.ExpansionRegionIterator ExpansionRegionIterator){
                    ExpansionRegionIterator.__fieldInitializers62289();
                }
            
        }
        
        
        
//#line 157 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
public java.lang.String
                                                                                                              toString(
                                                                                                              ){
            
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73166 =
              p;
            
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73167 =
              (("ExpansionRegion (p = ") + ((x10.core.Int.$box(t73166))));
            
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73168 =
              ((t73167) + (")"));
            
//#line 158 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73168;
        }
        
        
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final public au.edu.anu.mm.ExpansionRegion
                                                                                                             au$edu$anu$mm$ExpansionRegion$$au$edu$anu$mm$ExpansionRegion$this(
                                                                                                             ){
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return au.edu.anu.mm.ExpansionRegion.this;
        }
        
        
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final private void
                                                                                                             __fieldInitializers62290(
                                                                                                             ){
            
//#line 13 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers62290$P(
          final au.edu.anu.mm.ExpansionRegion ExpansionRegion){
            ExpansionRegion.__fieldInitializers62290();
        }
        
        public static class $Closure$75
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$75.class);
            
            public static final x10.rtt.RuntimeType<$Closure$75> $RTT = new x10.rtt.StaticFunType<$Closure$75>(
            /* base class */$Closure$75.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$75 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                au.edu.anu.mm.ExpansionRegion out$$ = (au.edu.anu.mm.ExpansionRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.p = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$75 $_obj = new $Closure$75((java.lang.System[]) null);
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
                $serializer.write(this.p);
                
            }
            
            // constructor just for allocation
            public $Closure$75(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));}
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73043 =
                      ((int) i) ==
                    ((int) 0);
                    
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73043) {
                        
//#line 37 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return 0;
                    } else {
                        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73042 =
                          ((int) i) ==
                        ((int) 1);
                        
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73042) {
                            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73036 =
                              this.
                                p;
                            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73037 =
                              (-(t73036));
                            
//#line 38 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73037;
                        } else {
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73038 =
                              (("min: ") + ((x10.core.Int.$box(i))));
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73039 =
                              ((t73038) + (" is not a valid rank for "));
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73040 =
                              ((t73039) + (this.
                                             out$$));
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t73041 =
                              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t73040)));
                            
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73041;
                        }
                    }
                }
                
                public au.edu.anu.mm.ExpansionRegion
                  out$$;
                public int
                  p;
                
                // creation method for java code
                public static au.edu.anu.mm.ExpansionRegion.$Closure$75 $make(final au.edu.anu.mm.ExpansionRegion out$$,
                                                                              final int p){return new $Closure$75(out$$,p);}
                public $Closure$75(final au.edu.anu.mm.ExpansionRegion out$$,
                                   final int p) { {
                                                         this.out$$ = out$$;
                                                         this.p = p;
                                                     }}
                
            }
            
        public static class $Closure$76
        extends x10.core.Ref
          implements x10.core.fun.Fun_0_1,
                      x10.x10rt.X10JavaSerializable 
        {
            private static final long serialVersionUID = 1L;
            private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, $Closure$76.class);
            
            public static final x10.rtt.RuntimeType<$Closure$76> $RTT = new x10.rtt.StaticFunType<$Closure$76>(
            /* base class */$Closure$76.class
            , /* parents */ new x10.rtt.Type[] {new x10.rtt.ParameterizedType(x10.core.fun.Fun_0_1.$RTT, x10.rtt.Types.INT, x10.rtt.Types.INT), x10.rtt.Types.OBJECT}
            );
            public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
            
            
            public static x10.x10rt.X10JavaSerializable $_deserialize_body($Closure$76 $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                au.edu.anu.mm.ExpansionRegion out$$ = (au.edu.anu.mm.ExpansionRegion) $deserializer.readRef();
                $_obj.out$$ = out$$;
                $_obj.p = $deserializer.readInt();
                return $_obj;
                
            }
            
            public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
            
                $Closure$76 $_obj = new $Closure$76((java.lang.System[]) null);
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
                $serializer.write(this.p);
                
            }
            
            // constructor just for allocation
            public $Closure$76(final java.lang.System[] $dummy) { 
            super($dummy);
            }
            // dispatcher for method abstract public (a1:Z1)=>U.operator()(a1:Z1):U
            public java.lang.Object
              $apply(final java.lang.Object a1,final x10.rtt.Type t1){return x10.core.Int.$box($apply$O(x10.core.Int.$unbox(a1)));}
            
                
                public int
                  $apply$O(
                  final int i){
                    
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73052 =
                      ((int) i) ==
                    ((int) 0);
                    
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73052) {
                        
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73045 =
                          this.
                            p;
                        
//#line 43 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73045;
                    } else {
                        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final boolean t73051 =
                          ((int) i) ==
                        ((int) 1);
                        
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
if (t73051) {
                            
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final int t73046 =
                              this.
                                p;
                            
//#line 44 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
return t73046;
                        } else {
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73047 =
                              (("max: ") + ((x10.core.Int.$box(i))));
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73048 =
                              ((t73047) + (" is not a valid rank for "));
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final java.lang.String t73049 =
                              ((t73048) + (this.
                                             out$$));
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
final x10.lang.ArrayIndexOutOfBoundsException t73050 =
                              ((x10.lang.ArrayIndexOutOfBoundsException)(new x10.lang.ArrayIndexOutOfBoundsException(t73049)));
                            
//#line 45 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/ExpansionRegion.x10"
throw t73050;
                        }
                    }
                }
                
                public au.edu.anu.mm.ExpansionRegion
                  out$$;
                public int
                  p;
                
                // creation method for java code
                public static au.edu.anu.mm.ExpansionRegion.$Closure$76 $make(final au.edu.anu.mm.ExpansionRegion out$$,
                                                                              final int p){return new $Closure$76(out$$,p);}
                public $Closure$76(final au.edu.anu.mm.ExpansionRegion out$$,
                                   final int p) { {
                                                         this.out$$ = out$$;
                                                         this.p = p;
                                                     }}
                
            }
            
        
        }
        