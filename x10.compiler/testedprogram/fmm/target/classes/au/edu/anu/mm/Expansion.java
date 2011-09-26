package au.edu.anu.mm;


public class Expansion
extends x10.core.Ref
  implements x10.util.concurrent.Atomic,
              x10.x10rt.X10JavaSerializable 
{
    private static final long serialVersionUID = 1L;
    private static final short $_serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, Expansion.class);
    
    public static final x10.rtt.RuntimeType<Expansion> $RTT = new x10.rtt.NamedType<Expansion>(
    "au.edu.anu.mm.Expansion", /* base class */Expansion.class
    , /* parents */ new x10.rtt.Type[] {x10.rtt.Types.OBJECT}
    );
    public x10.rtt.RuntimeType<?> $getRTT() {return $RTT;}
    
    
    public static x10.x10rt.X10JavaSerializable $_deserialize_body(Expansion $_obj , x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        x10.array.Array terms = (x10.array.Array) $deserializer.readRef();
        $_obj.terms = terms;
        $_obj.p = $deserializer.readInt();
        return $_obj;
        
    }
    
    public static x10.x10rt.X10JavaSerializable $_deserializer(x10.x10rt.X10JavaDeserializer $deserializer) throws java.io.IOException { 
    
        Expansion $_obj = new Expansion((java.lang.System[]) null);
        $deserializer.record_reference($_obj);
        return $_deserialize_body($_obj, $deserializer);
        
    }
    
    public short $_get_serialization_id() {
    
         return $_serialization_id;
        
    }
    
    public void $_serialize(x10.x10rt.X10JavaSerializer $serializer) throws java.io.IOException {
    
        if (terms instanceof x10.x10rt.X10JavaSerializable) {
        $serializer.write( (x10.x10rt.X10JavaSerializable) this.terms);
        } else {
        $serializer.write(this.terms);
        }
        $serializer.write(this.p);
        
    }
    
    // constructor just for allocation
    public Expansion(final java.lang.System[] $dummy) { 
    super($dummy);
    }
    
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public int
          X10$object_lock_id0;
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public x10.util.concurrent.OrderedLock
                                                                                                       getOrderedLock(
                                                                                                       ){
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t63243 =
              this.
                X10$object_lock_id0;
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t63244 =
              x10.util.concurrent.OrderedLock.getLock((int)(t63243));
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
return t63244;
        }
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public static int
          X10$class_lock_id1 =
          x10.util.concurrent.OrderedLock.createNewLockID();
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public static x10.util.concurrent.OrderedLock
                                                                                                       getStaticOrderedLock(
                                                                                                       ){
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t63245 =
              au.edu.anu.mm.Expansion.X10$class_lock_id1;
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t63246 =
              x10.util.concurrent.OrderedLock.getLock((int)(t63245));
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
return t63246;
        }
        
        
//#line 28 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
/** The terms X_{lm} (with m >= 0) in this expansion */public x10.array.Array<x10.lang.Complex>
          terms;
        
//#line 31 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
/** The number of terms in the expansion. */public int
          p;
        
        
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
// creation method for java code
        public static au.edu.anu.mm.Expansion $make(final int p){return new au.edu.anu.mm.Expansion((java.lang.System[]) null).$init(p);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.Expansion au$edu$anu$mm$Expansion$$init$S(final int p) { {
                                                                                                   
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                   
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                   
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final au.edu.anu.mm.Expansion this6228263737 =
                                                                                                     this;
                                                                                                   
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this6228263737.X10$object_lock_id0 = -1;
                                                                                                   
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final au.edu.anu.mm.ExpansionRegion expRegion =
                                                                                                     ((au.edu.anu.mm.ExpansionRegion)(new au.edu.anu.mm.ExpansionRegion((java.lang.System[]) null)));
                                                                                                   
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t6324763738 =
                                                                                                     x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                   
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
expRegion.$init(((int)(p)),
                                                                                                                                                                                                                t6324763738);
                                                                                                   
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> alloc39843 =
                                                                                                     ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
                                                                                                   
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg62730 =
                                                                                                     ((x10.array.Region)(((x10.array.Region)
                                                                                                                           expRegion)));
                                                                                                   
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.x10$lang$Object$$init$S();
                                                                                                   
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__627316273563729 =
                                                                                                     ((x10.array.Region)(((x10.array.Region)
                                                                                                                           reg62730)));
                                                                                                   
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6273663730 =
                                                                                                      null;
                                                                                                   
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6324863725 =
                                                                                                     ((__desugarer__var__0__627316273563729) != (null));
                                                                                                   
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6325163726 =
                                                                                                     !(t6324863725);
                                                                                                   
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6325163726) {
                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6325063727 =
                                                                                                         true;
                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6325063727) {
                                                                                                           
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6324963728 =
                                                                                                             new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                                                                                                           
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6324963728;
                                                                                                       }
                                                                                                   }
                                                                                                   
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6273663730 = ((x10.array.Region)(__desugarer__var__0__627316273563729));
                                                                                                   
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6325263731 =
                                                                                                     ((x10.array.Region)(ret6273663730));
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.region = ((x10.array.Region)(t6325263731));
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6325363732 =
                                                                                                     reg62730.
                                                                                                       rank;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.rank = t6325363732;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6325463733 =
                                                                                                     reg62730.
                                                                                                       rect;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.rect = t6325463733;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6325563734 =
                                                                                                     reg62730.
                                                                                                       zeroBased;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.zeroBased = t6325563734;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6325663735 =
                                                                                                     reg62730.
                                                                                                       rail;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.rail = t6325663735;
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6325763736 =
                                                                                                     reg62730.size$O();
                                                                                                   
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.size = t6325763736;
                                                                                                   
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646273263739 =
                                                                                                     new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                   
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646273263739.$init(((x10.array.Region)(reg62730)));
                                                                                                   
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.layout = ((x10.array.RectLayout)(alloc199646273263739));
                                                                                                   
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6273863740 =
                                                                                                     ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc39843).
                                                                                                                               layout));
                                                                                                   
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6273363741 =
                                                                                                     this6273863740.
                                                                                                       size;
                                                                                                   
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6325863742 =
                                                                                                     ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6273363741)), true)));
                                                                                                   
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39843.raw = ((x10.core.IndexedMemoryChunk)(t6325863742));
                                                                                                   
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.terms = ((x10.array.Array)(alloc39843));
                                                                                                   
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.p = p;
                                                                                               }
                                                                                               return this;
                                                                                               }
        
        // constructor
        public au.edu.anu.mm.Expansion $init(final int p){return au$edu$anu$mm$Expansion$$init$S(p);}
        
        
        
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
// creation method for java code
        public static au.edu.anu.mm.Expansion $make(final int p,
                                                    final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.Expansion((java.lang.System[]) null).$init(p,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.Expansion au$edu$anu$mm$Expansion$$init$S(final int p,
                                                                             final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                       
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                                                       
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final au.edu.anu.mm.Expansion this6273963755 =
                                                                                                                                         this;
                                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this6273963755.X10$object_lock_id0 = -1;
                                                                                                                                       
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final au.edu.anu.mm.ExpansionRegion expRegion =
                                                                                                                                         ((au.edu.anu.mm.ExpansionRegion)(new au.edu.anu.mm.ExpansionRegion((java.lang.System[]) null)));
                                                                                                                                       
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t6325963756 =
                                                                                                                                         x10.util.concurrent.OrderedLock.createNewLock();
                                                                                                                                       
//#line 34 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
expRegion.$init(((int)(p)),
                                                                                                                                                                                                                                                    t6325963756);
                                                                                                                                       
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> alloc39844 =
                                                                                                                                         ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
                                                                                                                                       
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg62742 =
                                                                                                                                         ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                               expRegion)));
                                                                                                                                       
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.x10$lang$Object$$init$S();
                                                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__627436274763747 =
                                                                                                                                         ((x10.array.Region)(((x10.array.Region)
                                                                                                                                                               reg62742)));
                                                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6274863748 =
                                                                                                                                          null;
                                                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6326063743 =
                                                                                                                                         ((__desugarer__var__0__627436274763747) != (null));
                                                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6326363744 =
                                                                                                                                         !(t6326063743);
                                                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6326363744) {
                                                                                                                                           
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6326263745 =
                                                                                                                                             true;
                                                                                                                                           
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6326263745) {
                                                                                                                                               
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6326163746 =
                                                                                                                                                 new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                                                                                                                                               
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6326163746;
                                                                                                                                           }
                                                                                                                                       }
                                                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6274863748 = ((x10.array.Region)(__desugarer__var__0__627436274763747));
                                                                                                                                       
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6326463749 =
                                                                                                                                         ((x10.array.Region)(ret6274863748));
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.region = ((x10.array.Region)(t6326463749));
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6326563750 =
                                                                                                                                         reg62742.
                                                                                                                                           rank;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.rank = t6326563750;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6326663751 =
                                                                                                                                         reg62742.
                                                                                                                                           rect;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.rect = t6326663751;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6326763752 =
                                                                                                                                         reg62742.
                                                                                                                                           zeroBased;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.zeroBased = t6326763752;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6326863753 =
                                                                                                                                         reg62742.
                                                                                                                                           rail;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.rail = t6326863753;
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6326963754 =
                                                                                                                                         reg62742.size$O();
                                                                                                                                       
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.size = t6326963754;
                                                                                                                                       
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646274463757 =
                                                                                                                                         new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                                                       
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646274463757.$init(((x10.array.Region)(reg62742)));
                                                                                                                                       
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.layout = ((x10.array.RectLayout)(alloc199646274463757));
                                                                                                                                       
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6275063758 =
                                                                                                                                         ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc39844).
                                                                                                                                                                   layout));
                                                                                                                                       
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6274563759 =
                                                                                                                                         this6275063758.
                                                                                                                                           size;
                                                                                                                                       
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6327063760 =
                                                                                                                                         ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6274563759)), true)));
                                                                                                                                       
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39844.raw = ((x10.core.IndexedMemoryChunk)(t6327063760));
                                                                                                                                       
//#line 35 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.terms = ((x10.array.Array)(alloc39844));
                                                                                                                                       
//#line 36 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.p = p;
                                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t63271 =
                                                                                                                                         paramLock.getIndex();
                                                                                                                                       
//#line 33 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.X10$object_lock_id0 = ((int)(t63271));
                                                                                                                                   }
                                                                                                                                   return this;
                                                                                                                                   }
        
        // constructor
        public au.edu.anu.mm.Expansion $init(final int p,
                                             final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$Expansion$$init$S(p,paramLock);}
        
        
        
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
// creation method for java code
        public static au.edu.anu.mm.Expansion $make(final au.edu.anu.mm.Expansion e){return new au.edu.anu.mm.Expansion((java.lang.System[]) null).$init(e);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.Expansion au$edu$anu$mm$Expansion$$init$S(final au.edu.anu.mm.Expansion e) { {
                                                                                                                       
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                                       
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final au.edu.anu.mm.Expansion this6275163768 =
                                                                                                                         this;
                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this6275163768.X10$object_lock_id0 = -1;
                                                                                                                       
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> alloc39845 =
                                                                                                                         ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
                                                                                                                       
//#line 314 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> init62754 =
                                                                                                                         ((x10.array.Array)(e.
                                                                                                                                              terms));
                                                                                                                       
//#line 314 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.x10$lang$Object$$init$S();
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6327263761 =
                                                                                                                         ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)init62754).
                                                                                                                                               region));
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.region = ((x10.array.Region)(t6327263761));
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6327363762 =
                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62754).
                                                                                                                           rank;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.rank = t6327363762;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6327463763 =
                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62754).
                                                                                                                           rect;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.rect = t6327463763;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6327563764 =
                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62754).
                                                                                                                           zeroBased;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.zeroBased = t6327563764;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6327663765 =
                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62754).
                                                                                                                           rail;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.rail = t6327663765;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6327763766 =
                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62754).
                                                                                                                           size;
                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.size = t6327763766;
                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199766275563769 =
                                                                                                                         new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6327863767 =
                                                                                                                         ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)alloc39845).
                                                                                                                                               region));
                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199766275563769.$init(((x10.array.Region)(t6327863767)));
                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.layout = ((x10.array.RectLayout)(alloc199766275563769));
                                                                                                                       
//#line 318 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6275963770 =
                                                                                                                         ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc39845).
                                                                                                                                                   layout));
                                                                                                                       
//#line 318 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6275663771 =
                                                                                                                         this6275963770.
                                                                                                                           size;
                                                                                                                       
//#line 319 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> r6275763772 =
                                                                                                                         x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6275663771)), false);
                                                                                                                       
//#line 320 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6327963773 =
                                                                                                                         ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)init62754).
                                                                                                                                                          raw));
                                                                                                                       
//#line 320 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<x10.lang.Complex>copy(t6327963773,((int)(0)),r6275763772,((int)(0)),((int)(n6275663771)));
                                                                                                                       
//#line 321 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39845.raw = ((x10.core.IndexedMemoryChunk)(r6275763772));
                                                                                                                       
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.terms = ((x10.array.Array)(alloc39845));
                                                                                                                       
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t63280 =
                                                                                                                         e.
                                                                                                                           p;
                                                                                                                       
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.p = t63280;
                                                                                                                   }
                                                                                                                   return this;
                                                                                                                   }
        
        // constructor
        public au.edu.anu.mm.Expansion $init(final au.edu.anu.mm.Expansion e){return au$edu$anu$mm$Expansion$$init$S(e);}
        
        
        
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
// creation method for java code
        public static au.edu.anu.mm.Expansion $make(final au.edu.anu.mm.Expansion e,
                                                    final x10.util.concurrent.OrderedLock paramLock){return new au.edu.anu.mm.Expansion((java.lang.System[]) null).$init(e,paramLock);}
        
        // constructor for non-virtual call
        final public au.edu.anu.mm.Expansion au$edu$anu$mm$Expansion$$init$S(final au.edu.anu.mm.Expansion e,
                                                                             final x10.util.concurrent.OrderedLock paramLock) { {
                                                                                                                                       
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                                                       
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"

                                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final au.edu.anu.mm.Expansion this6276063781 =
                                                                                                                                         this;
                                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this6276063781.X10$object_lock_id0 = -1;
                                                                                                                                       
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> alloc39846 =
                                                                                                                                         ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
                                                                                                                                       
//#line 314 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> init62763 =
                                                                                                                                         ((x10.array.Array)(e.
                                                                                                                                                              terms));
                                                                                                                                       
//#line 314 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.x10$lang$Object$$init$S();
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6328163774 =
                                                                                                                                         ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)init62763).
                                                                                                                                                               region));
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.region = ((x10.array.Region)(t6328163774));
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6328263775 =
                                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62763).
                                                                                                                                           rank;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.rank = t6328263775;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6328363776 =
                                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62763).
                                                                                                                                           rect;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.rect = t6328363776;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6328463777 =
                                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62763).
                                                                                                                                           zeroBased;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.zeroBased = t6328463777;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6328563778 =
                                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62763).
                                                                                                                                           rail;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.rail = t6328563778;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6328663779 =
                                                                                                                                         ((x10.array.Array<x10.lang.Complex>)init62763).
                                                                                                                                           size;
                                                                                                                                       
//#line 316 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.size = t6328663779;
                                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199766276463782 =
                                                                                                                                         new x10.array.RectLayout((java.lang.System[]) null);
                                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6328763780 =
                                                                                                                                         ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)alloc39846).
                                                                                                                                                               region));
                                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199766276463782.$init(((x10.array.Region)(t6328763780)));
                                                                                                                                       
//#line 317 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.layout = ((x10.array.RectLayout)(alloc199766276463782));
                                                                                                                                       
//#line 318 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6276863783 =
                                                                                                                                         ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc39846).
                                                                                                                                                                   layout));
                                                                                                                                       
//#line 318 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6276563784 =
                                                                                                                                         this6276863783.
                                                                                                                                           size;
                                                                                                                                       
//#line 319 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> r6276663785 =
                                                                                                                                         x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6276563784)), false);
                                                                                                                                       
//#line 320 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6328863786 =
                                                                                                                                         ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)init62763).
                                                                                                                                                                          raw));
                                                                                                                                       
//#line 320 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.core.IndexedMemoryChunk.<x10.lang.Complex>copy(t6328863786,((int)(0)),r6276663785,((int)(0)),((int)(n6276563784)));
                                                                                                                                       
//#line 321 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc39846.raw = ((x10.core.IndexedMemoryChunk)(r6276663785));
                                                                                                                                       
//#line 40 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.terms = ((x10.array.Array)(alloc39846));
                                                                                                                                       
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t63289 =
                                                                                                                                         e.
                                                                                                                                           p;
                                                                                                                                       
//#line 41 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.p = t63289;
                                                                                                                                       
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t63290 =
                                                                                                                                         paramLock.getIndex();
                                                                                                                                       
//#line 39 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.X10$object_lock_id0 = ((int)(t63290));
                                                                                                                                   }
                                                                                                                                   return this;
                                                                                                                                   }
        
        // constructor
        public au.edu.anu.mm.Expansion $init(final au.edu.anu.mm.Expansion e,
                                             final x10.util.concurrent.OrderedLock paramLock){return au$edu$anu$mm$Expansion$$init$S(e,paramLock);}
        
        
        
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public void
                                                                                                       add(
                                                                                                       final au.edu.anu.mm.Expansion e){
            
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
try {{
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t63291 =
                  this.getOrderedLock();
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t63292 =
                  e.getOrderedLock();
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
x10.util.concurrent.OrderedLock.acquireTwoLocks(((x10.util.concurrent.OrderedLock)(t63291)),
                                                                                                                                                             ((x10.util.concurrent.OrderedLock)(t63292)));
                
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
x10.lang.Runtime.pushAtomic();
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39865max3986763873 =
                  p;
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3986563869 =
                  0;
                
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6329463870 =
                      i3986563869;
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6334563871 =
                      ((t6329463870) <= (((int)(i39865max3986763873))));
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6334563871)) {
                        
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int l63866 =
                      i3986563869;
                    
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39849min3985063864 =
                      (-(l63866));
                    
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39849max3985163865 =
                      l63866;
                    
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3984963861 =
                      i39849min3985063864;
                    
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                      true;
                                                                                                                      ) {
                        
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6329863862 =
                          i3984963861;
                        
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6334463863 =
                          ((t6329863862) <= (((int)(i39849max3985163865))));
                        
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6334463863)) {
                            
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                        }
                        
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int m63858 =
                          i3984963861;
                        
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6279463843 =
                          ((x10.array.Array)(this.
                                               terms));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06279163844 =
                          l63866;
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16279263845 =
                          m63858;
                        
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6277163846 =
                          ((x10.array.Array)(this.
                                               terms));
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06276963847 =
                          l63866;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16277063848 =
                          m63858;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6277263849 =
                           null;
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6330163814 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6277163846).
                                                region));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6330263815 =
                          t6330163814.contains$O((int)(i06276963847),
                                                 (int)(i16277063848));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6330363816 =
                          !(t6330263815);
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6330363816) {
                            
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06276963847),
                                                                                                                                                               (int)(i16277063848));
                        }
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6331263817 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6277163846).
                                                           raw));
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6277763818 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6277163846).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06277463819 =
                          i06276963847;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16277563820 =
                          i16277063848;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6277863821 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6330463787 =
                          this6277763818.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6277663788 =
                          ((i06277463819) - (((int)(t6330463787))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6330563789 =
                          offset6277663788;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6330663790 =
                          this6277763818.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6330763791 =
                          ((t6330563789) * (((int)(t6330663790))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6330863792 =
                          ((t6330763791) + (((int)(i16277563820))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6330963793 =
                          this6277763818.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6331063794 =
                          ((t6330863792) - (((int)(t6330963793))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6277663788 = t6331063794;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6331163795 =
                          offset6277663788;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6277863821 = t6331163795;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6331363822 =
                          ret6277863821;
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6331463823 =
                          ((x10.lang.Complex[])t6331263817.value)[t6331363822];
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6277263849 = t6331463823;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6332963850 =
                          ret6277263849;
                        
//#line 51 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6278263851 =
                          ((x10.array.Array)(e.
                                               terms));
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06278063852 =
                          l63866;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16278163853 =
                          m63858;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6278363854 =
                           null;
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6331563824 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6278263851).
                                                region));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6331663825 =
                          t6331563824.contains$O((int)(i06278063852),
                                                 (int)(i16278163853));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6331763826 =
                          !(t6331663825);
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6331763826) {
                            
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06278063852),
                                                                                                                                                               (int)(i16278163853));
                        }
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6332663827 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6278263851).
                                                           raw));
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6278863828 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6278263851).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06278563829 =
                          i06278063852;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16278663830 =
                          i16278163853;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6278963831 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6331863796 =
                          this6278863828.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6278763797 =
                          ((i06278563829) - (((int)(t6331863796))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6331963798 =
                          offset6278763797;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6332063799 =
                          this6278863828.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6332163800 =
                          ((t6331963798) * (((int)(t6332063799))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6332263801 =
                          ((t6332163800) + (((int)(i16278663830))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6332363802 =
                          this6278863828.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6332463803 =
                          ((t6332263801) - (((int)(t6332363802))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6278763797 = t6332463803;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6332563804 =
                          offset6278763797;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6278963831 = t6332563804;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6332763832 =
                          ret6278963831;
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6332863833 =
                          ((x10.lang.Complex[])t6332663827.value)[t6332763832];
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6278363854 = t6332863833;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6333063855 =
                          ret6278363854;
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6279363856 =
                          ((x10.lang.Complex)(t6332963850.$plus(((x10.lang.Complex)(t6333063855)))));
                        
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6279563857 =
                           null;
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6333163834 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6279463843).
                                                region));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6333263835 =
                          t6333163834.contains$O((int)(i06279163844),
                                                 (int)(i16279263845));
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6333363836 =
                          !(t6333263835);
                        
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6333363836) {
                            
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06279163844),
                                                                                                                                                               (int)(i16279263845));
                        }
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6334263837 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6279463843).
                                                           raw));
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6280063838 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6279463843).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06279763839 =
                          i06279163844;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16279863840 =
                          i16279263845;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6280163841 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6333463805 =
                          this6280063838.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6279963806 =
                          ((i06279763839) - (((int)(t6333463805))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6333563807 =
                          offset6279963806;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6333663808 =
                          this6280063838.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6333763809 =
                          ((t6333563807) * (((int)(t6333663808))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6333863810 =
                          ((t6333763809) + (((int)(i16279863840))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6333963811 =
                          this6280063838.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6334063812 =
                          ((t6333863810) - (((int)(t6333963811))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6279963806 = t6334063812;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6334163813 =
                          offset6279963806;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6280163841 = t6334163813;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6334363842 =
                          ret6280163841;
                        
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6334263837.value)[t6334363842] = v6279363856;
                        
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6279563857 = ((x10.lang.Complex)(v6279363856));
                        
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6329963859 =
                          i3984963861;
                        
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6330063860 =
                          ((t6329963859) + (((int)(1))));
                        
//#line 50 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3984963861 = t6330063860;
                    }
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6329563867 =
                      i3986563869;
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6329663868 =
                      ((t6329563867) + (((int)(1))));
                    
//#line 49 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3986563869 = t6329663868;
                }
            }}finally {{
                  
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
x10.lang.Runtime.popAtomic();
                  
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t63346 =
                    this.getOrderedLock();
                  
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.concurrent.OrderedLock t63347 =
                    e.getOrderedLock();
                  
//#line 48 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
x10.util.concurrent.OrderedLock.releaseTwoLocks(((x10.util.concurrent.OrderedLock)(t63346)),
                                                                                                                                                               ((x10.util.concurrent.OrderedLock)(t63347)));
              }}
            }
        
        
//#line 60 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public void
                                                                                                       unsafeAdd(
                                                                                                       final au.edu.anu.mm.Expansion e){
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39897max39899 =
              p;
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3989763956 =
              0;
            
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6334963957 =
                  i3989763956;
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6340063958 =
                  ((t6334963957) <= (((int)(i39897max39899))));
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6340063958)) {
                    
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                }
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int l63953 =
                  i3989763956;
                
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39881min3988263951 =
                  (-(l63953));
                
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39881max3988363952 =
                  l63953;
                
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3988163948 =
                  i39881min3988263951;
                
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6335363949 =
                      i3988163948;
                    
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6339963950 =
                      ((t6335363949) <= (((int)(i39881max3988363952))));
                    
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6339963950)) {
                        
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int m63945 =
                      i3988163948;
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6282863930 =
                      ((x10.array.Array)(this.
                                           terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06282563931 =
                      l63953;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16282663932 =
                      m63945;
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6280563933 =
                      ((x10.array.Array)(this.
                                           terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06280363934 =
                      l63953;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16280463935 =
                      m63945;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6280663936 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6335663901 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6280563933).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6335763902 =
                      t6335663901.contains$O((int)(i06280363934),
                                             (int)(i16280463935));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6335863903 =
                      !(t6335763902);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6335863903) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06280363934),
                                                                                                                                                           (int)(i16280463935));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6336763904 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6280563933).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6281163905 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6280563933).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06280863906 =
                      i06280363934;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16280963907 =
                      i16280463935;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6281263908 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6335963874 =
                      this6281163905.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6281063875 =
                      ((i06280863906) - (((int)(t6335963874))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336063876 =
                      offset6281063875;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336163877 =
                      this6281163905.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336263878 =
                      ((t6336063876) * (((int)(t6336163877))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336363879 =
                      ((t6336263878) + (((int)(i16280963907))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336463880 =
                      this6281163905.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336563881 =
                      ((t6336363879) - (((int)(t6336463880))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6281063875 = t6336563881;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336663882 =
                      offset6281063875;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6281263908 = t6336663882;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6336863909 =
                      ret6281263908;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6336963910 =
                      ((x10.lang.Complex[])t6336763904.value)[t6336863909];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6280663936 = t6336963910;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6338463937 =
                      ret6280663936;
                    
//#line 64 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6281663938 =
                      ((x10.array.Array)(e.
                                           terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06281463939 =
                      l63953;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16281563940 =
                      m63945;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6281763941 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6337063911 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6281663938).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6337163912 =
                      t6337063911.contains$O((int)(i06281463939),
                                             (int)(i16281563940));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6337263913 =
                      !(t6337163912);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6337263913) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06281463939),
                                                                                                                                                           (int)(i16281563940));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6338163914 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6281663938).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6282263915 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6281663938).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06281963916 =
                      i06281463939;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16282063917 =
                      i16281563940;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6282363918 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6337363883 =
                      this6282263915.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6282163884 =
                      ((i06281963916) - (((int)(t6337363883))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6337463885 =
                      offset6282163884;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6337563886 =
                      this6282263915.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6337663887 =
                      ((t6337463885) * (((int)(t6337563886))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6337763888 =
                      ((t6337663887) + (((int)(i16282063917))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6337863889 =
                      this6282263915.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6337963890 =
                      ((t6337763888) - (((int)(t6337863889))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6282163884 = t6337963890;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6338063891 =
                      offset6282163884;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6282363918 = t6338063891;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6338263919 =
                      ret6282363918;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6338363920 =
                      ((x10.lang.Complex[])t6338163914.value)[t6338263919];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6281763941 = t6338363920;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6338563942 =
                      ret6281763941;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6282763943 =
                      ((x10.lang.Complex)(t6338463937.$plus(((x10.lang.Complex)(t6338563942)))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6282963944 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6338663921 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6282863930).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6338763922 =
                      t6338663921.contains$O((int)(i06282563931),
                                             (int)(i16282663932));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6338863923 =
                      !(t6338763922);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6338863923) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06282563931),
                                                                                                                                                           (int)(i16282663932));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6339763924 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6282863930).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6283463925 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6282863930).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06283163926 =
                      i06282563931;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16283263927 =
                      i16282663932;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6283563928 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6338963892 =
                      this6283463925.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6283363893 =
                      ((i06283163926) - (((int)(t6338963892))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339063894 =
                      offset6283363893;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339163895 =
                      this6283463925.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339263896 =
                      ((t6339063894) * (((int)(t6339163895))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339363897 =
                      ((t6339263896) + (((int)(i16283263927))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339463898 =
                      this6283463925.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339563899 =
                      ((t6339363897) - (((int)(t6339463898))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6283363893 = t6339563899;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339663900 =
                      offset6283363893;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6283563928 = t6339663900;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6339863929 =
                      ret6283563928;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6339763924.value)[t6339863929] = v6282763943;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6282963944 = ((x10.lang.Complex)(v6282763943));
                    
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6335463946 =
                      i3988163948;
                    
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6335563947 =
                      ((t6335463946) + (((int)(1))));
                    
//#line 63 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3988163948 = t6335563947;
                }
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6335063954 =
                  i3989763956;
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6335163955 =
                  ((t6335063954) + (((int)(1))));
                
//#line 62 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3989763956 = t6335163955;
            }
        }
        
        
//#line 69 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public java.lang.String
                                                                                                       toString(
                                                                                                       ){
            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.util.StringBuilder s =
              ((x10.util.StringBuilder)(new x10.util.StringBuilder((java.lang.System[]) null)));
            
//#line 70 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
s.$init();
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39929max3993164000 =
              p;
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3992963996 =
              0;
            
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6340263997 =
                  i3992963996;
                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6342763998 =
                  ((t6340263997) <= (((int)(i39929max3993164000))));
                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6342763998)) {
                    
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                }
                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i63993 =
                  i3992963996;
                
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39913min3991463991 =
                  (-(i63993));
                
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39913max3991563992 =
                  i63993;
                
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3991363988 =
                  i39913min3991463991;
                
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6340663989 =
                      i3991363988;
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6342663990 =
                      ((t6340663989) <= (((int)(i39913max3991563992))));
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6342663990)) {
                        
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int j63985 =
                      i3991363988;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6302563978 =
                      ((x10.array.Array)(terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06302363979 =
                      i63993;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16302463980 =
                      j63985;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6302663981 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6340963968 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6302563978).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6341063969 =
                      t6340963968.contains$O((int)(i06302363979),
                                             (int)(i16302463980));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6341163970 =
                      !(t6341063969);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6341163970) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06302363979),
                                                                                                                                                           (int)(i16302463980));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6342063971 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6302563978).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6303163972 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6302563978).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06302863973 =
                      i06302363979;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16302963974 =
                      i16302463980;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6303263975 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341263959 =
                      this6303163972.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6303063960 =
                      ((i06302863973) - (((int)(t6341263959))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341363961 =
                      offset6303063960;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341463962 =
                      this6303163972.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341563963 =
                      ((t6341363961) * (((int)(t6341463962))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341663964 =
                      ((t6341563963) + (((int)(i16302963974))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341763965 =
                      this6303163972.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341863966 =
                      ((t6341663964) - (((int)(t6341763965))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6303063960 = t6341863966;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6341963967 =
                      offset6303063960;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6303263975 = t6341963967;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6342163976 =
                      ret6303263975;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6342263977 =
                      ((x10.lang.Complex[])t6342063971.value)[t6342163976];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6302663981 = t6342263977;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6342363982 =
                      ret6302663981;
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final java.lang.String t6342463983 =
                      (("") + (t6342363982));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final java.lang.String t6342563984 =
                      ((t6342463983) + (" "));
                    
//#line 73 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
s.add(((java.lang.String)(t6342563984)));
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6340763986 =
                      i3991363988;
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6340863987 =
                      ((t6340763986) + (((int)(1))));
                    
//#line 72 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3991363988 = t6340863987;
                }
                
//#line 75 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
s.add(((java.lang.String)("\n")));
                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6340363994 =
                  i3992963996;
                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6340463995 =
                  ((t6340363994) + (((int)(1))));
                
//#line 71 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3992963996 = t6340463995;
            }
            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final java.lang.String t63428 =
              s.toString();
            
//#line 77 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
return t63428;
        }
        
        
//#line 86 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public static x10.array.Array<x10.array.Array<x10.lang.Complex>>
                                                                                                       genComplexK(
                                                                                                       final double phi,
                                                                                                       final int p){
            
//#line 87 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.array.Array<x10.lang.Complex>> complexK =
              ((x10.array.Array)(new x10.array.Array<x10.array.Array<x10.lang.Complex>>((java.lang.System[]) null, new x10.rtt.ParameterizedType(x10.array.Array.$RTT, x10.lang.Complex.$RTT))));
            
//#line 243 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.x10$lang$Object$$init$S();
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectRegion1D alloc199706303764098 =
              ((x10.array.RectRegion1D)(new x10.array.RectRegion1D((java.lang.System[]) null)));
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6342964001 =
              ((2) - (((int)(1))));
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199706303764098.$init(((int)(0)),
                                                                                                                                       t6342964001);
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__5__630366304164099 =
              ((x10.array.Region)(((x10.array.Region)
                                    alloc199706303764098)));
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6304264100 =
               null;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6343064002 =
              __desugarer__var__5__630366304164099.
                rank;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6343264003 =
              ((int) t6343064002) ==
            ((int) 1);
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6343264003) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6343164004 =
                  __desugarer__var__5__630366304164099.
                    zeroBased;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6343264003 = ((boolean) t6343164004) ==
                ((boolean) true);
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6343464005 =
              t6343264003;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6343464005) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6343364006 =
                  __desugarer__var__5__630366304164099.
                    rect;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6343464005 = ((boolean) t6343364006) ==
                ((boolean) true);
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6343664007 =
              t6343464005;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6343664007) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6343564008 =
                  __desugarer__var__5__630366304164099.
                    rail;
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6343664007 = ((boolean) t6343564008) ==
                ((boolean) true);
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
boolean t6343764009 =
              t6343664007;
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6343764009) {
                
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
t6343764009 = ((__desugarer__var__5__630366304164099) != (null));
            }
            
//#line 246 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6343864010 =
              t6343764009;
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6344164011 =
              !(t6343864010);
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6344164011) {
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6344064012 =
                  true;
                
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6344064012) {
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6343964013 =
                      new x10.lang.FailedDynamicCheckException("x10.array.Region{self.rank==1, self.zeroBased==true, self.rect==true, self.rail==true, self!=null}");
                    
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6343964013;
                }
            }
            
//#line 245 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6304264100 = ((x10.array.Region)(__desugarer__var__5__630366304164099));
            
//#line 245 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region myReg6303564101 =
              ((x10.array.Region)(ret6304264100));
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.region = ((x10.array.Region)(myReg6303564101));
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.rank = 1;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.rect = true;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.zeroBased = true;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.rail = true;
            
//#line 247 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.size = 2;
            
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199716303864102 =
              new x10.array.RectLayout((java.lang.System[]) null);
            
//#line 97 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int _max06304564103 =
              ((2) - (((int)(1))));
            
//#line 98 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.rank = 1;
            
//#line 99 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.min0 = 0;
            
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6344264014 =
              ((_max06304564103) - (((int)(0))));
            
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6344364015 =
              ((t6344264014) + (((int)(1))));
            
//#line 100 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.delta0 = t6344364015;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6344464016 =
              alloc199716303864102.
                delta0;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final boolean t6344564017 =
              ((t6344464016) > (((int)(0))));
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int t6344664018 =
               0;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
if (t6344564017) {
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6344664018 = alloc199716303864102.
                                                                                                                                      delta0;
            } else {
                
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
t6344664018 = 0;
            }
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6344764019 =
              t6344664018;
            
//#line 101 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.size = t6344764019;
            
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.min1 = 0;
            
//#line 103 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.delta1 = 0;
            
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.min2 = 0;
            
//#line 104 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.delta2 = 0;
            
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.min3 = 0;
            
//#line 105 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.delta3 = 0;
            
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.min = null;
            
//#line 106 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
alloc199716303864102.delta = null;
            
//#line 249 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.layout = ((x10.array.RectLayout)(alloc199716303864102));
            
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6304764104 =
              ((x10.array.RectLayout)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                        layout));
            
//#line 250 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6303964105 =
              this6304764104.
                size;
            
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t6344864106 =
              ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.array.Array<x10.lang.Complex>>allocate(new x10.rtt.ParameterizedType(x10.array.Array.$RTT, x10.lang.Complex.$RTT), ((int)(n6303964105)), true)));
            
//#line 251 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
complexK.raw = ((x10.core.IndexedMemoryChunk)(t6344864106));
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3996164095 =
              0;
            
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                              true;
                                                                                                              ) {
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6345064096 =
                  i3996164095;
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6349964097 =
                  ((t6345064096) <= (((int)(1))));
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6349964097)) {
                    
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                }
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int r64092 =
                  i3996164095;
                
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06306064083 =
                  r64092;
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> alloc3984764084 =
                  ((x10.array.Array)(new x10.array.Array<x10.lang.Complex>((java.lang.System[]) null, x10.lang.Complex.$RTT)));
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6345364085 =
                  (-(p));
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.IntRange t6345464086 =
                  ((x10.lang.IntRange)(x10.lang.IntRange.$make(((int)(t6345364085)), ((int)(p)))));
                
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region reg6304864087 =
                  ((x10.array.Region)(x10.array.Region.$implicit_convert(((x10.lang.IntRange)(t6345464086)))));
                
//#line 129 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.x10$lang$Object$$init$S();
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region __desugarer__var__0__630496305364024 =
                  ((x10.array.Region)(((x10.array.Region)
                                        reg6304864087)));
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Region ret6305464025 =
                   null;
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6345564020 =
                  ((__desugarer__var__0__630496305364024) != (null));
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6345864021 =
                  !(t6345564020);
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6345864021) {
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6345764022 =
                      true;
                    
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6345764022) {
                        
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.FailedDynamicCheckException t6345664023 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Region{self!=null}");
                        
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
throw t6345664023;
                    }
                }
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6305464025 = ((x10.array.Region)(__desugarer__var__0__630496305364024));
                
//#line 131 ... "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6345964026 =
                  ((x10.array.Region)(ret6305464025));
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.region = ((x10.array.Region)(t6345964026));
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6346064027 =
                  reg6304864087.
                    rank;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.rank = t6346064027;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6346164028 =
                  reg6304864087.
                    rect;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.rect = t6346164028;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6346264029 =
                  reg6304864087.
                    zeroBased;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.zeroBased = t6346264029;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6346364030 =
                  reg6304864087.
                    rail;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.rail = t6346364030;
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int t6346464031 =
                  reg6304864087.size$O();
                
//#line 131 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.size = t6346464031;
                
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout alloc199646305064068 =
                  new x10.array.RectLayout((java.lang.System[]) null);
                
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc199646305064068.$init(((x10.array.Region)(reg6304864087)));
                
//#line 133 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.layout = ((x10.array.RectLayout)(alloc199646305064068));
                
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6305664069 =
                  ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)alloc3984764084).
                                            layout));
                
//#line 134 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int n6305164070 =
                  this6305664069.
                    size;
                
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6346564071 =
                  ((x10.core.IndexedMemoryChunk)(x10.core.IndexedMemoryChunk.<x10.lang.Complex>allocate(x10.lang.Complex.$RTT, ((int)(n6305164070)), true)));
                
//#line 135 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
alloc3984764084.raw = ((x10.core.IndexedMemoryChunk)(t6346564071));
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> __desugarer__var__29__6305764088 =
                  ((x10.array.Array)(((x10.array.Array<x10.lang.Complex>)
                                       alloc3984764084)));
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
x10.array.Array<x10.lang.Complex> ret6305864089 =
                   null;
                
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6346664072 =
                  ((x10.array.Array<x10.lang.Complex>)__desugarer__var__29__6305764088).
                    rect;
                
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
boolean t6346864073 =
                  ((boolean) t6346664072) ==
                ((boolean) true);
                
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (t6346864073) {
                    
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6346764074 =
                      ((x10.array.Array<x10.lang.Complex>)__desugarer__var__29__6305764088).
                        rail;
                    
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
t6346864073 = ((boolean) t6346764074) ==
                    ((boolean) false);
                }
                
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
boolean t6347064075 =
                  t6346864073;
                
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (t6347064075) {
                    
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6346964076 =
                      ((x10.array.Array<x10.lang.Complex>)__desugarer__var__29__6305764088).
                        rank;
                    
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
t6347064075 = ((int) t6346964076) ==
                    ((int) 1);
                }
                
//#line 89 . "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6347164077 =
                  t6347064075;
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6347464078 =
                  !(t6347164077);
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (t6347464078) {
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6347364079 =
                      true;
                    
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (t6347364079) {
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.FailedDynamicCheckException t6347264080 =
                          new x10.lang.FailedDynamicCheckException("x10.array.Array[x10.lang.Complex]{self.rect==true, self.rail==false, self.rank==1}");
                        
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
throw t6347264080;
                    }
                }
                
//#line 89 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
ret6305864089 = ((x10.array.Array)(__desugarer__var__29__6305764088));
                
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> v6306164090 =
                  ((x10.array.Array)(ret6305864089));
                
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret6306264091 =
                   null;
                
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t6347564032 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                                   raw));
                
//#line 512 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.array.Array[])t6347564032.value)[i06306064083] = v6306164090;
                
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6306264091 = ((x10.array.Array)(v6306164090));
                
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39945min3994664081 =
                  (-(p));
                
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39945max3994764082 =
                  p;
                
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3994564065 =
                  i39945min3994664081;
                
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                  true;
                                                                                                                  ) {
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6347764066 =
                      i3994564065;
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6349864067 =
                      ((t6347764066) <= (((int)(i39945max3994764082))));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6349864067)) {
                        
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int k64062 =
                      i3994564065;
                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06306964044 =
                      r64092;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.lang.Complex> ret6307064045 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6307164046: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.lang.Complex>> t6348064047 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.lang.Complex>>)complexK).
                                                       raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.lang.Complex> t6348164048 =
                      ((x10.array.Array)(((x10.array.Array[])t6348064047.value)[i06306964044]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6307064045 = ((x10.array.Array)(t6348164048));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6307164046;}
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6307964049 =
                      ((x10.array.Array)(ret6307064045));
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06307764050 =
                      k64062;
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6348264051 =
                      ((x10.lang.Complex)(x10.lang.Complex.getInitialized$I()));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final double t6348364052 =
                      ((double)(int)(((int)(k64062))));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6348464053 =
                      t6348264051.$times((double)(t6348364052));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6348864054 =
                      t6348464053.$times((double)(phi));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6348564055 =
                      ((int) r64092) ==
                    ((int) 0);
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int t6348664056 =
                       0;
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (t6348564055) {
                        
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
t6348664056 = 1;
                    } else {
                        
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
t6348664056 = -1;
                    }
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6348764057 =
                      t6348664056;
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final double t6348964058 =
                      ((double)(int)(((int)(t6348764057))));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6349064059 =
                      t6348864054.$times((double)(t6348964058));
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6307864060 =
                      ((x10.lang.Complex)(x10.lang.Math.exp(((x10.lang.Complex)(t6349064059)))));
                    
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6308064061 =
                       null;
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6349164036 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6307964049).
                                            region));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6349264037 =
                      t6349164036.contains$O((int)(i06307764050));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6349364038 =
                      !(t6349264037);
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6349364038) {
                        
//#line 515 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06307764050));
                    }
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6349664039 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6307964049).
                                                       raw));
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6308464040 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6307964049).
                                                layout));
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06308264041 =
                      i06307764050;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6308564042 =
                       0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6349464033 =
                      this6308464040.
                        min0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6308364034 =
                      ((i06308264041) - (((int)(t6349464033))));
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6349564035 =
                      offset6308364034;
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6308564042 = t6349564035;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6349764043 =
                      ret6308564042;
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6349664039.value)[t6349764043] = v6307864060;
                    
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6308064061 = ((x10.lang.Complex)(v6307864060));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6347864063 =
                      i3994564065;
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6347964064 =
                      ((t6347864063) + (((int)(1))));
                    
//#line 90 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3994564065 = t6347964064;
                }
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6345164093 =
                  i3996164095;
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6345264094 =
                  ((t6345164093) + (((int)(1))));
                
//#line 88 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3996164095 = t6345264094;
            }
            
//#line 92 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
return complexK;
        }
        
        
//#line 103 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public void
                                                                                                        rotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(
                                                                                                        final x10.array.Array temp,
                                                                                                        final x10.array.Array complexK,
                                                                                                        final x10.array.Array wigner){
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40025max40027 =
              p;
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i4002564292 =
              1;
            
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6350164293 =
                  i4002564292;
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6361164294 =
                  ((t6350164293) <= (((int)(i40025max40027))));
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6361164294)) {
                    
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                }
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int l64289 =
                  i4002564292;
                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06308764282 =
                  l64289;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Double> ret6308864283 =
                   null;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6308964284: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Double>> t6350464285 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Double>>)wigner).
                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Double> t6350564286 =
                  ((x10.array.Array)(((x10.array.Array[])t6350464285.value)[i06308764282]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6308864283 = ((x10.array.Array)(t6350564286));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6308964284;}
                
//#line 106 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.core.Double> Dl64287 =
                  ((x10.array.Array)(ret6308864283));
                
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39977min3997864278 =
                  (-(l64289));
                
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39977max3997964279 =
                  l64289;
                
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3997764166 =
                  i39977min3997864278;
                
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6350764167 =
                      i3997764166;
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6354164168 =
                      ((t6350764167) <= (((int)(i39977max3997964279))));
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6354164168)) {
                        
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int k64163 =
                      i3997764166;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06311464142 =
                      k64163;
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6309764143 =
                      ((x10.array.Array)(terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06309564144 =
                      l64289;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16309664145 =
                      k64163;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6309864146 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6351064132 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6309764143).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6351164133 =
                      t6351064132.contains$O((int)(i06309564144),
                                             (int)(i16309664145));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6351264134 =
                      !(t6351164133);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6351264134) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06309564144),
                                                                                                                                                           (int)(i16309664145));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6352164135 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6309764143).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6310364136 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6309764143).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06310064137 =
                      i06309564144;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16310164138 =
                      i16309664145;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6310464139 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6351364109 =
                      this6310364136.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6310264110 =
                      ((i06310064137) - (((int)(t6351364109))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6351464111 =
                      offset6310264110;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6351564112 =
                      this6310364136.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6351664113 =
                      ((t6351464111) * (((int)(t6351564112))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6351764114 =
                      ((t6351664113) + (((int)(i16310164138))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6351864115 =
                      this6310364136.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6351964116 =
                      ((t6351764114) - (((int)(t6351864115))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6310264110 = t6351964116;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6352064117 =
                      offset6310264110;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6310464139 = t6352064117;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6352264140 =
                      ret6310464139;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6352364141 =
                      ((x10.lang.Complex[])t6352164135.value)[t6352264140];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6309864146 = t6352364141;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6353264147 =
                      ret6309864146;
                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06310664148 =
                      k64163;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6310764149 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6310864150: {
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6352464151 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)complexK).
                                            region));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6352564152 =
                      t6352464151.contains$O((int)(i06310664148));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6352664153 =
                      !(t6352564152);
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6352664153) {
                        
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06310664148));
                    }
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6352964154 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)complexK).
                                                       raw));
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6311164155 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)complexK).
                                                layout));
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06310964156 =
                      i06310664148;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6311264157 =
                       0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6352764118 =
                      this6311164155.
                        min0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6311064119 =
                      ((i06310964156) - (((int)(t6352764118))));
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6352864120 =
                      offset6311064119;
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6311264157 = t6352864120;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6353064158 =
                      ret6311264157;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6353164159 =
                      ((x10.lang.Complex[])t6352964154.value)[t6353064158];
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6310764149 = t6353164159;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6310864150;}
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6353364160 =
                      ret6310764149;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6311564161 =
                      ((x10.lang.Complex)(t6353264147.$times(((x10.lang.Complex)(t6353364160)))));
                    
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6311664162 =
                       null;
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6353464124 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                            region));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6353564125 =
                      t6353464124.contains$O((int)(i06311464142));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6353664126 =
                      !(t6353564125);
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6353664126) {
                        
//#line 515 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06311464142));
                    }
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6353964127 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                       raw));
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6312064128 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                layout));
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06311864129 =
                      i06311464142;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6312164130 =
                       0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6353764121 =
                      this6312064128.
                        min0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6311964122 =
                      ((i06311864129) - (((int)(t6353764121))));
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6353864123 =
                      offset6311964122;
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6312164130 = t6353864123;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6354064131 =
                      ret6312164130;
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6353964127.value)[t6354064131] = v6311564161;
                    
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6311664162 = ((x10.lang.Complex)(v6311564161));
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6350864164 =
                      i3997764166;
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6350964165 =
                      ((t6350864164) + (((int)(1))));
                    
//#line 108 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3997764166 = t6350964165;
                }
                
//#line 110 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int m_sign64288 =
                  1;
                
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40009max4001164281 =
                  l64289;
                
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i4000964275 =
                  0;
                
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6354364276 =
                      i4000964275;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6361064277 =
                      ((t6354364276) <= (((int)(i40009max4001164281))));
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6361064277)) {
                        
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int m64272 =
                      i4000964275;
                    
//#line 112 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
x10.lang.Complex O_lm64255 =
                      x10.lang.Complex.getInitialized$ZERO();
                    
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39993min3999464235 =
                      (-(l64289));
                    
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i39993max3999564236 =
                      l64289;
                    
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i3999364214 =
                      i39993min3999464235;
                    
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6354764215 =
                          i3999364214;
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6357764216 =
                          ((t6354764215) <= (((int)(i39993max3999564236))));
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6357764216)) {
                            
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                        }
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int k64211 =
                          i3999364214;
                        
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6357464191 =
                          O_lm64255;
                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06312364192 =
                          k64211;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6312464193 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6312564194: {
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6355064195 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                                region));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6355164196 =
                          t6355064195.contains$O((int)(i06312364192));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6355264197 =
                          !(t6355164196);
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6355264197) {
                            
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06312364192));
                        }
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6355564198 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                           raw));
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6312864199 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                    layout));
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06312664200 =
                          i06312364192;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6312964201 =
                           0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6355364169 =
                          this6312864199.
                            min0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6312764170 =
                          ((i06312664200) - (((int)(t6355364169))));
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6355464171 =
                          offset6312764170;
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6312964201 = t6355464171;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6355664202 =
                          ret6312964201;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6355764203 =
                          ((x10.lang.Complex[])t6355564198.value)[t6355664202];
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6312464193 = t6355764203;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6312564194;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6357264204 =
                          ret6312464193;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06313164205 =
                          m64272;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16313264206 =
                          k64211;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6313364207 =
                           0;
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6355864181 =
                          ((x10.array.Region)(((x10.array.Array<x10.core.Double>)Dl64287).
                                                region));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6355964182 =
                          t6355864181.contains$O((int)(i06313164205),
                                                 (int)(i16313264206));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6356064183 =
                          !(t6355964182);
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6356064183) {
                            
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06313164205),
                                                                                                                                                               (int)(i16313264206));
                        }
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6356964184 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)Dl64287).
                                                           raw));
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6313864185 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)Dl64287).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06313564186 =
                          i06313164205;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16313664187 =
                          i16313264206;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6313964188 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356164172 =
                          this6313864185.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6313764173 =
                          ((i06313564186) - (((int)(t6356164172))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356264174 =
                          offset6313764173;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356364175 =
                          this6313864185.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356464176 =
                          ((t6356264174) * (((int)(t6356364175))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356564177 =
                          ((t6356464176) + (((int)(i16313664187))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356664178 =
                          this6313864185.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356764179 =
                          ((t6356564177) - (((int)(t6356664178))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6313764173 = t6356764179;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6356864180 =
                          offset6313764173;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6313964188 = t6356864180;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6357064189 =
                          ret6313964188;
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6357164190 =
                          ((double[])t6356964184.value)[t6357064189];
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6313364207 = t6357164190;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6357364208 =
                          ret6313364207;
                        
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6357564209 =
                          t6357264204.$times((double)(t6357364208));
                        
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6357664210 =
                          t6357464191.$plus(((x10.lang.Complex)(t6357564209)));
                        
//#line 114 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
O_lm64255 = t6357664210;
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6354864212 =
                          i3999364214;
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6354964213 =
                          ((t6354864212) + (((int)(1))));
                        
//#line 113 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i3999364214 = t6354964213;
                    }
                    
//#line 116 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6314464256 =
                      ((x10.array.Array)(terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06314164257 =
                      l64289;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16314264258 =
                      m64272;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6314364259 =
                      ((x10.lang.Complex)(O_lm64255));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6314564260 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6357864237 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6314464256).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6357964238 =
                      t6357864237.contains$O((int)(i06314164257),
                                             (int)(i16314264258));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6358064239 =
                      !(t6357964238);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6358064239) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06314164257),
                                                                                                                                                           (int)(i16314264258));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6358964240 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6314464256).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6315064241 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6314464256).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06314764242 =
                      i06314164257;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16314864243 =
                      i16314264258;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6315164244 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358164217 =
                      this6315064241.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6314964218 =
                      ((i06314764242) - (((int)(t6358164217))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358264219 =
                      offset6314964218;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358364220 =
                      this6315064241.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358464221 =
                      ((t6358264219) * (((int)(t6358364220))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358564222 =
                      ((t6358464221) + (((int)(i16314864243))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358664223 =
                      this6315064241.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358764224 =
                      ((t6358564222) - (((int)(t6358664223))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6314964218 = t6358764224;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6358864225 =
                      offset6314964218;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6315164244 = t6358864225;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6359064245 =
                      ret6315164244;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6358964240.value)[t6359064245] = v6314364259;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6314564260 = ((x10.lang.Complex)(v6314364259));
                    
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6315664261 =
                      ((x10.array.Array)(terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06315364262 =
                      l64289;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16315464263 =
                      (-(m64272));
                    
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6359164264 =
                      O_lm64255;
                    
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6359364265 =
                      t6359164264.conjugate();
                    
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6359264266 =
                      m_sign64288;
                    
//#line 118 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final double t6359464267 =
                      ((double)(int)(((int)(t6359264266))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6315564268 =
                      ((x10.lang.Complex)(t6359364265.$times((double)(t6359464267))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6315764269 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6359564246 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6315664261).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6359664247 =
                      t6359564246.contains$O((int)(i06315364262),
                                             (int)(i16315464263));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6359764248 =
                      !(t6359664247);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6359764248) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06315364262),
                                                                                                                                                           (int)(i16315464263));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6360664249 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6315664261).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6316264250 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6315664261).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06315964251 =
                      i06315364262;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16316064252 =
                      i16315464263;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6316364253 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6359864226 =
                      this6316264250.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6316164227 =
                      ((i06315964251) - (((int)(t6359864226))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6359964228 =
                      offset6316164227;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6360064229 =
                      this6316264250.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6360164230 =
                      ((t6359964228) * (((int)(t6360064229))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6360264231 =
                      ((t6360164230) + (((int)(i16316064252))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6360364232 =
                      this6316264250.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6360464233 =
                      ((t6360264231) - (((int)(t6360364232))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6316164227 = t6360464233;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6360564234 =
                      offset6316164227;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6316364253 = t6360564234;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6360764254 =
                      ret6316364253;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6360664249.value)[t6360764254] = v6315564268;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6315764269 = ((x10.lang.Complex)(v6315564268));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6360864270 =
                      m_sign64288;
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6360964271 =
                      (-(t6360864270));
                    
//#line 119 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
m_sign64288 = t6360964271;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6354464273 =
                      i4000964275;
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6354564274 =
                      ((t6354464273) + (((int)(1))));
                    
//#line 111 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i4000964275 = t6354564274;
                }
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6350264290 =
                  i4002564292;
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6350364291 =
                  ((t6350264290) + (((int)(1))));
                
//#line 105 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i4002564292 = t6350364291;
            }
        }
        
        
//#line 132 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
public void
                                                                                                        backRotate_0_$_x10$lang$Complex_$_1_$_x10$lang$Complex_$_2_$_x10$array$Array$_x10$lang$Double_$_$(
                                                                                                        final x10.array.Array temp,
                                                                                                        final x10.array.Array complexK,
                                                                                                        final x10.array.Array wigner){
            
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40089max40091 =
              p;
            
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i4008964479 =
              1;
            
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                               true;
                                                                                                               ) {
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6361364480 =
                  i4008964479;
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6372464481 =
                  ((t6361364480) <= (((int)(i40089max40091))));
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6372464481)) {
                    
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                }
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int l64476 =
                  i4008964479;
                
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06316564469 =
                  l64476;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array<x10.core.Double> ret6316664470 =
                   null;
                
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6316764471: {
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.array.Array<x10.core.Double>> t6361664472 =
                  ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.array.Array<x10.core.Double>>)wigner).
                                                   raw));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Array<x10.core.Double> t6361764473 =
                  ((x10.array.Array)(((x10.array.Array[])t6361664472.value)[i06316564469]));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6316664470 = ((x10.array.Array)(t6361764473));
//#line 413 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6316764471;}
                
//#line 135 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.core.Double> Dl64474 =
                  ((x10.array.Array)(ret6316664470));
                
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40041min4004264465 =
                  (-(l64476));
                
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40041max4004364466 =
                  l64476;
                
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i4004164335 =
                  i40041min4004264465;
                
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6361964336 =
                      i4004164335;
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6364364337 =
                      ((t6361964336) <= (((int)(i40041max4004364466))));
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6364364337)) {
                        
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int k64332 =
                      i4004164335;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06318464325 =
                      k64332;
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6317564326 =
                      ((x10.array.Array)(terms));
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06317364327 =
                      l64476;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16317464328 =
                      k64332;
                    
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6317664329 =
                       null;
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6362264315 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6317564326).
                                            region));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6362364316 =
                      t6362264315.contains$O((int)(i06317364327),
                                             (int)(i16317464328));
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6362464317 =
                      !(t6362364316);
                    
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6362464317) {
                        
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06317364327),
                                                                                                                                                           (int)(i16317464328));
                    }
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6363364318 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6317564326).
                                                       raw));
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6318164319 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6317564326).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06317864320 =
                      i06317364327;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16317964321 =
                      i16317464328;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6318264322 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6362564295 =
                      this6318164319.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6318064296 =
                      ((i06317864320) - (((int)(t6362564295))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6362664297 =
                      offset6318064296;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6362764298 =
                      this6318164319.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6362864299 =
                      ((t6362664297) * (((int)(t6362764298))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6362964300 =
                      ((t6362864299) + (((int)(i16317964321))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6363064301 =
                      this6318164319.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6363164302 =
                      ((t6362964300) - (((int)(t6363064301))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6318064296 = t6363164302;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6363264303 =
                      offset6318064296;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6318264322 = t6363264303;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6363464323 =
                      ret6318264322;
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6363564324 =
                      ((x10.lang.Complex[])t6363364318.value)[t6363464323];
                    
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6317664329 = t6363564324;
                    
//#line 509 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6318564330 =
                      ((x10.lang.Complex)(ret6317664329));
                    
//#line 508 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6318664331 =
                       null;
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6363664307 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                            region));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6363764308 =
                      t6363664307.contains$O((int)(i06318464325));
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6363864309 =
                      !(t6363764308);
                    
//#line 514 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6363864309) {
                        
//#line 515 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06318464325));
                    }
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6364164310 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                       raw));
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6319064311 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                layout));
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06318864312 =
                      i06318464325;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6319164313 =
                       0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6363964304 =
                      this6319064311.
                        min0;
                    
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6318964305 =
                      ((i06318864312) - (((int)(t6363964304))));
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6364064306 =
                      offset6318964305;
                    
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6319164313 = t6364064306;
                    
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6364264314 =
                      ret6319164313;
                    
//#line 517 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6364164310.value)[t6364264314] = v6318564330;
                    
//#line 519 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6318664331 = ((x10.lang.Complex)(v6318564330));
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6362064333 =
                      i4004164335;
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6362164334 =
                      ((t6362064333) + (((int)(1))));
                    
//#line 137 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i4004164335 = t6362164334;
                }
                
//#line 139 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int m_sign64475 =
                  1;
                
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40073max4007564468 =
                  l64476;
                
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i4007364462 =
                  0;
                
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                   true;
                                                                                                                   ) {
                    
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6364564463 =
                      i4007364462;
                    
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6372364464 =
                      ((t6364564463) <= (((int)(i40073max4007564468))));
                    
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6372364464)) {
                        
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                    }
                    
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int m64459 =
                      i4007364462;
                    
//#line 141 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
x10.lang.Complex O_lm64427 =
                      x10.lang.Complex.getInitialized$ZERO();
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40057min4005864407 =
                      (-(l64476));
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int i40057max4005964408 =
                      l64476;
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
int i4005764383 =
                      i40057min4005864407;
                    
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
for (;
                                                                                                                       true;
                                                                                                                       ) {
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6364964384 =
                          i4005764383;
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final boolean t6367964385 =
                          ((t6364964384) <= (((int)(i40057max4005964408))));
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
if (!(t6367964385)) {
                            
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
break;
                        }
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int k64380 =
                          i4005764383;
                        
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6367664360 =
                          O_lm64427;
                        
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06319364361 =
                          k64380;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6319464362 =
                           null;
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6319564363: {
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6365264364 =
                          ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)temp).
                                                region));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6365364365 =
                          t6365264364.contains$O((int)(i06319364361));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6365464366 =
                          !(t6365364365);
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6365464366) {
                            
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06319364361));
                        }
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6365764367 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)temp).
                                                           raw));
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6319864368 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)temp).
                                                    layout));
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06319664369 =
                          i06319364361;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6319964370 =
                           0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6365564338 =
                          this6319864368.
                            min0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6319764339 =
                          ((i06319664369) - (((int)(t6365564338))));
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6365664340 =
                          offset6319764339;
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6319964370 = t6365664340;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6365864371 =
                          ret6319964370;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6365964372 =
                          ((x10.lang.Complex[])t6365764367.value)[t6365864371];
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6319464362 = t6365964372;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6319564363;}
                        
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6367464373 =
                          ret6319464362;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06320164374 =
                          m64459;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16320264375 =
                          k64380;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
double ret6320364376 =
                           0;
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6366064350 =
                          ((x10.array.Region)(((x10.array.Array<x10.core.Double>)Dl64474).
                                                region));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6366164351 =
                          t6366064350.contains$O((int)(i06320164374),
                                                 (int)(i16320264375));
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6366264352 =
                          !(t6366164351);
                        
//#line 434 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6366264352) {
                            
//#line 435 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06320164374),
                                                                                                                                                               (int)(i16320264375));
                        }
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.core.Double> t6367164353 =
                          ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.core.Double>)Dl64474).
                                                           raw));
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6320864354 =
                          ((x10.array.RectLayout)(((x10.array.Array<x10.core.Double>)Dl64474).
                                                    layout));
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06320564355 =
                          i06320164374;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16320664356 =
                          i16320264375;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6320964357 =
                           0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6366364341 =
                          this6320864354.
                            min0;
                        
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6320764342 =
                          ((i06320564355) - (((int)(t6366364341))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6366464343 =
                          offset6320764342;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6366564344 =
                          this6320864354.
                            delta1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6366664345 =
                          ((t6366464343) * (((int)(t6366564344))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6366764346 =
                          ((t6366664345) + (((int)(i16320664356))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6366864347 =
                          this6320864354.
                            min1;
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6366964348 =
                          ((t6366764346) - (((int)(t6366864347))));
                        
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6320764342 = t6366964348;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6367064349 =
                          offset6320764342;
                        
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6320964357 = t6367064349;
                        
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6367264358 =
                          ret6320964357;
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6367364359 =
                          ((double[])t6367164353.value)[t6367264358];
                        
//#line 437 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6320364376 = t6367364359;
                        
//#line 433 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final double t6367564377 =
                          ret6320364376;
                        
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6367764378 =
                          t6367464373.$times((double)(t6367564377));
                        
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6367864379 =
                          t6367664360.$plus(((x10.lang.Complex)(t6367764378)));
                        
//#line 143 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
O_lm64427 = t6367864379;
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6365064381 =
                          i4005764383;
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6365164382 =
                          ((t6365064381) + (((int)(1))));
                        
//#line 142 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i4005764383 = t6365164382;
                    }
                    
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6368864428 =
                      O_lm64427;
                    
//#line 410 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06321164429 =
                      m64459;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6321264430 =
                       null;
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
__ret6321364431: {
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6368064432 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)complexK).
                                            region));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6368164433 =
                      t6368064432.contains$O((int)(i06321164429));
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6368264434 =
                      !(t6368164433);
//#line 415 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6368264434) {
                        
//#line 416 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06321164429));
                    }
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6368564435 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)complexK).
                                                       raw));
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6321664436 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)complexK).
                                                layout));
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06321464437 =
                      i06321164429;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6321764438 =
                       0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6368364386 =
                      this6321664436.
                        min0;
//#line 130 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6321564387 =
                      ((i06321464437) - (((int)(t6368364386))));
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6368464388 =
                      offset6321564387;
//#line 131 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6321764438 = t6368464388;
//#line 129 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6368664439 =
                      ret6321764438;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6368764440 =
                      ((x10.lang.Complex[])t6368564435.value)[t6368664439];
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6321264430 = t6368764440;
//#line 418 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
break __ret6321364431;}
                    
//#line 409 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex t6368964441 =
                      ret6321264430;
                    
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6369064442 =
                      t6368864428.$times(((x10.lang.Complex)(t6368964441)));
                    
//#line 145 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
O_lm64427 = t6369064442;
                    
//#line 146 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6322264443 =
                      ((x10.array.Array)(terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06321964444 =
                      l64476;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16322064445 =
                      m64459;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6322164446 =
                      ((x10.lang.Complex)(O_lm64427));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6322364447 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6369164409 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6322264443).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6369264410 =
                      t6369164409.contains$O((int)(i06321964444),
                                             (int)(i16322064445));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6369364411 =
                      !(t6369264410);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6369364411) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06321964444),
                                                                                                                                                           (int)(i16322064445));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6370264412 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6322264443).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6322864413 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6322264443).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06322564414 =
                      i06321964444;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16322664415 =
                      i16322064445;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6322964416 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6369464389 =
                      this6322864413.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6322764390 =
                      ((i06322564414) - (((int)(t6369464389))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6369564391 =
                      offset6322764390;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6369664392 =
                      this6322864413.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6369764393 =
                      ((t6369564391) * (((int)(t6369664392))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6369864394 =
                      ((t6369764393) + (((int)(i16322664415))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6369964395 =
                      this6322864413.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6370064396 =
                      ((t6369864394) - (((int)(t6369964395))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6322764390 = t6370064396;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6370164397 =
                      offset6322764390;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6322964416 = t6370164397;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6370364417 =
                      ret6322964416;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6370264412.value)[t6370364417] = v6322164446;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6322364447 = ((x10.lang.Complex)(v6322164446));
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.array.Array<x10.lang.Complex> this6323464448 =
                      ((x10.array.Array)(terms));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i06323164449 =
                      l64476;
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final int i16323264450 =
                      (-(m64459));
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6370464451 =
                      O_lm64427;
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final x10.lang.Complex t6370664452 =
                      t6370464451.conjugate();
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6370564453 =
                      m_sign64475;
                    
//#line 148 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final double t6370764454 =
                      ((double)(int)(((int)(t6370564453))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.lang.Complex v6323364455 =
                      ((x10.lang.Complex)(t6370664452.$times((double)(t6370764454))));
                    
//#line 535 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.lang.Complex ret6323564456 =
                       null;
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.Region t6370864418 =
                      ((x10.array.Region)(((x10.array.Array<x10.lang.Complex>)this6323464448).
                                            region));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6370964419 =
                      t6370864418.contains$O((int)(i06323164449),
                                             (int)(i16323264450));
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final boolean t6371064420 =
                      !(t6370964419);
                    
//#line 536 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
if (t6371064420) {
                        
//#line 537 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
x10.array.Array.raiseBoundsError$P((int)(i06323164449),
                                                                                                                                                           (int)(i16323264450));
                    }
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.core.IndexedMemoryChunk<x10.lang.Complex> t6371964421 =
                      ((x10.core.IndexedMemoryChunk)(((x10.array.Array<x10.lang.Complex>)this6323464448).
                                                       raw));
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
final x10.array.RectLayout this6324064422 =
                      ((x10.array.RectLayout)(((x10.array.Array<x10.lang.Complex>)this6323464448).
                                                layout));
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i06323764423 =
                      i06323164449;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int i16323864424 =
                      i16323264450;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int ret6324164425 =
                       0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371164398 =
                      this6324064422.
                        min0;
                    
//#line 135 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
int offset6323964399 =
                      ((i06323764423) - (((int)(t6371164398))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371264400 =
                      offset6323964399;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371364401 =
                      this6324064422.
                        delta1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371464402 =
                      ((t6371264400) * (((int)(t6371364401))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371564403 =
                      ((t6371464402) + (((int)(i16323864424))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371664404 =
                      this6324064422.
                        min1;
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371764405 =
                      ((t6371564403) - (((int)(t6371664404))));
                    
//#line 136 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
offset6323964399 = t6371764405;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6371864406 =
                      offset6323964399;
                    
//#line 137 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
ret6324164425 = t6371864406;
                    
//#line 134 .. "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/RectLayout.x10"
final int t6372064426 =
                      ret6324164425;
                    
//#line 539 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
((x10.lang.Complex[])t6371964421.value)[t6372064426] = v6323364455;
                    
//#line 540 . "/home/zhangsa/x10/x10svnatomicsets/x10.dist/stdlib/x10.jar:x10/array/Array.x10"
ret6323564456 = ((x10.lang.Complex)(v6323364455));
                    
//#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6372164457 =
                      m_sign64475;
                    
//#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6372264458 =
                      (-(t6372164457));
                    
//#line 149 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
m_sign64475 = t6372264458;
                    
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6364664460 =
                      i4007364462;
                    
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6364764461 =
                      ((t6364664460) + (((int)(1))));
                    
//#line 140 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i4007364462 = t6364764461;
                }
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6361464477 =
                  i4008964479;
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final int t6361564478 =
                  ((t6361464477) + (((int)(1))));
                
//#line 134 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
i4008964479 = t6361564478;
            }
        }
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final public au.edu.anu.mm.Expansion
                                                                                                       au$edu$anu$mm$Expansion$$au$edu$anu$mm$Expansion$this(
                                                                                                       ){
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
return au.edu.anu.mm.Expansion.this;
        }
        
        
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
final private void
                                                                                                       __fieldInitializers39641(
                                                                                                       ){
            
//#line 25 "/home/zhangsa/x10/x10programs/anuchem/apps/fmm/src/au/edu/anu/mm/Expansion.x10"
this.X10$object_lock_id0 = -1;
        }
        
        final public static void
          __fieldInitializers39641$P(
          final au.edu.anu.mm.Expansion Expansion){
            Expansion.__fieldInitializers39641();
        }
        
    }
    